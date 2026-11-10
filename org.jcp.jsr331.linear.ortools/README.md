# JSR 331 Google OR-Tools adapter

This module implements the JSR 331 linear-solver class name
`javax.constraints.linear.impl.LinearSolver` using
[Google OR-Tools](https://github.com/google/or-tools).

The adapter translates the common JSR 331 linear model directly to the OR-Tools
`MPSolver` API. It does not create temporary model or solution files and does
not start an external process.

## Solver backend

The default backend is the SCIP solver bundled with OR-Tools. Select a different
installed OR-Tools `MPSolver` backend with:

```text
-Djsr331.ortools.solver=CBC
```

Examples include `SCIP`, `CBC`, `SAT`, `GLOP`, and `PDLP`. An LP-only backend
such as GLOP or PDLP cannot be used when the JSR 331 model contains integer
variables.

## Supported platforms

Version 3.0.0 requires JDK 21 and uses OR-Tools 9.15.6755. The official Maven
artifact supplies native libraries for:

- Windows x86-64
- Linux x86-64 and arm64
- macOS x86-64 and Apple Silicon

No separate solver installation is required for the default backend.

## Supported model features

- bounded integer variables (`Var`) and continuous variables (`VarReal`)
- mixed integer/continuous linear constraints
- `=`, `==`, `is`, `<=`, and `>=`
- strict `<` and `>` when the expression and bound are integral
- minimization and maximization
- JSR 331 global solve time limits

`!=`, `<>`, and `is not` are rejected because they are disjunctive rather than
a single linear constraint. Strict inequalities over continuous expressions are
also rejected because an LP has a closed feasible region.

The OR-Tools Java `MPSolver` API exports MPS models but does not import them, so
the inherited `solve(File)` entry point is not supported by this adapter.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-ortools</artifactId>
  <version>3.0.0</version>
</dependency>
```

Only one JSR 331 solver adapter should be present on the application classpath.

## Build and test

```shell
mvn test
```

The tests cover native loading, integer maximization, continuous minimization,
mixed integer/continuous optimization, infeasibility, derived objective
variables, strict integral inequalities, and backend validation.

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent`, `jsr331`, and `jsr331-linear` first.
Then run:

```shell
mvn clean deploy -Prelease
```

The Maven artifact is `com.javasolver:jsr331-ortools:3.0.0`.
