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

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Logger for each method to print current test name and duration.<br>
 * <br>
 * Include in each Junit4 Test classes:<br>
 * <pre>
 *  @Rule(order = Integer.MIN_VALUE)
 *  public TestWatcher watchman= new TestWatcherJunit4();
 * </pre>
 */
public class TestWatcherJunit4 extends TestWatcher {
  private long startTime;

  @Override
  protected void starting(Description description) {
    SysErrLogger.FAKE_LOGGER.sysout(
        Color.YELLOW + "==============\nStarting test: " +
        description.getMethodName() + Color.RESET);
    startTime = System.nanoTime();
  }

  @Override
  protected void finished(Description description) {
    long time = (System.nanoTime() - startTime) / 1000000;
    SysErrLogger.FAKE_LOGGER.sysout(
        Color.BLUE + "Ending test: " + description.getMethodName() + " in " +
        time + " ms\n==============" + Color.RESET);
  }

  enum Color {
    /**
     * Color end string, color reset
     */
    RESET("\033[0m"),
    /**
     * YELLOW
     */
    YELLOW("\033[0;33m"),
    /**
     * BLUE
     */
    BLUE("\033[0;34m");
    private final String code;

    Color(String code) {
      this.code = code;
    }

    @Override
    public String toString() {
      return code;
    }
  }
}
