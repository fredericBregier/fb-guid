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

import java.io.PrintStream;
import java.util.Properties;
import java.util.regex.Pattern;

import static org.fb.utils.various.SystemRandomSecure.*;

/**
 * A collection of utility methods to retrieve and parse the values of the Java
 * system properties.
 */
public final class SystemPropertyUtil {
  // Since logger could be not available yet, one must not declare there a Logger

  public static final String MACHINE_ID = "org.fb.utils.machineId";
  private static final String USING_THE_DEFAULT_VALUE2 = "using the default value: ";
  private static final String FIND_0_9 = "-?\\d+";
  private static final String USING_THE_DEFAULT_VALUE = USING_THE_DEFAULT_VALUE2;
  private static final Properties PROPS = new Properties();
  private static final String INVALID_PROPERTY = "Invalid property ";
  private static final Pattern COMPILE_0_9 = Pattern.compile(FIND_0_9);

  // Retrieve all system properties at once so that there's no need to deal with
  // security exceptions from next time. Otherwise, we might end up with logging every
  // security exceptions on every system property access or introducing more complexity
  // just because of less verbose logging.
  static {
    refresh();
    initializedRandomContext();
  }

  private SystemPropertyUtil() {
    // Unused
  }

  /**
   * Returns the value of the Java system property with the specified {@code
   * key}, while falling back to the
   * specified default value if the property access fails.
   *
   * @return the property value. {@code def} if there's no such property or if
   *     an access to the specified
   *     property is not allowed.
   */
  public static boolean getBoolean(final String key, final boolean def) {
    ParametersChecker.checkParameter("Key", key);

    String value = PROPS.getProperty(key);
    if (value == null) {
      return def;
    }

    value = value.trim().toLowerCase();
    if (value.isEmpty()) {
      return true;
    }

    if ("true".equals(value) || "yes".equals(value) || "1".equals(value)) {
      return true;
    }

    if ("false".equals(value) || "no".equals(value) || "0".equals(value)) {
      return false;
    }

    SysErrLogger.FAKE_LOGGER.syserr(
        "Unable to parse the boolean system property '" + key + "':" + value + " - " +
        USING_THE_DEFAULT_VALUE2 + def);

    return def;
  }

  /**
   * Returns the value of the Java system property with the specified {@code
   * key}, while falling back to the
   * specified default value if the property access fails.
   *
   * @return the property value. {@code def} if there's no such property or if
   *     an access to the specified
   *     property is not allowed.
   */
  public static int getInt(final String key, final int def) {
    ParametersChecker.checkParameter("Key", key);

    String value = PROPS.getProperty(key);
    if (value == null) {
      return def;
    }

    value = value.trim().toLowerCase();
    if (COMPILE_0_9.matcher(value).matches()) {
      try {
        return Integer.parseInt(value);
      } catch (final Exception e) {
        // Ignore
      }
    }

    SysErrLogger.FAKE_LOGGER.syserr(
        "Unable to parse the integer system property '" + key + "':" + value + " - " +
        USING_THE_DEFAULT_VALUE2 + def);

    return def;
  }  private static final Platform mOs = getOS();

  /**
   * Returns the value of the Java system property with the specified {@code
   * key}, while falling back to the
   * specified default value if the property access fails.
   *
   * @return the property value. {@code def} if there's no such property or if
   *     an access to the specified
   *     property is not allowed.
   */
  public static long getLong(final String key, final long def) {
    ParametersChecker.checkParameter("Key", key);

    String value = PROPS.getProperty(key);
    if (value == null) {
      return def;
    }

    value = value.trim().toLowerCase();
    if (COMPILE_0_9.matcher(value).matches()) {
      try {
        return Long.parseLong(value);
      } catch (final Exception e) {
        // Ignore
      }
    }

    SysErrLogger.FAKE_LOGGER.syserr(
        "Unable to parse the long integer system property '" + key + "':" + value + " - " +
        USING_THE_DEFAULT_VALUE2 + def);

    return def;
  }

  /**
   * Returns the value of the Java system property with the specified {@code
   * key}, while falling back to the specified
   * default value if the property access fails.
   *
   * @param key of system property
   * @param def the default value
   *
   * @return the property value. {@code def} if there's no such property or if
   *     an access to the specified property is
   *     not allowed.
   *
   * @throws IllegalArgumentException key or def null
   */
  public static String getAndSet(final String key, final String def) {
    ParametersChecker.checkParameter("Key", key);
    if (def == null) {
      throw new IllegalArgumentException("Def cannot be null");
    }
    if (!PROPS.containsKey(key)) {
      System.setProperty(key, def);
      refresh();
      return def;
    }
    return PROPS.getProperty(key);
  }

  /**
   * Re-retrieves all system properties so that any post-launch properties
   * updates are retrieved.
   */
  public static void refresh() {
    Properties newProps;
    try {
      newProps = System.getProperties();
    } catch (final SecurityException e) {
      SysErrLogger.FAKE_LOGGER.ignoreLog(e);
      SysErrLogger.FAKE_LOGGER.syserr(
          "Unable to retrieve the system properties; default values will be used: " + e.getMessage());
      newProps = new Properties();
    }

    synchronized (PROPS) {
      PROPS.clear();
      PROPS.putAll(newProps);
    }
    // Get the MACHINE_ID if specified
    if (!contains(MACHINE_ID) || ParametersChecker.isEmpty(get(MACHINE_ID))) {
      try {
        System.clearProperty(MACHINE_ID);
        synchronized (PROPS) {
          PROPS.clear();
          PROPS.putAll(newProps);
        }
      } catch (final Throwable ignored) {//NOSONAR
        SysErrLogger.FAKE_LOGGER.ignoreLog(ignored);
      }
    }
  }

  /**
   * Returns {@code true} if and only if the system property with the
   * specified
   * {@code key} exists.
   */
  public static boolean contains(final String key) {
    ParametersChecker.checkParameter("Key", key);
    return PROPS.containsKey(key);
  }

  /**
   * Returns the value of the Java system property with the specified {@code
   * key}, while falling back to
   * {@code null} if the property access fails.
   *
   * @return the property value or {@code null}
   */
  public static String get(final String key) {
    return get(key, null);
  }

  /**
   * Returns the value of the Java system property with the specified {@code
   * key}, while falling back to the specified
   * default value if the property access fails.
   *
   * @param key of system property
   * @param def the default value
   *
   * @return the property value. {@code def} if there's no such property or if
   *     an access to the specified property is
   *     not allowed.
   *
   * @throws IllegalArgumentException key null
   */
  public static String get(final String key, final String def) {
    ParametersChecker.checkParameter("Key", key);
    String value = PROPS.getProperty(key);
    if (value == null) {
      return def;
    }

    try {
      value = ParametersChecker.checkSanityString(value);
    } catch (final InvalidArgumentRuntimeException e) {
      SysErrLogger.FAKE_LOGGER.syserr(INVALID_PROPERTY + key, e);
      return def;
    }

    return value;
  }

  /**
   * @return the MachineId if specified or null defined in environment variable
   *     org.fb.utils.machineId using 6 bytes in Hexadecimal format
   */
  public static String getMachineId() {
    return get(MACHINE_ID);
  }

  /**
   * Returns the value of the Java system property with the specified {@code
   * key}, while falling back to the specified
   * default value if the property access fails.
   *
   * @param key of system property
   * @param def the default value
   *
   * @return the property value. {@code def} if there's no such property or if
   *     an access to the specified property is
   *     not allowed.
   *
   * @throws IllegalArgumentException key null
   */
  public static boolean getAndSet(final String key, final boolean def) {
    ParametersChecker.checkParameter("Key", key);
    if (!PROPS.containsKey(key)) {
      System.setProperty(key, Boolean.toString(def));
      refresh();
      return def;
    }
    return get(key, def);
  }

  /**
   * Returns the value of the Java system property with the specified {@code
   * key}, while falling back to the specified
   * default value if the property access fails.
   *
   * @param key of system property
   * @param def the default value
   *
   * @return the property value. {@code def} if there's no such property or if
   *     an access to the specified property is
   *     not allowed.
   *
   * @throws IllegalArgumentException key null
   */
  public static boolean get(final String key, final boolean def) {
    ParametersChecker.checkParameter("Key", key);
    String value = PROPS.getProperty(key);
    if (value == null) {
      return def;
    }

    value = value.trim().toLowerCase();
    if (value.isEmpty()) {
      return true;
    }

    if ("true".equals(value) || "yes".equals(value) || "1".equals(value)) {
      return true;
    }

    if ("false".equals(value) || "no".equals(value) || "0".equals(value)) {
      return false;
    }
    try {
      ParametersChecker.checkSanityString(value);
    } catch (final InvalidArgumentRuntimeException e) {
      SysErrLogger.FAKE_LOGGER.syserr(INVALID_PROPERTY + key, e);
      return def;
    }
    SysErrLogger.FAKE_LOGGER.syserr(
        "Unable to parse the boolean system property '" + key + "':" + value + " - " +
        USING_THE_DEFAULT_VALUE + def);

    return def;
  }

  /**
   * Returns the value of the Java system property with the specified {@code
   * key}, while falling back to the specified
   * default value if the property access fails.
   *
   * @param key of system property
   * @param def the default value
   *
   * @return the property value. {@code def} if there's no such property or if
   *     an access to the specified property is
   *     not allowed.
   *
   * @throws IllegalArgumentException key null
   */
  public static int getAndSet(final String key, final int def) {
    ParametersChecker.checkParameter("Key", key);
    if (!PROPS.containsKey(key)) {
      System.setProperty(key, Integer.toString(def));
      refresh();
      return def;
    }
    return get(key, def);
  }

  /**
   * Returns the value of the Java system property with the specified {@code
   * key}, while falling back to the specified
   * default value if the property access fails.
   *
   * @param key the system property
   * @param def the default value
   *
   * @return the property value. {@code def} if there's no such property or if
   *     an access to the specified property is
   *     not allowed.
   *
   * @throws IllegalArgumentException key null
   */
  public static int get(final String key, final int def) {
    ParametersChecker.checkParameter("Key", key);
    String value = PROPS.getProperty(key);
    if (value == null) {
      return def;
    }

    value = value.trim().toLowerCase();
    if (COMPILE_0_9.matcher(value).matches()) {
      try {
        return Integer.parseInt(value);
      } catch (final Exception ignored) {
        // Since logger could be not available yet
        // Ignore
        SysErrLogger.FAKE_LOGGER.ignoreLog(ignored);
      }
    }
    try {
      ParametersChecker.checkSanityString(value);
    } catch (final InvalidArgumentRuntimeException e) {
      SysErrLogger.FAKE_LOGGER.syserr(INVALID_PROPERTY + key, e);
      return def;
    }
    SysErrLogger.FAKE_LOGGER.syserr(
        "Unable to parse the integer system property '" + key + "':" + value + " - " +
        USING_THE_DEFAULT_VALUE + def);

    return def;
  }

  /**
   * Returns the value of the Java system property with the specified {@code
   * key}, while falling back to the specified
   * default value if the property access fails.
   *
   * @param key of system property
   * @param def the default value
   *
   * @return the property value. {@code def} if there's no such property or if
   *     an access to the specified property is
   *     not allowed.
   *
   * @throws IllegalArgumentException key null
   */
  public static long getAndSet(final String key, final long def) {
    ParametersChecker.checkParameter("Key", key);
    if (!PROPS.containsKey(key)) {
      System.setProperty(key, Long.toString(def));
      refresh();
      return def;
    }
    return get(key, def);
  }

  /**
   * Returns the value of the Java system property with the specified {@code
   * key}, while falling back to the specified
   * default value if the property access fails.
   *
   * @param key of system property
   * @param def the default value
   *
   * @return the property value. {@code def} if there's no such property or if
   *     an access to the specified property is
   *     not allowed.
   *
   * @throws IllegalArgumentException key null
   */
  public static long get(final String key, final long def) {
    ParametersChecker.checkParameter("Key", key);
    String value = PROPS.getProperty(key);
    if (value == null) {
      return def;
    }

    value = value.trim().toLowerCase();
    if (COMPILE_0_9.matcher(value).matches()) {
      try {
        return Long.parseLong(value);
      } catch (final Exception ignored) {
        // Since logger could be not available yet
        // Ignore
        SysErrLogger.FAKE_LOGGER.ignoreLog(ignored);
      }
    }
    try {
      ParametersChecker.checkSanityString(value);
    } catch (final InvalidArgumentRuntimeException e) {
      SysErrLogger.FAKE_LOGGER.syserr(INVALID_PROPERTY + key, e);
      return def;
    }

    SysErrLogger.FAKE_LOGGER.syserr(
        "Unable to parse the long integer system property '" + key + "':" + value + " - " +
        USING_THE_DEFAULT_VALUE + def);

    return def;
  }

  /**
   * Set the value of the Java system property with the specified {@code key}
   * to the specified default value.
   *
   * @param key of system property
   * @param def the default value
   *
   * @return the ancient value.
   *
   * @throws IllegalArgumentException key or def null
   */
  public static String set(final String key, final String def) {
    ParametersChecker.checkParameter("Key", key);
    if (def == null) {
      throw new IllegalArgumentException("Def cannot be null");
    }
    String old = null;
    if (PROPS.containsKey(key)) {
      old = PROPS.getProperty(key);
    }
    System.setProperty(key, def);
    refresh();
    return old;
  }

  /**
   * Set the value of the Java system property with the specified {@code key}
   * to the specified default value.
   *
   * @param key of system property
   * @param def the default value
   *
   * @return the ancient value.
   *
   * @throws IllegalArgumentException key null
   */
  public static boolean set(final String key, final boolean def) {
    ParametersChecker.checkParameter("Key", key);
    boolean old = false;
    if (PROPS.containsKey(key)) {
      old = get(key, def);
    }
    System.setProperty(key, Boolean.toString(def));
    refresh();
    return old;
  }

  /**
   * Set the value of the Java system property with the specified {@code key}
   * to the specified default value.
   *
   * @param key of system property
   * @param def the default value
   *
   * @return the ancient value.
   *
   * @throws IllegalArgumentException key null
   */
  public static int set(final String key, final int def) {
    ParametersChecker.checkParameter("Key", key);
    int old = 0;
    if (PROPS.containsKey(key)) {
      old = get(key, def);
    }
    System.setProperty(key, Integer.toString(def));
    refresh();
    return old;
  }

  /**
   * Set the value of the Java system property with the specified {@code key}
   * to the specified default value.
   *
   * @param key of system property
   * @param def the default value
   *
   * @return the ancient value.
   *
   * @throws IllegalArgumentException key null
   */
  public static long set(final String key, final long def) {
    ParametersChecker.checkParameter("Key", key);
    long old = 0;
    if (PROPS.containsKey(key)) {
      old = get(key, def);
    }
    System.setProperty(key, Long.toString(def));
    refresh();
    return old;
  }

  /**
   * Remove the key of the Java system property with the specified {@code
   * key}.
   *
   * @param key of system property
   *
   * @throws IllegalArgumentException key null
   */
  public static void clear(final String key) {
    ParametersChecker.checkParameter("Key", key);
    PROPS.remove(key);
    System.clearProperty(key);
    refresh();
  }

  /**
   * Print to System.out the content of the properties
   *
   * @param out the output stream to be used
   *
   * @throws IllegalArgumentException out null
   */
  public static void debug(final PrintStream out) {
    ParametersChecker.checkParameter("Out", out);
    PROPS.list(out);
  }

  /**
   * @return True if Windows
   */
  public static boolean isWindows() {
    return getOS() == Platform.WINDOWS;
  }

  /**
   * @return the Platform
   */
  public static Platform getOS() {
    if (mOs == null) {
      Platform mOs2 = Platform.UNSUPPORTED;
      String os = "";
      try {
        os = System.getProperty("os.name").toLowerCase();
      } catch (final Exception ignored) {
        // ignore
        SysErrLogger.FAKE_LOGGER.ignoreLog(ignored);
      }
      if (os.contains("win")) {
        mOs2 = Platform.WINDOWS;
        // Windows
      }
      if (os.contains("mac")) {
        mOs2 = Platform.MAC;
        // Mac
      }
      if (os.contains("nux")) {
        mOs2 = Platform.UNIX;
        // Linux
      }
      if (os.contains("nix")) {
        mOs2 = Platform.UNIX;
        // Unix
      }
      if (os.contains("sunos")) {
        mOs2 = Platform.SOLARIS;
        // Solaris
      }
      return mOs2;
    }
    return mOs;
  }

  /**
   * @return True if Mac
   */
  public static boolean isMac() {
    return getOS() == Platform.MAC;
  }

  /**
   * @return True if Unix
   */
  public static boolean isUnix() {
    return getOS() == Platform.UNIX;
  }

  /**
   * @return True if Solaris
   */
  public static boolean isSolaris() {
    return getOS() == Platform.SOLARIS;
  }

  public static void debug() {
    PROPS.list(System.out);//NOSONAR
  }

  /**
   * Inspired from http://commons.apache.org/lang/api-2.4/org/apache/commons/lang/
   * SystemUtils.html
   */
  public enum Platform {
    /**
     * Windows
     */
    WINDOWS,
    /**
     * Mac
     */
    MAC,
    /**
     * Unix
     */
    UNIX,
    /**
     * Solaris
     */
    SOLARIS,
    /**
     * Unsupported
     */
    UNSUPPORTED
  }




}
