package it.ssc.parser;

import it.ssc.log.SscLogger;
import it.ssc.metadata.Field;
import it.ssc.metadata.FieldInterface;
import it.ssc.parser.InputSubDichiarationInterface.TYPE_INPUT_STEP;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.step.exception.InvalidDichiarationOptions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputDichiarationInfo implements  Cloneable {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private ArrayList<InputSubDichiarationInterface> list_input_step;
	private ArrayList<FieldInterface> fields;
	private ArrayList<InputSubDichiarationVar> list_dichiaration_var;

	
	public InputDichiarationInfo(ArrayList<InputSubDichiarationInterface> list_input_step) throws InvalidInformatStringException{
		this.list_input_step=list_input_step;
		fields=builField();
		list_dichiaration_var=builListDichiarationVar(); 
	}
	
	public void renameInputVarIn(String new_name,String old_name) throws InvalidDichiarationOptions, InvalidInformatStringException {
		boolean trovato=false;
		for(InputSubDichiarationInterface isdi: list_input_step) {
			if(isdi.getTypeInputStep()==TYPE_INPUT_STEP.DICHIARATION_VAR) {
				if(((InputSubDichiarationVar)isdi).getNameVar().equals(old_name)) {
					((InputSubDichiarationVar)isdi).setNameVar(new_name);
					trovato=true;
				}
			}
		}
		if(!trovato) throw new InvalidDichiarationOptions("La variabile da rinominare "+old_name +" non esiste");
		fields=builField();
		list_dichiaration_var=builListDichiarationVar(); 
	}

	public ArrayList<InputSubDichiarationInterface> getInputDichiarationInfo() {
		return list_input_step;
	}

	public InputSubDichiarationVar getDichiarationVar(int index_column) {
		return list_dichiaration_var.get(index_column-1);
	}
	
	public FieldInterface getField(int index_column) {
		return fields.get(index_column-1);
	}
	
	public String getColumnName(int index_column) {
		//return fields.get(index_column-1).getNameField();
		return list_dichiaration_var.get(index_column-1).getNameVar();
	}
	
	public int getColumnCount() {
		return list_dichiaration_var.size();
	}
	
	private ArrayList<FieldInterface>  builField()  throws InvalidInformatStringException {
		ArrayList<FieldInterface> list_field=new ArrayList<FieldInterface>();
		Iterator<InputSubDichiarationInterface> iter=  list_input_step.iterator();
		while(iter.hasNext()) {
			InputSubDichiarationInterface isdi=iter.next();
			if(isdi.getTypeInputStep()==InputSubDichiarationInterface.TYPE_INPUT_STEP.DICHIARATION_VAR) {
				InputSubDichiarationVar isdv=(InputSubDichiarationVar)isdi;
				list_field.add(new Field(isdv.getTypeVar(),						                 
										 isdv.getLengthVar(),
						                 isdv.getNameVar(),0,0,0)); //per ora non posso dichiarare da input 
				                                                  // quindi passo sempre precisione e scala a zero
			}
		}
		if(list_field.isEmpty()) throw new InvalidInformatStringException("ERRORE. Non sono stati dichiarati campi nel formato di input");
		return list_field;
	}
	
	
	private ArrayList<InputSubDichiarationVar>  builListDichiarationVar() throws InvalidInformatStringException   {
		ArrayList<InputSubDichiarationVar> dichiar_var=new ArrayList<InputSubDichiarationVar>();
		Iterator<InputSubDichiarationInterface> iter=  list_input_step.iterator();
		while(iter.hasNext()) {
			InputSubDichiarationInterface isdi=iter.next();
			if(isdi.getTypeInputStep()==InputSubDichiarationInterface.TYPE_INPUT_STEP.DICHIARATION_VAR) {
				dichiar_var.add((InputSubDichiarationVar)isdi);
			}
		}
		if(dichiar_var.isEmpty()) throw new InvalidInformatStringException("ERRORE. Non sono stati dichiarati campi nel formato di input");
		
		return dichiar_var;
	}
	
	public InputDichiarationInfo clone() {
		
		InputDichiarationInfo clone=null;
		try {
			clone=(InputDichiarationInfo)super.clone();
			clone.fields=(ArrayList<FieldInterface>) fields.clone();
			clone.list_dichiaration_var=(ArrayList<InputSubDichiarationVar>) list_dichiaration_var.clone();
			clone.list_input_step=(ArrayList<InputSubDichiarationInterface>) list_input_step.clone();
			
			
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE,"Clonazione InputDichiarationInfo",e);
		}
		return clone;
	}
}
