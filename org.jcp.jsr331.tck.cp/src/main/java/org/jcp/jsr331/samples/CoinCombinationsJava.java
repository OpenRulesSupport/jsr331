package org.jcp.jsr331.samples;

public class CoinCombinationsJava {
    
    public static void main(String[] args) {
        
        long startTime = System.currentTimeMillis();

        int n = 0;
        for(int c1=0; c1 <= 100; c1++) {
            for(int c2=0; c2 <= 100/2; c2++) {
                for(int c5=0; c5 <= 100/5; c5++) {
                    for(int c10=0; c10 <= 100/10; c10++) {
                        for(int c20=0; c20 <= 100/20; c20++) {
                            for(int c50=0; c50 <= 100/50; c50++) {
                                for(int c100=0; c100 <= 100/100; c100++) {
                                    if (c1 + c2*2 + c5*5 + c10*10 + c20*20 + c50*50 + c100*100 == 100) {
                                        n++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Total " + n + " combinations");
        long endTime = System.currentTimeMillis();
        System.out.println("Total elapsed time: " + (endTime - startTime +1) + " ms");
    }
}
