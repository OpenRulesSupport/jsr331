# JSR 331 GLPK Adapter

This project provides a [JSR 331](https://jsr331.org/) command-line adapter for the open source [GNU Linear Programming Kit](https://www.gnu.org/software/glpk/). Version 3.0.0 requires JDK 21.

## Installation

Install GLPK and ensure that `glpsol` (`glpsol.exe` on Windows) is available on `PATH`. The executable can also be selected with the `LP_SOLVER_EXE` Java system property.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-glpk</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build

```shell
mvn clean package
```

Runtime solving requires a working GLPK installation.

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent`, `jsr331`, and `jsr331-linear` first. Then run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.
