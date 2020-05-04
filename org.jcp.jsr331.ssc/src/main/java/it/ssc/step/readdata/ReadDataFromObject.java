package it.ssc.step.readdata;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import it.ssc.log.SscLogger;
import it.ssc.parser.InputDichiarationInfo;
import it.ssc.parser.InputSubDichiarationInterface;
import it.ssc.parser.InputSubDichiarationVar;
import it.ssc.parser.exception.InvalidDateFormatException;
import it.ssc.pdv.PDV;
import it.ssc.pdv.PDVAll;
import it.ssc.pdv.PDVField;
import it.ssc.ref.Input;
import it.ssc.ref.InputRows;
import it.ssc.step.exception.ErrorStepInvocation;
import it.ssc.step.exception.InvalidObjectException;

/**
 * Leggo una stringa di input, la suddivido in token Mantengo sempre la stringa
 * originaria. Vedo se il primo campo e a lettura libera o no. Se si prendo il
 * primo token, altrimenti faccio substring della stringa originaria. Leggo il
 * valore e lo memorizzo nel PDV.
 * 
 * @author Stefano Scarioli
 * 
 */

class ReadDataFromObject implements ReadDataInterface {

	private static final Logger logger = SscLogger.getLogger();
	private InputRows ref_string;
	private InputDichiarationInfo info_input_step;
	private OptionsRead options_read;

	/* RICORDARSI DELLA GESTIONE DEI MISSING */

	ReadDataFromObject(Input ref, OptionsRead options_read) {
		/*
		 * Posso tranquillamente fare il cast perche ho controllato a monte,
		 * nella classe read data, che e' di tipo InputObject
		 */
		this.ref_string = ((InputRows) ref);
		this.info_input_step = ref_string.getInputDichiarationInfo();
		this.options_read = options_read;
	}

	public PDV createPDV() throws InvalidDateFormatException {

		int num_var = info_input_step.getColumnCount();
		PDV pdv = new PDV();
		pdv.setTokenMissing(this.ref_string.getMissingValue());
		for (int _a = 0; _a < num_var; _a++) {
			String name_var = info_input_step.getDichiarationVar(_a + 1).getNameVar();
			Class<?> type_var = info_input_step.getDichiarationVar(_a + 1).getTypeVar();
			PDVField<?> pdv_field = pdv.addNewField(name_var, type_var);
			pdv_field.lentgh_field = info_input_step.getDichiarationVar(_a + 1).getLengthVar();
			// System.out.println("LLL:"+pdv_field.lentgh_field );
			pdv_field.date_format_input = info_input_step.getDichiarationVar(_a + 1).getFormatDate();
			pdv_field.manager_missing_value = info_input_step.getDichiarationVar(_a + 1).getSettingMissing();
			pdv_field.type_sql = info_input_step.getDichiarationVar(_a + 1).getTypeSql();
		}
		return pdv;
	}

	public SourceDataInterface getSourceData() throws IOException {

		return new SourceData(ref_string.getListObject());
	}

	private final class SourceData implements SourceDataInterface {

		private ArrayList<InputSubDichiarationInterface> list_input_step;
		private ArrayList<Object[]> list_object;
		private Object[] objects_memorized;

		private int obs_lette;
		private long max_obs_read;
		private Iterator<InputSubDichiarationInterface> iter_step;
		private int index_array_pdv;

		public SourceData(ArrayList<Object[]> list_obejct) throws IOException {
			this.list_object = list_obejct;
			this.obs_lette = 0;
			this.list_input_step = info_input_step.getInputDichiarationInfo();
			this.max_obs_read = options_read.getMaxObsRead();
		}

		public void setLogActive(boolean active) {
			
		}

		public boolean readFromSourceWriteIntoPDV(PDVAll pdv) throws Exception {

			String name_var = null;
			iter_step = list_input_step.iterator();
			objects_memorized = null;
			index_array_pdv = 0;

			if ((max_obs_read != -1 && obs_lette >= max_obs_read) || obs_lette >= list_object.size())
				return false;

			try {
				while (iter_step.hasNext()) {
					InputSubDichiarationInterface isi = iter_step.next();
					if (isi.getTypeInputStep() == InputSubDichiarationInterface.TYPE_INPUT_STEP.DICHIARATION_VAR) {
						InputSubDichiarationVar isd = (InputSubDichiarationVar) isi;
						name_var = isd.getNameVar();

						if (objects_memorized == null) {
							objects_memorized = list_object.get(obs_lette);
						}

						try {
							pdv.getField(name_var).loadValueFromObject(objects_memorized[index_array_pdv]);
						} 
						catch (java.lang.NumberFormatException | InvalidDateFormatException | InvalidObjectException nfe) {
							logger.log(Level.WARNING, "All'osservazione " + this.obs_lette + " " + nfe.getMessage()
									+ ". Il valore della variabile " + name_var + " e' stato settato a null");
							pdv.getField(name_var).is_null = true;
						}

						index_array_pdv++;
					}
				}
			} 
			catch(java.lang.ArrayIndexOutOfBoundsException e ) {
				logger.log(Level.SEVERE, "Interrotta la lettura della Stringa di dati di input");
				logger.log(Level.SEVERE, "Numero di oggetti passati inferiore a quelli dichiarati");
				throw e;
			}
			
			catch (Exception e) {
				logger.log(Level.SEVERE, "Interrotta la lettura della Stringa di dati di input");
				throw e;
			}
			obs_lette++;
			return true;
		}

		public void readNullFromSourceWriteIntoPDV(PDVAll pdv) throws ErrorStepInvocation {
			throw new ErrorStepInvocation(
					"Questo metodo non dovrebbe essere mai richiamato sourcedata.readNullFromSourceWriteIntoPDV(PDVAll pdv)");
		}

		public void close() throws IOException {

		}
	}
}
