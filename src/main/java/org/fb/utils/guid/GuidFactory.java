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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.fb.utils.exceptions.InvalidArgumentRuntimeException;
import org.fb.utils.json.JsonHandler;
import org.fb.utils.various.BaseXx;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Guid Factory
 */
public final class GuidFactory {
  /**
   * ARK header
   */
  static final String ARK = "ark:/";
  static final short BYTE_SIZE = 8;
  static final String ATTEMPTED_TO_PARSE_MALFORMED_ARK_GUID = "Attempted to parse malformed ARK Guid: ";
  static final short BYTE_MASK = 0xFF;
  static final short MIN_SHORT = -32768;
  static final short MAX_SHORT = 32767;
  static final int MIN_INT = -2147483648;
  static final int MAX_INT = 2147483647;
  static final int MASK_INT = 0xFFFFFFFF;
  private static final short DEFAULT_TENANT = 2;
  private static final short DEFAULT_PLATFORM = 6;
  private static final short DEFAULT_PID = 3;
  private static final short DEFAULT_TIME = 6;
  private static final short DEFAULT_COUNTER = 3;
  private static final short MAX_TENANT = 8;
  private static final short MAX_PLATFORM = 8;
  private static final short MAX_PID = 4;
  private static final short MAX_TIME = 8;
  private static final short MAX_COUNTER = 4;
  private static final short MIN_TENANT = 1;
  private static final short MIN_PLATFORM = 1;
  private static final short MIN_PID = 0;
  private static final short MIN_TIME = 4;
  private static final short MIN_COUNTER = 2;

  private static final short HEADER_POS = 0;
  private static final short SUBSIZE1_POS = 1;
  private static final short SUBSIZE2_POS = 2;
  private static final short HEADER_SIZE = 3;
  private static final short HEADER_64_SIZE = 4;
  private static final AtomicInteger COUNTER = new AtomicInteger(MIN_COUNTER);
  private static final short TENANT_POS = HEADER_POS + HEADER_SIZE;
  private static final short MAX_SIZE =
      HEADER_SIZE + MAX_TENANT + MAX_PLATFORM + MAX_PID + MAX_TIME + MAX_COUNTER;
  private static final byte VERSION = 3;
  private short tenantSize = GUID_CONFIGURATION.DEFAULT.tenantSize;
  private short platformSize = GUID_CONFIGURATION.DEFAULT.platformSize;
  private short pidSize = GUID_CONFIGURATION.DEFAULT.pidSize;
  private short timeSize = GUID_CONFIGURATION.DEFAULT.timeSize;
  private short counterSize = GUID_CONFIGURATION.DEFAULT.counterSize;
  private int maxCounter;
  private short keySize;
  private short key16Size;
  private short key32Size;
  private short key64Size;
  private short platformPos;
  private short pidPos;
  private short timePos;
  private short counterPos;
  private long tenantId;
  private long platformId = JvmProcessMacIds.getMacLong();
  private int pid = JvmProcessMacIds.getJvmPID();

  public GuidFactory() {
    finalizeConfiguration();
  }

  private void finalizeConfiguration() {
    keySize = (short) (tenantSize + platformSize + pidSize + timeSize + counterSize + HEADER_SIZE);
    final short subsize = (short) (keySize - HEADER_SIZE);
    key16Size = (short) (subsize * 2 + HEADER_64_SIZE);
    key32Size = (short) (subsize * 8 / 5 + (subsize * 8 % 5 > 0? 1 : 0) + HEADER_64_SIZE);
    key64Size = (short) (subsize * 8 / 6 + (subsize * 8 % 6 > 0? 1 : 0) + HEADER_64_SIZE);
    platformPos = (short) (TENANT_POS + tenantSize);
    pidPos = (short) (platformPos + platformSize);
    timePos = (short) (pidPos + pidSize);
    counterPos = (short) (timePos + timeSize);
    maxCounter = (int) ((1L << counterSize * 8 - 1) - 1);
  }

  public Guid newGuid() {
    return new Guid(this);
  }

  public Guid newGuid(final long tenantId) {
    return new Guid(this, tenantId);
  }

  public Guid newGuid(final long tenantId, final long platformId) {
    return new Guid(this, tenantId, platformId);
  }

  public Guid getGuid(final String idSource) {
    return new Guid(this, idSource);
  }

  public Guid getGuid(final byte[] bytes) {
    return new Guid(this, bytes);
  }

  public GuidFactory useConfiguration(final GUID_CONFIGURATION guidConfiguration) {
    _setTenantSize(guidConfiguration.tenantSize)._setPlatformSize(guidConfiguration.platformSize)
                                                ._setPidSize(guidConfiguration.pidSize)
                                                ._setTimeSize(guidConfiguration.timeSize)
                                                ._setCounterSize(guidConfiguration.counterSize)
                                                .finalizeConfiguration();
    return this;
  }

  public GuidFactory setTenantId(final long tenantId) {
    this.tenantId = tenantId;
    return this;
  }

  public GuidFactory setPlatformId(final long platformId) {
    this.platformId = platformId;
    return this;
  }

  public GuidFactory resetPlatformId() {
    platformId = JvmProcessMacIds.getMacLong();
    return this;
  }

  public GuidFactory setPid(final int pid) {
    this.pid = pid;
    return this;
  }

  public GuidFactory resetPid() {
    pid = JvmProcessMacIds.getJvmPID();
    return this;
  }

  public short getTenantSize() {
    return tenantSize;
  }

  private GuidFactory _setTenantSize(final short tenantSize) {
    if (tenantSize > MAX_TENANT || tenantSize < MIN_TENANT) {
      throw new InvalidArgumentRuntimeException(
          "TenantSize must be betwen " + MIN_TENANT + " and " + MAX_TENANT);
    }
    this.tenantSize = tenantSize;
    return this;
  }

  public GuidFactory setTenantSize(final short tenantSize) {
    _setTenantSize(tenantSize);
    finalizeConfiguration();
    return this;
  }

  public int getPlatformSize() {
    return platformSize;
  }

  private GuidFactory _setPlatformSize(final short platformSize) {
    if (platformSize > MAX_PLATFORM || platformSize < MIN_PLATFORM) {
      throw new InvalidArgumentRuntimeException(
          "PlatformSize must be betwen " + MIN_PLATFORM + " and " + MAX_PLATFORM);
    }
    this.platformSize = platformSize;
    return this;
  }

  public GuidFactory setPlatformSize(final short platformSize) {
    _setPlatformSize(platformSize);
    finalizeConfiguration();
    return this;
  }

  public short getPidSize() {
    return pidSize;
  }

  private GuidFactory _setPidSize(final short pidSize) {
    if (pidSize > MAX_PID || pidSize < MIN_PID) {
      throw new InvalidArgumentRuntimeException("PidSize must be betwen " + MIN_PID + " and " + MAX_PID);
    }
    this.pidSize = pidSize;
    return this;
  }

  public GuidFactory setPidSize(final short pidSize) {
    _setPidSize(pidSize);
    finalizeConfiguration();
    return this;
  }

  public short getTimeSize() {
    return timeSize;
  }

  private GuidFactory _setTimeSize(final short timeSize) {
    if (timeSize > MAX_TIME || timeSize < MIN_TIME) {
      throw new InvalidArgumentRuntimeException("TimeSize must be betwen " + MIN_TIME + " and " + MAX_TIME);
    }
    this.timeSize = timeSize;
    finalizeConfiguration();
    return this;
  }

  public GuidFactory setTimeSize(final short timeSize) {
    _setTimeSize(timeSize);
    finalizeConfiguration();
    return this;
  }

  public short getCounterSize() {
    return counterSize;
  }

  private GuidFactory _setCounterSize(final short counterSize) {
    if (counterSize > MAX_COUNTER || counterSize < MIN_COUNTER) {
      throw new InvalidArgumentRuntimeException(
          "CounterSize must be betwen " + MIN_COUNTER + " and " + MAX_COUNTER);
    }
    this.counterSize = counterSize;
    return this;
  }

  public GuidFactory setCounterSize(final short counterSize) {
    _setCounterSize(counterSize);
    finalizeConfiguration();
    return this;
  }

  public short getKeySize() {
    return keySize;
  }

  public short getKey16Size() {
    return key16Size;
  }

  public short getKey32Size() {
    return key32Size;
  }

  public short getKey64Size() {
    return key64Size;
  }

  private String getHeaderEncoded() {
    return BaseXx.getBase64(getHeader());
  }

  private byte[] getHeader() {
    final byte[] bytes = new byte[HEADER_SIZE];
    bytes[0] = VERSION & 0xFF;
    int value = tenantSize - MIN_TENANT;
    value <<= 3;
    value += platformSize - MIN_PLATFORM;
    bytes[SUBSIZE1_POS] = (byte) (value & 0xFF);
    value = pidSize - MIN_PID;
    value <<= 3;
    value += timeSize - MIN_TIME;
    value <<= 2;
    value += counterSize - MIN_COUNTER;
    bytes[SUBSIZE2_POS] = (byte) (value & 0xFF);
    return bytes;
  }

  private GuidFactory setFromHeader(String encoded64) {
    final byte[] bytes = BaseXx.getFromBase64(encoded64.substring(0, HEADER_64_SIZE));
    final int version = bytes[0] & 0xFF;
    if (version != VERSION) {
      throw new InvalidArgumentRuntimeException("Incorrect Version");
    }
    int value = bytes[SUBSIZE1_POS];
    _setPlatformSize((short) ((value & 0x07) + MIN_PLATFORM));
    value >>>= 3;
    _setTenantSize((short) ((value & 0x07) + MIN_TENANT));
    value = bytes[SUBSIZE2_POS];
    _setCounterSize((short) ((value & 0x03) + MIN_COUNTER));
    value >>>= 2;
    _setTimeSize((short) ((value & 0x07) + MIN_TIME));
    value >>>= 3;
    _setPidSize((short) ((value & 0x07) + MIN_PID));
    finalizeConfiguration();
    return this;
  }

  /**
   * @param json Json representation
   *
   * @return the Guid from the Json
   */
  public static Guid getFromJson(final String json) {
    try {
      return JsonHandler.mapper.readerFor(Guid.class).readValue(json);
    } catch (JsonProcessingException e) {
      throw new InvalidArgumentRuntimeException("Json value incorrect", e);
    }
  }

  public enum GUID_CONFIGURATION {
    BIGGEST(MAX_TENANT, MAX_PLATFORM, MAX_PID, MAX_TIME, MAX_COUNTER),
    STANDARD((short) 3, DEFAULT_PLATFORM, DEFAULT_PID, DEFAULT_TIME, DEFAULT_COUNTER),
    DEFAULT(DEFAULT_TENANT, DEFAULT_PLATFORM, DEFAULT_PID, DEFAULT_TIME, DEFAULT_COUNTER),
    TINY(DEFAULT_TENANT, (short) 4, (short) 2, DEFAULT_TIME, DEFAULT_COUNTER),
    SMALLEST(MIN_TENANT, MIN_PLATFORM, MIN_PID, MIN_TIME, MIN_COUNTER);
    private final short tenantSize;
    private final short platformSize;
    private final short pidSize;
    private final short timeSize;
    private final short counterSize;

    GUID_CONFIGURATION(final short tenantSize, final short platformSize, final short pidSize,
                       final short timeSize, final short counterSize) {
      this.tenantSize = tenantSize;
      this.platformSize = platformSize;
      this.pidSize = pidSize;
      this.timeSize = timeSize;
      this.counterSize = counterSize;
    }
  }

  @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
  public static final class Guid implements Comparable<Guid> {
    /**
     * real Guid
     */
    @JsonIgnore
    private final byte[] bguid;
    @JsonIgnore
    private final GuidFactory guidFactory;

    /**
     * Constructor that takes a byte array as this Guid's content
     *
     * @param bytes Guid content
     *
     * @throws InvalidArgumentRuntimeException if the byte array is incorrect
     */
    private Guid(final GuidFactory factory, final byte[] bytes) {
      guidFactory = factory;
      if (bytes == null) {
        throw new InvalidArgumentRuntimeException("Empty argument");
      }
      if (bytes.length < guidFactory.keySize) {
        throw new InvalidArgumentRuntimeException(
            "Attempted to parse malformed Guid: (" + bytes.length + ')');
      }
      bguid = new byte[guidFactory.keySize];
      System.arraycopy(bytes, 0, bguid, 0, guidFactory.keySize);
      validateVersion();
    }

    @JsonIgnore
    private void validateVersion() {
      if (getVersion() != VERSION) {
        throw new InvalidArgumentRuntimeException("Invalid Version");
      }
    }

    /**
     * extract version field as a hex char from raw Guid bytes
     *
     * @return version char
     */
    @JsonIgnore
    public short getVersion() {
      return (short) (bguid[HEADER_POS] & BYTE_MASK);
    }

    /**
     * Build from String key
     *
     * @throws InvalidArgumentRuntimeException if the isSource is incorrect
     */
    private Guid(final GuidFactory factory, final String idsource) {
      guidFactory = factory;
      bguid = new byte[Math.min(idsource.length(), MAX_SIZE)];
      setString(idsource);
      validateVersion();
    }

    /**
     * Internal function
     *
     * @return this
     */
    @JsonSetter("id")
    Guid setString(final String idsource) {
      if (idsource == null) {
        throw new InvalidArgumentRuntimeException("Empty argument");
      }
      final String id = idsource.trim();
      if (id.startsWith(ARK)) {
        String ids = id;
        ids = ids.substring(ARK.length());
        final int separator = ids.indexOf('/');
        if (separator <= 0) {
          throw new InvalidArgumentRuntimeException(ATTEMPTED_TO_PARSE_MALFORMED_ARK_GUID + id);
        }
        long tenantId;
        try {
          tenantId = Long.parseLong(ids.substring(0, separator));
        } catch (final NumberFormatException e) {
          throw new InvalidArgumentRuntimeException(ATTEMPTED_TO_PARSE_MALFORMED_ARK_GUID + id);
        }
        // Get HEADER as Base64
        ids = ids.substring(separator + 1);
        guidFactory.setFromHeader(ids);
        // BASE32
        ids = ids.substring(HEADER_64_SIZE);
        try {
          final byte[] base32 = BaseXx.getFromBase32(ids);
          if (base32.length != guidFactory.keySize - guidFactory.tenantSize - HEADER_SIZE) {
            throw new InvalidArgumentRuntimeException(ATTEMPTED_TO_PARSE_MALFORMED_ARK_GUID + id);
          }
          System.arraycopy(guidFactory.getHeader(), 0, bguid, HEADER_POS, HEADER_SIZE);
          // Guid Tenant
          for (int pos = TENANT_POS + guidFactory.tenantSize - 1; pos >= TENANT_POS; pos--) {
            bguid[pos] = (byte) (tenantId & BYTE_MASK);
            tenantId >>>= BYTE_SIZE;
          }
          // BASE32
          System.arraycopy(base32, 0, bguid, guidFactory.platformPos,
                           guidFactory.keySize - guidFactory.tenantSize - HEADER_SIZE);
        } catch (final IllegalArgumentException e) {
          throw new InvalidArgumentRuntimeException("Invalid Base32", e);
        }
        return this;
      }
      // Read Base 64 for header
      guidFactory.setFromHeader(id);
      System.arraycopy(guidFactory.getHeader(), 0, bguid, HEADER_POS, HEADER_SIZE);
      final String ids = id.substring(HEADER_64_SIZE);
      final int len = id.length();
      try {
        if (len == guidFactory.key16Size) {
          // HEXA BASE16
          final byte[] bytes = BaseXx.getFromBase16(ids);
          System.arraycopy(bytes, 0, bguid, TENANT_POS, guidFactory.keySize - HEADER_SIZE);
        } else if (len == guidFactory.key32Size) {
          // BASE32
          final byte[] bytes = BaseXx.getFromBase32(ids);
          System.arraycopy(bytes, 0, bguid, TENANT_POS, guidFactory.keySize - HEADER_SIZE);
        } else if (len == guidFactory.key64Size) {
          // BASE64
          final byte[] bytes = BaseXx.getFromBase64(ids);
          System.arraycopy(bytes, 0, bguid, TENANT_POS, guidFactory.keySize - HEADER_SIZE);
        } else {
          throw new InvalidArgumentRuntimeException("Attempted to parse malformed Guid: (" + len + ") " + id);
        }
      } catch (final IllegalArgumentException e) {
        throw new InvalidArgumentRuntimeException("Attempted to parse malformed Guid: " + id, e);
      }
      return this;
    }

    private Guid() {
      guidFactory = new GuidFactory();
      bguid = new byte[MAX_SIZE];
    }

    /**
     * Constructor that generates a new Guid using the default Tenant id, Process id,
     * Platform Id
     */
    private Guid(final GuidFactory factory) {
      this(factory, factory.tenantId, factory.platformId);
    }

    /**
     * Constructor that generates a new Guid using the current process id and
     * timestamp
     *
     * @param tenantId tenant id between -2^(c*8-1) and 2^(c*8-1)-1
     * @param platformId platform Id between -2^(p*8-1) and 2^(p*8-1)-1
     */
    private Guid(final GuidFactory factory, final long tenantId, final long platformId) {
      // atomically
      guidFactory = factory;
      bguid = new byte[guidFactory.keySize];
      final long time = System.currentTimeMillis();
      final int count = getNewCounter(guidFactory.maxCounter);
      // 3 bytes = Version + Encoding (24)
      final byte[] bytes = guidFactory.getHeader();
      System.arraycopy(bytes, 0, bguid, HEADER_POS, HEADER_SIZE);

      // Tenant
      long value = tenantId;
      for (int pos = TENANT_POS + guidFactory.tenantSize - 1; pos >= TENANT_POS; pos--) {
        bguid[pos] = (byte) (value & BYTE_MASK);
        value >>>= BYTE_SIZE;
      }

      // Platform
      value = platformId;
      for (int pos = guidFactory.platformPos + guidFactory.platformSize - 1; pos >= guidFactory.platformPos;
           pos--) {
        bguid[pos] = (byte) (value & BYTE_MASK);
        value >>>= BYTE_SIZE;
      }

      // JVMPID
      int ivalue = guidFactory.pid;
      for (int pos = guidFactory.pidPos + guidFactory.pidSize - 1; pos >= guidFactory.pidPos; pos--) {
        bguid[pos] = (byte) (ivalue & BYTE_MASK);
        ivalue >>>= BYTE_SIZE;
      }

      // Timestamp
      value = time;
      for (int pos = guidFactory.timePos + guidFactory.timeSize - 1; pos >= guidFactory.timePos; pos--) {
        bguid[pos] = (byte) (value & BYTE_MASK);
        value >>>= BYTE_SIZE;
      }

      // Counter against collision
      ivalue = count;
      for (int pos = guidFactory.counterPos + guidFactory.counterSize - 1; pos >= guidFactory.counterPos;
           pos--) {
        bguid[pos] = (byte) (ivalue & BYTE_MASK);
        ivalue >>>= BYTE_SIZE;
      }
    }

    private static synchronized int getNewCounter(final int max) {
      if (COUNTER.compareAndSet(max, MIN_COUNTER)) {
        return max;
      } else {
        return COUNTER.getAndIncrement();
      }
    }

    /**
     * Constructor that generates a new Guid using the current process id,
     * Platform Id and timestamp with no tenant
     *
     * @param tenantId tenant id between -2^(c*8-1) and 2^(c*8-1)-1
     *
     * @throws InvalidArgumentRuntimeException if any of the argument are out
     *     of range
     */
    private Guid(final GuidFactory factory, final long tenantId) {
      this(factory, tenantId, factory.platformId);
    }

    /**
     * @return the KeySize
     */
    @JsonIgnore
    public int getKeySize() {
      return guidFactory.getKeySize();
    }

    /**
     * @return the Base64 representation
     */
    @JsonIgnore
    public String toBase64() {
      return guidFactory.getHeaderEncoded() +
             BaseXx.getBase64(bguid, HEADER_SIZE, guidFactory.keySize - HEADER_SIZE);
    }

    /**
     * @return the Hexadecimal representation
     */
    @JsonIgnore
    public String toHex() {
      return guidFactory.getHeaderEncoded() +
             BaseXx.getBase16(bguid, HEADER_SIZE, guidFactory.keySize - HEADER_SIZE);
    }

    /**
     * @return the Ark representation of this Guid
     */
    @JsonIgnore
    public String toArk() {
      return new StringBuilder(ARK).append(getTenantId()).append('/').append(toArkName()).toString();
    }

    /**
     * @return the Tenant Id of Guid from which it belongs to (default being 0)
     */
    @JsonIgnore
    public long getTenantId() {
      long value = 0;
      for (int i = 0; i < guidFactory.tenantSize; i++) {
        value <<= BYTE_SIZE;
        value |= bguid[TENANT_POS + i] & BYTE_MASK;
      }
      return value;
    }

    /**
     * @return the Ark Name part of Ark representation
     */
    @JsonIgnore
    public String toArkName() {
      return guidFactory.getHeaderEncoded() + BaseXx.getBase32(bguid, guidFactory.platformPos,
                                                               guidFactory.keySize - guidFactory.tenantSize -
                                                               HEADER_SIZE);
    }

    /**
     * @return the String representation of this Guid
     */
    @JsonGetter("id")
    public String getId() {
      return toString();
    }

    @Override
    public String toString() {
      return toBase32();
    }

    /**
     * @return the Base32 representation (default of toString)
     */
    @JsonIgnore
    public String toBase32() {
      return guidFactory.getHeaderEncoded() +
             BaseXx.getBase32(bguid, HEADER_SIZE, guidFactory.keySize - HEADER_SIZE);
    }

    /**
     * Extract Platform id
     *
     * @return the Platform id
     */
    @JsonIgnore
    public long getPlatformId() {
      long value = 0;
      for (int i = 0; i < guidFactory.platformSize; i++) {
        value <<= BYTE_SIZE;
        value |= bguid[guidFactory.platformPos + i] & BYTE_MASK;
      }
      return value;
    }

    /**
     * Extract PID
     *
     * @return the PID
     */
    @JsonIgnore
    public int getProcessId() {
      int value = 0;
      for (int i = 0; i < guidFactory.pidSize; i++) {
        value <<= BYTE_SIZE;
        value |= bguid[guidFactory.pidPos + i] & BYTE_MASK;
      }
      return value;
    }

    @Override
    @JsonIgnore
    public int hashCode() {
      return Arrays.hashCode(getBytes());
    }

    /**
     * copy the uuid of this Guid, so that it can't be changed, and return it
     *
     * @return raw byte array of Guid
     */
    @JsonIgnore
    public byte[] getBytes() {
      return Arrays.copyOf(bguid, guidFactory.keySize);
    }

    @Override
    public boolean equals(final Object o) {
      if (!(o instanceof Guid)) {
        return false;
      }
      return this == o || Arrays.equals(getBytes(), ((Guid) o).getBytes());
    }

    @Override
    public int compareTo(final Guid guid) {
      final long id = getTenantId();
      final long id2 = guid.getTenantId();
      if (id != id2) {
        return id < id2? -1 : 1;
      }
      final long ts = getTimestamp();
      final long ts2 = guid.getTimestamp();
      if (ts == ts2) {
        final int ct = getCounter();
        final int ct2 = guid.getCounter();
        if (ct == ct2) {
          // then all must be equals, else whatever
          return Arrays.equals(getBytes(), guid.getBytes())? 0 : -1;
        }
        // Cannot be equal
        return ct < ct2? -1 : 1;
      }
      // others as ProcessId or Platform are unimportant in comparison
      return ts < ts2? -1 : 1;
    }

    /**
     * Extract timestamp and return as long
     *
     * @return millisecond UTC timestamp from generation of the Guid, or -1 for
     *     unrecognized format
     */
    @JsonIgnore
    public long getTimestamp() {
      if (getVersion() != VERSION) {
        return -1;
      }
      long time = 0;
      for (int i = 0; i < guidFactory.timeSize; i++) {
        time <<= BYTE_SIZE;
        time |= bguid[guidFactory.timePos + i] & BYTE_MASK;
      }
      return time;
    }

    /**
     * @return the associated counter against collision value
     */
    @JsonIgnore
    public int getCounter() {
      int count = 0;
      for (int i = 0; i < guidFactory.counterSize; i++) {
        count <<= BYTE_SIZE;
        count |= bguid[guidFactory.counterPos + i] & BYTE_MASK;
      }
      return count;
    }

    @JsonIgnore
    public String getJson() throws JsonProcessingException {
      return JsonHandler.writeAsString(this);
    }
  }
}
