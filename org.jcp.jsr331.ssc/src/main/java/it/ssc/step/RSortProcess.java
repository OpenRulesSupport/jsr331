package it.ssc.step;

import it.ssc.context.SessionIPRIV;
import it.ssc.ref.Input;
import it.ssc.ref.OutputRefInterface;
import it.ssc.step.exception.InvalidDichiarationOptions;
import it.ssc.step.readdata.OptionsRead;
import it.ssc.step.trasformation.OptionsTrasformationSort;
import it.ssc.step.writedata.OptionsWrite;

class RSortProcess extends CoreDataSortStep implements SortStep {
	
	RSortProcess(OutputRefInterface new_dataset_output,	Input input_ref, SessionIPRIV parent_session) {
		this.output_ref = new_dataset_output;
		this.parent_session = parent_session;
		this.input_ref = input_ref;
		this.opt_read = new OptionsRead();
		this.opt_trasf = new OptionsTrasformationSort();
		this.opt_write = new OptionsWrite();
	}
	
	public void setDropVarOutput(String... name_field) {
		opt_write.setDropOutput(name_field);
	}

	public void setKeepVarOutput(String... name_field) {
		opt_write.setKeepOutput(name_field);
	}

	public void setMaxObsRead(long obs_read) throws InvalidDichiarationOptions {
		opt_read.setMaxObsRead(obs_read);
	}

	public void setVariablesToSort(String variables_to_order) {
		this.opt_trasf.setVariablesToSort(variables_to_order);
	}

	public void setMaxNumberRecordLoadInMemoryForSort(int max_dim_array) {
		this.opt_trasf.setDimensionArrayForSort(max_dim_array);
	}
	
	public void run() throws Exception {
		this.execute();
	}

}
