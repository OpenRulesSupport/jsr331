OpenRules Sample Project "Miss Manners"
======================================

What's Being Demonstrated
-------------------------

This project demonstrates the use of the Rule Solver solving a famous
problem "Miss Manners" that is frequently used as a benchmark for rule engine.

The problem is to find an acceptable seating arrangement for guests 
at a dinner party.  It should match people with the same hobbies, 
and to seat everyone next to a member of the opposite sex.  
The “same hobbies” means that people seating next to each other  
should have at least one common hobby.

How It Is Organized
-------------------

This project contains one main xls-file Manners.xls that actually consists
of 3 data sets for the problem instances of size 16, 64, and 128 (default).  

The supporting Java code consists of two files:

1) Guest.java - a basic Java bean that defines guest's id, gender, and hobies
2) MannersSolver.java - that contains three major methods:
- define(): specifies the problem variables and constraints
- solve(): find one solution
- solveAll(): find first 3 solutions (this limit 3 may be easily changed). 

To represent and solve the problem we uses a constraint programming API as
defined at www.cpstandards.org. In our model we define one integer variable 
per seat that represents the proper guest.  All these variables are subject
to the constraint "allDifferent". A gender for each seat is a constrained 
integer variable that is defined using the “elementAt” constraint with 
a seat variable being considered as an index.  Then gender constraints are 
defined as two simple “not equal” constraints for all neighbors. 

A set of hobbies for each seat is a constrained set variable that is defined 
using “elementAt” constraint on regular sets with a seat variable being an index. 
Then hobby constraints are defined as two “intersection is not empty” constraints
for all neighbors. 

I both solving methods we use the standard search goals available from any CP solver.

In this implementation we are using an open source CP solver "Constrainer".


How to Build
------------

The project can be imported into the Eclipse IDE and it will be automatically 
built under when you make changes in it.
You do not need Eclipse to run the project for different data sets.

How to Run
----------

Double-click to the run.bat to run this console application and see results in a DOS view.
You may change the size of the test problem (128,64,16) in the script file run.bat.

Dependencies
------------
We expect that you have the latest Java installed - see http://java.sun.com/javase/downloads/index.jsp.

