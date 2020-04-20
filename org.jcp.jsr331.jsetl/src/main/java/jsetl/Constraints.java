package jsetl;

import jsetl.annotation.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class Constraints {


    /**
     * Returns a constraint which is the conjunction of {@code constraint} for each successive pair of elements {@code first} and {@code second}
     * in the array {@code lObjects}. For example: if {@code lObjects == {1,2,3}} and {@code constraint == first != second} the returned constraint
     * will be {@code 1 != 2 AND 2 != 3 AND 3 != 4}.
     * @param lObjects array of logical objects.
     * @param first first object of the pair.
     * @param second second object of the pair.
     * @param constraint constraint involving {@code first} and {@code second}.
     * @return the constructed constraint.
     */
    public static @NotNull
    ConstraintClass forAllAdjacentPairs(LObject[] lObjects, LObject first, LObject second, ConstraintClass constraint){
        int n = lObjects.length;
        ConstraintClass result = new ConstraintClass();
        VariablesGetter variablesGetter = new VariablesGetter();
        List<LObject> dummyVariables = variablesGetter.getVariables(constraint).stream().filter(object -> object instanceof IntLVar && ((IntLVar)object).isDummy()).collect(Collectors.toList());
        DummyVariablesReplacer dummyVariablesReplacer = new DummyVariablesReplacer(dummyVariables);


        for(int i = 0; i < n - 1; ++i){
            dummyVariablesReplacer.generateNewSubstitutions();
            ConstraintClass constraint2 = dummyVariablesReplacer.replaceInConstraint(constraint);
            ConstraintMapper constraintMapper1 = new ConstraintMapper(first, lObjects[i]);
            ConstraintMapper constraintMapper2 = new ConstraintMapper(second, lObjects[i + 1]);
            result.add(constraintMapper2.mapConstraintDeeply(constraintMapper1.mapConstraintDeeply(constraint2)));
        }

        return result;

    }
}
