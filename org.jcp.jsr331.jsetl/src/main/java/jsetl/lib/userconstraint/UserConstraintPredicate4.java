package jsetl.lib.userconstraint;

import jsetl.exception.Fail;
import jsetl.LObject;

public class UserConstraintPredicate4<T1,T2,T3,T4> extends UserConstraint {
    public UserConstraintPredicate4(String name, Predicate4<T1,T2,T3,T4> predicate){
        super(name, (c,s) -> {
            if(!c.isGround()){
                return false;
            }
            Object argument1, argument2, argument3, argument4;
            argument1 = c.getArg(1);
            argument2 = c.getArg(2);
            argument3 = c.getArg(3);
            argument4 = c.getArg(4);

            argument1 = argument1 instanceof LObject ? ((LObject) argument1).getValue() : argument1;
            argument2 = argument2 instanceof LObject ? ((LObject) argument2).getValue() : argument2;
            argument3 = argument3 instanceof LObject ? ((LObject) argument3).getValue() : argument3;
            argument4 = argument4 instanceof LObject ? ((LObject) argument4).getValue() : argument4;
            try {
                if (predicate.test((T1) argument1, (T2) argument2, (T3) argument3, (T4) argument4))
                    return true;
                else
                    throw new Fail();
            } catch (ClassCastException classCastException){
                throw new Fail();
            }
        });
    }

}
