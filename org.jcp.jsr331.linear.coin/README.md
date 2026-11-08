# JSR 331 COIN-OR CLP Adapter

This project provides a [JSR 331](https://jsr331.org/) command-line adapter for the open source [COIN-OR CLP](https://github.com/coin-or/Clp) linear and mixed-integer solver. Version 3.0.0 requires JDK 21.

## Installation

Install the CLP command-line executable and ensure that `clp` (`clp.exe` on Windows) is available on `PATH`. The executable can also be selected with the `LP_SOLVER_EXE` Java system property.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-coin</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build

```shell
mvn clean package
```

Solver-dependent tests require a working CLP executable.

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent`, `jsr331`, and `jsr331-linear` first. Then run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.
