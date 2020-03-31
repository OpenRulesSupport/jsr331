//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//=============================================
package javax.constraints;

import java.util.Set;

/**
 * This interface represents a constrained set variable
 * which, when bound, is equal to a set of elements. The domain
 * of a constrained set variable is a set of Sets that consist of
 * regular integers.
 * <p>
 * The domain of a constrained set variable defines two sets:
 * <ol>
 * <li> The Required Set: the set of the elements that belong to all
 * the possible values of the variable (the lower bound);
 * <li> The Possible Set: the set of the elements that belong to
 * at least one of the possible values of the variable (the upper bound).
 * The required set is always a subset of the possible set.
 * You may only remove elements from the possible set - method "remove".
 * You may only add element to the required set - method "require".
 *</ol><p>
 * The cardinality of a set constrained variable is an integer
 * constrained variable.
 * <p>
 * You may define set intersections and unions.
 * <p>
 * See also {@link Problem#variableSet(String, int[]) Problem#elementAt(Set[], Var)}
 *
 */
public interface VarSet extends ConstrainedVariable {

	/**
	 * @return true when all elements in the domain are required
	 * @return false otherwise
	 */
	public boolean isBound();

	/**
	 * @return a Set of integers in the required set
	 * @throws Exception if this set variable in not bound
	 */
	public Set<Integer> getValue() throws Exception;

	/**
	 * Instantiates this set variable with the constant "set" of integers.
	 * All integers that do not belong to the "set" will be removed from
	 * the possible set.
	 * @param set
	 * @throws Exception if some values in the "set' are not possible
	 */
	public void setValue(Set<Integer> set) throws Exception;

	/**
	 * @return the required set
	 */
	public Set<Integer> getRequiredSet();

//	/**
//	 * @return an array of all constrained integer variable that represent
//	 * the current state of the required set
//	 */
//	public Var[] getRequiredVars();

	/**
	 * @return the possible set
	 */
	public Set<Integer> getPossibleSet();

	/**
	 * @return true if the "value" belongs to the possible set
	 */
	public boolean isPossible(int value);

	/**
	 * @return true if the "value" belongs to the required set
	 */
	public boolean isRequired(int value);

	/**
	 * Removes the "value' from the possible set
	 * @param value
	 * @throws Exception
	 */
	public void remove(int value) throws Exception;

	/**
	 * Adds the "value" to the required set
	 * @param val
	 * @throws Exception if the value is outside of the possible set
	 */
	public void require(int val) throws Exception;

	/**
	 * @param setOfValues
	 * @return true if the possible set contains "setOfValues"
	 */
	public boolean contains(Set<Integer> setOfValues);

	/**
	 * Creates a new set variable that is an intersection of this set variable
	 * with the "varSet" passed as a parameter.
	 * @param varSet
	 * @return a set variable
	 * @throws Exception
	 */
	public VarSet intersection(VarSet varSet) throws Exception;

	/**
	 * Sets the cardinality of this set variable to be equals 0 if
	 * the flag is true, and to be more or equal 1 if the flag is false.
	 * @param flag
	 */
	public void setEmpty(boolean flag);

	/**
	 * Creates a new set variable that is an union of this set variable
	 * with the "varSet" passed as a parameter.
	 * @param varSet
	 * @return a set variable
	 * @throws Exception
	 */
	public VarSet union(VarSet varSet) throws Exception;

	/**
	 *
	 * @return an integer constraint variable that represents a number of elements
	 * in the domain of this variable when it is instantiated
	 */
	public Var getCardinality();

}
