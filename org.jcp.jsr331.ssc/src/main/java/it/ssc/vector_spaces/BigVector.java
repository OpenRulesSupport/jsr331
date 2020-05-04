package it.ssc.vector_spaces;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BigVector {
	
	static final BigDecimal ZERO_BIG=new BigDecimal("0.0",MathContext.DECIMAL128);
	public enum TYPE_VECTOR {ROW, COLUMN};
	private TYPE_VECTOR tipo;
	private BigDecimal[] big_vector;
	
	/** 
	 * Crea un vettore riga */
	public BigVector(double[]  vector) {
		this(vector,TYPE_VECTOR.ROW);
	}
	
	
	public  BigVector(int size,TYPE_VECTOR tipo) {
		this.tipo=tipo;
		big_vector=new BigDecimal[size];
		inizialize();
	}
	 
	private void inizialize() {
		for(int _a=0;_a<big_vector.length;_a++) {
			big_vector[_a]=ZERO_BIG;
		}
	}	
	
	public TYPE_VECTOR getTipo() {
		return tipo;
	}
	
	public BigVector(double[] vector,TYPE_VECTOR tipo) {
		this.tipo=tipo;
		if(vector==null) throw new NullPointerException("Non posso costruire un oggetto Vector con argomento costruttore a null");
		
		big_vector=new BigDecimal[vector.length];
		String str_double;
		for(int _a=0;_a<vector.length;_a++) {
			str_double=String.valueOf(vector[_a]);
			big_vector[_a]=new BigDecimal(str_double,MathContext.DECIMAL128);
		}
	}
	
	public void multiply(double scalar)  {
		BigDecimal big_scalar=new BigDecimal(String.valueOf(scalar),MathContext.DECIMAL128);
		for(int _a=0;_a<big_vector.length;_a++) {
			big_vector[_a]=big_vector[_a].multiply(big_scalar, MathContext.DECIMAL128);
		}
	}
	
	public double getCell(int index) {
		return big_vector[index].setScale(30, RoundingMode.HALF_DOWN).doubleValue();
	}
	
	
	public void setBigCell(int index,BigDecimal value) {
		if(value==null)  big_vector[index]=ZERO_BIG;
		else big_vector[index]=value;
	}
	
	public BigDecimal getBigCell(int index) {
		return big_vector[index];
	}
	
	public int lenght() {
		return big_vector.length;
	}
	
	private static BigDecimal _scalarProduct(BigVector vector0,BigVector vector1) throws VectorException {
		if(vector1==null || vector0==null ) throw new VectorException("Non posso effettuare il prodotto cartesiano con un vettore a null");
		if(vector0.lenght() != vector1.lenght())  throw new VectorException("Non posso effettuare il prodotto cartesiano su vettori di dimensioni diverse.");
		
		BigDecimal scalare= ZERO_BIG;
		BigDecimal single_produtc;
		for(int _a=0;_a<vector0.lenght();_a++) {
			single_produtc=vector0.big_vector[_a].multiply(vector1.big_vector[_a], MathContext.DECIMAL128);
			scalare=scalare.add(single_produtc, MathContext.DECIMAL128);
		}
		return scalare;
	}
	
	public static double scalarProduct(BigVector vector0,BigVector vector1) throws VectorException {
		return _scalarProduct(vector0,vector1).setScale(30, RoundingMode.HALF_DOWN).doubleValue();
	}
	
	public double getNorma() throws Exception { 
		BigDecimal norma_e2=_scalarProduct(this,this);
		return Math.pow( norma_e2.doubleValue(),0.5);
	}
	
	public void traspose() { 
		if(tipo==TYPE_VECTOR.ROW) tipo=TYPE_VECTOR.COLUMN;
		else tipo=TYPE_VECTOR.ROW;
	}
}
