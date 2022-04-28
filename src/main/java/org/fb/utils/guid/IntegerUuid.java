/*
 * Copyright (c) 2019-2022. FbUtilities Contributors and Frederic Bregier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 *  under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 *   OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.fb.utils.guid;

import org.fb.utils.exceptions.InvalidArgumentRuntimeException;
import org.fb.utils.various.BaseXx;
import org.fb.utils.various.RandomUtil;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UUID Generator (also Global UUID Generator) but limited to 1 Integer (32
 * bits) <br>
 * <br>
 * Inspired from com.groupon locality-uuid which used combination of internal
 * counter value. see https://github.com/groupon/locality-uuid.java <br>
 * <br>
 * Force sequence and take care of errors and improves some performance
 * issues.<br>
 * <br>
 * Benchmark is about 11 millions/s UUID and up to 2^32 values
 */
public final class IntegerUuid {
  /**
   * Counter part
   */
  private static final AtomicInteger COUNTER =
      new AtomicInteger(RandomUtil.RANDOM.nextInt());
  /**
   * Byte size of UUID
   */
  private static final int UUIDSIZE = 4;

  /**
   * real UUID
   */
  private final byte[] uuid = { 0, 0, 0, 0 };

  /**
   * Constructor that generates a new UUID using the current process id, MAC
   * address, and timestamp
   */
  public IntegerUuid() {
    // atomically
    final int count = getCounter();
    uuid[0] = (byte) (count >> 24);
    uuid[1] = (byte) (count >> 16);
    uuid[2] = (byte) (count >> 8);
    uuid[3] = (byte) count;
  }

  private static synchronized int getCounter() {
    if (COUNTER.compareAndSet(Integer.MAX_VALUE, Integer.MIN_VALUE)) {
      return Integer.MAX_VALUE;
    } else {
      return COUNTER.getAndIncrement();
    }
  }

  /**
   * Constructor that takes a byte array as this UUID's content
   *
   * @param bytes UUID content
   */
  public IntegerUuid(final byte[] bytes) {
    if (bytes.length != UUIDSIZE) {
      throw new InvalidArgumentRuntimeException(
          "Attempted to parse malformed UUID: " + Arrays.toString(bytes));
    }
    System.arraycopy(bytes, 0, uuid, 0, UUIDSIZE);
  }

  public IntegerUuid(final int value) {
    uuid[0] = (byte) (value >> 24);
    uuid[1] = (byte) (value >> 16);
    uuid[2] = (byte) (value >> 8);
    uuid[3] = (byte) value;
  }

  public IntegerUuid(final String idsource) {
    final String id = idsource.trim();

    if (id.length() != UUIDSIZE * 2) {
      throw new InvalidArgumentRuntimeException(
          "Attempted to parse malformed UUID: " + id);
    }
    System.arraycopy(BaseXx.getFromBase16(id), 0, uuid, 0, UUIDSIZE);
  }

  @Override
  public String toString() {
    return BaseXx.getBase16(uuid);
  }

  /**
   * copy the uuid of this UUID, so that it can't be changed, and return it
   *
   * @return raw byte array of UUID
   */
  public byte[] getBytes() {
    return Arrays.copyOf(uuid, UUIDSIZE);
  }

  /**
   * extract timestamp from raw UUID bytes and return as int
   *
   * @return millisecond UTC timestamp from generation of the UUID
   */
  public long getTimestamp() {
    long time;
    time = ((long) uuid[0] & 0xFF) << 24;
    time |= ((long) uuid[2] & 0xFF) << 16;
    time |= ((long) uuid[2] & 0xFF) << 8;
    time |= (long) uuid[3] & 0xFF;
    return time;
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof IntegerUuid)) {
      return false;
    }
    return this == o || Arrays.equals(uuid, ((IntegerUuid) o).uuid);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(uuid);
  }

  /**
   * @return the equivalent UUID as int
   */
  public int getInt() {
    int value = ((int) uuid[0] & 0xFF) << 24;
    value |= ((long) uuid[1] & 0xFF) << 16;
    value |= ((long) uuid[2] & 0xFF) << 8;
    value |= (long) uuid[3] & 0xFF;
    return value;
  }
}
