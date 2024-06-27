package pricing;

public class InputRecord {
	
	String	saleId;
	String	productLookId;
	int		origSalePrice;
	double	basePrice;
	int		msrpPrice;
	int		numUnitsAvailable;
	
	int		suggestedSalePrice;
	int		predictedNumUnitsSold;
	int		expectedRevenue;
	
	
	public String getSaleId() {
		return saleId;
	}
	public void setSaleId(String saleId) {
		this.saleId = saleId;
	}
	public String getProductLookId() {
		return productLookId;
	}
	public void setProductLookId(String productLookId) {
		this.productLookId = productLookId;
	}
	public int getSuggestedSalePrice() {
		return suggestedSalePrice;
	}
	public void setSuggestedSalePrice(int suggestedSalePrice) {
		this.suggestedSalePrice = suggestedSalePrice;
	}
	public int getOrigSalePrice() {
		return origSalePrice;
	}
	public void setOrigSalePrice(int origSalePrice) {
		this.origSalePrice = origSalePrice;
	}
	public double getBasePrice() {
		return basePrice;
	}
	public void setBasePrice(double basePrice) {
		this.basePrice = basePrice;
	}
	public int getMsrpPrice() {
		return msrpPrice;
	}
	public void setMsrpPrice(int msrpPrice) {
		this.msrpPrice = msrpPrice;
	}
	public int getNumUnitsAvailable() {
		return numUnitsAvailable;
	}
	public void setNumUnitsAvailable(int numUnitsAvailable) {
		this.numUnitsAvailable = numUnitsAvailable;
	}
	public int getPredictedNumUnitsSold() {
		return predictedNumUnitsSold;
	}
	public void setPredictedNumUnitsSold(int predictedNumUnitsSold) {
		this.predictedNumUnitsSold = predictedNumUnitsSold;
	}
	public int getExpectedRevenue() {
		return expectedRevenue;
	}
	public void setExpectedRevenue(int expectedRevenue) {
		this.expectedRevenue = expectedRevenue;
	}
	
}
