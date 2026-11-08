# JSR 331 lp_solve Adapter

This project provides a [JSR 331](https://jsr331.org/) command-line adapter for the open source [lp_solve](https://sourceforge.net/projects/lpsolve/) mixed-integer linear programming solver. Version 3.0.0 requires JDK 21.

## Installation

Install lp_solve and ensure that `lp_solve` (`lp_solve.exe` on Windows) is available on `PATH`. The executable can also be selected with the `LP_SOLVER_EXE` Java system property.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-lpsolve</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build

```shell
mvn clean package
```

Running solver-dependent examples requires a working lp_solve executable.

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent`, `jsr331`, and `jsr331-linear` first. Then run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.
