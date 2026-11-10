# JSR 331 GLPK adapter

This module implements the JSR 331 linear-solver class name
`javax.constraints.linear.impl.LinearSolver` using the callable-library API of
the open-source [GNU Linear Programming Kit](https://www.gnu.org/software/glpk/).

The adapter translates the common JSR 331 linear model directly to GLPK through
JNA. Ordinary solves no longer create temporary MPS and solution files or start
the `glpsol` executable.

## GLPK installation

Version 3.0.0 requires JDK 21 and a compatible GLPK native library. GLPK 4.65 is
the tested version.

Place the GLPK callable library on the operating system's native library path,
or specify its absolute path:

```text
-Djsr331.glpk.library=C:\lp\glpk-4.65\w64\glpk_4_65.dll
```

If `LP_SOLVER_EXE` points to `glpsol`, the adapter also looks for the native
library beside that executable. On Windows the expected default name is
`glpk_4_65.dll`; on Linux it is `libglpk.so`, and on macOS `libglpk.dylib`.

## Supported model features

- bounded integer variables (`Var`) and continuous variables (`VarReal`)
- mixed integer/continuous linear constraints
- `=`, `==`, `is`, `<=`, and `>=`
- strict `<` and `>` when the expression and bound are integral
- minimization and maximization
- JSR 331 global solve time limits
- explicit MPS imports through `solve(File)`

`!=`, `<>`, and `is not` are rejected because they are disjunctive rather than
a single linear constraint. Strict inequalities over continuous expressions are
also rejected because an LP has a closed feasible region.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-glpk</artifactId>
  <version>3.0.0</version>
</dependency>
```

Only one JSR 331 solver adapter should be present on the application classpath.

## Build and test

On the tested Windows installation:

```shell
mvn test -Djsr331.glpk.library=C:\lp\glpk-4.65\w64\glpk_4_65.dll
```

The tests cover native loading, integer maximization, continuous minimization,
mixed integer/continuous optimization, infeasibility, strict integral
inequalities, and imported MPS models.

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent`, `jsr331`, and `jsr331-linear` first.
Then run:

```shell
mvn clean deploy -Prelease
```

The Maven artifact is `com.javasolver:jsr331-glpk:3.0.0`.
