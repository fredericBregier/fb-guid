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

import com.google.common.io.BaseEncoding;

/**
 * Base16, Base32 and Base64 codecs
 */
public final class BaseXx {
  private static final String ARGUMENT_NULL_NOT_ALLOWED =
      "argument null not allowed";
  private static final BaseEncoding BASE64_URL_WITHOUT_PADDING =
      BaseEncoding.base64Url().omitPadding();
  private static final BaseEncoding BASE64_URL_WITH_PADDING =
      BaseEncoding.base64Url();
  private static final BaseEncoding BASE64 = BaseEncoding.base64();
  private static final BaseEncoding BASE32 =
      BaseEncoding.base32().lowerCase().omitPadding();
  private static final BaseEncoding BASE16 =
      BaseEncoding.base16().lowerCase().omitPadding();

  private static final Boolean NOT_NULL = Boolean.TRUE;

  private BaseXx() {
    // empty
  }

  /**
   * @param bytes to transform
   *
   * @return the Base 16 representation
   *
   * @throws IllegalArgumentException if argument is not compatible
   */
  public static String getBase16(final byte[] bytes) {
    ParametersChecker.checkParameter(ARGUMENT_NULL_NOT_ALLOWED, bytes,
                                     NOT_NULL);
    return BASE16.encode(bytes);
  }

  /**
   * @param bytes to transform
   *
   * @return the Base 32 representation
   *
   * @throws IllegalArgumentException if argument is not compatible
   */
  public static String getBase32(final byte[] bytes) {
    ParametersChecker.checkParameter(ARGUMENT_NULL_NOT_ALLOWED, bytes,
                                     NOT_NULL);
    return BASE32.encode(bytes);
  }

  /**
   * @param bytes to transform
   *
   * @return the Base 64 Without Padding representation (used only for url)
   *
   * @throws IllegalArgumentException if argument is not compatible
   */
  public static String getBase64UrlWithoutPadding(final byte[] bytes) {
    ParametersChecker.checkParameter(ARGUMENT_NULL_NOT_ALLOWED, bytes,
                                     NOT_NULL);
    return BASE64_URL_WITHOUT_PADDING.encode(bytes);
  }

  /**
   * @param bytes to transform
   *
   * @return the Base 64 With Padding representation (used only for url)
   *
   * @throws IllegalArgumentException if argument is not compatible
   */
  public static String getBase64UrlWithPadding(final byte[] bytes) {
    ParametersChecker.checkParameter(ARGUMENT_NULL_NOT_ALLOWED, bytes,
                                     NOT_NULL);
    return BASE64_URL_WITH_PADDING.encode(bytes);
  }

  /**
   * @param bytes to transform
   *
   * @return the Base 64 With Padding representation
   *
   * @throws IllegalArgumentException if argument is not compatible
   */
  public static String getBase64(final byte[] bytes) {
    ParametersChecker.checkParameter(ARGUMENT_NULL_NOT_ALLOWED, bytes,
                                     NOT_NULL);
    return BASE64.encode(bytes);
  }

  /**
   * @param base16 to transform
   *
   * @return the byte from Base 16
   *
   * @throws IllegalArgumentException if argument is not compatible
   */
  public static byte[] getFromBase16(final String base16) {
    ParametersChecker.checkParameter(ARGUMENT_NULL_NOT_ALLOWED, base16);
    return BASE16.decode(base16);
  }

  /**
   * @param base32 to transform
   *
   * @return the byte from Base 32
   *
   * @throws IllegalArgumentException if argument is not compatible
   */
  public static byte[] getFromBase32(final String base32) {
    ParametersChecker.checkParameter(ARGUMENT_NULL_NOT_ALLOWED, base32);
    return BASE32.decode(base32);
  }

  /**
   * @param base64 to transform
   *
   * @return the byte from Base 64 Without Padding (used only for url)
   *
   * @throws IllegalArgumentException if argument is not compatible
   */
  public static byte[] getFromBase64UrlWithoutPadding(final String base64) {
    ParametersChecker.checkParameter(ARGUMENT_NULL_NOT_ALLOWED, base64);
    return BASE64_URL_WITHOUT_PADDING.decode(base64);
  }

  /**
   * @param base64Padding to transform
   *
   * @return the byte from Base 64 With Padding (used only for url)
   *
   * @throws IllegalArgumentException if argument is not compatible
   */
  public static byte[] getFromBase64UrlPadding(final String base64Padding) {
    ParametersChecker.checkParameter(ARGUMENT_NULL_NOT_ALLOWED, base64Padding);
    return BASE64_URL_WITH_PADDING.decode(base64Padding);
  }

  /**
   * @param base64Padding to transform
   *
   * @return the byte from Base 64 With Padding
   *
   * @throws IllegalArgumentException if argument is not compatible
   */
  public static byte[] getFromBase64(final String base64Padding) {
    ParametersChecker.checkParameter(ARGUMENT_NULL_NOT_ALLOWED, base64Padding);
    return BASE64.decode(base64Padding);
  }
}
