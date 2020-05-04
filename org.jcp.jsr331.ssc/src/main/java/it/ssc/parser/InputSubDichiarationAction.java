package it.ssc.parser;


public class InputSubDichiarationAction  implements InputSubDichiarationInterface {
	private final TYPE_INPUT_STEP type;
	
	private boolean union_line_next=false;
	private int point_column=0;
	private int point_row=0;
	
	public InputSubDichiarationAction() {
		type=TYPE_INPUT_STEP.DICHIARATION_ACTION;
	}
	
	public TYPE_INPUT_STEP getTypeInputStep() {
		return type;
	}

	public boolean isReadLineNext() {
		return union_line_next;
	}

	public void setReadLineNext(boolean union_line_next) {
		this.union_line_next = union_line_next;
	}

	public int getPointColumn() {
		return point_column;
	}

	//mettere controllo colonna puntata > 0 
	public void setPointColumn(int point_column) {
		this.point_column = point_column;
	}

	//mettere controllo riga puntata > 0 
	public int getPointRow() {
		return point_row;
	}

	public void setPointRow(int point_row) {
		this.point_row = point_row;
	}
}
