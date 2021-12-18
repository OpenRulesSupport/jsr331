package javax.constraints.impl;

import javax.constraints.VarReal;

public class LinearRealConstraint {
    
    double[] coefficients;
    VarReal[] vars;
    String oper;
    double value;
    
    public LinearRealConstraint(double[] coefficients, VarReal[] vars, String oper, double value) {
        super();
        this.coefficients = coefficients;
        this.vars = vars;
        this.oper = oper;
        this.value = value;
    }
    
    public LinearRealConstraint(double[] coefficients, VarReal[] vars, String oper, VarReal var) {
        int length = vars.length;
        double[] coefficients1 = new double[length+1];
        VarReal[] vars1 = new VarReal[length+1];
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

    public double[] getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(double[] coefficients) {
        this.coefficients = coefficients;
    }

    public VarReal[] getVars() {
        return vars;
    }

    public void setVars(VarReal[] vars) {
        this.vars = vars;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    
    
    

}
