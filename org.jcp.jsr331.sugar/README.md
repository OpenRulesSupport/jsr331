# JSR 331 Sugar Adapter

This project provides a [JSR 331](https://jsr331.org/) implementation based on the open source Sugar 2.1.3 constraint solver developed by Naoyuki Tamura at [Kobe University](https://www.kobe-u.ac.jp/en/). Version 3.0.0 requires JDK 21.

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-sugar</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Build

```shell
mvn clean package
```

## Publishing to Maven Central

Publish `jsr331-parent` and `jsr331` version 3.0.0 first. Then run:

```shell
mvn clean deploy -Prelease
```

MvnRepository indexes releases after they become available from Maven Central.
