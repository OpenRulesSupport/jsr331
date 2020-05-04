package it.ssc.step.trasformation;

public class OptionsTrasformationSort {
	private String variables_to_order;
	private int dimension_array_for_sort=80000; 
	
	public void setVariablesToSort(String variables_to_order) {
		this.variables_to_order=variables_to_order;
	}
	
	public String getVariablesToSort() {
		return this.variables_to_order;
	}

	public int getDimensionArrayForSort() { 
		return dimension_array_for_sort;
	}

	public void setDimensionArrayForSort(int dimension_array_for_sort) {
		this.dimension_array_for_sort = dimension_array_for_sort;
	}	
}
