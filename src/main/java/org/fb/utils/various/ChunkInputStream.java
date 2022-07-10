package org.fb.utils.various;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Transform one InputStream to Multiple InputStream, each one being a chunk of the primary one
 */
public class ChunkInputStream extends InputStream {
  private static final int BUF_SIZE = 65536;
  private final InputStream inputStream;
  private final long chunkSize;
  private long currentLen;
  private final long totalLen;
  private long currentTotalRead = 0;

  /**
   * @param inputStream the InputStream to split as multiple InputStream by chunk
   * @param chunkSize the chunk size to split on
   */
  public ChunkInputStream(final InputStream inputStream, final long len, final long chunkSize) {
    this.inputStream = inputStream;
    this.chunkSize = chunkSize;
    totalLen = len > 0? len : -1;
  }

  /**
   * @return True if the next Chunk of InputStream is ready, else implies closing of native InputStream
   *
   * @throws IOException if an issue occurs
   */
  public boolean nextChunk() throws IOException {
    currentLen = 0;
    if (totalLen > 0 && currentTotalRead >= totalLen) {
      inputStream.close();
      return false;
    }
    boolean cont = inputStream.available() > 0;
    // FIXME could be 0 but not ended if totalLen unknown: maybe use reading 1 byte and replaying it
    if (!cont) {
      inputStream.close();
    }
    return cont;
  }

  public long getChunkSize() {
    if (totalLen > 0) {
      return Math.min(chunkSize - currentLen, totalLen - currentTotalRead);
    }
    return -1;
  }

  @Override
  public int read() throws IOException {
    if (currentLen >= chunkSize) {
      return -1;
    }
    currentLen++;
    currentTotalRead++;
    return inputStream.read();
  }

  @Override
  public int available() throws IOException {
    if (currentLen >= chunkSize) {
      return 0;
    }
    int available = inputStream.available();
    var maxLen = chunkSize - currentLen;
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
    var realLen = len;
    var maxLen = chunkSize - currentLen;
    if (maxLen < len) {
      realLen = (int) maxLen;
    }
    int read = inputStream.read(b, off, realLen);
    currentLen += read;
    currentTotalRead += read;
    return read;
  }

  @Override
  public long skip(final long len) throws IOException {
    if (currentLen >= chunkSize) {
      return 0;
    }
    var realLen = len;
    var maxLen = chunkSize - currentLen;
    if (maxLen < len) {
      realLen = (int) maxLen;
    }
    long read = inputStream.skip(realLen);
    currentLen += read;
    currentTotalRead += read;
    return read;
  }

  @Override
  public long transferTo(final OutputStream out) throws IOException {
    long transferred = 0L;
    var read = 0;
    for (final byte[] buffer = new byte[BUF_SIZE]; (read = read(buffer, 0, BUF_SIZE)) >= 0;
         transferred += read) {
      out.write(buffer, 0, read);
    }
    return transferred;
  }
}
