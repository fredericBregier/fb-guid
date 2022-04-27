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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

public class BaseXxTest {
  @Rule(order = Integer.MIN_VALUE)
  public TestWatcher watchman = new TestWatcherJunit4();


  @Test(expected = InvalidArgumentRuntimeException.class)
  public void testBase16() throws IOException {
    BaseXx.getBase16(null);
    fail("EXPECTING_EXCEPTION_ILLEGAL_ARGUMENT_EXCEPTION");
  }

  @Test(expected = InvalidArgumentRuntimeException.class)
  public void testBase32() throws FileNotFoundException {
    BaseXx.getBase32(null);
    fail("EXPECTING_EXCEPTION_ILLEGAL_ARGUMENT_EXCEPTION");
  }

  @Test(expected = InvalidArgumentRuntimeException.class)
  public void testBase64() throws IOException {
    BaseXx.getBase64UrlWithoutPadding(null);
    fail("EXPECTING_EXCEPTION_ILLEGAL_ARGUMENT_EXCEPTION");
  }

  @Test(expected = InvalidArgumentRuntimeException.class)
  public void testBase64UrlPadding() throws IOException {
    BaseXx.getBase64UrlWithPadding(null);
    fail("EXPECTING_EXCEPTION_ILLEGAL_ARGUMENT_EXCEPTION");
  }


  @Test(expected = InvalidArgumentRuntimeException.class)
  public void testFromBase16() throws IOException {
    BaseXx.getFromBase16(null);
    fail("EXPECTING_EXCEPTION_ILLEGAL_ARGUMENT_EXCEPTION");
  }

  @Test(expected = InvalidArgumentRuntimeException.class)
  public void testFromBase32() throws FileNotFoundException {
    BaseXx.getFromBase32(null);
    fail("EXPECTING_EXCEPTION_ILLEGAL_ARGUMENT_EXCEPTION");
  }

  @Test(expected = InvalidArgumentRuntimeException.class)
  public void testFromBase64() throws IOException {
    BaseXx.getFromBase64UrlWithoutPadding(null);
    fail("EXPECTING_EXCEPTION_ILLEGAL_ARGUMENT_EXCEPTION");
  }

  @Test(expected = InvalidArgumentRuntimeException.class)
  public void testFromBase64Padding() throws IOException {
    BaseXx.getFromBase64UrlPadding(null);
    fail("EXPECTING_EXCEPTION_ILLEGAL_ARGUMENT_EXCEPTION");
  }

  @Test
  public void testBase64UrlPaddingOK() throws IOException {
    final String encoded =
        BaseXx.getBase64UrlWithPadding("FBTest64P".getBytes());
    assertNotNull(encoded);
    final byte[] bytes = BaseXx.getFromBase64UrlPadding(encoded);
    assertNotNull(bytes);
    assertArrayEquals(bytes, "FBTest64P".getBytes());
  }

  @Test
  public void testBase64PaddingOK() throws IOException {
    final String encoded = BaseXx.getBase64("FBTest64P".getBytes());
    assertNotNull(encoded);
    final byte[] bytes = BaseXx.getFromBase64(encoded);
    assertNotNull(bytes);
    assertArrayEquals(bytes, "FBTest64P".getBytes());
  }

  @Test
  public void testBase64UrlWithoutPaddingOK() throws IOException {
    final String encoded =
        BaseXx.getBase64UrlWithoutPadding("FBTest64".getBytes());
    assertNotNull(encoded);
    final byte[] bytes = BaseXx.getFromBase64UrlWithoutPadding(encoded);
    assertNotNull(bytes);
    assertArrayEquals(bytes, "FBTest64".getBytes());
  }

  @Test
  public void testBase32OK() throws IOException {
    final String encoded = BaseXx.getBase32("FBTest32".getBytes());
    assertNotNull(encoded);
    final byte[] bytes = BaseXx.getFromBase32(encoded);
    assertNotNull(bytes);
    assertArrayEquals(bytes, "FBTest32".getBytes());
  }

  @Test
  public void testBase16OK() throws IOException {
    final String encoded = BaseXx.getBase16("FBTest16".getBytes());
    assertNotNull(encoded);
    final byte[] bytes = BaseXx.getFromBase16(encoded);
    assertNotNull(bytes);
    assertArrayEquals(bytes, "FBTest16".getBytes());
  }

}