package it.ssc.parser;

public interface DateFormat {
	
	public enum DATE_FORMAT {
		DATA_OBJECT,
		GG_MM_AAAA, 
		GG_MM_AA, 
		MM_GG_AAAA, 
		MMGGAAAA, 
		MM_GG_AA, 
		MMGGAA, 
		GG_MMM_AAAA, 
		GGMMAAAA, 
		GGMMAA, 
		AAAAMMGG,
		HH_MM_SS
	};

}
