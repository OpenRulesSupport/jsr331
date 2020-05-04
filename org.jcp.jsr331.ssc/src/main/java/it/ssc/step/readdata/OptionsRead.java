package it.ssc.step.readdata;

import it.ssc.step.exception.InvalidDichiarationOptions;

public class OptionsRead {
	private long max_obs_read=-1;
	 
	public void setMaxObsRead(long max_obs_read) throws InvalidDichiarationOptions {
		if(max_obs_read <=0) throw new InvalidDichiarationOptions("ERRORE ! Il numero massimo di osservazioni "+
				                                                  "da leggere deve essere un numero positivo. obs="+max_obs_read);
		this.max_obs_read=max_obs_read;
	}
	
	public long getMaxObsRead() {
		return this.max_obs_read;
	}
}
