package jsetl.lib.userconstraint;

import jsetl.ConstraintClass;
import jsetl.exception.Fail;
import jsetl.SolverClass;
import jsetl.annotation.NotNull;

public interface ConstraintSolvingCase {
    boolean solve(@NotNull ConstraintClass constraint, @NotNull SolverClass solver) throws Fail;
}
