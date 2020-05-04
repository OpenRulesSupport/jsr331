package it.ssc.step.writedata;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

public class OptionsWrite {
	private HashMap<String,String> hash_format;
	private HashMap<String,String> hash_label;
	private boolean  is_append_output;
	private String[] name_var_keep;
	private String[] name_var_drop;
	private Hashtable<String,String> rename_var;
	
	private String string_format_print;
	private String[] var_to_print;
	private String token_missing;
	
	private String header_printf_file;
	private Locale l;
	
	public OptionsWrite() {
		this.name_var_keep=new String[0];
		this.name_var_drop=new String[0];
		this.rename_var=new Hashtable<String,String> ();
	}
	
	public void setLocale(Locale l) {
		this.l=l;
	}
	public Locale getLocale() {
		return this.l;
	}
	
	public void setHeaderPrintfFile(String header) {
		this.header_printf_file=header;
	}
	
	public String  getHeaderPrintfFile() {
		return this.header_printf_file;
	}

	public void setFormat(String nome_campo, String nome_formato) {
		hash_format.put(nome_campo, nome_formato);
	}

	public void setLabel(String nome_campo, String nome_label) {
		hash_label.put(nome_campo, nome_label);
	}

	public void setAppendOutput(boolean append) {
		this.is_append_output = append;
	}

	public void setDropOutput(String[] name_var) {
		name_var_drop = name_var;
	}

	public void setKeepOutput(String[] name_var) {
		name_var_keep = name_var;
	}
	
	public String[] getDropOutput() {
		return name_var_drop ;
	}

	public String[] getKeepOutput() {
		return name_var_keep ;
	}
	
	public HashMap<String,String> getHashFormat() {
		return hash_format;
	}

	public HashMap<String,String> getHashLabel() {
		return hash_label;
	}

	public boolean isAppendOutput() {
		return is_append_output;
	}
	
	public void setPrintfOptions(String format_to_print, String... var ) {
		this.string_format_print=format_to_print;
		this.var_to_print=var;
	}

	public String getStringFormatPrintf() {
		return string_format_print;
	}

	public String[] getVarToPrint() {
		return var_to_print;
	}
	
	public void setOutputTokenMissing(String token_miss) {
		this.token_missing=token_miss;
	}
	
	public String  getOutputTokenMissing() {
		return this.token_missing;
	}
	
	public void renameVar(String current_name, String new_name) {
		this.rename_var.put(current_name, new_name);
	}
}
