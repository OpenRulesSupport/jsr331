# JSR331 - www.jsr331.org    
[![N|Solid](https://jsr331.files.wordpress.com/2013/05/jcp.jpg)](http://jcp.org/en/jsr/detail?id=331)
[JSR331 “Java Constraint Programming API”](http://jsr331.org) is a JCP Specification Standard that has been developed under the terms of the www.JCP.org. 

# Project org.jcp.jsr331.choco
This project provides the JSR331 implementation of a constraint solver that is based on the open source Java constraint programming library "Choco2" v. 2.1.5. It is avaible from this [GitHub repository](https://github.com/OpenRulesSupport/jsr331/tree/master/org.jcp.jsr331.choco) 

# Installation
As we do not have the source code of Choco-2 anymore, you need to add Choco2's jar file to your Maven's system folder ~/.m2. You can do it by running the bat-file **installChoco2.bat** from this [GitHub repository](https://github.com/OpenRulesSupport/jsr331/tree/master/org.jcp.jsr331.choco/lib). 

*Note.* This installation step will not be necessary when we implement the same interface using the latest version of Choco that is not backward compatible with Choco-2.


