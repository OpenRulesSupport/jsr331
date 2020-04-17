# JSR331 - www.jsr331.org    
[![N|Solid](https://jsr331.files.wordpress.com/2013/05/jcp.jpg)](http://jcp.org/en/jsr/detail?id=331)
[JSR331 “Java Constraint Programming API”](http://jsr331.org) is a JCP Specification Standard that has been developed under the terms of the www.JCP.org. 

# JSR331 Scheduler

JSR331 Scheduler is an implementation of a generic Java API for scheduling and resource allocation problems. It's build on the top of the JSR331 constraint programming API as the project [org.jcp.jsr331.scheduler](https://github.com/OpenRulesSupport/jsr331/tree/master/org.jcp.jsr331.scheduler). 

This package was developed by Jacob Feldman and made publicly available as a part of the JSR331. It can be download with all sources from this [GitHub repository](https://github.com/OpenRulesSupport/jsr331/tree/master/org.jcp.jsr331.scheduler). It is also available through the MVN Repository and can be included in any Maven-based project as an embedded component of the JSR331 by adding the following dependency:
~~~
<dependency>
		<groupId>com.javasolver</groupId>
		<artifactId>jsr331-scheduler</artifactId>
		<version>${jsr331.version}</version>
</dependency>
~~~
# Scheduling and Resource Allocation Concepts
The following scheme represents the major scheduling and resource allocation concepts implemented in JSR331 Scheduler:

![N|Solid](https://javasolvers.files.wordpress.com/2020/04/jsr331scheduler.concepts.png)

JSR331 Scheduler follows the representation of scheduling and resource allocation problems introduced in the great product called [ILOG Scheduler](http://lia.deis.unibo.it/Courses/AI/applicationsAI2009-2010/materiale/cp15doc/pdf/usrscheduler.pdf) written by Claude LePape and Jean-Francois Puget in 1990s. The following [presentation](http://openrules.com/pdf/BRForum2012.Feldman.v1.pdf) explains how to use JSR331 Scheduler. 

# Sample Problems

The examples of scheduling and resource allocation problems which can be defined and solved with JSR331 Scheduler are provided in the public GitHub project [org.jcp.jsr331.tck.cp](https://github.com/OpenRulesSupport/jsr331/tree/master/org.jcp.jsr331.tck.cp) in the folder *"org.jcp.jsr331.scheduler.samples"*.