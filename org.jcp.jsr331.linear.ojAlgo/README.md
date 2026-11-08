# JSR 331 ojAlgo adapter

This module is a replacement for `org.jcp.jsr331.linear.ojAlgo`, updated for
[ojAlgo 57.4.0](https://github.com/optimatika/ojAlgo/releases/tag/v57.4.0).
It implements the class name expected by the JSR 331 linear runtime:

```text
javax.constraints.linear.impl.LinearSolver
```

Unlike the historical adapter, this implementation translates the JSR 331
linear model directly to ojAlgo's `ExpressionsBasedModel`. It does not create
temporary MPS and solution files or invoke an external process.

## Supported model features

- bounded integer variables (`Var`) and continuous variables (`VarReal`)
- mixed integer/continuous linear constraints
- `=`, `==`, `is`, `<=`, and `>=`
- strict `<` and `>` when the entire expression and right-hand bound are integral
- minimization and maximization
- JSR 331 global solve time limits, passed to ojAlgo in milliseconds
- imported MPS models through the inherited `solve(File)` entry point

`!=`, `<>`, and `is not` are rejected because they are disjunctive rather than
a single linear constraint. Strict inequalities over continuous expressions are
also rejected because they cannot be represented exactly by a closed LP feasible
region.

## Maven dependency

Version 3.0.0 requires JDK 21.

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-ojalgo</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build and test

```shell
mvn test
```

The tests cover integer maximization, continuous minimization, infeasibility,
derived objective variables, mixed integer/continuous constraints, and strict
integral inequalities.

### JSR 331 mixed-domain helper note

The shared `jsr331-linear` implementation creates an invalid default
domain in `scalProd(double[], ConstrainedVariable[])`. For a mixed-domain
objective, declare a bounded `VarReal` and link it with the corresponding
`post(double[], ConstrainedVariable[], "=", objective)` overload. The adapter
fully supports the resulting mixed model; the test suite includes this pattern.

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent`, `jsr331`, and `jsr331-linear` first. Then run:

```shell
mvn clean deploy -Prelease
```

The Maven artifact is `com.javasolver:jsr331-ojalgo:3.0.0`. MvnRepository indexes releases after they become available from Maven Central.
