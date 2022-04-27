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

import com.google.common.base.Strings;
import org.fb.utils.exceptions.InvalidArgumentRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Checker for Parameters <br>
 * <br>
 * Can be used for String (testing also emptiness) and for general Object.<br>
 * For null String only, use the special method.
 */
public final class ParametersChecker {

  // Default ASCII for Param check
  private static final Pattern UNPRINTABLE_PATTERN =
      Pattern.compile("[\\p{Cntrl}&&[^\r\n\t]]");
  private static final List<String> RULES = new ArrayList<>();

  // default parameters for XML check
  private static final String CDATA_TAG_UNESCAPED = "<![CDATA[";
  private static final String CDATA_TAG_ESCAPED = "&lt;![CDATA[";
  private static final String ENTITY_TAG_UNESCAPED = "<!ENTITY";
  private static final String ENTITY_TAG_ESCAPED = "&lt;!ENTITY";
  // default parameters for Javascript check
  private static final String SCRIPT_TAG_UNESCAPED = "<script>";
  private static final String SCRIPT_TAG_ESCAPED = "&lt;script&gt;";
  private static final String MANDATORY_PARAMETER = " is mandatory parameter";

  static {
    RULES.add(CDATA_TAG_UNESCAPED);
    RULES.add(CDATA_TAG_ESCAPED);
    RULES.add(ENTITY_TAG_UNESCAPED);
    RULES.add(ENTITY_TAG_ESCAPED);
    RULES.add(SCRIPT_TAG_UNESCAPED);
    RULES.add(SCRIPT_TAG_ESCAPED);
  }

  private ParametersChecker() {
    // empty
  }

  /**
   * Check if any parameter are null or empty and if so, throw an
   * IllegalArgumentException
   *
   * @param errorMessage the error message
   * @param parameters parameters to be checked
   *
   * @throws InvalidArgumentRuntimeException if null or empty
   */
  public static void checkParameter(final String errorMessage,
                                    final String... parameters)
      throws InvalidArgumentRuntimeException {
    if (parameters == null) {
      throw new InvalidArgumentRuntimeException(errorMessage);
    }
    for (final String parameter : parameters) {
      if (Strings.isNullOrEmpty(parameter) || parameter.trim().isEmpty()) {
        throw new InvalidArgumentRuntimeException(errorMessage);
      }
    }
  }

  /**
   * Check if any parameter are null or empty and if so, throw an
   * IllegalArgumentException
   *
   * @param errorMessage the error message
   * @param parameters set of parameters
   *
   * @throws InvalidArgumentRuntimeException if null or empty
   */
  public static void checkParameterDefault(final String errorMessage,
                                           final String... parameters)
      throws InvalidArgumentRuntimeException {
    if (parameters == null) {
      throw new InvalidArgumentRuntimeException(
          errorMessage + MANDATORY_PARAMETER);
    }
    for (final String parameter : parameters) {
      if (Strings.isNullOrEmpty(parameter) || parameter.trim().isEmpty()) {
        throw new InvalidArgumentRuntimeException(
            errorMessage + MANDATORY_PARAMETER);
      }
    }
  }

  /**
   * Check if any parameter are null or empty and if so, return false
   *
   * @param parameters set of parameters
   *
   * @return True if not null and not empty neither containing only spaces
   */
  public static boolean isNotEmpty(final String... parameters) {
    if (parameters == null) {
      return false;
    }
    for (final String parameter : parameters) {
      if (Strings.isNullOrEmpty(parameter) || parameter.trim().isEmpty()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check if any parameter are null or empty and if so, throw an
   * IllegalArgumentException
   *
   * @param errorMessage the error message
   * @param parameters set of parameters
   *
   * @throws InvalidArgumentRuntimeException if null or empty
   */
  public static void checkParameterDefault(final String errorMessage,
                                           final Object... parameters)
      throws InvalidArgumentRuntimeException {
    if (parameters == null) {
      throw new InvalidArgumentRuntimeException(
          errorMessage + MANDATORY_PARAMETER);
    }
    for (final Object parameter : parameters) {
      if (parameter == null) {
        throw new InvalidArgumentRuntimeException(
            errorMessage + MANDATORY_PARAMETER);
      }
    }
  }

  /**
   * Check if any parameter are null and if so, throw an
   * IllegalArgumentException
   *
   * @param errorMessage the error message
   * @param parameters parameters to be checked
   *
   * @throws InvalidArgumentRuntimeException if null
   */
  public static void checkParameterNullOnly(final String errorMessage,
                                            final String... parameters)
      throws InvalidArgumentRuntimeException {
    if (parameters == null) {
      throw new InvalidArgumentRuntimeException(errorMessage);
    }
    for (final String parameter : parameters) {
      if (parameter == null) {
        throw new InvalidArgumentRuntimeException(errorMessage);
      }
    }
  }

  /**
   * Check if any parameter are null and if so, throw an
   * IllegalArgumentException
   *
   * @param errorMessage set of parameters
   * @param parameters set parameters to be checked
   *
   * @throws InvalidArgumentRuntimeException if null
   */
  public static void checkParameter(final String errorMessage,
                                    final Object... parameters)
      throws InvalidArgumentRuntimeException {
    if (parameters == null) {
      throw new InvalidArgumentRuntimeException(errorMessage);
    }
    for (final Object parameter : parameters) {
      if (parameter == null) {
        throw new InvalidArgumentRuntimeException(errorMessage);
      }
    }
  }

  /**
   * Check if an integer parameter is greater or equals to minValue
   *
   * @param name name of the variable
   * @param variable the value of variable to check
   * @param minValue the min value
   *
   * @throws InvalidArgumentRuntimeException if invalid
   */
  public static void checkValue(final String name, final long variable,
                                final long minValue)
      throws InvalidArgumentRuntimeException {
    if (variable < minValue) {
      throw new InvalidArgumentRuntimeException(
          "Parameter " + name + " is less than " + minValue);
    }
  }

  /**
   * Check external argument to avoid Path Traversal attack
   *
   * @param value to check
   *
   * @throws InvalidArgumentRuntimeException if invalid
   */
  public static String checkSanityString(final String value)
      throws InvalidArgumentRuntimeException {
    checkSanityString(new String[] { value });
    return value;
  }

  /**
   * Check external argument (null is consider as correct)
   *
   * @param strings
   *
   * @throws InvalidArgumentRuntimeException if invalid
   */
  public static void checkSanityString(final String... strings)
      throws InvalidArgumentRuntimeException {
    for (final String field : strings) {
      if (isEmpty(field)) {
        continue;
      }
      if (UNPRINTABLE_PATTERN.matcher(field).find()) {
        throw new InvalidArgumentRuntimeException("Invalid input bytes");
      }
      for (final String rule : RULES) {
        if (rule != null && field.contains(rule)) {
          throw new InvalidArgumentRuntimeException("Invalid tag sanity check");
        }
      }
    }
  }

  /**
   * Check if any parameter are null or empty and if so, return true
   *
   * @param parameters set of parameters
   *
   * @return True if any is null or empty or containing only spaces
   */
  public static boolean isEmpty(final String... parameters) {
    if (parameters == null) {
      return true;
    }
    for (final String parameter : parameters) {
      if (Strings.isNullOrEmpty(parameter) || parameter.trim().isEmpty()) {
        return true;
      }
    }
    return false;
  }
}