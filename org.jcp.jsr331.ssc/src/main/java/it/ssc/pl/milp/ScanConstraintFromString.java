package it.ssc.pl.milp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import it.ssc.i18n.RB;
import it.ssc.pl.milp.InternalConstraint.TYPE_CONSTR;

final class ScanConstraintFromString {
	private ArrayList<InternalConstraint> new_constraints;
	private ArrayList<String> nomi_var;
	private int dimension;
	private Double Ai[];
	private ArrayProblem arraysProb;
	
	public ScanConstraintFromString(ArrayList<String> inequality,ArrayList<String> nomi_var) throws  ParseException, LPException {
		this.nomi_var=nomi_var;
		this.dimension=nomi_var.size();
		this.new_constraints=new ArrayList<InternalConstraint>();
		Ai=new Double[this.dimension];
		arraysProb=new ArrayProblem(this.dimension);
		
		for(String line_problem:inequality) {
			parseSingleLine(line_problem);
		}	
	}
	
	//da file 
	public ScanConstraintFromString(BufferedReader br,ArrayList<String> nomi_var) throws  IOException, ParseException, LPException {
		this.nomi_var=nomi_var;
		this.dimension=nomi_var.size();
		this.new_constraints=new ArrayList<InternalConstraint>();
		Ai=new Double[this.dimension];
		arraysProb=new ArrayProblem(this.dimension);
		
		String line;
		while((line = br.readLine()) != null   ) {
			parseSingleLine(line);
		}	
	}
	
	public ArrayList<InternalConstraint> getConstraints() {
		return new_constraints;
	}
	

	private void parseSingleLine(String inequality) throws  ParseException, LPException {
		if(inequality.trim().equals("")) return;
		
		//pattern per i vincoli generici
		//row1:5x1 +2x2 +3X4 >= 9
		Pattern pattern =     Pattern.compile("\\s*(\\p{Alpha}+\\p{Alnum}*\\s*:)?\\s*[+-]?\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\p{Alnum}*)\\s*",Pattern.CASE_INSENSITIVE);
		Matcher matcher_group_var = pattern.matcher(inequality);
				
		//upper e lower di tipo <= x <=
		Pattern pattern_1 = Pattern.compile("\\s*(bound_scar\\s*:)?\\s*(((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.))\\s*<\\s*=)?\\s*(\\p{Alpha}+\\p{Alnum}*)\\s*(<\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.)))?\\s*",Pattern.CASE_INSENSITIVE);
		Matcher upper_1 = pattern_1.matcher(inequality);
		//upper e lower di tipo >= x >=
		Pattern pattern_2 = Pattern.compile("\\s*(bound_scar\\s*:)?\\s*(((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.))\\s*>\\s*=)?\\s*(\\p{Alpha}+\\p{Alnum}*)\\s*(>\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.)))?\\s*",Pattern.CASE_INSENSITIVE);
		Matcher upper_2 = pattern_2.matcher(inequality);
		//int, binary e semicont 
		Pattern pattern3 =   Pattern.compile("\\s*((bin)|(sec)|(int))\\s+((\\p{Alpha}+)(\\p{Alnum}*))\\s*",Pattern.CASE_INSENSITIVE);
		Matcher int_bin_sec = pattern3.matcher(inequality);

		if (upper_1.matches()) {
			scanUpper(inequality);
		}
		else if (upper_2.matches()) {
			scanUpper(inequality);
		}
		else if (int_bin_sec.lookingAt() ) {
			checkSintassiInt(inequality);
			scanIntSecBin(inequality);
		}
		else  if (matcher_group_var.lookingAt()  && inequality.matches("(.+)((<\\s*=)|(>\\s*=)|(=))\\s*(([+-]?)(\\d+)(\\.?)(\\d*))\\s*")) {
			checkSintassi(inequality);
			scanDisequestion(inequality);
		} 
		else {
			throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" ["+inequality+"]");
		}
	}
	
	private void scanDisequestion(String line) throws LPException {
		
		InternalConstraint internal=new InternalConstraint(dimension);
		String name;
		double aj=0,b;
		int index=0;
		for(int a=0;a<dimension;a++) Ai[a]=null;

		if (line.matches("\\s*(\\p{Alpha}+\\p{Alnum}*\\s*:)(.+)")) {
			String[] tokens = line.split(":");
			name=tokens[0].trim();
			//nome del vincolo
			internal.setName(name);
			line=tokens[1];
		} 

		if (line.matches("(.+)>\\s*=(.+)")) {
			//System.out.println("ge:"+line);
			internal.setType(TYPE_CONSTR.GE);
		} 
		else if (line.matches("(.+)<\\s*=(.+)")) {
			//System.out.println("le:"+line);
			internal.setType(TYPE_CONSTR.LE);
		} 
		else if (line.contains("=")) {
			//System.out.println("eq:"+line);
			internal.setType(TYPE_CONSTR.EQ);
		} 

		String[] disequation = line.split("[><]?\\s*=");
		b = Double.parseDouble(disequation[1].trim());
		internal.setBi(b);
		line= disequation[0];
		//System.out.println("line:"+line);

		Pattern pattern2 = Pattern.compile("(([+-]?)\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\p{Alnum}*)\\s*)",Pattern.CASE_INSENSITIVE);
		Matcher matcher2 = pattern2.matcher(line);
		
		while (matcher2.find()) {
			
			String segno_var=matcher2.group(2); 
			if(segno_var==null) segno_var="+";
			
			String number_var=matcher2.group(3); 
			if(number_var==null) number_var="1";
			aj=Double.parseDouble(segno_var+number_var);

			String nome_var=matcher2.group(4).toUpperCase(); 
			index=nomi_var.indexOf(nome_var);
			if(index==-1 ) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg2",nome_var));
			
			if(Ai[index]!=null) {
				aj=aj+Ai[index];
			}
			Ai[index]=aj;
		
			/*
			for(int a=0;a<=matcher2.groupCount();a++) {
				System.out.println("ZZZZ>>>>>" + matcher2.group(a) + "<-:"+a); 
			}
			*/
		}
		for(int a=0;a<Ai.length;a++) if(Ai[a]!=null) internal.setAij(a,Ai[a]);
		new_constraints.add(internal);
	}
	
	
	private void scanUpper(String line) throws LPException {

		line=  line.replaceAll("\\s*(?i)(bound_scar\\s*:)\\s*", "");
		boolean minore=false;
		Pattern pattern_2 = Pattern.compile("\\s*(((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.))\\s*(>|<)\\s*=)?\\s*(\\p{Alpha}+\\p{Alnum}*)\\s*((>|<)\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.)))?\\s*",Pattern.CASE_INSENSITIVE);                               
		Matcher upper = pattern_2.matcher(line);
		if (line.matches("(.+)<\\s*=(.+)")) minore=true; 
		
		if (upper.matches()) {
			/*
			for(int a=0;a<=upper.groupCount();a++) {
				System.out.println("xxxxxxx::" + upper.group(a) + "::"+a); 
			}
			*/
			String segno1=upper.group(4); 
			String numero1=upper.group(5); 
			String punto1=upper.group(6); 
			
			String segno2=upper.group(13);
			String numero2=upper.group(14);
			String punto2=upper.group(15); 
			
			if(numero1==null && punto1==null  && numero2==null && punto2==null) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg4")+"["+line+"]");

			String nome_var=upper.group(8).toUpperCase();
			int index=nomi_var.indexOf(nome_var);
			if(index==-1 ) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg5",nome_var,line));

			/*
			System.out.println("--NUMERO1>>>>>"+numero1);
			System.out.println("--NUMERO2>>>>>"+numero2);
			System.out.println("--SEGNO1>>>>>"+segno1);
			System.out.println("--SEGNO2>>>>>"+segno2);
			System.out.println("--var>>>>>"+nome_var);
			System.out.println("--PPP1>>>>>"+punto1);
			System.out.println("--PPP2>>>>>"+punto2);
			*/

			if(segno1==null) segno1="+"; 
			if(segno2==null) segno2="+"; 

			double a1=0,a2=0;
			if(punto1!=null) a1=Double.NaN;
			else if(numero1!=null) a1=Double.parseDouble(segno1+numero1);

			if(punto2!=null) a2=Double.NaN;
			else if(numero2!=null) a2=Double.parseDouble(segno2+numero2);

			if(minore) {
				if(numero1!=null || punto1!=null) {
					if(arraysProb.array_lower[index]==null) arraysProb.array_lower[index]=a1;
					else throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg6",nome_var));
				}

				if(numero2!=null || punto2!=null) {
					if(arraysProb.array_upper[index]==null) arraysProb.array_upper[index]=a2;
					else throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg7",nome_var));
				}
			}
			else {
				if(numero1!=null || punto1!=null) {
					if(arraysProb.array_upper[index]==null) arraysProb.array_upper[index]=a1;
					else throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg7",nome_var));
				}
				if(numero2!=null || punto2!=null)  {
					if(arraysProb.array_lower[index]==null) arraysProb.array_lower[index]=a2;
					else throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg6",nome_var));
				}
			}
		}
	}

	private void scanIntSecBin(String line) throws LPException { 

		if (line.toLowerCase().contains("int")) {
			//se ho trovato delle varaibil intere , memorizzo l'informazione nel caso mi trovi in ambito 
			//non MILP, altrimenti Exceptiuon
			arraysProb.isMilp=true;
			
			if(line.matches("\\s*(?i)(int)\\s*(?i)all\\s")) {
				for(int j=0;j<arraysProb.array_int.length;j++) {
					arraysProb.array_int[j]=1;
				}
				//System.out.println("ALL INT");
				return;
			}
			
			
			String line2=line.replaceAll("\\s*(?i)(int)\\s*", "").trim();
			//System.out.println("@@@@@@@@@@@@::"+line);
			String[] tokens = line2.split("\\s*,\\s*");
			if(tokens.length==0) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg8")+"["+line+"]");
			for(String none_var:tokens) {  
				int index=nomi_var.indexOf(none_var.toUpperCase());
				if(index==-1 ) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg2", none_var));
				arraysProb.array_int[index]=1;
			}		 
		} 
		else if (line.toLowerCase().contains("bin")) {
			arraysProb.isMilp=true;
			
			if(line.matches("\\s*(?i)(bin)\\s*(?i)all\\s")) {
				for(int j=0;j<arraysProb.array_int.length;j++) {
					arraysProb.array_bin[j]=1;
				}
				//System.out.println("ALL bin");
				return;
			}
			
			String line2=line.replaceAll("\\s*(?i)(bin)\\s*", "").trim();
			//System.out.println("@@@@@@@@@@@@::"+line);
			String[] tokens = line2.split("\\s*,\\s*");
			if(tokens.length==0) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg10")+"["+line+"]");
			for(String none_var:tokens) {  
				int index=nomi_var.indexOf(none_var.toUpperCase());
				if(index==-1 )  throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg2", none_var));
				arraysProb.array_bin[index]=1;
			}	
		} 
		else if (line.toLowerCase().contains("sec")) {
			arraysProb.isMilp=true;
			if(line.matches("\\s*(?i)(sec)\\s*(?i)all\\s")) {
				for(int j=0;j<arraysProb.array_int.length;j++) {
					arraysProb.array_sec[j]=1;
				}
				//System.out.println("ALL sec");
				return;
			}
			
			String line2=line.replaceAll("\\s*(?i)(sec)\\s*", "").trim();
			//System.out.println("@@@@@@@@@@@@::"+line);
			String[] tokens = line2.split("\\s*,\\s*");
			if(tokens.length==0) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg9")+"["+line+"]");
			for(String none_var:tokens) {  
				int index=nomi_var.indexOf(none_var.toUpperCase());
				if(index==-1 )  throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg2", none_var));
				arraysProb.array_sec[index]=1;
			}	
		} 
	}

	public ArrayProblem getArraysProb() {
		return arraysProb;
	}
	
	private void checkSintassi(String fo_string) throws  ParseException {
		//s.split("[><]?\\s*=");

		Pattern pattern =     Pattern.compile("\\s*(\\p{Alpha}+\\p{Alnum}*\\s*:)?\\s*[+-]?\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\p{Alnum}*)\\s*",Pattern.CASE_INSENSITIVE);
		Matcher matcher_group_var = pattern.matcher(fo_string);
		int end=0;
		//MAX o MIN
		if (matcher_group_var.lookingAt()) {
			end=matcher_group_var.end();
		}	
		else { 
			throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" ["+fo_string+"]");
		}
		String resto=fo_string.substring(end);
		
		String resto2=resto.trim();
		Pattern pattern2 = Pattern.compile("[+-]\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\p{Alnum}*)\\s*",Pattern.CASE_INSENSITIVE);
		Pattern pattern3 = Pattern.compile("((<\\s*=)|(>\\s*=)|(=))\\s*(([+-]?)(\\d+)(\\.?)(\\d*))\\s*",Pattern.CASE_INSENSITIVE);
		int end2=0;
		
		while(!resto2.equals(""))  {
			Matcher matcher2 = pattern2.matcher(resto2);
			Matcher matcher3 = pattern3.matcher(resto2);
			if (matcher2.lookingAt()) {
				end2=matcher2.end();
				resto2=resto2.substring(end2);
			}	
			else if(matcher3.matches())  {
				resto2="";
			}
			else { 
				throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" ["+resto2+"]");
			}
		}
	}
	
	
	private void checkSintassiInt(String fo_string) throws LPException, ParseException {

		Pattern pattern = Pattern.compile("\\s*((bin)|(sec)|(int))\\s+((\\p{Alpha}+)(\\p{Alnum}*))\\s*(,)?\\s*",Pattern.CASE_INSENSITIVE);
		Matcher matcher_group_var = pattern.matcher(fo_string);
		int end=0;
		//MAX o MIN
		if (matcher_group_var.lookingAt()) {
			end=matcher_group_var.end();
		}	
		else { 
			throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg4")+" ["+fo_string+"]");
		}
		String resto=fo_string.substring(end);
		resto=resto.trim();
		
		if(!resto.equals("")) {
			String[] tokens = resto.split("\\s*,\\s*");
			for(String token:tokens) {  
				if(!token.matches("\\s*(\\p{Alpha}+\\p{Alnum}*)\\s*"))
					throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg4")+" ["+token+"]");
			}	
		}
	}
}
