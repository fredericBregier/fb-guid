package org.fb.utils.various;

import java.io.IOException;
import java.io.InputStream;

/**
 * Transform one InputStream to Multiple InputStream, each one being a chunk of the primary one
 */
public class ChunkInputStream extends InputStream {
  private final InputStream inputStream;
  private final long chunkSize;
  private long currentLen;

  /**
   * @param inputStream the InputStream to split as multiple InputStream by chunk
   * @param chunkSize the chunk size to split on
   */
  public ChunkInputStream(final InputStream inputStream, final long chunkSize) {
    this.inputStream = inputStream;
    this.chunkSize = chunkSize;
  }

  /**
   * @return True if the next Chunk of InputStream is ready, else implies closing of native InputStream
   *
   * @throws IOException if an issue occurs
   */
  public boolean nextChunk() throws IOException {
    currentLen = 0;
    boolean cont = inputStream.available() > 0;
    if (!cont) {
      inputStream.close();
    }
    return cont;
  }

  @Override
  public int read() throws IOException {
    if (currentLen >= chunkSize) {
      return -1;
    }
    currentLen++;
    return inputStream.read();
  }

  @Override
  public int available() throws IOException {
    if (currentLen >= chunkSize) {
      return 0;
    }
    int available = inputStream.available();
    long maxLen = chunkSize - currentLen;
    if (available > maxLen) {
      available = (int) maxLen;
    }
    return available;
  }

  @Override
  public void close() throws IOException {
    currentLen = chunkSize;
  }

  @Override
  public boolean markSupported() {
    return false;
  }

  @Override
  public int read(final byte[] b, final int off, final int len) throws IOException {
    if (currentLen >= chunkSize) {
      return -1;
    }
    int realLen = len;
    long maxLen = chunkSize - currentLen;
    if (maxLen < len) {
      realLen = (int) maxLen;
    }
    int read = inputStream.read(b, off, realLen);
    currentLen += read;
    return read;
  }

}
