# JSR 331 Gurobi Adapter

This project provides a direct Java adapter from [JSR 331](https://jsr331.org/) to the commercial [Gurobi Optimizer](https://www.gurobi.com/solutions/gurobi-optimizer/). It supports linear and mixed-integer models and requires JDK 21.

The adapter creates variables, constraints, and objectives through Gurobi's Java API. Normal JSR 331 solves remain in memory: they do not create intermediate MPS or solution files. The explicit `solve(File)` API still accepts model formats supported by Gurobi, including LP and MPS.

## Installation and license

The Maven dependency downloads Gurobi's full Java package, including native solver libraries for supported Windows, Linux, and macOS platforms. A valid Gurobi license is still required. Configure the license using the method appropriate for your Gurobi installation or license service.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-gurobi</artifactId>
  <version>3.0.0</version>
</dependency>
```

`jsr331-gurobi` depends on `com.gurobi:gurobi:13.0.3`, which Gurobi publishes to Maven Central.

## Build and test

```shell
mvn clean test
```

The integration tests run when a valid Gurobi license is available. Otherwise they are skipped.

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent`, `jsr331`, and `jsr331-linear` first. Then run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.

## Acknowledgment

This JSR 331 adapter was developed based on [Gurobi Optimizer](https://www.gurobi.com/solutions/gurobi-optimizer/) with the assistance of OpenAI Codex and reviewed and tested by Jacob Feldman.
