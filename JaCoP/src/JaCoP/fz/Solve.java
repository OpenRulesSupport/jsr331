/**
 *  Solve.java 
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

import JaCoP.constraints.Constraint;
import JaCoP.constraints.XgtC;
import JaCoP.constraints.XltC;
import JaCoP.constraints.XplusYeqC;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.search.ComparatorVariable;
import JaCoP.search.CreditCalculator;
import JaCoP.search.DepthFirstSearch;
import JaCoP.search.LDS;
import JaCoP.search.PrintOutListener;
import JaCoP.search.Search;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.SimpleSolutionListener;
import JaCoP.set.search.SetSimpleSolutionListener;

/**
 * 
 * The parser part responsible for parsing the solve part of the flatzinc file.
 * 
 * @author Krzysztof Kuchcinski
 *
 */
public class Solve implements ParserTreeConstants {

    Tables dictionary;
    Options options;
    Store store;
    int initNumberConstraints;
    int NumberBoolVariables;

    ComparatorVariable tieBreaking=null;
    SelectChoicePoint variable_selection;
    ArrayList<Search> list_seq_searches = null;

    boolean debug = false;
    boolean print_search_info = false;
    boolean setSearch = false;
    boolean heuristicSeqSearch = false;
	
    Variable costVariable;
	
    int costValue;

    Parser parser;

    /**
     * It creates a parser for the solve part of the flatzinc file. 
     * 
     * @param store the constraint store within which context the search will take place.
     */
    public Solve(Store store) {
	this.store = store;
    }

    /**
     * It parses the solve part. 
     * 
     * @param node the current parsing node.
     * @param table the table containing all the various variable definitions encoutered thus far.
     * @param opt option specifies to flatzinc parser in respect to search (e.g. all solutions). 
     */
    public void search(ASTSolveItem node, Tables table, Options opt) {

// 	System.out.println(table);

	initNumberConstraints = store.numberConstraints();

	if (opt.getVerbose())
	    System.out.println("Model constraints defined. Variables = "+store.size() + ", Bool variables = "+NumberBoolVariables +
			       ", Constraints = "+initNumberConstraints);

	dictionary = table;
	options = opt;
	int solveKind=-1;
	Variable[] search_variables = null;
	String search_type = null;

	//     	node.dump("");

	ASTSolveKind kind=null;
	int count = node.jjtGetNumChildren();
	SearchItem si;

	// 	System.out.println("Number constraints = "+store.numberConstraints());
	// 	System.out.println("Number  of variables = "+store.size());

	if (count == 1) {// only solve kind
	    int varSize = dictionary.defaultSearchVariables.size();
	    for (int i=0; i<dictionary.defaultSearchArrays.size(); i++)
		varSize += dictionary.defaultSearchArrays.get(i).length;

	    search_variables = new Variable[varSize];
	    search_type = "int_search";
	    int n=0;
	    for (int i=0; i<dictionary.defaultSearchArrays.size(); i++)
		for (int j=0; j<dictionary.defaultSearchArrays.get(i).length; j++)
		    search_variables[n++] = dictionary.defaultSearchArrays.get(i)[j];
	    for (int i=0; i<dictionary.defaultSearchVariables.size(); i++)
		search_variables[n++] = dictionary.defaultSearchVariables.get(i);

	    // set_search
	    if (search_variables.length == 0) {
		search_type = "set_search";
		n=0;
		varSize = dictionary.defaultSearchSetVariables.size();
		for (int i=0; i<dictionary.defaultSearchSetArrays.size(); i++)
		    varSize += dictionary.defaultSearchSetArrays.get(i).length;

		search_variables = new Variable[varSize];
		for (int i=0; i<dictionary.defaultSearchSetArrays.size(); i++)
		    for (int j=0; j<dictionary.defaultSearchSetArrays.get(i).length; j++)
			search_variables[n++] = dictionary.defaultSearchSetArrays.get(i)[j];
		for (int i=0; i<dictionary.defaultSearchSetVariables.size(); i++)
		    search_variables[n++] = dictionary.defaultSearchSetVariables.get(i);
	    }

	    if (search_variables.length != 0 ) {
		kind = (ASTSolveKind)node.jjtGetChild(0);
		solveKind = getKind(kind.getKind());

		si = new SearchItem(store, dictionary);
		si.search_type = search_type;
		si.search_variables = search_variables;

		run_single_search(solveKind, kind, si);
	    }
	    else {
		System.out.println("----------");
		return;
	    }
	}
	else if (count == 2) {// annotation

	    si = new SearchItem(store, dictionary);
	    si.searchParameters(node, 0);
	    // 	    System.out.println("*** "+si);
	    search_type = si.type();

	    if (search_type.equals("int_search") || search_type.equals("set_search") ||
		search_type.equals("bool_search")) {

		kind = (ASTSolveKind)node.jjtGetChild(1);
		solveKind = getKind(kind.getKind());

		run_single_search(solveKind, kind, si);
	    }
	    else if (search_type.equals("seq_search")) {
		kind = (ASTSolveKind)node.jjtGetChild(1);
		solveKind = getKind(kind.getKind());

		run_sequence_search(solveKind, kind, si);
	    }
	    else {
		System.err.println("Not recognized structure of solve statement; compilation aborted");
		System.exit(0);
	    } 
	}
	else {
	    System.err.println("Not recognized structure of solve statement; compilation aborted");
	    System.exit(0);
	}
    }

    void run_single_search(int solveKind, SimpleNode kind, SearchItem si) {

	// 	System.out.println(solveKind + " : " + si);

	Variable cost = null;
	DepthFirstSearch label = null;
	boolean optimization = false;

	if (si.type().equals("int_search"))
	    label = int_search(si);
	else if (si.type().equals("bool_search"))
	    label = int_search(si);
	else if (si.type().equals("set_search")) {
	    label = set_search(si);
	    setSearch=true;
	}
	else {
	    System.err.println("Not recognized or supported search type \""+si.type()+"\"; compilation aborted");
	    System.exit(0);
	}

	boolean Result = false;	

	Thread tread = java.lang.Thread.currentThread();
	java.lang.management.ThreadMXBean b = java.lang.management.ManagementFactory.getThreadMXBean();


	long startCPU = b.getThreadCpuTime(tread.getId());
	// 	long startUser = b.getThreadUserTime(tread.getId());

	int to = options.getTimeOut();
	if (to > 0) 
	    label.setTimeOut(to);

	// LDS heuristic search
	if (si.exploration().equals("lds")) 
	    lds_search(label, si.ldsValue);
	// Credit heuristic search
	if (si.exploration().equals("credit")) 
	    credit_search(label, si.creditValue, si.bbsValue);

	if (si.exploration() == null || si.exploration().equals("complete") 
	    || si.exploration().equals("lds")
	    || si.exploration().equals("credit")
	    )
	    switch (solveKind) {
	    case 0: // satisfy
		if (setSearch) {
		    label.setSolutionListener(new SetCostListener());
		    label.setPrintInfo(false);
		}
		else {
		    label.setSolutionListener(new CostListener());
		    label.setPrintInfo(false);
		}

		if (options.getAll()) { // all solutions
		    label.getSolutionListener().searchAll(true); 
		    label.getSolutionListener().recordSolutions(false);
		    if (options.getNumberSolutions()>0)
			label.getSolutionListener().setSolutionLimit(options.getNumberSolutions());
		    //  		    System.out.println("Search All");
		}

		Result = label.labeling(store, variable_selection);
		break;
	    case 1: // minimize
		optimization = true;
		cost = getCost((ASTSolveExpr)kind.jjtGetChild(0));
		costVariable = cost;

		if (setSearch) 
		    label.setSolutionListener(new SetCostListener());
		else 
		    label.setSolutionListener(new CostListener());

		if (options.getNumberSolutions()>0) {
		    label.getSolutionListener().setSolutionLimit(options.getNumberSolutions());
		    label.respectSolutionLimitInOptimization=true;
		}

		label.setPrintInfo(false);
		Result = label.labeling(store, variable_selection, cost);
		break;
	    case 2: //maximize
		optimization = true;
		cost = getCost((ASTSolveExpr)kind.jjtGetChild(0));
		Variable max_cost = new Variable(store, "-"+cost.id(), JaCoP.core.Constants.MinInt, 
						 JaCoP.core.Constants.MaxInt);
		pose(new XplusYeqC(max_cost, cost, 0));

		costVariable = cost;
		if (setSearch) 
		    label.setSolutionListener(new SetCostListener());
		else 
		    label.setSolutionListener(new CostListener());

		if (options.getNumberSolutions()>0) {
		    label.getSolutionListener().setSolutionLimit(options.getNumberSolutions());
		    label.respectSolutionLimitInOptimization=true;
		}

		label.setPrintInfo(false);
		Result = label.labeling(store, variable_selection, max_cost);
		break;
	    }
	else {
	    System.err.println("Not recognized or supported "+si.exploration()+" search explorarion strategy ; compilation aborted");
	    System.exit(0);
	}

	if (Result) {
	    if (!optimization && options.getAll()) {
		// 		label.getSolutionListener().printAllSolutions();
		if (si.exploration().equals("complete")) 
		    if (! label.timeOutOccured) {
			if (options.getNumberSolutions() == -1 || options.getNumberSolutions() > label.getSolutionListener().solutionsNo())
			    System.out.println("==========");
		    }
		    else
			System.out.println("=====TIME-OUT=====");
		else
		    if (label.timeOutOccured) 
			System.out.println("=====TIME-OUT=====");
	    }
	    else if (optimization) {
		if (si.exploration().equals("complete"))
		    if (! label.timeOutOccured) {
			if (options.getNumberSolutions() == -1 || options.getNumberSolutions() > label.getSolutionListener().solutionsNo())
			    System.out.println("==========");
		    }    
		    else
			System.out.println("=====TIME-OUT=====");
		else
		    if (label.timeOutOccured) 
			System.out.println("=====TIME-OUT=====");
	    }
	}
	else
	    if (label.timeOutOccured) {
		System.out.println("=====UNKNOWN=====");
		System.out.println("=====TIME-OUT=====");
	    }
	    else
		if (si.exploration().equals("complete"))
		    System.out.println("=====UNSATISFIABLE=====");
		else
		    System.out.println("=====UNKNOWN=====");

	if (options.getStatistics()) {
	    System.out.println("\nModel variables : "+(int)(store.size()+NumberBoolVariables) +
			       "\nModel constraints : "+initNumberConstraints+
			       "\n\nSearch CPU time : " + (b.getThreadCpuTime(tread.getId()) - startCPU)/(long)1e+6 + "ms"+
			       "\nSearch nodes : "+label.getNodes()+
			       "\nSearch decisions : "+label.getDecisions()+
			       "\nWrong search decisions : "+label.getWrongDecisions()+
			       "\nSearch backtracks : "+label.getBacktracks()+
			       "\nMax search depth : "+label.getMaximumDepth()+
			       "\nNumber solutions : "+label.getSolutionListener().solutionsNo()
			       );
	}
    }

    void run_sequence_search(int solveKind, SimpleNode kind, SearchItem si) {
	DepthFirstSearch masterLabel=null;
	DepthFirstSearch last_search=null;
	SelectChoicePoint masterSelect=null;
	list_seq_searches = new ArrayList<Search>();

	for (int i=0; i<si.getSearchItems().size(); i++)
	    if (i == 0) { // master search
		masterLabel = sub_search(si.getSearchItems().get(i), masterLabel, true);
		last_search = masterLabel;
		masterSelect = variable_selection;
		if (!print_search_info) masterLabel.setPrintInfo(false);
	    }
	    else {
		DepthFirstSearch label = sub_search(si.getSearchItems().get(i), last_search, false);
		last_search.addChildSearch(label);
		last_search = label;
		if (!print_search_info) last_search.setPrintInfo(false);
	    }

	//  	System.out.println(list_seq_searches);

	boolean Result = false;	
	Variable cost=null;
	boolean optimization = false;

	Search final_search = list_seq_searches.get(list_seq_searches.size()-1);

	for (int i=0; i<list_seq_searches.size(); i++) {
	    if (!print_search_info) 
		list_seq_searches.get(i).setPrintInfo(false);
	    if (!setSearch) //list_seq_searches.get(i).getSolutionListener() instanceof SimpleSolutionListener )
		// int search
		list_seq_searches.get(i).setSolutionListener(new EmptyListener());
	    else {
		// set search
		list_seq_searches.get(i).setSolutionListener(new SetEmptyListener());
	    }
	}

	Thread tread = java.lang.Thread.currentThread();
	java.lang.management.ThreadMXBean b = java.lang.management.ManagementFactory.getThreadMXBean();

	long startCPU = b.getThreadCpuTime(tread.getId());
	// 	long startUser = b.getThreadUserTime(tread.getId());

	int to = options.getTimeOut();
	if (to > 0)
	    for (Search s : list_seq_searches)
		s.setTimeOut(to);

	if (si.exploration() == null || si.exploration().equals("complete"))
	    switch (solveKind) {
	    case 0: // satisfy
		if (options.getAll()) { // all solutions
					// 		    final_search.getSolutionListener().searchAll(true);
		    for (int i=0; i<list_seq_searches.size(); i++) {
			list_seq_searches.get(i).getSolutionListener().searchAll(true);
			list_seq_searches.get(i).getSolutionListener().recordSolutions(false);
		    }
		    //  		    System.out.println("Search All");
		}
		if (setSearch)
		    final_search.setSolutionListener(new SetCostListener());
		else
		    final_search.setSolutionListener(new CostListener());

		if (options.getNumberSolutions()>0)
		    final_search.getSolutionListener().setSolutionLimit(options.getNumberSolutions());

		Result = masterLabel.labeling(store, masterSelect);
		break;
	    case 1: // minimize
		optimization = true;
		cost = getCost((ASTSolveExpr)kind.jjtGetChild(0));
		Result = restart_search(masterLabel, masterSelect, cost, true);

		//     		Result = masterLabel.labeling(store, masterSelect, cost);
		break;
	    case 2: //maximize
		optimization = true;
		cost = getCost((ASTSolveExpr)kind.jjtGetChild(0));
		Result = restart_search(masterLabel, masterSelect, cost, false);

		// 		cost = getCost((ASTSolveExpr)kind.jjtGetChild(0));
		// 		Variable max_cost = new Variable(store, "-"+cost.id(), JaCoP.core.Constants.MinInt, 
		// 						 JaCoP.core.Constants.MaxInt);
		// 		pose(new XplusYeqC(max_cost, cost, 0));
		// 		Cost = max_cost;
		// 		final_search.setSolutionListener(new CostListener());
		// 		Result = masterLabel.labeling(store, masterSelect, max_cost);
		break;
	    }
	else {
	    System.err.println("Not recognized or supported "+si.exploration()+
			       " search explorarion strategy ; compilation aborted");
	    System.exit(0);
	}

	if (Result) {
	    if (!optimization && options.getAll()) {
		if (!heuristicSeqSearch)
		    if (! anyTimeOutOccured(list_seq_searches)) {
			if (options.getNumberSolutions() == -1 || options.getNumberSolutions() > final_search.getSolutionListener().solutionsNo())
			    System.out.println("==========");
		    }
		    else
			System.out.println("=====TIME-OUT=====");
		else
		    if (anyTimeOutOccured(list_seq_searches)) 
			System.out.println("=====TIME-OUT=====");
	    }
	    else if (optimization) {
		if (!heuristicSeqSearch)
		    if (! anyTimeOutOccured(list_seq_searches)) {
			if (options.getNumberSolutions() == -1 || options.getNumberSolutions() > final_search.getSolutionListener().solutionsNo())
			    System.out.println("==========");
		    }
		    else
			System.out.println("=====TIME-OUT=====");
		else
		    if (anyTimeOutOccured(list_seq_searches)) 
			System.out.println("=====TIME-OUT=====");
	    }
	}
	else
	    if (anyTimeOutOccured(list_seq_searches)) {
		System.out.println("=====UNKNOWN=====");
		System.out.println("=====TIME-OUT=====");
	    }
	    else 
		System.out.println("=====UNSATISFIABLE=====");

	if (options.getStatistics()) {
	    int nodes=0, decisions=0, wrong=0, backtracks=0, depth=0;
	    for (int i=0; i<list_seq_searches.size(); i++) {
		Search label=list_seq_searches.get(i);
		nodes += label.getNodes(); 
		decisions += label.getDecisions();
		wrong += label.getWrongDecisions();
		backtracks += label.getBacktracks();
		depth += label.getMaximumDepth();
	    }
	    System.out.println("\nModel variables : "+(int)(store.size()+NumberBoolVariables)+
			       "\nModel constraints : "+initNumberConstraints+
			       "\n\nSearch CPU time : " + (b.getThreadCpuTime(tread.getId()) - startCPU)/(long)1e+6 + "ms"+
			       "\nSearch nodes : "+nodes+
			       "\nSearch decisions : "+decisions+
			       "\nWrong search decisions : "+wrong+
			       "\nSearch backtracks : "+backtracks+
			       "\nMax search depth : "+depth+
			       "\nNumber solutions : "+final_search.getSolutionListener().solutionsNo()
			       );
	}
    }

    boolean anyTimeOutOccured(ArrayList<Search> list_seq_searches) {

	for (int i=0; i<list_seq_searches.size(); i++)
	    if ( ((DepthFirstSearch)list_seq_searches.get(i)).timeOutOccured)
		return true;
	return false;
    }


    DepthFirstSearch sub_search(SearchItem si, DepthFirstSearch l, boolean master) {
	DepthFirstSearch last_search=l;
	DepthFirstSearch label = null;

	if (si.type().equals("int_search")) {
	    label = int_search(si);
	    if (!master) label.setSelectChoicePoint(variable_selection);

	    // LDS heuristic search
	    if (si.exploration().equals("lds")) {
		lds_search(label, si.ldsValue);
		heuristicSeqSearch = true;
	    }
	    // Credit heuristic search
	    if (si.exploration().equals("credit")) {
		credit_search(label, si.creditValue, si.bbsValue);
		heuristicSeqSearch = true;
	    }
	    list_seq_searches.add(label);
	}
	else if (si.type().equals("bool_search")) {
	    label = int_search(si);
	    if (!master) label.setSelectChoicePoint(variable_selection);

	    // LDS heuristic search
	    if (si.exploration().equals("lds")) {
		lds_search(label, si.ldsValue);
		heuristicSeqSearch = true;
	    }
	    // Credit heuristic search
	    if (si.exploration().equals("credit")) {
		credit_search(label, si.creditValue, si.bbsValue);
		heuristicSeqSearch = true;
	    }

	    list_seq_searches.add(label);
	}
	else if (si.type().equals("set_search")) {
	    setSearch=true;
	    label = set_search(si);
	    if (!master) 
		label.setSelectChoicePoint(variable_selection);

	    // LDS heuristic search
	    if (si.exploration().equals("lds")) {
		lds_search(label, si.ldsValue);
		heuristicSeqSearch = true;
	    }
	    // Credit heuristic search
	    if (si.exploration().equals("credit")) {
		credit_search(label, si.creditValue, si.bbsValue);
		heuristicSeqSearch = true;
	    }

	    list_seq_searches.add(label);
	}
	else if (si.type().equals("seq_search")) {
	    for (int i=0; i<si.getSearchItems().size(); i++)
		if (i == 0) { // master search
		    DepthFirstSearch label_seq = sub_search(si.getSearchItems().get(i), last_search, false);
		    last_search = label_seq;
		    label = label_seq;
		}
		else {
		    DepthFirstSearch label_seq = sub_search(si.getSearchItems().get(i), last_search, false);
		    last_search.addChildSearch(label_seq);
		    last_search = label_seq;
		}
	}
	else {
	    System.err.println("Not recognized or supported search type \""+si.type()+"\"; compilation aborted");
	    System.exit(0);
	}
	return label;
    }

    DepthFirstSearch int_search(SearchItem si) {

	variable_selection = si.getSelect();
	DepthFirstSearch label = new DepthFirstSearch();
	label.setSolutionListener(new PrintOutListener());
	return label;
    }

    DepthFirstSearch set_search(SearchItem si) {

	variable_selection = si.getSelect();
	DepthFirstSearch label = new DepthFirstSearch();
	label.setSolutionListener(new SetSimpleSolutionListener());
	return label;
    }

    void printSolution() {

	// System.out.println("*** Solution :");
	if (dictionary.outputVariables.size() > 0)
	    for (int i=0; i<dictionary.outputVariables.size(); i++) {
		Variable v = dictionary.outputVariables.get(i);

 		if (v instanceof JaCoP.core.BooleanVariable) {
		    String boolVar = v.id()+"=";
		    if (v.singleton())
			switch (v.value()) {
			case 0: boolVar += "false";
			    break;
			case 1: boolVar += "true";
			    break;
			default: boolVar += v.dom();
			}
		    System.out.println(boolVar);
		}
		else
		    System.out.println(v);
	    }

	for (int i=0; i<dictionary.outputArray.size(); i++) {
	    OutputArrayAnnotation a = dictionary.outputArray.get(i);
	    System.out.println(a);
	}
    }

    int getKind(String k) {
	if (k.equals("satisfy")) // 0 = satisfy
	    return 0;
	else if (k.equals("minimize")) // 1 = minimize
	    return 1;
	else if (k.equals("maximize")) // 2 = maximize
	    return 2;
	else {
	    System.err.println("Not supported search kind; compilation aborted");
	    System.exit(0);
	    return -1;
	}
    }

    Variable getCost(ASTSolveExpr node) {
	if (node.getType() == 0) // ident
	    return dictionary.getVariable(node.getIdent());
	else if (node.getType() == 1) // array access
	    return dictionary.getVariableArray(node.getIdent())[node.getIndex()];
	else {
	    System.err.println("Wrong cost function specification " + node);
	    System.exit(0);
	    return new Variable(store);
	}
    }

    Variable getVariable(ASTScalarFlatExpr node) {
	if (node.getType() == 0) //int
	    return new Variable(store, node.getInt(), node.getInt());
	else if (node.getType() == 2) // ident
	    return dictionary.getVariable(node.getIdent());
	else if (node.getType() == 3) // array access
	    return dictionary.getVariableArray(node.getIdent())[node.getInt()];
	else {
	    System.err.println("Wrong parameter " + node);
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
	    if (((ASTScalarFlatExpr)node).getType() == 2) // ident
		return dictionary.getVariableArray(((ASTScalarFlatExpr)node).getIdent());
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

    Variable[] getSetVarArray(SimpleNode node) {
	if (node.getId() == JJTARRAYLITERAL) {
	    int count = node.jjtGetNumChildren();
	    Variable[] aa = new Variable[count];
	    for (int i=0;i<count;i++) {
		ASTScalarFlatExpr child = (ASTScalarFlatExpr)node.jjtGetChild(i);
		Variable el = getSetVariable(child);
		aa[i] = el;
	    }
	    return aa;
	}
	else if (node.getId() == JJTSCALARFLATEXPR) {
	    if (((ASTScalarFlatExpr)node).getType() == 2) // ident
		return dictionary.getSetVariableArray(((ASTScalarFlatExpr)node).getIdent());
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

    Variable getSetVariable(ASTScalarFlatExpr node) {
	if (node.getType() == 2) // ident
	    return dictionary.getSetVariable(node.getIdent());
	else if (node.getType() == 3) // array access
	    return dictionary.getSetVariableArray(node.getIdent())[node.getInt()];
	else {
	    System.err.println("Wrong parameter on list of search set varibales" + node);
	    System.exit(0);
	    return new Variable(store);
	}
    }

    void pose(Constraint c) {
	store.impose(c);
	if (debug)
	    System.out.println(c);
    }

    boolean restart_search(Search masterLabel, SelectChoicePoint masterSelect, 
			   Variable cost, boolean minimize) {
	costVariable = cost;
	Search final_search = list_seq_searches.get(list_seq_searches.size()-1);
	if (setSearch)
	    final_search.setSolutionListener(new SetCostListener());
	else
	    final_search.setSolutionListener(new CostListener());

	// 	final_search.setSolutionListener(new CostListener());

	for (Search s : list_seq_searches) 
	    s.setAssignSolution(false);
	store.setLevel(store.level+1);
	boolean Result = true, optimalResult = false; 
	while (Result) {
	    Result = masterLabel.labeling(store, masterSelect);
	    if (minimize) //minimize
		pose(new XltC(cost, costValue));
	    else // maximize
		pose(new XgtC(cost, costValue));

	    optimalResult = optimalResult || Result;

	    if (options.getNumberSolutions() == final_search.getSolutionListener().solutionsNo())
		break;
	}
	store.removeLevel(store.level);
	store.setLevel(store.level-1);

	Result = optimalResult;
	if (Result)
	    for (Search s : list_seq_searches) 
		s.assignSolution();

	return Result;
    }

    void lds_search(DepthFirstSearch label, int lds_value) {
	//  	System.out.println("LDS("+lds_value+")");

	LDS lds = new LDS(lds_value); 
	if (label.getExitChildListener() == null)
	    label.setExitChildListener(lds);
	else
	    label.getExitChildListener().setChildrenListeners(lds);
    }


    void credit_search(DepthFirstSearch label, int creditValue, int bbsValue) {
	//  	System.out.println("Credit("+creditValue+", "+bbsValue+")");

	int maxDepth = 1000; //JaCoP.core.Constants.MaxInt;
	CreditCalculator credit = new CreditCalculator(creditValue, bbsValue, maxDepth);

	if (label.getConsistencyListener() == null)
	    label.setConsistencyListener(credit);
	else
	    label.getConsistencyListener().setChildrenListeners(credit);

	label.setExitChildListener(credit);
	label.setTimeOutListener(credit);
    }

    String getArrayName(Variable v) {
	String s = v.id();
	char c = '['; 
	int ci = (int)c;
	int end = s.indexOf(ci ); 
	return s.substring(0, end);
    }

    void setNumberBoolVariables(int n) {
	NumberBoolVariables = n;
    }


    /**
     * 
     * TODO
     * 
     * @author Krzysztof Kuchcinski
     *
     */
    public class EmptyListener extends SimpleSolutionListener {

	public boolean executeAfterSolution(Search search, SelectChoicePoint select) {

	    boolean returnCode = super.executeAfterSolution(search, select);

	    return returnCode;
	}	

    }

    /**
     * 
     * TODO
     * 
     * @author Krzysztof Kuchcinski
     *
     */
    public class SetEmptyListener extends SetSimpleSolutionListener {

	public boolean executeAfterSolution(Search search, SelectChoicePoint select) {

	    boolean returnCode = super.executeAfterSolution(search, select);

	    return returnCode;
	}	

    }


    /**
     * 
     * TODO
     * 
     * @author Krzysztof Kuchcinski
     *
     */
    public class CostListener extends SimpleSolutionListener {

	public boolean executeAfterSolution(Search search, SelectChoicePoint select) {

	    boolean returnCode = super.executeAfterSolution(search, select);

	    if (costVariable != null)
		costValue = costVariable.value();

	    printSolution();
	    System.out.println("----------");

	    return returnCode;
	}	

    }

    /**
     * 
     * TODO
     * @author Krzysztof Kuchcinski
     *
     */
    public class SetCostListener extends SetSimpleSolutionListener {

	public boolean executeAfterSolution(Search search, SelectChoicePoint select) {

	    boolean returnCode = super.executeAfterSolution(search, select);

	    if (costVariable != null)
		costValue = costVariable.value();

	    printSolution();
	    System.out.println("----------");

	    return returnCode;
	}	
    }
}
