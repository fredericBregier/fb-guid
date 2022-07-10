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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.fb.utils.exceptions.InvalidArgumentRuntimeException;
import org.fb.utils.guid.GuidFactory.Guid;
import org.fb.utils.various.SysErrLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public abstract class GuidFactoryAbstract {
  static final GuidFactory guidFactory = new GuidFactory();
  private static final int NB = 1000000;
  private static final int VERSION = 3 & 0xFF;
  private static String WRONG_ARK3;
  private static String WRONG_ARK2;
  private static String WRONG_ARK1;
  private static byte[] WRONG_BYTES;
  private static byte[] WRONG_BYTES2;
  private static byte[] WRONG_BYTES3;
  private static String WRONG_STRING_ID;
  private static String BASE16;
  private static String BASE32;
  private static String BASE64;
  private static String BASEARK;
  private static byte[] BYTES;

  @BeforeEach
  public void setup() {
    setupGuidFactory();
    final Guid ref = guidFactory.newGuid(10);
    BASE16 = ref.toHex();
    BASE32 = ref.toBase32();
    BASE64 = ref.toBase64();
    BASEARK = ref.toArk();
    BYTES = ref.getBytes();
    WRONG_STRING_ID = BASE32.substring(0, BASE32.length() - 1);
    WRONG_ARK3 = BASEARK.replace("ark:/10/", "ark:/1a/");
    WRONG_ARK2 = BASEARK.replace("ark:/10/", "ark:/");
    WRONG_ARK1 = BASEARK.substring(0, BASEARK.length() - 1);
    WRONG_BYTES = Arrays.copyOf(BYTES, BYTES.length);
    WRONG_BYTES[0] = 2;
    WRONG_BYTES2 = Arrays.copyOf(BYTES, BYTES.length - 1);
    WRONG_BYTES3 = Arrays.copyOf(BYTES, BYTES.length + 1);
  }

  abstract void setupGuidFactory();

  @Test
  public void testStructure() {
    Guid id;
    try {
      id = guidFactory.getGuid(BASE32);
      final String str = id.toHex();

      assertEquals('A', str.charAt(0));
      assertEquals(guidFactory.getKey16Size(), str.length());

      id = guidFactory.newGuid();
      assertEquals(VERSION, id.getVersion());
      assertEquals(0, id.getTenantId());
      assertEquals(getMasked(guidFactory.getPlatformSize(), JvmProcessMacIds.getMacLong()),
                   id.getPlatformId());
      int pid = id.getProcessId();

      id = guidFactory.newGuid(100);
      assertEquals(VERSION, id.getVersion());
      assertEquals(100, id.getTenantId());
      assertEquals(getMasked(guidFactory.getPlatformSize(), JvmProcessMacIds.getMacLong()),
                   id.getPlatformId());
      assertEquals(pid, id.getProcessId());
    } catch (final InvalidArgumentRuntimeException e) {
      fail("Should not raize an exception");
    }
  }

  private static long getMasked(final int nbBytes, final long value) {
    long mask = 0;
    for (int i = 0; i < nbBytes; i++) {
      mask <<= 8;
      mask += 0xFF;
    }
    return value & mask;
  }

  @Test
  public void testParsing() {
    for (int i = 0; i < 1000; i++) {
      Guid id1;
      try {
        id1 = guidFactory.getGuid(BASE32);
      } catch (final InvalidArgumentRuntimeException e) {
        fail("Should not raize an exception");
        return;
      }
      Guid id2;
      try {
        id2 = guidFactory.getGuid(id1.toHex());
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertEquals(0, id1.compareTo(id2));

        final Guid id3 = guidFactory.getGuid(id1.getBytes());
        assertEquals(id1, id3);
        assertEquals(id1.hashCode(), id3.hashCode());
        assertEquals(0, id1.compareTo(id3));

        final Guid id4 = guidFactory.getGuid(id1.toBase32());
        assertEquals(id1, id4);
        assertEquals(id1.hashCode(), id4.hashCode());
        assertEquals(0, id1.compareTo(id4));

        final Guid id5 = guidFactory.getGuid(id1.toArk());
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
    Guid id;
    try {
      id = guidFactory.getGuid(BASE32);
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
      final Guid parsed1 = guidFactory.getGuid(BASE32);
      assertEquals(VERSION, parsed1.getVersion());
    } catch (final InvalidArgumentRuntimeException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testHexBase32() {
    try {
      final Guid parsed1 = guidFactory.getGuid(BASE32);
      final Guid parsed2 = guidFactory.getGuid(BASE64);
      final Guid parsed0 = guidFactory.getGuid(BASE16);
      final Guid parsed8 = guidFactory.getGuid(BASEARK);
      final Guid parsed9 = guidFactory.getGuid(BYTES);
      assertEquals(parsed1, parsed2);
      assertEquals(parsed1, parsed8);
      assertEquals(parsed1, parsed9);
      assertEquals(parsed1, parsed0);
      final Guid parsed3 = guidFactory.getGuid(parsed9.getBytes());
      final Guid parsed4 = guidFactory.getGuid(parsed9.toBase32());
      final Guid parsed5 = guidFactory.getGuid(parsed9.toHex());
      final Guid parsed6 = guidFactory.getGuid(parsed9.toString());
      final Guid parsed7 = guidFactory.getGuid(parsed9.toBase64());
      assertEquals(parsed9, parsed3);
      assertEquals(parsed9, parsed4);
      assertEquals(parsed9, parsed5);
      assertEquals(parsed9, parsed6);
      assertEquals(parsed9, parsed7);
      final Guid generated = guidFactory.newGuid();
      assertEquals(3, generated.getVersion());
    } catch (final InvalidArgumentRuntimeException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testJson() throws JsonProcessingException {
    Guid guid;
    try {
      guid = guidFactory.newGuid(10, 1024);
      System.out.println(
          "Hex: " + guid.toHex() + " B32: " + guid.toBase32() + " B64:" + guid.toBase64() + " Ark:" +
          guid.toArk());
      final byte[] bytes = guid.getBytes();
      for (int i = 0; i < bytes.length; i++) {
        System.out.print((int) bytes[i] + ", ");
      }
      System.out.println();
      guidFactory.getGuid(guid.toBase32());
      guid = guidFactory.getGuid(BASE32);
    } catch (final InvalidArgumentRuntimeException e) {
      fail("Should not raize an exception");
      return;
    }
    final String json = guid.getJson();
    System.out.println(json);
    final Guid uuid2 = GuidFactory.getFromJson(json);
    assertEquals(guid, uuid2);
    final Guid guid2 = guidFactory.getGuid(guid.getId());
    final String json2 = guid2.getJson();
    final Guid uuid3 = GuidFactory.getFromJson(json2);
    assertEquals(guid, uuid3);
  }

  @Test
  public final void testIllegalArgument() {
    try {
      guidFactory.getGuid(WRONG_ARK1);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      guidFactory.getGuid(WRONG_ARK2);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      guidFactory.getGuid(WRONG_ARK3);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      guidFactory.getGuid(WRONG_BYTES);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      guidFactory.getGuid(WRONG_BYTES2);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    try {
      guidFactory.getGuid(WRONG_BYTES3);
    } catch (final InvalidArgumentRuntimeException ignored) {
      ignored.printStackTrace();
      fail("SHOULD_NOT_HAVE_AN_EXCEPTION");
    }
    try {
      guidFactory.getGuid(WRONG_STRING_ID);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException ignored) {
    }
    Guid guid = null;
    Guid guid2 = null;
    try {
      guid = guidFactory.getGuid(BASE32);
      guid2 = guidFactory.getGuid(BASE16);
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
    final int n = NB;
    final int effectiveN = n / numThreads * numThreads;
    final Guid[] uuids = new Guid[effectiveN];

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

    final Set<Guid> uuidSet = new HashSet<Guid>(effectiveN);
    uuidSet.addAll(Arrays.asList(uuids));
    if (guidFactory.getCounterSize() > 2) {
      assertEquals(effectiveN, uuidSet.size());
    } else if (effectiveN != uuidSet.size()) {
      SysErrLogger.FAKE_LOGGER.syserr(
          "Concurrent generation in error since counter not big enough: " + guidFactory.getCounterSize() +
          "with " + uuidSet.size() + " items against willing " + effectiveN);
    }
  }

  static class Generator extends Thread {
    final int id;
    final int n;
    private final Guid[] uuids;

    Generator(final int n, final Guid[] uuids, final int id) {
      this.n = n;
      this.uuids = uuids;
      this.id = id * n;
    }

    @Override
    public void run() {
      for (int i = 0; i < n; i++) {
        uuids[id + i] = guidFactory.newGuid();
      }
    }
  }

  static class GeneratorGuid extends Thread {
    final int id;
    final int n;
    private final Guid[] uuids;
    private final GuidFactory factory;

    GeneratorGuid(final int n, final Guid[] uuids, final int id, final GuidFactory factory) {
      this.n = n;
      this.uuids = uuids;
      this.id = id * n;
      this.factory = factory;
    }

    @Override
    public void run() {
      for (int i = 0; i < n; i++) {
        uuids[id + i] = factory.newGuid();
      }
    }
  }
}
