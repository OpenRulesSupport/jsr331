package javax.constraints.groovy.tests



import javax.constraints.impl.Var;
import javax.constraints.impl.Problem;


Integer.metaClass.multiply = { Var var -> 
	var*delegate
}

int num = 10;
Problem p = new Problem();
Var x = p.variable("x",1,5);
p.post(x,"<",3);
println "IntMultiply: " + 10.multiply(x)

println "Int*: " + 10*x

Var mul = 5*x;
p.log(mul);