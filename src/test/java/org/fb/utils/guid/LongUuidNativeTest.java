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

public class LongUuidNativeTest {
  private static final int NB = 1000000;
  private static final int NB_THREAD = 10;

  @Test
  public void testParsing() {
    final long id1 = LongUuid.getLongUuid();
    final LongUuid id2 = new LongUuid(id1);
    assertEquals(id1, id2.getLong());
  }

  @Test
  public void testNonSequentialValue() {
    final int n = NB;
    final long[] ids = new long[n];

    for (int i = 0; i < n; i++) {
      ids[i] = LongUuid.getLongUuid();
    }

    for (int i = 1; i < n; i++) {
      assertNotEquals(ids[i - 1], ids[i]);
    }
  }

  @Test
  public void testPIDField() throws Exception {
    final long id = LongUuid.getLongUuid();
    final LongUuid longUuid = new LongUuid(id);
    assertEquals(JvmProcessMacIds.getJvmByteId() >> 4 & 0x0F, longUuid.getProcessId());
  }

  @Test
  public void testForDuplicates() {
    final int n = NB;
    final Set<Long> uuids = new HashSet<Long>(n);
    final Long[] uuidArray = new Long[n];

    final long start = System.currentTimeMillis();
    for (int i = 0; i < n; i++) {
      uuidArray[i] = LongUuid.getLongUuid();
    }
    final long stop = System.currentTimeMillis();
    System.out.println("Time = " + (stop - start) + " so " + n / (stop - start) * 1000 + " Uuids/s");

    uuids.addAll(Arrays.asList(uuidArray));

    System.out.println("Create " + n + " and get: " + uuids.size());
    assertEquals(n, uuids.size());
    System.out.println("Time elapsed: " + uuidArray[0] + " - " + uuidArray[n - 1] + " = " +
                       (uuidArray[n - 1] - uuidArray[0]) + " & " +
                       (new LongUuid(uuidArray[n - 1]).getTimestamp() -
                        new LongUuid(uuidArray[0]).getTimestamp()));
  }

  @Test
  public void concurrentGeneration() throws Exception {
    final int numThreads = NB_THREAD;
    final Thread[] threads = new Thread[numThreads];
    final int n = NB;
    final int effectiveN = n / numThreads * numThreads;
    final Long[] uuids = new Long[effectiveN];

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

    final Set<Long> uuidSet = new HashSet<Long>(effectiveN);
    uuidSet.addAll(Arrays.asList(uuids));

    assertEquals(effectiveN, uuidSet.size());
  }

  @Test
  public void concurrentCounterGeneration() throws Exception {
    final int numThreads = NB_THREAD;
    final Thread[] threads = new Thread[numThreads];
    final int n = NB;
    final int effectiveN = n / numThreads * numThreads;
    final Long[] uuids = new Long[effectiveN];

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
    uuidSet.addAll(Arrays.asList(uuids));

    assertEquals(effectiveN, uuidSet.size());
  }

  private static class CounterParallel extends Thread {
    final int id;
    final int n;
    private final Long[] uuids;

    private CounterParallel(final int n, final Long[] uuids, final int id) {
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

  private static class Generator extends Thread {
    final int id;
    final int n;
    private final Long[] uuids;

    private Generator(final int n, final Long[] uuids, final int id) {
      this.n = n;
      this.uuids = uuids;
      this.id = id * n;
    }

    @Override
    public void run() {
      for (int i = 0; i < n; i++) {
        uuids[id + i] = LongUuid.getLongUuid();
      }
    }
  }
}