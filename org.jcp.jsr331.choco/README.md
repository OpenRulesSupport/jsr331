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

## Build and test

From a checkout where the JSR-331 parent and API modules are available to Maven:

```shell
mvn -f org.jcp.jsr331.choco/pom.xml test
```

`Choco6SmokeTest` is a dependency-free executable integration test that covers core
constraints, solution iteration, arithmetic expressions, and optimization.

Custom JSR-331 propagators, XML import/export, real variables, and set variables remain
outside this adapter's native feature set.
