/**
 *  Predicate.java 
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

package JaCoP.constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import JaCoP.core.Constants;
import JaCoP.core.FDV;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Predicate wrapper translate a predicate expression from XCSP into constraints
 * available in JaCoP. It accepts only functional representation. Possibly, 
 * some auxilary variables will be created.
 * 
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 * @version 2.4
 */

public class Predicate extends DecomposedConstraint implements Constants {

	//TODO if possible recognize existence of constraints of the
	// the following types, so save/load preserves the number of 
	// constraints in the model (otherwise it may represent constraints
	// using other constraints.
	
	String constraintParameters;

	final boolean debug = false;

	String description;

	Store store;

	String predicateParameters;

	ArrayList<Constraint> decompositionConstraints;
	
	ArrayList<Variable> auxilaryVariables;
	
	/**
	 * It creates/imposes constraints into the store as soon as the Predicate
	 * constraint is being imposed.
	 * @param constraintParameters parameters to the constraint.
	 * @param predicateParameters parameters specified within a predicate definition.
	 * @param description description of the constraint specified as the predicate.
	 * @param store the constraint store in which context the constraints are being created/imposed.
	 */
	public Predicate(String constraintParameters, String predicateParameters,
					 String description, Store store) {

		this.store = store;

		this.constraintParameters = constraintParameters;
		this.predicateParameters = predicateParameters;
		this.description = description;

	}

	@Override
	public ArrayList<Constraint> decompose(Store store) {

		if (decompositionConstraints != null)
			return decompositionConstraints;
		
		decompositionConstraints = new ArrayList<Constraint>();
		auxilaryVariables = new ArrayList<Variable>();
		
		StringTokenizer tokenizer = new StringTokenizer(predicateParameters, " ");
		StringTokenizer tokenizerConstraint = new StringTokenizer(
				constraintParameters, " ");

		HashMap<String, Object> variableMaping = new HashMap<String, Object>();

		while (tokenizer.hasMoreTokens()) {

			String nextToken = tokenizer.nextToken();

			if (nextToken.equals("int"))
				continue;

			String name = tokenizerConstraint.nextToken();

			Variable temp = store.findVariable(name);
			
			if (temp == null)
				variableMaping.put(nextToken, Integer.valueOf(name));
			else
				variableMaping.put(nextToken, temp);

		}

		if (debug)
			System.out.println(variableMaping);

		/// TODO, test if trimming spaces does not cause problems.
		description = description.replace(" ", "");
		
		tokenizer = new StringTokenizer(description, "(,)");

		String nextToken = tokenizer.nextToken();

		Object token = parse(nextToken, tokenizer, store, variableMaping);

		if (debug)
			System.out.println(token);

		decompositionConstraints.add( (Constraint) token );

		return decompositionConstraints;
	}

	
    /**
     * It allows to obtain the constraint specified by the predicate
     * without imposing it.
     *
     * @param store the constraint store in which context the constraint is being created.
     * @return the constraint represented by this predicate constraint (expression).
     */

    public PrimitiveConstraint getConstraint(Store store) {

            StringTokenizer tokenizer = new StringTokenizer(predicateParameters,
                            " ");

            StringTokenizer tokenizerConstraint = new StringTokenizer(
                            constraintParameters, " ");

            HashMap<String, Object> variableMaping = new HashMap<String, Object>();

            while (tokenizer.hasMoreTokens()) {

                    String nextToken = tokenizer.nextToken();

                    if (nextToken.equals("int"))
                            continue;

                    String name = tokenizerConstraint.nextToken();

                    Variable temp = store.findVariable(name);
                    if (temp == null)
                            variableMaping.put(nextToken, Integer.valueOf(name));
                    else
                            variableMaping.put(nextToken, temp);

            }

            if (debug)
                    System.out.println(variableMaping);

            description = description.replace(" ", "");
            
            tokenizer = new StringTokenizer(description, "(,)");

            String nextToken = tokenizer.nextToken();

            Object token = parse(nextToken, tokenizer, store, variableMaping);

            if (debug)
                    System.out.println(token);

            return (PrimitiveConstraint) token;

    }

	
	
	private Object parse(String token, StringTokenizer tokenizer, Store store,
			HashMap<String, Object> variableMaping) {

		try {
			return Integer.valueOf(token);
		} catch (Exception ex) {
			// if not an integer then just go on.
		}

        if (variableMaping.get(token) != null)
			return variableMaping.get(token);
		else {

			if (token.equals("abs")) {

				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				if (o1 instanceof Integer) {
					String val = String.valueOf(o1);
					if (variableMaping.get(val) == null) 
						variableMaping.put(val, new Variable(store, (Integer) o1, (Integer) o1));
					o1 = variableMaping.get(val);
				}

				if (o1 instanceof Variable) {
					Variable auxilary = new Variable(store, Constants.MinInt,
												Constants.MaxInt);
					auxilaryVariables.add(auxilary);
					decompositionConstraints.add(new AbsXeqY((Variable) o1, auxilary));
					return auxilary;
				}
			}

			if (token.equals("sub")) {

				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				if (o1 instanceof Variable && o2 instanceof Variable) {
					FDV auxilary = new FDV(store, Constants.MinInt,
							Constants.MaxInt);
					auxilaryVariables.add(auxilary);
					decompositionConstraints.add(new XplusYeqZ((Variable) o2, auxilary,
							(Variable) o1));
					return auxilary;
				} else if (o1 instanceof Variable && o2 instanceof Integer) {
					FDV auxilary = new FDV(store, Constants.MinInt,
							Constants.MaxInt);
					auxilaryVariables.add(auxilary);
					decompositionConstraints.add(new XplusCeqZ(auxilary, (Integer) o2,
							(Variable) o1));
					return auxilary;
				} else if (o1 instanceof Integer && o2 instanceof Variable) {
					FDV auxilary = new FDV(store, Constants.MinInt,
							Constants.MaxInt);
					auxilaryVariables.add(auxilary);
					decompositionConstraints.add(new XplusYeqC((Variable) o2, auxilary,
							(Integer) o1));
					return auxilary;
				}

			}

			if (token.equals("add")) {

				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				if (o1 instanceof Variable && o2 instanceof Variable) {
					FDV auxilary = new FDV(store, Constants.MinInt,
							Constants.MaxInt);
					auxilaryVariables.add(auxilary);
					decompositionConstraints.add(new XplusYeqZ((Variable) o1, (Variable) o2,
							auxilary));
					return auxilary;
				} else if (o1 instanceof Variable && o2 instanceof Integer) {
					FDV auxilary = new FDV(store, Constants.MinInt,
							Constants.MaxInt);
					auxilaryVariables.add(auxilary);
					decompositionConstraints.add(new XplusCeqZ((Variable) o1, (Integer) o2,
							auxilary));
					return auxilary;
				} else if (o1 instanceof Integer && o2 instanceof Variable) {
					FDV auxilary = new FDV(store, Constants.MinInt,
							Constants.MaxInt);
					auxilaryVariables.add(auxilary);
					decompositionConstraints.add(new XplusCeqZ((Variable) o2, (Integer) o1,
							auxilary));
					return auxilary;
				}

			}

			if (token.equals("mul")) {

				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				if (o1 instanceof Variable && o2 instanceof Variable) {
					FDV auxilary = new FDV(store, Constants.MinInt,
							Constants.MaxInt);
					auxilaryVariables.add(auxilary);
					decompositionConstraints.add(new XmulYeqZ((Variable) o1, (Variable) o1,
							auxilary));
					return auxilary;
				} else if (o1 instanceof Variable && o2 instanceof Integer) {
					FDV auxilary = new FDV(store, Constants.MinInt,
							Constants.MaxInt);
					auxilaryVariables.add(auxilary);
					decompositionConstraints.add(new XmulCeqZ((Variable) o1, (Integer) o2,
							auxilary));
					return auxilary;
				} else if (o1 instanceof Integer && o2 instanceof Variable) {
					FDV auxilary = new FDV(store, Constants.MinInt,
							Constants.MaxInt);
					auxilaryVariables.add(auxilary);
					decompositionConstraints.add(new XmulCeqZ((Variable) o2, (Integer) o1,
							auxilary));
					return auxilary;
				}

			}

			if (token.equals("div")) {

				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				if (o1 instanceof Variable && o2 instanceof Variable) {
					FDV auxilary = new FDV(store, Constants.MinInt,
							Constants.MaxInt);
					auxilaryVariables.add(auxilary);
					decompositionConstraints.add(new XdivYeqZ((Variable) o1, 
										  (Variable) o2, auxilary));
					return auxilary;
				} else if (o1 instanceof Variable && o2 instanceof Integer) {
					FDV auxilary = new FDV(store, Constants.MinInt,
							Constants.MaxInt);
					auxilaryVariables.add(auxilary);
					int valO2 = ((Integer)o2).intValue();
					Variable varO2 = new Variable(store, valO2, valO2);
					decompositionConstraints.add(new XdivYeqZ((Variable) o1, 
										  varO2, 
										  auxilary));
					return auxilary;
				} else if (o1 instanceof Integer && o2 instanceof Variable) {
					FDV auxilary = new FDV(store, Constants.MinInt,
							Constants.MaxInt);
					auxilaryVariables.add(auxilary);
					int valO1 = ((Integer)o1).intValue();
					Variable varO1 = new Variable(store, valO1, valO1);
					decompositionConstraints.add(new XdivYeqZ(varO1,
										  (Variable) o2, 
										  auxilary));
					return auxilary;
				}

			}

			if (token.equals("mod")) {

				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				if (o1 instanceof Variable && o2 instanceof Variable) {
					FDV auxilary = new FDV(store, 0, Constants.MaxInt);
					
					auxilaryVariables.add(auxilary);
					decompositionConstraints.add(new XmodYeqZ((Variable) o1, 
										  (Variable) o2, auxilary));
					
					return auxilary;
				} else if (o1 instanceof Variable && o2 instanceof Integer) {

					FDV auxilary = new FDV(store, 0, (Integer) o2 - 1);
					
					auxilaryVariables.add(auxilary);

					int valO2 = ((Integer)o2).intValue();
					Variable varO2 = new Variable(store, valO2, valO2);
					decompositionConstraints.add(new XmodYeqZ((Variable) o1, 
										  varO2, 
										  auxilary));

					return auxilary;
				} else if (o1 instanceof Integer && o2 instanceof Variable) {

					FDV auxilary = new FDV(store, 0, Constants.MaxInt);
					
					auxilaryVariables.add(auxilary);
					
					int valO1 = ((Integer)o1).intValue();
					Variable varO1 = new Variable(store, valO1, valO1);
					decompositionConstraints.add(new XmodYeqZ(varO1,
										  (Variable) o2, 
										  auxilary));

					return auxilary;
				}
			}

			if (token.equals("pow")) {

				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				if (o1 instanceof Integer) {
					String val = String.valueOf(o1);
					if (variableMaping.get(val) == null)
						variableMaping.put(val,	new Variable(store, (Integer) o1, (Integer) o1));
					o1 = variableMaping.get(val);
				}

				if (o2 instanceof Integer) {
					String val = String.valueOf(o2);
					if (variableMaping.get(val) == null)
						variableMaping.put(val,
								new FDV(store, (Integer) o2, (Integer) o2));
					o2 = variableMaping.get(val);
				}

				FDV auxilary = new FDV(store, Constants.MinInt,
						Constants.MaxInt);
				auxilaryVariables.add(auxilary);

				decompositionConstraints.add(new XexpYeqZ((Variable) o1, (Variable) o2,
						auxilary));

				return auxilary;

			}

			if (token.equals("eq")) {

				String nextToken = tokenizer.nextToken();

				if (nextToken.equals("abs")) {

					String nextNextToken = tokenizer.nextToken();

					if (nextNextToken.equals("sub")) {

						Object o1 = parse(tokenizer.nextToken(), tokenizer,
								store, variableMaping);
						Object o2 = parse(tokenizer.nextToken(), tokenizer,
								store, variableMaping);
						Object o3 = parse(tokenizer.nextToken(), tokenizer,
								store, variableMaping);

						if (o1 instanceof Integer) {
							String val = String.valueOf(o1);
							if (variableMaping.get(val) == null)
								variableMaping.put(val, new Variable(store, (Integer) o1,
										(Integer) o1));
							o1 = variableMaping.get(val);
						}

						if (o2 instanceof Integer) {
							String val = String.valueOf(o2);
							if (variableMaping.get(val) == null)
								variableMaping.put(val, new Variable(store, (Integer) o2,
										(Integer) o2));
							o2 = variableMaping.get(val);
						}

						if (o3 instanceof Integer) {
							String val = String.valueOf(o3);
							if (variableMaping.get(val) == null)
								variableMaping.put(val, new Variable(store, (Integer) o3,
										(Integer) o3));
							o3 = variableMaping.get(val);
						}

						return new Distance((Variable) o1, (Variable) o2,
								(Variable) o3);

					} else {

						Object o1 = parse(nextNextToken, tokenizer, store,
								variableMaping);

						FDV auxilary = new FDV(store, Constants.MinInt,
								Constants.MaxInt);
						auxilaryVariables.add(auxilary);

						decompositionConstraints.add(new AbsXeqY((Variable) o1, auxilary));

						Object o2 = parse(tokenizer.nextToken(), tokenizer,
								store, variableMaping);

						return (new XeqY(auxilary, (Variable) o2));

					}

				}

				if (nextToken.equals("add")) {

					Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
							variableMaping);
					Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
							variableMaping);
					Object o3 = parse(tokenizer.nextToken(), tokenizer, store,
							variableMaping);

					if (o1 instanceof Variable && o2 instanceof Variable
							&& o3 instanceof Variable) {
						return (new XplusYeqZ((Variable) o1, (Variable) o2,
								(Variable) o3));
					}

					if (o1 instanceof Variable && o2 instanceof Integer
							&& o3 instanceof Variable) {
						return (new XplusCeqZ((Variable) o1, (Integer) o2,
								(Variable) o3));
					}

					if (o1 instanceof Integer && o2 instanceof Variable
							&& o3 instanceof Variable) {
						return (new XplusCeqZ((Variable) o2, (Integer) o1,
								(Variable) o3));
					}

					if (o1 instanceof Variable && o2 instanceof Variable
							&& o3 instanceof Integer) {
						return (new XplusYeqC((Variable) o1, (Variable) o2,
								(Integer) o3));
					}

				}

				if (nextToken.equals("mul")) {

					Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
							variableMaping);
					Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
							variableMaping);
					Object o3 = parse(tokenizer.nextToken(), tokenizer, store,
							variableMaping);

					if (o1 instanceof Variable && o2 instanceof Variable
							&& o3 instanceof Variable) {
						return (new XmulYeqZ((Variable) o1, (Variable) o2,
								(Variable) o3));
					}

					if (o1 instanceof Variable && o2 instanceof Integer
							&& o3 instanceof Variable) {
						return (new XmulCeqZ((Variable) o1, (Integer) o2,
								(Variable) o3));
					}

					if (o1 instanceof Integer && o2 instanceof Variable
							&& o3 instanceof Variable) {
						return (new XmulCeqZ((Variable) o2, (Integer) o1,
								(Variable) o3));
					}

					if (o1 instanceof Variable && o2 instanceof Variable
							&& o3 instanceof Integer) {
						return (new XmulYeqC((Variable) o1, (Variable) o2,
								(Integer) o3));
					}

				}

				// System.out.println("nextToken " + nextToken);

				Object o1 = parse(nextToken, tokenizer, store, variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				// System.out.println(o1);
				// System.out.println(o2);

				if (o1 instanceof Variable && o2 instanceof Variable)
					return new XeqY((Variable) o1, (Variable) o2);
				else if (o1 instanceof Variable && o2 instanceof Integer)
					return new XeqC((Variable) o1, (Integer) o2);
				else if (o1 instanceof Integer && o2 instanceof Variable)
					return new XeqC((Variable) o2, (Integer) o1);
				else
					return new Eq((PrimitiveConstraint) (o1),
							(PrimitiveConstraint) (o2));
			}

			if (token.equals("ne")) {

				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				if (o1 instanceof Variable && o2 instanceof Variable)
					return new XneqY((Variable) o1, (Variable) o2);
				else if (o1 instanceof Variable && o2 instanceof Integer)
					return new XneqC((Variable) o1, (Integer) o2);
				else if (o1 instanceof Integer && o2 instanceof Variable)
					return new XneqC((Variable) o2, (Integer) o1);
				else
					return new Eq(new Not((PrimitiveConstraint) (o1)),
							(PrimitiveConstraint) (o2));
			}

			if (token.equals("ge")) {
				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				if (o1 instanceof Variable && o2 instanceof Variable)
					return new XgteqY((Variable) o1, (Variable) o2);
				else if (o1 instanceof Variable && o2 instanceof Integer)
					return new XgteqC((Variable) o1, (Integer) o2);
				else if (o1 instanceof Integer && o2 instanceof Variable)
					return new XlteqC((Variable) o2, (Integer) o1);
				else
					System.out.println("ge predicate does not have variable parameters");
			}

			if (token.equals("gt")) {
				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				if (o1 instanceof Variable && o2 instanceof Variable)
					return new XgtY((Variable) o1, (Variable) o2);
				else if (o1 instanceof Variable && o2 instanceof Integer)
					return new XgtC((Variable) o1, (Integer) o2);
				else if (o1 instanceof Integer && o2 instanceof Variable)
					return new XltC((Variable) o2, (Integer) o1);
				else
					System.out.println("gt predicate does not have variable parameters");
			}

			if (token.equals("le")) {

				String nextToken = tokenizer.nextToken();

				if (nextToken.equals("add")) {

					Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
							variableMaping);
					Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
							variableMaping);
					Object o3 = parse(tokenizer.nextToken(), tokenizer, store,
							variableMaping);

					if (o1 instanceof Variable && o2 instanceof Variable
							&& o3 instanceof Variable) {
						return (new XplusYlteqZ((Variable) o1, (Variable) o2,
								(Variable) o3));
					}

					if (o1 instanceof Variable && o2 instanceof Integer
							&& o3 instanceof Variable) {
						return (new XplusClteqZ((Variable) o1, (Integer) o2,
								(Variable) o3));
					}

					if (o1 instanceof Integer && o2 instanceof Variable
							&& o3 instanceof Variable) {
						return (new XplusClteqZ((Variable) o2, (Integer) o1,
								(Variable) o3));
					}

					if (o1 instanceof Variable && o2 instanceof Variable
							&& o3 instanceof Integer) {

						String val = String.valueOf(o3);
						if (variableMaping.get(val) == null)
							variableMaping.put(val, new Variable(store, (Integer) o3,
									(Integer) o3));
						o3 = variableMaping.get(val);

						return (new XplusYlteqZ((Variable) o1, (Variable) o2,
								(Variable) o3));
					}

				}

				Object o1 = parse(nextToken, tokenizer, store, variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				if (o1 instanceof Variable && o2 instanceof Variable)
					return new XlteqY((Variable) o1, (Variable) o2);
				else if (o1 instanceof Variable && o2 instanceof Integer)
					return new XlteqC((Variable) o1, (Integer) o2);
				else if (o1 instanceof Integer && o2 instanceof Variable)
					return new XgteqC((Variable) o2, (Integer) o1);
				else
					System.out.println("le predicate does not have variable parameters");
			}

			if (token.equals("lt")) {
				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				if (o1 instanceof Variable && o2 instanceof Variable)
					return new XltY((Variable) o1, (Variable) o2);
				else if (o1 instanceof Variable && o2 instanceof Integer)
					return new XltC((Variable) o1, (Integer) o2);
				else if (o1 instanceof Integer && o2 instanceof Variable)
					return new XgtC((Variable) o2, (Integer) o1);
				else
					System.out.println("lt predicate does not have variable parameters");
			}

			if (token.equals("not")) {
				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				return new Not((PrimitiveConstraint) o1);
			}

			if (token.equals("and")) {
				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				return new And((PrimitiveConstraint) o1,
						(PrimitiveConstraint) o2);
			}

			if (token.equals("or")) {
				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				return new Or((PrimitiveConstraint) o1,
						(PrimitiveConstraint) o2);
			}

			if (token.equals("xor")) {
				Object o1 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);
				Object o2 = parse(tokenizer.nextToken(), tokenizer, store,
						variableMaping);

				return new Eq(new Not((PrimitiveConstraint) (o1)),
						(PrimitiveConstraint) (o2));
			}

			System.out.println(token);
			return null;

		}

	}


	@Override
	public void imposeDecomposition(Store store) {
		
		if (decompositionConstraints == null)
			decompose(store);
		
		for (Constraint c : decompositionConstraints)
			store.impose(c);
		
		store.auxilaryVariables.addAll(auxilaryVariables);
	}

	@Override
	public ArrayList<Variable> auxiliaryVariables() { 
		return auxilaryVariables;
	}
	
}
