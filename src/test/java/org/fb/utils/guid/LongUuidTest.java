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

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LongUuidTest {
  private static final int NB = 1000000;
  private static final int NB_THREAD = 10;

  @Test
  public void testStructure() {
    final LongUuid id = new LongUuid();
    final String str = id.toString();

    assertEquals(16, str.length());
  }

  @Test
  public void testParsing() {
    final LongUuid id1 = new LongUuid();
    final LongUuid id2 = new LongUuid(id1.toString());
    assertEquals(id1, id2);
    assertEquals(id1.hashCode(), id2.hashCode());
    assertEquals(id1.getLong(), id2.getLong());

    final LongUuid id3 = new LongUuid(id1.getBytes());
    assertEquals(id1, id3);
    final LongUuid id4 = new LongUuid(id1.getLong());
    assertEquals(id1, id4);
  }

  @Test
  public void testNonSequentialValue() {
    final int n = NB;
    final long[] ids = new long[n];

    for (int i = 0; i < n; i++) {
      ids[i] = new LongUuid().getLong();
    }

    for (int i = 1; i < n; i++) {
      assertNotEquals(ids[i - 1], ids[i]);
    }
  }

  @Test
  public void testGetBytesImmutability() {
    final LongUuid id = new LongUuid();
    final byte[] bytes = id.getBytes();
    final byte[] original = Arrays.copyOf(bytes, bytes.length);
    bytes[0] = 0;
    bytes[1] = 0;
    bytes[2] = 0;

    assertArrayEquals(id.getBytes(), original);
  }

  @Test
  public void testConstructorImmutability() {
    final LongUuid id = new LongUuid();
    final byte[] bytes = id.getBytes();
    final byte[] original = Arrays.copyOf(bytes, bytes.length);

    final LongUuid id2 = new LongUuid(bytes);
    bytes[0] = 0;
    bytes[1] = 0;

    assertArrayEquals(id2.getBytes(), original);
  }

  @Test
  public void testPIDField() throws Exception {
    final LongUuid id = new LongUuid();

    assertEquals(JvmProcessMacIds.getJvmByteId() >> 4 & 0x0F, id.getProcessId());
  }

  @Test
  public void testForDuplicates() {
    final int n = NB;
    final Set<Long> uuids = new HashSet<Long>(n);
    final LongUuid[] uuidArray = new LongUuid[n];

    final long start = System.currentTimeMillis();
    for (int i = 0; i < n; i++) {
      uuidArray[i] = new LongUuid();
    }
    final long stop = System.currentTimeMillis();
    System.out.println("Time = " + (stop - start) + " so " + n / (stop - start) * 1000 + " Uuids/s");

    for (int i = 0; i < n; i++) {
      uuids.add(uuidArray[i].getLong());
    }

    System.out.println("Create " + n + " and get: " + uuids.size());
    assertEquals(n, uuids.size());
    int i = 1;
    int largest = 0;
    for (; i < n; i++) {
      if (uuidArray[i].getTimestamp() != uuidArray[i - 1].getTimestamp()) {
        int j = i + 1;
        final long time = uuidArray[i].getTimestamp();
        for (; j < n; j++) {
          if (uuidArray[j].getTimestamp() != time) {
            if (largest < j - i + 1) {
              largest = j - i + 1;
            }
            i = j;
            break;
          }
        }
      }
    }
    if (largest == 0) {
      largest = n;
    }
    System.out.println(
        "Time elapsed: " + uuidArray[0] + '(' + uuidArray[0].getTimestamp() + ':' + uuidArray[0].getLong() +
        ") - " + uuidArray[n - 1] + '(' + uuidArray[n - 1].getTimestamp() + ':' + uuidArray[n - 1].getLong() +
        ") = " + (uuidArray[n - 1].getLong() - uuidArray[0].getLong()) + " & " +
        (uuidArray[n - 1].getTimestamp() - uuidArray[0].getTimestamp()));
    System.out.println(largest + " different consecutive elements for same time");
  }

  @Test
  public void concurrentGeneration() throws Exception {
    final int numThreads = NB_THREAD;
    final Thread[] threads = new Thread[numThreads];
    final int n = NB;
    final int effectiveN = n / numThreads * numThreads;
    final LongUuid[] uuids = new LongUuid[effectiveN];

    final long start = System.currentTimeMillis();
    for (int i = 0; i < numThreads; i++) {
      threads[i] = new Generator(n / numThreads, uuids, i);
      threads[i].start();
    }

    for (int i = 0; i < numThreads; i++) {
      threads[i].join();
    }
    final long stop = System.currentTimeMillis();
    System.out.println("Time = " + (stop - start) + " so " + n / (stop - start) * 1000 + " Uuids/s");

    final Set<LongUuid> uuidSet = new HashSet<LongUuid>(effectiveN);
    uuidSet.addAll(Arrays.asList(uuids));

    assertEquals(effectiveN, uuidSet.size());
  }

  @Test
  public void concurrentCounterGeneration() throws Exception {
    final int numThreads = NB_THREAD;
    final Thread[] threads = new Thread[numThreads];
    final int n = NB;
    final int effectiveN = n / numThreads * numThreads;
    final long[] uuids = new long[effectiveN];

    final long start = System.currentTimeMillis();
    for (int i = 0; i < numThreads; i++) {
      threads[i] = new CounterParallel(n / numThreads, uuids, i);
      threads[i].start();
    }

    for (int i = 0; i < numThreads; i++) {
      threads[i].join();
    }
    final long stop = System.currentTimeMillis();
    System.out.println("Time = " + (stop - start) + " so " + n / (stop - start) * 1000 + " Counter/s");

    final Set<Long> uuidSet = new HashSet<Long>(effectiveN);
    for (Long i : uuids) {
      uuidSet.add(i);
    }

    assertEquals(effectiveN, uuidSet.size());
  }

  private static class CounterParallel extends Thread {
    final int id;
    final int n;
    private final long[] uuids;

    private CounterParallel(final int n, final long[] uuids, final int id) {
      this.n = n;
      this.uuids = uuids;
      this.id = id * n;
    }

    @Override
    public void run() {
      for (int i = 0; i < n; i++) {
        uuids[id + i] = (System.currentTimeMillis() << 20) + LongUuid.getCounter();
      }
    }
  }

  static class Generator extends Thread {
    final int id;
    final int n;
    private final LongUuid[] uuids;

    Generator(final int n, final LongUuid[] uuids, final int id) {
      this.n = n;
      this.uuids = uuids;
      this.id = id * n;
    }

    @Override
    public void run() {
      for (int i = 0; i < n; i++) {
        uuids[id + i] = new LongUuid();
      }
    }
  }
}