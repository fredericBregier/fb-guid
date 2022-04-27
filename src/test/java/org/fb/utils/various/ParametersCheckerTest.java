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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Parameters Checker Test
 */
public class ParametersCheckerTest {
  @Rule(order = Integer.MIN_VALUE)
  public TestWatcher watchman = new TestWatcherJunit4();


  @Test
  public final void testCheckParamaterStringStringArray() {
    try {
      ParametersChecker.checkParameter("test message", (String[]) null);
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameter("test message", null, "notnull");
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameter("test message", "notnull", null);
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameter("test message", "", "notnull");
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameter("test message", "notnull", "");
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameter("test message", "notnull", " ");
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameter("test message", "notNull", "notnull");
      ParametersChecker.checkParameter("test message", "notnull");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
  }

  @Test
  public final void testIsNotEmptyStringStringArray() {
    try {
      assertFalse(ParametersChecker.isNotEmpty((String[]) null));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertFalse(
          ParametersChecker.isNotEmpty("test message", null, "notnull"));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertFalse(
          ParametersChecker.isNotEmpty("test message", "notnull", null));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertFalse(ParametersChecker.isNotEmpty("test message", "", "notnull"));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertFalse(ParametersChecker.isNotEmpty("test message", "notnull", ""));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertFalse(ParametersChecker.isNotEmpty("test message", "notnull", " "));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      assertTrue(
          ParametersChecker.isNotEmpty("test message", "notNull", "notnull"));
      assertTrue(ParametersChecker.isNotEmpty("test message", "notnull"));
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
  }

  @Test
  public final void testCheckParamaterDefaultStringStringArray() {
    try {
      ParametersChecker.checkParameterDefault("test message", (String[]) null);
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameterDefault("test message", null, "notnull");
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameterDefault("test message", "notnull", null);
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameterDefault("test message", "", "notnull");
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameterDefault("test message", "notnull", "");
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameterDefault("test message", "notnull", " ");
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameterDefault("test message", "notNull",
                                              "notnull");
      ParametersChecker.checkParameterDefault("test message", "notnull");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
  }

  @Test
  public final void testCheckParamaterNullOnlyStringStringArray() {
    try {
      ParametersChecker.checkParameterNullOnly("test message", (String[]) null);
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameterNullOnly("test message", null, "notnull");
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameterNullOnly("test message", "notnull", null);
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
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
      ParametersChecker.checkParameterNullOnly("test message", "notNull",
                                               "notnull");
      ParametersChecker.checkParameterNullOnly("test message", "notnull");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
  }

  @Test
  public final void testCheckParamaterStringObjectArray() {
    try {
      ParametersChecker.checkParameter("test message", (Object[]) null);
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    final List<String> list = new ArrayList<String>();
    try {
      ParametersChecker.checkParameter("test message", null, list);
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameter("test message", list, null);
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
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
  public final void testCheckParamaterDefaultStringObjectArray() {
    try {
      ParametersChecker.checkParameterDefault("test message", (Object[]) null);
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    final List<String> list = new ArrayList<String>();
    try {
      ParametersChecker.checkParameterDefault("test message", null, list);
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameterDefault("test message", list, null);
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
    }
    try {
      ParametersChecker.checkParameterDefault("test message", list, list);
      ParametersChecker.checkParameterDefault("test message", list);
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      fail("SHOULD_NOT_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    }
  }

  @Test
  public final void testCheckValue() {
    try {
      ParametersChecker.checkValue("test", 1, 2);
      fail("SHOULD_RAIZED_ILLEGAL_ARGUMENT_EXCEPTION");
    } catch (final InvalidArgumentRuntimeException e) { // NOSONAR
      // ok
    }
    ParametersChecker.checkValue("test", 1, 1);
    ParametersChecker.checkValue("test", 1, 0);
  }

}
