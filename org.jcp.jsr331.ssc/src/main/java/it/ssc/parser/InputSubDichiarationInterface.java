package it.ssc.parser;

public interface InputSubDichiarationInterface {
	
	public TYPE_INPUT_STEP getTypeInputStep();
	public enum TYPE_INPUT_STEP {DICHIARATION_VAR, DICHIARATION_ACTION};

}
