# JSR 331 OR-Tools Constraint Adapter

This module connects the JSR 331 constraint-programming API directly to [Google OR-Tools CP-SAT](https://developers.google.com/optimization/cp/cp_solver). It is separate from `jsr331-ortools`, which provides the JSR 331 linear/MIP adapter through OR-Tools `MPSolver`.

The adapter supports integer and Boolean variables, sparse domains, linear constraints, logical/reified constraints, all-different, element, cardinality and global-cardinality constraints, arithmetic expressions, optimization, search limits, and solution iteration. Version 3.0.0 requires JDK 21.

OR-Tools CP-SAT is an integer solver, so this adapter does not implement `VarReal`. Imported XML models are also not supported.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-ortools-cp</artifactId>
  <version>3.0.0</version>
</dependency>
```

OR-Tools and its native libraries are installed automatically through Maven.

## Build and test

```shell
mvn clean test
```

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent` and `jsr331` first. Then run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.
