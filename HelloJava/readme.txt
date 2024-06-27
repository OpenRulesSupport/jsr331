OpenRules Sample Project "HelloJava"
====================================

What's Being Demonstrated
-------------------------

This is a basic rules project that shows how rules defined in Excel 
can use Java objects defined in an external Java application. It also shows 
how to organize and call rule services as regular Java methods.

How It Is Organized
-------------------

This project contains one main xls-file HelloCustomer.xls from which you
can launch two decision tables defineGreeting and defineSalutation placed in 
another file HelloRules.xls.  

It also includes a Java package "hello" with 3 Java classes:
- Customer.java: a simple Java bean that defines customer's attributes
- Response.java: a placeholder for the rule engine response; has a hash map,
into which a rule engine can place any generated information like "greeting:,
"salutation", etc.
- RunHelloCustomer.java: a class that demonstrates how to create and call
a rule engine from Java. Inside this Java class, we create an instanse of
OpenRulesEngine using the main xls-file that described as

file:rules/HelloCustomer.xls

It means that we keep HelloCustomer.xls in the subdirectory "rules" of the project 
directory (in our case, it is "HelloJava"). If you decide to place the file
HelloCustomer.xls inside your src-directory from where it would be automatically 
copied to the "bin" subdirectoty during compilation, you could use the
following descriptor:

classpath:/HelloCustomer.xls

You can use build.properties to modify the main java class name, the location
of the main xls-file and the name of the main launching method inside xls files:

main.java.class=hello.RunHelloCustomer
main.xls.file=file:rules/HelloCustomer.xls
main.xls.method=helloCustomer

The rules inside xls-files are using Java objects from the Java package "hello". 
To make this fact known to OpenRules, we added to the tables "Environment" inside 
"HelloCustomer.xls" a property "import.java" with value "hello.*".

We are also using a static Java method "out()" to print some information from
the launch-method "helloCustomer" described inside "HelloCustomer.xls". The method
"out" is a static method of the Java class Methods from the standard OpenRules
library "com.openrules.tools". To make this fact known to OpenRules, we added to 
the tables "Environment" a property "import.static" with value 
"com.openrules.tools.Methods".


There is one included Excel file "HelloRules.xls" located in the subdirectory
"include" of teh directory "rules". While you could keep both files in the same 
folder "rules" (or even to place all tables directly in the file HelloCustomer.xls) 
for demonstration purposes we decided to put HelloRules.xls in the folder 
"rules/input". The information where input files are located is kept inside the 
main xls file HelloCustomer.xls in the table "Environment": see property "include" 
with the value "include/HelloRules.xls".

It means that the included file HelloRules.xls is described relatively to the location 
of the main file HelloCustomer.xls - in our case this location is the folder "rules".


How to Build
------------

The project is automatically built under Eclipse when you make changes in it.
When used outside Eclipse, use compile.bat (executes target "compile.java" 
from build.xml) to validate and build the application.

How to Run
----------

Double-click to the run.bat (executes target "run" from build.xml) to run this 
console application and see results in a DOS view.
File build.properties allows you to change the name of the main Java file and 
main xls-file and methods without re-compilation.

Dependencies
------------
This project depends on openrules.config that is supposed to be at the
same directory level as the project HelloJava.

