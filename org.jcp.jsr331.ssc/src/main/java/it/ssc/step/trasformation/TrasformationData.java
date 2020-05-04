package it.ssc.step.trasformation;

import java.util.logging.Level;
import java.util.logging.Logger;
import it.ssc.context.Config;

import it.ssc.dynamic_source.CreateDymamicSource;
import it.ssc.dynamic_source.CreateDynamicObject;
import it.ssc.dynamic_source.DynamicClassInterface;
import it.ssc.log.SscLogger;
import it.ssc.parser.InputDichiarationInfo;
import it.ssc.parser.InputSubDichiarationInterface;
import it.ssc.parser.InputSubDichiarationVar;
import it.ssc.parser.ParserDeclarationNewVarString;
import it.ssc.parser.InputSubDichiarationInterface.TYPE_INPUT_STEP;
import it.ssc.parser.exception.InvalidDateFormatException;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.pdv.PDVAll;
import it.ssc.pdv.PDVField;

public final class TrasformationData {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private DynamicClassInterface obj_source;
	private InputDichiarationInfo info_input_step;
	private BeanEquiJoin bean_equi;

	public TrasformationData(PDVAll pdv, OptionsTrasformation opt_trasf, String path_compiler) throws Exception {
		String source_user = opt_trasf.getSourceUserCode().trim();
		String where_condition=opt_trasf.getWhereCondition().trim();
		String declare_new_var=opt_trasf.getDeclareNewVar();
		String declare_java_attribute=opt_trasf.getDeclareJavaAttribute();
		
		bean_equi=opt_trasf.getBeanJoin();
		if(bean_equi!= null)  {
			//se i test non vanno bene generano eccezione 
			bean_equi.testExisteVars(pdv);
			bean_equi.isComparable(pdv);
		}
			
		if (declare_new_var!=null) { 
			ParserDeclarationNewVarString parse_string=new ParserDeclarationNewVarString();
			parse_string.parser(declare_new_var);
			info_input_step = parse_string.createInputDichiarationInfo(); 
			testExistNameDichiarationVar(pdv);
			refreshPDVaddNewVar(pdv);
		}
		
		if (bean_equi!= null || !source_user.equals("") || !where_condition.equals("") ) {
			CreateDymamicSource dyn_source = new CreateDymamicSource(pdv,source_user,where_condition,declare_java_attribute);
			if (bean_equi!= null ) { 
				dyn_source.createSourceEquiJoin(bean_equi.getNameVar1(),bean_equi.getNameVar2());
			}
			CreateDynamicObject create_obj=new CreateDynamicObject(dyn_source,path_compiler);
			obj_source=(DynamicClassInterface)create_obj.createObject();
			obj_source._setParameterStep(opt_trasf.getParameterStep());
		}
		
		
		//Mettere il codice che verifica se l'associazione nome_var - formato e'
		//palusibile, si verifica insomma che la variabile esista. I formati possono
		//anche non esistere, o meglio non essere caricati in memoria. 
		//lo stesso per nome var - label.  
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void refreshPDVaddNewVar(PDVAll pdv) throws InvalidDateFormatException {
		
		int num_var = info_input_step.getColumnCount();
		for (int _a = 0; _a < num_var; _a++) {
			String name_var = info_input_step.getDichiarationVar(_a + 1).getNameVar();
			Class type_var=info_input_step.getDichiarationVar(_a + 1).getTypeVar();
			PDVField pdv_field = pdv.addNewField(name_var,type_var);
			pdv_field.lentgh_field = info_input_step.getDichiarationVar(_a + 1).getLengthVar();
			pdv_field.date_format_input = info_input_step.getDichiarationVar(_a + 1).getFormatDate();
			pdv_field.manager_missing_value = info_input_step.getDichiarationVar(_a + 1).getSettingMissing();
			pdv_field.type_sql=info_input_step.getDichiarationVar(_a + 1).getTypeSql();
			pdv_field.is_retain=info_input_step.getDichiarationVar(_a + 1).isRetain();
		}
	}
	
	public void inizializePDV(PDVAll pdv) throws Exception {
		setNullValueVarNew(pdv);
		pdv.setRecordDeleted(false);
	}
	
	
	public void trasformPDV(PDVAll pdv) throws Exception {
		//non esegue equi join
		trasformPDV(pdv,false);
	}
	
	public void trasformPDV(PDVAll pdv,boolean exec_equi_join) throws Exception {
		if(obj_source!=null) {
			obj_source._setPDV(pdv);
			try {
				obj_source._recallSourceFromUser(pdv,exec_equi_join);
			}
			catch(NullPointerException npe)  {
				logger.log(Level.SEVERE,"Presenza di operazioni su variabili che assumono valori null."+Config.NL
						   +" La gestione di tale casistica e' a carico dello sviluppatore");
				throw npe;
			}
		}
	}
	
	public boolean isEquiJoin() {
		if(obj_source!=null && bean_equi!= null ) {
			return obj_source._isEquiJoin();
		}
		return false;
	}
	
	public Object getReturnObject() { 
		if(obj_source!=null) {
			return obj_source._getReturnObject();
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	private void setNullValueVarNew(PDVAll pdv) throws InvalidDateFormatException {
		if(info_input_step!=null)  {
			int num_var = info_input_step.getColumnCount();
			for (int _a = 0; _a < num_var; _a++) {
				String name_var = info_input_step.getDichiarationVar(_a + 1).getNameVar();
				
				if(!info_input_step.getDichiarationVar(_a + 1).isRetain()) {
					pdv.getField(name_var).is_null=true;
					pdv.getField(name_var).loadInitValue();
				}	
			}
		}
	} 
	
	private void testExistNameDichiarationVar(PDVAll pdv) throws InvalidInformatStringException {
		int nun_filed = pdv.getSize();
		for (int a = 0; a < nun_filed; a++) {
			String name_var = pdv.getField(a).getName();
			for (InputSubDichiarationInterface isdi : info_input_step.getInputDichiarationInfo()) {
				if (isdi.getTypeInputStep() == TYPE_INPUT_STEP.DICHIARATION_VAR) {
					InputSubDichiarationVar isdv = (InputSubDichiarationVar) isdi;
					if (isdv.getNameVar().equals(name_var)) {
						throw new InvalidInformatStringException("Variabile "+name_var+" gia dichiarata.");
					}
				}
			}
		}
	}
}
