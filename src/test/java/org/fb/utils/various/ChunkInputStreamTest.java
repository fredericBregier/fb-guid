package org.fb.utils.various;

import org.fb.utils.various.DigestInputStream.DigestAlgo;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ChunkInputStreamTest {

  @Test
  public void test1ChunkInputStream() {
    final long len = 10 * 1024 * 1024 * 1024L;
    final long chunk = 10 * 1024 * 1024;
    long read = 0;
    final byte[] bytes = new byte[65536];
    try (FakeInputStream inputStream = new FakeInputStream(len, (byte) 'A')) {
      try (ChunkInputStream chunkInputStream = new ChunkInputStream(inputStream, 0, chunk)) {
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
    read = 0;
    try (FakeInputStream inputStream = new FakeInputStream(len, (byte) 'A')) {
      try (ChunkInputStream chunkInputStream = new ChunkInputStream(inputStream, len, chunk)) {
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
  public void test2ChunkInputStreamConsumeAll() {
    final long len = 10 * 1024 * 1024 * 1024L;
    final long chunk = 10 * 1024 * 1024;
    long read = 0;
    try (FakeInputStream inputStream = new FakeInputStream(len, (byte) 'A')) {
      try (ChunkInputStream chunkInputStream = new ChunkInputStream(inputStream, 0, chunk)) {
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
    read = 0;
    try (FakeInputStream inputStream = new FakeInputStream(len, (byte) 'A')) {
      try (ChunkInputStream chunkInputStream = new ChunkInputStream(inputStream, len, chunk)) {
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
  public void test3FakeInputStream() {
    final long len = 10 * 1024 * 1024 * 1024L;
    long read = 0;
    final byte[] bytes = new byte[65536];
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
  public void test5DigestInputStreamSha256() {
    test5DigestInputStreamDigest(DigestAlgo.SHA256);
    test5DigestInputStreamDigest(DigestAlgo.SHA3_256);
    test5DigestInputStreamDigest(DigestAlgo.SHA512);
  }

  public void test5DigestInputStreamDigest(DigestAlgo digestAlgo) {
    final long len = 1024 * 1024 * 1024L;
    long read = 0;
    final byte[] bytes = new byte[65536];
    String hash;
    try (FakeInputStream inputStream0 = new FakeInputStream(len, (byte) 'A');
         DigestInputStream inputStream = new DigestInputStream(inputStream0, digestAlgo)) {
      long subread;
      assertTrue(inputStream.available() > 0);
      long start = System.nanoTime();
      while ((subread = inputStream.read(bytes)) >= 0) {
        read += subread;
      }
      long stop = System.nanoTime();
      SysErrLogger.FAKE_LOGGER.sysout(digestAlgo.algoName + " Time: " + (stop - start) / 1000000);
      assertEquals(len, read);
      assertEquals(-1, inputStream.read());
      assertEquals(0, inputStream.available());
      assertFalse(inputStream.markSupported());
      hash = inputStream.getDigestBase64();
    } catch (IOException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    read = 0;
    try (FakeInputStream inputStream0 = new FakeInputStream(len, (byte) 'A');
         DigestInputStream inputStream = new DigestInputStream(inputStream0, digestAlgo)) {
      assertTrue(inputStream.available() > 0);
      long start = System.nanoTime();
      read = FakeInputStream.consumeAll(inputStream);
      long stop = System.nanoTime();
      SysErrLogger.FAKE_LOGGER.sysout(digestAlgo.algoName + " Time: " + (stop - start) / 1000000);
      assertEquals(len, read);
      assertEquals(-1, inputStream.read());
      assertEquals(0, inputStream.available());
      assertFalse(inputStream.markSupported());
      String hash2 = inputStream.getDigestBase64();
      assertEquals(hash, hash2);
    } catch (IOException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void test4FakeInputStreamSkip() {
    final long len = 10 * 1024 * 1024 * 1024L;
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
