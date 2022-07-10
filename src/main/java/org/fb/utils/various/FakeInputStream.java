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
import java.io.OutputStream;
import java.util.Arrays;

public class FakeInputStream extends InputStream {
  private static final int BUF_SIZE = 65536;
  private static final int MAX_AVAILABLE = 1024 * 1024 * 100;
  private static final String ARGS_SHALL_NOT_BE_NULL = "Args shall not be null";
  private final byte b;
  private long toSend;

  public FakeInputStream(final long len, final byte b) {
    toSend = len;
    this.b = b;
  }

  public static long consumeAll(final InputStream inputStream) throws IOException {
    long len = 0;
    var read = 0L;
    var bytes = new byte[BUF_SIZE];
    while ((read = inputStream.read(bytes, 0, BUF_SIZE)) >= 0) {
      len += read;
    }
    return len;
  }

  @Override
  public int read(final byte[] bytes) throws IOException {
    ParametersChecker.checkParameterNullOnly(ARGS_SHALL_NOT_BE_NULL, bytes);
    return read(bytes, 0, bytes.length);
  }

  @Override
  public int read(final byte[] bytes, final int off, final int len) throws IOException {
    ParametersChecker.checkParameterNullOnly(ARGS_SHALL_NOT_BE_NULL, bytes);
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
    return (int) Math.min(MAX_AVAILABLE, toSend);
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
    return b & 0xFF;
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

  @Override
  public long transferTo(final OutputStream out) throws IOException {
    ParametersChecker.checkParameterNullOnly(ARGS_SHALL_NOT_BE_NULL, out);
    final long readFinal = toSend;
    var bytes = new byte[BUF_SIZE];
    Arrays.fill(bytes, b);
    var read = (int) skip(BUF_SIZE);
    while (read > 0) {
      out.write(bytes, 0, read);
      read = (int) skip(BUF_SIZE);
    }
    return readFinal;
  }
}
