# JSR 331 API

[JSR 331, Java Constraint Programming API](https://jsr331.org/), provides a standard Java API for modeling and solving constraint satisfaction and optimization problems with interchangeable solvers.

This module contains:

- `javax.constraints`: the standard constraint programming and linear solver interfaces
- `javax.constraints.impl`: common implementation classes shared by solver adapters

Version 3.0.0 requires JDK 21.

## Maven dependency

After version 3.0.0 is published to Maven Central, add:

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331</artifactId>
  <version>3.0.0</version>
</dependency>
```

A solver adapter such as `jsr331-choco`, `jsr331-constrainer`, `jsr331-jacop`, `jsr331-jsetl`, or `jsr331-ortools-cp` is also required to solve a model.

## Build and test

```shell
mvn clean test
```

## Publishing to Maven Central

Publish `com.javasolver:jsr331-parent:3.0.0` first. Configure the `central` server credentials in Maven `settings.xml`, ensure the signing key's public key is available from a supported public key server, and run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.
