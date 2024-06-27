
This is a light-weight version of the Java Constraint Programming library
known as "Constrainer" 
Copyright © 2004, Exigen Properties, Inc. and/or affiliates. All rights reserved.  

The original Constrainer version 5.4 is available under the terms of 
open source GNU LGPL license from:

http://sourceforge.net/projects/constrainer/

The source code of the original version was downloaded from:
 
cvs -d:pserver:anonymous@openl-tablets.cvs.sourceforge.net:/cvsroot/openl-tablets

The "constrainer.light" is a modified version of this open source product 
that supports a pure constraint programming concepts needed for the JSR-331. 
The source code with all modifications marked by "== Changed" can be found
in the Eclipse project "constrainer.light".


The list of changes:

1) The following projects have been removed:
- com.exigen.ie.ccc
- com.exigen.ie.constrainer.consistencyChecking
- com.exigen.ie.constrainer.lpsolver
- com.exigen.ie.constrainer.lpsolver.impl
- com.exigen.ie.exigensimplex
- com.exigen.ie.exigensimplex.glpkimpl
- com.exigen.ie.simplex
- org.open.crt
- org.openl.util (the only file Log.java moved to com.exigen.ie.tools)

The proper projects also removed from examples and tests.

2) Added build.xml that allows to build lib/constrainer.light.jar

3) Changes and Bug Fixes:

August, 2012 
============
Constrainer did not correctly stop upon a time limit for one solution search. 
Fixed problems in Constrainer's execute() and GoalFastMinimize.

March, 2010 
===========
The search Time Limit originally defined in seconds has been changed to
support milliseconds

March, 2010
===========
An error was found in constrainer/impl/IntExpArrayElement1.java
The test /tests/TestElementConstraint.java triggered an java.lang.RuntimeException: 
Invalid elementAt-expression: [A[10] B[4..10] C[7..10]][ind[0..2]]. 
java.lang.ArrayIndexOutOfBoundsException: 7
The fix: in the line 944 of IntExpArrayElement1:935 
"min < values[valCounter-1]" had been replaced with "min <= values[valCounter-1]"
The TestIntExpElementAt1.java works fine.


