# JSR 331 CPLEX Adapter

This project provides a [JSR 331](https://jsr331.org/) command-line adapter for [IBM ILOG CPLEX Optimization Studio](https://www.ibm.com/products/ilog-cplex-optimization-studio). Version 3.0.0 requires JDK 21.

## Installation

Install and license CPLEX, then ensure that `cplex` (`cplex.exe` on Windows) is available on `PATH`. The executable can also be selected with the `LP_SOLVER_EXE` Java system property. CPLEX itself is not included in this Maven artifact.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-cplex</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build

```shell
mvn clean package
```

Runtime solving requires a valid CPLEX installation and license.

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent`, `jsr331`, and `jsr331-linear` first. Then run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.
