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
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Parameters Checker Test
 */
public class ParametersCheckerTest {
  private final List tocheck = SingletonUtils.singletonList();

  @Test
  public final void testCheckParamaterObjectArray() {
    // String
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameter("test message", (String[]) null));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameter("test message", null, "notnull"));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameter("test message", "notnull", null));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameter("test message", "", "notnull"));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameter("test message", "notnull", ""));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameter("test message", "notnull", " "));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameter("test message", "notNull", "notnull");
      ParametersChecker.checkParameter("test message", "notnull");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
    // Object
    try {
      ParametersChecker.checkParameter("test message", tocheck, tocheck);
      ParametersChecker.checkParameter("test message", tocheck, "notnull");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameter("test message", null, " "));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameter("test message", (Object[]) null));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    final List<String> list = new ArrayList<String>();
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameter("test message", null, list));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameter("test message", list, null));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameter("test message", list, list);
      ParametersChecker.checkParameter("test message", list);
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
  }

  @Test
  public final void testIsNotEmptyObjectArray() {
    try {
      assertFalse(ParametersChecker.isNotEmpty((String[]) null));
      assertFalse(ParametersChecker.isNotEmpty("test message", null, "notnull"));
      assertFalse(ParametersChecker.isNotEmpty("test message", "notnull", null));
      assertFalse(ParametersChecker.isNotEmpty("test message", "", "notnull"));
      assertFalse(ParametersChecker.isNotEmpty("test message", "notnull", ""));
      assertFalse(ParametersChecker.isNotEmpty("test message", "notnull", " "));
      assertTrue(ParametersChecker.isNotEmpty("test message", "notNull", "notnull"));
      assertTrue(ParametersChecker.isNotEmpty("test message", "notnull"));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
    // Object
    try {
      assertFalse(ParametersChecker.isNotEmpty("test message", tocheck, " "));
      assertTrue(ParametersChecker.isNotEmpty("test message", tocheck, "notnull"));
      assertTrue(ParametersChecker.isNotEmpty("test message", tocheck));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
  }

  @Test
  public final void testIsEmptyObjectArray() {
    try {
      assertTrue(ParametersChecker.isEmpty((String[]) null));
      assertTrue(ParametersChecker.isEmpty("test message", null, "notnull"));
      assertTrue(ParametersChecker.isEmpty("test message", "notnull", null));
      assertTrue(ParametersChecker.isEmpty("test message", "", "notnull"));
      assertTrue(ParametersChecker.isEmpty("test message", "notnull", ""));
      assertTrue(ParametersChecker.isEmpty("test message", "notnull", " "));
      assertTrue(ParametersChecker.isEmpty("test message", tocheck, " "));
      assertFalse(ParametersChecker.isEmpty("test message", "notNull", "notnull"));
      assertFalse(ParametersChecker.isEmpty("test message", "notnull"));
      assertFalse(ParametersChecker.isEmpty("test message", tocheck));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
  }

  @Test
  public final void testCheckParamaterNullOnlyObjectArray() {
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameterNullOnly("test message", (String[]) null));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameterNullOnly("test message", null, "notnull"));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameterNullOnly("test message", "notnull", null));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameterNullOnly("test message", "", "notnull");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
    try {
      ParametersChecker.checkParameterNullOnly("test message", "notnull", "");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
    try {
      ParametersChecker.checkParameterNullOnly("test message", "notNull", "notnull");
      ParametersChecker.checkParameterNullOnly("test message", "notnull");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkParameterNullOnly("test message", tocheck, null));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameterNullOnly("test message", tocheck, "notnull");
      ParametersChecker.checkParameterNullOnly("test message", tocheck);
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
  }

  @Test
  public final void testCheckValue() {
    try {
      assertThrows(InvalidArgumentRuntimeException.class, () -> ParametersChecker.checkValue("test", 1, 2));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      // ok
    }
    ParametersChecker.checkValue("test", 1, 1);
    ParametersChecker.checkValue("test", 1, 0);
  }

  @Test
  public final void testSanity() {
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkSanityString("test\b"));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      // ok
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkSanityString("test\b", "test"));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      // ok
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkSanityString("test", "test\b"));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      // ok
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkSanityString("test" + "<![CDATA[", "test"));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      // ok
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkSanityString("<![CDATA[test", "test"));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      // ok
    }
    try {
      assertThrows(InvalidArgumentRuntimeException.class,
                   () -> ParametersChecker.checkSanityString("test" + "<![CDATA[test", "test"));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      // ok
    }

    ParametersChecker.checkSanityString("test");
    ParametersChecker.checkSanityString("test", "test", "", null);
  }

  @Test
  public final void testHasNotEmpty() {
    try {
      assertTrue(ParametersChecker.hasNotEmpty("test\n"));
      assertTrue(ParametersChecker.hasNotEmpty(tocheck, "test\n"));
      assertTrue(ParametersChecker.hasNotEmpty(tocheck, ""));
      assertTrue(ParametersChecker.hasNotEmpty("test\n", null));
      assertFalse(ParametersChecker.hasNotEmpty("", " "));
      assertFalse(ParametersChecker.hasNotEmpty(null, " "));
      assertFalse(ParametersChecker.hasNotEmpty(null));
      assertFalse(ParametersChecker.hasNotEmpty("", ""));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      // ok
    }
  }
}
