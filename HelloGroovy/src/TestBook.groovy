import org.jcp.jsr331.groovy.Customer;
//================================================= p.62
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

//================================================= p.66
closureMethodString("Dolly") { name ->
	println "Hello, ${name}"
}
closureMethodString("Jacob",greeter) 

//================================================= p.66
Customer getCustomerRecord(id) {
	println "Id=${id}"
	if (id == 12345)
		return new Customer(id:1,name:"Joe Smith");
	else
		return null;
}

def withCustomer(id,c) {
	def cust = getCustomerRecord(id)
	c.call(cust)
}

def foundClosure = { customer ->
	if (customer) 
		println "Found customer ${customer.name}"
	else
		println "Customer not found"
}

withCustomer(12345,foundClosure)
withCustomer(1234,foundClosure)