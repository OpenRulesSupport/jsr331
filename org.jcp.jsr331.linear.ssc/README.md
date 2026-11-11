# JSR 331 SSC Adapter

This project provides a [JSR 331](https://jsr331.org/) linear-programming adapter for [SSC](https://www.ssclab.org/) 4.7.8, a Java library designed and developed by Stefano Scarioli. Version 3.0.0 requires JDK 21.

The adapter works directly through the `org.ssclab:SSC-LP` Java dependency and does not require a separate native solver installation.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-ssc</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build and test

```shell
mvn clean test
```

## Publishing to Maven Central

This is the canonical source directory for `com.javasolver:jsr331-ssc:3.0.0`. Publish version 3.0.0 of `jsr331-parent`, `jsr331`, and `jsr331-linear` first. Then run:

```shell
mvn clean deploy -Prelease
```

Do not also deploy the duplicate `org.jcp.jsr331.ssc` directory under the same version. MvnRepository indexes the canonical release after it becomes available from Maven Central.

## Acknowledgment

This JSR 331 adapter was developed based on [SSC](https://www.ssclab.org/) LP Solver 4.7.8 with the assistance of OpenAI Codex and reviewed and tested by Jacob Feldman.
