package pricing;

import java.util.ArrayList;

public class SaleRecord {
	
	// input
	String	uniqueId;
	String	saleId;
	String	productLookId;
	int		origSalePrice;
	double	basePrice;
	int		msrpPrice;
	int		numUnitsAvailable;
	
	ArrayList<Integer>	domain;
	ArrayList<Integer> 	expectedRevenueArray;
	ArrayList<Integer>	expectedNumUnitsSoldArray;
	
	public SaleRecord(String id, InputRecord inputRecord) {
		this.uniqueId = id;
		this.saleId = inputRecord.saleId;
		this.productLookId = inputRecord.productLookId;
		this.origSalePrice = inputRecord.origSalePrice;
		this.basePrice = inputRecord.basePrice;
		this.msrpPrice = inputRecord.msrpPrice;
		this.numUnitsAvailable = inputRecord.numUnitsAvailable;
		domain = new ArrayList<Integer>();
		expectedRevenueArray = new ArrayList<Integer>();
		expectedNumUnitsSoldArray = new ArrayList<Integer>();
	}
	
	
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
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

	public boolean isSameAs(InputRecord record) {
		return this.saleId.equals(record.saleId) && this.productLookId.equals(record.productLookId);
	}
	
	public void addInputRecord(InputRecord record) {
		domain.add(new Integer(record.suggestedSalePrice));
		expectedNumUnitsSoldArray.add(new Integer(record.predictedNumUnitsSold));
		expectedRevenueArray.add(new Integer(record.expectedRevenue));
	}
	
	public int[] getDomain() {
		int[] array = new int[domain.size()];
	    for (int i=0; i < domain.size(); i++)
	    {
	        array[i] = domain.get(i).intValue();
	    }
	    return array;
	}
	
	public int[] getExpectedNumUnitsSoldArray() {
		int[] array = new int[expectedNumUnitsSoldArray.size()];
	    for (int i=0; i < expectedNumUnitsSoldArray.size(); i++)
	    {
	        array[i] = expectedNumUnitsSoldArray.get(i).intValue();
	    }
	    return array;
	}
	
	public int[] getExpectedRevenueArray() {
		int[] array = new int[expectedRevenueArray.size()];
	    for (int i=0; i < expectedRevenueArray.size(); i++)
	    {
	        array[i] = expectedRevenueArray.get(i).intValue();
	    }
	    return array;
	}


	@Override
	public String toString() {
		return "SaleRecord [uniqueId=" + uniqueId + ", saleId=" + saleId
				+ ", productLookId=" + productLookId + ", origSalePrice="
				+ origSalePrice + ", basePrice=" + basePrice + ", msrpPrice="
				+ msrpPrice + ", numUnitsAvailable=" + numUnitsAvailable
				+ "\ndomain=" + domain + "\nexpectedRevenueArray="
				+ expectedRevenueArray + "\nexpectedNumUnitsSoldArray="
				+ expectedNumUnitsSoldArray + "]";
	}

}
