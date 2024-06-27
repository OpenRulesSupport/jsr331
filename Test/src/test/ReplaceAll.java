package test;

public class ReplaceAll {

	    public static void main (String[]args)
	    {

	        String a  = " ABOVE_	 	MID ";
	        System.out.println("Input: <"+a+">");

	        String b = a.replaceAll("[\\s+_]","");

	        System.out.println("Output: <"+b+">");

	    }
	}
