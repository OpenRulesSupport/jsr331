package it.ssc.step.sort;

public class SortItem {
	private String var_name;
	private boolean is_asc=true;
	
	public String getVarName() {
		return var_name;
	}
	public void setVarName(String var_name) {
		this.var_name = var_name;
	}
	public boolean isAsc() {
		return is_asc;
	}
	public void setIsAsc(boolean is_asc) {
		this.is_asc = is_asc;
	}
}
