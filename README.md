FB GUID
=======

FB GUID provides several efficient and robust implementations of _UUID_, 
one being also **GUID**:

* UUID various implementations
  * `IntegerUuid` reserved to minimalist UUID as Integer (so up to 2^32 values 
    with 4 bytes)
  * `LongUuid` reserved for local usages (as local UUID) as Long (so up to 2⁶4 
    values) using PID of Java Process, Timestamp (limited to 35 years rolling)
    and a counter to prevent millisecond collisions (total being 8 bytes)
  * `GUID` for all usages, local and distributed UUID (Global UUID as GUID), with
    Tenant Id, Platform Id (based on specified value or MAC Address), PID of 
    Java process, Timestamp (up to 925 years rolling) and and a counter to 
    prevent millisecond collisions (total being 21 bytes) 
* Simple integration with Jackon for the `GUID`
* Base 64, 32, 16 and ARK representations

## Usage

To create one GUID:
```java
// for simple GUID with no Tenant and default Platform Id
GUID guid = new GUID();
// for simple GUID with one Tenant (0 <= long <= 2^30-1) 
// and default Platform Id
GUID guid = new GUID(tenantId);
// for simple GUID with one Tenant (0 <= long <= 2^30) 
// and a Platform Id (0 <= long <= 2^31-1)
GUID guid = new GUID(tenantId, platformId);
```
Once the GUID is generated, one can get whatever representation he/she wants:
```java
// Gives the Ark://tenant/arkName
guid.toArk();
// which can be used to get back the GUID
GUID guid2 = new GUID(guid.toArk());
// As HEXADECIMAL (42 bytes)
guid.toHex();
// As 32 based (34 bytes) (default as toString())
guid.toBase32();
guid.toString();
// As 64 based (28 bytes)
guid.toBase64();
// As a byte array (21 bytes)
guid.getBytes();
// for simple GUID with one Tenant (0 <= long <= 2^30) 
// and a Platform Id (0 <= long <= 2^31-1)
GUID guid = new GUID(tenantId, platformId);
```
You can compare GUID between them (as for the IntegerUuid and the LongUuid):
```java
GUID guid = new GUID();
GUID guid2 = new GUID();
guid.compareTo(guid2); // < 0 since guid generated before guid2
// Comparison is: tenant first, then timestamp, then counter 
```

## Dependencies

Only Jackson and Guava.

## License

This project is distributed under the terms of the [Apache 2.0](LICENSE-2.0.txt) License

