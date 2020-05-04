package it.ssc.ref;


import it.ssc.context.Config;
import it.ssc.io.UtilFile;
import it.ssc.library.Library;
import it.ssc.metadata.CreateMetadataFMT;
import it.ssc.metadata.MetaDataDatasetFMTInterface;
import it.ssc.metadata.MetaDataDatasetFMTSerializable;
import it.ssc.metadata.NameMetaParameters;
import it.ssc.pdv.PDVKeep;

import java.io.File;


public class OutputRefFmt implements OutputRefInterface {

	private static final TYPE_REF type_ref = TYPE_REF.REF_FMT;
	private Library library;
	private String name_table;
	//public static final String ESTENSION_FILEK = Config.ESTENSION_FILE_FMT;
	//public static final String ESTENSION_METAK = Config.ESTENSION_META_FMT;
	//public static final String ESTENSION_COPY_TEMPK =  Config.ESTENSION_COPY_TEMP_FMT;

	OutputRefFmt(Library lib, String name_ds) {
		this.library = lib;
		this.name_table = name_ds;
	}

	public boolean isExistingDatasetComplete() throws Exception {
		return (getFile().exists() && getFileFMTMeta().exists());
	}

	public TYPE_REF getTypeRef() {
		return type_ref;
	}

	public File getFile() throws Exception {
		String name_path_with_sep = UtilFile.getPathDirWithSeparatorFinal(library.getAbsolutePath());
		String path_complete_ds = name_path_with_sep + name_table + Config.ESTENSION_FILE_FMT;
		return new File(path_complete_ds);
	}

	public File getFileFMTMeta() throws Exception {
		String name_path_with_sep = UtilFile.getPathDirWithSeparatorFinal(library.getAbsolutePath());
		String path_complete_ds = name_path_with_sep + name_table + Config.ESTENSION_META_FMT;
		return new File(path_complete_ds);
	}
	
	public String getNameTable() {
		return name_table;
	}
	
	public String getNameLibrary() {
		 return library.getName();
	}

	public String getNameComplete() {
		return library.getName() + "." + name_table;
	}
	
	/**
	 * 
	 * 
	 * @param pdv
	 * @param obs
	 * @throws Exception
	 */

	public void writeMetaData(PDVKeep pdv, Long obs) throws Exception {
		CreateMetadataFMT w_meta = new CreateMetadataFMT();
		w_meta.setField(pdv);
		w_meta.setProperties(NameMetaParameters.NAME_META_PARAMETERS.NOBS_LONG,	obs);
		w_meta.writeAndClose(this.getFileFMTMeta());
	}

	/**
	 * Salva i meta dati Questo metodo viene richiamato nel caso in cui il dataset esiste  gia' e 
	 * i dati vanno semplicemente in append. In questo caso i metadati passati sono 
	 * quelli gia esistenti del dataset in cui si va in append
	 * 
	 * @param meta
	 * @param obs
	 * @param h
	 * @throws Exception
	 */
	public void writeMetaData(MetaDataDatasetFMTInterface meta, Long obs)	throws Exception {
		CreateMetadataFMT w_meta = new CreateMetadataFMT((MetaDataDatasetFMTSerializable) meta);
		w_meta.setProperties(NameMetaParameters.NAME_META_PARAMETERS.NOBS_LONG,	obs);
		w_meta.writeAndClose(this.getFileFMTMeta());
	}

	public Library getLibrary() {
		return this.library;
	}
}
