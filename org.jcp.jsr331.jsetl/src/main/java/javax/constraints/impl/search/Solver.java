package javax.constraints.impl.search;

import java.util.ArrayList;
import java.util.Vector;

import javax.constraints.impl.Problem;
import javax.constraints.impl.Constraint;

import javax.constraints.Objective;
import javax.constraints.ProblemState;
import javax.constraints.SearchStrategy.SearchStrategyType;

import jsetl.ConstraintClass;
import jsetl.SolverClass;
import jsetl.exception.Failure;
import jsetl.IntLVar;
import jsetl.LabelingOptions;

/**
 * Implement the JSR331 solver extending the abstract class 
 * AbstractSolver of the common implementation. 
 * 
 * The class contains an instance of the JSetL SolverClass that implement
 * the solver, a boolean variable for checking variable labeling, and an
 * integer variable for numbering solutions. 
 * 
 * @author Fabio Biselli
 */
public class Solver extends AbstractSolver {
	
	/**
	 * JSetL concrete solver.
	 */
	private SolverClass jsetlSolver;
	
	/**
	 * Represents the current number of solution found.
	 */
	int numberOfSolutions;
	
	/**
	 * Auxiliary array of JSetL constraints. Represents all constraints
	 * of the problem.
	 */
	protected ConstraintClass[] jsetlConstraints;
	
	/**
	 * Auxiliary array of JSetL integer variables. Represent all auxiliary
	 * variables used in the problem definition.
	 */
	jsetl.IntLVar[] auxIntLVar;
	
	/**
	 * True if the problem has no constraints.
	 */
	Boolean emptyConstraints = true;

	/**
	 * Build a new SolverClass.
	 * 
	 * @param problem the Problem which the solver is bound.
	 */
	public Solver(Problem problem) {
		super(problem);
		jsetlSolver = new SolverClass();
		numberOfSolutions = 0;
		getProblemConstraints();
		setProblemConstraints();
		getAuxVariables();
	}

	/**
	 * Loads all auxiliary variables from the problem.
	 */
	private void getAuxVariables() {
		Problem p = (Problem) getProblem();
		auxIntLVar = p.getAuxIntLVar();
	}

	/**
	 * Auxiliary method that add to the JSetL solver all constraints
	 * saved in the problem bound to <code>this</code> SolverClass.
	 */
	private void setProblemConstraints() {
		if (emptyConstraints) 
			return;
		for (ConstraintClass constraint : jsetlConstraints)
			jsetlSolver.add(constraint);
	}

	/**
	 * Auxiliary method that load all the saved  problem constraints and
	 * store them into the array <code>jsetlConstraints</code>. 
	 */
	private void getProblemConstraints() {
		jsetlConstraints = ((Problem) getProblem()).getJSetLConstraints();
		if (jsetlConstraints == null)
			emptyConstraints  = true;
		else emptyConstraints = false;
	}

	/**
	 * Build and return a new SearchStrategy.
	 */
	public SearchStrategy newSearchStrategy() {
		return new SearchStrategy(this);
	}

	/**
	 * This method attempts to find a solution of the problem, for which the 
	 * solver was defined. It uses the search strategy defined by the method 
	 * setSearchStrategy(). It returns the found solution (if any) or null. 
	 * It also saves the solution and makes it available through the method 
	 * getSolution(). If a solution is not found, the problem state is 
	 * restored to that of before the invocation of this method. If a 
	 * solution is found, the problem state will be restored only if the 
	 * parameter "restore" is true. Otherwise all problem variables will be 
	 * instantiated with the solution values. The search can be limited by 
	 * time, number of failed attempts, etc. 
	 * 
	 * @param restoreOrNot defines if the problem state should be restored 
	 * after a solution is found.
	 * 
	 * @return a solution if successful, null otherwise.
	 */
	public Solution findSolution(ProblemState restoreOrNot) {
		applyHeuristic();
		Solution solution = null;
		if(restoreOrNot == ProblemState.RESTORE) {
			if(jsetlSolver.check()) {
				try {
					solution = new Solution(this, 
							numberOfSolutions++);
				} catch (Failure e) {
					e.printStackTrace();
					return null;
				}
			}
			else return null;
		}
		else if (restoreOrNot == ProblemState.DO_NOT_RESTORE) {
			try {
				jsetlSolver.solve();
				solution = new Solution(this, 
						numberOfSolutions++);
			} catch (Failure e) {
				solution = null;
				//e.printStackTrace();
			}
		}
		return solution;
	}
	
	/**
	 * Auxiliary method that apply labels to JSetL variables. Loads the
	 * search strategies for each variable, than apply the specified 
	 * heuristic.
	 * 
	 *  <p> If labels are already applied in the variables of 
	 *  <code>this</code> solver the method returns without  change.
	 */
	private void applyHeuristic() {
		Vector<javax.constraints.SearchStrategy> strategies =
			getSearchStrategies();
		if (strategies.isEmpty()) {
			SearchStrategy strategy = 
				(SearchStrategy) getSearchStrategy();
			strategy.label();
			strategy.labelSets();
		}
		else {
			for (int i = 0; i < strategies.size(); i++) {
				javax.constraints.SearchStrategy strategy = strategies.elementAt(i);
				if (strategy.getType()== SearchStrategyType.DEFAULT ) {
					((SearchStrategy) strategy).label();
					((SearchStrategy) strategy).labelSets();
				}
			}
		}
		getAuxVariables();
		if (auxIntLVar == null || auxIntLVar.length == 0)
			return;
		LabelingOptions lop = new LabelingOptions();
		jsetlSolver.add(IntLVar.label(lop,auxIntLVar));
	}

	/**
	 * This method attempts to find all solutions for the Problem. It uses the
	 *  default search strategy or the strategy defined by the latest method 
	 *  setSearchStrategy(). It returns an array of found solutions or null 
	 *  if there are no solutions. A user has to be careful not to overload 
	 *  the available memory because the number of found solutions could be 
	 *  huge. The process of finding all solutions can be also controlled by:
	 *  MaxNumberOfSolutions that is the total number of considered 
	 *  solutions that may be limited by the method setMaxNumberOfSolutions();
	 *  
	 *  TotalTimeLimit that is the number of seconds allocated for the 
	 *  entire optimization process. 
	 *  The problem state after the execution of this method is always 
	 *  restored. 
	 */
	public Solution[] findAllSolutions() {
		ArrayList<Solution> array = new ArrayList<Solution>();
		applyHeuristic();
		long startTime = System.currentTimeMillis();
		if (jsetlSolver.check() 
				&& (this.getMaxNumberOfSolutions() > 0 ||
						this.getMaxNumberOfSolutions() == -1)) {
			do { 
				if (getTimeLimit() > 0) {
					if (System.currentTimeMillis() - startTime > 
					getTimeLimit())
						break;				
				}
				Solution solution;
				try {
					solution = new Solution(this, 
							numberOfSolutions++);
				} catch (Failure e) {
					solution = null;
					e.printStackTrace();
				}
				array.add(solution);
			} while(jsetlSolver.nextSolution() && 
					((this.getMaxNumberOfSolutions() > 
					numberOfSolutions) ||
					this.getMaxNumberOfSolutions() == -1));
		}
		else return null;
		Solution[] result = new Solution[array.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = array.get(i);
			solutions.add(i, array.get(i));
		}
		return result;
	}
	
	/**
	 * This method attempts to find the solution that minimizes/maximizes the
	 *  objective variable. It uses the search strategy defined by the method 
	 *  setSearchStrategy(strategy). The optimization process can be also 
	 *  controlled by:
	 *  OptimizationTolarance that is a difference between optimal 
	 *  solutions - see setOptimizationTolarance()
	 *  MaxNumberOfSolutions that is the total number of considered 
	 *  solutions - may be limited by the method setMaxNumberOfSolutions()
	 *  TotalTimeLimit that is the number of seconds allocated for the 
	 *  entire optimization process. 
	 *  At the same time the time for one iteration inside optimization loop 
	 *  (a search of one solution) can be also limited by the use of the 
	 *  special type of search strategy.
	 *  The problem state after the execution of this method is always 
	 *  restored. All variables that were added to the problems (plus 
	 *  the objectiveVar) will have their assigned values saved inside the 
	 *  optimal solution. 
	 *  
	 *  @param objective Objective.MINIMIZE or Objective.MAXIMIZE
	 *  @param objectiveVar the variable that is being minimized/maximized.
	 *  
	 *  @return Solution if a solution is found, null if there are no 
	 *  solutions.
	 */
	public Solution findOptimalSolution(Objective objective, 
			javax.constraints.Var objectiveVar) {
		addObjective(objectiveVar);
		long startTime = System.currentTimeMillis();
		if(objectiveVar.getName().isEmpty())
			objectiveVar.setName("Objective"); 
		if(getProblem().getVar(objectiveVar.getName()) == null) {
			getProblem().add(objectiveVar);
		}
		Solution solution;
		applyHeuristic();
		try {
			IntLVar target = (IntLVar) objectiveVar.getImpl();
			if(objective.equals(Objective.MAXIMIZE))
				jsetlSolver.maximize(target);
			else
				jsetlSolver.minimize(target);

			solution = new Solution(this,
					numberOfSolutions);
		} catch (Failure e) {
			e.printStackTrace();
			return null;
		}

		if (solution != null)
			log("Optimal solution is found. Objective: "
					+solution.getValue(objectiveVar.getName()));
		return solution;
	}
	
	/**
	 * Auxiliary method that add to the JSetL solver the given ConstraintClass
	 * <code>c</code>.
	 * 
	 * @param c the ConstraintClass.
	 */
	public void addJSetLConstraint(ConstraintClass c) {
		jsetlSolver.add(c);
	}

	/**
	 * Auxiliary method that print the JSetL constraint store.
	 */
	public void showStore() {
		jsetlSolver.showStore();
	}

    /**
     * Getter method for the JSetL SolverClass.
     * 
     * @return the JSetL SolverClass.
     */
	public SolverClass getSolverClass() {
		return jsetlSolver;
	}
	
	/**
	 * Auxiliary method that add a given JSR331 constraint to the
	 * JSetL solver.
	 * 
	 * @param constraint the JSR331 constraint.
	 */
	public void add(Constraint constraint) {
		jsetlSolver.add((constraint).getConstraint());
	}
	
	/**
	 * Creates a solution iterator that allows a user to search and navigate
	 * through multiple solutions.
	 * 
	 * @return a solution iterator.
	 */
	public SolutionIterator solutionIterator() {
		return new SolutionIterator(this);
	}

	@Override
	public boolean applySolution(javax.constraints.Solution solution) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void trace(javax.constraints.Var var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(javax.constraints.Var[] vars) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(javax.constraints.VarSet setVar) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * True if the problem has an alternative solution.
	 * 
	 * @return a boolean value, true if a next solution exists, false otherwise.
	 */
	public boolean hasNext() {
		return jsetlSolver.nextSolution();
	}


}

