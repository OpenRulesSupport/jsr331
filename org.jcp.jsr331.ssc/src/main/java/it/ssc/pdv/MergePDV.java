package it.ssc.pdv;

import it.ssc.parser.exception.InvalidDateFormatException;
import it.ssc.pdv.PDVField;

public class MergePDV {
	
	public static PDV createPDVMerge(PDV...  array_pdv) throws InvalidDateFormatException {
		
		PDV pdv_new = new PDV();
		
		for (PDV pdv : array_pdv) {

			int num = pdv.getSize();
			for (int _a=0;_a<num;_a++) {
				PDVField pdv_field_old=pdv.getField(_a);
				PDVField pdv_field_new = pdv_new.addNewField(pdv_field_old.getName(), pdv_field_old.type);
				pdv_field_new.lentgh_field = pdv_field_old.lentgh_field;
				pdv_field_new.precision = pdv_field_old.precision;
				pdv_field_new.scale = pdv_field_old.scale ;
				pdv_field_new.type_sql =pdv_field_old.type_sql;
			}
		}
		return pdv_new;
	}

}
