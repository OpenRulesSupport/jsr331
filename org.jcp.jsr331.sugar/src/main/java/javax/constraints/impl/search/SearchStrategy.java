package javax.constraints.impl.search;

import javax.constraints.Solver;

public class SearchStrategy extends AbstractSearchStrategy {
    public SearchStrategy(Solver solver) {
        super(solver);
        javax.constraints.Var[] vars = solver.getProblem().getVars();
        setVars(vars);
        // TODO JSR331 Implementation
        // getProblem().log("SearchStrategy is not supported");
    }
}
