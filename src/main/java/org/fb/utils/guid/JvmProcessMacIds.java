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

package org.fb.utils.guid;

import org.fb.utils.various.RandomUtil;
import org.fb.utils.various.SysErrLogger;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import static org.fb.utils.various.SystemPropertyUtil.*;

public final class JvmProcessMacIds {
  /**
   * Definition for Machine Id replacing MAC address
   */
  private static final Pattern MACHINE_ID_PATTERN = Pattern.compile("^(?:[0-9a-fA-F][:-]?){6,8}$");
  private static final int MACHINE_ID_LEN = 8;
  /**
   * MAX value on 4 bytes (64 system use 2^31-1 id, in fact shall be 4 M)
   */
  private static final int MAX_PID = 0x7FFFFFFF;
  private static final int BYTE_FILTER = 0xFF;
  private static final Object[] EMPTY_OBJECTS = new Object[0];
  private static final Class<?>[] EMPTY_CLASSES = new Class<?>[0];
  private static final Pattern COMPILE = Pattern.compile("[:-]");
  private static final byte[] EMPTY_BYTES = {};

  private static final int JVMPID;
  private static byte[] mac;
  private static long macLong;
  private static int macInt;
  private static byte jvmByteId;
  private static int jvmIntegerId;
  private static long jvmLongId;

  private static void _initialize() {
    macLong = macAddressAsLong();
    macInt = macAddressAsInt();
    jvmIntegerId = jvmInstanceIdAsInteger();
    jvmByteId = jvmInstanceIdAsByte();
    jvmLongId = jvmInstanceIdAsLong();
  }

  static {
    JVMPID = jvmProcessId();
    mac = macAddress();
    macLong = macAddressAsLong();
    macInt = macAddressAsInt();
    jvmIntegerId = jvmInstanceIdAsInteger();
    jvmByteId = jvmInstanceIdAsByte();
    jvmLongId = jvmInstanceIdAsLong();
  }

  public static int getJvmPID() {
    return JVMPID;
  }

  public static byte[] getMac() {
    return mac;
  }

  public static long getMacLong() {
    return macLong;
  }

  public static int getMacInt() {
    return macInt;
  }

  public static byte getJvmByteId() {
    return jvmByteId;
  }

  public static int getJvmIntegerId() {
    return jvmIntegerId;
  }

  public static long getJvmLongId() {
    return jvmLongId;
  }

  private JvmProcessMacIds() {
  }

  /**
   * Use both PID and MAC address but as 8 bites hash
   *
   * @return one id as much as possible unique
   */
  private static byte jvmInstanceIdAsByte() {
    return (byte) (Integer.hashCode(jvmIntegerId) & BYTE_FILTER);
  }

  /**
   * Use both PID and MAC address but as 4 bytes hash
   *
   * @return one id as much as possible unique
   */
  private static int jvmInstanceIdAsInteger() {
    final long id = 31L * JVMPID + macInt;
    return Long.hashCode(id);
  }

  /**
   * @return the JVM Process ID
   */
  private static int jvmProcessId() {
    // Note: may fail in some JVM implementations
    // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
    try {
      final ClassLoader loader = getSystemClassLoader();
      String value;
      value = jvmProcessIdManagementFactory(loader, EMPTY_OBJECTS, EMPTY_CLASSES);
      final int atIndex = value.indexOf('@');
      if (atIndex >= 0) {
        value = value.substring(0, atIndex);
      }
      int processId = -1;
      processId = parseProcessId(processId, value);
      if (processId < 0 || processId > MAX_PID) {
        processId = RandomUtil.RANDOM.nextInt(MAX_PID);
      }
      return processId;
    } catch (final Throwable e) {//NOSONAR
      SysErrLogger.FAKE_LOGGER.syserr(e);
      return RandomUtil.RANDOM.nextInt(MAX_PID);
    }
  }

  /**
   * @return MAC address as int (truncated to 4 bytes instead of 8)
   */
  private static int macAddressAsInt() {
    return (mac[3] & BYTE_FILTER) << 24 | (mac[2] & BYTE_FILTER) << 16 | (mac[1] & BYTE_FILTER) << 8 |
           mac[0] & BYTE_FILTER;
  }

  private static ClassLoader getSystemClassLoader() {
    return ClassLoader.getSystemClassLoader();
  }

  /**
   * @return the processId as String
   */
  private static String jvmProcessIdManagementFactory(final ClassLoader loader, final Object[] emptyObjects,
                                                      final Class<?>[] emptyClasses) {
    String value;
    try {
      // Invoke
      // java.lang.management.ManagementFactory.getRuntimeMXBean().getName()
      final Class<?> mgmtFactoryType = Class.forName("java.lang.management.ManagementFactory", true, loader);
      final Class<?> runtimeMxBeanType = Class.forName("java.lang.management.RuntimeMXBean", true, loader);

      final Method getRuntimeMXBean = mgmtFactoryType.getMethod("getRuntimeMXBean", emptyClasses);
      final Object bean = getRuntimeMXBean.invoke(null, emptyObjects);
      final Method getName = runtimeMxBeanType.getDeclaredMethod("getName", emptyClasses);
      value = (String) getName.invoke(bean, emptyObjects);
    } catch (final Exception e) {
      SysErrLogger.FAKE_LOGGER.syserr("Unable to get PID, try another way: " + e.getMessage());

      try {
        // Invoke android.os.Process.myPid()
        final Class<?> processType = Class.forName("android.os.Process", true, loader);
        final Method myPid = processType.getMethod("myPid", emptyClasses);
        value = myPid.invoke(null, emptyObjects).toString();
      } catch (final Exception e2) {
        SysErrLogger.FAKE_LOGGER.syserr("Unable to get PID: " + e2.getMessage());
        value = "";
      }
    }
    return value;
  }

  /**
   * @return the processId
   */
  private static int parseProcessId(final int oldProcessId, final String customProcessId) {
    int processId = oldProcessId;
    try {
      processId = Integer.parseInt(customProcessId);
    } catch (final NumberFormatException e) {
      // Malformed input.
    }
    if (processId < 0 || processId > MAX_PID) {
      processId = RandomUtil.RANDOM.nextInt(MAX_PID);
    }
    return processId;
  }

  /**
   * Use both PID (2 bytes at must) and MAC address
   *
   * @return one id as much as possible unique
   */
  private static long jvmInstanceIdAsLong() {
    return (macLong & 0xFFFFFFFFFFFFL) + ((long) JVMPID << 6 * 8 & 0xFFFF);
  }

  /**
   * @return MAC address as long
   */
  private static long macAddressAsLong() {
    long value = (long) (mac[5] & BYTE_FILTER) << 40 | (long) (mac[4] & BYTE_FILTER) << 32 |
                 (long) (mac[3] & BYTE_FILTER) << 24 | (long) (mac[2] & BYTE_FILTER) << 16 |
                 (long) (mac[1] & BYTE_FILTER) << 8 | mac[0] & BYTE_FILTER;
    if (mac.length > 6) {
      value |= (long) (mac[6] & BYTE_FILTER) << 48;
      if (mac.length > 7) {
        return (long) (mac[7] & BYTE_FILTER) << 56 | value;
      }
    }
    return value;
  }

  /**
   * @return the mac address if possible, else random values
   */
  private static byte[] macAddress() {
    try {
      byte[] machineId = null;
      final String customMachineId = getMachineId();
      if (customMachineId != null && MACHINE_ID_PATTERN.matcher(customMachineId).matches()) {
        machineId = parseMachineId(customMachineId);
      }

      if (machineId == null) {
        machineId = defaultMachineId();
      }
      return machineId;
    } catch (final Throwable e) {//NOSONAR
      return RandomUtil.getRandom(MACHINE_ID_LEN);
    }
  }

  private static byte[] parseMachineId(String value) {
    // Strip separators.
    value = COMPILE.matcher(value).replaceAll("");

    final int len = value.length();
    final int lenJ = len / 2;
    final byte[] machineId = new byte[lenJ];
    for (int j = 0, i = 0; i < len && j < lenJ; i += 2, j++) {
      machineId[j] = (byte) Integer.parseInt(value.substring(i, i + 2), 16);
    }
    return machineId;
  }

  private static byte[] defaultMachineId() {
    // Find the best MAC address available.
    final byte[] notFound = EMPTY_BYTES;
    byte[] bestMacAddr = notFound;
    InetAddress bestInetAddr;
    try {
      bestInetAddr = InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
    } catch (final UnknownHostException e) {
      // Never happens.
      throw new IllegalArgumentException(e);
    }

    // Retrieve the list of available network interfaces.
    final Map<NetworkInterface, InetAddress> ifaces = new LinkedHashMap<>();
    try {
      for (final Enumeration<NetworkInterface> i = NetworkInterface.getNetworkInterfaces();
           i.hasMoreElements(); ) {
        final NetworkInterface iface = i.nextElement();
        // Use the interface with proper INET addresses only.
        Enumeration<InetAddress> addresses =
            AccessController.doPrivileged(new PrivilegedAction<Enumeration<InetAddress>>() {
              @Override
              public Enumeration<InetAddress> run() {
                return iface.getInetAddresses();
              }
            });
        if (addresses == null) {
          addresses = Collections.enumeration(Collections.emptyList());
        }
        if (addresses.hasMoreElements()) {
          final InetAddress a = addresses.nextElement();
          if (!a.isLoopbackAddress()) {
            ifaces.put(iface, a);
          }
        }
      }
    } catch (final SocketException ignored) {
      // nothing
    }

    for (final Entry<NetworkInterface, InetAddress> entry : ifaces.entrySet()) {
      final NetworkInterface iface = entry.getKey();
      final InetAddress inetAddr = entry.getValue();
      if (iface.isVirtual()) {
        continue;
      }

      final byte[] macAddr;
      try {
        macAddr = AccessController.doPrivileged(new PrivilegedExceptionAction<byte[]>() {
          @Override
          public byte[] run() throws SocketException {
            return iface.getHardwareAddress();
          }
        });
      } catch (final PrivilegedActionException ignore) {
        continue;
      }

      boolean replace = false;
      int res = compareAddresses(bestMacAddr, macAddr);
      if (res < 0) {
        // Found a better MAC address.
        replace = true;
      } else if (res == 0) {
        // Two MAC addresses are of pretty much same quality.
        res = compareAddresses(bestInetAddr, inetAddr);
        if (res < 0 || res == 0 && bestMacAddr.length < macAddr.length) {
          // Found a MAC address with better INET address.
          // Cannot tell the difference. Choose the longer one.
          replace = true;
        }
      }

      if (replace) {
        bestMacAddr = macAddr;
        bestInetAddr = inetAddr;
      }
    }

    if (bestMacAddr == notFound) {
      bestMacAddr = RandomUtil.getRandom(MACHINE_ID_LEN);
    }
    return bestMacAddr;
  }

  /**
   * @return positive - current is better, 0 - cannot tell from MAC addr,
   *     negative - candidate is better.
   */
  private static int compareAddresses(final byte[] current, final byte[] candidate) {
    if (candidate == null) {
      return 1;
    }
    // Must be EUI-48 or longer.
    if (candidate.length < 6) {
      return 1;
    }
    // Must not be filled with only 0 and 1.
    boolean onlyZeroAndOne = true;
    for (final byte b : candidate) {
      if (b != 0 && b != 1) {
        onlyZeroAndOne = false;
        break;
      }
    }
    if (onlyZeroAndOne) {
      return 1;
    }
    // Must not be a multicast address
    if ((candidate[0] & 1) != 0) {
      return 1;
    }
    // Current is empty
    if (current.length == 0) {
      return -1;
    }
    // Prefer globally unique address.
    if ((candidate[0] & 2) == 0) {
      if ((current[0] & 2) == 0) {
        // Both current and candidate are globally unique addresses.
        return 0;
      } else {
        // Only current is globally unique.
        return -1;
      }
    } else {
      if ((current[0] & 2) == 0) {
        // Only candidate is globally unique.
        return 1;
      } else {
        // Both current and candidate are non-unique.
        return 0;
      }
    }
  }

  /**
   * @return positive - current is better, 0 - cannot tell, negative -
   *     candidate
   *     is better
   */
  private static int compareAddresses(final InetAddress current, final InetAddress candidate) {
    return scoreAddress(current) - scoreAddress(candidate);
  }

  private static int scoreAddress(final InetAddress addr) {
    if (addr.isAnyLocalAddress()) {
      return 0;
    }
    if (addr.isMulticastAddress()) {
      return 1;
    }
    if (addr.isLinkLocalAddress()) {
      return 2;
    }
    if (addr.isSiteLocalAddress()) {
      return 3;
    }

    return 4;
  }

  /**
   * Up to the 8 first bytes will be used. If Null or less than 6 bytes, extra
   * bytes will be randomly generated, up to 6 bytes.
   *
   * @param mac the MAC address in byte format (up to the 8 first
   *     bytes will be used)
   */
  public static synchronized void setMac(final byte[] mac) {
    if (mac == null) {
      JvmProcessMacIds.mac = RandomUtil.getRandom(MACHINE_ID_LEN);
    } else {
      if (mac.length < 6) {
        JvmProcessMacIds.mac = Arrays.copyOf(mac, 6);
        for (int i = mac.length; i < 6; i++) {
          JvmProcessMacIds.mac[i] = (byte) RandomUtil.RANDOM.nextInt(256);
        }
      } else {
        JvmProcessMacIds.mac = Arrays.copyOf(mac, Math.min(mac.length, MACHINE_ID_LEN));
      }
    }
    _initialize();
  }
}
