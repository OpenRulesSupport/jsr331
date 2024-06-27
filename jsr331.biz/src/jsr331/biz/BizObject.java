package jsr331.biz;

/*----------------------------------------------------------------
 * Copyright Cork Constraint Computation Centre, 2006-2007
 * University College Cork, Ireland, www.4c.ucc.ie
 *---------------------------------------------------------------*/

abstract public class BizObject {

	private final BizProblem problem;

	private String name;

	private int status;

	private boolean visible;

	private String value; // keeps a string value of the variable
						  // defined by the last found solution

	private String domain;  // keeps a string value of the variable's domain
							// defined by the last constraint posting

	public BizObject(BizProblem csp, String name) {
		this.problem = csp;
		this.name = name;
		status = BizConstant.STATUS_UNKNOWN;
		visible = true;
	}

	public BizObject(BizProblem csp) {
		this(csp, "");
	}

	public BizProblem getProblem() {
		return problem;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * This message should be overloaded by a concrete CorkObject implementation
	 */
	public String getInfo(String type) {
		problem.log("UNKNOWN CorkObject");
		return "";
	}

	/**
	 * This method should be implemented in an interface to a selected CP solver to
	 * save a value of the instantiated constrained variables associated with
	 * this CorkObject and defined during the latest solution search
	 */
	abstract public void saveValue();

	/**
	 * @return true if the domain of the variable contains only one value
	 */
	abstract public boolean isBound();

	/**
	 * This method is used by a selected CorkCSP implementation to save the values
	 * of all variables after finding the problem solution
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * This method is used to display variables values (solutions) after successful
	 * solution search using goals Find Solution, Minimize or Maximize.
	 * @return a string that represents a value of the variable found during
	 * the last solution search; an empty string if no solutions.
	 */
	public String getValue() {
		if (getProblem().getNumberOfFoundSolutions() == 0) {
			//getProblem().log("Attempt to get value of unbound constrained object "+getName());
			return "";
		}
		return value;
	}

	/**
	 * This method is used to display domains of variables after successful
	 * solution search using goals Find Solution, Minimize or Maximize.
	 * @return a string that represents a domain of the constrained object
	 * after the latest constraint posting.
	 */
	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * This method should be defined by an adapter to a concrete solver
	 * implementation. It saves the current state of the object's domain
	 * as a String.
	 */
	abstract public void saveDomain();

}
