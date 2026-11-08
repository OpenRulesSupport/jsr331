# JSR 331 Scheduler

JSR 331 Scheduler is a generic Java API for scheduling and resource-allocation problems, built on the [JSR 331 constraint programming API](https://jsr331.org/). Version 3.0.0 requires JDK 21.

This package was developed by Jacob Feldman and is available from the [JSR 331 GitHub repository](https://github.com/OpenRulesSupport/jsr331/tree/master/org.jcp.jsr331.scheduler).

## Maven dependency

```xml
<dependency>
  <groupId>com.javasolver</groupId>
  <artifactId>jsr331-scheduler</artifactId>
  <version>3.0.0</version>
</dependency>
```

## Scheduling and resource-allocation concepts
The following scheme represents the major scheduling and resource allocation concepts implemented in JSR331 Scheduler:

![N|Solid](https://javasolvers.files.wordpress.com/2020/04/jsr331scheduler.concepts.png)

JSR331 Scheduler follows the representation of scheduling and resource allocation problems introduced in the great product called [ILOG Scheduler](http://lia.deis.unibo.it/Courses/AI/applicationsAI2009-2010/materiale/cp15doc/pdf/usrscheduler.pdf) written by Claude LePape and Jean-Francois Puget in 1990s. The following [presentation](http://openrules.com/pdf/BRForum2012.Feldman.v1.pdf) explains how to use JSR331 Scheduler. 

## Sample problems

The examples of scheduling and resource allocation problems which can be defined and solved with JSR331 Scheduler are provided in the public GitHub project [org.jcp.jsr331.tck.cp](https://github.com/OpenRulesSupport/jsr331/tree/master/org.jcp.jsr331.tck.cp) in the folder *"org.jcp.jsr331.scheduler.samples"*.

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
