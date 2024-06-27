package test;

public class MaxInt {

	public static void main(String[] unused) {
		do_shorts();
		do_ints();
		do_longs();
	}

	protected static void do_shorts() {
		short i = Short.MAX_VALUE;
		System.out.println("i=" + i++);
		System.out.println("i=" + i++);
		System.out.println("i=" + i++);
	}

	protected static void do_ints() {
		int i = Integer.MAX_VALUE;
		System.out.println("i=" + i++);
		System.out.println("i=" + i++);
		System.out.println("i=" + i++);
	}
	
	protected static void do_longs() {
		long i = Long.MAX_VALUE;
		System.out.println("i=" + i++);
		System.out.println("i=" + i++);
		System.out.println("i=" + i++);
	}
}
