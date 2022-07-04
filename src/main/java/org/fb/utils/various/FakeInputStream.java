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

package org.fb.utils.various;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class FakeInputStream extends InputStream {
  private final byte b;
  private long toSend;

  public FakeInputStream(final long len, final byte b) {
    toSend = len;
    this.b = b;
  }

  public static long consumeAll(final InputStream inputStream) throws IOException {
    long len = 0;
    long read;
    final byte[] bytes = new byte[65536];
    while ((read = inputStream.read(bytes, 0, 65536)) >= 0) {
      len += read;
    }
    return len;
  }

  @Override
  public int read(final byte[] bytes) throws IOException {
    ParametersChecker.checkParameterNullOnly("Args shall not be null", bytes);
    return read(bytes, 0, bytes.length);
  }

  @Override
  public int read(final byte[] bytes, final int off, final int len) throws IOException {
    ParametersChecker.checkParameterNullOnly("Args shall not be null", bytes);
    if (toSend <= 0) {
      return -1;
    }
    final int read = (int) Math.min(len, toSend);
    Arrays.fill(bytes, off, off + read, b);
    toSend -= read;
    return read;
  }

  @Override
  public int available() throws IOException {
    return toSend >= Integer.MAX_VALUE? Integer.MAX_VALUE : (int) toSend;
  }

  @Override
  public void close() throws IOException {
    toSend = -1;
  }

  @Override
  public int read() throws IOException {
    if (toSend <= 0) {
      return -1;
    }
    toSend--;
    return b;
  }

  @Override
  public boolean markSupported() {
    return false;
  }

  @Override
  public long skip(final long n) throws IOException {
    final long read = Math.min(toSend, n);
    toSend -= read;
    return read;
  }
}
