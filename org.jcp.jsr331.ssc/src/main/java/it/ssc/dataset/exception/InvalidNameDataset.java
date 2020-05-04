package it.ssc.dataset.exception;

import  it.ssc.i18n.RB;
public class InvalidNameDataset extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidNameDataset(String name_ds) {
		super(RB.getString("it.ssc.dataset.exception.InvalidNameDataset.msg1")+name_ds);
	}
}
