/**
 *  VariablesParameters.java 
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
import java.util.HashSet;
import JaCoP.set.core.SetDomain;
import JaCoP.set.core.Set;
import JaCoP.core.BoundDomain;
import JaCoP.core.IntervalDomain;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.core.BooleanVariable;
import JaCoP.constraints.XeqC;

/**
 * TODO, a short description what it does and how it is used. Remark, 
 * it would be beneficial if all the methods were described, like
 * generateParameters(...) below.
 * 
 * @author Krzysztof Kuchcinski
 *
 */
public class VariablesParameters implements ParserTreeConstants {

    Tables dictionary;
    int lowInterval, highInterval;
    ArrayList<Integer> intList;
    HashSet<String> annotations;
    ArrayList<Set> indexBounds;

    int numberBooleanVariables=0;

    /**
     * It constructs variables parameters. 
     */
    public VariablesParameters() {}

    /**
     * It generates a parameter from a given node and stores information about it in the table. 
     * 
     * @param node the node from which the parameter is being generated.
     * @param table the table where the parameters are being stored.
     */
    void generateParameters(SimpleNode node, Tables table) {

	dictionary = table;
	annotations = new HashSet<String>();

	int type = getType(node);

	int initChild = getAnnotations(node, 1);

	// 	node.dump("");
	//    	System.out.println("*** Type = " + type + " init index = " + initChild);
	//    	System.out.println("*** Annotations: " + annotations);

	String ident;
	int val;
	Set setValue;
	switch (type ) {
	case 0: // int
	case 1: // int interval
	case 2: // int list
	case 3: // bool
	    ident = ((ASTVarDeclItem)node).getIdent();
	    val = getScalarFlatExpr(node, initChild);
	    table.addInt(ident, val);
	    break;
	case 4: // set int
	case 5: // set interval
	case 6: // set list
	case 7: // bool set
	    ident = ((ASTVarDeclItem)node).getIdent();
	    setValue = getSetLiteral(node, initChild);
	    table.addSet(ident, setValue);
	    break;
	default: 
	    System.err.println("Not supported type in parameter; compilation aborted."); 
	    System.exit(0);
	}
    }

    void generateVariables(SimpleNode node, Tables table, Store store) {

	dictionary = table;
	annotations = new HashSet<String>();
	boolean var_introduced = false, output_var=false;
	OutputArrayAnnotation outArrayAnn=null;

	int type = getType(node);

	int initChild = getAnnotations(node, 1);

	// 	node.dump("");
	//    	System.out.println("*** Type = " + type + " init index = " + initChild);
	//    	System.out.println("*** Annotations: " + annotations);

	if (annotations.contains("var_is_introduced"))
	    var_introduced = true;
	if (annotations.contains("output_var"))
	    output_var = true;

	// 	    System.out.println("IS INTRODUCED");

	String ident;
	Variable var;
	BooleanVariable boolVar;
	Set setValue;
	int initVal;
	switch (type ) {
	case 0: // int
	    ident = ((ASTVarDeclItem)node).getIdent();
	    var = new Variable(store, ident, JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);
	    table.addVariable(ident, var);
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		initVal = getScalarFlatExpr(node, initChild);
// 		XeqY c = new XeqY(var, new Variable(store, initVal, initVal));
		XeqC c = new XeqC(var, initVal);
		store.impose(c);
		//  		System.out.println(c);
	    }
	    if (!var_introduced) table.addSearchVar(var);
	    if (output_var) table.addOutVar(var);
	    break;
	case 1: // int interval
	    ident = ((ASTVarDeclItem)node).getIdent();
	    var = new Variable(store, ident, lowInterval, highInterval);
	    table.addVariable(ident, var);
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		initVal = getScalarFlatExpr(node, initChild);
// 		XeqY c = new XeqY(var, new Variable(store, initVal, initVal));
		XeqC c = new XeqC(var, initVal);
		store.impose(c);
		//   		System.out.println(c);
	    }
	    if (!var_introduced) table.addSearchVar(var);
	    if (output_var) table.addOutVar(var);
	    break;
	case 2: // int list
	    ident = ((ASTVarDeclItem)node).getIdent();
	    var = new Variable(store, ident);
	    for (Integer e : intList)
		var.addDom(e.intValue(), e.intValue());
	    table.addVariable(ident, var);
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		initVal = getScalarFlatExpr(node, initChild);
// 		XeqY c = new XeqY(var, new Variable(store, initVal, initVal));
		XeqC c = new XeqC(var, initVal);
		store.impose(c);
		// 		System.out.println(c);
	    }
	    if (!var_introduced) table.addSearchVar(var);
	    if (output_var) table.addOutVar(var);
	    break;
	case 3: // bool
	    ident = ((ASTVarDeclItem)node).getIdent();
	    boolVar = new BooleanVariable(store, ident);
	    table.addVariable(ident, boolVar);
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		initVal = getScalarFlatExpr(node, initChild);
// 		XeqY c = new XeqY(boolVar, new Variable(store, initVal, initVal));
		XeqC c = new XeqC(boolVar, initVal);
		store.impose(c);
		// 		System.out.println(c);
	    }
	    if (!var_introduced) table.addSearchVar(boolVar);
	    if (output_var) table.addOutVar(boolVar);
	    numberBooleanVariables++;
	    break;
	case 4: // set int
	    ident = ((ASTVarDeclItem)node).getIdent();
	    var = new Variable(store, ident, new SetDomain(JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt));
	    table.addSetVariable(ident, var);
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		setValue = getSetLiteral(node, initChild);
		JaCoP.set.constraints.XeqY c = new JaCoP.set.constraints.XeqY(var, new Variable(store, "", new SetDomain(setValue, setValue)));
		store.impose(c);
		//  		System.out.println(c);
	    }
	    if (!var_introduced) table.addSearchSetVar(var);
	    if (output_var) table.addOutVar(var);
	    break;
	case 5: // set interval
	    ident = ((ASTVarDeclItem)node).getIdent();
	    var = new Variable(store, ident, new SetDomain(lowInterval, highInterval));
	    table.addSetVariable(ident, var);
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		setValue = getSetLiteral(node, initChild);
		JaCoP.set.constraints.XeqY c = new JaCoP.set.constraints.XeqY(var, new Variable(store, "", new SetDomain(setValue, setValue)));
		store.impose(c);
		//  		System.out.println(c);
	    }
	    if (!var_introduced) table.addSearchSetVar(var);
	    if (output_var) table.addOutVar(var);
	    break;
	case 6: // set list
	    ident = ((ASTVarDeclItem)node).getIdent();
	    SetDomain dom = new SetDomain();
	    for (Integer e : intList)
		dom.addDom(e.intValue(), e.intValue());
	    var = new Variable(store, ident, dom);
	    table.addSetVariable(ident, var);
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		setValue = getSetLiteral(node, initChild);
		JaCoP.set.constraints.XeqY c = new JaCoP.set.constraints.XeqY(var, new Variable(store, "", new SetDomain(setValue, setValue)));
		store.impose(c);
		//  		System.out.println(c);
	    }
	    if (!var_introduced) table.addSearchSetVar(var);
	    if (output_var) table.addOutVar(var);
	    break;
	case 7: // bool set
	    ident = ((ASTVarDeclItem)node).getIdent();
	    var = new Variable(store, ident, new SetDomain(0, 1));
	    table.addSetVariable(ident, var);
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		setValue = getSetLiteral(node, initChild);
		JaCoP.set.constraints.XeqY c = new JaCoP.set.constraints.XeqY(var, new Variable(store, "", new SetDomain(setValue, setValue)));
		store.impose(c);
		//  		System.out.println(c);
	    }
	    if (!var_introduced) table.addSearchSetVar(var);
	    if (output_var) table.addOutVar(var);
	    break;
	default: 
	    System.err.println("Not supported type in parameter; compilation aborted."); 
	    System.exit(0);
	}

    }

    void generateArray(SimpleNode node, Tables table, Store store) {
	if ( ((ASTVarDeclItem)node).getKind() == 2 )
	    generateArrayVariables(node, table, store);
	else if ( ((ASTVarDeclItem)node).getKind() == 3 )
	    generateArrayParameters(node, table);
	else {
	    System.err.println("Internal error");
	    System.exit(0);
	}
    }

    void generateArrayParameters(SimpleNode node, Tables table) {

	dictionary = table;
	annotations = new HashSet<String>();
// 	boolean output_array = false;
// 	OutputArrayAnnotation outArrayAnn=null;

	int type = getType(node);

	int initChild = getAnnotations(node, 1);

	// 	node.dump("");
	//    	System.out.println("*** Type = " + type + " init index = " + initChild);
	//    	System.out.println("*** Annotations: " + annotations);

	String ident = ((ASTVarDeclItem)node).getIdent();

// 	if (annotations.contains("output_array")) {
// 	    output_array = true;
// 	    outArrayAnn = new OutputArrayAnnotation(ident, indexBounds);
// 	}

	int size;
	int[] val;
	Set[] setValue;
	switch (type ) {
	case 0: // array of int
	case 1: // array of int interval
	case 2: // array of int list
	case 3: // array of bool
// 	    ident = ((ASTVarDeclItem)node).getIdent();
	    size = ((ASTVarDeclItem)node).getHighIndex() - ((ASTVarDeclItem)node).getLowIndex() + 1;
	    val = getArrayOfScalarFlatExpr(node, initChild, size);
	    table.addIntArray(ident, val);
	    break;
	case 4: // array of set int
	case 5: // array of set interval
	case 6: // array of set list
	case 7: // array of bool set
// 	    ident = ((ASTVarDeclItem)node).getIdent();
	    size = ((ASTVarDeclItem)node).getHighIndex() - ((ASTVarDeclItem)node).getLowIndex() + 1;
	    setValue = getSetLiteralArray(node, initChild, size);
	    table.addSetArray(ident, setValue);
	    break;
	default: 
	    System.err.println("Not supported type in array parameter; compilation aborted."); 
	    System.exit(0);
	}
    }

    void generateArrayVariables(SimpleNode node, Tables table, Store store) {

	dictionary = table;
	annotations = new HashSet<String>();
	indexBounds = new ArrayList<Set>();
	boolean output_array = false;
	OutputArrayAnnotation outArrayAnn=null;

	int type = getType(node);

	int initChild = getArrayAnnotations(node, 1);

	//    	node.dump("");
	//    	System.out.println("*** Type = " + type + " init index = " + initChild);
	//     	System.out.println("*** Annotations: " + annotations + "  " + indexBounds);

	String ident = ((ASTVarDeclItem)node).getIdent();

	if (annotations.contains("output_array")) {
	    output_array = true;
	    outArrayAnn = new OutputArrayAnnotation(ident, indexBounds);
	}

	int size;
	Variable[] varArray;
	switch (type ) {
	case 0: // array of int
	    size = ((ASTVarDeclItem)node).getHighIndex() - ((ASTVarDeclItem)node).getLowIndex() + 1;
	    varArray = null;
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		varArray = getScalarFlatExpr_ArrayVar(store, node, initChild);
		for (int i=0; i<varArray.length; i++)
		    if ( ! ground(varArray[i]) )
			table.addSearchVar(varArray[i]);
	    }
	    else { // no init values
		varArray = new Variable[size];
		for (int i=0; i<size; i++)
		    varArray[i] = new Variable(store, ident+"["+ i +"]", JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt);
		table.addSearchArray(varArray);
	    }
	    table.addVariableArray(ident, varArray);
	    if (output_array) {
		outArrayAnn.setArray(varArray);
		table.addOutArray(outArrayAnn);
	    }
	    break;
	case 1: // array of int interval
	    size = ((ASTVarDeclItem)node).getHighIndex() - ((ASTVarDeclItem)node).getLowIndex() + 1;
	    varArray = null;
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		// array initialization
		varArray = getScalarFlatExpr_ArrayVar(store, node, initChild);
		for (int i=0; i<varArray.length; i++)
		    if ( ! ground(varArray[i]) )
			table.addSearchVar(varArray[i]);
	    }
	    else { // no init values
		varArray = new Variable[size];
		for (int i=0; i<size; i++)
		    varArray[i] = new Variable(store, ident+"["+ i +"]", lowInterval, highInterval);
		table.addSearchArray(varArray);
	    }
	    table.addVariableArray(ident, varArray);
	    if (output_array) {
		outArrayAnn.setArray(varArray);
		table.addOutArray(outArrayAnn);
	    }
	    break;
	case 2: // array of int list
	    size = ((ASTVarDeclItem)node).getHighIndex() - ((ASTVarDeclItem)node).getLowIndex() + 1;
	    varArray = null;
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		// array initialization
		varArray = getScalarFlatExpr_ArrayVar(store, node, initChild);
		for (int i=0; i<varArray.length; i++)
		    if ( ! ground(varArray[i]) )
			table.addSearchVar(varArray[i]);
	    }
	    else { // no init values
		varArray = new Variable[size];
		for (int i=0; i<size; i++) {
		    IntervalDomain dom = new IntervalDomain();
		    for (Integer e : intList)
			dom.addDom(e.intValue(), e.intValue());
		    varArray[i] = new Variable(store, ident+"["+i+"]", dom);
		}
		table.addSearchArray(varArray);
	    }
	    table.addVariableArray(ident, varArray);
	    if (output_array) {
		outArrayAnn.setArray(varArray);
		table.addOutArray(outArrayAnn);
	    }
	    break;
	case 3: // array of bool
	    size = ((ASTVarDeclItem)node).getHighIndex() - ((ASTVarDeclItem)node).getLowIndex() + 1;
	    varArray = null;
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		varArray = getScalarFlatExpr_ArrayVar(store, node, initChild);
	    }
	    else { // no init values
		varArray = new Variable[size];
		for (int i=0; i<size; i++)
		    varArray[i] = new BooleanVariable(store, ident+"["+i+"]"); 
		table.addSearchArray(varArray);
		numberBooleanVariables += size;
	    }
	    table.addVariableArray(ident, varArray);
	    if (output_array) {
		outArrayAnn.setArray(varArray);
		table.addOutArray(outArrayAnn);
	    }
	    break;
	case 4: // array of set int
	    size = ((ASTVarDeclItem)node).getHighIndex() - ((ASTVarDeclItem)node).getLowIndex() + 1;
	    varArray = null;
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		// array initialization
		varArray = getSetFlatExpr_ArrayVar(store, node, initChild);
	    }
	    else { // no init values
		varArray = new Variable[size];
		for (int i=0; i<size; i++)
		    varArray[i] = new Variable(store, ident+"["+i+"]", 
					       new SetDomain(JaCoP.core.Constants.MinInt, JaCoP.core.Constants.MaxInt));
		table.addSearchSetArray(varArray);
	    }
	    table.addSetVariableArray(ident, varArray);
	    if (output_array) {
		outArrayAnn.setArray(varArray);
		table.addOutArray(outArrayAnn);
	    }
	    break;
	case 5: // array of set interval
	    size = ((ASTVarDeclItem)node).getHighIndex() - ((ASTVarDeclItem)node).getLowIndex() + 1;
	    varArray = null;
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		// array initialization
		varArray = getSetFlatExpr_ArrayVar(store, node, initChild);
	    }
	    else { // no init values
		varArray = new Variable[size];
		for (int i=0; i<size; i++)
		    varArray[i] = new Variable(store, ident+"["+i+"]", new SetDomain(lowInterval, highInterval));
		table.addSearchSetArray(varArray);
	    }
	    table.addSetVariableArray(ident, varArray);
	    if (output_array) {
		outArrayAnn.setArray(varArray);
		table.addOutArray(outArrayAnn);
	    }
	    break;
	case 6: // array of set list
	    size = ((ASTVarDeclItem)node).getHighIndex() - ((ASTVarDeclItem)node).getLowIndex() + 1;
	    varArray = null;
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		// array initialization
		varArray = getSetFlatExpr_ArrayVar(store, node, initChild);
	    }
	    else { // no init values
		varArray = new Variable[size];
		for (int i=0; i<size; i++) {
		    Set sd = new Set();
		    for (Integer e : intList)
			sd.addDom(e.intValue(), e.intValue());
		    varArray[i] = new Variable(store, ident+"["+i+"]", new SetDomain(new Set(), sd));
		}
		table.addSearchSetArray(varArray);
	    }
	    table.addSetVariableArray(ident, varArray);
	    if (output_array) {
		outArrayAnn.setArray(varArray);
		table.addOutArray(outArrayAnn);
	    }
	    break;
	case 7: // array of bool set
	    size = ((ASTVarDeclItem)node).getHighIndex() - ((ASTVarDeclItem)node).getLowIndex() + 1;
	    varArray = null;
	    if (initChild < ((ASTVarDeclItem)node).jjtGetNumChildren()) {
		// array initialization
		varArray = getSetFlatExpr_ArrayVar(store, node, initChild);
	    }
	    else { // no init values
		varArray = new Variable[size];
		for (int i=0; i<size; i++)
		    varArray[i] = new Variable(store, ident+"["+i+"]", new SetDomain(0,1));
		table.addSearchSetArray(varArray);
	    }
	    table.addSetVariableArray(ident, varArray);
	    if (output_array) {
		outArrayAnn.setArray(varArray);
		table.addOutArray(outArrayAnn);
	    }
	    break;
	default: 
	    System.err.println("Not supported type in array parameter; compilation aborted."); 
	    System.exit(0);
	}
    }


    // 0 - int; 1 - int interval; 2 - int list; 3 - bool; 
    // 4 - set int; 5 - set interval; 6 - set list; 7- bool set;
    int getType(SimpleNode node) {
	SimpleNode child = (SimpleNode)node.jjtGetChild(0);
	//  	System.out.println("*** " + child + " value = " + ((SimpleNode)child).jjtGetValue());
	if (child.getId() == JJTINTTIEXPRTAIL) {
	    int intType = ((ASTIntTiExprTail)child).getType();
	    switch (intType) {
		// 	    case 0: // int
		// 		break;
	    case 1: // int interval
		lowInterval = ((ASTIntTiExprTail)child).getLow(); 
		highInterval = ((ASTIntTiExprTail)child).getHigh(); 
		if (lowInterval < JaCoP.core.Constants.MinInt || highInterval > JaCoP.core.Constants.MaxInt)
		    throw new ArithmeticException("Too large bounds on intervals " + lowInterval + ".." + highInterval);
		break;
	    case 2: // int list
		SimpleNode grand_child = (SimpleNode)child.jjtGetChild(0);
		intList = ((ASTIntLiterals)grand_child).getList();
		for (Integer e : intList)
		    if (e.intValue() < JaCoP.core.Constants.MinInt || e.intValue() > JaCoP.core.Constants.MaxInt)
			throw new ArithmeticException("Too large element in set " + e.intValue());
		break;
	    }
	    return intType;
	}
	else if (child.getId() == JJTBOOLTIEXPRTAIL) 
	    return 3;
	else if (child.getId() == JJTSETTIEXPRTAIL)  {
	    SimpleNode grand_child = (SimpleNode)child.jjtGetChild(0);
	    if (grand_child.getId() == JJTINTTIEXPRTAIL) {

		int intType = ((ASTIntTiExprTail)grand_child).getType();
		switch (intType) {
		    // 		case 0: // int
		    // 		    break;
		case 1: // int interval
		    lowInterval = ((ASTIntTiExprTail)grand_child).getLow(); 
		    highInterval = ((ASTIntTiExprTail)grand_child).getHigh(); 
		    if (lowInterval < JaCoP.core.Constants.MinInt || highInterval > JaCoP.core.Constants.MaxInt)
			throw new ArithmeticException("Too large bounds on intervals " + lowInterval + ".." + highInterval);
		    break;
		case 2: // int list
		    SimpleNode grand_grand_child = (SimpleNode)grand_child.jjtGetChild(0);
		    intList = ((ASTIntLiterals)grand_grand_child).getList();
		    for (Integer e : intList)
			if (e.intValue() < JaCoP.core.Constants.MinInt || e.intValue() > JaCoP.core.Constants.MaxInt)
			    throw new ArithmeticException("Too large element in set " + e.intValue());
		    break;
		}
		//  		return ((ASTIntTiExprTail)grand_child).getType()+4;
		return intType+4;
	    }
	    else if (grand_child.getId() == JJTBOOLTIEXPRTAIL)
		return 7;
	    else return -1;
	}
	else return -1;
    }

    int getAnnotations(SimpleNode node, int i) {
	int j = i;
	int count = node.jjtGetNumChildren();
	if (j < count ) {
	    SimpleNode child = (SimpleNode)node.jjtGetChild(j);
	    while (j < count && child.getId() == JJTANNOTATION) {
		annotations.add(((ASTAnnotation)child).getAnnId()); 
		j++;
		if (j<count) 
		    child = (SimpleNode)node.jjtGetChild(j);
	    }
	}
	return j;
    }

    int getArrayAnnotations(SimpleNode node, int i) {
	int j = i;
	int count = node.jjtGetNumChildren();
	if (j < count ) {
	    SimpleNode child = (SimpleNode)node.jjtGetChild(j);
	    while (j < count && child.getId() == JJTANNOTATION) {
		String id = ((ASTAnnotation)child).getAnnId();
		annotations.add(id); 
		if (id.equals("output_array")) {
		    int no = child.jjtGetNumChildren();
		    if (no > 1 || ((SimpleNode)child.jjtGetChild(0)).getId() != JJTANNEXPR) {
			System.err.println("More than one annotation expression in output_array annotation; execution aborted");
			System.exit(0);
			return -1;
		    }
		    else {
			SimpleNode grandchild = (SimpleNode)child.jjtGetChild(0);
			int number = grandchild.jjtGetNumChildren();
			if (number == 1) {
			    SimpleNode arrayLiteral = (SimpleNode)grandchild.jjtGetChild(0);
			    if (arrayLiteral.getId() == JJTARRAYLITERAL) {
				int numberSL = arrayLiteral.jjtGetNumChildren();
				for (int p=0; p<numberSL; p++ ) {
				    SimpleNode setLiteral = (SimpleNode)arrayLiteral.jjtGetChild(p);
				    if (((ASTSetLiteral)setLiteral).getType() == 0) {// interval
					int s_n = setLiteral.jjtGetNumChildren();
					if (s_n == 2) {
					    int low = ((ASTIntFlatExpr)setLiteral.jjtGetChild(0)).getInt();
					    int high = ((ASTIntFlatExpr)setLiteral.jjtGetChild(1)).getInt();
					    Set indexes = new Set(low, high);
					    indexBounds.add(indexes);
					    // 					    System.out.println(indexes+"->"+indexes.min() +"__"+indexes.max());
					}
					else {
					    System.err.println("Unexpected set literal in output_array annotation; execution aborted");
					    System.exit(0);
					    return -1;
					}
				    }
				    else {
					System.err.println("Unexpected set literal in output_array annotation; execution aborted");
					System.exit(0);
					return -1;
				    }
				}
			    }
			    else {
				System.err.println("Wrong expression in output_array annotation; execution aborted");
				System.exit(0);
				return -1;
			    }
			}
		    }
		}
		j++;
		if (j<count) 
		    child = (SimpleNode)node.jjtGetChild(j);
	    }
	}
	return j;
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

    Variable[] getScalarFlatExpr_ArrayVar(Store store, SimpleNode node, int index) {

	SimpleNode child = (SimpleNode)node.jjtGetChild(index);
	if (child.getId() == JJTARRAYLITERAL) {
	    int count = child.jjtGetNumChildren();
	    Variable[] av = new Variable[count];
	    // 	    System.out.println(child + " count = " + count);
	    for (int i=0; i<count; i++) {
		av[i] = getScalarFlatExpr_var(store, child, i);
	    }
	    return av;
	}
	else {
	    System.err.println("Expeceted array literal, found " + child.getId() + " ; compilation aborted."); 
	    System.exit(0);
	    return new Variable[1];
	}
    }

    Variable getScalarFlatExpr_var(Store store, SimpleNode node, int i) {
	SimpleNode child = (SimpleNode)node.jjtGetChild(i);
	if (child.getId() == JJTSCALARFLATEXPR) {
	    switch ( ((ASTScalarFlatExpr)child).getType() ) {
	    case 0: // int
		return new Variable(store, ((ASTScalarFlatExpr)child).getInt(), ((ASTScalarFlatExpr)child).getInt());
	    case 1: // bool
		BoundDomain d = new BoundDomain(((ASTScalarFlatExpr)child).getInt(), ((ASTScalarFlatExpr)child).getInt());
		BooleanVariable bb = new BooleanVariable(store,"",d);
		//numberBooleanVariables++; // not really a variable; constant
		return bb;
	    case 2: // ident
		Variable var = dictionary.getVariable(((ASTScalarFlatExpr)child).getIdent());
		if (var != null)
		    return var;
		else {
		    Integer n = dictionary.getInt(((ASTScalarFlatExpr)child).getIdent());
		    if (n != null)
			return new Variable(store, n.intValue(), n.intValue());
		    else break;
		}
	    case 3: // array acces
		Variable avar = dictionary.getVariableArray(((ASTScalarFlatExpr)child).getIdent())[((ASTScalarFlatExpr)child).getInt()];
		if (avar != null)
		    return avar;
		else {
		    Integer an = dictionary.getIntArray(((ASTScalarFlatExpr)child).getIdent())[((ASTScalarFlatExpr)child).getInt()];
		    if (an != null)
			return new Variable(store, an.intValue(), an.intValue());
		    else break;
		}
	    default: // string & float;
		System.err.println("Not supported scalar in parameter; compilation aborted."); 
		System.exit(0);
	    }
	}
	else {
	    System.err.println("Not supported parameter assignment; compilation aborted."); 
	    System.exit(0);
	}
	return new Variable(store);
    }


    Variable[] getSetFlatExpr_ArrayVar(Store store, SimpleNode node, int index) {

	SimpleNode child = (SimpleNode)node.jjtGetChild(index);

	if (child.getId() == JJTARRAYLITERAL) {
	    int count = child.jjtGetNumChildren();
	    Variable[] av = new Variable[count];
	    //System.out.println(child + " count = " + count);
	    for (int i=0; i<count; i++) {
		av[i] = getSetFlatExpr_var(store, child, i);
	    }
	    return av;
	}
	else {
	    System.err.println("Expeceted array literal, found " + child.getId() + " ; compilation aborted."); 
	    System.exit(0);
	    return new Variable[1];
	}
    }

    Variable getSetFlatExpr_var(Store store, SimpleNode node, int i) {
	SimpleNode child = (SimpleNode)node.jjtGetChild(i);
	if (child.getId() == JJTSCALARFLATEXPR) {
	    switch ( ((ASTScalarFlatExpr)child).getType() ) {
	    case 2: // ident
		Variable var = dictionary.getSetVariable(((ASTScalarFlatExpr)child).getIdent());
		if (var != null)
		    return var;
		else {
		    Integer n = dictionary.getInt(((ASTScalarFlatExpr)child).getIdent());
		    if (n != null)
			return new Variable(store, n.intValue(), n.intValue());
		    else break;
		}
	    case 3: // array acces
		Variable avar = dictionary.getSetVariableArray(((ASTScalarFlatExpr)child).getIdent())[((ASTScalarFlatExpr)child).getInt()];
		if (avar != null)
		    return avar;
		else {
		    Set an = dictionary.getSetArray(((ASTScalarFlatExpr)child).getIdent())[((ASTScalarFlatExpr)child).getInt()];
		    if (an != null)
			return new Variable(store, "", new SetDomain(an, an));
		    else break;
		}
	    default: // string & float;
		System.err.println("Not supported scalar in parameter; compilation aborted."); 
		System.exit(0);
	    }
	}
	else if (child.getId() == JJTSETLITERAL) {
	    Set s = getSetLiteral(node, i);
	    Variable setVar = new Variable(store, "", new SetDomain(s, s));
	    return setVar;
	}
	else {
	    System.err.println("Not supported parameter assignment; compilation aborted."); 
	    System.exit(0);
	}
	return new Variable(store);
    }

    int[] getArrayOfScalarFlatExpr(SimpleNode node, int index, int size) {
	SimpleNode child = (SimpleNode)node.jjtGetChild(index);
	int count = child.jjtGetNumChildren();
	if (count == size) {
	    int[] aa = new int[size];
	    for (int i=0;i<count;i++) {
		int el = getScalarFlatExpr(child, i);
		aa[i] = el;
	    }
	    return aa;
	}
	else { 
	    System.err.println("Different size declaration and intiallization of int array; compilation aborted."); 
	    System.exit(0);
	    return new int[] {};
	}
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

    Set[] getSetLiteralArray(SimpleNode node, int index, int size) {
	Set[] s = new Set[size];
	int arrayIndex=0;

	SimpleNode child = (SimpleNode)node.jjtGetChild(index);
	if (child.getId() == JJTARRAYLITERAL) {
	    int count = child.jjtGetNumChildren();
	    if (count == size) {
		for (int i=0; i<count; i++) {
		    s[arrayIndex++] = getSetLiteral(child, i);
		}
	    }
	    else {
		System.err.println("Different array sizes in specification and initialization; compilation aborted."); 
		System.exit(0);	    
	    }
	}
	return s;
    }

    boolean ground(Variable v) {
	return v.min() == v.max();
    } 
}
