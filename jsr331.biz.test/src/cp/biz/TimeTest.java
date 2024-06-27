package cp.biz;

import cp.constrainer.CSP;

public class TimeTest {

	public static void main(String[] args) {

		CSP csp = new CSP("XYZ");
		csp.profileOn();
		try {
			for(int i=0; i<1000000; i++) {
				Thread.sleep(1);
				if (i%100000 == 0)
					csp.log("i="+i);
			}
		}
		catch(Exception e) {
			csp.log("Caught");
		}
		csp.profileLog();
	}

}
