# JSR 331 Adapter for IBM ILOG CP Optimizer

This module connects the JSR 331 constraint-programming API directly to IBM
ILOG CP Optimizer. It uses CP Optimizer's Java modeling and search APIs without
generating intermediate files.

## Prerequisites

- JDK 21 or newer
- IBM ILOG CPLEX Optimization Studio 22.2
- The JSR 331 API artifact `com.javasolver:jsr331:3.0.0`

IBM does not publish `ILOG.CP.jar` to Maven Central. Install the JAR supplied
with Optimization Studio into the local Maven repository:

```bat
mvn install:install-file -Dfile="C:\Program Files\IBM\ILOG\CPLEX_Studio_Community222\cpoptimizer\lib\ILOG.CP.jar" -DgroupId=com.ibm.ilog.optim -DartifactId=cpoptimizer-jar -Dversion=22.2.0 -Dpackaging=jar -DgeneratePom=true
```

Then build and test the adapter:

```bat
mvn clean test
```

At runtime the adapter locates both the CP Optimizer wrapper and its matching
CPLEX engine library from the standard Optimization Studio installation
folders. For a nonstandard install, set either `CPLEX_HOME`,
`-Djsr331.cpoptimizer.home=...`, or the exact wrapper path with
`-Djsr331.cpoptimizer.library=...`.

CP Optimizer is commercial IBM software. Its license and redistribution terms
are separate from the JSR 331 adapter's license.
