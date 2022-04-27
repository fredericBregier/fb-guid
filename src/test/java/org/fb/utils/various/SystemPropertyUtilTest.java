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

package org.fb.utils.various;

import org.fb.utils.exceptions.InvalidArgumentRuntimeException;
import org.fb.utils.various.SystemPropertyUtil.Platform;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class SystemPropertyUtilTest {
  private static final String OS_NAME = "os.name";
  private static final String OTHER = "other";
  private static final String KEY_TEST = "keyTest";
  private static final String KEY_ITEST = "keyTestI";
  private static final String KEY_LTEST = "keyTestL";
  private static final String KEY_BTEST = "keyTestB";
  private static final String KEY_VALUE = "KeyValue";
  private static final int KEY_IVALUE = 1;
  private static final long KEY_LVALUE = 2L;
  private static final boolean KEY_BVALUE = true;
  @Rule(order = Integer.MIN_VALUE)
  public TestWatcher watchman = new TestWatcherJunit4();

  @Test
  public final void testSystemPropertyDefault() {
    SystemPropertyUtil.refresh();
    assertNull(SystemPropertyUtil.get(SystemPropertyUtil.MACHINE_ID));
    SystemPropertyUtil.set(SystemPropertyUtil.MACHINE_ID, "AE1632");
    assertEquals("AE1632", SystemPropertyUtil.getMachineId());
    SystemPropertyUtil.clear(SystemPropertyUtil.MACHINE_ID);
    SystemPropertyUtil.refresh();
    assertNull(SystemPropertyUtil.getMachineId());
  }

  @Test
  public final void testSystemPropertyString() {
    SystemPropertyUtil.refresh();
    SystemPropertyUtil.set(KEY_TEST, KEY_VALUE);
    assertTrue(SystemPropertyUtil.contains(KEY_TEST));
    assertEquals(KEY_VALUE, SystemPropertyUtil.get(KEY_TEST));
    assertEquals(KEY_VALUE, SystemPropertyUtil.get(KEY_TEST, OTHER));
    assertEquals(KEY_VALUE, SystemPropertyUtil.getAndSet(KEY_TEST, OTHER));
    assertEquals(OTHER, SystemPropertyUtil.getAndSet(KEY_TEST + '2', OTHER));
    assertEquals(OTHER, SystemPropertyUtil.set(KEY_TEST + '2', OTHER));
  }

  @Test
  public final void testSystemPropertyBoolean() {
    SystemPropertyUtil.set(KEY_BTEST, KEY_BVALUE);
    assertTrue(SystemPropertyUtil.contains(KEY_BTEST));
    assertEquals(Boolean.toString(KEY_BVALUE),
                 SystemPropertyUtil.get(KEY_BTEST));
    assertEquals(KEY_BVALUE, SystemPropertyUtil.get(KEY_BTEST, false));
    assertEquals(KEY_BVALUE, SystemPropertyUtil.getAndSet(KEY_BTEST, false));
    assertFalse(SystemPropertyUtil.getAndSet(KEY_BTEST + '2', false));
    assertFalse(SystemPropertyUtil.set(KEY_BTEST + '2', false));
    assertFalse(SystemPropertyUtil.get(KEY_BTEST + '3', false));
    assertNull(SystemPropertyUtil.set(KEY_BTEST + '3', "true"));
    assertTrue(SystemPropertyUtil.get(KEY_BTEST + '3', false));
    assertEquals("true", SystemPropertyUtil.set(KEY_BTEST + '3', "yes"));
    assertTrue(SystemPropertyUtil.get(KEY_BTEST + '3', false));
    assertEquals("yes", SystemPropertyUtil.set(KEY_BTEST + '3', "1"));
    assertTrue(SystemPropertyUtil.get(KEY_BTEST + '3', false));
    assertEquals("1", SystemPropertyUtil.set(KEY_BTEST + '3', "yes2"));
    assertFalse(SystemPropertyUtil.get(KEY_BTEST + '3', false));
    assertEquals("yes2", SystemPropertyUtil.set(KEY_BTEST + '3', ""));
    assertTrue(SystemPropertyUtil.get(KEY_BTEST + '3', false));
  }

  @Test
  public final void testSystemPropertyInt() {
    SystemPropertyUtil.set(KEY_ITEST, KEY_IVALUE);
    assertTrue(SystemPropertyUtil.contains(KEY_ITEST));
    assertEquals(Integer.toString(KEY_IVALUE),
                 SystemPropertyUtil.get(KEY_ITEST));
    assertEquals(KEY_IVALUE, SystemPropertyUtil.get(KEY_ITEST, 4));
    assertEquals(KEY_IVALUE, SystemPropertyUtil.getAndSet(KEY_ITEST, 4));
    assertEquals(4, SystemPropertyUtil.getAndSet(KEY_ITEST + '2', 4));
    assertEquals(4, SystemPropertyUtil.set(KEY_ITEST + '2', 4));
    assertEquals(5, SystemPropertyUtil.get(KEY_ITEST + '3', 5));
    assertNull(SystemPropertyUtil.set(KEY_ITEST + '3', "yes2"));
    assertEquals(6, SystemPropertyUtil.get(KEY_ITEST + '3', 6));
  }

  @Test
  public final void testSystemPropertyLong() {
    SystemPropertyUtil.set(KEY_LTEST, KEY_LVALUE);
    assertTrue(SystemPropertyUtil.contains(KEY_LTEST));
    assertEquals(Long.toString(KEY_LVALUE), SystemPropertyUtil.get(KEY_LTEST));
    assertEquals(KEY_LVALUE, SystemPropertyUtil.get(KEY_LTEST, 3L));
    assertEquals(KEY_LVALUE, SystemPropertyUtil.getAndSet(KEY_LTEST, 3L));
    assertEquals(3L, SystemPropertyUtil.getAndSet(KEY_LTEST + '2', 3L));
    assertEquals(3L, SystemPropertyUtil.set(KEY_LTEST + '2', 3L));
    assertEquals(4L, SystemPropertyUtil.get(KEY_LTEST + '3', 4L));
    assertNull(SystemPropertyUtil.set(KEY_LTEST + '3', "yes2"));
    assertEquals(5L, SystemPropertyUtil.get(KEY_LTEST + '3', 5L));
  }

  @Test
  public final void testSystemPropertyDebug() {
    final AtomicBoolean bool = new AtomicBoolean(false);
    final OutputStream outputStream = new OutputStream() {
      @Override
      public void write(byte[] b, int off, int len) throws IOException {
        bool.set(true);
      }

      @Override
      public void write(byte[] b) throws IOException {
        bool.set(true);
      }

      @Override
      public void write(int i) throws IOException {
        bool.set(true);
      }
    };
    final PrintStream out = new PrintStream(outputStream);
    SystemPropertyUtil.debug(out);
    assertTrue(bool.get());
  }

  @Test
  public final void testSystemPropertyOs() {
    SystemPropertyUtil.get(OS_NAME);
    final Platform platform = SystemPropertyUtil.getOS();
    switch (platform) {
      case MAC: {
        assertTrue(SystemPropertyUtil.isMac());
        assertFalse(SystemPropertyUtil.isWindows());
        assertFalse(SystemPropertyUtil.isUnix());
        assertFalse(SystemPropertyUtil.isSolaris());
        break;
      }
      case SOLARIS: {
        assertFalse(SystemPropertyUtil.isMac());
        assertFalse(SystemPropertyUtil.isWindows());
        assertFalse(SystemPropertyUtil.isUnix());
        assertTrue(SystemPropertyUtil.isSolaris());
        break;
      }
      case UNIX: {
        assertFalse(SystemPropertyUtil.isMac());
        assertFalse(SystemPropertyUtil.isWindows());
        assertTrue(SystemPropertyUtil.isUnix());
        assertFalse(SystemPropertyUtil.isSolaris());
        break;
      }
      case UNSUPPORTED: {
        assertFalse(SystemPropertyUtil.isMac());
        assertFalse(SystemPropertyUtil.isWindows());
        assertFalse(SystemPropertyUtil.isUnix());
        assertFalse(SystemPropertyUtil.isSolaris());
        break;
      }
      case WINDOWS: {
        assertFalse(SystemPropertyUtil.isMac());
        assertTrue(SystemPropertyUtil.isWindows());
        assertFalse(SystemPropertyUtil.isUnix());
        assertFalse(SystemPropertyUtil.isSolaris());
        break;
      }
      default: {
      }
    }
  }

  @Test
  public final void testSystemPropertyError() {
    try {
      SystemPropertyUtil.contains(null);
      fail("SHOULD_RAIZED_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) {// NOSONAR

    }
    try {
      SystemPropertyUtil.get(null);
      fail("SHOULD_RAIZED_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) {// NOSONAR

    }
    try {
      SystemPropertyUtil.get(null, KEY_IVALUE);
      fail("SHOULD_RAIZED_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) {// NOSONAR

    }
    try {
      SystemPropertyUtil.get(null, KEY_BVALUE);
      fail("SHOULD_RAIZED_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) {// NOSONAR

    }
    try {
      SystemPropertyUtil.get(null, KEY_LVALUE);
      fail("SHOULD_RAIZED_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) {// NOSONAR

    }
    try {
      SystemPropertyUtil.getAndSet(null, KEY_VALUE);
      fail("SHOULD_RAIZED_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) {// NOSONAR

    }
    try {
      SystemPropertyUtil.getAndSet(null, KEY_BVALUE);
      fail("SHOULD_RAIZED_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) {// NOSONAR

    }
    try {
      SystemPropertyUtil.getAndSet(null, KEY_IVALUE);
      fail("SHOULD_RAIZED_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) {// NOSONAR

    }
    try {
      SystemPropertyUtil.getAndSet(null, KEY_LVALUE);
      fail("SHOULD_RAIZED_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) {// NOSONAR

    }
    try {
      SystemPropertyUtil.set(null, KEY_VALUE);
      fail("SHOULD_RAIZED_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) {// NOSONAR

    }
    try {
      SystemPropertyUtil.set(null, KEY_BVALUE);
      fail("SHOULD_RAIZED_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) {// NOSONAR

    }
    try {
      SystemPropertyUtil.set(null, KEY_IVALUE);
      fail("SHOULD_RAIZED_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) {// NOSONAR

    }
    try {
      SystemPropertyUtil.set(null, KEY_LVALUE);
      fail("SHOULD_RAIZED_AN_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) {// NOSONAR

    }

  }
}
