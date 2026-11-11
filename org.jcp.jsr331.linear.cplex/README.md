# JSR 331 CPLEX Adapter

This project provides a direct Java adapter from [JSR 331](https://jsr331.org/) to [IBM ILOG CPLEX Optimization Studio](https://www.ibm.com/products/ilog-cplex-optimization-studio). It supports linear and mixed-integer models and requires JDK 21.

The adapter creates variables, constraints, and objectives through the CPLEX Concert Java API. Normal JSR 331 solves remain in memory: they do not create intermediate MPS, command, XML, or solution files. The explicit `solve(File)` API still accepts model formats supported by `IloCplex.importModel`, including LP and MPS.

## CPLEX installation

Install and license CPLEX. CPLEX is commercial software and its `cplex.jar` and native libraries are not included in this Maven artifact.

Install the CPLEX Java API from your CPLEX installation into the local Maven repository. The following example targets CPLEX 22.2.0 Community Edition on Windows:

```shell
mvn install:install-file "-Dfile=C:\Program Files\IBM\ILOG\CPLEX_Studio_Community222\cplex\lib\cplex.jar" -DgroupId=com.ibm.ilog.optim -DartifactId=cplex-jar -Dversion=22.2.0 -Dpackaging=jar
```

Add `cplex.jar` to the application classpath. The adapter automatically locates the version-matched native library in standard CPLEX Studio installation directories and in directories identified by `CPLEX_STUDIO_DIR*` or `CPLEX_HOME` environment variables.

For a nonstandard installation, identify either the library itself or the CPLEX Studio directory:

```shell
java -Djsr331.cplex.library="C:\path\to\cplex2220.dll" ...
java -Djsr331.cplex.home="C:\path\to\CPLEX_Studio_Community222" ...
```

The traditional `-Djava.library.path=...`, `PATH`, or corresponding Linux/macOS library-path configuration remains supported. The JAR and native library must come from the same CPLEX release and platform.

## Maven dependencies

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-cplex</artifactId>
  <version>3.0.0</version>
</dependency>
<dependency>
  <groupId>com.ibm.ilog.optim</groupId>
  <artifactId>cplex-jar</artifactId>
  <version>22.2.0</version>
</dependency>
```

The CPLEX dependency is optional in `jsr331-cplex` because IBM does not publish the proprietary JAR to Maven Central; applications must provide it explicitly.

## Build and test

After installing `cplex.jar` locally:

```shell
mvn clean test
```

The integration tests run when a compatible licensed CPLEX native runtime is available. Otherwise they are skipped.

## Publishing to Maven Central

Publish version 3.0.0 of `jsr331-parent`, `jsr331`, and `jsr331-linear` first. Then run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.

## Acknowledgment

This JSR 331 adapter was developed based on [IBM ILOG CPLEX Optimization Studio](https://www.ibm.com/products/ilog-cplex-optimization-studio) with the assistance of OpenAI Codex and reviewed and tested by Jacob Feldman.
