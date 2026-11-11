# JSR 331 HiGHS adapter

This module implements the JSR 331 linear-solver class name
`javax.constraints.linear.impl.LinearSolver` using the
[HiGHS](https://highs.dev/) LP/MIP solver.

The adapter translates the common JSR 331 linear model directly to the HiGHS C
API through JNA. Ordinary solves do not create temporary MPS or solution files
and do not start an external process.

## Supported platforms

Version 3.0.0 requires JDK 21 and includes HiGHS 1.9.0 native libraries for:

- Windows x86-64
- Linux x86-64
- macOS Apple Silicon (arm64)

No separate solver installation is required on these platforms. To use another
compatible HiGHS build or platform, set:

```text
-Djsr331.highs.library=/absolute/path/to/highs/library
```

## Supported model features

- bounded integer variables (`Var`) and continuous variables (`VarReal`)
- mixed integer/continuous linear constraints
- `=`, `==`, `is`, `<=`, and `>=`
- strict `<` and `>` when the expression and bound are integral
- minimization and maximization
- JSR 331 global solve time limits
- imported MPS models through `solve(File)`

`!=`, `<>`, and `is not` are rejected because they are disjunctive rather than
a single linear constraint. Strict inequalities over continuous expressions are
also rejected because an LP has a closed feasible region.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-highs</artifactId>
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
variables, strict integral inequalities, and imported MPS models.

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent`, `jsr331`, and `jsr331-linear` first.
Then run:

```shell
mvn clean deploy -Prelease
```

The Maven artifact is `com.javasolver:jsr331-highs:3.0.0`.

## Acknowledgment

This JSR 331 adapter was developed based on [HiGHS](https://highs.dev/) LP/MIP solver with the assistance of OpenAI Codex and reviewed and tested by Jacob Feldman.
