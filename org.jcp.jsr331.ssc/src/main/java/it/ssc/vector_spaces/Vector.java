package it.ssc.vector_spaces;

public class Vector {
	

	public enum TYPE_VECTOR {ROW, COLUMN};
	private TYPE_VECTOR tipo;
	private double[] vector;
	
	/** 
	 * Crea un vettore riga */
	
	
	public Vector(double[]  vectorArg) {
		this(vectorArg,TYPE_VECTOR.ROW);
	}
	
	public  Vector(int size,TYPE_VECTOR tipo) {
		this.tipo=tipo;
		vector=new double[size];
	}
	
	public TYPE_VECTOR getTipo() {
		return tipo;
	}
	
	public Vector(double[] vectorArg,TYPE_VECTOR tipo) {
		this.tipo=tipo;
		if(vectorArg==null) throw new NullPointerException("Non posso costruire un oggetto Vector con argomento del costruttore a null");
		this.vector=new double[vectorArg.length];
		for(int _a=0;_a<vectorArg.length;_a++) {
			vector[_a]=vectorArg[_a];
		}
	}
	
	public void multiply(double scalare)  {
		for(int _a=0;_a<vector.length;_a++) {
			vector[_a]=vector[_a] * scalare;
		}
	}
	
	public double getCell(int index) {
		return vector[index];
	}
	
	public void setCell(int index,double value) {
		vector[index]=value;
	}
	
	public int lenght() {
		return vector.length;
	}
	
	private static double _scalarProduct(Vector vector0,Vector vector1) throws VectorException {
		if(vector1==null || vector0==null ) throw new VectorException("Non posso effettuare il prodotto cartesiano con un vettore a null");
		if(vector0.lenght() != vector1.lenght())  throw new VectorException("Non posso effettuare il prodotto cartesiano su vettori di dimensioni diverse.");
		
		double scalare= 0.0;
		double single_produtc;
		for(int _a=0;_a<vector0.lenght();_a++) {
			single_produtc=vector0.vector[_a] * vector1.vector[_a];
			scalare=scalare + single_produtc;
		}
		return scalare;
	}
	
	public static double scalarProduct(Vector vector0,Vector vector1) throws VectorException {
		return _scalarProduct(vector0,vector1);
	}
	
	public double getNorma() throws Exception { 
		double norma_e2=_scalarProduct(this,this);
		return Math.pow( norma_e2,0.5);
	}
	
	public void traspose() { 
		if(tipo==TYPE_VECTOR.ROW) tipo=TYPE_VECTOR.COLUMN;
		else tipo=TYPE_VECTOR.ROW;
	}

	public double[] getVector() {
		return vector;
	}
}

