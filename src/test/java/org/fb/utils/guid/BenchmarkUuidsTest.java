/*
 * Copyright (c) 2022. FbUtilities Contributors and Frederic Bregier
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

import org.fb.utils.guid.GuidFactory.GUID_CONFIGURATION;
import org.fb.utils.guid.GuidFactory.Guid;
import org.junit.Test;

public class BenchmarkUuidsTest {
  private static final int NB = 20000000;

  @Test
  public void concurrentGeneration() throws Exception {
    final int numThreads = 10;
    final Thread[] threads = new Thread[numThreads];
    final int n = NB;
    final int effectiveN = n / numThreads * numThreads;

    // Warmup
    long timems = setTinyGUIDs(threads, effectiveN, n, numThreads);
    System.out.println("Tiny: Time = " + timems + " so " + n / timems * 1000 + " Uuids/s");

    timems = setIntegerUuids(threads, effectiveN, n, numThreads);
    System.out.println("Intg: Time = " + timems + " so " + n / timems * 1000 + " Uuids/s");
    timems = setLongUuids(threads, effectiveN, n, numThreads);
    System.out.println("Long: Time = " + timems + " so " + n / timems * 1000 + " Uuids/s");
    timems = setGUIDs(threads, effectiveN, n, numThreads);
    System.out.println("GUID: Time = " + timems + " so " + n / timems * 1000 + " Uuids/s");
    timems = setTinyGUIDs(threads, effectiveN, n, numThreads);
    System.out.println("Tiny: Time = " + timems + " so " + n / timems * 1000 + " Uuids/s");
    timems = setGuids(threads, effectiveN, n, numThreads,
                      new GuidFactory().useConfiguration(GUID_CONFIGURATION.SMALLEST)
                                       .setCounterSize((short) 3));
    System.out.println("Smallest: Time = " + timems + " so " + n / timems * 1000 + " Uuids/s");
    timems = setGuids(threads, effectiveN, n, numThreads,
                      new GuidFactory().useConfiguration(GUID_CONFIGURATION.TINY));
    System.out.println("Tiny2: Time = " + timems + " so " + n / timems * 1000 + " Uuids/s");
    timems = setGuids(threads, effectiveN, n, numThreads,
                      new GuidFactory().useConfiguration(GUID_CONFIGURATION.STANDARD));
    System.out.println("Standard: Time = " + timems + " so " + n / timems * 1000 + " Uuids/s");
    timems = setGuids(threads, effectiveN, n, numThreads,
                      new GuidFactory().useConfiguration(GUID_CONFIGURATION.BIGGEST));
    System.out.println("Biggest: Time = " + timems + " so " + n / timems * 1000 + " Uuids/s");
  }

  private long setTinyGUIDs(final Thread[] threads, final int effectiveN, final int n, final int numThreads)
      throws InterruptedException {
    final TinyGUID[] tinyGUIDs = new TinyGUID[effectiveN];
    final long start = System.currentTimeMillis();
    for (int i = 0; i < numThreads; i++) {
      threads[i] = new TinyGUIDTest.Generator(n / numThreads, tinyGUIDs, i);
      threads[i].start();
    }
    for (int i = 0; i < numThreads; i++) {
      threads[i].join();
    }
    final long stop = System.currentTimeMillis();
    return stop - start;
  }

  private long setIntegerUuids(final Thread[] threads, final int effectiveN, final int n,
                               final int numThreads) throws InterruptedException {
    IntegerUuid[] integerUuids = new IntegerUuid[n];
    long start = System.currentTimeMillis();
    for (int i = 0; i < numThreads; i++) {
      threads[i] = new IntegerUuidTest.Generator(n / numThreads, integerUuids, i);
      threads[i].start();
    }
    for (int i = 0; i < numThreads; i++) {
      threads[i].join();
    }
    long stop = System.currentTimeMillis();
    return stop - start;
  }

  private long setLongUuids(final Thread[] threads, final int effectiveN, final int n, final int numThreads)
      throws InterruptedException {
    final LongUuid[] longUuids = new LongUuid[effectiveN];
    final long start = System.currentTimeMillis();
    for (int i = 0; i < numThreads; i++) {
      threads[i] = new LongUuidTest.Generator(n / numThreads, longUuids, i);
      threads[i].start();
    }
    for (int i = 0; i < numThreads; i++) {
      threads[i].join();
    }
    final long stop = System.currentTimeMillis();
    return stop - start;
  }

  private long setGUIDs(final Thread[] threads, final int effectiveN, final int n, final int numThreads)
      throws InterruptedException {
    GUID[] uuids = new GUID[effectiveN];
    final long start = System.currentTimeMillis();
    for (int i = 0; i < numThreads; i++) {
      threads[i] = new GUIDTest.Generator(n / numThreads, uuids, i);
      threads[i].start();
    }
    for (int i = 0; i < numThreads; i++) {
      threads[i].join();
    }
    final long stop = System.currentTimeMillis();
    return stop - start;
  }

  private long setGuids(final Thread[] threads, final int effectiveN, final int n, final int numThreads,
                        final GuidFactory guidFactory) throws InterruptedException {
    Guid[] uuids = new Guid[effectiveN];
    final long start = System.currentTimeMillis();
    for (int i = 0; i < numThreads; i++) {
      threads[i] = new GuidFactoryAbstract.GeneratorGuid(n / numThreads, uuids, i, guidFactory);
      threads[i].start();
    }
    for (int i = 0; i < numThreads; i++) {
      threads[i].join();
    }
    final long stop = System.currentTimeMillis();
    return stop - start;
  }

}
