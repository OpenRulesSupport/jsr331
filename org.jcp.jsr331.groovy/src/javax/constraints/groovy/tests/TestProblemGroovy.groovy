package javax.constraints.groovy.tests

import javax.constraints.groovy.ProblemGroovy;
import javax.constraints.Var;

import org.codehaus.groovy.ast.stmt.TryCatchStatement;

int num = 10;
ProblemGroovy p = new ProblemGroovy()
Var x = p.variable("x",1,5)
p.post(x,"<",3)
println "IntMultiply: " + 10.multiply(x)

println "Int*: " + 10*x

p.log x
Var sub = 5-x
p.log(sub)

try {
	Var division = 5.divide(x) // 5/x
    p.log(division)
}
catch(Exception e) {
	println "fail fo divide"
	e.printStackTrace()
}
