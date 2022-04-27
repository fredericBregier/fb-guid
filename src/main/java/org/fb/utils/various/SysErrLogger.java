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

/**
 * Utility class to be used only in classes where standard Logger is not allowed
 */
public final class SysErrLogger {
  /**
   * FAKE LOGGER used where no LOG could be done
   */
  public static final SysErrLogger FAKE_LOGGER = new SysErrLogger();

  private SysErrLogger() {
    // Empty
  }

  /**
   * Utility method to log nothing
   *
   * @param throwable to log ignore
   */
  public void ignoreLog(final Throwable throwable) {// NOSONAR
    // Nothing to do
  }

  /**
   * Utility method to log through System.out
   */
  public void sysout() {
    System.out.println(); // NOSONAR
  }

  /**
   * Utility method to log through System.out
   *
   * @param message to write for no error log
   */
  public void sysout(final Object message) {
    System.out.println(message); // NOSONAR
  }

  /**
   * Utility method to log through System.err
   *
   * @param message to write for error
   */
  public void syserr(final Object message) {
    System.err.println("ERROR " + message); // NOSONAR
  }

  /**
   * Utility method to log through System.err the current Stacktrace
   */
  public void syserr() {
    new RuntimeException("ERROR Stacktrace").printStackTrace(); // NOSONAR
  }

  /**
   * Utility method to log through System.err the current Stacktrace
   *
   * @param message to write for error
   * @param e throw to write as error
   */
  public void syserr(final String message, final Throwable e) {
    System.err.print("ERROR " + message + ": "); // NOSONAR
    e.printStackTrace(); // NOSONAR
  }

  /**
   * Utility method to log through System.err the current Stacktrace
   *
   * @param e throw to write as error
   */
  public void syserr(final Throwable e) {
    System.err.print("ERROR: "); // NOSONAR
    e.printStackTrace(); // NOSONAR
  }
}
