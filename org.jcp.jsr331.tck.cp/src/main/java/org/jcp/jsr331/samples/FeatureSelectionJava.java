package org.jcp.jsr331.samples;

import java.util.Arrays;

class Feature implements Comparable<Feature> {
	String id;
	int cost;
	int value;
	double ratio;
	
	public Feature(String id, int cost, int value) {
		super();
		this.id = id;
		this.cost = cost;
		this.value = value;
		this.ratio = (double) value/cost;
	}

    public int compareTo(Feature feature) {
        return Double.compare(feature.ratio,this.ratio); 
    }
}

public class FeatureSelectionJava {
    public static void main(String[] args) {

        Feature[] features = new Feature[] {
        		new Feature("1", 5000, 7),
        		new Feature("2", 4000, 6),
        		new Feature("3", 3000, 5),
        		new Feature("4", 2000, 4),
        		new Feature("5", 1000, 3),
        		new Feature("6", 1000, 2),
        		new Feature("7", 1000, 1),
        		new Feature("8", 1000, 1),
        		new Feature("9", 1000, 1),
        		new Feature("10",1000, 1)
        };
        int budget = 10000;
        
        Arrays.sort(features);
        
        int totalCost = 0;
        int totalValue = 0;
        for(Feature f : features) {
        	if (totalCost + f.cost <= budget) {
        		totalCost += f.cost;
        		totalValue += f.value;
        		System.out.println("Select Feature " + f.id 
        		 + " Cost=" + f.cost + " Value=" + f.value + " Ratio=" + f.ratio);
        	}
        }
        
        System.out.println("Total Value = " + totalValue);
        System.out.println("Total Cost = " + totalCost);
    }
}


