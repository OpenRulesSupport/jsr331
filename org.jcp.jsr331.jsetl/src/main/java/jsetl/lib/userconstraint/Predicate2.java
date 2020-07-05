package jsetl.lib.userconstraint;

import jsetl.annotation.Nullable;

public interface Predicate2<T1, T2>{
    boolean test(@Nullable T1 argument1, @Nullable T2 argument2);
}
