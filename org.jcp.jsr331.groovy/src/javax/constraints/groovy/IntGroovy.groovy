package javax.constraints.groovy


import javax.constraints.impl.Var;
import javax.constraints.impl.Problem;

class IntGroovy {
	static {
		Integer.metaClass.multiply = { Var var, Closure c -> 
			c.call([delegate,var])
		}
	}
}

//int num = 10;
//Problem p = new Problem();
//Var x = p.variable("x",1,5);
//p.post(x,"<",3);
//10.multiply(x) { int thisInt, Var var -> 
//	println "IntMultiply: " + var*thisInt;
//}