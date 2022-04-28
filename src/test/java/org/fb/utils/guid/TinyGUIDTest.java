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

public class TinyGUIDTest {
  private static final int NB = 1000000;
  private static final String WRONG_ARK3 = "ark:/10a/aiaaabaaagahepcz6ryzyqa";
  private static final String WRONG_ARK2 = "ark:/10aiaaabaaagahepcz6ryzyqa";
  private static final String WRONG_ARK1 =
      "ark:/10/aiaaabaaagahepcz6ryzyqaaaa";
  private static final byte[] WRONG_BYTES = {
      2, 0, 10, 0, 0, 4, 0, 1, -128, 114, 60, 89, -12, 113, -100
  };
  private static final byte[] WRONG_BYTES2 = {
      3, 0, 10, 0, 0, 4, 0, 1, -128, 114, 60, 89, -12, 113, -100, 64
  };
  private static final byte[] WRONG_BYTES3 = {
      2, 0, 10, 0, 0, 4, 0, 1, -128, 114, 60, 89, -12, 113, -100, 64, 12
  };
  private static final String WRONG_STRING_ID =
      "02000a000004000180723c59f4719c40a";
  private static final String BASE16 = "02000a000004000180723c59f4719c40";
  private static final String BASE32 = "aiaauaaaaqaadadshrm7i4m4ia";
  private static final String BASE64 = "AgAKAAAEAAGAcjxZ9HGcQA";
  private static final String BASEARK = "ark:/10/aiaaabaaagahepcz6ryzyqa";
  private static final byte[] BYTES = {
      2, 0, 10, 0, 0, 4, 0, 1, -128, 114, 60, 89, -12, 113, -100, 64
  };
  private static final int VERSION = TinyGUID.VERSION;
  private static final int HEXLENGTH = TinyGUID.KEYSIZE * 2;
  @Rule(order = Integer.MIN_VALUE)
  public TestWatcher watchman = new TestWatcherJunit4();

  @Test
  public void testStructure() {
    TinyGUID id;
    try {
      id = new TinyGUID(BASE32);
      final String str = id.toHex();

      assertEquals('0', str.charAt(0));
      assertEquals('2', str.charAt(1));
      assertEquals(HEXLENGTH, str.length());

      id = new TinyGUID();
      assertEquals(VERSION, id.getVersion());
      assertEquals(0, id.getTenantId());
      assertEquals(JvmProcessId.jvmInstanceIdAsInteger() & 0x7FFFFFFF,
                   id.getPlatformId());
      byte[] platformId = id.getPlatformIdAsBytes();

      id = new TinyGUID(100);
      assertEquals(VERSION, id.getVersion());
      assertEquals(100, id.getTenantId());
      assertEquals(JvmProcessId.jvmInstanceIdAsInteger() & 0x7FFFFFFF,
                   id.getPlatformId());
      assertArrayEquals(platformId, id.getPlatformIdAsBytes());

      id = new TinyGUID(100, 999);
      assertEquals(VERSION, id.getVersion());
      assertEquals(100, id.getTenantId());
      assertEquals(999, id.getPlatformId());
    } catch (final InvalidArgumentRuntimeException e) {
      fail("Should not raize an exception");
    }
  }

  @Test
  public void testParsing() {
    for (int i = 0; i < 1000; i++) {
      TinyGUID id1;
      try {
        id1 = new TinyGUID(BASE32);
      } catch (final InvalidArgumentRuntimeException e) {
        fail("Should not raize an exception");
        return;
      }
      TinyGUID id2;
      try {
        id2 = new TinyGUID(id1.toHex());
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertEquals(0, id1.compareTo(id2));

        final TinyGUID id3 = new TinyGUID(id1.getBytes());
        assertEquals(id1, id3);
        assertEquals(id1.hashCode(), id3.hashCode());
        assertEquals(0, id1.compareTo(id3));

        final TinyGUID id4 = new TinyGUID(id1.toBase32());
        assertEquals(id1, id4);
        assertEquals(id1.hashCode(), id4.hashCode());
        assertEquals(0, id1.compareTo(id4));

        final TinyGUID id5 = new TinyGUID(id1.toArk());
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
    TinyGUID id;
    try {
      id = new TinyGUID(BASE32);
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
      final TinyGUID parsed1 = new TinyGUID(BASE32);
      assertEquals(VERSION, parsed1.getVersion());
    } catch (final InvalidArgumentRuntimeException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testHexBase32() {
    try {
      final TinyGUID parsed1 = new TinyGUID(BASE32);
      final TinyGUID parsed2 = new TinyGUID(BASE64);
      final TinyGUID parsed0 = new TinyGUID(BASE16);
      final TinyGUID parsed8 = new TinyGUID(BASEARK);
      final TinyGUID parsed9 = new TinyGUID(BYTES);
      assertEquals(parsed1, parsed2);
      assertEquals(parsed1, parsed8);
      assertEquals(parsed1, parsed9);
      assertEquals(parsed1, parsed0);
      final TinyGUID parsed3 = new TinyGUID(parsed9.getBytes());
      final TinyGUID parsed4 = new TinyGUID(parsed9.toBase32());
      final TinyGUID parsed5 = new TinyGUID(parsed9.toHex());
      final TinyGUID parsed6 = new TinyGUID(parsed9.toString());
      final TinyGUID parsed7 = new TinyGUID(parsed9.toBase64());
      assertEquals(parsed9, parsed3);
      assertEquals(parsed9, parsed4);
      assertEquals(parsed9, parsed5);
      assertEquals(parsed9, parsed6);
      assertEquals(parsed9, parsed7);
      final TinyGUID generated = new TinyGUID();
      assertEquals(2, generated.getVersion());
    } catch (final InvalidArgumentRuntimeException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testJson() {
    TinyGUID tinyGUID;
    try {
      tinyGUID = new TinyGUID(10, 1024);
      System.out.println(
          "Hex: " + tinyGUID.toHex() + " B32: " + tinyGUID.toBase32() +
          " B64:" + tinyGUID.toBase64() + " Ark:" + tinyGUID.toArk());
      byte[] bytes = tinyGUID.getBytes();
      for (int i = 0; i < bytes.length; i++) {
        System.out.print((int) bytes[i] + " ");
      }
      System.out.println();
      tinyGUID = new TinyGUID(BASE32);
    } catch (final InvalidArgumentRuntimeException e) {
      fail("Should not raize an exception");
      return;
    }
    final String json = JsonHandler.writeAsString(tinyGUID);
    final TinyGUID uuid2 = JsonHandler.getFromString(json, TinyGUID.class);
    assertEquals("Json check", tinyGUID, uuid2);
    final TinyGUID tinyGUID2 = new TinyGUID(tinyGUID.getId());
    final String json2 = JsonHandler.writeAsString(tinyGUID2);
    final TinyGUID uuid3 = JsonHandler.getFromString(json2, TinyGUID.class);
    assertEquals("Json check", tinyGUID, uuid3);
  }

  @Test
  public final void testIllegalArgument() {
    try {
      new TinyGUID(WRONG_ARK1);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      new TinyGUID(WRONG_ARK2);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      new TinyGUID(WRONG_ARK3);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      new TinyGUID(WRONG_BYTES);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      new TinyGUID(WRONG_BYTES2);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      new TinyGUID(WRONG_BYTES3);
    } catch (final InvalidArgumentRuntimeException ignored) {
      fail("SHOULD_NOT_HAVE_AN_EXCEPTION");
    }
    try {
      new TinyGUID(WRONG_STRING_ID);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    TinyGUID tinyGUID = null;
    TinyGUID tinyGUID2 = null;
    try {
      tinyGUID = new TinyGUID(BASE32);
      tinyGUID2 = new TinyGUID(BASE16);
    } catch (final InvalidArgumentRuntimeException e) {
      fail("SHOULD_NOT_HAVE_AN_EXCEPTION");
      return;
    }
    assertNotEquals(null, tinyGUID);
    assertNotEquals(tinyGUID, new Object());
    assertEquals(tinyGUID, tinyGUID);
    assertEquals(tinyGUID, tinyGUID2);
  }


  @Test
  public void concurrentGeneration() throws Exception {
    final int numThreads = 10;
    final Thread[] threads = new Thread[numThreads];
    final int n = NB;
    final int effectiveN = n / numThreads * numThreads;
    final TinyGUID[] uuids = new TinyGUID[effectiveN];

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

    final Set<TinyGUID> uuidSet = new HashSet<TinyGUID>(effectiveN);
    uuidSet.addAll(Arrays.asList(uuids));

    assertEquals(effectiveN, uuidSet.size());
  }

  static class Generator extends Thread {
    final int id;
    final int n;
    private final TinyGUID[] uuids;

    Generator(final int n, final TinyGUID[] uuids, final int id) {
      this.n = n;
      this.uuids = uuids;
      this.id = id * n;
    }

    @Override
    public void run() {
      for (int i = 0; i < n; i++) {
        uuids[id + i] = new TinyGUID();
      }
    }
  }
}
