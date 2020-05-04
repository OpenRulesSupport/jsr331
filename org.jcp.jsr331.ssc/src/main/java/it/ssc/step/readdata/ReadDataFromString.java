package it.ssc.step.readdata;

import it.ssc.i18n.RB;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import it.ssc.context.Config;
import it.ssc.log.SscLogger;
import it.ssc.parser.InputDichiarationInfo;
import it.ssc.parser.InputSubDichiarationAction;
import it.ssc.parser.InputSubDichiarationInterface;
import it.ssc.parser.InputSubDichiarationVar;
import it.ssc.parser.exception.InvalidDateFormatException;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.pdv.PDV;
import it.ssc.pdv.PDVAll;
import it.ssc.pdv.PDVField;
import it.ssc.ref.Input;
import it.ssc.ref.InputString;
import it.ssc.step.exception.ErrorStepInvocation;

/**
 * Leggo una stringa di input, la suddivido in token Mantengo sempre la stringa
 * originaria. Vedo se il primo campo e a lettura libera o no. Se si prendo il
 * primo token, altrimenti faccio substring della stringa originaria. Leggo il
 * valore e lo memorizzo nel PDV.
 * 
 * @author Stefano Scarioli
 * 
 */

class ReadDataFromString implements ReadDataInterface {
	
	private static final Logger logger=SscLogger.getLogger(); 
	private InputString ref_string;
	private InputDichiarationInfo info_input_step;
	private OptionsRead options_read;

	/* RICORDARSI DELLA GESTIONE DEI MISSING */

	ReadDataFromString(Input ref, OptionsRead options_read) throws InvalidInformatStringException {
		/*
		 * Posso tranquillamente fare il cast perche ho controllato a monte,
		 * nella classe read data, che e' di tipo InputString
		 */
		this.ref_string = ((InputString)ref);
		this.info_input_step = ref_string.getInputDichiarationInfo();
		if(this.info_input_step==null) {
			throw new InvalidInformatStringException("Dopo aver creato un oggetto InputString  va richiamato il metodo setInputFormat(String format)  ");
		}
		this.options_read=options_read;
	}

	public PDV createPDV() throws InvalidDateFormatException  {

		int num_var = info_input_step.getColumnCount();
		PDV pdv = new PDV();
	    pdv.setTokenMissing(this.ref_string.getMissingValue());
		for (int _a = 0; _a < num_var; _a++) {
			String name_var = info_input_step.getDichiarationVar(_a + 1).getNameVar();
			Class<?> type_var=info_input_step.getDichiarationVar(_a + 1).getTypeVar();
			PDVField<?> pdv_field = pdv.addNewField(name_var,type_var);
			pdv_field.lentgh_field = info_input_step.getDichiarationVar(_a + 1).getLengthVar();
			//System.out.println("LLL:"+pdv_field.lentgh_field );
			pdv_field.date_format_input = info_input_step.getDichiarationVar(_a + 1).getFormatDate();
			pdv_field.manager_missing_value = info_input_step.getDichiarationVar(_a + 1).getSettingMissing();
			pdv_field.type_sql=info_input_step.getDichiarationVar(_a + 1).getTypeSql();
		}
		return pdv;
	}

	public SourceDataInterface getSourceData() throws IOException {
		return new SourceData(ref_string.getStringReader());
	}

	private final class SourceData implements SourceDataInterface {

		private ArrayList<InputSubDichiarationInterface> list_input_step; 
		private BufferedReader buff;
		private String line_memorized;
		private String substring_start_end;
		private Pattern separator = ref_string.getSeparator();
		private long obs_lette;
		private long max_obs_read;
		private Iterator<InputSubDichiarationInterface> iter_step;
		private int star_column , end_column, point_row = 0;
		private Scanner scanner_str;
		private boolean log_active;

		public SourceData(StringReader string_reader) throws IOException {
			this.buff = new BufferedReader(string_reader);
			this.obs_lette=0;
			this.log_active=true;
			this.list_input_step = info_input_step.getInputDichiarationInfo();
			this.max_obs_read=options_read.getMaxObsRead();
		}

		public void setLogActive(boolean active) {
			log_active=active;
		}
		
		public boolean readFromSourceWriteIntoPDV(PDVAll pdv) throws Exception {

			star_column = 0;
			end_column = 0;
			String name_var = null;
			iter_step = list_input_step.iterator();
			scanner_str = null;
			line_memorized = null;
			
			//commentato il 23/09/2012 poiche trasferito in trsformationData
			//pdv.setRecordDeleted(false);
			
			if(max_obs_read!=-1 && obs_lette >= max_obs_read) return false;
			
			try {
				while (iter_step.hasNext()) {
					InputSubDichiarationInterface isi = iter_step.next();
					if (isi.getTypeInputStep() == InputSubDichiarationInterface.TYPE_INPUT_STEP.DICHIARATION_VAR) {
						InputSubDichiarationVar isd = (InputSubDichiarationVar) isi;
						name_var = isd.getNameVar();
						if (isd.isColumnFormat()) {
							star_column = isd.getStartColumn();
							end_column = isd.getEndColumn();
							if (line_memorized == null) {
								if ((line_memorized = buff.readLine()) == null) {
									return false;
								}
							}
							
							try {
								substring_start_end = line_memorized.substring(star_column - 1, end_column);
								pdv.getField(name_var).loadValue(substring_start_end);
							} 
							
							catch(java.lang.StringIndexOutOfBoundsException sbo) 	{
								logger.log(Level.WARNING,"Indice della stringa fuori dalla lunhezza del record.+ " +Config.NL+
										   " All'osservazione "+(this.obs_lette+1)+" " +sbo.getMessage()+
								           ". Il valore della variabile "+name_var+" e' stata settato a null");
								pdv.getField(name_var).is_null=true;
								
							}				
							
							catch(java.lang.NumberFormatException  | InvalidDateFormatException nfe) 	{
								logger.log(Level.WARNING,"All'osservazione "+(this.obs_lette+1)+" " +nfe.getMessage()+
										           ". Il valore della variabile "+name_var+" e' stata settato a null");
								pdv.getField(name_var).is_null=true;
							}
						}
						// Tipo di formato di input libero
						else {
							if (line_memorized == null) {
								if ((line_memorized = buff.readLine()) == null) {
									return false;
								}
							}
							if (end_column != 0) {
								if (scanner_str != null) scanner_str.close();
								scanner_str = new Scanner(line_memorized.substring(end_column)).useDelimiter(separator);
								end_column = 0;
							} 
							else if (scanner_str == null) {
								scanner_str = new Scanner(line_memorized).useDelimiter(separator);
							}
							try {
								pdv.getField(name_var).loadValueFromScanner(scanner_str);
							}	
							catch(java.lang.NumberFormatException | InvalidDateFormatException nfe) 	{
								logger.log(Level.WARNING,"All'osservazione "+(this.obs_lette+1)+" " +nfe.getMessage()+
						           				   ". Il valore della variabile "+name_var+" e' stato settato a null");
								pdv.getField(name_var).is_null=true;
							}
						}
					} 
					
					//se di tipo InputSubDichiarationInterface.TYPE_INPUT_STEP.DICHIARATION_ACTION
					else {
						end_column = 0;
						if (scanner_str != null) scanner_str.close();
						scanner_str = null;
						InputSubDichiarationAction ise = (InputSubDichiarationAction) isi;
						if( ise.isReadLineNext())  {
							if ((line_memorized = buff.readLine()) == null) {
								return false;
							}
						}
						else if ((point_row = ise.getPointRow()) > 0) {
							
							while (point_row > 0) {
								point_row--;
								if ((line_memorized = buff.readLine()) == null) {
									return false;
								}
							}
						} 
						else if ((end_column = (ise.getPointColumn()-1)) >= 0) ;
					}
				}
				if (scanner_str != null) scanner_str.close();
			}
			catch (Exception e) {
				logger.log(Level.SEVERE,"Interrotta la lettura della Stringa di dati di input");
				throw e;
			}
			obs_lette++;
			return true; 
		}
		
		public void readNullFromSourceWriteIntoPDV(PDVAll pdv) throws ErrorStepInvocation {
			throw new ErrorStepInvocation("Questo metodo non dovrebbe essere mai richiamato sourcedata.readNullFromSourceWriteIntoPDV(PDVAll pdv)");
		}
		
		
		public void close() throws IOException {
			if(log_active) logger.log(Level.INFO,RB.getString("it.ssc.step.readdata.ReadDataFromString.msg1")+obs_lette);
			if (buff != null) {
				buff.close();
			}
		}
	}
}
