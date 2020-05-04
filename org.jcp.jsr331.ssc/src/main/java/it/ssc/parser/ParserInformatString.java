package it.ssc.parser;

import it.ssc.log.SscLevel;
import it.ssc.log.SscLogger;
import it.ssc.parser.exception.InvalidInformatStringException;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * I nomi dei campi possono iniziare solo con caratteri alfabetici e contenere solo
 * caratteri alfanumerici e "_" (undescore). 
 * <br><br>
 * 
 * Il formato di lettura e' costituito dal nome del campo e dal tipo.   
 * Tipo consentiti (byte, char , int, long, float, double, boolean , string(lunghezza) 
 * varstring , Date(informat input ). Le date saranno lette come da formato
 * ma memorizzate in un campo long che esprime in millisecondi l'intervallo di tempo 
 * dal 1970.  
 * <br><br>
 * 
 * Ogni valore sul file di input  e' separato da un separatore che sara' impostato nel ref
 * <br><br>
 * 
 *
 * Nei diversi formati (libero o a colonna) i valori missing (null sul dataset) sono 
 * caricati tramite l'opzione setMissingValue() e con il valore corrispondente sui dati
 * che poi viene appunto convertito sulk pdv in null. 
 * Si puo poi, nel caso in qui il campo sia di tipo string o char,  scegliere se convertire 
 * questo missing in una seguenza di spazi :  aggiungendo  {space}  si dichiara di mettere 
 * lo spazio al posto del null in fase  di scrittura, di default mette null.
 * <br><br>
 * E' possibile, se i dati caricati sono di tipo string o char , fare in moodo che se il 
 * carattere caricato e' una stringa vuota o costituita di soli spazi, di convertire 
 * questro "blank" in null. Aggiungendo {null} si converte la stringa costituita di 
 * caratteri di tipo spaziatore con null sul dataset di output. 
 * 
 * 
 * <br><br>
 * <strong>
 * Formato libero , esempio :
 * cognome: string(158), nome: string(256), eta: int ,sposato: boolean, giorno_matrimonio:
 * date(gg/mm/aa) </strong>
 * <br>
 * Il valore booleano puo essere rappresentato sul file con ("true","false") o ("f","t"), 
 * indipendentemente dall'essere maiuscolo o minuscolo. Nel caso di campo String , tra 
 * parentesi tonde,  si definisce la lunghezza massima del campo che verra scritto sul 
 * dataset di output, per cui se il record recuperato e' piu' lungo verra' troncato poco 
 * prima di essere salvato sul dataset, anche se nel passo di data mantiene la sua lunghezza. 
 * Nel caso di formato libero si puo impostare il separatore, ovvero il carattere o sequenza
 * di caratteri che verrra utilizzata per separare i dati in input. 
 * <br> <strong>Verificare con attenzione caratteri come \t etc.</strong> 
 * 
 * 
 * <br>
 * <br>
 * <strong>
 * Formato a colonna : 
 * cognome: string[1-13]{SPACE}, nome: string(5)[14-20], eta:int[21-30] ,sposato:boolean[34], 
 * giorno_matrimonio:date(gg/mm/aa)[35-45]
 * </strong>
 * <br>
 * I valori tra perentesi quadre rappresentano il range di colonne che viene letto. Nel caso 
 * che in una  colonna di tipo string non si trovi nessun valore ma uno spazio o al contrario 
 * il valore di missing value (.), verra caricato rispettivamente il valore di " " o null. 
 * Se si vuole cambiare impostazione nella memorizzazione dei null, occorre aggiungere l'istruzione 
 * di non valorizzazione dei null : {SPACE}; Se invece ci sono dei blank e si vuole metterli a null 
 * in fase di scrittura del dataset si imposta {NULL}. Questo solo per   stringhe e character
 * <br>
 * Nel caso del secondo string, il valore viene letto dalle colonne 14 alla 20 comprese, pero 
 * poi il valore viene memorizzato sul dataset in un campo di lunghezza 5, anche se durante il passo 
 * di data la sua lunghezza e' sempre di lunghezza 7.  
 * 
 * <br>
 * <br>
 * <strong>
 * Formato misto : 
 * cognome: string, nome: string[14-20], eta:int ,sposato:boolean[34], giorno_matrimonio:date(gg/mm/aa)[35-45] 
 *</strong> 
 *
 * Nel formato misto , dopo ogni campo letto, il cursore di lettura si pone immediatamente dopo il campo 
 * precedentemente letto, a quel punto il campo successivo viene letto a formato libero o no, a seconda del 
 * tipo di dichiarazione.  
 *  
 * <br>
 * <br> 
 *  
 *  
 * Il formato gestisce i salti record o salto campi o leggere lo stesso record logico presente su piu‘ righe :  
 *  <br>a) @@  fa leggere sullo stesso pdv i dati della righa successiva del file.  Naturalmente 
 *             non ha senso metterlo alla fine dell'istruzioni di importazione. In quanto non ci sarebbero piu' 
 *             campi da leggere.  
 *             @@ equivale a #1
 *  <br>b) @n  fa puntare il cursore alla colonna n sul riga fisica attualmente caricata dal file. 
 *            
 *  <br>c) #n  fa puntare alla n-esima successiva riga del file di input. Puo essere messo in mezzo alla istruzione 
 *             di importazione andando avanti nelle operazioni di lettura sullo stesso pdv; se invece messo alla 
 *             fine fa puntare alla n-esima riga successiva e poi inizia un altro record. Se metto ad esempio alla 
 *             fine dell'istruzione di importazione #1, punta alla riga successsiva, ma poi inizia il record successivo alla riga in quanto
 *             l'istruzione di input essendo terminata passa al record successivo.   
 *             Puo essere messo in mezzo o alla fine.   
 *  <br>d) ##  riservato 
 *  <br>e) @   blocco del record in caso di successive letture (condizionali)
 *  <br>f) #   riservato
 *  
 *  <br> E' opzionale combinare , in una seguenza successiva, solo una sola istruzione che inizia 
 *  per cancelletto, con una che inizia per chiocciola. Ad esempio : 
 *  <br>
 *  cognome: string, nome: string[14-20], @@, @34,  eta:int ,sposato:boolean
 *  <br>
 *  un istruzione del genere non ha effetto : <br>
 *  cognome: string, nome: string[14-20],#34,@@ ,  eta:int ,sposato:boolean<br>
 *  in quanto fa puntare il cursore alla colonna 34 , ma poi va alla righa successiva per continuare 
 *  a leggere lo stesso record su una nuova riga 
 *  
 *  
 *  <br><br>
 *   
 * 
 * @author Stefano Scarioli 
 * @version 1.0
 * @since version 1.0 
 */


public class ParserInformatString extends GenericParser {
	
	private static final Logger logger=SscLogger.getLogger();
   
	/**
	 * L'operazione di parser della stringa deve dare luogo a due tipi di informazioni.
	 * Una e' data dal tipo di campi letti, l'altra sono le azioni che deve eventualmente 
	 * effettuare nelle operazioni di lettura (direttive @ / # ). Simuliamo :
	 *  
	 * Effettuo la separazione dei token con la virgola, 
	 */

	public void parser(String informat_string) throws InvalidInformatStringException {

		this.list_input_step=new ArrayList<InputSubDichiarationInterface>();
		//imposto come delimitatore la virgola per separare gli input step 
		Scanner scanner = new Scanner(informat_string).useDelimiter(",");
		while (scanner.hasNext()) {
			//prendo il valore  e verifico se e' relativo alla dichiarazione 
			//di una variabile o a un'istruzione @ o # 
			String input_step = scanner.next().trim();

			//pattern per la dichiarazione di variabili 
			Pattern pattern_dich_var = Pattern.compile("(\\p{Alpha}\\w*\\s*):(.+)");
			Matcher matcher_dich_var = pattern_dich_var.matcher(input_step);

			//pattern per le istruzioni @ o # 
			Pattern pattern_azioni = Pattern.compile("(#{1,2}|@{1,2})|([@#]\\d+)");
			Matcher matcher_azioni = pattern_azioni.matcher(input_step);
			
			//pattern per i gruppi di variabili 
			Pattern pattern_group_var = Pattern.compile("((\\p{Alpha}+)(\\d+)\\s*\\-\\s*(\\p{Alpha}+)(\\d+)\\s*):(.+)"); 
			Matcher matcher_group_var = pattern_group_var.matcher(input_step);

			if (matcher_dich_var.matches()) {
				String nome_variabile =matcher_dich_var.group(1).trim();
				if(!nome_variabile.matches("[A-Z_0-9]+"))  {
					logger.log(SscLevel.WARNING,"Il nome della variabile '"+nome_variabile +"' non e' in maiuscolo.");
					logger.log(SscLevel.NOTE,"Per uniformita' tutte le variabili devono essere maiuscole: il nome verra'"+
							           " convertito in '"+nome_variabile.toUpperCase()+"'");
				}
				nome_variabile=nome_variabile.toUpperCase();
				if(existNameDichiarationVar(nome_variabile)) {
					throw new InvalidInformatStringException("Variabile "+nome_variabile+" gia dichiarata.");
				}
				InputSubDichiarationVar single_input_dich_var=new InputSubDichiarationVar();
				single_input_dich_var.setNameVar(nome_variabile);
				String type_variabile = matcher_dich_var.group(2).trim();
				parseType(type_variabile,single_input_dich_var);
				this.list_input_step.add(single_input_dich_var);
				
			}
			
			else if (matcher_group_var.matches()) {
				
				String nome_variabile_da =matcher_group_var.group(2).trim();
				String nome_variabile_a =matcher_group_var.group(4).trim();
				String num_da =matcher_group_var.group(3).trim();
				String num_a =matcher_group_var.group(5).trim();
				String type_variabile = matcher_group_var.group(6).trim();
				/*
				for(int a=0;a<=matcher_group_var.groupCount();a++) {
				    System.out.println("KKKKK>>>>>" + matcher_group_var.group(a) + "<-:"+a);
			    } */
				if(!nome_variabile_da.equals(nome_variabile_a)) {
					throw new InvalidInformatStringException("Range variabili da "+nome_variabile_da+num_da+" a "+nome_variabile_a+num_a+" devono avere lo stesso prefisso alfabetico.");
				}
				int num_var_da=Integer.parseInt(num_da); 
				int num_var_a=Integer.parseInt(num_a); 
				if(num_var_da >= num_var_a) {
					throw new InvalidInformatStringException("Range variabili da "+nome_variabile_da+num_da+" a "+nome_variabile_a+num_a+" deve partire da un numero intero minore ad un numero intero maggiore.");
				}
				
				if(!nome_variabile_da.matches("[A-Z_0-9]+"))  {
					logger.log(SscLevel.WARNING,"Il nome del range di variabili '"+nome_variabile_da +"' non e' in maiuscolo.");
					logger.log(SscLevel.NOTE,"Per uniformita' tutte le variabili devono essere maiuscole: il nome verra'"+
							           " convertito in '"+nome_variabile_da.toUpperCase()+"'");
				}
				nome_variabile_da=nome_variabile_da.toUpperCase();
				for(int a=num_var_da;a<=num_var_a;a++)  {
					String single_var_n=nome_variabile_da+a;
					if(existNameDichiarationVar(single_var_n)) {
						throw new InvalidInformatStringException("Variabile "+single_var_n+" gia dichiarata.");
					}
					
					InputSubDichiarationVar single_input_dich_var=new InputSubDichiarationVar();
					single_input_dich_var.setNameVar(single_var_n);
					parseType(type_variabile,single_input_dich_var);
					this.list_input_step.add(single_input_dich_var);
				}
			}
			
			else if (matcher_azioni.matches()) {
				InputSubDichiarationAction single_input_dic_act=new InputSubDichiarationAction();
				parseAction(input_step,single_input_dic_act);
				this.list_input_step.add(single_input_dic_act);
			}
			else  {
				throw new InvalidInformatStringException("ERRORE. Errato formato di input o uso di caratteri non consentiti" +informat_string ); 
			}
		}
		scanner.close();
	}
	
	private void parseAction(String string_to_parse, InputSubDichiarationAction single_input_step) 
	                throws  InvalidInformatStringException {
		
		Pattern pattern_azioni = Pattern.compile("((#{1,2})|(@{1,2}))|(([@#])(\\d+))");
		Matcher matcher_azioni = pattern_azioni.matcher(string_to_parse);

		if (matcher_azioni.matches()) {
			
			for(int a=0;a<=matcher_azioni.groupCount();a++) {
			    //System.out.println("KKKKK>>>>>" + matcher_azioni.group(a) + "<-:"+a);
		    }
			
			if(matcher_azioni.group(3)!=null) {
				single_input_step.setReadLineNext(true);
			}
			if(matcher_azioni.group(4)!=null) {
				if(matcher_azioni.group(5).equals("@"))  {
					int colonna = Integer.parseInt(matcher_azioni.group(6));
					if(colonna==0)  throw new InvalidInformatStringException("L'indice di colonna deve essere maggiore di zero");
					single_input_step.setPointColumn(colonna);
				}	
				else if(matcher_azioni.group(5).equals("#"))  {
					int row = Integer.parseInt(matcher_azioni.group(6));
					if(row==0)  throw new InvalidInformatStringException("L'indice di riga deve essere maggiore di zero");
					single_input_step.setPointRow(row);
				}
			}
		}
	}
	
}
