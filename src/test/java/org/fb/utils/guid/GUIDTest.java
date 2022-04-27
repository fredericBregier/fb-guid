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
import org.fb.utils.json.JsonHandler;
import org.fb.utils.various.TestWatcherJunit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class GUIDTest {
  private static final String WRONG_ARK3 =
      "ark:/1a/aeasppnwoyafrlybkt3kfuyaaaaac";
  private static final String WRONG_ARK2 =
      "ark:/1aeasppnwoyafrlybkt3kfuyaaaaac";
  private static final String WRONG_ARK1 =
      "ark:/1/aeasppnwoyafrlybkt3kfuyaaaaacaaaaa";
  private static final byte[] WRONG_BYTES = {
      2, 1, 0, 0, 0, 1, 39, -67, -74, 118, 0, 88, -81, 1, 84, -10, -94, -45, 0,
      0, 0, 1
  };
  private static final String WRONG_STRING_ID =
      "02010000000127bdb6760058af0154f6a2d300000001";
  private static final String BASE16 =
      "0100000000000000000020ae016c1e21cc0c000001";
  private static final String BASE32 = "aeaaaaaaaaaaaaaaecxac3a6ehgayaaaae";
  private static final String BASE64 = "AQAAAAAAAAAAACCuAWweIcwMAAAB";
  private static final String BASEARK = "ark:/0/aeaaaaaaaaqk4almdyq4ydaaaaaq";
  private static final byte[] BYTES = {
      1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, -82, 1, 108, 30, 33, -52, 12, 0, 0, 1
  };
  private static final int VERSION = 1 & 0x1F;
  private static final int HEXLENGTH = GUID.KEYSIZE * 2;
  @Rule(order = Integer.MIN_VALUE)
  public TestWatcher watchman = new TestWatcherJunit4();

  @Test
  public void testStructure() {
    GUID id;
    try {
      id = new GUID(BASE32);
      final String str = id.toHex();

      assertEquals('0', str.charAt(0));
      assertEquals('1', str.charAt(1));
      assertEquals(HEXLENGTH, str.length());

      id = new GUID();
      assertEquals(VERSION, id.getVersion());
      assertEquals(0, id.getTenantId());
      assertEquals(JvmProcessId.macAddressAsInt() & 0x7FFFFFFF, id.getPlatformId());
      long pid = id.getProcessId();
      byte []platformId = id.getPlatformIdAsBytes();

      id = new GUID(100);
      assertEquals(VERSION, id.getVersion());
      assertEquals(100, id.getTenantId());
      assertEquals(JvmProcessId.macAddressAsInt() & 0x7FFFFFFF, id.getPlatformId());
      assertEquals(pid, id.getProcessId());
      assertArrayEquals(platformId, id.getPlatformIdAsBytes());
    } catch (final InvalidArgumentRuntimeException e) {
      fail("Should not raize an exception");
    }
  }

  @Test
  public void testParsing() {
    for (int i = 0; i < 1000; i++) {
      GUID id1;
      try {
        id1 = new GUID(BASE32);
      } catch (final InvalidArgumentRuntimeException e) {
        fail("Should not raize an exception");
        return;
      }
      GUID id2;
      try {
        id2 = new GUID(id1.toHex());
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertEquals(0, id1.compareTo(id2));

        final GUID id3 = new GUID(id1.getBytes());
        assertEquals(id1, id3);
        assertEquals(id1.hashCode(), id3.hashCode());
        assertEquals(0, id1.compareTo(id3));

        final GUID id4 = new GUID(id1.toBase32());
        assertEquals(id1, id4);
        assertEquals(id1.hashCode(), id4.hashCode());
        assertEquals(0, id1.compareTo(id4));

        final GUID id5 = new GUID(id1.toArk());
        assertEquals(id1, id5);
        assertEquals(id1.hashCode(), id5.hashCode());
        assertEquals(0, id1.compareTo(id5));
      } catch (final InvalidArgumentRuntimeException e) {
        fail(e.getMessage());
      }
    }
  }

  @Test
  public void testGetBytesImmutability() {
    GUID id;
    try {
      id = new GUID(BASE32);
    } catch (final InvalidArgumentRuntimeException e) {
      fail("Should not raize an exception");
      return;
    }
    final byte[] bytes = id.getBytes();
    final byte[] original = Arrays.copyOf(bytes, bytes.length);
    bytes[0] = 0;
    bytes[1] = 0;
    bytes[2] = 0;

    assertArrayEquals(id.getBytes(), original);
  }

  @Test
  public void testVersionField() {
    try {
      final GUID parsed1 = new GUID(BASE32);
      assertEquals(VERSION, parsed1.getVersion());
    } catch (final InvalidArgumentRuntimeException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testHexBase32() {
    try {
      final GUID parsed1 = new GUID(BASE32);
      final GUID parsed2 = new GUID(BASE64);
      final GUID parsed0 = new GUID(BASE16);
      final GUID parsed8 = new GUID(BASEARK);
      final GUID parsed9 = new GUID(BYTES);
      assertEquals(parsed1, parsed2);
      assertEquals(parsed1, parsed8);
      assertEquals(parsed1, parsed9);
      assertEquals(parsed1, parsed0);
      final GUID parsed3 = new GUID(parsed9.getBytes());
      final GUID parsed4 = new GUID(parsed9.toBase32());
      final GUID parsed5 = new GUID(parsed9.toHex());
      final GUID parsed6 = new GUID(parsed9.toString());
      final GUID parsed7 = new GUID(parsed9.toBase64());
      assertEquals(parsed9, parsed3);
      assertEquals(parsed9, parsed4);
      assertEquals(parsed9, parsed5);
      assertEquals(parsed9, parsed6);
      assertEquals(parsed9, parsed7);
      final GUID generated = new GUID();
      assertEquals(1, generated.getVersion());
    } catch (final InvalidArgumentRuntimeException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testJson() {
    GUID guid;
    try {
      guid = new GUID(BASE32);
    } catch (final InvalidArgumentRuntimeException e) {
      fail("Should not raize an exception");
      return;
    }
    final String json = JsonHandler.writeAsString(guid);
    final GUID uuid2 = JsonHandler.getFromString(json, GUID.class);
    assertEquals("Json check", guid, uuid2);
    final GUID guid2 = new GUID(guid.getId());
    final String json2 = JsonHandler.writeAsString(guid2);
    final GUID uuid3 = JsonHandler.getFromString(json2, GUID.class);
    assertEquals("Json check", guid, uuid3);
  }

  @Test
  public final void testIllegalArgument() {
    try {
      new GUID(WRONG_ARK1);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      new GUID(WRONG_ARK2);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      new GUID(WRONG_ARK3);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      new GUID(WRONG_BYTES);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      new GUID(WRONG_STRING_ID);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    GUID guid = null;
    GUID guid2 = null;
    try {
      guid = new GUID(BASE32);
      guid2 = new GUID(BASE16);
    } catch (final InvalidArgumentRuntimeException e) {
      fail("SHOULD_NOT_HAVE_AN_EXCEPTION");
      return;
    }
    assertNotEquals(null, guid);
    assertNotEquals(guid, new Object());
    assertEquals(guid, guid);
    assertEquals(guid, guid2);
  }


  @Test
  public void concurrentGeneration() throws Exception {
    final int numThreads = 10;
    final Thread[] threads = new Thread[numThreads];
    final int n = 1000000;
    final int effectiveN = n / numThreads * numThreads;
    final GUID[] uuids = new GUID[effectiveN];

    final long start = System.currentTimeMillis();
    for (int i = 0; i < numThreads; i++) {
      threads[i] = new Generator(n / numThreads, uuids, i);
      threads[i].start();
    }

    for (int i = 0; i < numThreads; i++) {
      threads[i].join();
    }
    final long stop = System.currentTimeMillis();
    System.out.println(
        "Time = " + (stop - start) + " so " + n / (stop - start) * 1000 +
        " Uuids/s");

    final Set<GUID> uuidSet = new HashSet<GUID>(effectiveN);
    uuidSet.addAll(Arrays.asList(uuids));

    assertEquals(effectiveN, uuidSet.size());
  }

  private static class Generator extends Thread {
    final int id;
    final int n;
    private final GUID[] uuids;

    private Generator(final int n, final GUID[] uuids, final int id) {
      this.n = n;
      this.uuids = uuids;
      this.id = id * n;
    }

    @Override
    public void run() {
      for (int i = 0; i < n; i++) {
        uuids[id + i] = new GUID();
      }
    }
  }
}
