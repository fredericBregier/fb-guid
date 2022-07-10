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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.fb.utils.exceptions.InvalidArgumentRuntimeException;
import org.fb.utils.various.BaseXx;
import org.fb.utils.various.SingletonUtils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.fb.utils.guid.GuidFactory.*;

/**
 * TinyGUID implementation using 16 bytes:<br>
 * - 1 byte = Version (8)<br>
 * - 2 bytes = Tenant Id (16)<br>
 * - 4 bytes = Platform Id and JVM PID as Integer Hash (32)<br>
 * - 6 bytes = timestamp (48) (so up to 8 925 years after Time 0 so year 10 895)
 * <br>
 * - 3 bytes = counter against collision (24) (so up to 16 000 Millions/s) <br>
 * This implementation aims to get guarantee unique UUID globally (so GUUID)
 * by using Platform Id for platform specification (based on MAC address or
 * specific 6 bytes as Hexadecimal value) for global aspect, JVM PID for
 * multiple instances on the same platform, Timestamp and counter for
 * locallity unicity, and Tenant Id for specific usage where tenants are
 * used.<br>
 * It is compatible with Jackson, Comparable and able to transform back and
 * forward to ARK format.<br>
 * Benchmark shows about 9 millions/s generated TinyGUID.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public final class TinyGUID implements Comparable<TinyGUID> {
  /**
   * Native size of the TinyGUID
   */
  static final int KEYSIZE = 16;
  static final int KEYB64SIZE = 22;
  static final int KEYB32SIZE = 26;
  static final int KEYB16SIZE = KEYSIZE * 2;
  static final int HEADER_POS = 0;
  static final int HEADER_SIZE = 1;
  static final int TENANT_SIZE = 2;
  static final int PLATFORM_SIZE = 4;
  static final int TIME_SIZE = 6;
  static final int COUNTER_SIZE = 3;
  static final int TENANT_POS = HEADER_POS + HEADER_SIZE;
  static final int PLATFORM_POS = TENANT_POS + TENANT_SIZE;
  static final int TIME_POS = PLATFORM_POS + PLATFORM_SIZE;
  static final int COUNTER_POS = TIME_POS + TIME_SIZE;
  /**
   * Version to store (to check correctness if future algorithm) between 0 and
   * 255
   */
  static final int VERSION = 2 & BYTE_MASK;
  /**
   * Bits size of Counter
   */
  private static final int SIZE_COUNTER = COUNTER_SIZE * 8;
  /**
   * Max Counter value
   */
  private static final int MAX_COUNTER = (1 << SIZE_COUNTER) - 1;
  /**
   * Min Counter value
   */
  private static final int MIN_COUNTER = 0;
  /**
   * Counter part
   */
  private static final AtomicInteger COUNTER = new AtomicInteger(MIN_COUNTER);
  /**
   * real GUID
   */
  @JsonIgnore
  private final byte[] bguid = new byte[KEYSIZE];

  /**
   * Constructor that takes a byte array as this TinyGUID's content
   *
   * @param bytes TinyGUID content
   *
   * @throws InvalidArgumentRuntimeException if the byte array is incorrect
   */
  public TinyGUID(final byte[] bytes) {
    if (bytes == null) {
      throw new InvalidArgumentRuntimeException("Empty argument");
    }
    if (bytes.length < KEYSIZE) {
      throw new InvalidArgumentRuntimeException(
          "Attempted to parse malformed TinyGUID: (" + bytes.length + ')');
    }
    System.arraycopy(bytes, 0, bguid, 0, KEYSIZE);
    if (getVersion() != VERSION) {
      throw new InvalidArgumentRuntimeException("Version is incorrect: " + getVersion());
    }
  }

  /**
   * extract version field as a hex char from raw TinyGUID bytes
   *
   * @return version char
   */
  @JsonIgnore
  public int getVersion() {
    return bguid[HEADER_POS] & BYTE_MASK;
  }

  /**
   * Build from String key
   *
   * @throws InvalidArgumentRuntimeException if the isSource is incorrect
   */
  public TinyGUID(final String idsource) {
    setString(idsource);
    if (getVersion() != VERSION) {
      throw new InvalidArgumentRuntimeException("Version is incorrect: " + getVersion());
    }
  }

  /**
   * Internal function
   *
   * @return this
   */
  @JsonSetter("id")
  TinyGUID setString(final String idsource) {
    if (idsource == null) {
      throw new InvalidArgumentRuntimeException("Empty argument");
    }
    var id = idsource.trim();
    if (id.startsWith(ARK)) {
      var ids = id;
      ids = ids.substring(ARK.length());
      var separator = ids.indexOf('/');
      if (separator <= 0) {
        throw new InvalidArgumentRuntimeException(ATTEMPTED_TO_PARSE_MALFORMED_ARK_GUID + id);
      }
      var tenantId = 0;
      try {
        tenantId = Short.parseShort(ids.substring(0, separator));
      } catch (final NumberFormatException e) {
        throw new InvalidArgumentRuntimeException(ATTEMPTED_TO_PARSE_MALFORMED_ARK_GUID + id);
      }
      // BASE32
      ids = ids.substring(separator + 1);
      var base32 = BaseXx.getFromBase32(ids);
      if (base32.length != KEYSIZE - TENANT_SIZE) {
        throw new InvalidArgumentRuntimeException(ATTEMPTED_TO_PARSE_MALFORMED_ARK_GUID + id);
      }
      System.arraycopy(base32, 0, bguid, HEADER_POS, HEADER_SIZE);
      bguid[TENANT_POS + 1] = (byte) (tenantId & BYTE_MASK);
      tenantId >>>= BYTE_SIZE;
      bguid[TENANT_POS] = (byte) (tenantId & BYTE_MASK);
      // BASE32
      System.arraycopy(base32, HEADER_SIZE, bguid, PLATFORM_POS, PLATFORM_SIZE + TIME_SIZE + COUNTER_SIZE);
      return this;
    }
    var len = id.length();
    try {
      if (len == KEYB16SIZE) {
        // HEXA BASE16
        System.arraycopy(BaseXx.getFromBase16(id), 0, bguid, 0, KEYSIZE);
      } else if (len == KEYB32SIZE) {
        // BASE32
        System.arraycopy(BaseXx.getFromBase32(id), 0, bguid, 0, KEYSIZE);
      } else if (len == KEYB64SIZE) {
        // BASE64
        System.arraycopy(BaseXx.getFromBase64(id), 0, bguid, 0, KEYSIZE);
      } else {
        throw new InvalidArgumentRuntimeException(
            "Attempted to parse malformed TinyGUID: (" + len + ") " + id);
      }
    } catch (final IllegalArgumentException e) {
      throw new InvalidArgumentRuntimeException("Attempted to parse malformed TinyGUID: " + id, e);
    }
    return this;
  }

  /**
   * Constructor that generates a new TinyGUID using the current process id,
   * Platform Id and timestamp with no tenant
   */
  public TinyGUID() {
    this((short) 0, JvmProcessMacIds.getJvmIntegerId() & MASK_INT);
  }

  /**
   * Constructor that generates a new TinyGUID using the current process id and
   * timestamp
   *
   * @param tenantId tenant id between -2^16 and 2^15-1
   * @param platformId platform Id between -2^31 and 2^31-1
   *
   * @throws InvalidArgumentRuntimeException if any of the argument are out
   *     of range
   */
  public TinyGUID(final short tenantId, final int platformId) {
    if (tenantId < MIN_SHORT || tenantId > MAX_SHORT) {
      throw new InvalidArgumentRuntimeException("TenantId must be between -2^16 and 2^15-1: " + tenantId);
    }
    if (platformId < MIN_INT || platformId > MAX_INT) {
      throw new InvalidArgumentRuntimeException("PlatformId must be between -2^31 and 2^31-1: " + platformId);
    }

    // atomically
    var time = System.currentTimeMillis();
    var count = getNewCounter();
    // 1 bytes = Version (8)
    bguid[HEADER_POS] = (byte) VERSION;

    // 1 byte = Domain (16)
    var value = 0;
    value = tenantId;
    bguid[TENANT_POS + 1] = (byte) (value & BYTE_MASK);
    value >>>= BYTE_SIZE;
    bguid[TENANT_POS] = (byte) (value & BYTE_MASK);

    // 4 bytes = Platform (32)
    value = platformId;
    bguid[PLATFORM_POS + 3] = (byte) (value & BYTE_MASK);
    value >>>= BYTE_SIZE;
    bguid[PLATFORM_POS + 2] = (byte) (value & BYTE_MASK);
    value >>>= BYTE_SIZE;
    bguid[PLATFORM_POS + 1] = (byte) (value & BYTE_MASK);
    value >>>= BYTE_SIZE;
    bguid[PLATFORM_POS] = (byte) (value & BYTE_MASK);

    // 6 bytes = timestamp (so up to 8 925 years after Time 0 so year 10
    // 895)
    var lvalue = time;
    bguid[TIME_POS + 5] = (byte) (lvalue & BYTE_MASK);
    lvalue >>>= BYTE_SIZE;
    bguid[TIME_POS + 4] = (byte) (lvalue & BYTE_MASK);
    lvalue >>>= BYTE_SIZE;
    bguid[TIME_POS + 3] = (byte) (lvalue & BYTE_MASK);
    lvalue >>>= BYTE_SIZE;
    bguid[TIME_POS + 2] = (byte) (lvalue & BYTE_MASK);
    lvalue >>>= BYTE_SIZE;
    bguid[TIME_POS + 1] = (byte) (lvalue & BYTE_MASK);
    lvalue >>>= BYTE_SIZE;
    bguid[TIME_POS] = (byte) (lvalue & BYTE_MASK);

    // 3 bytes = counter against collision
    value = count;
    bguid[COUNTER_POS + 2] = (byte) (value & BYTE_MASK);
    value >>>= BYTE_SIZE;
    bguid[COUNTER_POS + 1] = (byte) (value & BYTE_MASK);
    value >>>= BYTE_SIZE;
    bguid[COUNTER_POS] = (byte) (value & BYTE_MASK);

  }

  private static synchronized int getNewCounter() {
    if (COUNTER.compareAndSet(MAX_COUNTER, MIN_COUNTER)) {
      return MAX_COUNTER;
    } else {
      return COUNTER.getAndIncrement();
    }
  }

  public TinyGUID(final int tenantId) {
    this((short) (tenantId & 0xFFFF));
  }

  /**
   * Constructor that generates a new TinyGUID using the current process id,
   * Platform Id and timestamp with no tenant
   *
   * @param tenantId tenant id between -2^31 and 2^31-1
   *
   * @throws InvalidArgumentRuntimeException if any of the argument are out
   *     of range
   */
  public TinyGUID(final short tenantId) {
    this(tenantId, JvmProcessMacIds.getJvmIntegerId() & MASK_INT);
  }

  public TinyGUID(final int tenantId, final int platformId) {
    this((short) (tenantId & 0xFFFF), platformId);
  }

  /**
   * @return the KeySize
   */
  public static int getKeySize() {
    return KEYSIZE;
  }

  public static short getKey16Size() {
    return KEYB16SIZE;
  }

  public static short getKey32Size() {
    return KEYB32SIZE;
  }

  public static short getKey64Size() {
    return KEYB64SIZE;
  }

  /**
   * @return the Base64 representation
   */
  @JsonIgnore
  public String toBase64() {
    return BaseXx.getBase64(bguid);
  }

  /**
   * @return the Hexadecimal representation
   */
  @JsonIgnore
  public String toHex() {
    return BaseXx.getBase16(bguid);
  }

  /**
   * @return the Ark representation of this TinyGUID
   */
  @JsonIgnore
  public String toArk() {
    return new StringBuilder(ARK).append(getTenantId()).append('/').append(toArkName()).toString();
  }

  /**
   * @return the Tenant Id of TinyGUID from which it belongs to (default being 0)
   */
  @JsonIgnore
  public short getTenantId() {
    return (short) ((bguid[TENANT_POS] & BYTE_MASK) << BYTE_SIZE | bguid[TENANT_POS + 1] & BYTE_MASK);
  }

  /**
   * @return the Ark Name part of Ark representation
   */
  public String toArkName() {
    var temp = new byte[KEYSIZE - TENANT_SIZE];
    System.arraycopy(bguid, HEADER_POS, temp, 0, HEADER_SIZE);
    System.arraycopy(bguid, PLATFORM_POS, temp, HEADER_SIZE, PLATFORM_SIZE + TIME_SIZE + COUNTER_SIZE);
    return BaseXx.getBase32(temp);
  }

  /**
   * @return the String representation of this TinyGUID
   */
  @JsonGetter("id")
  public String getId() {
    return toString();
  }

  @Override
  public String toString() {
    return toBase32();
  }

  /**
   * @return the Base32 representation (default of toString)
   */
  @JsonIgnore
  public String toBase32() {
    return BaseXx.getBase32(bguid);
  }

  /**
   * Extract Platform id as int. Could be using partial MAC address.
   *
   * @return the Platform id as int, or -1 for unrecognized format
   */
  @JsonIgnore
  public int getPlatformId() {
    return (bguid[PLATFORM_POS] & BYTE_MASK) << BYTE_SIZE * 3 |
           (bguid[PLATFORM_POS + 1] & BYTE_MASK) << BYTE_SIZE * 2 |
           (bguid[PLATFORM_POS + 2] & BYTE_MASK) << BYTE_SIZE | bguid[PLATFORM_POS + 3] & BYTE_MASK;
  }

  /**
   * Extract Platform id as bytes. Could be using partial MAC address.
   *
   * @return byte array of TinyGUID fragment, or null for unrecognized format
   */
  @JsonIgnore
  public byte[] getPlatformIdAsBytes() {
    if (getVersion() != VERSION) {
      return SingletonUtils.getSingletonByteArray();
    }
    final byte[] x = new byte[6];
    x[0] = 0;
    x[1] = 0;
    x[2] = (byte) (bguid[PLATFORM_POS] & BYTE_MASK);
    x[3] = bguid[PLATFORM_POS + 1];
    x[4] = bguid[PLATFORM_POS + 2];
    x[5] = bguid[PLATFORM_POS + 3];
    return x;
  }

  @Override
  @JsonIgnore
  public int hashCode() {
    return Arrays.hashCode(bguid);
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof TinyGUID)) {
      return false;
    }
    return this == o || Arrays.equals(bguid, ((TinyGUID) o).bguid);
  }

  @Override
  public int compareTo(final TinyGUID guid) {
    var id = getTenantId();
    var id2 = guid.getTenantId();
    if (id != id2) {
      return id < id2? -1 : 1;
    }
    var ts = getTimestamp();
    var ts2 = guid.getTimestamp();
    if (ts == ts2) {
      var ct = getCounter();
      var ct2 = guid.getCounter();
      if (ct == ct2) {
        // then all must be equals, else whatever
        return Arrays.equals(bguid, guid.getBytes())? 0 : -1;
      }
      // Cannot be equal
      return ct < ct2? -1 : 1;
    }
    // others as ProcessId or Platform are unimportant in comparison
    return ts < ts2? -1 : 1;
  }

  /**
   * Extract timestamp and return as long
   *
   * @return millisecond UTC timestamp from generation of the TinyGUID, or -1 for
   *     unrecognized format
   */
  @JsonIgnore
  public long getTimestamp() {
    if (getVersion() != VERSION) {
      return -1;
    }
    long time = 0;
    for (var i = 0; i < TIME_SIZE; i++) {
      time <<= BYTE_SIZE;
      time |= bguid[TIME_POS + i] & BYTE_MASK;
    }
    return time;
  }

  /**
   * @return the associated counter against collision value
   */
  @JsonIgnore
  public int getCounter() {
    return (bguid[COUNTER_POS] & BYTE_MASK) << BYTE_SIZE * 2 |
           (bguid[COUNTER_POS + 1] & BYTE_MASK) << BYTE_SIZE | bguid[COUNTER_POS + 2] & BYTE_MASK;
  }

  /**
   * copy the uuid of this TinyGUID, so that it can't be changed, and return it
   *
   * @return raw byte array of TinyGUID
   */
  @JsonIgnore
  public byte[] getBytes() {
    return Arrays.copyOf(bguid, bguid.length);
  }

}
