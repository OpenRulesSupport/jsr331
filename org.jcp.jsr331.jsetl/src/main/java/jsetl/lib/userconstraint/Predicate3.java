package jsetl.lib.userconstraint;

import jsetl.annotation.Nullable;

public interface Predicate3<T1, T2, T3>{
    boolean test(@Nullable T1 argument1, @Nullable T2 argument2, @Nullable T3 argument3);
}
