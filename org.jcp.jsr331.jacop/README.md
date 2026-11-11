# JSR-331 JaCoP adapter

This module implements JSR-331 using [JaCoP 4.10.0](https://github.com/radsz/jacop/tree/4.10.0).
Version 3.0.0 requires JDK 21, consistently with the current JSR-331 parent project.

## Maven dependency

After version 3.0.0 is published to Maven Central, add:

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-jacop</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build and test

Build and run the adapter tests with:

```shell
mvn test
```

JaCoP is resolved from Maven Central; solver binaries are not checked into this module.
The JaCoP license and its supplemental terms are retained in `LICENSE-JACOP.txt`.

## Publishing to Maven Central

Publish `jsr331-parent` and `jsr331` version 3.0.0 first. With the Maven Central credentials and GPG key configured, run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.

## Acknowledgment

This JSR 331 adapter was developed based on [JaCoP 4.10.0](https://github.com/radsz/jacop/tree/4.10.0) with the assistance of OpenAI Codex and reviewed and tested by Jacob Feldman.
