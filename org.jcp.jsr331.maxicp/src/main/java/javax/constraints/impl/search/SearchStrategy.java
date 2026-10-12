package javax.constraints.impl.search;

/** JSR-331 search strategy consumed by the MaxiCP solver bridge. */
public class SearchStrategy extends AbstractSearchStrategy {
    public SearchStrategy(javax.constraints.Solver solver) {
        super(solver);
        setVars(solver.getProblem().getVars());
    }
}
