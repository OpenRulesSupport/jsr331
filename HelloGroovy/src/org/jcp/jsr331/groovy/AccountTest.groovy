package org.jcp.jsr331.groovy

class AccountTest {

	static main(args) {
		def customer = new Customer(id:1,name:"Joe Smith")
		Object saving = new Account(id:2, balance:0.0, owner:customer)

		saving.credit 20.00
		println saving
	
	}

}
