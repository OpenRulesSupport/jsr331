package javax.constraints.groovy.tests

import javax.constraints.impl.Var;
import javax.constraints.impl.Problem;
import javax.constraints.groovy.IntGroovy;


int num = 10;
Problem p = new Problem();
Var x = p.variable("x",1,5);
p.post(x,"<",3);

def ig = new IntGroovy()

10.multiply(x) { int thisInt, Var var -> 
	println "IntMultiply: " + var*thisInt;
}