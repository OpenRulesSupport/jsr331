package javax.constraints.visual;

import java.io.PrintStream;

import javax.constraints.SearchStrategy;
import javax.constraints.impl.search.Solver;

public class SolverVisual extends Solver {

	PrintStream stream = null;
	ProblemVisual problemVisual;
	Nodes nodes;

	public SolverVisual(ProblemVisual problem) {
		super(problem);
		problemVisual = problem;
		nodes = new Nodes();
	}

	/**
	 * Create a problem visualizer that will write all visualization information
	 * about problem variables and constraints to the file with a given name
	 * 
	 * @param filename
	 *            String
	 * @throws Exception
	 *             IOException - if writing to the specified output file results
	 *             in an IOException;
	 */
	public void startVisualization(String fileName){
		try {
			stream = new PrintStream(fileName);
			out("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
			out("<tree version=\"1.0\"");
			out("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			out("  xsi:noNamespaceSchemaLocation=\"tree.xsd\">");
			nodes = new Nodes();
			nodes.addNode();
			addRootNode(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected Nodes nodes() {
		return nodes;
	}
	
	protected void out(String text) {
		stream.println(text);
	}

	/**
	 * Finalizes the writing to the currently started visualizer and closes the
	 * proper print stream.
	 */
	public void stopVisualization() {
		out("</tree>");
		stream.close();
	}

	/**
	 * add unique root node for SearchTreeLog, before any other nodes are
	 * created
	 * 
	 * @param id
	 *            int
	 */
	public void addRootNode(int id) {
		out("<root id=\"" + id + "\"/>");
		problemVisual.snapshot(id);
	}

	/**
	 * add a choice node for the SearchTreeLog, where the choice succeeded
	 * 
	 * @param id
	 * @param parentId
	 * @param variableName
	 * @param size
	 * @param value
	 */
	public void addSuccessNode(int id, int parentId, String variableName,
			int size, int value) {
		out("<try id=\"" + id + "\" parent=\"" + parentId + "\" name=\""
				+ variableName + "\" size=\"" + size + "\" value=\"" + value
				+ "\"/>");
		problemVisual.snapshot(id);
	}

	public void addSuccessNode(int id, int parentId, String variableName,
			int size, String choice) {
		out("<try id=\"" + id + "\" parent=\"" + parentId + "\" name=\""
				+ variableName + "\" size=\"" + size + "\" choice=\"" + choice
				+ "\"/>");
		problemVisual.snapshot(id);
	}

	/**
	 * adds a failure node for the SearchTreeLog, where the attempted choice
	 * failed due to propagation
	 * 
	 * @param id
	 * @param parentId
	 * @param variableName
	 * @param size
	 * @param value
	 */
	public void addFailureNode(int id, int parentId, String variableName,
			int size, int value) {
		out("<fail id=\"" + id + "\" parent=\"" + parentId + "\" name=\""
				+ variableName + "\" size=\"" + size + "\" value=\"" + value
				+ "\"/>");
		problemVisual.snapshot(id);
	}

	public void addFailureNode(int id, int parentId, String variableName,
			int size, String choice) {
		out("<failc id=\"" + id + "\" parent=\"" + parentId + "\" name=\""
				+ variableName + "\" size=\"" + size + "\" choice=\"" + choice
				+ "\"/>");
		problemVisual.snapshot(id);
	}

	/**
	 * label a Success node as a solution,i.e. the current variable assignment
	 * satisfies all constraints
	 * 
	 * @param id
	 */
	public void labelSolutionNode(int id) {
		out("<succ id=\"" + id + "\"/>");
		problemVisual.snapshot(id);
	}
	
	/**
	 * This methods returns a new default search strategy 
	 * @return a new default search strategy
	 */
	public SearchStrategy newSearchStrategy() {
		return new GoalAssignValuesVisual(this);
	}
}
