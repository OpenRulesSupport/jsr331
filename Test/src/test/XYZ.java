package test;

public class XYZ {

	public static void main(String[] unused) {
		
		for(int x = 0; x <= 10; x++) {
			for (int y = 0; y <= 10; y++) {
				for (int z = 0; z <= 10; z++) {
					if (x < y && (x + y == z))
						System.out.println("x="+x + " y="+y + " z=" +z);
				}
			}
		}
	}
}
