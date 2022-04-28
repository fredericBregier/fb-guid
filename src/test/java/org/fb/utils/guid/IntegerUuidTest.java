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

import org.fb.utils.various.TestWatcherJunit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class IntegerUuidTest {
  private static final int NB = 1000000;
  @Rule(order = Integer.MIN_VALUE)
  public TestWatcher watchman = new TestWatcherJunit4();

  @Test
  public void testStructure() {
    IntegerUuid id = new IntegerUuid();
    String str = id.toString();

    assertEquals(8, str.length());
  }

  @Test
  public void testParsing() {
    IntegerUuid id1 = new IntegerUuid();
    IntegerUuid id2 = new IntegerUuid(id1.toString());
    assertEquals(id1, id2);
    assertEquals(id1.hashCode(), id2.hashCode());
    assertEquals(id1.getInt(), id2.getInt());

    IntegerUuid id3 = new IntegerUuid(id1.getBytes());
    assertEquals(id1, id3);
    IntegerUuid id4 = new IntegerUuid(id1.getInt());
    assertEquals(id1, id4);
  }

  @Test
  public void testNonSequentialValue() {
    final int n = NB;
    long[] ids = new long[n];

    for (int i = 0; i < n; i++) {
      ids[i] = new IntegerUuid().getInt();
    }

    for (int i = 1; i < n; i++) {
      assertNotEquals(ids[i - 1], ids[i]);
    }
  }

  @Test
  public void testGetBytesImmutability() {
    IntegerUuid id = new IntegerUuid();
    byte[] bytes = id.getBytes();
    byte[] original = Arrays.copyOf(bytes, bytes.length);
    bytes[0] = 0;
    bytes[1] = 0;
    bytes[2] = 0;

    assertArrayEquals(id.getBytes(), original);
  }

  @Test
  public void testConstructorImmutability() {
    IntegerUuid id = new IntegerUuid();
    byte[] bytes = id.getBytes();
    byte[] original = Arrays.copyOf(bytes, bytes.length);

    IntegerUuid id2 = new IntegerUuid(bytes);
    bytes[0] = 0;
    bytes[1] = 0;

    assertArrayEquals(id2.getBytes(), original);
  }

  @Test
  public void testForDuplicates() {
    int n = NB;
    Set<Integer> uuids = new HashSet<Integer>();
    IntegerUuid[] uuidArray = new IntegerUuid[n];

    long start = System.currentTimeMillis();
    for (int i = 0; i < n; i++) {
      uuidArray[i] = new IntegerUuid();
    }
    long stop = System.currentTimeMillis();
    if (stop == start) {
      stop += 1;
    }
    System.out.println(
        "Time = " + (stop - start) + " so " + n * 1000 / (stop - start) +
        " Uuids/s");

    for (int i = 0; i < n; i++) {
      uuids.add(uuidArray[i].getInt());
    }

    System.out.println("Create " + n + " and get: " + uuids.size());
    assertEquals(n, uuids.size());
    int i = 1;
    int largest = 0;
    for (; i < n; i++) {
      if (uuidArray[i].getInt() != uuidArray[i - 1].getInt()) {
        int j = i + 1;
        long time = uuidArray[i].getInt();
        for (; j < n; j++) {
          if (uuidArray[j].getInt() != time) {
            if (largest < j - i + 2) {
              largest = j - i + 2;
            }
          } else {
            i = j;
            break;
          }
        }
        i = j;
      }
    }
    if (largest == 0) {
      largest = n;
    }
    System.out.println(uuidArray[0] + "(" + uuidArray[0].getTimestamp() + ':' +
                       uuidArray[0].getInt() + ") - " + uuidArray[n - 1] + '(' +
                       uuidArray[n - 1].getTimestamp() + ':' +
                       uuidArray[n - 1].getInt() + ") = " +
                       (uuidArray[n - 1].getInt() - uuidArray[0].getInt() + 1));
    System.out.println(largest + " different consecutive elements");
  }

  @Test
  public void concurrentGeneration() throws Exception {
    int numThreads = 10;
    Thread[] threads = new Thread[numThreads];
    int n = NB;
    IntegerUuid[] uuids = new IntegerUuid[n];

    long start = System.currentTimeMillis();
    for (int i = 0; i < numThreads; i++) {
      threads[i] = new Generator(n / numThreads, uuids, i);
      threads[i].start();
    }

    for (int i = 0; i < numThreads; i++) {
      threads[i].join();
    }
    long stop = System.currentTimeMillis();
    System.out.println(
        "Time = " + (stop - start) + " so " + n * 1000 / (stop - start) +
        " Uuids/s");

    int effectiveN = n / numThreads * numThreads;
    Set<IntegerUuid> uuidSet =
        new HashSet<IntegerUuid>(Arrays.asList(uuids).subList(0, effectiveN));

    assertEquals(effectiveN, uuidSet.size());
  }

  static class Generator extends Thread {
    private final IntegerUuid[] uuids;
    int id;
    int n;
    int numThreads;

    Generator(int n, IntegerUuid[] uuids, int id) {
      this.n = n;
      this.uuids = uuids;
      this.id = id * n;
    }

    @Override
    public void run() {
      for (int i = 0; i < n; i++) {
        uuids[id + i] = new IntegerUuid();
      }
    }
  }
}