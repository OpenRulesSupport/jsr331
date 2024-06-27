package com.quantego.clp;

/**
 * Constraint type.
 * @author Nils Loehndorf
 * JF moved from CLPConstraint to a separate file as 
 * CLPExpression references in could not be resolved
 *
 */
public enum TYPE {
	LEQ, GEQ, EQ, NEQ
}