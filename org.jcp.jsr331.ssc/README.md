# JSR 331 SSC Adapter — Legacy Duplicate

This directory duplicates the Java source and Maven coordinate of [`org.jcp.jsr331.linear.ssc`](../org.jcp.jsr331.linear.ssc/README.md):

```text
com.javasolver:jsr331-ssc:3.0.0
```

Maven Central releases are immutable and cannot contain two different deployments with the same coordinate. Use `org.jcp.jsr331.linear.ssc` as the canonical source directory and do not deploy this duplicate separately.

This directory can still be built locally with JDK 21:

```shell
mvn clean package
```
