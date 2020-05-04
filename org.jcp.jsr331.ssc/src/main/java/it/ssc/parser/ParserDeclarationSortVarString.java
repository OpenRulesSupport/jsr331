package it.ssc.parser;

import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.step.sort.SortItem;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserDeclarationSortVarString {
	private ArrayList<SortItem> sort_item;
	
	public void parser(String informat_sort_string) throws InvalidInformatStringException {

		this.sort_item=new ArrayList<SortItem>();
		//imposto come delimitatore la virgola per separare gli input step 
		Scanner scanner = new Scanner(informat_sort_string).useDelimiter(",");
		while (scanner.hasNext()) {
			//prendo il valore  e verifico se e' relativo alla dichiarazione 
			//di una variabile o a un'istruzione @ o # 
			String input_step = scanner.next().trim();
			//pattern per la dichiarazione di variabili 
			Pattern pattern_dich_var = Pattern.compile("(\\p{Alpha}\\w*)(\\s+(([Aa][Ss][Cc])|([Dd][Ee][Ss][Cc])))?");
			Matcher matcher_dich_var = pattern_dich_var.matcher(input_step);

			if (matcher_dich_var.matches()) {
				SortItem sorter_item=new SortItem();
				/*
				for(int a=0;a<=matcher_dich_var.groupCount();a++) {
				    System.out.println("KKKKK>>>>>" + matcher_dich_var.group(a) + "<-:"+a);
			    }
			    */
				String var_name=matcher_dich_var.group(1);
				var_name=var_name.toUpperCase();
				sorter_item.setVarName(var_name);
				if(matcher_dich_var.group(5)!=null) {
					sorter_item.setIsAsc(false);
				}
				sort_item.add(sorter_item);
			}
			
			else  {
				throw new InvalidInformatStringException("ERRORE. Errato formato per il sorting del dataset " +informat_sort_string ); 
			}
		}
		scanner.close();
	}
	public ArrayList<SortItem> getListSortItem() {
		return  this.sort_item;
	}

}
