package it.ssc.metadata;

import java.util.ArrayList;
import it.ssc.metadata.exception.IncompatibleTypeFMT;
import it.ssc.pdv.PDVField;
import it.ssc.step.exception.InvalidDichiarationOptions;

public class UtilMetaData {
	
	
	//compara posizione, tipi e lunghezza
	//il nome no ! 
	public static void isCompatibleInputPDVComparedWithMetaOutput(MetaDataDatasetFMTInterface meta, ArrayList<PDVField<?>> list_field_pdv) throws InvalidDichiarationOptions, IncompatibleTypeFMT {
		
		
		ArrayList<FieldInterface> list_filed_meta=meta.getListField();
		int num_filed=list_field_pdv.size();
		int num_filed_meta=list_filed_meta.size();
		if(num_filed_meta!=num_filed)  	{
			throw new IncompatibleTypeFMT("ERRORE ! La sorgente e la destinazione hanno un numero di colonne differenti");
		}
		for(int a=0;a<num_filed;a++) {
			PDVField<?> f_pdv=list_field_pdv.get(a);
			FieldInterface f_meta=list_filed_meta.get(a);
			if(!(f_pdv.type==f_meta.getType() && f_pdv.lentgh_field==f_meta.getLenght()))	{
				String message="ERRORE ! la sorgente e la destinazione non sono omogenei : \n"+ 
				"campo "+f_pdv.getName() +" , lunghezza: " +f_pdv.lentgh_field + " type : "+f_pdv.type.getName() + "\n"+
				"campo "+f_meta.getName() + " , lunghezza: " +f_meta.getLenght() + " type : "+f_meta.getType().getName();
				 throw new IncompatibleTypeFMT(message);
			}
		}
	}
}
