# scip-mip-java

A compact Java 8+ modeling API for mixed-integer programming, backed by the
native [SCIP Optimization Suite](https://github.com/scipopt/scip) 10.1.0 solver.

```java
try (SCIP model = new SCIP()) {
    SCIPVariable x = model.addVariable().binary().obj(6);
    SCIPVariable y = model.addVariable().binary().obj(5);
    SCIPVariable z = model.addVariable().binary().obj(4);

    model.createExpression().add(4, x).add(3, y).add(2, z).leq(5);

    SCIP.Status status = model.maximize();
    System.out.println(status);                   // OPTIMAL
    System.out.println(model.getObjectiveValue()); // 9.0
}
```

## Features

- continuous, integer, and binary variables;
- equality, inequality, and ranged linear constraints;
- minimization and maximization with objective offsets;
- time, node, solution, relative-gap, absolute-gap, and thread limits;
- primal/dual bounds, optimality gap, node count, and solution access;
- arbitrary native SCIP integer, long-integer, and real parameters;
- LP/MPS/CIP export through SCIP's writer plugins;
- deterministic cleanup with `AutoCloseable`.

The `full` JAR bundles the official SCIP 10.1.0 runtime for Windows x64, Linux
x86_64, Linux aarch64, and macOS aarch64. On other platforms, install SCIP as a
shared library and either make it visible to the system loader or set
`SCIP_LIBRARY_PATH` to the library file or directory (this also overrides the
bundled runtime).

## Maven

Use the following dependency after version 3.0.0 is published to Maven Central:

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>scip-mip-java</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build

```shell
mvn test
mvn package
```

The regular JAR, source JAR, Javadoc JAR, and `full` JAR are created in
`target/`. The `full` JAR includes the supported native SCIP runtimes and all
Java dependencies.

## Publishing to Maven Central

After configuring the `central` server credentials in Maven `settings.xml`, a
GPG signing key, and publishing that public key to a Sonatype-supported
keyserver, publish with:

```shell
mvn clean deploy -Prelease
```

## Acknowledgment

The project was developed based on SCIP Optimization Suite 10.0 [https://github.com/scipopt/scip](https://github.com/scipopt/scip) with the assistance of OpenAI Codex and reviewed and tested by Jacob Feldman.

## License

The Java adapter is Apache-2.0 licensed. Bundled native components retain the
licenses shipped in the official SCIP distribution; see `THIRD_PARTY_LICENSES`.
