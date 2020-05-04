package it.ssc.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import it.ssc.pdv.PDVField;
import it.ssc.pdv.PDVKeep;
import it.ssc.step.exception.InvalidDichiarationOptions;


/**
 * Questa classe e' adibita alla scrittura dei metadati per le tabelle native. 
 * Questi metdati sono relativi a parecchie  le proprieta' presenti nel PDVKeep, 
 * nel quale vi è ad esempio anche il tipo sql. 
 * @author infoedge
 *
 */

public class CreateMetadataFMT {
	private MetaDataDatasetFMTSerializable meta;
	
	/**
	 * Costruttore nel caso di nuovo dataset 
	 */
	
	public CreateMetadataFMT() {
		meta=new MetaDataDatasetFMTSerializable();
	}
	
	/**
	 * Costruttore nel caso di append di dati su un dataset gia esistente con i 
	 * suoi metadati 
	 * @param meta
	 */
	
	public CreateMetadataFMT(MetaDataDatasetFMTSerializable meta) {
		this.meta=meta;
	}
	
	public void setField(PDVKeep pdv) throws InvalidDichiarationOptions {
		
		for(PDVField pdv_field:pdv.getListFieldKeep())  { 
			Field field=new Field(pdv_field.type, 
					              pdv_field.lentgh_field, 
					              pdv_field.getName(),
					              pdv_field.precision,
					              pdv_field.scale,
					              pdv_field.type_sql) ;
			meta.addField(field);
		}
	}
	
	public void setProperties(NameMetaParameters.NAME_META_PARAMETERS key, Object value) {
		meta.setProperties(key, value);
	}
	
	public void writeAndClose(File metafile) throws FileNotFoundException, IOException {
		ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(metafile));
		oos.writeObject(meta);
		oos.close();
		
	}
	
	public MetaDataDatasetFMTSerializable getMetaData() {
		return meta;
	}
}
