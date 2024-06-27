OpenRules Sample Project "HelloJsr94Multi"
=========================================

What's Being Demonstrated
-------------------------

This is a basic rules project that demonstrates how to launch 
OpenRules in accordance with JSR 94 standard Rule Engine API: 
see http://www.jcp.org/en/jsr/detail?id=94. Additionally to the simple
run of one method described in the project HelloJsr94, this project 
demonstrate how to run different methods inside the same rule set 
using the same instance of OpenrulesEngine.

How It Is Organized
-------------------

This project depends on three other projects:
- lib.jsr94: a standard JSR 94 API
- com.openrules.jsr94: JSR94 reference implementation for OpenRules.
- openrules.config.

HelloJsr94Multi contains one main xls-file rules/HelloCustomer.xls with 
three decision tables:
- defineSummerGreeting
- defineWinterGreeting
- defineSalutation.  

It also includes a Java package "hello" with 3 Java classes:
- Customer.java: a simple Java bean that defines customer's attributes
- Response.java: a placeholder for the rule engine response; has a hash map,
into which a rule engine can place any generated information like "greeting:,
"salutation", etc.
- RunHelloJsr94Multi.java: a class that demonstrates how to create a rule engine
and execute different methods such as helloSummer and helloWinter
using JSR94 API.

How to Build
------------

The project is automatically built under Eclipse when you make changes in it.
Look at files build.xml and build.properties to see an example of how to 
add external libraries to your project.

How to Run
----------

Double-click to the run.bat to run this console application and see
results in a DOS view.

