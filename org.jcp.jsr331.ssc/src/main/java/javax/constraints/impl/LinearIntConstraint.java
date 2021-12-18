package javax.constraints.impl;

import javax.constraints.Var;

public class LinearIntConstraint {
    
    int[] coefficients;
    Var[] vars;
    String oper;
    int value;
    
    public LinearIntConstraint(int[] coefficients, Var[] vars, String oper, int value) {
        super();
        this.coefficients = coefficients;
        this.vars = vars;
        this.oper = oper;
        this.value = value;
    }
    
    public LinearIntConstraint(int[] coefficients, Var[] vars, String oper, Var var) {
        int length = vars.length;
        int[] coefficients1 = new int[length+1];
        Var[] vars1 = new Var[length+1];
        for(int i = 0; i < length; i++) {
            coefficients1[i] = coefficients[i];
            vars1[i] = vars[i];
        }
        coefficients1[length] = -1;
        vars1[length] = var;
        
        this.coefficients = coefficients1;
        this.vars = vars1;
        this.oper = oper;
        this.value = 0;
    }

    public int[] getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(int[] coefficients) {
        this.coefficients = coefficients;
    }

    public Var[] getVars() {
        return vars;
    }

    public void setVars(Var[] vars) {
        this.vars = vars;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    

}
