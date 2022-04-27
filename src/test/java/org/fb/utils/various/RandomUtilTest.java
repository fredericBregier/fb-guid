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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class RandomUtilTest {
  private static final byte[] BYTES_0_LENGTH = {};
  @Rule(order = Integer.MIN_VALUE)
  public TestWatcher watchman = new TestWatcherJunit4();

  @Test
  public void testRandom() {
    final byte[] byteArray0 = RandomUtil.getRandom(90);
    assertNotNull(byteArray0);
    final byte[] byteArray1 = RandomUtil.getRandom(90);
    assertFalse(Arrays.equals(byteArray0, byteArray1));
    final byte[] byteArray3 = RandomUtil.getRandom(0);
    assertArrayEquals(BYTES_0_LENGTH, byteArray3);
    final byte[] byteArray4 = RandomUtil.getRandom(-10);
    assertArrayEquals(BYTES_0_LENGTH, byteArray4);
  }

  @Test
  public void testSingletons() throws IOException {
    final byte[] bytes = SingletonUtils.getSingletonByteArray();
    assertEquals(0, bytes.length);

    final List<RandomUtilTest> emptyList = SingletonUtils.singletonList();
    assertTrue(emptyList.isEmpty());
    assertEquals(0, emptyList.size());
    try {
      emptyList.add(this);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final UnsupportedOperationException e) {// NOSONAR
      // ignore
    }
    assertTrue(emptyList.isEmpty());
    assertEquals(0, emptyList.size());
    try {
      emptyList.remove(0);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final UnsupportedOperationException e) {// NOSONAR
      // ignore
    }
    assertTrue(emptyList.isEmpty());
    assertEquals(0, emptyList.size());

    final Set<RandomUtilTest> emptySet = SingletonUtils.singletonSet();
    assertTrue(emptySet.isEmpty());
    assertEquals(0, emptySet.size());
    try {
      emptySet.add(this);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final UnsupportedOperationException e) {// NOSONAR
      // ignore
    }
    assertTrue(emptySet.isEmpty());
    assertEquals(0, emptySet.size());
    emptySet.remove(this);
    assertTrue(emptySet.isEmpty());
    assertEquals(0, emptySet.size());

    final Map<RandomUtilTest, RandomUtilTest> emptyMap =
        SingletonUtils.singletonMap();
    assertTrue(emptyMap.isEmpty());
    assertEquals(0, emptyMap.size());
    try {
      emptyMap.put(this, this);
      fail("SHOULD_HAVE_AN_EXCEPTION");
    } catch (final UnsupportedOperationException e) {// NOSONAR
      // ignore
    }
    assertTrue(emptyMap.isEmpty());
    assertEquals(0, emptyMap.size());
    emptyMap.remove(this);
    assertTrue(emptyMap.isEmpty());
    assertEquals(0, emptyMap.size());
  }

  @Test
  public void testSingletonStreams() throws IOException {
    final InputStream emptyIS = SingletonUtils.singletonInputStream();
    final byte[] buffer = new byte[10];
    assertEquals(0, emptyIS.available());
    assertEquals(0, emptyIS.skip(10));
    assertEquals(-1, emptyIS.read());
    assertEquals(-1, emptyIS.read(buffer));
    assertEquals(-1, emptyIS.read(buffer, 0, buffer.length));
    assertTrue(emptyIS.markSupported());
    emptyIS.mark(5);
    emptyIS.reset();
    emptyIS.close();

    // No error
    final OutputStream voidOS = SingletonUtils.singletonOutputStream();
    voidOS.write(buffer);
    voidOS.write(1);
    voidOS.write(buffer, 0, buffer.length);
    voidOS.flush();
    voidOS.close();
  }
}
