package it.ssc.ref;


public interface OutputRefInterface {
	
	public enum TYPE_REF {REF_FILE, REF_DB, REF_FMT, REF_FMT_MEMORY}; 
	
	public TYPE_REF getTypeRef();

}
