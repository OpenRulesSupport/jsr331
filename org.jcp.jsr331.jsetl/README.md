# JSR 331 JSetL Adapter

This project provides a [JSR 331](https://jsr331.org/) implementation based on the open source JSetL 3.0 Java constraint programming library. Version 3.0.0 requires JDK 21. Its source is available from this [GitHub repository](https://github.com/OpenRulesSupport/jsr331/tree/master/org.jcp.jsr331.jsetl).

JSetL is a Java library that combines the object-oriented programming paradigm 
of Java with valuable concepts of CLP languages, such as logical variables, 
lists, unification, constraint solving, nondeterminism. The library  provides 
also sets and set constraints like those found  in CLP(SET) (see here). 
Unification may involve logical variables, as well as list and set objects 
("set unification"). Constraints concern basic set-theoretical operations 
(e.g., membership, union, intersection, etc.), as well as equality, inequality 
and integer comparison operations. Set constraints are solved using a complete 
solver that accounts for partially specified sets (i.e., sets containing unknown 
elements). Equality, inequality and comparison constraints on integers are dealt 
with as Finite-Domain Constraints. 

JSetL was developed at the Department of Mathematics of the University of Parma, Italy. The library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License, Version 3.

*Note.* The project was upgraded in July 2020 by the University of Parma from version 2.3 to 3.0.

## Maven dependency

After version 3.0.0 is published to Maven Central, add:

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-jsetl</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build and test

```shell
mvn clean test
```

## Publishing to Maven Central

Publish `jsr331-parent` and `jsr331` version 3.0.0 first. With the Maven Central credentials and GPG key configured, run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.


