package javax.constraints.impl.search;

import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.valselector.MidVal;
import choco.cp.solver.search.integer.valselector.MinVal;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.*;
import choco.kernel.solver.branch.AbstractIntBranchingStrategy;
import choco.kernel.solver.branch.VarSelector;
import choco.kernel.solver.search.ValIterator;
import choco.kernel.solver.search.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

import javax.constraints.Problem;
import javax.constraints.Solver;
import javax.constraints.impl.Var;
import java.util.Random;

public class SearchStrategy extends AbstractSearchStrategy {

    AbstractIntBranchingStrategy chocoStrategy;

    public SearchStrategy(Solver solver) {
        super(solver);
//		CPSolver s =
//			((javax.constraints.impl.search.Solver)solver).getChocoSolver();
        javax.constraints.Var[] vars = solver.getProblem().getVars();
        setVars(vars);
//        IntegerVariable[] chocoVars = new IntegerVariable[vars.length];
//        for (int i = 0; i < chocoVars.length; i++) {
//            Var var = (Var) vars[i];
//            chocoVars[i] = var.getChocoVar();
//		}
        // <cpru>: should not be created before being sure the scope of variables is defined
//		VarSelector varSel = new MinDomain(s,s.getVar(chocoVars));
//		VarSelector varSel = new RandomIntVarSelector(s);
//		ValIterator valHeuri = new IncreasingDomain();
//		chocoStrategy = new AssignVar(varSel,valHeuri);
            // new DomOverWDegBranching
        }


    public AbstractIntBranchingStrategy getChocoStrategy() {
        CPSolver s = ((javax.constraints.impl.search.Solver) solver).getChocoSolver();
        javax.constraints.VarSelector varSel = getVarSelector();
        javax.constraints.ValueSelector valSel = getValueSelector();

        VarSelector chocoVarSel = null;
        ValSelector chocoValueSel = null;
        ValIterator chocoValueIter = null;
        Problem p = solver.getProblem();
        IntDomainVar[] chocoVars = new IntDomainVar[vars.length];
        for (int i = 0; i < chocoVars.length; i++) {
            Var var = (Var) vars[i];
            chocoVars[i] = s.getVar(var.getChocoVar());
		}

            /*
            * All var selection orders as defined in jsr331 interface VarSelector
            *
            * selection of variables in order of definition
            * INPUT_ORDER,
            * done
            *
            * smallest lower bound
            * MIN_VALUE,
            * done
            *
            * largest upper bound
            * MAX_VALUE,
            * done
            *
            * min size of domain, tie break undefined
            * MIN_DOMAIN,
            * done
            *
            * min size of domain, random tie break
            * MIN_DOMAIN_MIN_VALUE,
            * unsupported message
            *
            * min size of domain, random tie break
            * MIN_DOMAIN_RANDOM,
            * unsupported message
            *
            * random selection of variables
            * RANDOM,
            * done
            *
            * min size of domain as first criteria, tie break by degree
            * that is the number of attached constraints
            * MIN_DOMAIN_MAX_DEGREE,
            * unsupported message
            *
            * min value of fraction of domain size and degree
            * MIN_DOMAIN_OVER_DEGREE,
            * unsupported message
            *
            * min value of domain size over weighted degree
            * MIN_DOMAIN_OVER_WEIGHTED_DEGREE,
            * unsupported message
            *
            * largest number of recorded failures in attached constraints
            * MAX_WEIGHTED_DEGREE,
            * unsupported message
            *
            * largest impact, select variable which when assigned restricts
            * the domains of all other variables by the largest amount
            * MAX_IMPACT,
            *
            * largest number of attached constraints
            * MAX_DEGREE,
            * done
            *
            * largest difference between smallest and second smallest value in domain
            * MAX_REGRET,
            * done
            *
            * custom variable selector
            * CUSTOM
            */
            long seed = new Random().nextLong();
            if (varSel != null) {
                String messageUnsupportedVarSelection = " is not supported, default variable selection used instead";
                switch (varSel.getType()) {
                    case INPUT_ORDER:
                        chocoVarSel = new StaticVarOrder(s, chocoVars);
                        break;
                    case MIN_DOMAIN:
                        chocoVarSel = new MinDomain(s, chocoVars);
                        break;
                    case MIN_VALUE:
                        chocoVarSel = new MinValueDomain(s, chocoVars);
                        break;
                    case MAX_VALUE:
                        chocoVarSel = new MaxValueDomain(s, chocoVars);
                        break;
                    case RANDOM:
                        chocoVarSel = new RandomIntVarSelector(s, chocoVars, seed);
                        break;
                    case MAX_DEGREE:
                        chocoVarSel = new MostConstrained(s, chocoVars);
                        break;
                    case MAX_REGRET:
                        chocoVarSel = new MaxRegret(s, chocoVars);
                        break;
                    case MIN_DOMAIN_MIN_VALUE:
                        p.log("MIN_DOMAIN_MIN_VALUE" + messageUnsupportedVarSelection);
                        chocoVarSel = new RandomIntVarSelector(s, chocoVars, seed);
                        break;
                    case MIN_DOMAIN_RANDOM:
                        p.log("MIN_DOMAIN_RANDOM" + messageUnsupportedVarSelection);
                        chocoVarSel = new RandomIntVarSelector(s, chocoVars, seed);
                        break;
                    case MIN_DOMAIN_MAX_DEGREE:
                        p.log("MIN_DOMAIN_MAX_DEGREE" + messageUnsupportedVarSelection);
                        chocoVarSel = new RandomIntVarSelector(s, chocoVars, seed);
                        break;
                    case MIN_DOMAIN_OVER_DEGREE:
                        p.log("MIN_DOMAIN_OVER_DEGREE" + messageUnsupportedVarSelection);
                        chocoVarSel = new RandomIntVarSelector(s, chocoVars, seed);
                        break;
                    case MIN_DOMAIN_OVER_WEIGHTED_DEGREE:
                        p.log("MIN_DOMAIN_OVER_WEIGHTED_DEGREE" + messageUnsupportedVarSelection);
                        chocoVarSel = new RandomIntVarSelector(s, chocoVars, seed);
                        break;
                    case MAX_WEIGHTED_DEGREE:
                        p.log("MAX_WEIGHTED_DEGREE" + messageUnsupportedVarSelection);
                        chocoVarSel = new RandomIntVarSelector(s, chocoVars, seed);
                        break;
                    case MAX_IMPACT:
                        p.log("MAX_IMPACT" + messageUnsupportedVarSelection);
                        chocoVarSel = new RandomIntVarSelector(s, chocoVars, seed);
                        break;
                    default:
                        chocoVarSel = new RandomIntVarSelector(s, chocoVars, seed);
                        break;
                }
            } else {
                chocoVarSel = new RandomIntVarSelector(s, chocoVars, seed);
            }

            /*
            * All value selection orders as defined in jsr331 interface ValuSelector
            *
            * try values in increasing order one at a time
            * without removing failed values on backtracking
            * IN_DOMAIN,
            *
            * try values in increasing order, remove value on backtracking
            * MIN,
            *
            * try values in decreasing order, remove value on backtracking
            * MAX,
            *
            * try to alternate minimal and maximal values
            * MIN_MAX_ALTERNATE,
            *
            * try values in the middle of domain,
            * the closest to (min+max)/2
            * MIDDLE,
            *
            * try the median values first,
            * e.g if domain has 5 values, try the third value first
            * MEDIAN,
            *
            * try a random value
            * RANDOM,
            *
            * try a value which causes the smallest domain reduction in all other variables
            * MIN_IMPACT,
            *
            * custom selector
            * CUSTOM
            */


            if (valSel != null) {
                switch (valSel.getType()) {
                    case MIN:
                        chocoValueIter = new IncreasingDomain();
                        break;
                    case MAX:
                        chocoValueIter = new DecreasingDomain();
                        break;
                    case IN_DOMAIN:
                        chocoValueSel = new MinVal();
                        break;
                    case MIDDLE:
                        chocoValueSel = new MidVal();
                        break;
                    case RANDOM:
                        chocoValueSel = new RandomIntValSelector();
                        break;
                    default:
                        chocoValueIter = new IncreasingDomain();
                        break;
                }
            } else {
                chocoValueIter = new IncreasingDomain();
            }

            if (chocoValueSel == null) {
                return new AssignVar(chocoVarSel, chocoValueIter);
            } else {
                return new AssignVar(chocoVarSel, chocoValueSel);
            }
        }

    }
