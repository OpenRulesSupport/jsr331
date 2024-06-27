package javax.constraints.groovy.tests

import org.jcp.jsr331.groovy.*

String exp = "x*3+y*4-z*7 <= 16"
	
println "Split ${exp} in three parts:"

def results = XoperY.apply(exp);
println results