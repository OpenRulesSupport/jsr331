# SCIP Java

A compact Java 8+ modeling API backed by the native
[SCIP](https://github.com/scipopt/scip) 10.1.0 solver.

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

The `full` JAR bundles the official SCIP 10.1.0 Windows x64 runtime. On Linux
and macOS, install SCIP as a shared library and either make it visible to the
system loader or set `SCIP_LIBRARY_PATH` to the library file or directory.

## Build

```shell
mvn test
mvn package
```

The Java adapter is Apache-2.0 licensed. Bundled native components retain the
licenses shipped in the official SCIP distribution; see `THIRD_PARTY_LICENSES`.
