package jsetl.lib.userconstraint;

import jsetl.exception.Fail;
import jsetl.LObject;

public class UserConstraintPredicate1<T1> extends UserConstraint {
    public UserConstraintPredicate1(String name, Predicate1<T1> predicate){
        super(name, (c,s) -> {
            if(!c.isGround()){
                return false;
            }
            Object argument1;
            argument1 = c.getArg(1);

            argument1 = argument1 instanceof LObject ? ((LObject) argument1).getValue() : argument1;
            try {
                if (predicate.test((T1) argument1))
                    return true;
                else
                    throw new Fail();
            } catch (ClassCastException classCastException){
                throw new Fail();
            }
        });
    }

}
