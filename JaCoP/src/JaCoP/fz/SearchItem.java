/**
 *  SearchItem.java 
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
import java.util.Arrays;

import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.search.ComparatorVariable;
import JaCoP.search.Indomain;
import JaCoP.search.IndomainMax;
import JaCoP.search.IndomainMiddle;
import JaCoP.search.IndomainMin;
import JaCoP.search.IndomainRandom;
import JaCoP.search.LargestDomain;
import JaCoP.search.LargestMin;
import JaCoP.search.MaxRegret;
import JaCoP.search.MostConstrainedStatic;
import JaCoP.search.SelectChoicePoint;
import JaCoP.search.SimpleSelect;
import JaCoP.search.SmallestDomain;
import JaCoP.search.SmallestMin;
import JaCoP.search.SplitSelect;
import JaCoP.set.search.MinCardDiff;
import JaCoP.set.search.MinGlbCard;
import JaCoP.set.search.MaxCardDiff;
import JaCoP.set.search.SetSimpleSelect;


/**
 * 
 * The part of the parser responsible for parsing search part of the flatzinc specification. 
 * 
 * @author Krzysztof Kuchcinski
 *
 */
public class SearchItem implements ParserTreeConstants {

    Tables dictionary;
    Store store;

    ArrayList<SearchItem> search_seq = new ArrayList<SearchItem>();
    Variable[] search_variables;
    String search_type, explore="complete", indomain, var_selection_heuristic;

    int ldsValue = 0, creditValue = 0, bbsValue = 0;

    ComparatorVariable tieBreaking=null;

    /**
     * It constructs search part parsing object based on dictionaries
     * provided as well as store object within which the search will take place. 
     * 
     * @param store the finite domain store within which the search will take place. 
     * @param table the holder of all the objects present in the flatzinc file.
     */
    public SearchItem(Store store, Tables table) {
	this.dictionary = table;
	this.store = store;
    }

    void searchParameters(SimpleNode node, int n) {

	//   	node.dump("");

	ASTAnnotation ann = (ASTAnnotation)node.jjtGetChild(n);
	search_type = ann.getAnnId();

	if (search_type.equals("int_search") || search_type.equals("bool_search")) {
	    ASTAnnExpr expr1 = (ASTAnnExpr)ann.jjtGetChild(0);
	    search_variables = getVarArray((SimpleNode)expr1.jjtGetChild(0));

	    ASTAnnExpr expr2 = (ASTAnnExpr)ann.jjtGetChild(1);
	    var_selection_heuristic = ((ASTScalarFlatExpr)expr2.jjtGetChild(0)).getIdent();

	    ASTAnnExpr expr3 = (ASTAnnExpr)ann.jjtGetChild(2);
	    indomain = ((ASTScalarFlatExpr)expr3.jjtGetChild(0)).getIdent();

	    ASTAnnExpr expr4 = (ASTAnnExpr)ann.jjtGetChild(3);
	    if (! expr4.idPresent()) //(expr4.jjtGetNumChildren() == 1)
		explore = ((ASTScalarFlatExpr)expr4.jjtGetChild(0)).getIdent();
	    else if (expr4.getIdent().equals("credit")) {
		explore = "credit";
		if (expr4.jjtGetNumChildren() == 2) {
		    if (((SimpleNode)expr4.jjtGetChild(0)).getId() == JJTANNEXPR) {
			ASTAnnExpr cp = (ASTAnnExpr)expr4.jjtGetChild(0);
			if (cp.jjtGetNumChildren() == 1) {
			    creditValue = ((ASTScalarFlatExpr)cp.jjtGetChild(0)).getInt();
			}
		    }
		    ASTAnnExpr bbs = (ASTAnnExpr)expr4.jjtGetChild(1);
		    if (bbs.getId() == JJTANNEXPR && bbs.getIdent().equals("bbs")) {
			if (bbs.jjtGetNumChildren() == 1) {
			    if (((SimpleNode)bbs.jjtGetChild(0)).getId() == JJTANNEXPR) {
				ASTAnnExpr bv = (ASTAnnExpr)bbs.jjtGetChild(0);
				if (bv.jjtGetNumChildren() == 1) {
				    bbsValue = ((ASTScalarFlatExpr)bv.jjtGetChild(0)).getInt();
				    //  				    System.out.println("Credit("+creditValue+", "+bbsValue+")");
				    return;
				}
			    }
			}
		    }
		}
		explore = "complete";
		System.err.println("Warning: not recognized search exploration type; use \"complete\"");
	    }
	    else if  (expr4.getIdent().equals("lds")) {
		explore = "lds";
		if (expr4.jjtGetNumChildren() == 1) {
		    if (((SimpleNode)expr4.jjtGetChild(0)).getId() == JJTANNEXPR) {
			ASTAnnExpr ae = (ASTAnnExpr)expr4.jjtGetChild(0);
			if (ae.jjtGetNumChildren() == 1) {
			    ldsValue = ((ASTScalarFlatExpr)ae.jjtGetChild(0)).getInt();
			    return;
			}
		    }
		}
		explore = "complete";
		System.err.println("Warning: not recognized search exploration type; use \"complete\"");
	    }
	    else {
		System.err.println("Error: not recognized search exploration type; execution aborted");
		System.exit(0);
	    }
	}
	else if (search_type.equals("set_search")) {
	    ASTAnnExpr expr1 = (ASTAnnExpr)ann.jjtGetChild(0);
	    search_variables = getSetVarArray((SimpleNode)expr1.jjtGetChild(0));

	    ASTAnnExpr expr2 = (ASTAnnExpr)ann.jjtGetChild(1);
	    var_selection_heuristic = ((ASTScalarFlatExpr)expr2.jjtGetChild(0)).getIdent();

	    ASTAnnExpr expr3 = (ASTAnnExpr)ann.jjtGetChild(2);
	    indomain = ((ASTScalarFlatExpr)expr3.jjtGetChild(0)).getIdent();

	    ASTAnnExpr expr4 = (ASTAnnExpr)ann.jjtGetChild(3);
	    if (! expr4.idPresent())  //(expr4.jjtGetNumChildren() == 1)
		explore = ((ASTScalarFlatExpr)expr4.jjtGetChild(0)).getIdent();
	    else if  (expr4.getIdent().equals("credit")) {
		// 		explore = expr4.getIdent();
		explore = "complete";
		System.err.println("Warning: not recognized search exploration type; use \"complete\"");
		// 		System.exit(0);
	    }
	    else if  (expr4.getIdent().equals("lds")){
		explore = "lds";
		if (expr4.jjtGetNumChildren() == 1) {
		    if (((SimpleNode)expr4.jjtGetChild(0)).getId() == JJTANNEXPR) {
			ASTAnnExpr ae = (ASTAnnExpr)expr4.jjtGetChild(0);
			if (ae.jjtGetNumChildren() == 1) {
			    ldsValue = ((ASTScalarFlatExpr)ae.jjtGetChild(0)).getInt();
			    return;
			}
		    }
		}
		explore = "complete";
		System.err.println("Warning: not recognized search exploration type; use \"complete\"");
	    }
	    else {
		System.err.println("Error: not recognized search exploration type; execution aborted");
		System.exit(0);
	    }
	}
	else if (search_type.equals("seq_search")) {
	    int count = ann.jjtGetNumChildren();
	    for (int i=0; i<count; i++) {
		SearchItem subSearch = new SearchItem(store, dictionary);
		subSearch.searchParameters(ann, i);
		search_seq.add(subSearch);
	    }
	}
    }

    SelectChoicePoint getSelect() {
	if (search_type.equals("int_search") || search_type.equals("bool_search"))
	    return getIntSelect();
	else if (search_type.equals("set_search"))
	    return getSetSelect();
	else {
	    System.err.println("Error: not recognized search type \""+ search_type+"\";");
	    System.exit(0);
	    return null;
	}
    }

    SelectChoicePoint getIntSelect() {
	ComparatorVariable var_sel = getVarSelect();
	if (indomain != null && indomain.equals("indomain_split")) {
	    if (tieBreaking == null)
		return new SplitSelect(search_variables, var_sel, new IndomainMiddle());
	    else
		return new SplitSelect(search_variables, var_sel, tieBreaking, new IndomainMiddle());
	}
	else {
	    Indomain indom = getIndomain(indomain);
	    if (tieBreaking == null)
		return new SimpleSelect(search_variables, var_sel, indom);
	    else
		return new SimpleSelect(search_variables, var_sel, tieBreaking, indom);
	}
    }

    SelectChoicePoint getSetSelect() {
	ComparatorVariable var_sel = getsetVarSelect();
	Indomain indom = getIndomain(indomain);
	if (tieBreaking == null)
	    return new SetSimpleSelect(search_variables, var_sel, indom);
	else
	    return new SetSimpleSelect(search_variables, var_sel, tieBreaking, indom);
    }

    Indomain getIndomain(String indomain) {
	if (indomain == null)
	    return new IndomainMin();
	else if (indomain.equals("indomain_min")) 
	    return new IndomainMin();
	else if (indomain.equals("indomain_max")) 
	    return new IndomainMax();
	else if (indomain.equals("indomain_middle")) 
	    return new IndomainMiddle();
	else if (indomain.equals("indomain_random")) 
	    return new IndomainRandom();
	else
	    System.err.println("Warning: Not implemented indomain method \""+ 
			       indomain +"\"; used indomain_min");
	return new IndomainMin();
    }

    ComparatorVariable getVarSelect() {

	tieBreaking = null;
	if (var_selection_heuristic == null)
	    return null;
	else if (var_selection_heuristic.equals("input_order"))
	    return null;
	else if (var_selection_heuristic.equals("first_fail")) 
 	    return new SmallestDomain();
	else if (var_selection_heuristic.equals("anti_first_fail"))
	    return new LargestDomain();
	else if (var_selection_heuristic.equals("most_constrained")) {
	    tieBreaking = new MostConstrainedStatic();
	    return new SmallestDomain();
	}
	else if (var_selection_heuristic.equals("occurrence"))
	    return new MostConstrainedStatic();
	else if (var_selection_heuristic.equals("smallest")) {
	    tieBreaking = new SmallestDomain();
	    return new SmallestMin();
	}
	else if (var_selection_heuristic.equals("largest"))
	    return new LargestMin();
	else if (var_selection_heuristic.equals("max_regret"))
	    return new MaxRegret();
	else 
	    System.err.println("Warning: Not implemented variable selection heuristic \""+
			       var_selection_heuristic +"\"; used input_order");

	return null; // input_order
    }

    ComparatorVariable getsetVarSelect() {

	tieBreaking = null;
	if (var_selection_heuristic == null)
	    return null;
	else if (var_selection_heuristic.equals("input_order"))
	    return null;
	else if (var_selection_heuristic.equals("first_fail"))
	    return new MinCardDiff();
	else if (var_selection_heuristic.equals("smallest"))
	    return new MinGlbCard();
	else if (var_selection_heuristic.equals("occurrence"))
	    return new MostConstrainedStatic();
	else if (var_selection_heuristic.equals("anti_first_fail"))
	    return new MaxCardDiff();
	//  	else if (var_selection_heuristic.equals("most_constrained")) {
	// 	    tieBreaking = new MostConstrainedStatic();
	//  	    return new SmallestDomain();
	// 	}
	// 	else if (var_selection_heuristic.equals("largest"))
	// 	    return new LargestMin();
	// 	else if (var_selection_heuristic.equals("max_regret"))
	// 	    return new MaxRegret();
	else 
	    System.err.println("Warning: Not implemented variable selection heuristic \""+
			       var_selection_heuristic +"\"; used input_order");

	return null; // input_order
    }

    Variable getVariable(ASTScalarFlatExpr node) {
	if (node.getType() == 0) //int
	    return new Variable(store, node.getInt(), node.getInt());
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

    String type() {
	return search_type;
    }

    String exploration() {
	return explore;
    }

    String indomain() {
	return indomain;
    }

    String var_selection() {
	return var_selection_heuristic;
    }

    Variable[] vars() {
	return search_variables;
    }

    ArrayList<SearchItem> getSearchItems() {
	return search_seq;
    }

    public String toString() {
	String s="";
	if (search_seq.size() == 0)
	    s = search_type + ", "+ Arrays.asList(search_variables) +", "+explore + ", " + var_selection_heuristic+", "+indomain;
	else {
	    for (SearchItem se : search_seq)
		s += se +"\n";
	}
	return s;
    }
}
