# JSR331 - www.jsr331.org    
[![N|Solid](https://jsr331.files.wordpress.com/2013/05/jcp.jpg)](http://jcp.org/en/jsr/detail?id=331)
[JSR331 “Java Constraint Programming API”](http://jsr331.org) is a JCP Specification Standard that has been developed under the terms of the www.JCP.org. 

# Project org.jcp.jsr331.cplex
This project provides the JSR331 implementation of a linear solver that is based on the commercial linear programming solver ["CPLEX"](https://www.ibm.com/products/ilog-cplex-optimization-studio). Its JSR331 interface is avaible from this [GitHub repository](https://github.com/OpenRulesSupport/jsr331/tree/master/org.jcp.jsr331.linear.cplex). 

# Installation
To use this JSR331 with CPLEX, you need to get a CPLEX's license and its executable **cplex.exe** - contact https://www.ibm.com/. You will need to make sure that your Environment variable PATH includes the path to cplex.exe.