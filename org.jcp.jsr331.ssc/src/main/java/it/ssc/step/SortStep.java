package it.ssc.step;

import it.ssc.ref.Input;
import it.ssc.step.exception.InvalidDichiarationOptions;
import it.ssc.step.parallel.Parallelizable;

public interface SortStep extends Parallelizable { 
	
	public void execute() throws Exception;

	public void setDropVarOutput(String... name_field);

	public void setKeepVarOutput(String... name_field);

	public void setMaxObsRead(long obs_read) throws InvalidDichiarationOptions;
	
	public Input getDataRefCreated();
	
	public void setVariablesToSort(String variables);
	
	public void setMaxNumberRecordLoadInMemoryForSort(int max);

	
}
