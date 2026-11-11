# JSR-331 adapter for MaxiCP

This module provides the standard JSR-331 implementation packages
(`javax.constraints.impl`) backed by [MaxiCP](https://github.com/aia-uclouvain/maxicp).
It is intended to live beside the other adapters in the
[OpenRulesSupport/jsr331](https://github.com/OpenRulesSupport/jsr331) repository
under the directory `org.jcp.jsr331.maxicp`.

## Requirements

- Java 21 (required by MaxiCP 1.0.0)
- Maven 3.9 or later
- `com.javasolver:jsr331:3.0.0`
- `org.maxicp:maxicp:1.0.0`

## Build

```shell
mvn clean test
```

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-maxicp</artifactId>
  <version>3.0.0</version>
</dependency>
```

When building from a JSR-331 source checkout, install the parent and common API
first:

```shell
mvn -f ../org.jcp.jsr331.parent/pom.xml install
mvn -f ../org.jcp.jsr331/pom.xml install
mvn clean test
```

## Example

```java
import javax.constraints.*;

Problem p = ProblemFactory.newProblem("send-more-money");
Var x = p.variable("x", 0, 10);
Var y = p.variable("y", 0, 10);
p.post(x.plus(y), "=", 10);
p.postAllDifferent(new Var[] {x, y});

Solution solution = p.getSolver().findSolution();
if (solution != null) {
    solution.log();
}
```

No application code refers to MaxiCP classes; selecting this adapter on the
runtime classpath is enough for `ProblemFactory` to instantiate it.

## Implemented surface

- bounded and sparse integer variables
- Boolean variables and reified linear comparisons
- arithmetic views: addition, subtraction, multiplication, division modeling,
  absolute value, sums, and scalar products
- linear relations with all six JSR-331 operators
- element constraints over constant and variable arrays
- all-different
- cardinality and global cardinality
- logical AND, OR, negation, and implication
- depth-first search with JSR-331 variable/value selectors
- first solution, solution iteration/all-solutions, time limits, and the
  standard JSR-331 optimization loop
- basic custom propagator callbacks

## Deliberate limitations

- JSR-331 real and set variables are not mapped to MaxiCP in this first module.
- Direct global constraints such as all-different are postable but are not
  reified; calling `asBool()` on one throws an explanatory exception.
- JSR-331 domain type hints are accepted but MaxiCP chooses its own internal
  representation.
- Tracing hooks are currently no-ops; `logStats()` reports MaxiCP search
  statistics.

These limitations match the integer-focused scope of the older JSR-331 Choco
adapter while leaving room for later scheduling and sequence-variable
extensions specific to MaxiCP.

## Publishing to Maven Central

Publish `jsr331-parent` and `jsr331` version 3.0.0 first. Then run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.

## Acknowledgment

This JSR 331 adapter was developed based on [MaxiCP](https://github.com/aia-uclouvain/maxicp) CP Solver 1.0.0 with the assistance of OpenAI Codex and reviewed and tested by Jacob Feldman.
