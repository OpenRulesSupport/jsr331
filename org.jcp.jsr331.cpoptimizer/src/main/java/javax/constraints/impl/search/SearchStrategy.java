package javax.constraints.impl.search;

import javax.constraints.SearchStrategy.SearchStrategyType;
import javax.constraints.ValueSelectorType;
import javax.constraints.VarSelectorType;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cp.IloCP;
import ilog.cp.IloIntValueChooser;
import ilog.cp.IloIntVarChooser;
import ilog.cp.IloSearchPhase;

/** Maps a JSR 331 search variable list to a CP Optimizer search phase. */
public class SearchStrategy extends AbstractSearchStrategy {
    public SearchStrategy(javax.constraints.Solver solver) {
        super(solver);
        javax.constraints.Var[] problemVariables = solver.getProblem().getVars();
        setVars(problemVariables == null ? new javax.constraints.Var[0] : problemVariables);
    }

    IloSearchPhase createPhase(IloCP cp) throws IloException {
        if (getType() == SearchStrategyType.CUSTOM || vars == null || vars.length == 0)
            return null;
        IloIntVar[] nativeVariables = new IloIntVar[vars.length];
        for (int index = 0; index < vars.length; index++)
            nativeVariables[index] = ((javax.constraints.impl.Var) vars[index]).getCpVar();

        IloIntVarChooser variableChooser;
        VarSelectorType variableType = getVarSelector() == null
                ? VarSelectorType.MIN_DOMAIN : getVarSelector().getType();
        variableChooser = switch (variableType) {
            case MAX_WEIGHTED_DEGREE ->
                    cp.intVarChooser(cp.selectLargest(cp.domainSize()));
            case RANDOM, MIN_DOMAIN_RANDOM ->
                    cp.intVarChooser(cp.selectRandomVar());
            case INPUT_ORDER ->
                    cp.intVarChooser(cp.selectSmallest(cp.varIndex(nativeVariables)));
            default -> cp.intVarChooser(cp.selectSmallest(cp.domainSize()));
        };

        IloIntValueChooser valueChooser;
        ValueSelectorType valueType = getValueSelector() == null
                ? ValueSelectorType.MIN : getValueSelector().getType();
        valueChooser = switch (valueType) {
            case MAX -> cp.intValueChooser(cp.selectLargest(cp.value()));
            case RANDOM -> cp.intValueChooser(cp.selectRandomValue());
            default -> cp.intValueChooser(cp.selectSmallest(cp.value()));
        };
        return cp.searchPhase(nativeVariables, variableChooser, valueChooser);
    }
}
