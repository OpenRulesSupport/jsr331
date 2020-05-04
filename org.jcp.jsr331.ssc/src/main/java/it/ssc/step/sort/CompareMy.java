package it.ssc.step.sort;

import java.util.GregorianCalendar;

public class CompareMy {
	
	public static int compare(Double value1, Double value2) {
		if (value1 != null && value2 != null) {
			return value1.compareTo(value2);
		} 
		else if (value1 == null) {
			if (value2 != null) {
				return 1;
			} 
			else {
				return 0;
			}
		} 
		else {
			return -1;
		}
	}
	
	//Non c'e' StringBufer perche e' gia convertito in Styring 
	
	public static int compare(String value1, String value2) {
		if (value1 != null && value2 != null) {
			return value1.compareTo(value2);
		} 
		else if (value1 == null) {
			if (value2 != null) {
				return 1;
			} 
			else {
				return 0;
			}
		} else {
			return -1;
		}
	}

	
	public static int compare(GregorianCalendar value1, GregorianCalendar value2) {
		if (value1 != null && value2 != null) {
			return value1.compareTo(value2);
		} 
		else if (value1 == null) {
			if (value2 != null) {
				return 1;
			} 
			else {
				return 0;
			}
		} 
		else {
			return -1;
		}
	}
	
	public static int compare(Integer value1, Integer value2) {
		if (value1 != null && value2 != null) {
			return value1.compareTo(value2);
		} 
		else if (value1 == null) {
			if (value2 != null) {
				return 1;
			} 
			else {
				return 0;
			}
		} 
		else {
			return -1;
		}
	}
	

	public static int compare(Short value1, Short value2) {
		if (value1 != null && value2 != null) {
			return value1.compareTo(value2);
		} 
		else if (value1 == null) {
			if (value2 != null) {
				return 1;
			} 
			else {
				return 0;
			}
		} 
		else {
			return -1;
		}
	}
	
	
	public static int compare(Long value1, Long value2) {
		if (value1 != null && value2 != null) {
			return value1.compareTo(value2);
		} 
		else if (value1 == null) {
			if (value2 != null) {
				return 1;
			} 
			else {
				return 0;
			}
		} 
		else {
			return -1;
		}
	}
	
	public static int compare(Boolean value1, Boolean value2) {
		if (value1 != null && value2 != null) {
			return value1.compareTo(value2);
		} 
		else if (value1 == null) {
			if (value2 != null) {
				return 1;
			} 
			else {
				return 0;
			}
		} 
		else {
			return -1;
		}
	}
	
	public static int compare(Float value1, Float value2) {
		if (value1 != null && value2 != null) {
			return value1.compareTo(value2);
		} 
		else if (value1 == null) {
			if (value2 != null) {
				return 1;
			} 
			else {
				return 0;
			}
		} 
		else {
			return -1;
		}
	}
	
	
	
	public static int compare(Byte value1, Byte value2) {
		if (value1 != null && value2 != null) {
			return value1.compareTo(value2);
		} 
		else if (value1 == null) {
			if (value2 != null) {
				return 1;
			} 
			else {
				return 0;
			}
		} 
		else {
			return -1;
		}
	}

	
	public static int compare(Character value1, Character value2) {
		if (value1 != null && value2 != null) {
			return value1.compareTo(value2);
		} 
		else if (value1 == null) {
			if (value2 != null) {
				return 1;
			} 
			else {
				return 0;
			}
		} 
		else {
			return -1;
		}
	}
	
	
}
