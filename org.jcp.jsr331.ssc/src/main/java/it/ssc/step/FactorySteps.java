package it.ssc.step;

import it.ssc.ref.Input;


public interface FactorySteps {
	
	/*
	public DataSource createDataSource(Input input_reference) throws Exception; 
	
	public DataSource createDataSource(String name_input_dataset) throws Exception; 
	*/
	
	public DataStep createDataStep(String new_dataset, String name_input_dataset) throws Exception; 
	
	public DataStep createDataStep(String new_dataset, Input input_reference) throws Exception; 
	
	public FileStep createFileStep(String path_file, String lib_dot_idataset) throws Exception ;

	public FileStep createFileStep(String path_file,Input input_reference) throws Exception ;
	
	public CrossJoinStep createCrossJoinStep(String new_dataset)  throws Exception;
	
	public OuterJoinStep createOuterJoinStep(String new_dataset)  throws Exception;
	
	public DataStep createMemoryStep(String lib_dot_idataset) throws Exception ;
	
	public DataStep createMemoryStep(Input input_reference) throws Exception ;
	
	public SortStep createSortStep(String new_dataset, String name_input_dataset) throws Exception; 
	
	public SortStep createSortStep(String new_dataset, Input input_reference) throws Exception; 

}
