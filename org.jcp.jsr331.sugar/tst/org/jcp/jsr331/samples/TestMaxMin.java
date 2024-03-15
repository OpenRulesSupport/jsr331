package org.jcp.jsr331.samples;

import javax.constraints.*;

public class TestMaxMin {

	static Problem p = ProblemFactory.newProblem("TestXYZ");
   
    Var max;
   
    public void defineAndSolve()
    {
        Var a=p.variable("a",1,10);
        Var b=p.variable("b",1,10);
        Var e=p.variable("e",1,10);
       
        Var c=(a.multiply(2)).minus(b);
        p.add("c",c);
        Var d=(b.multiply(0)).minus(a);
        p.add("d",d);
        Var g=(b.multiply(10)).minus(a);
        p.add("g",g);
          
        p.log(p.getVars());
        Var [] c_d_g = {c,d,g};
        Var max = p.max(c_d_g);
        max.setName("max(c_d_g)");
        Var min = p.min(c_d_g);
        min.setName("min(c_d_g)");
       
        // This version works well but I have problems finding the maximum value from an array
        
        Var max_cd= p.max(c,d);
        Var max_dg= p.max(d,g);
        Var max_final= p.max(max_cd,max_dg);
        p.add("MAX",max_final); //!!!!!!!!!!!!!!
       
        p.log("After max");
        p.log(p.getVars());  
        
//        p.post(a,"=",1);
//        p.post(b,"=",1);
//        p.post(c,"=",1);
        
        Solver solver = p.getSolver();
        Solution s = solver.findSolution();
   
        if(s==null)
        	p.log("Nici o solutie");
        else
        {
          s.log();
          p.log(max.toString());
          p.log(min.toString());
        }
    }
 
    public static void main(String[] args) {
       
        TestMaxMin x = new TestMaxMin();
        x.defineAndSolve();
    }
 
}
