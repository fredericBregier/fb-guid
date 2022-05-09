/*
 * Copyright (c) 2022. FbUtilities Contributors and Frederic Bregier
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

package org.fb.utils.guid;

import org.junit.Test;

import static org.junit.Assert.*;

public class JvmProcessMacIdsTest {

  @Test
  public void checkValues() {
    byte[] mac = JvmProcessMacIds.getMac();
    long macL = JvmProcessMacIds.getMacLong();
    long value = 0;
    for (int i = 0; i < mac.length; i++) {
      value <<= 8;
      value |= mac[mac.length - i - 1] & 0xFF;
    }
    assertEquals(macL, value);
    int pid = JvmProcessMacIds.getJvmPID();
    int macI = JvmProcessMacIds.getMacInt();
    int jvmI = JvmProcessMacIds.getJvmIntegerId();
    byte jvmB = JvmProcessMacIds.getJvmByteId();
    long jvmL = JvmProcessMacIds.getJvmLongId();
  }
}
