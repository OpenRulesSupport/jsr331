package jsr331.openrules.gui;

import java.util.Vector;

import jsr331.biz.BizProblem;

import org.openl.util.Log;


public class ProblemManager {

	Vector<BizProblem> problems;

	int selected = -1;

	public ProblemManager() {
		problems = new Vector<BizProblem>();
	}

	public void select(int i) {
		Log.info("select problem number: "+i);
		if (i < 0 || i > problems.size()-1) {
			Log.error("CSP "+i+" is not defined");
		 	selected  = -1;
		}
		else
			selected = i;
	}

	public Vector<BizProblem> getBizProblems() {
		return problems;
	}

	public BizProblem getSelectedBizProblem() {
		if (selected < 0)
			return null;
		return problems.elementAt(selected);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void addProblem(BizProblem p) {
		problems.add(p);
	}

	public Vector<BizProblem> getProblems() {
		return problems;
	}

	public int getNumberOfProblems() {
		return problems.size();
	}

	public void setProblems(Vector<BizProblem> problems) {
		this.problems = problems;
	}

}
