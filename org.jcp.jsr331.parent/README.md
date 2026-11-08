# JSR 331

[![N|Solid](https://jsr331.files.wordpress.com/2013/05/jcp.jpg)](http://jcp.org/en/jsr/detail?id=331)

[JSR 331, Java Constraint Programming API](https://jsr331.org/), is a Java Community Process specification.

## JSR 331 Parent POM

This project provides the common Maven configuration for JSR 331 projects. Its source is available in the [JSR 331 GitHub repository](https://github.com/OpenRulesSupport/jsr331/tree/master/org.jcp.jsr331.parent).

Version 3.0.0 requires JDK 21.

## Using the parent POM

To use the parent configuration in a Maven project, add:

```xml
<parent>   
    <groupId>com.javasolver</groupId>
    <artifactId>jsr331-parent</artifactId>
    <version>3.0.0</version>
</parent>
```

Add the JSR 331 API and any required logging dependencies:

```xml
<dependency>
    <groupId>com.javasolver</groupId>
    <artifactId>jsr331</artifactId>
    <version>${jsr331.version}</version>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
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
```
To use CP solvers, you also need to add these profiles:
```xml
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
```
To use LP solvers, you also need to add these profiles:
```xml
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
```
The corresponding examples of the "pom.xml" files can be found at the GitHub projects:
* org.jcp.jsr331.tck.cp
* org.jcp.jsr331.tck.linear

## Publishing to Maven Central

Configure the `central` server credentials in Maven `settings.xml`, make sure the signing key's public key is available from a supported public key server, and run:

```shell
mvn clean deploy -Prelease
```

The release profile signs the POM and submits it to the Maven Central Publisher Portal. After Maven Central publishes version 3.0.0, third-party indexes such as [MvnRepository](https://mvnrepository.com/) will discover it automatically.

