package javax.constraints;

/*
 * Used while posing constraint that potentially could be violated conflicting with other constraints.
 * Constraints with a higher probabilities to succeed should prevail when conflicts occur.
 */

public enum Probability {
		NEVER(0),
		VERY_LOW(10),
		LOW(25),
		BELOW_MID(35),
		MID(50),
		ABOVE_MID(60),
		HIGH(75),
		VERY_HIGH(90),
		ALWAYS(100);
		 
		private int value;

	    private Probability(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	    	return value;
	    }
	    
	    public static Probability get(String id) {
	    	if (id == null)
	    		return NEVER;
	    	String pattern = id.replaceAll("[\\s+_]","");
	    	if (pattern.equalsIgnoreCase("VERYLOW"))
	    		return VERY_LOW;
	    	if (pattern.equalsIgnoreCase("LOW"))
	    		return LOW;
	    	if (pattern.equalsIgnoreCase("BELOWMID"))
	    		return BELOW_MID;
	    	if (pattern.equalsIgnoreCase("MID") || pattern.equalsIgnoreCase("Middle") || pattern.equalsIgnoreCase("Average"))
	    		return MID;
	    	if (pattern.equalsIgnoreCase("ABOVEMID"))
	    		return ABOVE_MID;
	    	if (pattern.equalsIgnoreCase("HIGH"))
	    		return HIGH;
	    	if (pattern.equalsIgnoreCase("VERYHIGH"))
	    		return VERY_HIGH;
	    
	    	return NEVER; 
	    }
	
}
