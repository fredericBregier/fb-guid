package org.fb.utils.various;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import java.io.IOException;

import static org.junit.Assert.*;

public class ChunkInputStreamTest {
  @Rule(order = Integer.MIN_VALUE)
  public TestWatcher watchman = new TestWatcherJunit4();

  @Test
  public void testChunkInputStream() {
    long len = 10 * 1024 * 1024 * 1024L;
    long chunk = 10 * 1024 * 1024;
    long read = 0;
    byte[] bytes = new byte[65536];
    try (FakeInputStream inputStream = new FakeInputStream(len, (byte) 'A')) {
      try (ChunkInputStream chunkInputStream = new ChunkInputStream(inputStream, chunk)) {
        while (chunkInputStream.nextChunk()) {
          long subread;
          long chunkRead = 0;
          assertTrue(chunkInputStream.available() > 0);
          assertEquals('A', chunkInputStream.read());
          chunkRead++;
          while ((subread = chunkInputStream.read(bytes)) >= 0) {
            chunkRead += subread;
          }
          assertEquals(chunk, chunkRead);
          assertEquals(-1, chunkInputStream.read());
          assertEquals(0, chunkInputStream.available());
          assertFalse(chunkInputStream.markSupported());
          read += chunkRead;
        }
      }
      assertEquals(len, read);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testChunkInputStreamConsumeAll() {
    long len = 10 * 1024 * 1024 * 1024L;
    long chunk = 10 * 1024 * 1024;
    long read = 0;
    try (FakeInputStream inputStream = new FakeInputStream(len, (byte) 'A')) {
      try (ChunkInputStream chunkInputStream = new ChunkInputStream(inputStream, chunk)) {
        while (chunkInputStream.nextChunk()) {
          long chunkRead = FakeInputStream.consumeAll(chunkInputStream);
          assertEquals(chunk, chunkRead);
          read += chunkRead;
        }
      }
      assertEquals(len, read);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testFakeInputStream() {
    long len = 10 * 1024 * 1024 * 1024L;
    long read = 0;
    byte[] bytes = new byte[65536];
    try (FakeInputStream inputStream = new FakeInputStream(len, (byte) 'A')) {
      long subread;
      assertTrue(inputStream.available() > 0);
      assertEquals('A', inputStream.read());
      read++;
      while ((subread = inputStream.read(bytes)) >= 0) {
        read += subread;
      }
      assertEquals(len, read);
      assertEquals(-1, inputStream.read());
      assertEquals(0, inputStream.available());
      assertFalse(inputStream.markSupported());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testFakeInputStreamSkip() {
    long len = 10 * 1024 * 1024 * 1024L;
    long read = 0;
    try (FakeInputStream inputStream = new FakeInputStream(len, (byte) 'X')) {
      long subread;
      assertTrue(inputStream.available() > 0);
      assertEquals('X', inputStream.read());
      read++;
      while ((subread = inputStream.skip(65536)) > 0) {
        read += subread;
      }
      assertEquals(len, read);
      assertEquals(-1, inputStream.read());
      assertEquals(0, inputStream.available());
      assertFalse(inputStream.markSupported());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
