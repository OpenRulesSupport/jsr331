package jsetl.lib.userconstraint;

import jsetl.exception.Fail;
import jsetl.LObject;

public class UserConstraintPredicate3<T1,T2,T3> extends UserConstraint {
    public UserConstraintPredicate3(String name, Predicate3<T1,T2,T3> predicate){
        super(name, (c,s) -> {
            if(!c.isGround()){
                return false;
            }
            Object argument1, argument2, argument3;
            argument1 = c.getArg(1);
            argument2 = c.getArg(2);
            argument3 = c.getArg(3);

            argument1 = argument1 instanceof LObject ? ((LObject) argument1).getValue() : argument1;
            argument2 = argument2 instanceof LObject ? ((LObject) argument2).getValue() : argument2;
            argument3 = argument3 instanceof LObject ? ((LObject) argument3).getValue() : argument3;
            try {
                if (predicate.test((T1) argument1, (T2) argument2, (T3) argument3))
                    return true;
                else
                    throw new Fail();
            } catch (ClassCastException classCastException){
                throw new Fail();
            }
        });
    }

}
