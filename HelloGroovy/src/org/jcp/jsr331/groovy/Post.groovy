package org.jcp.jsr331.groovy
import javax.constraints.impl.constraint.Linear

def post = { left,oper,right -> new Linear(left,oper,right).post() }
