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

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;

/**
 * Improve Random generation
 */
public final class SystemRandomSecure {
  private static volatile boolean initialized;
  private static boolean specialSecureRandom;

  static {
    initializedRandomContext();
  }

  private SystemRandomSecure() {
    // Nothing
  }

  public static void initializedRandomContext() {
    try {
      if (!initialized) {
        registerRandomSecure();
        initialized = true;
      }
    } catch (final Throwable throwable) {//NOSONAR
      SysErrLogger.FAKE_LOGGER.syserr("Error occurs at startup: " +//NOSONAR
                                      throwable.getMessage(), throwable);//NOSONAR
    }
  }

  /**
   * To fix issue on SecureRandom using bad algotithm
   * </br>
   * Called at second place
   */
  private static void registerRandomSecure() {
    if (System.getProperty("os.name").contains("Windows")) {
      final Provider provider = Security.getProvider("SunMSCAPI");
      if (provider != null) {
        Security.removeProvider(provider.getName());
        Security.insertProviderAt(provider, 1);
        specialSecureRandom = true;
      }
    } else {
      System.setProperty("java.security.egd", "file:/dev/./urandom");
      final Provider provider = Security.getProvider("SUN");
      var type = "SecureRandom";
      var alg = "NativePRNGNonBlocking";
      if (provider != null) {
        var name = String.format("%s.%s", type, alg);
        final Provider.Service service = provider.getService(type, alg);
        if (service != null) {
          Security.insertProviderAt(
              new Provider(name, provider.getVersionStr(), "FB quick fix for SecureRandom using urandom") {
                private static final long serialVersionUID = 1001L;

                {
                  System.setProperty(name, service.getClassName());
                }

              }, 1);
          specialSecureRandom = true;
        }
      }
    }
  }

  public static SecureRandom getSecureRandom() {
    if (!specialSecureRandom) {
      return new SecureRandom();
    }
    if (System.getProperty("os.name").contains("Windows")) {
      try {
        return SecureRandom.getInstance("Windows-PRNG", "SunMSCAPI");
      } catch (final NoSuchAlgorithmException | NoSuchProviderException e) {
        return new SecureRandom();
      }
    } else {
      try {
        return SecureRandom.getInstance("NativePRNGNonBlocking", "SUN");
      } catch (final NoSuchAlgorithmException | NoSuchProviderException e) {
        return new SecureRandom();
      }
    }
  }
}
