package it.ssc.parser;



import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.parser.exception.InvalidInformatStringException;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Scanner;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserDeclarationNewVarString  extends GenericParser {
	
	private static final Logger logger=SscLogger.getLogger();
	
	/** 
	 *  
	 * Effettuo la separazione dei token con la virgola, 
	 */
	
	protected void parseType(String type_to_parse,InputSubDichiarationVar single_input_step)
							 throws InvalidInformatStringException {
		this.is_dich_column=false;
		super.parseType(type_to_parse,single_input_step);
	}

	public void parser(String informat_string) throws InvalidInformatStringException {

		this.list_input_step=new ArrayList<InputSubDichiarationInterface>();
		
		//imposto come delimitatore la virgola per separare gli input step 
		
		Scanner scanner = new Scanner(informat_string).useDelimiter(",");
		
		while (scanner.hasNext()) {
			//prendo il valore  e verifico se e' relativo alla dichiarazione 
			//di una variabile o a un'istruzione @ o # 
			String input_step = scanner.next().trim();

			//pattern per la dichiarazione di variabili 
			Pattern pattern_dich_var = Pattern.compile("([Rr][Ee][Tt][Aa][Ii][Nn]\\s+)?(\\p{Alpha}\\w*\\s*:.+)");
			Matcher matcher_dich_var = pattern_dich_var.matcher(input_step);


			if (matcher_dich_var.matches()) {
				InputSubDichiarationVar single_input_dich_var=new InputSubDichiarationVar();
				if(matcher_dich_var.group(1)!=null) {
					single_input_dich_var.setVarAsRetain();
					input_step=matcher_dich_var.group(2);
				}
				
				
				Scanner sub_scanner = new Scanner(input_step).useDelimiter("\\s*:\\s*");
				String nome_variabile = sub_scanner.next().trim();
				if(!nome_variabile.matches("[A-Z_0-9]+"))  {
					logger.log(SscLevel.WARNING,"Il nome della variabile '"+nome_variabile +"' non e' in maiuscolo. ");
					logger.log(SscLevel.NOTE,"Per uniformita' tutte le variabili devono essere maiuscole: il nome verra' "+
							           "convertito in '"+nome_variabile.toUpperCase()+"'");
				}
				nome_variabile=nome_variabile.toUpperCase();
				if(existNameDichiarationVar(nome_variabile)) {
					throw new InvalidInformatStringException("Variabile "+nome_variabile+" gia dichiarata.");
				}
				single_input_dich_var.setNameVar(nome_variabile);
				String type_variabile = sub_scanner.next().trim();
				parseType(type_variabile,single_input_dich_var);
				this.list_input_step.add(single_input_dich_var);
				sub_scanner.close();
			}
			
			else  {
				throw new InvalidInformatStringException("ERRORE. Errato formato di input " +informat_string ); 
			}
		}
		scanner.close();
	}
	
	protected boolean parseTypeDate(String type_to_parse,InputSubDichiarationVar single_input_step)
			  throws InvalidInformatStringException {

		Pattern pattern = Pattern.compile("date\\s*((\\(\\s*(.+)\\s*\\))?)\\s*(((\\[\\s*(\\d+)\\s*\\-\\s*(\\d+)\\s*\\]))?)",
						  Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(type_to_parse);
		boolean sub_macher_date = true;
		if (matcher.matches()) {
			single_input_step.setTypeVar(GregorianCalendar.class);
			// imposto lunghezza della variabile string
			if (matcher.group(3) != null) {
				if (is_dich_column) {
					sub_macher_date = ParserDate.parser(matcher.group(3),single_input_step);
					if (!sub_macher_date)
						return false;
				} 
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare il formato di data "+ matcher.group(3));
			}
			// imposto ,se presente, range star e end colonna di lettura
			if (matcher.group(7) != null && matcher.group(8) != null) {
				if (is_dich_column) {
					int start_colonna = Integer.parseInt(matcher.group(7));
					int end_colonna = Integer.parseInt(matcher.group(8));
					single_input_step.setStartAndEndColumn(start_colonna,end_colonna);
				} 
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}
			return true;
		} 
		else {
			return false;
		}
	}
}
