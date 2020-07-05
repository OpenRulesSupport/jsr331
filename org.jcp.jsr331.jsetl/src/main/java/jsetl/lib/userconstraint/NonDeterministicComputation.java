package jsetl.lib.userconstraint;

import jsetl.annotation.NotNull;
import java.util.Objects;
import java.util.stream.Stream;

public class NonDeterministicComputation extends UserConstraint {

    public NonDeterministicComputation(
            @NotNull String name,
            @NotNull NonDeterministicComputationCase beforeAllCases,
            @NotNull NonDeterministicComputationCase... runnableCases){

        super(name, (c,s) -> {beforeAllCases.run(c.getAlternative()); return false;},
                nonDeterministicComputationCasesToConstraintCases(runnableCases));
    }

    private static @NotNull
    ConstraintSolvingCase[] nonDeterministicComputationCasesToConstraintCases(
            @NotNull NonDeterministicComputationCase... nonDeterministicComputationCases){

        assert nonDeterministicComputationCases != null;
        assert Stream.of(nonDeterministicComputationCases).noneMatch(Objects::isNull);

        int numberOfCases = nonDeterministicComputationCases.length;

        ConstraintSolvingCase[] cases = new ConstraintSolvingCase[numberOfCases];

        for(int i = 0; i < numberOfCases; ++i){
            int k = i;
                cases[i] = (c,s) -> {nonDeterministicComputationCases[k].run(k); return true;};
        }

        assert cases != null;
        assert Stream.of(cases).noneMatch(Objects::isNull);
        return cases;
    }
}
