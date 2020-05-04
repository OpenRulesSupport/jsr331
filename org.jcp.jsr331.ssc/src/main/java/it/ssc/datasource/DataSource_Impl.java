package it.ssc.datasource;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.function.Function;

import it.ssc.context.SessionIPRIV;
import it.ssc.pdv.PDV;
import it.ssc.pdv.PDVField;
import it.ssc.ref.Input;
import it.ssc.step.exception.InvalidDichiarationOptions;
import it.ssc.step.readdata.OptionsRead;
import it.ssc.step.readdata.ReadData;
import it.ssc.step.readdata.SourceDataInterface;
import it.ssc.step.trasformation.OptionsTrasformation;
import it.ssc.step.trasformation.TrasformationData;


public class DataSource_Impl implements DataSource {
	
	
	private OptionsRead opt_read;
	private OptionsTrasformation opt_trasf;
	private SessionIPRIV parent_session;
	private SourceDataInterface source;
	private PDV pdv;
	private PDVField<?> pdv_field;
	private boolean has_next;
	TrasformationData trasf_data;
	
	
	public DataSource_Impl(Input input_ref,	SessionIPRIV parent_session) throws Exception {
		this.parent_session = parent_session;
		this.parent_session.generateExceptionOfSessionClose(); 
		this.opt_read = new OptionsRead();
		this.opt_trasf = new OptionsTrasformation();
	
		
		ReadData read_data = new ReadData(input_ref, opt_read);
		//Crea il pdv 
		this.pdv = read_data.createPDV();
		//trasforma e aggiunge nel pdv variabili di tipo declare
		//Deve controllare che non siano gia presenti nel PDV 
		this.trasf_data = new TrasformationData(pdv,opt_trasf,parent_session.getPathCompiler());
		this.source = read_data.getSourceData();
	}
	
	public int getNumColunm() {
		return pdv.getSize();
	}
	
	public String getNameColunm(int column_index) {
		return pdv.getField(column_index).getName();
	}
		
		
	public boolean next() throws Exception  {
		this.has_next=source.readFromSourceWriteIntoPDV(this.pdv);
		if(this.has_next) { 
			trasf_data.inizializePDV(this.pdv);
			trasf_data.trasformPDV(this.pdv);   
		}	
		return this.has_next;
	}

	public void close() throws Exception   {
		if (source != null) source.close();
	}
	
	public Object getObject(String name) throws DataSourceException {
		pdv_field=pdv.getField(name);
		if(pdv_field==null)  throw new DataSourceException("ERRORE ! La variabile "+name +" non esiste nel DataSource");
		if(pdv_field.is_null)  {
			return null;
		}
		else if(pdv_field.type==StringBuffer.class)  {
			return pdv_field.value_generics.toString();
		}
		else {
			return pdv_field.value_generics;
		}
	}
	
	
	public ArrayList<String> getListNameVar() throws InvalidDichiarationOptions {
		 ArrayList<PDVField<?>> list=pdv.getListFieldKeep();
		 ArrayList<String> list_var=new  ArrayList<String>();
		 for(PDVField<?> field: list) {
			 list_var.add(field.getName());
		 }
		 return list_var;
	}
	
	
	public Object getObject(int column_index)  {
		pdv_field=pdv.getField(column_index);
		if(pdv_field.is_null)  {
			return null;
		}
		else if(pdv_field.type==StringBuffer.class)  {
			return pdv_field.value_generics.toString();
		}
		else {
			return pdv_field.value_generics;
		}
	}
	
	public  <I,R> R getValueReduce( Function<I,R> remapping,String name) throws DataSourceException {
		@SuppressWarnings("unchecked")
		I value=(I)getObject(name);
		return remapping.apply(value);
	}
	
	public Integer getInteger(String name) throws DataSourceException {
		return (Integer)getObject(name);
	}
	
	public Integer getInteger(int column_index) {
		return (Integer)getObject(column_index);
	}
	
	public Double getDouble(String name) throws DataSourceException {
		return (Double)getObject(name);
	}
	
	public Double getDouble(int column_index) {
		return (Double)getObject(column_index);
	}
	
	public Float getFloat(String name) throws DataSourceException {
		return (Float)getObject(name);
	}
	
	public Float getFloat(int column_index) {
		return (Float)getObject(column_index);
	}
	
	public Byte getByte(String name) throws DataSourceException {
		return (Byte)getObject(name);
	}
	
	public Byte getByte(int column_index) {
		return (Byte)getObject(column_index);
	}
	
	public Long getLong(String name) throws DataSourceException {
		return (Long)getObject(name);
	}
	
	public Long getLong(int column_index) {
		return (Long)getObject(column_index);
	}
	
	public Boolean getBoolean(String name) throws DataSourceException {
		return (Boolean)getObject(name);
	}
	
	public Boolean getBoolean(int column_index) {
		return (Boolean)getObject(column_index);
	}

	public String getString(String name) throws DataSourceException {
		return (String)getObject(name);
	}
	
	public String getString(int column_index) {
		return (String)getObject(column_index);
	}
	
	public GregorianCalendar getGregorianCalendar(String name) throws DataSourceException {
		return (GregorianCalendar)getObject(name);
	}
	
	public GregorianCalendar getGregorianCalendar(int column_index) {
		return (GregorianCalendar)getObject(column_index);
	}
	
	public Character getCharacter(String name) throws DataSourceException {
		return (Character)getObject(name);
	}
	
	public Character getCharacter(int column_index) {
		return (Character)getObject(column_index);
	}
	
	public Short getShort(String name) throws DataSourceException {
		return (Short)getObject(name);
	}
	
	public Short getShort(int column_index) {
		return (Short)getObject(column_index);
	}
}
