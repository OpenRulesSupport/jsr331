# clp-mip-java

Java interface for the [Coin-OR CLP linear solver](https://github.com/coin-or/Clp the Coin-OR CBC solver with mixed-integer programming support, optimized for fast model building and fast re-solves.

## Overview

The interface provides a number of useful features for Java programmers who want to build either large models or apply decomposition strategies that require many re-solves. The interface has undergone several rounds of testing and profiling outside of unit tests.

#### Compact model creation using builders

Variables, constraints, and linear expressions are available as Java objects to make model building as easy as possible.

Decision variables as well as their bounds and objective coefficients can be created in a single line. For example, ![x \in [-3,3]](https://latex.codecogs.com/gif.latex?x%20%5Cin%20%5B-3%2C3%5D) and ![y \in \mathbb{R}](https://latex.codecogs.com/gif.latex?y%20%5Cin%20%5Cmathbb%7BR%7D), would become
```
CLPVariable x = model.addVariable().lb(-3).ub(3);
CLPVariable y = model.addVariable().free();
```
Since there is no operator overloading in Java, algebraic formulations are not possible. To avoid excessive use of setters, constraints can be built by using a series of *add* statements. For example, ![3x + 4y \leq 10](https://latex.codecogs.com/gif.latex?3x&plus;4y%20%5Cleq%2010) can be expressed as
```
model.createExpression().add(3,x).add(4,y).leq(10);
```
The *add* statements also accept vectors of variables and coefficients. See the [original clp-java Javadoc](https://quantego.github.io/clp-java) for further reference.

#### Mixed-integer models

Variables can be declared integer or binary with the same builder API. Calling
`solve()`, `minimize()`, or `maximize()` automatically runs branch-and-bound
when the model contains an integer variable; CLP solves each node relaxation.

```java
CLP model = new CLP();
CLPVariable x = model.addVariable().binary().obj(6);
CLPVariable y = model.addVariable().integer().bounds(0, 3).obj(5);
model.createExpression().add(4, x).add(3, y).leq(7);

CLP.STATUS status = model.maximize();
System.out.println(status + ": " + model.getObjectiveValue());
```

The MIP search can be controlled with `mipMaxNodes`, `mipIntegerTolerance`,
`mipAbsoluteGap`, `mipRelativeGap`, and the existing `maxSeconds` setting.
Search statistics are available through `getMipNodeCount`, `getMipBestBound`,
and `getMipGap`. Use `createMIPFromMPS(file)` to honor integer markers in an
MPS file; `createFromMPS(file)` retains its historical LP-relaxation behavior.

This implementation provides portable basic MILP support without adding new
native libraries. Its branch-and-bound design was informed by Coin-OR CBC, but
it is not a replacement for CBC's full branch-and-cut engine. It does not include
CBC's cuts, primal heuristics, SOS constraints, callbacks,
or parallel search, and is intended for small and medium integer models.

#### Fast model building

Chunks of a model are buffered in heap for model building before being sent to the native lib. The size of the buffer can be set by the user. Models can be formulated in a row-by-row fashion, without bothering about possible performance bottlenecks. Models with millions of constraints can be generated quickly.

#### Direct memory access

Once a model is built, it lives only in native memory, except for referenced variable and constraint objects that remain on the heap. To update model coefficients, the model is accessed directly in native memory through direct byte buffers, which speeds up re-solves. When the model is garbage-collected, its native memory is released automatically.

## Installation

#### Maven
Use the following Maven coordinates:
```
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>clp-mip-java</artifactId>
  <version>3.0.0</version>
</dependency>
```

#### All-in-one jar file
Build the all-in-one `clp-mip-java` jar with:

```sh
mvn package
```

The resulting jar is created as `target/clp-mip-java-3.0.0-full.jar`.

The jar file contains the native libraries for the most important host systems (Intel Mac, Apple Silicon, Windows 64-bit, and Linux ARM and x86), avoiding `UnsatisfiedLinkError` messages and manual native-library path configuration. Copies of the required native libraries are created in a temporary directory at runtime.

#### Publishing to Maven Central

After configuring the `central` server credentials and a GPG signing key, publish with:

```sh
mvn deploy -Prelease
```

## Requirements

* Java JDK 21
* 64-bit Linux, Mac OS, or Windows

## Documentation

See the original [clp-java Javadoc](https://quantego.github.io/clp-java) for the inherited API. The mixed-integer extensions are described above.

## Acknowledgment

The MIP support was developed with the assistance of OpenAI Codex based on the Coin-OR CBC solver and reviewed and tested by Jacob Feldman. It builds on clp-java by Nils Loehndorf (Quantego) and the Coin-OR CLP solver. 
