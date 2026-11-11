# JSR-331 adapter for Choco Solver 6

This module implements the [JSR-331 Java Constraint Programming API](https://jsr331.org/)
on top of [Choco Solver](https://github.com/chocoteam/choco-solver/) 6.0.1.

The adapter uses Choco's current single-model API (`Model`, `IntVar`, native
constraints, and `Solver`) and no longer requires a locally installed Choco 2 JAR.
Maven resolves Choco from Maven Central:

```xml
<dependency>
  <groupId>org.choco-solver</groupId>
  <artifactId>choco-solver</artifactId>
  <version>6.0.1</version>
</dependency>
```

## Implemented functionality

- bounded and enumerated integer variables and boolean variables
- linear, arithmetic, logical, element, all-different, cardinality, and global-cardinality constraints
- sum and scalar-product expressions
- native Choco search, JSR-331 variable/value selector mappings, solution iteration, and optimization
- Choco environment-backed reversible integers

The adapter preserves the standard `javax.constraints.impl` package names, so existing
JSR-331 applications can select it without changing their model code.

Version 3.0.0 requires JDK 21.

## Maven dependency

After version 3.0.0 is published to Maven Central, add:

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-choco</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build and test

From a checkout where the JSR-331 parent and API modules are available to Maven:

```shell
mvn -f org.jcp.jsr331.choco/pom.xml test
```

`Choco6SmokeTest` is a dependency-free executable integration test that covers core
constraints, solution iteration, arithmetic expressions, and optimization.

Custom JSR-331 propagators, XML import/export, real variables, and set variables remain
outside this adapter's native feature set.

## Publishing to Maven Central

Publish `jsr331-parent` and `jsr331` version 3.0.0 first. With the Maven Central credentials and GPG key configured, run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.

## Acknowledgment

This JSR 331 adapter was developed based on [Choco Solver](https://github.com/chocoteam/choco-solver/) 6.0.1 with the assistance of OpenAI Codex and reviewed and tested by Jacob Feldman.
