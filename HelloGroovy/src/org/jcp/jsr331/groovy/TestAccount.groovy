package org.jcp.jsr331.groovy

customer = new Customer(id:1,name:"Joe Smith")
saving = new Account(id:2, balance:0.0, owner:customer)

saving.credit 20.00
print saving
