# JSR 331 Gurobi Adapter

This project provides a [JSR 331](https://jsr331.org/) command-line adapter for the commercial [Gurobi Optimizer](https://www.gurobi.com/solutions/gurobi-optimizer/). Version 3.0.0 requires JDK 21.

## Installation

Install and license Gurobi, then ensure that `gurobi_cl` (`gurobi_cl.exe` on Windows) is available on `PATH`. The executable can also be selected with the `LP_SOLVER_EXE` Java system property. Gurobi itself is not included in this Maven artifact.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-gurobi</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build

```shell
mvn clean package
```

Runtime solving requires a valid Gurobi installation and license.

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent`, `jsr331`, and `jsr331-linear` first. Then run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.
