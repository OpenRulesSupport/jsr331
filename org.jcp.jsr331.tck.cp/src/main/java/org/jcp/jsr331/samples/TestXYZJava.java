package org.jcp.jsr331.samples;

public class TestXYZJava {

	public static void main(String[] args) {
		// ==== PROBLEM DEFINITION ==============================
		for(int x = 1; x <= 10; x++) {
			for(int y = 1; y <= 10; y++) {
				for(int z = 1; z <= 10; z++) {
					if (x < y && x+y == z) {
						System.out.println("x=" + x + " y=" + y + " z=" + z);
						System.out.println("Cost=" + (3*x*y - 4*z)); 
						return;
					}
				}
			}
		}
		System.out.println("No solutions");
	}
}
