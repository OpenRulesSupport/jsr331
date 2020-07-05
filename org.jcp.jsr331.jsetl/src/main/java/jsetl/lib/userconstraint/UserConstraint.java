package jsetl.lib.userconstraint;

import jsetl.*;
import jsetl.annotation.NotNull;
import jsetl.exception.NotDefConstraintException;

import java.util.Arrays;
import java.util.Objects;

public class UserConstraint {
    private final String name;
    private final ConstraintSolvingCase beforeAllCases;
    private final ConstraintSolvingCase[] cases;

    public UserConstraint(@NotNull String name, @NotNull ConstraintSolvingCase beforeAllCases, @NotNull ConstraintSolvingCase... cases){
        Objects.requireNonNull(name);
        Objects.requireNonNull(beforeAllCases);
        Objects.requireNonNull(cases);
        if(Arrays.stream(cases).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        this.name = name;
        this.beforeAllCases = beforeAllCases;
        this.cases = cases;
    }

    public ConstraintClass create(@NotNull SolverClass solver, @NotNull Object... arguments){
        Objects.requireNonNull(solver);
        Objects.requireNonNull(arguments);
        if(Arrays.stream(arguments).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        int numberOfCases = cases.length;
        new NewConstraints(solver) {
            @Override
            protected void user_code(ConstraintClass constraint) throws NotDefConstraintException {
                if (!constraint.getName().equals(name))
                    throw new NotDefConstraintException();
                if (!beforeAllCases.solve(constraint, solver))
                    constraint.notSolved();
                else
                    return;
                int alternative = constraint.getAlternative();
                if (alternative < numberOfCases - 1)
                    solver.addChoicePoint(constraint);
                for (int i = 0; i < numberOfCases; ++i) {
                    if (i == alternative) {
                        if (!cases[i].solve(constraint, solver))
                            constraint.notSolved();
                    }
                }
            }
        };

        return new ConstraintClass(name, arguments);
    }


}
