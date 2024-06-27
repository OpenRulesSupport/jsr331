package org.jcp.jsr331.groovy
def flintstones = ["Fred", "Barney"]
def greeter = { println "Hello, ${it}!" }
flintstones.each greeter
greeter "Wilma"
//greeter = { }
//flintstones.each greeter
//greeter "Wilma"

def closureMethodString(String s, Closure c) {
	println "Greet someone"
	c.call(s)
}

closureMethodString("Dolly") { name ->
	println "Hello, ${name}"
}
closureMethodString("Jacob",greeter) 

def withCustomer(id,c) {
	def cust = getCustomerRecord(id)
	c.call(cust)
}

withCustomer