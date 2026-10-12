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

## Build and test

Prerequisites: JDK 11 or newer and Maven 3.8 or newer.

```shell
mvn test
```

The tests cover integer maximization, continuous minimization, infeasibility,
derived objective variables, mixed integer/continuous constraints, and strict
integral inequalities.

### JSR 331 mixed-domain helper note

The shared `jsr331-linear` 2.4.0 implementation creates an invalid default
domain in `scalProd(double[], ConstrainedVariable[])`. For a mixed-domain
objective, declare a bounded `VarReal` and link it with the corresponding
`post(double[], ConstrainedVariable[], "=", objective)` overload. The adapter
fully supports the resulting mixed model; the test suite includes this pattern.

## Add to the JSR 331 reactor

Copy this directory into the root of the
[`OpenRulesSupport/jsr331`](https://github.com/OpenRulesSupport/jsr331) checkout
and add this module to the root `pom.xml`:

```xml
<module>org.jcp.jsr331.linear.ojAlgo</module>
```

The resulting Maven artifact is `com.javasolver:jsr331-ojalgo:2.4.0`.
