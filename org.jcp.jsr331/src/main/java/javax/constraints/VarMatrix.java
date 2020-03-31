package javax.constraints;

/**
 * A VarMatrix is a two-dimensional array of Var objects
 *
 */

public interface VarMatrix {
	
	public int numberOfRows();
	
	public int numberOfColumns();
	
	public Var[] row(int i);
	
	public Var[] column(int j);
	
	public Var[] flat();
	
	public Var[] diagonal1();
	
	public Var[] diagonal2();
	
	public Var get(int i,int j);
	
	public void post(int[][] data);
	
	public void post(int i, int j, int value);
	
}
