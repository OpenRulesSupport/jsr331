package it.ssc.ref;


import it.ssc.context.Config;
import it.ssc.io.UtilFile;
import it.ssc.library.Library;
import it.ssc.library.exception.DatasetNotFoundException;
import it.ssc.metadata.FieldInterface;
import it.ssc.metadata.MetaDataDatasetFMTInterface;
import it.ssc.metadata.NameMetaParameters;
import it.ssc.metadata.ReadMetaDataFMT;
import it.ssc.step.exception.InvalidDichiarationOptions;

import java.io.File;
import java.util.ArrayList;

public class InputRefFmt implements Input {

	private static final TYPE_REF type_ref = TYPE_REF.REF_SSC;
	
	private Library library;
	private String name_table; 
	private String ESTENSION_FILE=Config.ESTENSION_FILE_FMT;
	private String ESTENSION_META=Config.ESTENSION_META_FMT;
	private MetaDataDatasetFMTInterface meta;
	
	public InputRefFmt(Library lib, String name_ds) throws Exception {
		this.library=lib;
		this.name_table=name_ds;
		if(!lib.existTable(name_ds)) throw new  DatasetNotFoundException("ERRORE ! "+lib.getName()+"."+name_ds+" dataset non trovato");
		this.readMetaData();
	}
	
	public TYPE_REF getTypeRef() {
		return type_ref;
	}
	
	public void renameVarToLoad(String new_name,String old_name) throws InvalidDichiarationOptions {
		meta.renameInputVarIn(new_name, old_name);
	}
	 
	public File getFile() throws Exception {
		String name_path_with_sep = UtilFile.getPathDirWithSeparatorFinal(library.getAbsolutePath());
		String path_complete_ds = name_path_with_sep + name_table + ESTENSION_FILE;
		return new File(path_complete_ds);
	}

	private File getFileFMTMeta() throws Exception {
		String name_path_with_sep = UtilFile.getPathDirWithSeparatorFinal(library.getAbsolutePath());
		String path_complete_ds = name_path_with_sep + name_table + ESTENSION_META;
		return new File(path_complete_ds);
	}

	void readMetaData() throws Exception {
		meta = ReadMetaDataFMT.readAndClose(getFileFMTMeta());
	}



	public java.sql.ResultSet getResultSet() {
		return null;
	}
	
	public ArrayList<FieldInterface> getListField() {
		return meta.getListField();
	}

	public long getNumObsDs() {
		Long num_records=(Long)meta.getMapProperties().get(NameMetaParameters.NAME_META_PARAMETERS.NOBS_LONG);
		return num_records;
	}

	public int getColumnCount() {
		return  meta.getListField().size();
	}

	public String getColumnName(int index_column) {
		return meta.getListField().get(index_column-1).getName();
	}

	public FieldInterface getField(int index_column) {
		return meta.getListField().get(index_column-1);
	}
	
	public String getNameTable() {
		return name_table;
	}
	public String getNameLibrary() {
		 return library.getName();
	}
	
	
	
	//non ha effetti, in quanto non carica nulla in memoria 
	public void close() {
		
	}

}