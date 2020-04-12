# JSR331 - www.jsr331.org    
[![N|Solid](https://jsr331.files.wordpress.com/2013/05/jcp.jpg)](http://jcp.org/en/jsr/detail?id=331)
[JSR331 “Java Constraint Programming API”](http://jsr331.org) is a JCP Specification Standard that has been developed under the terms of the www.JCP.org. 

# Project org.jcp.jsr331.constrainer
This project provides the JSR331 implementation of a constraint solver that is based on the Java constraint programming library "Constrainer" copyrighted by Exigen Properties, Inc. and/or affiliates: 
//////////////////////////////////////////////////////////////
Copyright Exigen Group 1998, 1999, 2000
320 Amboy Ave., Metuchen, NJ, 08840, USA, www.exigengroup.com
The copyright to the computer program(s) herein
is the property of Exigen Group, USA. All rights reserved.
The program(s) may be used and/or copied only with
the written permission of Exigen Group
or in accordance with the terms and conditions
stipulated in the agreement/contract under which
the program(s) have been supplied.
//////////////////////////////////////////////////////////////

The original Constrainer was developed in 1998-1999 by Dr. Jacob Feldman of IntelEngine, Inc. After IntelEngine, Inc. was acquired by Exigen Group in 2000, the Constrainer was made publicly available under the terms of open source GNU LGPL 
license and can be downloaded from the SourceForge: http://sourceforge.net/projects/constrainer/.

The source code of the Exigen's Constrainer version 5.4 was downloaded from a CVS repository in 2010 using the command:
*cvs -d:pserver:anonymous@openl-tablets.cvs.sourceforge.net:/cvsroot/openl-tablets*
 
Then it was modified to include only those features which needed to support the JSR331 interface. It was made publicly available under the terms of the same LGPL license. The modified source code keeps all Exigen Copyright notices and all major modifications in the source code are marked by "== Changed". All sources of the modified version including the JSR331 interface are available from from this [GitHub repository](https://github.com/OpenRulesSupport/jsr331/tree/master/org.jcp.jsr331.constrainer). This modified version is supported by Dr. Jacob Feldman, the JSR331 Specification Lead.

Major Changes and Bug Fixes:

**April, 2020**
* The Constrainer has been made available from the public GitHub and MVN 
repositories under the terms of open source GNU LGPL.

* Switched from commons.logging to slf4j

* The development and deployment facilities were modernized to support
modern building and version control options. The modified Constrainer is
available from the public GitHub and MVN repositories as an open source
product with unresticted use by the commercial and open source applications.

**August, 2012**
* Constrainer did not correctly stop upon a time limit for one solution search. 
* Fixed problems in Constrainer's execute() and GoalFastMinimize.

**March, 2010**
* The search Time Limit originally defined in seconds has been changed to
support milliseconds

**March, 2010**
* An error was found in constrainer/impl/IntExpArrayElement1.java
The test /tests/TestElementConstraint.java triggered an java.lang.RuntimeException: 
Invalid elementAt-expression: [A[10] B[4..10] C[7..10]][ind[0..2]]. 
java.lang.ArrayIndexOutOfBoundsException: 7
The fix: in the line 944 of IntExpArrayElement1:935 
"min < values[valCounter-1]" had been replaced with "min <= values[valCounter-1]"
The TestIntExpElementAt1.java works fine.

The following packages have been removed:
- com.exigen.ie.ccc
- com.exigen.ie.constrainer.consistencyChecking
- com.exigen.ie.constrainer.lpsolver
- com.exigen.ie.constrainer.lpsolver.impl
- com.exigen.ie.exigensimplex
- com.exigen.ie.exigensimplex.glpkimpl
- com.exigen.ie.simplex
- org.open.crt
- org.openl.util
