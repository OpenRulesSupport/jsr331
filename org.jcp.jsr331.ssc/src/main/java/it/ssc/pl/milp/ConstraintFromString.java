package it.ssc.pl.milp;

import  it.ssc.i18n.RB;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ConstraintFromString {
	private ArrayList<Constraint> constraint;
	
	 ConstraintFromString(int dimension, ArrayList<String> inequality) throws SimplexException, LPException {
		 this.constraint=new  ArrayList<Constraint> ();
		 for(String disequa:inequality) {
			 constraint.add(getConstraintFromString(dimension, disequa));
		 }
		 
	 }
	 
	 ConstraintFromString(int dimension, ArrayList<String> inequality,ArrayList<Constraint> constraint) throws SimplexException, LPException {
		 this.constraint=constraint;
		 for(String disequa:inequality) {
			 this.constraint.add(getConstraintFromString(dimension, disequa));
		 }
	 }
	 
	 
	 
	 public ArrayList<Constraint> getConstraint() {
		return constraint;
	}
	 

	private Constraint getConstraintFromString(int dim,String dis) throws  LPException, SimplexException {

		 Pattern pattern = Pattern.compile("\\s*([+-]?)\\s*(((\\d+)((\\.)?)(\\d*))?)X(\\d+)((\\s*([+-])\\s*(((\\d+)((\\.)?)(\\d*))?)X(\\d+))*)\\s*((<\\s*=)|(>\\s*=)|(=))\\s*(([+-]?)(\\d+)((\\.)?)(\\d*))",Pattern.CASE_INSENSITIVE);
		 Matcher matcher_group_var = pattern.matcher(dis);

		 if (matcher_group_var.matches()) {
			 return equationFromString(dim, dis);
		 } 
		 else {
			 throw new LPException(RB.getString("it.ssc.pl.milp.ConstraintFromString.msg1")+dis);
		 }
		 
	 }
	 
	 
	 private Constraint equationFromString(int dim, String s) throws SimplexException  {
		 ConsType rel=null;
		 //System.out.println("dim"+dim);
		 if (     s.matches("(.+)>\\s*=(.+)")) {
			 rel = ConsType.GE;
		 } 
		 else if (s.matches("(.+)<\\s*=(.+)")) {
			 rel = ConsType.LE;
		 } 
		 else if (s.contains("=")) {
			 rel = ConsType.EQ;
		 } 
		
		 String[] disequation = s.split("[><]?\\s*=");
		 double b = Double.parseDouble(disequation[1].trim());
		 
		 double[] Aj = new double[dim];
		 for(int i=0;i<dim;i++) Aj[i]=0.;
		 
		 String left = disequation[0].trim().replaceAll("x", "X");
		 left = left.replaceAll("[+]\\s*", "+");
		 left = left.replaceAll("[-]\\s*", "-");
		 String[] tokens = left.split("\\s*[+-]");
		 for (String token : tokens) {
			 if(token.equals("")) continue;
			 String[] token_split = token.trim().split("X");
			 String value=null;
			 if(token_split[0].equals("") || token_split[0].equals("+")) value="1";
			 else if (token_split[0].equals("-")) value="-1";
			 else value=token_split[0];
			 double valore= Double.parseDouble(value);
			 //System.out.println(token_split[0]);
			 //System.out.println(token_split[0]+"@@"+token_split[1]);
			 int index = Integer.parseInt(token_split[1]);
			 Aj[index-1] = valore;
		 }
		
		 return new Constraint(Aj,rel,b);
	 }
}
