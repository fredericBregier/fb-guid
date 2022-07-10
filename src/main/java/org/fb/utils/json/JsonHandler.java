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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.fb.utils.various.ParametersChecker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Json Handler
 */
public final class JsonHandler {

  public static final TypeReference<Map<String, Object>> typeReferenceMapStringObject =
      new TypeReference<Map<String, Object>>() {
      };
  /**
   * JSON parser
   */
  public static final ObjectMapper mapper =
      new ObjectMapper().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                        .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);

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
  public static ObjectNode getFromString(final String value) throws JsonProcessingException {
    try {
      return (ObjectNode) mapper.readTree(value);
    } catch (final JsonProcessingException e) {
      throw e;
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
  public static <T> T getFromString(final String value, final Class<T> clasz) throws JsonProcessingException {
    return mapper.readValue(value, clasz);
  }

  /**
   * @return the corresponding object
   */
  public static Object getFromFile(final File file, final Class<?> clasz) throws IOException {
    return mapper.readValue(file, clasz);
  }

  /**
   * @return the Json escaped representation of the object
   */
  public static String writeAsStringEscaped(final Object object) throws JsonProcessingException {
    var temp = writeAsString(object);
    return temp.replaceAll("[\\\\]+", "\\\\");
  }

  /**
   * @return the Json representation of the object
   */
  public static String writeAsString(final Object object) throws JsonProcessingException {
    return mapper.writeValueAsString(object);
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
  public static String prettyPrint(final Object object) throws JsonProcessingException {
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
  }

  /**
   * @return the corresponding HashMap
   */
  public static Map<String, Object> getMapFromString(final String value) throws JsonProcessingException {
    if (ParametersChecker.isNotEmpty(value)) {
      final Map<String, Object> info = mapper.readValue(value, typeReferenceMapStringObject);
      if (info == null) {
        return new HashMap<>();
      }
      return info;
    } else {
      return new HashMap<>();
    }
  }
}
