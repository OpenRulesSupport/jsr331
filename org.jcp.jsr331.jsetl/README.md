# JSR331 - www.jsr331.org    
[![N|Solid](https://jsr331.files.wordpress.com/2013/05/jcp.jpg)](http://jcp.org/en/jsr/detail?id=331)
[JSR331 “Java Constraint Programming API”](http://jsr331.org) is a JCP Specification Standard that has been developed under the terms of the www.JCP.org. 

# Project org.jcp.jsr331.jsetl
This project provides the JSR331 implementation of a constraint solver that is based on the open source Java constraint programming library "JSetL" v. 3.0. It is available from this [GitHub repository](https://github.com/OpenRulesSupport/jsr331/tree/master/org.jcp.jsr331.jsetl). 

JSetL is a Java library that combines the object-oriented programming paradigm 
of Java with valuable concepts of CLP languages, such as logical variables, 
lists, unification, constraint solving, nondeterminism. The library  provides 
also sets and set constraints like those found  in CLP(SET) (see here). 
Unification may involve logical variables, as well as list and set objects 
("set unification"). Constraints concern basic set-theoretical operations 
(e.g., membership, union, intersection, etc.), as well as equality, inequality 
and integer comparison operations. Set constraints are solved using a complete 
solver that accounts for partially specified sets (i.e., sets containing unknown 
elements). Equality, inequality and comparison constraints on integers are dealt 
with as Finite-Domain Constraints. 

JSetL has been developed at the Department of Mathematics of the University of 
Parma (Italy). It is completely written in Java. The full Java code of the JSetL 
library, along with sample programs and related documents, is available at 
http://cmt.math.unipr.it/jsetl.html#current. The library is free software; 
you can redistribute it and/or modify it under the terms of the GNU General 
Public License - http://www.gnu.org/licenses/gpl.html.

*Note.* The project has been upgrated in July-2020 by the University of Parma from v. 2.3 to 3.0


