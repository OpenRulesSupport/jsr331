# JSR 331 SCIP Adapter

This project connects the [JSR 331](https://jsr331.org/) linear API directly to `scip-mip-java`, a Java interface to the [SCIP Optimization Suite](https://github.com/scipopt/scip). Version 3.0.0 requires JDK 21.

The `scip-mip-java` dependency packages the supported native SCIP runtime, so a separate `scip.exe` installation is not required.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-scip</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build

```shell
mvn clean package
```

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent`, `jsr331`, `jsr331-linear`, and `scip-mip-java` first. Then run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.

## Acknowledgment

This JSR 331 adapter was developed based on [SCIP Optimization Suite](https://github.com/scipopt/scip) linear/MIP solver with the assistance of OpenAI Codex and reviewed and tested by Jacob Feldman.
