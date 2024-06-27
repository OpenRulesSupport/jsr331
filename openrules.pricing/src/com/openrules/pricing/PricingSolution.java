package com.openrules.pricing;

public class PricingSolution {
	int revenue;
	int totalPrice;
	PricingItem[] items;

	public PricingSolution(int totalPrice, PricingItem[] items, int revenue) {
		this.totalPrice = totalPrice;
		this.items = items;
		this.revenue = revenue;
	}

	public int getRevenue() {
		return revenue;
	}

	public void setRevenue(int revenue) {
		this.revenue = revenue;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}

	public PricingItem[] getItems() {
		return items;
	}

	public void setItems(PricingItem[] items) {
		this.items = items;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("PricingSolution: revenue=" + revenue + ", totalPrice=" + totalPrice);
		for (int i = 0; i < items.length; i++) {
			buf.append("\n" + items[i].toString());
		}
		return buf.toString();
	}

}
