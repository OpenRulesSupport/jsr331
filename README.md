# JSR331 - www.jsr331.org    
[![N|Solid](https://jsr331.files.wordpress.com/2013/05/jcp.jpg)](http://jcp.org/en/jsr/detail?id=331)
[JSR331 “Java Constraint Programming API”](http://jsr331.org) is a JCP Specification Standard that has been developed under the terms of the www.JCP.org. It was officially approved by the JCP Executive Committee as the Final Release on March 7, 2012. The Specification and Maintenance Lead is Dr. Jacob Feldman. He can be reached at jacobfeldman@openrules.com. 

Since April-2020 JSR-331 is made available through public [GitHub](https://github.com/OpenRulesSupport/jsr331) and [MVN](https://mvnrepository.com/search?q=jsr331) repositories. 
A simplified interface to JSR-331 is freely available at www.JavaSolver.com.

If you have any technical questions, please post them at [JSR-331 Support Forum](https://groups.google.com/forum/#!forum/jsr331). You may also send questions and comments directly to the [JSR-331 Specification Lead](mailto:jacobfeldman@openrules.com).

# Download
**Get it from GitHub.** You may  download the latest JSR-331 sources and executables from the public [GibHub repository](https://github.com/OpenRulesSupport/jsr331) – just click on the button “Clone or download” and select “Download ZIP”. When you unzip the downloaded file jsr331-master.zip, look at two folders:
- **org.jcp.jsr331.tck.cp** – it includes samples of constraint satisfaction problems ready to be executed with constraint solvers. For example, double-click on “runBins.bat” to execute the problem from src\main\java\org\jcp\jsr331\samples\Bins.java
- **org.jcp.jsr331.tck.linear** – it includes samples of constraint satisfaction problems with only linear constraints ready to be executed with LP solvers. For example, double-click on “runInsideOutsideProduction.bat” to execute the problem from src\main\java\org\jcp\jsr331\samples\runInsideOutsideProduction.java
To switch between solvers, you only need to reset the variable SOLVER inside these bat- files. All included solvers are described in the profiles of the pom.xml files inside these projects.

**Use it in your Maven Projects.** JSR-331 has been 100% mavenized and is available for automatic download from the public [MVN Repository](https://mvnrepository.com/search?q=jsr331). To add JSR331 to your Maven project, you should simply add the JSR-331 dependencies to your pom.xml file. [JavaSolver](https://javasolvers.wordpress.com/) provides a good example of how it can be done – just take a look at its [pom.xml](https://github.com/OpenRulesSupport/javasolver/blob/master/com.javasolver.samples/pom.xml) file.

# JSR331 Implementations

JSR-331 allow business application developers to easily switch between different JSR331 implementations *without any changes* in the application code. The same JSR331-based application can be executed using different constraint or linear solvers by changing only implementation-specific jar-files in their classpath.


![N|Solid](https://jsr331.files.wordpress.com/2018/08/jsr331implementations.png)

The current release includes three implementations that are based on open source CP solvers: 
-	[Choco™](http://choco.sourceforge.net/) (version 2, BSD license) 
-	[Constrainer™](http://sourceforge.net/projects/openl-tablets/?source=directory) (GNU LGPL license)
-	[JSetL™](http://cmt.math.unipr.it/jsetl.html) (GNU GPL license)

There are seven implementations that are based on the following LP/MIP tools. 
Commercial LP Solvers:
-	[IBM CPLEX](http://www-01.ibm.com/software/integration/optimization/cplex-optimizer/)
-	[GUROBI](http://www.gurobi.com/)

Open Source LP Solvers:
-	[SCIP](http://scip.zib.de/)
-	[GLPK](http://www.gnu.org/software/glpk/)
-	[COIN](https://projects.coin-or.org/Clp/)
-	[LP_SOLVE](http://lpsolve.sourceforge.net/5.0/)
-	[OJALGO](http://ojalgo.org/).

More implementations will be included in the standard installation as they become available.

== Dependencies and Profiles
To add JSR331 dependencies and selected constraint and/or linear solvers to your Maven-based application, your own pom.xml should include
<parent>   
&nbsp;&nbsp;&nbsp;&nbsp;<groupId>com.javasolver</groupId>
&nbsp;&nbsp;&nbsp;&nbsp;<artifactId>jsr331-parent</artifactId>
&nbsp;&nbsp;&nbsp;&nbsp;<version>2.0.2</version>
</parent>

You also need to add these dependencies and profiles:
~~~
<dependency>
    <groupId>com.javasolver</groupId>
    <artifactId>jsr331</artifactId>
    <version>${jsr331.version}</version>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>${log4j.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>${log4j.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
    <version>${log4j.version}</version>
</dependency>
~~~
To use CP solvers, you also need to add these profiles:
~~~
<profiles>
		<profile>
			<id>Constrainer</id>
			<activation>
				<property>
					<name>solver</name>
					<value>Constrainer</value>
				</property>
			</activation>
			<dependencies>
				<dependency>
					<groupId>com.javasolver</groupId>
					<artifactId>jsr331-constrainer</artifactId>
					<version>${jsr331.version}</version>
				</dependency>
			</dependencies>
		</profile>
		<profile>
			<id>Choco</id>
			<activation>
				<activeByDefault>true</activeByDefault>
				<property>
					<name>solver</name>
					<value>Choco</value>
				</property>
			</activation>
			<dependencies>
				<dependency>
					<groupId>com.javasolver</groupId>
					<artifactId>jsr331-choco</artifactId>
					<version>${jsr331.version}</version>
				</dependency>
			</dependencies>
		</profile>
		<profile>
			<id>JSetL</id>
			<activation>
				<property>
					<name>solver</name>
					<value>JSetL</value>
				</property>
			</activation>
			<dependencies>
				<dependency>
					<groupId>com.javasolver</groupId>
					<artifactId>jsr331-jsetl</artifactId>
					<version>${jsr331.version}</version>
				</dependency>
			</dependencies>
		</profile>
</profiles>
~~~
To use LP solvers, you also need to add these profiles:
~~~	
<profiles>
		<profile>
			<id>Scip</id>
			<activation>
	            <activeByDefault>true</activeByDefault>
	        </activation>
			<dependencies>
				<dependency>
					<groupId>com.javasolver</groupId>
					<artifactId>jsr331-scip</artifactId>
					<version>${jsr331.version}</version>
				</dependency>
			</dependencies>
		</profile>
		<profile>
			<id>GLPK</id>
			<dependencies>
				<dependency>
					<groupId>com.javasolver</groupId>
					<artifactId>jsr331-glpk</artifactId>
					<version>${jsr331.version}</version>
				</dependency>
			</dependencies>
		</profile>
		<profile>
			<id>Coin</id>
			<dependencies>
				<dependency>
					<groupId>com.javasolver</groupId>
					<artifactId>jsr331-coin</artifactId>
					<version>${jsr331.version}</version>
				</dependency>
			</dependencies>
		</profile>
</profiles>
~~~
The corresponding examples of the "pom.xml" files can be found at the GitHub projects:
* org.jcp.jsr331.tck.cp
* org.jcp.jsr331.tck.linear
 

