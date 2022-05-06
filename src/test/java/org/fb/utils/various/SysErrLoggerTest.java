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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class SysErrLoggerTest {
  private static final String NOT_EMPTY = "Not empty";
  private static final StringBuilder buf = new StringBuilder();
  private static PrintStream err;
  private static PrintStream out;
  @Rule(order = Integer.MIN_VALUE)
  public TestWatcher watchman = new TestWatcherJunit4();

  @BeforeClass
  public static void setUpBeforeClass() {
    err = System.err; // NOSONAR since Logger test
    try {
      System.setErr(new PrintStream(new OutputStream() {
        @Override
        public void write(final int b) {
          buf.append((char) b);
        }
      }, true, "UTF-8"));
    } catch (final UnsupportedEncodingException ignore) {
      // Ignore
    }
    out = System.out; // NOSONAR since Logger test
    try {
      System.setOut(new PrintStream(new OutputStream() {
        @Override
        public void write(final int b) {
          buf.append((char) b);
        }
      }, true, "UTF-8"));
    } catch (final UnsupportedEncodingException ignore) {
      // Ignore
    }
  }

  @AfterClass
  public static void tearDownAfterClass() {
    System.setErr(err);
    System.setOut(out);
  }

  @Test
  public void testSyserr() {
    buf.setLength(0);
    SysErrLogger.FAKE_LOGGER.ignoreLog(new Exception("Fake exception"));
    assertEquals(0, buf.length());
    SysErrLogger.FAKE_LOGGER.syserr(NOT_EMPTY);
    assertTrue(buf.length() > 0);
    buf.setLength(0);
    SysErrLogger.FAKE_LOGGER.syserr();
    assertTrue(buf.length() > 0);
    buf.setLength(0);
    SysErrLogger.FAKE_LOGGER.syserr(NOT_EMPTY, new Exception("Fake exception"));
    assertTrue(buf.length() > NOT_EMPTY.length() + 5);
    buf.setLength(0);
  }

  @Test
  public void testSysout() {
    buf.setLength(0);
    SysErrLogger.FAKE_LOGGER.ignoreLog(new Exception("Fake exception"));
    assertEquals(0, buf.length());
    SysErrLogger.FAKE_LOGGER.sysout(NOT_EMPTY);
    assertTrue(buf.length() > 0);
    buf.setLength(0);
    SysErrLogger.FAKE_LOGGER.sysout();
    assertTrue(buf.length() > 0);
    buf.setLength(0);
  }
}