package it.ssc.step.trasformation;

import java.util.ArrayList;

import it.ssc.dynamic_source.CreateDynamicObject;
import it.ssc.dynamic_source.CreateDynamicSourceSort;
import it.ssc.dynamic_source.DynamicClassSortInterface;
import it.ssc.parser.ParserDeclarationSortVarString;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.pdv.PDVAll;
import it.ssc.step.sort.SortItem;
import it.ssc.step.sort.exception.SortException;

public final class TrasformationDataSort {
	
	private DynamicClassSortInterface obj_source;
	
	public TrasformationDataSort(PDVAll pdv, OptionsTrasformationSort opt_trasf,String path_compiler) throws Exception {
		
		String variables_to_sort=opt_trasf.getVariablesToSort(); 
		if(variables_to_sort==null)  {
			throw new SortException("ERRORE ! Dichiarare le variabili su cui fare il sort con il metodo setVariablesToSort()");
		}
		
		ParserDeclarationSortVarString parse_string_sort=new ParserDeclarationSortVarString();
		parse_string_sort.parser(variables_to_sort);
		ArrayList<SortItem> list_sort_item=parse_string_sort.getListSortItem();
		testExistNameDichiarationVar(pdv,list_sort_item); 
		
		CreateDynamicSourceSort dyn_source = new CreateDynamicSourceSort(pdv,list_sort_item);
		CreateDynamicObject create_obj = new CreateDynamicObject(dyn_source,path_compiler);
		obj_source = (DynamicClassSortInterface) create_obj.createObject();
	
	}

	public void inizializePDV(PDVAll pdv) throws Exception {
		//setNullValueVarNew(pdv);
		pdv.setRecordDeleted(false);
	}
	
	public DynamicClassSortInterface loadRecord(PDVAll pdv) throws Exception {
		return obj_source._loadRecord(pdv);
	}
	
	
	public void uploadRecord(DynamicClassSortInterface record_sort,PDVAll pdv) throws Exception {
		 obj_source._uploadRecord(record_sort,pdv);
	}
	

	private void testExistNameDichiarationVar(PDVAll pdv,ArrayList<SortItem> list_sort_item)
			throws InvalidInformatStringException {

		int nun_filed = pdv.getSize();
		for (SortItem srtitem : list_sort_item) {
			boolean trovata = false;
			for (int a = 0; a < nun_filed; a++) {
				String name_var = pdv.getField(a).getName();
				if (srtitem.getVarName().equals(name_var)) {
					trovata = true;
				}
			}
			if (!trovata) {
				throw new InvalidInformatStringException(
						"Variabile "+ srtitem.getVarName()
								+ " dichiarata nella sort non esiste nel dataset di input.");
			}
		}
	}
}
