package it.ssc.pl.milp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.ssc.i18n.RB;

/*
 * formato a disequazioni
 * 
 * min: 3x1 + var3 + peso + varn
 * row1: 3var3 - peso >= 4
 * coin: -x1 + 5varn <=6
 * rein: +peso =6
 * 
 * 4 <= peso <= 8
 * var 3 >= 6
 * varn <= 8
 * 
 * int : x1 , peso
 * bin: var3
 * semicont: varn 
 * 
 * 
 */

final class ScanLineFOFromString {
	
	private ArrayList<String> list_nomi_var;
	private ArrayList<Double> list_cj_var;
	private String target_fo;
	
	public ScanLineFOFromString(ArrayList<String> pl_problem) throws  ParseException {
		Iterator<String> iter=pl_problem.iterator();
		String fo_string="";
		while(iter.hasNext() && fo_string.equals("")) {
			fo_string=iter.next().trim();
			iter.remove();
		}
		list_nomi_var=new ArrayList<String>();
		list_cj_var=new ArrayList<Double>();
		parse(fo_string);
	}
	
	
	public ScanLineFOFromString(BufferedReader br) throws  IOException, ParseException {
		
		String fo_string="",line="";
		while(fo_string.equals("") && (line = br.readLine()) != null   ) {
			fo_string=line.trim();
		}
		list_nomi_var=new ArrayList<String>();
		list_cj_var=new ArrayList<Double>();
		parse(fo_string);
	}
	
	public LinearObjectiveFunction getFOFunction() throws LPException {
		double array[]=new double[list_cj_var.size()];
		int a=0;
		for(double cj:list_cj_var) array[a++]=cj;
		GoalType goal=GoalType.MIN;
		if(target_fo.equals("MAX")) goal=GoalType.MAX;
		return new LinearObjectiveFunction(array, goal);
	}
	
		
	private void parse(String fo_string) throws  ParseException {
		checkSintassi(fo_string);
		scanFoFromString(fo_string);
	}
	
	public ArrayList<String> getListNomiVar() {
		return list_nomi_var;
	}

	private void scanFoFromString(String fo_string)  {

		Pattern pattern = Pattern.compile("\\s*(min|max)\\s*:\\s*(([+-]?)\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\p{Alnum}*))\\s*",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(fo_string);
		int end=0;
		//MAX o MIN
		if (matcher.lookingAt()) {
			end=matcher.end();
			target_fo =matcher.group(1).toUpperCase(); //MAX o MIN
			String segno_prima_var=matcher.group(3); 
			if(segno_prima_var==null) segno_prima_var="+";

			String number_prima_var=matcher.group(4); 
			if(number_prima_var==null) number_prima_var="1";
			list_cj_var.add(Double.parseDouble(segno_prima_var+number_prima_var));

			String nome_prima_var=matcher.group(5).toUpperCase(); 
			//non verifico nulla, in quanto e' la prima variabile inserita 
			list_nomi_var.add(nome_prima_var);
			/*
			 for(int a=0;a<=matcher.groupCount();a++) {
				 System.out.println("KKKKK>>>>>" + matcher.group(a) + "<-:"+a); 
			 }
			 */
		}	
		//tolgo la parte gia elaborata
		String resto=fo_string.substring(end);

		Pattern pattern2 = Pattern.compile("(([+-])\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\p{Alnum}*)\\s*)",Pattern.CASE_INSENSITIVE);
		Matcher matcher2 = pattern2.matcher(resto);
		double cj;
		while (matcher2.find()) {

			String segno_var=matcher2.group(2); 
			if(segno_var==null) segno_var="+";

			String number_var=matcher2.group(3); 
			if(number_var==null) number_var="1";
			
			cj=Double.parseDouble(segno_var+number_var);
			
			String nome_var=matcher2.group(4).toUpperCase(); 
			//if(list_nomi_var.contains(nome_var)) throw new LPException(RB.format("it.ssc.pl.milp.ScanLineFOFromString.msg2",nome_var));
			if(list_nomi_var.contains(nome_var)) {
				int index=list_nomi_var.indexOf(nome_var);
				cj=cj+list_cj_var.get(index);
				list_cj_var.set(index, cj);
			}
			else {
				list_cj_var.add(Double.parseDouble(segno_var+number_var));
				list_nomi_var.add(nome_var);
			}		
			/*
			 for(int a=0;a<=matcher2.groupCount();a++) {
				 System.out.println("ZZZZ>>>>>" + matcher2.group(a) + "<-:"+a); 
			 }
			 */
		}	
	}
	

	private void checkSintassi(String fo_string) throws ParseException {

		Pattern pattern = Pattern.compile("\\s*(min|max)\\s*:\\s*([+-]?)\\s*((\\d+)(\\.)?(\\d*))?((\\p{Alpha}+)(\\p{Alnum}*))\\s*",Pattern.CASE_INSENSITIVE);
		Matcher matcher_group_var = pattern.matcher(fo_string);
		int end=0;
		//MAX o MIN
		if (matcher_group_var.lookingAt()) {
			end=matcher_group_var.end();
		}	
		else { 
			throw new ParseException(RB.getString("it.ssc.pl.milp.ScanLineFOFromString.msg1")+" ["+fo_string+"]");
		}
		String resto=fo_string.substring(end);
		String resto2=resto.trim();
		Pattern pattern2 = Pattern.compile("[+-]\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\p{Alnum}*)\\s*",Pattern.CASE_INSENSITIVE);
		
		int end2=0;
		while(!resto2.equals(""))  {
			Matcher matcher2 = pattern2.matcher(resto2);
			if (matcher2.lookingAt()) {
				end2=matcher2.end();
				resto2=resto2.substring(end2);
			}	
			else { 
				throw new ParseException(RB.getString("it.ssc.pl.milp.ScanLineFOFromString.msg3")+" ["+resto2+"]");
			}
		}
	}
}
