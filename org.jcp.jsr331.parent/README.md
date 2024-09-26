# JSR331 - www.jsr331.org    
[![N|Solid](https://jsr331.files.wordpress.com/2013/05/jcp.jpg)](http://jcp.org/en/jsr/detail?id=331)
[JSR331 “Java Constraint Programming API”](http://jsr331.org) is a JCP Specification Standard that has been developed under the terms of the www.JCP.org. 

# Project org.jcp.jsr331.parent
This project a parent project that contains the [pom.xml](https://github.com/OpenRulesSupport/jsr331/blob/master/org.jcp.jsr331.parent/pom.xml) file common for all JSR331 projects. It is avaible from this [GitHub repository](https://github.com/OpenRulesSupport/jsr331/tree/master/org.jcp.jsr331.parent) 

To add JSR331 dependencies and selected constraint and/or linear solvers to your Maven-based application, your own pom.xml should include
~~~
<parent>   
    <groupId>com.javasolver</groupId>
    <artifactId>jsr331-parent</artifactId>
    <version>2.3.2</version>
</parent>
~~~

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
		<profile>
			<id>Sugar</id>
			<activation>
				<property>
					<name>solver</name>
					<value>Sugar</value>
				</property>
			</activation>
			<dependencies>
				<dependency>
					<groupId>com.javasolver</groupId>
					<artifactId>jsr331-sugar</artifactId>
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

