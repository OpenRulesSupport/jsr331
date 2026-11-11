# JSR 331 CLP Adapter

This project connects the [JSR 331](https://jsr331.org/) linear API directly to [clp-mip-java](https://github.com/OpenRulesSupport/jsr331/tree/master/clp-mip-java), which is based on the COIN-OR CLP solver and adds mixed-integer support. Version 3.0.0 requires JDK 21.

The `clp-mip-java` dependency packages native CLP libraries for supported 64-bit Windows, Linux, Intel macOS, and Apple Silicon systems, so no separate solver installation is normally required.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-clp</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build

```shell
mvn clean package
```

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent`, `jsr331`, `jsr331-linear`, and `clp-mip-java` first. Then run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.

## Acknowledgment

This JSR 331 adapter was developed based on [COIN-OR CLP solver](https://github.com/OpenRulesSupport/jsr331/tree/master/clp-mip-java) with the assistance of OpenAI Codex and reviewed and tested by Jacob Feldman.
