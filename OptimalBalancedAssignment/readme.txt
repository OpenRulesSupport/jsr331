OpenRules Sample Project "Balanced Assignment"
=============================================

What's Being Demonstrated
-------------------------

This is an implementation of the DMCommunity.org Challenge Sep-2018
See https://dmcommunity.org/challenge/challenge-sep-2018/

How to configure a solver
-------------------------

The project OptimalBalancedAssignment depends on com.openrules.opt

To run using a constraint solver "Constrainer", use the following jars 
inside Lib for com.openrules.opt Java Build properties:

- jsr331.constrainer.jar
- constrainer.light.jar

For a linear solver such as SCIP replace these jars with:

- jsr331.linear.jar
- jsr331.coin.jar

All jars can be found at org.jcp.jsr331.tck/lib





