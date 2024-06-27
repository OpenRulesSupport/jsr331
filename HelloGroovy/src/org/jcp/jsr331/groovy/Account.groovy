package org.jcp.jsr331.groovy

class Account {
	int id
	double balance
	Customer owner
	
	void credit(double deposit) {
		balance += deposit
	}
	
	String toString() {
		"Account id ${id} owner ${owner.name} balance is ${balance}"
	}
}


