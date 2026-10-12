# JSR-331 JaCoP adapter

This module implements JSR-331 using [JaCoP 4.10.0](https://github.com/radsz/jacop/tree/4.10.0).
It requires Java 17 or newer, consistently with the current JSR-331 parent project.

Build and run the adapter tests with:

```shell
mvn test
```

JaCoP is resolved from Maven Central; solver binaries are not checked into this module.
The JaCoP license and its supplemental terms are retained in `LICENSE-JACOP.txt`.
