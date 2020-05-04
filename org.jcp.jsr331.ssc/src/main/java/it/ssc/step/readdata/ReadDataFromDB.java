package it.ssc.step.readdata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import it.ssc.log.SscLogger;
import it.ssc.metadata.FieldInterface;
import it.ssc.metadata.exception.TypeSqlNotSupported;
import it.ssc.parser.exception.InvalidDateFormatException;
import it.ssc.pdv.PDV;
import it.ssc.pdv.PDVAll;
import it.ssc.pdv.PDVField;
import it.ssc.ref.Input;
import it.ssc.ref.InputRefDBInterface;
import it.ssc.step.exception.ErrorStepInvocation;
import it.ssc.util.GregorianCalendarFormat;


class ReadDataFromDB implements ReadDataInterface {

	private static final Logger logger=SscLogger.getLogger();
	
	private InputRefDBInterface ref_db;
	private OptionsRead options_read;

	ReadDataFromDB(Input ref, OptionsRead options_read) throws Exception {
		this.ref_db = (InputRefDBInterface) ref;
		this.ref_db.executeQuery();
		this.options_read = options_read;
	}

	public SourceDataInterface getSourceData() throws SQLException, TypeSqlNotSupported  {
		return new SourceData(ref_db.getResultSet());
	}

	public PDV createPDV() throws InvalidDateFormatException {
		ArrayList<FieldInterface> list_field = ref_db.getListField();
		int num_var = list_field.size();
		PDV pdv = new PDV();

		for (int _a = 0; _a < num_var; _a++) {
			String name_var = list_field.get(_a).getName(); 
			Class<?> type_var = list_field.get(_a).getType();
			PDVField<?> pdv_field = pdv.addNewField(name_var, type_var);
			pdv_field.lentgh_field = list_field.get(_a).getLenght();
			pdv_field.precision=list_field.get(_a).getPrecision();
			pdv_field.scale=list_field.get(_a).getScale();
			pdv_field.type_sql=list_field.get(_a).getTypeSql();
		}
		return pdv;
	}

	private final class SourceData implements SourceDataInterface {
		private java.sql.ResultSet resultset;
		private String name_var ;
		private int num_var_read;
		private long obs_lette;
		private long max_obs_read;
		private boolean is_var_mappated=true;
		@SuppressWarnings("rawtypes")
		private PDVField pdv_filed_var;
		private boolean log_active;

		private SourceData(ResultSet resultset)  {
			this.resultset = resultset;
			this.obs_lette=0;
			this.log_active=true;
			this.max_obs_read=options_read.getMaxObsRead();
		}
		
		public void setLogActive(boolean active) {
			log_active=active;
		}

		@SuppressWarnings("unchecked")
		public boolean readFromSourceWriteIntoPDV(PDVAll pdv) throws Exception {
			
			if(!resultset.next() || (max_obs_read!=-1 && obs_lette >= max_obs_read) ) return  false;
			
			num_var_read=0;	
			for(FieldInterface field:ref_db.getListField())  {
				name_var=field.getName();
				num_var_read++;	
				
				pdv_filed_var=pdv.getField(name_var);
				is_var_mappated =pdv_filed_var.loadValueFromDB(resultset.getObject(num_var_read));
				
				if(!is_var_mappated)  {
					//System.out.println("Non sono riuscito a mappare il tipo sul campo "+name_var+"... Ci provo con chiamate dirette");
					
					if (pdv_filed_var.type == Double.class) {
						pdv_filed_var.value_generics=resultset.getDouble(num_var_read);
					}
					else if (pdv_filed_var.type == GregorianCalendar.class) {
						pdv_filed_var.value_generics= new GregorianCalendarFormat().setTimeInMillix(resultset.getTimestamp(num_var_read).getTime());
					}
					else if (pdv_filed_var.type == Integer.class) {
						pdv_filed_var.value_generics=resultset.getInt(num_var_read);
					}
					else if (pdv_filed_var.type == Short.class) {
						pdv_filed_var.value_generics=resultset.getShort(num_var_read);
					}
					else if (pdv_filed_var.type == Float.class) {
						pdv_filed_var.value_generics=resultset.getFloat(num_var_read);
					}
					else if (pdv_filed_var.type == Byte.class) {
						pdv_filed_var.value_generics=resultset.getByte(num_var_read);
					}
					else if (pdv_filed_var.type == Boolean.class) {
						pdv_filed_var.value_generics=resultset.getBoolean(num_var_read);
					}
					else if (pdv_filed_var.type == Long.class) {
						pdv_filed_var.value_generics=resultset.getLong(num_var_read);
					}
					else {
						throw new Exception("Errore !!! Il valore sul campo "+name_var +" sul record "+(obs_lette+1)+ "non e' stato possibile leggerlo");
					}	
				}
			} 
			obs_lette++;		
			return true;
		}
		
	
		public void readNullFromSourceWriteIntoPDV(PDVAll pdv) throws ErrorStepInvocation {
			throw new ErrorStepInvocation("Questo metodo non dovrebbe essere mai richiamato sourcedata.readNullFromSourceWriteIntoPDV(PDVAll pdv)");
		}
		
		public void close() throws  SQLException {
			if(log_active) logger.log(Level.INFO,"Numero di osservazioni lette dal DB tramite la query ("+ref_db.getSql()+") :"+obs_lette);
			if (resultset != null) {
				resultset.close();
				//ref_db.setResultSetToNull();
			}
		}
	}
}
