package javax.constraints.visual;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.constraints.Constraint;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.impl.Problem;

public class ProblemVisual extends Problem {
	
	ArrayList<VisualEntry> visualEntries;
	int stateId;
	int vId;
	PrintStream stream = null;
	
	public ProblemVisual(String id) {
		super(id);
		visualEntries = new ArrayList<VisualEntry>();
		stateId = 0;
		vId = 0;
	}

	/**
	 * Create a problem visualizer that will write all visualization
	 * information about problem variables and constraints to the file
	 * with a given name
	 * @param filename String
	 * @throws Exception IOException - if writing to the specified
	 * output file results in an IOException;
	 */
	public void startVisualization(String fileName) {
		try {
			stream = new PrintStream(fileName);
			out("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
			out("<visualization version=\"1.0\"");
			out("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			out("  xsi:noNamespaceSchemaLocation=\"visualization.xsd\">");
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	protected void out(String text) {
		stream.println(text);
	}
	
	/**
	 * Finalizes the writing to the currently started visualizer and
	 * closes the proper print stream.
	 */
	public void stopVisualization() {
		out("</visualization>");
		stream.close();
	}
	
	protected Solver createSolver() {
		return new SolverVisual(this);
	}
	
//	/**
//	 * register a constraint for visualization in the ConstraintVariableLog
//	 * it will be drawn at every search step or when the log is updated manually
//	 * @param constraint
//	 */
//	public void register(Constraint constraint);
//	
//	public void register(Var var);
//	
//	public void register(Var[] varArray);
//	
//	public void register(Var[][] varMatrix);

	public void snapshot(){
		snapshot(-1);
	}
	
	protected void snapshot(int treeNode){
		stateId++;
		out("<state id=\""+stateId+"\" treeNode=\""+treeNode+"\">");
		for(VisualEntry entry : visualEntries){
			out("<visualizer_state id=\""+entry.getNr()+"\">");
			switch (entry.getType()){
			case CONSTRAINT:
				Constraint constraint = entry.getConstraint();
				Class<?> insClass = constraint.getClass();
				String methodName = "snapshot";
				Method method;
				try {
					method = insClass.getMethod(methodName,
							                    new Class[] {ProblemVisual.class});
					method.invoke(constraint, this);
				} catch (Exception e) {
					log("Error invoking the method " + methodName 
							+ " for Constraint "+entry.getName());
					log(e.toString());
				}
				//entry.getConstraint().snapshot(this);
				break;
			case VECTOR:
				snapshot(entry.getVector());
				break;
			case MATRIX:
				snapshot(entry.getMatrix());
				break;
		
			case VARIABLE:
				tagVariable(0,entry.getVariable());
				break;
			}
			out("</visualizer_state>");
		}
		out("</state>");
	}
	
//	public void tagVariable(Var var) {
//		out("<dvar index=\""+var.getIndex()+
//				"\" domain=\""+var.getMin()+".."+var.getMax()+"\"/>");
//	} 

	public void tagVariable(String index, Var var) {
		out("<dvar index=\""+index+
				"\" domain=\""+getDomainAsList(var)+"\"/>");
	}

	public void tagVariable(int index, Var var) {
		out("<dvar index=\""+index+
				"\" domain=\""+getDomainAsList(var)+"\"/>");
	}
	
	protected String getDomainAsList(Var var){
		String string=""+var.getMin();
		for(int i=var.getMin()+1;i<=var.getMax();i++){
			if(var.contains(i)) {
				string = string+" "+i;
			}
		}
		return string;
	}
	
	public void tagInteger(String index, int value) {
		out("<integer index=\""+index+
				"\" value=\""+value+"\"/>");
	}
	
	public void tagInteger(int index, int value) {
		out("<integer index=\""+index+
				"\" value=\""+value+"\"/>");
	}

	public void snapshot(Var[] vector){
		int n = vector.length;
		for(int i=0;i<n;i++){
			tagVariable(Integer.toString(i),vector[i]);
		}
	}
	
	public void snapshot(Var[][] matrix){
	}
	
	public void register(Constraint constraint) {
		visualEntries.add(new VisualEntry(constraint,vId++));
	}

	public void register(Var var) {
		visualEntries.add(new VisualEntry(var,vId++));
	}

	public void register(Var[] varArray) {
		visualEntries.add(new VisualEntry(varArray,vId++));
	}

	public void register(Var[][] varMatrix) {
		visualEntries.add(new VisualEntry(varMatrix,vId++));
	}

	public void visualizer(int id,String type,String display,
			int x,int y,int width,int height,int group,int min,int max){
		out("<visualizer id=\""+id+
				"\" type=\""+type+
				"\" display=\""+display+
				"\" x=\""+x+
				"\" y=\""+y+
				"\" width=\""+width+
				"\" height=\""+height+
				"\" group=\""+group+
				"\" min=\""+min+
				"\" max=\""+max+
				"\"/>");
	}
	
	public void startTagArgument(String index){
		out("<argument index=\""+index+"\">");
	}
	public void startTagArgument(int index){
		out("<argument index=\""+index+"\">");
	}
	public void endTagArgument(){
		out("</argument>");
	}
	public void startTagCollection(String index){
		out("<collection index=\""+index+"\">");
	}
	public void startTagCollection(int index){
		out("<collection index=\""+index+"\">");
	}
	public void endTagCollection(){
		out("</collection>");
	}
	public void startTagTuple(String index){
		out("<tuple index=\""+index+"\">");
	}
	public void startTagTuple(int index){
		out("<tuple index=\""+index+"\">");
	}
	public void endTagTuple(){
		out("</tuple>");
	}

}
