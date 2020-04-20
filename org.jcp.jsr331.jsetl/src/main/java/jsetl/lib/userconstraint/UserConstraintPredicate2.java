package jsetl.lib.userconstraint;

import jsetl.exception.Fail;
import jsetl.LObject;

public class UserConstraintPredicate2<T1,T2> extends UserConstraint {
    public UserConstraintPredicate2(String name, Predicate2<T1,T2> predicate){
        super(name, (c,s) -> {
            if(!c.isGround()){
                return false;
            }
            Object argument1, argument2;
            argument1 = c.getArg(1);
            argument2 = c.getArg(2);

            argument1 = argument1 instanceof LObject ? ((LObject) argument1).getValue() : argument1;
            argument2 = argument2 instanceof LObject ? ((LObject) argument2).getValue() : argument2;
            try {
                if (predicate.test((T1) argument1, (T2) argument2))
                    return true;
                else
                    throw new Fail();
            } catch (ClassCastException classCastException){
                throw new Fail();
            }
        });
    }

}
