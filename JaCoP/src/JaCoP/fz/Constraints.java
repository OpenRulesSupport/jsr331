/**
 *  Constraints.java 
 *  This file is part of JaCoP.
 *
 *  JaCoP is a Java Constraint Programming solver. 
 *	
 *	Copyright (C) 2000-2008 Krzysztof Kuchcinski and Radoslaw Szymanek
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  Notwithstanding any other provision of this License, the copyright
 *  owners of this work supplement the terms of this License with terms
 *  prohibiting misrepresentation of the origin of this work and requiring
 *  that modified versions of this work be marked in reasonable ways as
 *  different from the original version. This supplement of the license
 *  terms is in accordance with Section 7 of GNU Affero General Public
 *  License version 3.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package JaCoP.fz;

import java.util.ArrayList;

import JaCoP.constraints.AbsXeqY;
import JaCoP.constraints.Alldiff;
import JaCoP.constraints.Alldistinct;
import JaCoP.constraints.Among;
import JaCoP.constraints.AmongVar;
import JaCoP.constraints.AndBool;
import JaCoP.constraints.Assignment;
import JaCoP.constraints.Circuit;
import JaCoP.constraints.Constraint;
import JaCoP.constraints.Count;
import JaCoP.constraints.Cumulative;
import JaCoP.constraints.Diff2;
import JaCoP.constraints.Element;
import JaCoP.constraints.ExtensionalSupportMDD;
import JaCoP.constraints.GCC;
import JaCoP.constraints.Max;
import JaCoP.constraints.Min;
import JaCoP.constraints.Not;
import JaCoP.constraints.OrBool;
import JaCoP.constraints.PrimitiveConstraint;
import JaCoP.constraints.Reified;
import JaCoP.constraints.SumWeight;
import JaCoP.constraints.Values;
import JaCoP.constraints.XeqC;
import JaCoP.constraints.XeqY;
import JaCoP.constraints.XgtC;
import JaCoP.constraints.XgtY;
import JaCoP.constraints.XgteqC;
import JaCoP.constraints.XgteqY;
import JaCoP.constraints.XltC;
import JaCoP.constraints.XltY;
import JaCoP.constraints.XlteqC;
import JaCoP.constraints.XlteqY;
import JaCoP.constraints.XmulCeqZ;
import JaCoP.constraints.XmulYeqC;
import JaCoP.constraints.XmulYeqZ;
import JaCoP.constraints.XdivYeqZ;
import JaCoP.constraints.XmodYeqZ;
import JaCoP.constraints.XneqC;
import JaCoP.constraints.XneqY;
import JaCoP.constraints.XplusCeqZ;
import JaCoP.constraints.XplusYeqC;
import JaCoP.constraints.XplusYeqZ;
import JaCoP.constraints.XplusYgtC;
import JaCoP.constraints.XorBool;
import JaCoP.constraints.IfThenBool;
import JaCoP.constraints.knapsack.Knapsack;
import JaCoP.constraints.regular.Regular;
import JaCoP.core.BooleanVariable;
import JaCoP.core.Domain;
import JaCoP.core.IntervalDomain;
import JaCoP.core.BoundDomain;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.set.constraints.Card;
import JaCoP.set.constraints.ElementSet;
import JaCoP.set.constraints.Lex;
import JaCoP.set.constraints.XdiffYeqZ;
import JaCoP.set.constraints.XinY;
import JaCoP.set.constraints.XintersectYeqZ;
import JaCoP.set.constraints.XunionYeqZ;
import JaCoP.set.constraints.EInS;
import JaCoP.set.core.Set;
import JaCoP.set.core.SetDomain;
import JaCoP.set.core.SetValueEnumeration;
import JaCoP.util.fsm.FSM;
import JaCoP.util.fsm.FSMState;
import JaCoP.util.fsm.FSMTransition;

/**
 * 
 * The part of the parser responsible for parsing constraints. 
 * 
 * @author Krzysztof Kuchcinski 
 *
 */
public class Constraints implements ParserTreeConstants {

    Tables dictionary;
    Store store;
    String p;
    Variable zero, one;
    final static int eq=0, ne=1, lt=2, gt=3, le=4, ge=5;
    boolean intPresent = true;

    ArrayList<Variable[]> parameterListForAlldistincts = new ArrayList<Variable[]>();
    ArrayList<Constraint> delayedConstraints = new ArrayList<Constraint>();

//     boolean storeLevelIncreased=false;

    final static boolean debug = false;

    /**
     * It creates an object to parse the constraint part of the flatzinc file.
     * @param store the constraint store in which the constraints are being created.
     */
    public Constraints(Store store) {
	this.store = store;
// 	this.zero = new Variable(store, "zero", 0,0);
// 	this.one = new Variable(store, "one", 1,1);
    }

    void generateVarConstraint(SimpleNode node, Tables table) {

	dictionary = table;
 	this.zero = table.zero; 
	this.one = table.one; 

	//  	node.dump("");

	// constraint can be a variable of array access
	// not very usefull but possible
	if ( ((ASTConstElem)node).getName() == null) {
	    SimpleNode child = (SimpleNode)node.jjtGetChild(0);
	    if ( ((ASTVariableExpr)child).getIdent() != null) {
		Constraint c = new XeqC(dictionary.getVariable(((ASTVariableExpr)child).getIdent()), 1);
		pose(c);
	    }
	    else if (((ASTVariableExpr)child).getArrayAccess() != null) {
		ArrayAccess aa = ((ASTVariableExpr)child).getArrayAccess();
		Constraint c = new XeqC(dictionary.getVariableArray(aa.getIdent())[aa.getIndex()], 1);
		pose(c);
	    }
	}
    }

    void generateConstraints(SimpleNode node, Tables table) {

// 	if (!storeLevelIncreased) {
// 	    System.out.println("1. Level="+store.level);
// 	    store.setLevel(store.level + 1);
// 	    storeLevelIncreased = true;
// 	    System.out.println("2. Level="+store.level);
// 	}

	dictionary = table;
 	this.zero = table.zero; 
	this.one = table.one; 

	//    	node.dump("");

	// Predicates
	if (node.getId() == JJTCONSTELEM) {

	    p = ((ASTConstElem)node).getName();

	    if (p.startsWith("float_") ) {
		System.err.println("ERROR: JaCoP does not suppoprt constraints on floats");
		System.exit(0);
	    }
	    // int_* predicates
	    else if (p.startsWith("int_") ) {
		int operation = comparisonPredicate(p, 4);
		// 	    System.out.println(p + " op = " + operation);

		if (p.startsWith("negate", 4)) {
		    int_negate(node);
		}

		// int_eq*, int_ne*, int_lt*, int_gt*, int_le*, and int_ge*
		else if ( operation != -1) {
		    int_comparison(operation, node, 6);
		}

		// int_lin_* (eq, ne, lt, gt, le, ge)
		else if (p.startsWith("lin_", 4)) {
		    operation = comparisonPredicate(p, 8);
		    int_lin_relation(operation, node);
		}

		else if (p.startsWith("plus", 4)) {

		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
		    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);

		    if (p1.getType() == 0) {// p1 int
			pose(new XplusCeqZ(getVariable(p2), getInt(p1), getVariable(p3)));
		    }
		    else if (p2.getType() == 0) {// p2 int
			pose(new XplusCeqZ(getVariable(p1), getInt(p2), getVariable(p3)));
		    }
		    else if (p3.getType() == 0) {// p3 int
			pose(new XplusYeqC(getVariable(p1), getVariable(p2), getInt(p3)));
		    }
		    else
			pose(new XplusYeqZ(getVariable(p1), getVariable(p2), getVariable(p3)));
		}

		else if (p.startsWith("minus", 4)) {
		    // p1 - p2 = p3 <=> p2 + p3 = p1
		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
		    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);

		    if (p2.getType() == 0) {// p2 int
			pose(new XplusCeqZ(getVariable(p3), getInt(p2), getVariable(p1)));
		    }
		    else if (p3.getType() == 0) {// p3 int
			pose(new XplusCeqZ(getVariable(p2), getInt(p3), getVariable(p1)));
		    }
		    else if (p3.getType() == 0) {// p1 int
			pose(new XplusYeqC(getVariable(p2), getVariable(p3), getInt(p1)));
		    }
		    else
			pose(new XplusYeqZ(getVariable(p2), getVariable(p3), getVariable(p1)));
		}
		else if (p.startsWith("times", 4)) {
		    // 		    node.dump("");

		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
		    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);

		    if (p1.getType() == 0) {// p1 int
			pose(new XmulCeqZ(getVariable(p2), getInt(p1), getVariable(p3)));
		    }
		    else if (p2.getType() == 0) {// p2 int
			pose(new XmulCeqZ(getVariable(p1), getInt(p2), getVariable(p3)));
		    }
		    else if (p3.getType() == 0) {// p3 int
			pose(new XmulYeqC(getVariable(p1), getVariable(p2), getInt(p3)));
		    }
		    else
			pose(new XmulYeqZ(getVariable(p1), getVariable(p2), getVariable(p3)));
		}
		else if (p.startsWith("div", 4)) {
		    // p1/p2 = p3 <=> p2 * p3 = p1
		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
		    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);

		    Variable v1 = getVariable(p1);
		    Variable v2 = getVariable(p2);
		    Variable v3 = getVariable(p3);

		    pose(new XdivYeqZ(v1, v2, v3));

// 		    Variable r = new Variable(store, JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);
//  		    Variable t = new Variable(store, JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);

// 		    pose(new XplusYeqZ(r, t, v1));

// 		    // follows new flatzinc standard; reminder has the same sign as the first argument
//  		    Variable absV2 = new Variable(store, 0, JaCoP.core.Constants.MaxInt);
//  		    Variable minusAbsV2 = new Variable(store, JaCoP.core.Constants.MinInt, 0);
// 		    pose(new AbsXeqY(v2, absV2));
// 		    pose(new XplusYeqC(minusAbsV2, absV2, 0));
//  		    pose(new IfThen(new XgtC(v1, 0), new And(new XltY(r, absV2), new XgteqC(r, 0))));
//   		    pose(new IfThen(new XltC(v1, 0), new And(new XgtY(r, minusAbsV2), new XlteqC(r, 0))));

// 		    // pose(new Eq(new XgtC(v2, 0), new And(new XltY(r, v2), new XgteqC(r, 0))));
// 		    // pose(new Eq(new XltC(v2, 0), new And(new XgtY(r, v2), new XlteqC(r, 0))));

// 		    pose(new XmulYeqZ(v2, v3, t));
		}
		else if (p.startsWith("mod", 4)) {
		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
		    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);

		    Variable v1 = getVariable(p1);
		    Variable v2 = getVariable(p2);
		    Variable v3 = getVariable(p3);

		    pose(new XmodYeqZ(v1, v2, v3));

// 		    Variable t1 = new Variable(store, JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);
// 		    Variable t2 = new Variable(store, JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);

// 		    pose(new XplusYeqZ(t2, v3, v1)); 
// 		    // follows new flatzinc standard; reminder has the same sign as the first argument
//  		    Variable absV2 = new Variable(store, 0, JaCoP.core.Constants.MaxInt);
//  		    Variable minusAbsV2 = new Variable(store, JaCoP.core.Constants.MinInt, 0);
// 		    pose(new AbsXeqY(v2, absV2));
// 		    pose(new XplusYeqC(minusAbsV2, absV2, 0));
//  		    pose(new IfThen(new XgtC(v1, 0), new And(new XltY(v3, absV2), new XgteqC(v3, 0))));
//  		    pose(new IfThen(new XltC(v1, 0), new And(new XgtY(v3, minusAbsV2), new XlteqC(v3, 0))));

//   		    // pose(new Eq(new XgtC(v2, 0), new And(new XltY(v3, v2), new XgteqC(v3, 0))));
//   		    // pose(new Eq(new XltC(v2, 0), new And(new XgtY(v3, v2), new XlteqC(v3, 0))));

// 		    pose(new XmulYeqZ(t1, v2, t2));
		}
		else if (p.startsWith("min", 4)) {
		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
		    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);

		    Variable v1 = getVariable(p1);
		    Variable v2 = getVariable(p2);
		    Variable v3 = getVariable(p3);

		    pose(new Min(v3, new Variable[] {v1, v2}));
		}
		else if (p.startsWith("max", 4)) {
		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
		    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);

		    Variable v1 = getVariable(p1);
		    Variable v2 = getVariable(p2);
		    Variable v3 = getVariable(p3);

		    pose(new Max(v3, new Variable[] {v1, v2}));
		}
		else if (p.startsWith("abs", 4)) {
		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);

		    Variable v1 = getVariable(p1);
		    Variable v2 = getVariable(p2);

		    pose(new AbsXeqY(v1, v2));
		}
		else
		    System.out.println("TODO: "+p);
	    }

	    // array_* predicates
	    else if (p.startsWith("array_") ) {
		if (p.startsWith("bool_", 6)) {
		    if (p.startsWith("and", 11)) {
			//  			node.dump("");

			SimpleNode p1 = (SimpleNode)node.jjtGetChild(0);
			ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
			Variable[] a1 = getVarArray(p1);
			Variable v = getVariable(p2);

			pose(new AndBool(a1, v));
			// 			Variable t1 = new Variable(store, 0, a1.length);
			//  			pose(new Sum(a1, t1));
			//  			pose(new Reified(new XeqC(t1, a1.length), v));
		    }
		    else if (p.startsWith("or", 11)) {
			//   			node.dump("");

			SimpleNode p1 = (SimpleNode)node.jjtGetChild(0);
			ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
			Variable[] a1 = getVarArray(p1);
			Variable v = getVariable(p2);

			//	Variable t1 = new Variable(store, 0, a1.length);

			pose(new OrBool(a1, v));
			//   			pose(new Sum(a1, t1));
			//   			pose(new Reified(new XgtC(t1, 0), v));
		    }
		    else if (p.startsWith("element", 11)) {
			// array_bool_element
			generateIntElementConstraint(node);
		    }
		    else
			System.err.println("ERROR: Not expected constraint : "+p);
		}
		else if (p.startsWith("var_bool_element", 6) ) {
		    // array_var_bool_element
		    generateVarElementConstraint(node);
		    // 		    generateElementConstraint(p, 0);
		}
		else if (p.startsWith("var_int_element", 6) ) {
		    // array_var_int_element
		    generateVarElementConstraint(node);
		    // 		    generateElementConstraint(p, 1);
		}
		else if (p.startsWith("int_element", 6)) {
		    // array_int_element
		    generateIntElementConstraint(node);
		}
		else if (p.startsWith("var_set_element", 6) ) {
		    // array_var_set_element
		    generateSetElementConstraint(node);
		}
		else if (p.startsWith("set_element", 6)) {
		    // array_set_element
		    generateVarSetElementConstraint(node);
		}
		else
		    System.out.println("TODO: "+p);
	    }

	    // bool_* predicates
	    else if (p.startsWith("bool_") ) {

		int operation = comparisonPredicate(p, 5);

		// bool_left_imp
		// a <- b <-> r
		//-------------
		// 0 <- 0 <-> 1
		// 0 <- 1 <-> 0
		// 1 <- 0 <-> 1
		// 1 <- 1 <-> 1
		if (p.startsWith("left_imp", 5)) {

		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
		    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);

		    Variable v1 = getVariable(p1);
		    Variable v2 = getVariable(p2);
		    Variable v3 = getVariable(p3);

		    pose(new IfThenBool(v2, v1, v3));
		    //  		    pose(new Reified(new Not(new And(new XeqC(v1, 0), new XeqC(v2, 1))), v3));
		    // 					pose(new Reified(new Or(new XneqC(v1, 0), new XneqC(v2, 1)), v3));

		    // 		    pose(new ExtensionalSupportMDD(new Variable[] {v1, v2, v3}, 
		    // 						   new int[][] {{0,0,1}, {0,1,0}, {1,0,1},{1,1,1}}));
		} else if ( operation != -1) {
		    // bool_eq*, bool_ne*, bool_lt*, bool_gt*, bool_le*, and bool_ge*
		    int_comparison(operation, node, 7);
		}
		// bool_or
		else if (p.startsWith("or", 5)) {

		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
		    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);

		    Variable v1 = getVariable(p1);
		    Variable v2 = getVariable(p2);
		    Variable v3 = getVariable(p3);

		    pose(new OrBool(new Variable[] {v1, v2}, v3));
		    // 		    pose(new Reified(new XplusYgtC(v1, v2, 0), v3));
		}
		// bool_and
		else if (p.startsWith("and", 5)) {

		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
		    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);

		    Variable v1 = getVariable(p1);
		    Variable v2 = getVariable(p2);
		    Variable v3 = getVariable(p3);

 		    pose(new AndBool(new Variable[] {v1, v2}, v3));
// 		    pose(new Reified(new XplusYeqC(v1, v2, 2), v3));
		}
		// bool_xor
		else if (p.startsWith("xor", 5)) {

		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
		    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);

		    Variable v1 = getVariable(p1);
		    Variable v2 = getVariable(p2);
		    Variable v3 = getVariable(p3);

		    pose(new XorBool(v1, v2, v3));
		    // 					pose(new Reified(new XneqY(v1, v2), v3));
		}
		// bool_not
		else if (p.startsWith("not", 5)) {

		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);

		    Variable v1 = getVariable(p1);
		    Variable v2 = getVariable(p2);

 		    pose(new XneqY(v1, v2));
// 		    pose(new XplusYeqC(v1, v2, 1));
		}
		// bool_right_imp
		// a -> b <-> r
		//-------------
		// 0 -> 0 <-> 1
		// 0 -> 1 <-> 1
		// 1 -> 0 <-> 0
		// 1 -> 1 <-> 1
		else if (p.startsWith("right_imp", 5)) {
		    // 		    node.dump("");
		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);
		    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);

		    Variable v1 = getVariable(p1);
		    Variable v2 = getVariable(p2);
		    Variable v3 = getVariable(p3);

		    pose(new IfThenBool(v1, v2, v3));
		    //  		    pose(new Reified(new Not(new And(new XeqC(v1, 1), new XeqC(v2, 0))), v3));
		    // 					pose(new Reified(new Or(new XneqC(v1, 1), new XneqC(v2, 0)), v3));

		    //  		    pose(new ExtensionalSupportMDD(new Variable[] {v1, v2, v3}, 
		    //  						   new int[][] {{0,0,1}, {0,1,1}, {1,0,0},{1,1,1}}));
		}
		// bool_clause[_reified]+
		// bool_clause([x1,..., xm], [y1,..., yn], true) ===>
		// ===> array_bool_or([x1,..., xm, not y1,..., not yn], true)
		else if (p.startsWith("clause", 5)) {

		    Variable[] a1 = getVarArray((SimpleNode)node.jjtGetChild(0));
		    Variable[] a2 = getVarArray((SimpleNode)node.jjtGetChild(1));

		    if (a1.length ==0 && a2.length ==0 )
			return;

		    BooleanVariable t3;
		    if (a1.length ==0 ) {
			BoundDomain d = new BoundDomain(0,0);
			t3 = new BooleanVariable(store,"", d);
		    }
		    else {
  			t3 = new BooleanVariable(store);
  			pose(new OrBool(a1, t3));
		    }

		    BooleanVariable t4;
		    if (a2.length ==0 ) {
			BoundDomain d = new BoundDomain(0,0);
			t4 = new BooleanVariable(store,"", d);
		    }
		    else {
			// de Morgan law not y1 \/ not y2 ...\/ not yn = not(y1 /\ y2 .../\ yn)
			BooleanVariable t2 = new BooleanVariable(store);
			t4 = new BooleanVariable(store);
			pose(new AndBool(a2, t2)); // and
			pose(new XneqY(t2,t4));  // not
		    }

		    if (p.startsWith("_reif", 11)) {
			Variable v3 = getVariable((ASTScalarFlatExpr)node.jjtGetChild(2));
			pose(new Reified(new XplusYgtC(t3, t4, 0), v3));
			//  			pose(new Reified(new Or(new XgtC(t3, 0), new XgtC(t4, 0)), v3));
		    }
		    else
			pose(new XplusYgtC(t3, t4, 0));
		    // 			pose(new Or(new XgtC(t3, 0), new XgtC(t4, 0)));

		}
		else
		    System.out.println("TODO: "+p);
		// 		<-------------
	    }

	    // set_* predicates
	    else if (p.startsWith("set_") ) {

		if (p.startsWith("eq", 4)) {

		    Variable v1 = getSetVariable(node, 0);
		    Variable v2 = getSetVariable(node, 1);

		    pose(new JaCoP.set.constraints.XeqY(v1, v2));
		}
		else if (p.startsWith("ne", 4)) {

		    Variable v1 = getSetVariable(node, 0);
		    Variable v2 = getSetVariable(node, 1);

		    pose(new Not(new JaCoP.set.constraints.XeqY(v1, v2)));
		}
		else if (p.startsWith("lt", 4)) {

		    Variable v1 = getSetVariable(node, 0);
		    Variable v2 = getSetVariable(node, 1);

		    pose(new Lex(v1, v2));
		}
		else if (p.startsWith("gt", 4)) {

		    Variable v1 = getSetVariable(node, 0);
		    Variable v2 = getSetVariable(node, 1);

		    pose(new Lex(v2, v1));
		}
		else if (p.startsWith("le", 4)) {
		    // not OK, Lex does not allow eq
		    Variable v1 = getSetVariable(node, 0);
		    Variable v2 = getSetVariable(node, 1);

		    pose(new Lex(v1, v2));
		    // 		    System.err.println("Used lex ordering constraint instead of set_le; Lex inforces strict order, not equality");
		}
		else if (p.startsWith("ge", 4))  {
		    // not OK, bo Lex does not allow eq
		    Variable v1 = getSetVariable(node, 0);
		    Variable v2 = getSetVariable(node, 1);

		    pose(new Lex(v2, v1));
		    // 		    System.err.println("Used lex ordering constraint instead of set_ge; Lex inforces strict order, not equality");
		}

		else  if (p.startsWith("intersect", 4)) {

		    Variable v1 = getSetVariable(node, 0);
		    Variable v2 = getSetVariable(node, 1);
		    Variable v3 = getSetVariable(node, 2);

		    pose(new XintersectYeqZ(v1, v2, v3));
		}
		else if (p.startsWith("card", 4)) {

		    Variable v1 = getSetVariable(node, 0);
		    Variable v2 = getVariable((ASTScalarFlatExpr)node.jjtGetChild(1));

		    pose(new Card(v1, v2));
		}
		else if (p.startsWith("in", 4)) {

		    ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		    Variable v2 = getSetVariable(node, 1);

		    PrimitiveConstraint c;
		    if (p1.getType() == 0) { // p1 int
			int i1 = getInt(p1);
			c = new EInS(i1, v2);
		    }
		    else { // p1 var
			Variable v1 = getVariable(p1);
			c = new XinY(v1, v2);
		    }

		    if (p.startsWith("_reif", 6)) {
			Variable v3 = getVariable((ASTScalarFlatExpr)node.jjtGetChild(2));

			pose(new Reified(c, v3));
		    }
		    else
			pose(c);
		}
		else if (p.startsWith("subset", 4)) {

		    Variable v1 = getSetVariable(node, 0);
		    Variable v2 = getSetVariable(node, 1);

		    PrimitiveConstraint c = new XinY(v1, v2);
		    if (p.startsWith("_reif", 10)) {
			Variable v3 = getVariable((ASTScalarFlatExpr)node.jjtGetChild(2));

			pose(new Reified(c, v3));
		    }
		    else
			pose(c);
		}
		else if (p.startsWith("superset", 4)) {

		    Variable v1 = getSetVariable(node, 0);
		    Variable v2 = getSetVariable(node, 1);

		    PrimitiveConstraint c = new XinY(v2, v1);
		    if (p.startsWith("_reif", 12)) {
			Variable v3 = getVariable((ASTScalarFlatExpr)node.jjtGetChild(2));

			pose(new Reified(c, v3));
		    }
		    else
			pose(c);
		}
		else if (p.startsWith("union", 4)) {

		    Variable v1 = getSetVariable(node, 0);
		    Variable v2 = getSetVariable(node, 1);
		    Variable v3 = getSetVariable(node, 2);

		    pose(new XunionYeqZ(v1, v2, v3));
		}
		else if (p.startsWith("diff", 4)) {

		    Variable v1 = getSetVariable(node, 0);
		    Variable v2 = getSetVariable(node, 1);
		    Variable v3 = getSetVariable(node, 2);

		    pose(new XdiffYeqZ(v1, v2, v3));
		}
		else if (p.startsWith("symdiff", 4)) {

		    Variable v1 = getSetVariable(node, 0);
		    Variable v2 = getSetVariable(node, 1);
		    Variable v3 = getSetVariable(node, 2);

		    Variable t1 = new Variable(store, "", new SetDomain(JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt));
		    Variable t2 = new Variable(store, "", new SetDomain(JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt));
		    pose(new XdiffYeqZ(v1, v2, t1));
		    pose(new XdiffYeqZ(v2, v1, t2));
		    pose(new XunionYeqZ(t1, t2, v3));
		}
		else
		    System.out.println("TODO: "+p);
	    }
	    // bool2int and int2bool coercion operations
	    else if (p.equals("bool2int") || p.equals("int2bool") ) {
		// 		node.dump("");

		ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
		ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);

		pose(new XeqY(getVariable(p1), getVariable(p2)));
	    }
	    // ========== JaCoP constraints ==================>>
	    else if (p.startsWith("jacop_"))
		if (p.startsWith("cumulative", 6)) {
		    Variable[] s = getVarArray((SimpleNode)node.jjtGetChild(0));
		    Variable[] d = getVarArray((SimpleNode)node.jjtGetChild(1));
		    Variable[] r = getVarArray((SimpleNode)node.jjtGetChild(2));
		    Variable b = getVariable((ASTScalarFlatExpr)node.jjtGetChild(3));

		    pose(new Cumulative(s, d, r, b));
		}
		else if (p.startsWith("circuit", 6)) {
		    Variable[] v = getVarArray((SimpleNode)node.jjtGetChild(0));

		    pose(new Circuit(store, v));
		}
		else if (p.startsWith("alldiff", 6)) {
		    Variable[] v = getVarArray((SimpleNode)node.jjtGetChild(0));

		    IntervalDomain dom = new IntervalDomain();
		    for (Variable var : v)
			dom = (IntervalDomain)dom.union( var.dom() );
		    if (v.length <= 100 && v.length == dom.getSize()) {
			// we do not not pose Alldistinct directly because of possible inconsistency with its 
			// intiallization; we collect all vectors and pose it at the end when all constraints are posed
			//pose(new Alldistinct(v));

			parameterListForAlldistincts.add(v);

			//			System.out.println("Alldistinct imposed");
		    }
		    else {
			pose(new Alldiff(v));
			//			System.out.println("Alldiff imposed");
		    }
		}
		else if (p.startsWith("alldistinct", 6)) {
		    Variable[] v = getVarArray((SimpleNode)node.jjtGetChild(0));
		    // we do not not pose Alldistinct directly because of possible inconsistency with its 
		    // intiallization; we collect all vectors and pose it at the end when all constraints are posed

// 		    pose(new Alldistinct(v));
		    parameterListForAlldistincts.add(v);
//  		    System.out.println("Alldistinct imposed "+ java.util.Arrays.asList(v));
		}
		else if (p.startsWith("among", 6)) {
		    Variable[] x = getVarArray((SimpleNode)node.jjtGetChild(0));
		    Set s = getSetLiteral((SimpleNode)node, 1);
		    Variable v = getVariable((ASTScalarFlatExpr)node.jjtGetChild(2));

		    pose(new Among(x, s, v));
		}
		else if (p.startsWith("among_var", 6)) {
		    Variable[] x = getVarArray((SimpleNode)node.jjtGetChild(0));
		    Variable[] s = getVarArray((SimpleNode)node.jjtGetChild(1));
		    Variable v = getVariable((ASTScalarFlatExpr)node.jjtGetChild(2));

		    // we do not not pose AmongVar directly because of possible inconsistency with its 
		    // intiallization; we collect all constraints and pose them at the end when all other constraints are posed

		    delayedConstraints.add(new AmongVar(x, s, v));
// 		    pose(new AmongVar(x, s, v));
		}
		else if (p.startsWith("gcc", 6)) {
		    Variable[] x = getVarArray((SimpleNode)node.jjtGetChild(0));
		    Variable[] c = getVarArray((SimpleNode)node.jjtGetChild(1));
		    int index_min = getInt((ASTScalarFlatExpr)node.jjtGetChild(2));
		    int index_max = index_min + c.length - 1;

		    for (int i=0; i<x.length; i++) {
			if (index_min>x[i].max() || index_max<x[i].min()) {
			    System.err.println("ERROR: gcc domain error in variable " + x[i]);
			    System.exit(0);
			}
			if (index_min>x[i].min() && index_min<x[i].max()) 
			    x[i].domain.inMin(store.level, x[i], index_min);
			if (index_max<x[i].max() && index_max>x[i].min())
			    x[i].domain.inMax(store.level, x[i], index_max);
		    }
		    // 		    System.out.println("c = " + Arrays.asList(x));

		    // =========> remove all non-existing-values counters
		    Domain gcc_dom = new IntervalDomain();
		    for (Variable v : x)
			gcc_dom = gcc_dom.union( v.dom() );
		    ArrayList<Variable> c_list = new ArrayList<Variable>();
		    for (int i=0; i<c.length; i++)
			if ( gcc_dom.contains(i+index_min ) )
			    c_list.add(c[i]);
			else
			    pose(new XeqC(c[i], 0));
		    Variable[] c_array = new Variable[c_list.size()];
		    c_array = c_list.toArray(c_array);
		    // =========>

		    pose(new GCC(x, c_array));
		}
		else if (p.startsWith("diff2", 6)) {
		    Variable[] v = getVarArray((SimpleNode)node.jjtGetChild(0));

		    Variable[][] r = new Variable[v.length/4][4];
		    for (int i=0; i<r.length; i++)
			for (int j=0; j<4; j++)
			    r[i][j] = v[4*i+j];

		    pose(new Diff2(store, r));
		}
		else if (p.startsWith("list_diff2", 6)) {
		    Variable[] x = getVarArray((SimpleNode)node.jjtGetChild(0));
		    Variable[] y = getVarArray((SimpleNode)node.jjtGetChild(1));
		    Variable[] lx = getVarArray((SimpleNode)node.jjtGetChild(2));
		    Variable[] ly = getVarArray((SimpleNode)node.jjtGetChild(3));

		    pose(new Diff2(store, x, y, lx, ly));
		}
		else if (p.startsWith("count", 6)) {
		    Variable[] x = getVarArray((SimpleNode)node.jjtGetChild(0));
		    int y = getInt((ASTScalarFlatExpr)node.jjtGetChild(1));
		    Variable c = getVariable((ASTScalarFlatExpr)node.jjtGetChild(2));

		    pose(new Count(y, x, c));
		}
		else if (p.startsWith("nvalue", 6)) {
		    Variable n = getVariable((ASTScalarFlatExpr)node.jjtGetChild(0));
		    Variable[] x = getVarArray((SimpleNode)node.jjtGetChild(1));

		    pose(new Values(x, n));
		}
		else if (p.startsWith("minimum", 6)) {
		    Variable n = getVariable((ASTScalarFlatExpr)node.jjtGetChild(0));
		    Variable[] x = getVarArray((SimpleNode)node.jjtGetChild(1));

		    pose(new Min(n, x));
		}
		else if (p.startsWith("maximum", 6)) {
		    Variable n = getVariable((ASTScalarFlatExpr)node.jjtGetChild(0));
		    Variable[] x = getVarArray((SimpleNode)node.jjtGetChild(1));

		    pose(new Max(n, x));
		}
		else if (p.startsWith("table_int", 6) ||
			 p.startsWith("table_bool", 6)) { 
		    Variable[] v = getVarArray((SimpleNode)node.jjtGetChild(0));
		    int size = v.length;

		    int[] tbl = getIntArray((SimpleNode)node.jjtGetChild(1));
		    int[][] t = new int[tbl.length/size][size];
		    for (int i=0; i<t.length; i++)
			for (int j=0; j<size; j++)
			    t[i][j] = tbl[size*i+j];

		    // we do not not pose AmongVar directly because of possible inconsistency with its 
		    // intiallization; we collect all constraints and pose them at the end when all other constraints are posed

		    delayedConstraints.add(new ExtensionalSupportMDD(v, t));
// 		    pose(new ExtensionalSupportMDD(v, t));
		}
		else if (p.startsWith("assignment", 6)) {
		    Variable[] f = getVarArray((SimpleNode)node.jjtGetChild(0));
		    Variable[] invf = getVarArray((SimpleNode)node.jjtGetChild(1));
		    int index_f = getInt((ASTScalarFlatExpr)node.jjtGetChild(2));
		    int index_invf = getInt((ASTScalarFlatExpr)node.jjtGetChild(3));

		    // we do not not pose AmongVar directly because of possible inconsistency with its 
		    // intiallization; we collect all constraints and pose them at the end when all other constraints are posed

		    delayedConstraints.add(new Assignment(f, invf, index_f, index_invf));

// 		    pose(new Assignment(f, invf, index_f, index_invf));
		}
		else if (p.startsWith("regular", 6)) {

		    Variable[] x = getVarArray((SimpleNode)node.jjtGetChild(0));
		    int Q = getInt((ASTScalarFlatExpr)node.jjtGetChild(1));
		    int S = getInt((ASTScalarFlatExpr)node.jjtGetChild(2));
		    int[] d = getIntArray((SimpleNode)node.jjtGetChild(3));
		    int q0 = getInt((ASTScalarFlatExpr)node.jjtGetChild(4));
		    Set F = getSetLiteral((SimpleNode)node, 5);
		    int minIndex = getInt((ASTScalarFlatExpr)node.jjtGetChild(6));

		    // Build DFA
		    FSM dfa = new FSM();
		    FSMState[] s = new FSMState[Q]; 
		    for (int i=0; i<s.length; i++) { 
			s[i] = new FSMState(); 
			dfa.states.add(s[i]); 
		    } 
		    dfa.initState = s[q0 - 1]; 
		    SetValueEnumeration final_states = new SetValueEnumeration(F);
		    while (final_states.hasMoreElements())
			dfa.finalStates.add(s[final_states.nextElement()-1]); 

		    //  		    System.out.println("init state: "+ dfa.initState+", "+ F + "  final states: "+dfa.finalStates +",  first state: "+ s[0]);

		    for (int i=0; i<Q; i++) {
			//  			System.out.print(i+": ");
			for (int j=0; j<S; j++)
			    if (d[i*S+j] != 0) {
				s[i].transitions.add(new FSMTransition(new IntervalDomain(j+minIndex,j+minIndex), s[d[i*S+j]-minIndex])); 
				//   				System.out.print("("+(int)(j+minIndex)+") -> "+ (int)(d[i*S+j]-minIndex)+", ");
			    }
			//    			System.out.println();
		    }

		    pose(new Regular(dfa, x));

		    //  		    System.out.println(dfa+"\n");
		    //   		    System.out.println("Regular("+Arrays.asList(x)+", "+Q+", "+
		    //  				       S+", "+Arrays.asList(d)+", "+q0+", "+
		    //  				       dfa.finalStates+", "+minIndex+")");
		}
		else if (p.startsWith("knapsack", 6)) {
		    int[] weights = getIntArray((SimpleNode)node.jjtGetChild(0));
		    int[] profits = getIntArray((SimpleNode)node.jjtGetChild(1));
		    Variable W = getVariable((ASTScalarFlatExpr)node.jjtGetChild(2));
		    Variable P = getVariable((ASTScalarFlatExpr)node.jjtGetChild(3));
		    Variable[] x = getVarArray((SimpleNode)node.jjtGetChild(4));

		    pose(new Knapsack(profits, weights, x, W, P));
		}
	    // 		else if (p.startsWith("sequence", 6)) {
	    // 		    Variable[] x = getVarArray((SimpleNode)node.jjtGetChild(0));
	    // 		    Set u = getSetLiteral((SimpleNode)node, 1);
	    // 		    int q = getInt((ASTScalarFlatExpr)node.jjtGetChild(2));
	    // 		    int min = getInt((ASTScalarFlatExpr)node.jjtGetChild(3));
	    // 		    int max = getInt((ASTScalarFlatExpr)node.jjtGetChild(4));

	    //  		    DecomposedConstraint c = new Sequence(x, u, q, min, max);
	    // 		    System.out.println("sequence("+Arrays.asList(x)+", "+
	    // 				       u+", "+q+", "+", "+min+", "+max);
	    //   		    store.imposeDecomposition(c);
	    // 		}
		else
		    System.err.println("ERROR: Constraint "+p+" not supported.");
	    // >>========== JaCoP constraints ==================
	    else
		System.err.println("ERROR: Constraint "+p+" not supported.");
	}
    }

    int comparisonPredicate(String name, int offset) {

	if (name.startsWith("eq", offset))
	    return eq;
	else if (name.startsWith("ne", offset))
	    return ne;
	else if (name.startsWith("lt", offset))
	    return lt;
	else if (name.startsWith("gt", offset))
	    return gt;
	else if (name.startsWith("le", offset))
	    return le;
	else if (name.startsWith("ge", offset))
	    return ge;
	else return -1;
    }

    void int_negate(SimpleNode node) {
	// 	 node.dump("");

	// p1 + p2 = 0
	ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
	ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);

	PrimitiveConstraint c = null;
	if (p2.getType() == 0) { // p2 int
	    Variable v1 = getVariable(p1);
	    c = new XplusCeqZ(v1, p2.getInt(), zero);
	}
	else if (p1.getType() == 0) { // p1 int
	    Variable v2 = getVariable(p2);
	    c = new XplusCeqZ(v2, p1.getInt(), zero);
	}
	else {
	    Variable v1 = getVariable(p1);
	    Variable v2 = getVariable(p2);
	    c = new XplusYeqZ(v1, v2, zero);
	}
	pose(c);
    }



//     void int_comparison(int operation, SimpleNode node, int reifStart) {

// 	//   	 node.dump("");

// 	ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
// 	ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);

// 	boolean reified = false; 
// 	if (p.startsWith("_reif", reifStart)) {
// 	    reified = true;
// 	}

// 	if (reified) { // reified constraint
// 	    PrimitiveConstraint c = null;
// 	    Variable v1 = getVariable(p1);
// 	    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);
// 	    Variable v3 = getVariable(p3);

// 	    if (p2.getType() == 0) { // var rel int
// 		int i2 = getInt(p2);
// 		switch (operation) {
// 		case eq :
// 		    c = new XeqC(v1, i2);
// 		    break;
// 		case ne :
// 		    c = new XneqC(v1, i2);
// 		    break;
// 		case lt :
// 		    c = new XltC(v1, i2);
// 		    break;
// 		case gt :
// 		    c = new XgtC(v1, i2);
// 		    break;
// 		case le :
// 		    // 		v1.domain.in(store.level, v1, JaCoP.core.Constants.MinInt, i2);
// 		    c = new XlteqC(v1, i2);
// 		    break;
// 		case ge :
// 		    c = new XgteqC(v1, i2);
// 		    break;
// 		}
// 	    }
// 	    else { // var rel var
// 		Variable v2 = getVariable(p2);

// 		switch (operation) {
// 		case eq :
// 		    c = new XeqY(v1, v2);
// 		    break;
// 		case ne :
// 		    c = new XneqY(v1, v2);
// 		    break;
// 		case lt :
// 		    c = new XltY(v1, v2);
// 		    break;
// 		case gt :
// 		    c = new XgtY(v1, v2);
// 		    break;
// 		case le :
// 		    c = new XlteqY(v1, v2);
// 		    break;
// 		case ge :
// 		    c = new XgteqY(v1, v2);
// 		    break;
// 		}
// 	    }
//  	    Constraint cr = new Reified(c, v3);
//  	    pose(cr);
// 	}
// 	else  { // not reified constraints

// 	    if (p1.getType() == 0) { // first parameter int
// 		if (p2.getType() == 0) { // first parameter int & second parameter int
// 		    int i1 = getInt(p1);
// 		    int i2 = getInt(p2);
// 		    switch (operation) {
// 		    case eq :
// 			if (i1 != i2) throw new FailException();
// 			break;
// 		    case ne :
// 			if (i1 == i2) throw new FailException();
// 			break;
// 		    case lt :
// 			if (i1 >= i2) throw new FailException();
// 			break;
// 		    case gt :
// 			if (i1 <= i2) throw new FailException();
// 			break;
// 		    case le :
// 			if (i1 > i2) throw new FailException();
// 			break;
// 		    case ge :
// 			if (i1 < i2) throw new FailException();
// 			break;
// 		    }
// 		} 
// 		else { // first parameter int & second parameter var

// 		    int i1 = getInt(p1);
// 		    Variable v2 = getVariable(p2);

// 		    switch (operation) {
// 		    case eq :
// 			v2.domain.in(store.level, v2, i1, i1);
// 			break;
// 		    case ne :
// 			v2.domain.inComplement(store.level, v2, i1);
// 			break;
// 		    case lt :
// 			v2.domain.in(store.level, v2, i1+1, JaCoP.core.Constants.MaxInt);
// 			break;
// 		    case gt :
// 			v2.domain.in(store.level, v2, JaCoP.core.Constants.MinInt, i1-1);
// 			break;
// 		    case le :
// 			v2.domain.in(store.level, v2, i1, JaCoP.core.Constants.MaxInt);
// 			break;
// 		    case ge :
// 			v2.domain.in(store.level, v2, JaCoP.core.Constants.MinInt, i1);
// 			break;
// 		    }
// 		}
// 	    }
// 	    else { // first parameter var
// 		if (p2.getType() == 0) { // first parameter var & second parameter int

// 		    Variable v1 = getVariable(p1);
// 		    int i2 = getInt(p2);

// 		    switch (operation) {
// 		    case eq :
// 			v1.domain.in(store.level, v1, i2, i2);
// 			break;
// 		    case ne :
// 			v1.domain.inComplement(store.level, v1, i2);
// 			break;
// 		    case lt :
// 			v1.domain.in(store.level, v1, JaCoP.core.Constants.MinInt, i2-1);
// 			break;
// 		    case gt :
// 			v1.domain.in(store.level, v1, i2+1, JaCoP.core.Constants.MaxInt);
// 			break;
// 		    case le :
// 			v1.domain.in(store.level, v1, JaCoP.core.Constants.MinInt, i2);
// 			break;
// 		    case ge :
// 			v1.domain.in(store.level, v1, i2, JaCoP.core.Constants.MaxInt);
// 			break;
// 		    }

// 		} 
// 		else { // first parameter var & second parameter var

// 		    PrimitiveConstraint c = null;
// 		    Variable v1 = getVariable(p1);
// 		    Variable v2 = getVariable(p2);

// 		    switch (operation) {
// 		    case eq :
// 			c = new XeqY(v1, v2);
// 			break;
// 		    case ne :
// 			c = new XneqY(v1, v2);
// 			break;
// 		    case lt :
// 			c = new XltY(v1, v2);
// 			break;
// 		    case gt :
// 			c = new XgtY(v1, v2);
// 			break;
// 		    case le :
// 			c = new XlteqY(v1, v2);
// 			break;
// 		    case ge :
// 			c = new XgteqY(v1, v2);
// 			break;
// 		    }
// 		    pose(c);
// 		}
// 	    }

// 	}
//     }



    void int_comparison(int operation, SimpleNode node, int reifStart) {

	//   	 node.dump("");

	ASTScalarFlatExpr p1 = (ASTScalarFlatExpr)node.jjtGetChild(0);
	ASTScalarFlatExpr p2 = (ASTScalarFlatExpr)node.jjtGetChild(1);

	PrimitiveConstraint c = null;

	if (p2.getType() == 0) { // var rel int
	    int i2 = getInt(p2);
	    Variable v1 = getVariable(p1);
	    switch (operation) {
	    case eq :
		c = new XeqC(v1, i2);
		break;
	    case ne :
		c = new XneqC(v1, i2);
		break;
	    case lt :
		c = new XltC(v1, i2);
		break;
	    case gt :
		c = new XgtC(v1, i2);
		break;
	    case le :
 		c = new XlteqC(v1, i2);
		break;
	    case ge :
		c = new XgteqC(v1, i2);
		break;
	    }
	} else if (p1.getType() == 0) { // int rel var
	    int i1 = getInt(p1);
	    Variable v2 = getVariable(p2);
	    switch (operation) {
	    case eq :
		c = new XeqC(v2, i1);
		break;
	    case ne :
		c = new XneqC(v2, i1);
		break;
	    case lt :
		c = new XgtC(v2, i1);
		break;
	    case gt :
		c = new XltC(v2, i1);
		break;
	    case le :
 		c = new XgteqC(v2, i1);
		break;
	    case ge :
		c = new XlteqC(v2, i1);
		break;
	    }
	}
	else { // var rel var
	    Variable v1 = getVariable(p1);
	    Variable v2 = getVariable(p2);
	    switch (operation) {
	    case eq :
		c = new XeqY(v1, v2);
		break;
	    case ne :
		c = new XneqY(v1, v2);
		break;
	    case lt :
		c = new XltY(v1, v2);
		break;
	    case gt :
		c = new XgtY(v1, v2);
		break;
	    case le :
		c = new XlteqY(v1, v2);
		break;
	    case ge :
		c = new XgteqY(v1, v2);
		break;
	    }
	}
	if (p.startsWith("_reif", reifStart)) {
	    ASTScalarFlatExpr p3 = (ASTScalarFlatExpr)node.jjtGetChild(2);
	    Variable v3 = getVariable(p3);
	    Constraint cr = new Reified(c, v3);
	    pose(cr);
	}
	else {
	    pose(c);
	}
    }

    void int_lin_relation(int operation, SimpleNode node) {
	// int_lin_*[_reif] (* = eq | ne | lt | gt | le | ge)

	// 	node.dump("");

	int[] p1 = getIntArray((SimpleNode)node.jjtGetChild(0));
	Variable[] p2 = getVarArray((SimpleNode)node.jjtGetChild(1));
	int p3 = getInt((ASTScalarFlatExpr)node.jjtGetChild(2));

	if (p.startsWith("_reif", 10)) { // reified
	    Variable p4 = getVariable((ASTScalarFlatExpr)node.jjtGetChild(3));

	    Variable t;
	    switch (operation) {
	    case eq :
		if (p1.length == 2 && p1[0] == 1 && p1[1] == -1) {
		    pose(new Reified(new XplusCeqZ(p2[1], p3, p2[0]), p4));
		}
		else if (p1.length == 2 && p1[0] == -1 && p1[1] == 1) {
		    pose(new Reified(new XplusCeqZ(p2[0], p3, p2[1]), p4));
		} else
		    {
		    t = new Variable(store, JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);
		    pose(new SumWeight(p2, p1, t));
		    pose(new Reified(new XeqC(t, p3), p4));
		}
		break;
	    case ne :
		t = new Variable(store, JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);
		pose(new SumWeight(p2, p1, t));
		pose(new Reified(new XneqC(t, p3), p4));
		break;
	    case lt :
		t = new Variable(store, JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);
		pose(new SumWeight(p2, p1, t));
		pose(new Reified(new XltC(t, p3), p4));
		break;
	    case gt :
		t = new Variable(store, JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);
		pose(new SumWeight(p2, p1, t));
		pose(new Reified(new XgtC(t, p3), p4));
		break;
	    case le :
		t = new Variable(store, JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);
		pose(new SumWeight(p2, p1, t));
		pose(new Reified(new XlteqC(t, p3), p4));
		break;
	    case ge :
		t = new Variable(store, JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);
		pose(new SumWeight(p2, p1, t));
		pose(new Reified(new XgteqC(t, p3), p4));
		break;
	    }
	}
	else { // non reified
	    Variable t;
	    switch (operation) {
	    case eq :
		if (p1.length == 2 && p1[0] == 1 && p1[1] == -1) {
		    pose(new XplusCeqZ(p2[1], p3, p2[0]));
		}
		else if (p1.length == 2 && p1[0] == -1 && p1[1] == 1) {
		    pose(new XplusCeqZ(p2[0], p3, p2[1]));
		}
		else if (p1.length == 2 && p1[0] == 1 && p1[1] == 1) {
		    pose(new XplusYeqC(p2[0], p2[1], p3));
		}
		else {

// 		    Variable tt=zero, t_next=null;
// 		    for (int i=0; i<p1.length; i++) {
// 			if (p1[i] == 1)
// 			    if (i == p1.length-1) 
// 				pose(new XplusYeqC(tt, p2[i], p3));
// 			    else {
// 				t_next=new Variable(store,JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt); 
// 				pose(new XplusYeqZ(tt, p2[i], t_next));
// 			    }
// 			else {
// 			    Variable temp = new Variable(store,JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt); 
// 			    if (i == p1.length-1) {
// 				pose(new XmulCeqZ(p2[i], p1[i], temp));
// 				pose(new XplusYeqC(tt, temp, p3));
// 			    }
// 			    else {
// 				t_next=new Variable(store,JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt); 
// 				pose(new XmulCeqZ(p2[i], p1[i], temp));
// 				pose(new XplusYeqZ(tt, temp, t_next));
// 			    }
// 			}
// 			tt = t_next;
// 		    }

		    Variable v;
		    if (p3==0) v = zero;
		    else if (p3 == 1) v = one;
		    else v = new Variable(store, p3, p3);
		    pose(new SumWeight(p2, p1, v));

		}
		break;
	    case ne :
		IntervalDomain dne = new IntervalDomain(JaCoP.core.Constants.MinInt, p3-1);
		dne.addDom(p3+1, JaCoP.core.Constants.MaxInt);
		t = new Variable(store, "", dne); //JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);
		pose(new SumWeight(p2, p1, t));
// 		pose(new XneqC(t, p3));
		break;
	    case lt :
		t = new Variable(store, JaCoP.core.Constants.MinInt, p3-1);//JaCoP.core.Constants.MaxInt);
		pose(new SumWeight(p2, p1, t));
// 		pose(new XltC(t, p3));
		break;
	    case gt :
		t = new Variable(store, p3+1, JaCoP.core.Constants.MaxInt);//JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);
		pose(new SumWeight(p2, p1, t));
// 		pose(new XgtC(t, p3));
		break;
	    case le :
		t = new Variable(store, JaCoP.core.Constants.MinInt, p3); // JaCoP.core.Constants.MaxInt);
		pose(new SumWeight(p2, p1, t));
// 		pose(new XlteqC(t, p3));
		break;
	    case ge :
		t = new Variable(store, p3, JaCoP.core.Constants.MaxInt);//JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);
		pose(new SumWeight(p2, p1, t));
// 		pose(new XgteqC(t, p3));
		break;
	    }
	}
    }


    void generateIntElementConstraint(SimpleNode node) {
	Variable p1 = getVariable((ASTScalarFlatExpr)node.jjtGetChild(0));
	int[] p2 = getIntArray((SimpleNode)node.jjtGetChild(1));
	Variable p3 = getVariable((ASTScalarFlatExpr)node.jjtGetChild(2));

	pose(new Element(p1, p2, p3));
    }

    void generateVarElementConstraint(SimpleNode node) {
	Variable p1 = getVariable((ASTScalarFlatExpr)node.jjtGetChild(0));	    
	Variable p3 = getVariable((ASTScalarFlatExpr)node.jjtGetChild(2));

	int[] p2 = getIntArray((SimpleNode)node.jjtGetChild(1));
	if (p2 != null)
	    pose(new Element(p1, p2, p3));
	else {
	    Variable[] p2var = getVarArray((SimpleNode)node.jjtGetChild(1));
	    pose(new Element(p1, p2var, p3));
	}
    }

    void generateSetElementConstraint(SimpleNode node) {
	Variable p1 = getVariable((ASTScalarFlatExpr)node.jjtGetChild(0));
	Set[] p2 = getSetArray((SimpleNode)node.jjtGetChild(1));
	Variable p3 = getSetVariable((SimpleNode)node, 2);

	pose(new ElementSet(p1, p2, p3));
    }

    void generateVarSetElementConstraint(SimpleNode node) {

	Variable p1 = getVariable((ASTScalarFlatExpr)node.jjtGetChild(0));	    
	Variable p3 = getSetVariable((SimpleNode)node, 2);

	Set[] p2 = getSetArray((SimpleNode)node.jjtGetChild(1));
	if (p2 != null)
	    pose(new ElementSet(p1, p2, p3));
	else {
	    System.err.println("var_set_element with list of set variables is not avaible in JaCoP.set");
	    System.exit(0);
	}
    }

    int getInt(ASTScalarFlatExpr node) {
	intPresent = true;

	if (node.getType() == 0) //int
	    return node.getInt();
	else if (node.getType() == 2) // ident
	    return dictionary.getInt(node.getIdent());
	else if (node.getType() == 3) {// array access
	    int[] intTable = dictionary.getIntArray(node.getIdent());
	    if (intTable == null) {
		intPresent = false;
		return Integer.MIN_VALUE;
	    }
	    else
		return intTable[node.getInt()];
	}
	else {
	    System.err.println("Wrong parameter " + node);
	    System.exit(0);
	    return 0;
	}
    }

    int getScalarFlatExpr(SimpleNode node, int i) {
	SimpleNode child = (SimpleNode)node.jjtGetChild(i);
	if (child.getId() == JJTSCALARFLATEXPR) {
	    switch ( ((ASTScalarFlatExpr)child).getType() ) {
	    case 0: // int
		return ((ASTScalarFlatExpr)child).getInt();
	    case 1: // bool
		return ((ASTScalarFlatExpr)child).getInt();
	    case 2: // ident
		return dictionary.getInt(((ASTScalarFlatExpr)child).getIdent());
	    case 3: // array acces
		return dictionary.getIntArray(((ASTScalarFlatExpr)child).getIdent())[((ASTScalarFlatExpr)child).getInt()];
	    default: // string & float;
		System.err.println("Not supported scalar in parameter; compilation aborted."); 
		System.exit(0);
	    }
	}
	else {
	    System.err.println("Not supported parameter assignment; compilation aborted."); 
	    System.exit(0);
	}
	return -1;
    }

    // TODO, the functions below look awfully familiar to the functions from the Tables.java, maybe to be consistent they should be in Tables?
	
    Set[] getSetArray(SimpleNode node) {
	Set[] s=null;
	int arrayIndex=0;

	if (node.getId() == JJTARRAYLITERAL) {
	    int count = node.jjtGetNumChildren();
	    s = new Set[count];
	    for (int i=0; i<count; i++) {
		s[arrayIndex++] = getSetLiteral(node, i);
	    }
	}
	else if (node.getId() == JJTSCALARFLATEXPR) {
	    if ( ((ASTScalarFlatExpr)node).getType() == 2) {// ident
		s = dictionary.getSetArray(((ASTScalarFlatExpr)node).getIdent());
	    }
	    else {
		System.err.println("Wring set array."); 
		System.exit(0);
	    }
	}
	return s;
    }

    Set getSetLiteral(SimpleNode node, int index) {
	SimpleNode child = (SimpleNode)node.jjtGetChild(index);
	if (child.getId() == JJTSETLITERAL) {
	    switch ( ((ASTSetLiteral)child).getType() ) {
	    case 0: // interval
		SimpleNode grand_child_1 = (SimpleNode)child.jjtGetChild(0);
		SimpleNode grand_child_2 = (SimpleNode)child.jjtGetChild(1);
		if (grand_child_1.getId() == JJTINTFLATEXPR && grand_child_2.getId() == JJTINTFLATEXPR) {
		    int i1 = ((ASTIntFlatExpr)grand_child_1).getInt();
		    int i2 = ((ASTIntFlatExpr)grand_child_2).getInt();
		    return new Set(i1, i2);
		}
	    case 1: // list
		Set s= new Set();
		int el=-1111;
		int count = child.jjtGetNumChildren();
		for (int i=0;i<count;i++) {
		    el = getScalarFlatExpr(child, i);
		    s.addDom(el);
		}
		return s;
	    default: 
		System.err.println("Set type not supported; compilation aborted."); 
		System.exit(0);
	    }
	}
	else if (child.getId() == JJTSCALARFLATEXPR) {
	    switch ( ((ASTScalarFlatExpr)child).getType() ) {
	    case 0: // int
	    case 1: // bool
		System.err.println("Set initialization fault; compilation aborted."); 
		System.exit(0);
		break;
	    case 2: // ident
		return dictionary.getSet(((ASTScalarFlatExpr)child).getIdent());
	    case 3: // array access
		return dictionary.getSetArray(((ASTScalarFlatExpr)child).getIdent())[((ASTScalarFlatExpr)child).getInt()];
	    case 4: // string
	    case 5: // float
		System.err.println("Set initialization fault; compilation aborted."); 
		System.exit(0);
		break;
	    }
	}
	return new Set();
    }

    int[] getIntArray(SimpleNode node) {
	if (node.getId() == JJTARRAYLITERAL) {
	    int count = node.jjtGetNumChildren();
	    int[] aa = new int[count];
	    for (int i=0;i<count;i++) {
		ASTScalarFlatExpr child = (ASTScalarFlatExpr)node.jjtGetChild(i);
		int el = getInt(child);
// 		if (el == Integer.MIN_VALUE)
		if (! intPresent)
		    return null;
		else
		    aa[i] = el;
	    }
	    return aa;
	}
	else if (node.getId() == JJTSCALARFLATEXPR) {
	    if (((ASTScalarFlatExpr)node).getType() == 2) // ident
		return dictionary.getIntArray(((ASTScalarFlatExpr)node).getIdent());
	    else {
		System.err.println("Wrong type of int array; compilation aborted."); 
		System.exit(0);
		return new int[] {};
	    }
	}
	else {
	    System.err.println("Wrong type of int array; compilation aborted."); 
	    System.exit(0);
	    return new int[] {};
	}
    }

    Variable getVariable(ASTScalarFlatExpr node) {
	if (node.getType() == 0) {// int
	    int val = node.getInt();
	    if (val == 0) return zero;
	    else if (val == 1) return one;
	    else return new Variable(store, val, val);
	}
	if (node.getType() == 1) {// bool
	    int val = node.getInt();
	    if (val == 0) return zero;
	    else if (val == 1) return one;
	    return new Variable(store, val, val);
	}
	else if (node.getType() == 2) // ident
	    return dictionary.getVariable(node.getIdent());
	else if (node.getType() == 3) {// array access
	    if (node.getInt() > dictionary.getVariableArray(node.getIdent()).length ||
		node.getInt() < 0) {
		System.out.println("Index out of bound for " + node.getIdent() + "["+node.getInt()+"]");
		System.exit(0);
		return new Variable(store);
	    }
	    else
		return dictionary.getVariableArray(node.getIdent())[node.getInt()];
	}
	else {
	    System.err.println("Wrong parameter " + node);
	    System.exit(0);
	    return new Variable(store);
	}
    }

    Variable getSetVariable(SimpleNode node, int index) {

	SimpleNode child = (SimpleNode )node.jjtGetChild(index);
	if (child.getId() == JJTSETLITERAL) {
	    int count = child.jjtGetNumChildren();
	    if (count == 0)
		return new Variable(store, "", new SetDomain(new Set(), new Set()));
	    else {
		Set s2 = getSetLiteral((SimpleNode)node, index);
		return new Variable(store, "", new SetDomain(s2, s2));
	    }
	}
	else if (child.getId() == JJTSCALARFLATEXPR) {
	    if (((ASTScalarFlatExpr)child).getType() == 2) { // ident
		Variable v =  dictionary.getSetVariable(((ASTScalarFlatExpr)child).getIdent());
		if (v != null)
		    return v;  // Variable ident
		else { // Set ident
		    Set s = dictionary.getSet(((ASTScalarFlatExpr)child).getIdent());
		    return new Variable(store, "", new SetDomain(s, s));
		}
	    }
	    else if (((ASTScalarFlatExpr)child).getType() == 3) // array access
		return dictionary.getSetVariableArray(((ASTScalarFlatExpr)child).getIdent())[((ASTScalarFlatExpr)child).getInt()];
	    else {
		System.err.println("Wrong parameter in set " + child);
		System.exit(0);
		return new Variable(store);
	    }
	}
	else {
	    System.err.println("Wrong parameter in set " + child);
	    System.exit(0);
	    return new Variable(store);
	}
    }

    Variable[] getVarArray(SimpleNode node) {
	if (node.getId() == JJTARRAYLITERAL) {
	    int count = node.jjtGetNumChildren();
	    Variable[] aa = new Variable[count];
	    for (int i=0;i<count;i++) {
		ASTScalarFlatExpr child = (ASTScalarFlatExpr)node.jjtGetChild(i);
		Variable el = getVariable(child);
		aa[i] = el;
	    }
	    return aa;
	}
	else if (node.getId() == JJTSCALARFLATEXPR) {
	    if (((ASTScalarFlatExpr)node).getType() == 2) {// ident
		// array of var
		Variable[] v = dictionary.getVariableArray(((ASTScalarFlatExpr)node).getIdent());
		if (v != null)
		    return v;
		else { // array of int
		    int[] ia = dictionary.getIntArray(((ASTScalarFlatExpr)node).getIdent());
		    if (ia != null) {
			Variable[] aa = new Variable[ia.length];
			for (int i=0; i<ia.length; i++)
			    aa[i] = new Variable(store, ia[i], ia[i]);
			return aa;
		    }
		    else {
			System.err.println("Cannot find array " +((ASTScalarFlatExpr)node).getIdent() +
					   "; compilation aborted."); 
			System.exit(0);
			return new Variable[] {};
		    }
		}
	    }
	    else {
		System.err.println("Wrong type of Variable array; compilation aborted."); 
		System.exit(0);
		return new Variable[] {};
	    }
	}
	else {
	    System.err.println("Wrong type of Variable array; compilation aborted."); 
	    System.exit(0);
	    return new Variable[] {};
	}
    }

//     String int_array2string(int[] a) {
// 	String s = "[";
// 	for (int i=0; i<a.length; i++) 
// 	    if (i<a.length-1) 
// 		s += "" + a[i] + ", ";
// 	    else 
// 		s += "" + a[i];
// 	s += "]";
// 	return s;
//     }

//     String var_array2string(Variable[] a) {
// 	String s = "[";
// 	for (int i=0; i<a.length; i++) 
// 	    if (i<a.length-1) 
// 		s += "" + a[i] + ", ";
// 	    else 
// 		s += "" + a[i];
// 	s += "]";
// 	return s;
//     }


    void poseDelayedConstraints() {
	for (Constraint c : delayedConstraints) {
	    store.impose(c);
 	    if (debug)
 		System.out.println(c);
	}
	poseAlldistinctConstraints();
    }

    void poseAlldistinctConstraints() {
 	for (Variable[] v : parameterListForAlldistincts) {
      	    if (debug)
 		System.out.println("Alldistinct("+java.util.Arrays.asList(v)+")");
 	    store.impose(new Alldistinct(v));
 	}
    }

    void pose(Constraint c) {
	if (debug)
	    System.out.println(c);
	store.imposeWithConsistency(c);	
// 	store.impose(c);
    }
}
