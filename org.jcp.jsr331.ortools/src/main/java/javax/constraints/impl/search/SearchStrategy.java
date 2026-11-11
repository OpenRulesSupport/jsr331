package javax.constraints.impl.search;

import javax.constraints.SearchStrategy.SearchStrategyType;
import javax.constraints.ValueSelectorType;
import javax.constraints.VarSelectorType;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.DecisionStrategyProto.DomainReductionStrategy;
import com.google.ortools.sat.DecisionStrategyProto.VariableSelectionStrategy;
import com.google.ortools.sat.LinearArgument;

/** Maps JSR 331 selectors to CP-SAT decision strategies. */
public class SearchStrategy extends AbstractSearchStrategy {
    public SearchStrategy(javax.constraints.Solver solver) {
        super(solver);
        javax.constraints.Var[] problemVariables = solver.getProblem().getVars();
        setVars(problemVariables == null ? new javax.constraints.Var[0] : problemVariables);
    }

    public boolean apply(CpModel model) {
        if (getType() == SearchStrategyType.CUSTOM || vars == null || vars.length == 0) {
            return false;
        }
        LinearArgument[] nativeVariables = new LinearArgument[vars.length];
        for (int i = 0; i < vars.length; i++) {
            int index = ((javax.constraints.impl.Var) vars[i]).getCpVar().getIndex();
            nativeVariables[i] = model.getIntVarFromProtoIndex(index);
        }

        VariableSelectionStrategy variableSelection = VariableSelectionStrategy.CHOOSE_FIRST;
        if (getVarSelector() != null) {
            VarSelectorType type = getVarSelector().getType();
            variableSelection = switch (type) {
                case MIN_DOMAIN, MIN_DOMAIN_MIN_VALUE,
                     MIN_DOMAIN_RANDOM, MIN_DOMAIN_OVER_WEIGHTED_DEGREE ->
                        VariableSelectionStrategy.CHOOSE_MIN_DOMAIN_SIZE;
                case MAX_WEIGHTED_DEGREE -> VariableSelectionStrategy.CHOOSE_MAX_DOMAIN_SIZE;
                default -> VariableSelectionStrategy.CHOOSE_FIRST;
            };
        }

        DomainReductionStrategy valueSelection = DomainReductionStrategy.SELECT_MIN_VALUE;
        if (getValueSelector() != null) {
            ValueSelectorType type = getValueSelector().getType();
            valueSelection = switch (type) {
                case MAX -> DomainReductionStrategy.SELECT_MAX_VALUE;
                case MIDDLE -> DomainReductionStrategy.SELECT_MEDIAN_VALUE;
                case RANDOM -> DomainReductionStrategy.SELECT_RANDOM_HALF;
                case MIN_MAX_ALTERNATE -> DomainReductionStrategy.SELECT_LOWER_HALF;
                default -> DomainReductionStrategy.SELECT_MIN_VALUE;
            };
        }
        model.addDecisionStrategy(nativeVariables, variableSelection, valueSelection);
        return true;
    }
}
