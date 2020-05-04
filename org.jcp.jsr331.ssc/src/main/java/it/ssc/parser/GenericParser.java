package it.ssc.parser;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.ssc.metadata.TypeSSC;
import it.ssc.parser.InputSubDichiarationInterface.TYPE_INPUT_STEP;
import it.ssc.parser.InputSubDichiarationVar.SETTING_MISSING;
import it.ssc.parser.exception.InvalidInformatStringException;

abstract class GenericParser {

	protected ArrayList<InputSubDichiarationInterface> list_input_step;
	protected boolean is_dich_column=true;

	protected boolean existNameDichiarationVar(String name_var) {
		for (InputSubDichiarationInterface isdi : list_input_step) {
			if (isdi.getTypeInputStep() == TYPE_INPUT_STEP.DICHIARATION_VAR) {
				InputSubDichiarationVar isdv = (InputSubDichiarationVar) isdi;
				if (isdv.getNameVar().equals(name_var)) {
					return true;
				}
			}
		}
		return false;
	}

	protected void parseType(String type_to_parse,InputSubDichiarationVar single_input_step) 
			                 throws InvalidInformatStringException {
		
		if (parseTypeString(type_to_parse, single_input_step))
			;
		else if (parseTypeVarString(type_to_parse, single_input_step))
			;
		else if (parseTypeChar(type_to_parse, single_input_step))
			;
		else if (parseTypeByte(type_to_parse, single_input_step))
			;
		else if (parseTypeInt(type_to_parse, single_input_step))
			;
		else if (parseTypeShort(type_to_parse, single_input_step))
			;
		else if (parseTypeLong(type_to_parse, single_input_step))
			;
		else if (parseTypeFloat(type_to_parse, single_input_step))
			;
		else if (parseTypeDouble(type_to_parse, single_input_step))
			;
		else if (parseTypeBoolean(type_to_parse, single_input_step))
			;
		else if (parseTypeDate(type_to_parse, single_input_step))
			;
		else {
			throw new InvalidInformatStringException("ERRORE. Dichiarazione di tipo variabile non corretta: "+ type_to_parse);
		}
	}

	public InputDichiarationInfo createInputDichiarationInfo() 	throws InvalidInformatStringException {
		return new InputDichiarationInfo(this.list_input_step);
	}

	protected boolean parseTypeChar(String type_to_parse,InputSubDichiarationVar single_input_step)
			throws InvalidInformatStringException {

		Pattern pattern = Pattern.compile("singlechar\\s*(((\\[\\s*(\\d+)\\s*\\]))?)\\s*((\\{((SPACE)|(NULL))\\})?)",
						  Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(type_to_parse);
		if (matcher.matches()) {
			single_input_step.setTypeVar(Character.class);
			single_input_step.setLengthVar(1);
			// imposto ,se presente, colonna singola di lettura
			if ( matcher.group(4) != null) {
				if (is_dich_column) {
					int colonna = Integer.parseInt(matcher.group(4));
					single_input_step.setStartAndEndColumn(colonna, colonna);
				}
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}

			if (matcher.group(8) != null) {
				single_input_step.setSettingMissing(SETTING_MISSING.MISSING_SPACE);
			} 
			else if (matcher.group(9) != null) {
				single_input_step.setSettingMissing(SETTING_MISSING.MISSING_NULL);
			}
			return true;
		} 
		else {
			return false;
		}
	}

	protected boolean parseTypeString(String type_to_parse,	InputSubDichiarationVar single_input_step)
			throws InvalidInformatStringException {
		/* ? stava qui */
		Pattern pattern = Pattern.compile("fixstring\\s*((\\(\\s*(\\d+)\\s*\\))?)\\s*(((\\[\\s*(\\d+)\\s*\\])|(\\[\\s*(\\d+)\\s*\\-\\s*(\\d+)\\s*\\]))?)\\s*((\\{((SPACE)|(NULL))\\})?)",
						  Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(type_to_parse);
		if (matcher.matches()) {
			single_input_step.setTypeVar(StringBuffer.class);
			// imposto lunghezza della variabile string
			if (matcher.group(3) != null) {
				int lunghezza = Integer.parseInt(matcher.group(3));
				if (lunghezza > TypeSSC.DEFAULT_LENGTH_STRING_CHAR) {
					throw new InvalidInformatStringException("il tipo stringfix puo essere al massimo lungo "
									+ TypeSSC.DEFAULT_LENGTH_STRING_CHAR
									+ " caratteri");
				} 
				else if (lunghezza ==0) {
					throw new InvalidInformatStringException("Il tipo stringfix dichiarato deve avere una lunghezza > 0 ");
				} 
				else {
					single_input_step.setLengthVar(lunghezza);
				}
			} 
			else {
				throw new InvalidInformatStringException("il tipo stringfix deve avere dichiarata la lunghezza : stringfix(lunghezza)");
			}

			// imposto ,se presente, colonna singola di lettura
			if (matcher.group(7) != null) {
				if(is_dich_column) {
					int colonna = Integer.parseInt(matcher.group(7));
					single_input_step.setStartAndEndColumn(colonna, colonna);
				}
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}
			// imposto ,se presente, range star e end colonna di lettura
			else if (matcher.group(9) != null && matcher.group(10) != null) {
				if(is_dich_column) {
					int start_colonna = Integer.parseInt(matcher.group(9));
					int end_colonna = Integer.parseInt(matcher.group(10));
					single_input_step.setStartAndEndColumn(start_colonna,end_colonna);
				}
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}

			if (matcher.group(14) != null) {
				single_input_step.setSettingMissing(SETTING_MISSING.MISSING_SPACE);
			} 
			else if (matcher.group(15) != null) {
				single_input_step.setSettingMissing(SETTING_MISSING.MISSING_NULL);
			}
			return true;
		} 
		else {
			return false;
		}
	}

	protected boolean parseTypeVarString(String type_to_parse,InputSubDichiarationVar single_input_step)
			throws InvalidInformatStringException {
		/* ? stava qui */
		Pattern pattern = Pattern.compile("varstring\\s*((\\(\\s*(\\d+)\\s*\\))?)\\s*(((\\[\\s*(\\d+)\\s*\\])|(\\[\\s*(\\d+)\\s*\\-\\s*(\\d+)\\s*\\]))?)\\s*((\\{((SPACE)|(NULL))\\})?)",
						  Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(type_to_parse);
		if (matcher.matches()) {
			single_input_step.setTypeVar(String.class);
			// imposto lunghezza della variabile string
			if (matcher.group(3) != null) {
				int lunghezza = Integer.parseInt(matcher.group(3));
				if (lunghezza > TypeSSC.DEFAULT_LENGTH_VARSTRING_CHAR) {
					throw new InvalidInformatStringException("il tipo string puo essere al massimo lungo "
									+ TypeSSC.DEFAULT_LENGTH_VARSTRING_CHAR
									+ " caratteri");
				} 
				else if (lunghezza ==0) {
					throw new InvalidInformatStringException("Il tipo string dichiarato deve avere una lunghezza > 0 ");
				} 
				else {
					single_input_step.setLengthVar(lunghezza);
				}
			} 
			else {
				throw new InvalidInformatStringException("il tipo string deve avere dichiarata la lunghezza : string(lunghezza)");
			}
			// imposto ,se presente, colonna singola di lettura
			if (matcher.group(7) != null) {
				if(is_dich_column) {
					int colonna = Integer.parseInt(matcher.group(7));
					single_input_step.setStartAndEndColumn(colonna, colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}
			// imposto ,se presente, range star e end colonna di lettura
			else if (matcher.group(9) != null && matcher.group(10) != null) {
				if(is_dich_column) {
					int start_colonna = Integer.parseInt(matcher.group(9));
					int end_colonna = Integer.parseInt(matcher.group(10));
					single_input_step.setStartAndEndColumn(start_colonna,end_colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}

			if (matcher.group(14) != null) {
				single_input_step.setSettingMissing(SETTING_MISSING.MISSING_SPACE);
			} 
			else if (matcher.group(15) != null) {
				single_input_step.setSettingMissing(SETTING_MISSING.MISSING_NULL);
			}
			return true;
		} 
		else {
			return false;
		}
	}

	protected boolean parseTypeByte(String type_to_parse,InputSubDichiarationVar single_input_step)
			throws InvalidInformatStringException {

		Pattern pattern = Pattern.compile("byte\\s*(((\\[\\s*(\\d+)\\s*\\])|(\\[\\s*(\\d+)\\s*\\-\\s*(\\d+)\\s*\\]))?)",
						  Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(type_to_parse);
		if (matcher.matches()) {
			single_input_step.setTypeVar(Byte.class);
			
			// imposto ,se presente, colonna singola di lettura
			if (matcher.group(4) != null) {
				if(is_dich_column) {
					int colonna = Integer.parseInt(matcher.group(4));
					single_input_step.setStartAndEndColumn(colonna, colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}
			// imposto ,se presente, range star e end colonna di lettura
			else if (matcher.group(6) != null && matcher.group(7) != null) {
				if(is_dich_column) {
					int start_colonna = Integer.parseInt(matcher.group(6));
					int end_colonna = Integer.parseInt(matcher.group(7));
					single_input_step.setStartAndEndColumn(start_colonna,end_colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}

			return true;
		} 
		else {
			return false;
		}
	}

	protected boolean parseTypeInt(String type_to_parse,InputSubDichiarationVar single_input_step)
			throws InvalidInformatStringException {

		Pattern pattern = Pattern.compile("int\\s*(((\\[\\s*(\\d+)\\s*\\])|(\\[\\s*(\\d+)\\s*\\-\\s*(\\d+)\\s*\\]))?)",
						  Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(type_to_parse);
		if (matcher.matches()) {
			single_input_step.setTypeVar(Integer.class);
			// imposto ,se presente, colonna singola di lettura
			if (matcher.group(4) != null) {
				if(is_dich_column) {
					int colonna = Integer.parseInt(matcher.group(4));
					single_input_step.setStartAndEndColumn(colonna, colonna);
				} 
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}
			// imposto ,se presente, range star e end colonna di lettura
			else if (matcher.group(6) != null && matcher.group(7) != null) {
				if(is_dich_column) {
					int start_colonna = Integer.parseInt(matcher.group(6));
					int end_colonna = Integer.parseInt(matcher.group(7));
					single_input_step.setStartAndEndColumn(start_colonna,end_colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}

			return true;
		} 
		else {
			return false;
		}
	}

	protected boolean parseTypeShort(String type_to_parse,InputSubDichiarationVar single_input_step)
			throws InvalidInformatStringException {

		Pattern pattern = Pattern.compile("short\\s*(((\\[\\s*(\\d+)\\s*\\])|(\\[\\s*(\\d+)\\s*\\-\\s*(\\d+)\\s*\\]))?)",
						  Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(type_to_parse);
		if (matcher.matches()) {
			single_input_step.setTypeVar(Short.class);
			// imposto ,se presente, colonna singola di lettura
			if (matcher.group(4) != null) {
				if(is_dich_column) {
					int colonna = Integer.parseInt(matcher.group(4));
					single_input_step.setStartAndEndColumn(colonna, colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}
			// imposto ,se presente, range star e end colonna di lettura
			else if (matcher.group(6) != null && matcher.group(7) != null) {
				if(is_dich_column) {
					int start_colonna = Integer.parseInt(matcher.group(6));
					int end_colonna = Integer.parseInt(matcher.group(7));
					single_input_step.setStartAndEndColumn(start_colonna,end_colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}

			return true;
		} 
		else {
			return false;
		}
	}

	protected boolean parseTypeLong(String type_to_parse,InputSubDichiarationVar single_input_step)
			throws InvalidInformatStringException {

		Pattern pattern = Pattern.compile("long\\s*(((\\[\\s*(\\d+)\\s*\\])|(\\[\\s*(\\d+)\\s*\\-\\s*(\\d+)\\s*\\]))?)",
						  Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(type_to_parse);
		if (matcher.matches()) {
			single_input_step.setTypeVar(Long.class);
			/*
			 * for(int a=0;a<=matcher.groupCount();a++) {
			 * System.out.println("KKKKK>>>>>" + matcher.group(a) + "<-:"+a); }
			 */

			// imposto ,se presente, colonna singola di lettura
			if (matcher.group(4) != null) {
				if(is_dich_column) {
					int colonna = Integer.parseInt(matcher.group(4));
					single_input_step.setStartAndEndColumn(colonna, colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}
			// imposto ,se presente, range star e end colonna di lettura
			else if (matcher.group(6) != null && matcher.group(7) != null) {
				if(is_dich_column) {
					int start_colonna = Integer.parseInt(matcher.group(6));
					int end_colonna = Integer.parseInt(matcher.group(7));
					single_input_step.setStartAndEndColumn(start_colonna,end_colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}
			return true;
		} 
		else {
			return false;
		}
	}

	protected boolean parseTypeFloat(String type_to_parse,InputSubDichiarationVar single_input_step)
			throws InvalidInformatStringException {

		Pattern pattern = Pattern.compile("float\\s*(((\\[\\s*(\\d+)\\s*\\])|(\\[\\s*(\\d+)\\s*\\-\\s*(\\d+)\\s*\\]))?)",
						  Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(type_to_parse);
		if (matcher.matches()) {
			single_input_step.setTypeVar(Float.class);

			// imposto ,se presente, colonna singola di lettura
			if (matcher.group(4) != null) {
				if(is_dich_column) {
					int colonna = Integer.parseInt(matcher.group(4));
					single_input_step.setStartAndEndColumn(colonna, colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}
			// imposto ,se presente, range star e end colonna di lettura
			else if (matcher.group(6) != null && matcher.group(7) != null) {
				if(is_dich_column) {
					int start_colonna = Integer.parseInt(matcher.group(6));
					int end_colonna = Integer.parseInt(matcher.group(7));
					single_input_step.setStartAndEndColumn(start_colonna,end_colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}

			return true;
		} 
		else {
			return false;
		}
	}

	protected boolean parseTypeDouble(String type_to_parse,InputSubDichiarationVar single_input_step)
			throws InvalidInformatStringException {

		Pattern pattern = Pattern.compile("double\\s*(((\\[\\s*(\\d+)\\s*\\])|(\\[\\s*(\\d+)\\s*\\-\\s*(\\d+)\\s*\\]))?)",
						  Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(type_to_parse);
		if (matcher.matches()) {
			single_input_step.setTypeVar(Double.class);

			// imposto ,se presente, colonna singola di lettura
			if (matcher.group(4) != null) {
				if(is_dich_column) {
					int colonna = Integer.parseInt(matcher.group(4));
					single_input_step.setStartAndEndColumn(colonna, colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}
			// imposto ,se presente, range star e end colonna di lettura
			else if (matcher.group(6) != null && matcher.group(7) != null) {
				if(is_dich_column) {
					int start_colonna = Integer.parseInt(matcher.group(6));
					int end_colonna = Integer.parseInt(matcher.group(7));
					single_input_step.setStartAndEndColumn(start_colonna,end_colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}

			return true;
		} 
		else {
			return false;
		}
	}

	protected boolean parseTypeBoolean(String type_to_parse,InputSubDichiarationVar single_input_step)
			throws InvalidInformatStringException {

		Pattern pattern = Pattern.compile("boolean\\s*(((\\[\\s*(\\d+)\\s*\\])|(\\[\\s*(\\d+)\\s*\\-\\s*(\\d+)\\s*\\]))?)",
						  Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(type_to_parse);
		if (matcher.matches()) {
			single_input_step.setTypeVar(Boolean.class);
	
			// imposto ,se presente, colonna singola di lettura
			if (matcher.group(4) != null) {
				if(is_dich_column) {
					int colonna = Integer.parseInt(matcher.group(4));
					single_input_step.setStartAndEndColumn(colonna, colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}
			// imposto ,se presente, range star e end colonna di lettura
			else if (matcher.group(6) != null && matcher.group(7) != null) {
				if(is_dich_column) {
					int start_colonna = Integer.parseInt(matcher.group(6));
					int end_colonna = Integer.parseInt(matcher.group(7));
					single_input_step.setStartAndEndColumn(start_colonna,end_colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}
			return true;
		} 
		else {
			return false;
		}
	}

	protected boolean parseTypeDate(String type_to_parse,InputSubDichiarationVar single_input_step)
			throws InvalidInformatStringException {

		Pattern pattern = Pattern.compile("date\\s*((\\(\\s*(.+)\\s*\\)))\\s*(((\\[\\s*(\\d+)\\s*\\-\\s*(\\d+)\\s*\\]))?)",
						  Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(type_to_parse);
		boolean sub_macher_date = true;
		if (matcher.matches()) {
			single_input_step.setTypeVar(GregorianCalendar.class);
			// imposto lunghezza della variabile string
			if (matcher.group(3) != null) {
				if(is_dich_column) {
					sub_macher_date = ParserDate.parser(matcher.group(3),single_input_step);
					if (!sub_macher_date) return false; 
				
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare il formato di data "+matcher.group(3));
			}
			// imposto ,se presente, range star e end colonna di lettura
			if (matcher.group(7) != null && matcher.group(8) != null) { 
				if(is_dich_column) {
					int start_colonna = Integer.parseInt(matcher.group(7));
					int end_colonna = Integer.parseInt(matcher.group(8));
					single_input_step.setStartAndEndColumn(start_colonna,end_colonna);
				}	
				else throw new InvalidInformatStringException("ERRORE. Nel metodo declareNewVar() non si deve specificare nessun intervallo di colonne []");
			}
			return true;
		} 
		else {
			return false;
		}
	}
}
