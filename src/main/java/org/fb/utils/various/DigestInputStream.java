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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Allow to compute Digest while reading InputStream. Note that performance are not that good (370 Mo/s
 * using SHA-256, 512 Mo/s using SHA-512).
 */
public class DigestInputStream extends InputStream {
  public enum DigestAlgo {
    MD5("MD5", 16), MD2("MD2", 16), SHA1("SHA-1", 20), SHA256("SHA-256", 32), SHA384("SHA-384", 48),
    SHA512("SHA-512", 64), SHA3_256("SHA3-256", 64);

    public final String algoName;
    public final int byteSize;

    /**
     * @return the length in bytes of one Digest
     */
    public final int getByteSize() {
      return byteSize;
    }

    /**
     * @return the length in Hex form of one Digest
     */
    public final int getHexSize() {
      return byteSize * 2;
    }

    DigestAlgo(final String algoName, final int byteSize) {
      this.algoName = algoName;
      this.byteSize = byteSize;
    }

    public static DigestAlgo getFromName(final String name) {
      try {
        return valueOf(name);
      } catch (final IllegalArgumentException ignore) {//NOSONAR
        // ignore
      }
      if ("MD5" .equalsIgnoreCase(name)) {
        return MD5;
      } else if ("MD2" .equalsIgnoreCase(name)) {
        return MD2;
      } else if ("SHA-1" .equalsIgnoreCase(name)) {
        return SHA1;
      } else if ("SHA-256" .equalsIgnoreCase(name)) {
        return SHA256;
      } else if ("SHA-384" .equalsIgnoreCase(name)) {
        return SHA384;
      } else if ("SHA-512" .equalsIgnoreCase(name)) {
        return SHA512;
      } else {
        throw new IllegalArgumentException("Digest Algo not found");
      }
    }
  }

  private static final int BUF_SIZE = 65536;
  private final InputStream inputStream;
  private final MessageDigest digest;
  private byte[] digestValue = null;

  public DigestInputStream(final InputStream inputStream, final String algo) throws NoSuchAlgorithmException {
    this.inputStream = inputStream;
    try {
      digest = MessageDigest.getInstance(algo);
    } catch (final NoSuchAlgorithmException e) {
      throw new NoSuchAlgorithmException(algo + " : algorithm not supported by this JVM", e);
    }
  }

  public DigestInputStream(final InputStream inputStream, final DigestAlgo algo)
      throws NoSuchAlgorithmException {
    this.inputStream = inputStream;
    try {
      digest = MessageDigest.getInstance(algo.algoName);
    } catch (final NoSuchAlgorithmException e) {
      throw new NoSuchAlgorithmException(algo + " : algorithm not supported by this JVM", e);
    }
  }

  @Override
  public int read() throws IOException {
    final int c = inputStream.read();
    if (c >= 0) {
      digest.update((byte) c);
    }
    return c;
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    var read = inputStream.read(b, off, len);
    if (read > 0) {
      digest.update(b, off, read);
    }
    return read;
  }

  @Override
  public long skip(final long n) throws IOException {
    byte[] bytes = new byte[BUF_SIZE];
    var still = n;
    var total = 0L;
    var max = (int) Math.min(BUF_SIZE, still);
    var read = 0;
    while ((read = read(bytes, 0, max)) >= 0) {
      still -= read;
      total += read;
      max = (int) Math.min(BUF_SIZE, still);
      if (max == 0) {
        return total;
      }
    }
    return total;
  }

  @Override
  public boolean markSupported() {
    return false;
  }

  @Override
  public int available() throws IOException {
    return inputStream.available();
  }

  @Override
  public void close() throws IOException {
    inputStream.close();
  }

  public byte[] getDigestValue() {
    if (digestValue == null) {
      digestValue = digest.digest();
    }
    return digestValue;
  }

  public String getDigestBase64() {
    if (digestValue == null) {
      digestValue = digest.digest();
    }
    return BaseXx.getBase64Padding(digestValue);
  }
}
