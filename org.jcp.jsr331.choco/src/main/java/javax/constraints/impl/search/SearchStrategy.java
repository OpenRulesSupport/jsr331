package javax.constraints.impl.search;

import java.util.Random;

import javax.constraints.Solver;
import javax.constraints.impl.Var;

import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.variables.IntVar;

/** Maps JSR-331 selectors to native Choco 6 search strategies. */
public class SearchStrategy extends AbstractSearchStrategy {
    public SearchStrategy(Solver solver) {
        super(solver);
        javax.constraints.Var[] problemVars = solver.getProblem().getVars();
        setVars(problemVars == null ? new javax.constraints.Var[0] : problemVars);
    }

    public AbstractStrategy<?> getChocoStrategy() {
        IntVar[] chocoVars = new IntVar[vars.length];
        for (int i = 0; i < vars.length; i++) chocoVars[i] = ((Var) vars[i]).getChocoVar();
        if (chocoVars.length == 0) return null;

        javax.constraints.VarSelector selector = getVarSelector();
        javax.constraints.ValueSelector valueSelector = getValueSelector();
        boolean upper = valueSelector != null && valueSelector.getType() == javax.constraints.ValueSelectorType.MAX;
        if (selector == null) return upper ? Search.inputOrderUBSearch(chocoVars) : Search.inputOrderLBSearch(chocoVars);

        switch (selector.getType()) {
            case INPUT_ORDER:
                return upper ? Search.inputOrderUBSearch(chocoVars) : Search.inputOrderLBSearch(chocoVars);
            case MIN_DOMAIN:
            case MIN_DOMAIN_MIN_VALUE:
                return upper ? Search.minDomUBSearch(chocoVars) : Search.minDomLBSearch(chocoVars);
            case RANDOM:
            case MIN_DOMAIN_RANDOM:
                return Search.randomSearch(chocoVars, new Random().nextLong());
            case MIN_DOMAIN_OVER_WEIGHTED_DEGREE:
            case MAX_WEIGHTED_DEGREE:
                return Search.domOverWDegSearch(chocoVars);
            default:
                return upper ? Search.inputOrderUBSearch(chocoVars) : Search.inputOrderLBSearch(chocoVars);
        }
    }
}
