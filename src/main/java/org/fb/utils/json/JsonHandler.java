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
package org.fb.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.fb.utils.various.ParametersChecker;
import org.fb.utils.various.SysErrLogger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Json Handler
 */
public final class JsonHandler {

  public static final TypeReference<Map<String, Object>>
      typeReferenceMapStringObject = new TypeReference<Map<String, Object>>() {
  };
  /**
   * JSON parser
   */
  public static final ObjectMapper mapper =
      new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                        .configure(
                            JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                        .configure(
                            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                            false)
                        .configure(JsonGenerator.Feature.ESCAPE_NON_ASCII,
                                   true);

  private JsonHandler() {
  }

  /**
   * @return an empty ObjectNode
   */
  public static ObjectNode createObjectNode() {
    return mapper.createObjectNode();
  }

  /**
   * @return an empty ArrayNode
   */
  public static ArrayNode createArrayNode() {
    return mapper.createArrayNode();
  }

  /**
   * Parses a string representation of a JSON object and returns an
   * ObjectNode.
   * JSON Processing exceptions are kept.
   *
   * @return the objectNode or null if an error occurs
   *
   * @throws JsonProcessingException if exception from Json parsing
   */
  public static ObjectNode getFromStringExc(final String value)
      throws JsonProcessingException {
    try {
      return (ObjectNode) mapper.readTree(value);
    } catch (final JsonProcessingException e) {
      throw e;
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * Parses a string representation of a JSON object and returns an
   * ObjectNode,
   * swallowing any processing exception.
   *
   * @return the objectNode or null if an error occurs
   */
  public static ObjectNode getFromString(final String value) {
    try {
      return (ObjectNode) mapper.readTree(value);
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * @return the jsonNode (ObjectNode or ArrayNode)
   */
  public static ObjectNode getFromFile(final File file) {
    try {
      return (ObjectNode) mapper.readTree(file);
    } catch (final IOException e) {
      return null;
    }
  }

  /**
   * @return the object of type clasz
   */
  public static <T> T getFromString(final String value, final Class<T> clasz) {
    try {
      return mapper.readValue(value, clasz);
    } catch (final IOException e) {
      return null;
    }
  }

  /**
   * @return the corresponding object
   */
  public static Object getFromFile(final File file, final Class<?> clasz) {
    try {
      return mapper.readValue(file, clasz);
    } catch (final IOException e) {
      return null;
    }
  }

  /**
   * @return the Json representation of the object
   */
  public static String writeAsString(final Object object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (final JsonProcessingException e) {
      return "{}";
    }
  }

  /**
   * @return the Json escaped representation of the object
   */
  public static String writeAsStringEscaped(final Object object) {
    try {
      final String temp = mapper.writeValueAsString(object);
      return temp.replaceAll("[\\\\]+", "\\\\");
    } catch (final JsonProcessingException e) {
      return "{}";
    }
  }

  /**
   * Unespace source string before analyzing it as Json
   *
   * @return the unescaped source
   */
  public static String unEscape(final String source) {
    return source.replace("\\", "");
  }

  /**
   * @return True if correctly written
   */
  public static boolean writeAsFile(final Object object, final File file) {
    try {
      mapper.writeValue(file, object);
      return true;
    } catch (final IOException e) {
      return false;
    }
  }

  /**
   * @return the Json representation of the object in Pretty Print format
   */
  public static String prettyPrint(final Object object) {
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    } catch (final JsonProcessingException e) {
      return "{}";
    }
  }

  /**
   * @return the String if the field exists, else null
   */
  public static String getString(final ObjectNode node, final String field) {
    return getValue(node, field, (String) null);
  }

  /**
   * @return the String if the field exists, else defValue
   */
  public static String getValue(final ObjectNode node, final String field,
                                final String defValue) {
    final JsonNode elt = node.get(field);
    if (elt != null) {
      final String val = elt.asText();
      if ("null".equals(val)) {
        return defValue;
      }
      return val;
    }
    return defValue;
  }

  /**
   * @return the String if the field exists, else null
   */
  public static String getString(final ObjectNode node, final Enum<?> field) {
    return getValue(node, field.name(), (String) null);
  }

  /**
   * @return the Boolean if the field exists, else defValue
   */
  public static boolean getValue(final ObjectNode node, final String field,
                                 final boolean defValue) {
    return node.path(field).asBoolean(defValue);
  }

  /**
   * @return the Double if the field exists, else defValue
   */
  public static double getValue(final ObjectNode node, final String field,
                                final double defValue) {
    return node.path(field).asDouble(defValue);
  }

  /**
   * @return the Long if the field exists, else defValue
   */
  public static long getValue(final ObjectNode node, final String field,
                              final long defValue) {
    return node.path(field).asLong(defValue);
  }

  /**
   * @return the Integer if the field exists, else defValue
   */
  public static int getValue(final ObjectNode node, final String field,
                             final int defValue) {
    return node.path(field).asInt(defValue);
  }

  /**
   *
   */
  public static void setValue(final ObjectNode node, final String field,
                              final boolean value) {
    node.put(field, value);
  }

  /**
   *
   */
  public static void setValue(final ObjectNode node, final String field,
                              final double value) {
    node.put(field, value);
  }

  /**
   *
   */
  public static void setValue(final ObjectNode node, final String field,
                              final int value) {
    node.put(field, value);
  }

  /**
   *
   */
  public static void setValue(final ObjectNode node, final String field,
                              final long value) {
    node.put(field, value);
  }

  /**
   *
   */
  public static void setValue(final ObjectNode node, final String field,
                              final String value) {
    if (ParametersChecker.isEmpty(value)) {
      return;
    }
    node.put(field, value);
  }

  /**
   *
   */
  public static void setValue(final ObjectNode node, final String field,
                              final byte[] value) {
    if (value == null || value.length == 0) {
      return;
    }
    node.put(field, value);
  }

  /**
   * @return True if all fields exist
   */
  public static boolean exist(final ObjectNode node, final String... field) {
    for (final String string : field) {
      if (!node.has(string)) {
        return false;
      }
    }
    return true;
  }

  /**
   * @return the String if the field exists, else defValue
   */
  public static String getValue(final ObjectNode node, final Enum<?> field,
                                final String defValue) {
    return getValue(node, field.name(), defValue);
  }

  /**
   * @return the Boolean if the field exists, else defValue
   */
  public static boolean getValue(final ObjectNode node, final Enum<?> field,
                                 final boolean defValue) {
    return node.path(field.name()).asBoolean(defValue);
  }

  /**
   * @return the Double if the field exists, else defValue
   */
  public static double getValue(final ObjectNode node, final Enum<?> field,
                                final double defValue) {
    return node.path(field.name()).asDouble(defValue);
  }

  /**
   * @return the Long if the field exists, else defValue
   */
  public static long getValue(final ObjectNode node, final Enum<?> field,
                              final long defValue) {
    return node.path(field.name()).asLong(defValue);
  }

  /**
   * @return the Integer if the field exists, else defValue
   */
  public static int getValue(final ObjectNode node, final Enum<?> field,
                             final int defValue) {
    return node.path(field.name()).asInt(defValue);
  }

  /**
   * @return the byte array if the field exists, else defValue
   */
  public static byte[] getValue(final ObjectNode node, final Enum<?> field,
                                final byte[] defValue) {
    return getValue(node, field.name(), defValue);
  }

  /**
   * @return the byte array if the field exists, else defValue
   */
  public static byte[] getValue(final ObjectNode node, final String field,
                                final byte[] defValue) {
    final JsonNode elt = node.get(field);
    if (elt != null) {
      try {
        return elt.binaryValue();
      } catch (final IOException e) {
        return defValue;
      }
    }
    return defValue;
  }

  /**
   *
   */
  public static void setValue(final ObjectNode node, final Enum<?> field,
                              final boolean value) {
    node.put(field.name(), value);
  }

  /**
   *
   */
  public static void setValue(final ObjectNode node, final Enum<?> field,
                              final double value) {
    node.put(field.name(), value);
  }

  /**
   *
   */
  public static void setValue(final ObjectNode node, final Enum<?> field,
                              final int value) {
    node.put(field.name(), value);
  }

  /**
   *
   */
  public static void setValue(final ObjectNode node, final Enum<?> field,
                              final long value) {
    node.put(field.name(), value);
  }

  /**
   *
   */
  public static void setValue(final ObjectNode node, final Enum<?> field,
                              final String value) {
    if (ParametersChecker.isEmpty(value)) {
      return;
    }
    node.put(field.name(), value);
  }

  /**
   *
   */
  public static void setValue(final ObjectNode node, final Enum<?> field,
                              final byte[] value) {
    if (value == null || value.length == 0) {
      return;
    }
    node.put(field.name(), value);
  }

  /**
   * @return True if all fields exist
   */
  public static boolean exist(final ObjectNode node, final Enum<?>... field) {
    for (final Enum<?> enm : field) {
      if (!node.has(enm.name())) {
        return false;
      }
    }
    return true;
  }

  /**
   * @return the corresponding HashMap
   */
  public static Map<String, Object> getMapFromString(final String value) {
    if (ParametersChecker.isNotEmpty(value)) {
      Map<String, Object> info = null;
      try {
        info = mapper.readValue(value, typeReferenceMapStringObject);
      } catch (final IOException ignored) {
        SysErrLogger.FAKE_LOGGER.ignoreLog(ignored);
      }
      if (info == null) {
        info = new HashMap<>();
      }
      return info;
    } else {
      return new HashMap<>();
    }
  }
}
