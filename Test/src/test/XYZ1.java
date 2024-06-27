package test;

public class XYZ1 {

	public static void main(String[] unused) {
		
		for(int x = 0; x < 10; x++) {
			for (int y = x + 1; y <= 10; y++) {
				int z = x + y;
				if (z <= 10)
					System.out.println("x="+x + " y="+y + " z=" +z);
			}
		}
	}
}
