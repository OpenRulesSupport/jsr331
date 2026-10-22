package com.quantego.clp;

import com.quantego.clp.CLP;
import com.quantego.clp.CLPVariable;

public class MipSmoke {
    public static void main(String[] args) {
        CLP model = new CLP();
        CLPVariable x = model.addVariable().binary().obj(6);
        CLPVariable y = model.addVariable().binary().obj(5);
        CLPVariable z = model.addVariable().binary().obj(4);
        model.createExpression().add(4, x).add(3, y).add(2, z).leq(5);
        CLP.STATUS status = model.maximize();
        if (status != CLP.STATUS.OPTIMAL || Math.abs(model.getObjectiveValue() - 9.) > 1e-7)
            throw new AssertionError(status + " objective=" + model.getObjectiveValue());
        System.out.println("MIP smoke test passed: objective=" + model.getObjectiveValue());
    }
}
