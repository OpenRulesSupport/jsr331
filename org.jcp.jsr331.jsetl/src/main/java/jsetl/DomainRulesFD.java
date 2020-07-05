package jsetl;

import jsetl.annotation.NotNull;
import jsetl.exception.Fail;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This class implements rules that modify the domains of integer logical variables
 * to handle atomic constraints like equality, multiplication and labeling.
 * The rules used to update the domains are those of <strong>CLP(FD)</strong>.
 */
class DomainRulesFD {

    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Reference to the solver.
     */
    private final SolverClass solver;

    /**
     * Reference to the equality handler.
     */
    private final RwRulesEq eqHandler;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs a new {@code DomainRulesFD} with the given solver reference.
     * @param solver reference to the solver.
     */
    protected DomainRulesFD(final @NotNull SolverClass solver) {
        assert solver != null;

        this.solver = solver;
        this.eqHandler = new RwRulesEq(solver);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Updates domain of a integer logical variable.
     * @param intLVar the variable whose domain should be updated.
     * @param domain the new domain.
     * @param aConstraint the atomic constraint which asks to update the domain (needed in case of failure).
     * @throws Fail if the new domain is empty, i.e. {@code intLVar} has no possible values.
     */
    protected void
    updateDomain(@NotNull IntLVar intLVar, @NotNull MultiInterval domain, @NotNull AConstraint aConstraint)
      {
        assert intLVar != null;
        assert domain != null;
        assert aConstraint != null;

        if (domain.isEmpty())
            solver.fail(aConstraint);
        if (domain.isSingleton())
            eqHandler.eq(new AConstraint(Environment.eqCode, intLVar,
                    domain.getGlb()));
        else {
            intLVar.setDomain(domain);
            solver.storeUnchanged = false;
        }
    }

    /**
     * Updates the domain of {@code intLVar} by intersecting it with {@code domain}.
     * @param intLVar the variable whose domain is to be update.
     * @param domain the second argument of the intersection.
     * @param aConstraint the atomic constraint which requires the operation (needed in case of failure).
     * @throws Fail if the new domain is empty.
     */
    protected void
    domainRule(@NotNull IntLVar intLVar, @NotNull MultiInterval domain, @NotNull AConstraint aConstraint)
      {
        assert intLVar != null;
        assert domain != null;
        assert aConstraint != null;

        MultiInterval oldDomain = intLVar.getDomain();
        MultiInterval newDomain = oldDomain.intersect(domain);
        if (newDomain.size() < oldDomain.size())
            updateDomain(intLVar, newDomain, aConstraint);
    }

    /**
     * Handles equality between {@code x} and {@code y} by setting the domain of {@code x} as the intersection with the domain
     * of {@code y}.
     * @param x the first argument of the equality.
     * @param y the second argument of the equality.
     * @param aConstraint the atomic constraint which requires the operations, needed in case of failure.
     * @throws Fail if the intersection of the domains is empty.
     */
    protected void
    eqRule(@NotNull IntLVar x, @NotNull IntLVar y, @NotNull AConstraint aConstraint)
      {
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        domainRule(x, y.getDomain(), aConstraint);
    }
    

    /**
     * Handles equality between {@code x} and {@code k}
     * by setting the domain of {@code x} to {{@code k}}, if {@code k} is contained in it.
     * @param x the first argument of the equality.
     * @param k the second argument of the equality.
     * @param aConstraint the atomic constraint which requires the operations, needed in case of failure
     * @throws Fail if {@code k} is not in the domain of {@code x}.
     */
    protected void
    eqRule(@NotNull IntLVar x, @NotNull Integer k, @NotNull AConstraint aConstraint) {
        assert x != null;
        assert k != null;
        assert aConstraint != null;

        if (x.getDomain().contains(k))
            x.setDomain(new MultiInterval(k));
        else
            solver.fail(aConstraint);
    }

    /**
     * Handles the parameter constraint and can update the domains of the variables accordingly.
     * @param x the argument which must be lower than the other.
     * @param y the argument which must be greater than the other.
     * @param aConstraint the atomic constraint which requires the operations.
     * @throws Fail if some of the updated domains becomes empty.
     */
    protected void 
    ltRule(@NotNull IntLVar x, @NotNull IntLVar y, @NotNull AConstraint aConstraint)
      {
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        if (x.equals(y))
            solver.fail(aConstraint);
        MultiInterval domX = x.getDomain();
        MultiInterval domY = y.getDomain();
        domainRule(x, new MultiInterval(Interval.INF, domY.getLub() - 1), aConstraint);
        domainRule(y, new MultiInterval(domX.getGlb() + 1, Interval.SUP), aConstraint);
    }

    /**
     * Handles the parameter constraint and can update the domains of the variables accordingly.
     * @param x the argument which must be lower than the other.
     * @param k the argument which must be greater than the other.
     * @param aConstraint the atomic constraint which requires the operations.
     * @throws Fail if the updated domain becomes empty.
     */
    protected void 
    ltRule(@NotNull IntLVar x, @NotNull Integer k, @NotNull AConstraint aConstraint)
      {
        assert x != null;
        assert k != null;
        assert aConstraint != null;

        domainRule(x, new MultiInterval(Interval.INF, k - 1), aConstraint);
    }

    /**
     * Handles the parameter constraint and can update the domains of the variables accordingly.
     * @param x the argument which must be lower than or equal to the other.
     * @param y the argument which must be greater than or equal to the other.
     * @param aConstraint the atomic constraint which requires the operations, needed in case of failure.
     * @throws Fail if some of the updated domains becomes empty.
     */
    protected void 
    leRule(@NotNull IntLVar x, @NotNull IntLVar y, @NotNull AConstraint aConstraint)
      {
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        MultiInterval domX = x.getDomain();
        MultiInterval domY = y.getDomain();
        domainRule(x, new MultiInterval(Interval.INF, domY.getLub()), aConstraint);
        domainRule(y, new MultiInterval(domX.getGlb(), Interval.SUP), aConstraint);
    }

    /**
     * Handles the parameter constraint and can update the domains of the variables accordingly.
     * @param x the argument which must be lower than or equal to the other.
     * @param k the argument which must be greater than or equal to the other.
     * @param aConstraint the atomic constraint which requires the operations.
     * @throws Fail if the updated domain becomes empty.
     */
    protected void 
    leRule(@NotNull IntLVar x, @NotNull Integer k, @NotNull AConstraint aConstraint)
      {
        assert x != null;
        assert k != null;
        assert aConstraint != null;

        domainRule(x, new MultiInterval(Interval.INF, k), aConstraint);
    }

    /**
     * Handles the parameter constraint and can update the domains of the variable accordingly.
     * @param x the first argument of the inequality.
     * @param k the second argument of the inequality.
     * @param aConstraint the atomic constraint which requires the operations.
     * @throws Fail if the updated domain becomes empty.
     */
    protected void 
    neqRule(@NotNull IntLVar x, @NotNull Integer k, @NotNull AConstraint aConstraint)
      {
        assert x != null;
        assert k != null;
        assert aConstraint != null;

        MultiInterval dom = x.getDomain();
        if (dom.remove(k))
            updateDomain(x, dom, aConstraint);
    }
    
    /**
     * Handles the parameter constraint and can update the domains of the variables accordingly.
     * @param z the arguments which should be equal to the sum.
     * @param x one of the arguments of the sum.
     * @param y the second argument of the sum.
     * @param aConstraint the atomic constraint which requires the operations.
     * @throws Fail if some of the updated domains becomes empty.
     */
    protected void
    sumRule(@NotNull IntLVar z, @NotNull IntLVar x, @NotNull IntLVar y, @NotNull AConstraint aConstraint)
      {
        assert z != null;
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        domainRule(x, sub(z, y), aConstraint);
        domainRule(y, sub(z, x), aConstraint);
        domainRule(z, sum(x, y), aConstraint);
        if (x.isBound() && y.isBound() && z.isBound())
            aConstraint.setSolved(true);
    }

    /**
     * Handles the parameter constraint and can update the domains of the variables accordingly.
     * @param z the arguments which should be equal to the sum.
     * @param x one of the arguments of the sum.
     * @param k the second argument of the sum.
     * @param aConstraint the atomic constraint which requires the operations.
     * @throws Fail if some of the updated domains becomes empty.
     */
    protected void
    sumRule(@NotNull IntLVar z, @NotNull IntLVar x, @NotNull Integer k, @NotNull AConstraint aConstraint)
      {
        assert z != null;
        assert x != null;
        assert k != null;
        assert aConstraint != null;

        if (x.isBound()) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z,
                    x.getValue() + k));
            aConstraint.setSolved(true);
            return;
        }
        if (z.isBound()) {
            eqHandler.eq(new AConstraint(Environment.eqCode, x,
                    z.getValue() - k));
            aConstraint.setSolved(true);
            return;
        }
        domainRule(x, z.getDomain().sub(k), aConstraint);
        domainRule(z, x.getDomain().sum(k), aConstraint);
        if (x.isBound() && z.isBound())
            aConstraint.setSolved(true);
    }

    /**
     * Handles the parameter constraint and can update the domains of the variables accordingly.
     * @param z the arguments which should be equal to the multiplication.
     * @param x one of the arguments of the multiplication.
     * @param y the second argument of the multiplication.
     * @param aConstraint the atomic constraint which requires the operations.
     * @throws Fail if some of the updated domains becomes empty.
     */
    protected void
    mulRule(@NotNull IntLVar z, @NotNull IntLVar x, @NotNull IntLVar y, @NotNull AConstraint aConstraint)
      {
        assert z != null;
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        if (z.isBound() && x.equals(y)) {
            double k = Math.sqrt(z.getValue());
            if (k % 1 == 0) {
                int n = (int) k;
                MultiInterval mi = new MultiInterval(-n);
                mi.add(n);
                domainRule(x, mi, aConstraint);
                if (x.isBound() && y.isBound() && z.isBound())
                    aConstraint.setSolved(true);
                return;
            }
            else
                solver.fail(aConstraint);
        }
        domainRule(x, div(z, y), aConstraint);
        domainRule(y, div(z, x), aConstraint);
        domainRule(z, mul(x, y), aConstraint);
        if (x.isBound() && y.isBound() && z.isBound())
            aConstraint.setSolved(true);
    }

    /**
     * Handles the parameter constraint and can update the domains of the variables accordingly.
     * @param z the arguments which should be equal to the multiplication.
     * @param x one of the arguments of the multiplication.
     * @param k the second argument of the multiplication.
     * @param aConstraint the atomic constraint which requires the operations.
     * @throws Fail if some of the updated domains becomes empty.
     */
    protected void
    mulRule(@NotNull IntLVar z, @NotNull IntLVar x, @NotNull Integer k, @NotNull AConstraint aConstraint)
      {
        assert z != null;
        assert x != null;
        assert k != null;
        assert aConstraint != null;

        if (x.isBound()) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z,
                    x.getValue() * k));
            aConstraint.setSolved(true);
            return;
        }
        if (z.isBound()) {
            double y  = (double) z.getValue() / k;
            if (y % 1 == 0) {
                eqHandler.eq(new AConstraint(Environment.eqCode, x, (int) y));
                aConstraint.setSolved(true);
                return;
            }
            else
                solver.fail(aConstraint);
        }
        domainRule(x, z.getDomain().div(k), aConstraint);
        domainRule(z, x.getDomain().mul(k), aConstraint);
        if (x.isBound() && z.isBound())
            aConstraint.setSolved(true);
    }

    /**
     * Handles the parameter constraint and can update the domains of the variables accordingly.
     * @param z the arguments which should be equal to the module.
     * @param x one of the arguments of the module.
     * @param y the second argument of the module.
     * @param aConstraint the atomic constraint which requires the operations.
     * @throws Fail if some of the updated domains becomes empty.
     */
    protected void
    modRule(@NotNull IntLVar z, @NotNull IntLVar x, @NotNull IntLVar y, @NotNull AConstraint aConstraint)
      {
        assert z != null;
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        if (y.isBound()) {
            modRule(z, x, y.getValue(), aConstraint);
            return;
        }
        MultiInterval domY = y.getDomain();
        int t = Math.max(Math.abs(domY.getGlb()), Math.abs(domY.getLub())) - 1;
        int glbX = x.getDomain().getGlb();
        int lubX = x.getDomain().getLub();
        int glb = Math.max(glbX, -t);
        int lub = Math.min(lubX, t);
        if (lubX < 0)
            domainRule(z, new MultiInterval(glb, 0), aConstraint);
        else if (glbX >= 0)
            domainRule(z, new MultiInterval(0, lub), aConstraint);
        else
            domainRule(z, new MultiInterval(glb, lub), aConstraint);
        if (x.isBound() && y.isBound() && z.isBound())
            aConstraint.setSolved(true);
    }

    /**
     * Handles the parameter constraint and can update the domains of the variables accordingly.
     * @param z the arguments which should be equal to the module.
     * @param x one of the arguments of the module.
     * @param k the second argument of the module.
     * @param aConstraint the atomic constraint which requires the operations.
     * @throws Fail if the updated domain becomes empty.
     */
    protected void
    modRule(@NotNull IntLVar z, @NotNull IntLVar x, @NotNull Integer k, @NotNull AConstraint aConstraint)
      {
        assert z != null;
        assert x != null;
        assert k != null;
        assert aConstraint != null;

        if (x.isBound()) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z,
                    x.getValue() % k));
            aConstraint.setSolved(true);
            return;
        }
        int t = Math.abs(k) - 1;
        int glbX = x.getDomain().getGlb();
        int lubX = x.getDomain().getLub();
        int glb = Math.max(glbX, -t);
        int lub = Math.min(lubX, t);
        if (lubX < 0)
            domainRule(z, new MultiInterval(glb, 0), aConstraint);
        else if (glbX >= 0)
            domainRule(z, new MultiInterval(0, lub), aConstraint);
        else
            domainRule(z, new MultiInterval(glb, lub), aConstraint);
        if (x.isBound() && z.isBound())
            aConstraint.setSolved(true);
    }

    /**
     * Handles the parameter constraint and can update the domains of the variables accordingly.
     * @param x the argument of the module.
     * @param y the argument that must be equal to the absolute value of the first argument.
     * @param aConstraint the atomic constraint which requires the operations.
     * @throws Fail if some of the updated domains becomes empty.
     */
    protected void
    absRule(@NotNull IntLVar y, @NotNull IntLVar x, @NotNull AConstraint aConstraint)
      {
        assert y != null;
        assert x != null;
        assert aConstraint != null;

        if (x.isBound()) {
            eqHandler.eq(new AConstraint(Environment.eqCode, y,
                    Math.abs(x.getValue())));
            aConstraint.setSolved(true);
            return;
        }
        if (y.isBound()) {
            int a = y.getValue();
            MultiInterval dom = new MultiInterval(-a);
            dom.add(a);
            domainRule(x, dom, aConstraint);
            aConstraint.setSolved(true);
            return;
        }
        MultiInterval domX = x.getDomain();
        domainRule(y, domX.union(domX.opposite()), aConstraint);
        MultiInterval domY = y.getDomain();
        domainRule(x, domY.union(domY.opposite()), aConstraint);
        if (x.isBound() && y.isBound())
            aConstraint.setSolved(true);
    }

    /**
     * Non-deterministically labels the variable {@code intLVar} according to the labeling options {@code labelingOptions}.
     * @param intLVar the variable to label.
     * @param labelingOptions labeling option.
     * @param aConstraint the atomic constraint which requires the operations.
     * @throws Fail if the labeling does not succeed.
     */
    protected void 
    labelRule(@NotNull IntLVar intLVar, @NotNull LabelingOptions labelingOptions, @NotNull AConstraint aConstraint)
      {
        assert intLVar != null;
        assert labelingOptions != null;
        assert aConstraint != null;

        if (intLVar.isBound()) {
            aConstraint.setSolved(true);
            return;
        }
        if (!solver.storeUnchanged)
            return;
        MultiInterval dom = intLVar.getDomain();
        if (dom.isEmpty())
            return;
        switch (aConstraint.alternative) {
        case 0:
            int el = labelingOptions.getIntValue(dom);
            MultiInterval dom1 = new MultiInterval(dom.getGlb(), el - 1);
            MultiInterval dom2 = new MultiInterval(el + 1, dom.getLub());
            if (dom1.size() <= dom2.size()) {
                aConstraint.argument3 = dom2;
                solver.backtracking.addChoicePoint(aConstraint);
                if (!dom1.isEmpty()) {
                    aConstraint.argument3 = dom1;
                    solver.backtracking.addChoicePoint(aConstraint);
                }
            }
            else {
                if (!dom1.isEmpty()) {
                    aConstraint.argument3 = dom1;
                    solver.backtracking.addChoicePoint(aConstraint);
                }
                if (!dom2.isEmpty()) {
                    aConstraint.argument3 = dom2;
                    solver.backtracking.addChoicePoint(aConstraint);
                }
            }
            aConstraint.argument1 = intLVar;
            aConstraint.constraintKindCode = Environment.eqCode;
            aConstraint.argument2 = el;
            eqHandler.eq(aConstraint);
            return;
        case 1:
            --aConstraint.alternative;
            domainRule(intLVar, (MultiInterval) aConstraint.argument3, aConstraint);
            labelRule(intLVar, labelingOptions, aConstraint);
        }

    }

    /**
     * Labels each variable in {@code intLVars}, chosen according to labeling options {@code labelingOptions}.
     * @param intLVars intLVars to label. None of its entries can be {@code null}.
     * @param labelingOptions labeling option.
     * @param aConstraint the atomic constraint which requires the operations.
     * @throws Fail if the labeling does not succeed.
     */
    protected void 
    labelRule(@NotNull ArrayList<IntLVar> intLVars, @NotNull LabelingOptions labelingOptions, @NotNull AConstraint aConstraint)
      {
        assert intLVars != null;
        assert labelingOptions != null;
        assert aConstraint != null;
        assert intLVars.stream().noneMatch(Objects::isNull);

        if (!solver.storeUnchanged)
            return;
        IntLVar var = labelingOptions.getIntVariable(intLVars);
        intLVars.remove(var);
        if(!solver.check(new AConstraint(Environment.labelCode, var, labelingOptions)))
            solver.fail(aConstraint);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PRIVATE METHODS //////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Computes and returns the result of the sum of the convex closure of the domains of {@code x} and {@code y}.
     * @param x an integer logical variable.
     * @param y an integer logical variable.
     * @return the resulting multi-interval.
     */
    private static @NotNull MultiInterval
    sum(@NotNull IntLVar x, @NotNull IntLVar y) {
        assert x != null;
        assert y != null;

        Interval chX = x.getDomain().convexClosure();
        Interval chY = y.getDomain().convexClosure();
        return new MultiInterval(chX.sum(chY));
    }

    /**
     * Computes and returns the result of the subtraction of the convex closure of the domains of {@code x} and {@code y}.
     * @param x an integer logical variable.
     * @param y an integer logical variable.
     * @return the resulting multi-interval.
     */
    private static @NotNull MultiInterval
    sub(@NotNull IntLVar x, @NotNull IntLVar y) {
        assert x != null;
        assert y != null;

        Interval chX = x.getDomain().convexClosure();
        Interval chY = y.getDomain().convexClosure();
        return new MultiInterval(chX.sub(chY));
    }

    /**
     * Computes and returns the result of the multiplication of the convex closure of the domains of {@code x} and {@code y}.
     * @param x an integer logical variable.
     * @param y an integer logical variable.
     * @return the resulting multi-interval.
     */
    private static @NotNull MultiInterval
    mul(@NotNull IntLVar x, @NotNull IntLVar y) {
        assert x != null;
        assert y != null;

        Interval chX = x.getDomain().convexClosure();
        Interval chY = y.getDomain().convexClosure();
        return new MultiInterval(chX.mul(chY));
    }

    /**
     * Computes and returns the result of the division of the convex closure of the domains of {@code x} and {@code y}.
     * @param x an integer logical variable.
     * @param y an integer logical variable.
     * @return the resulting multi-interval.
     */
    private static @NotNull MultiInterval
    div(@NotNull IntLVar x, @NotNull IntLVar y) {
        assert x != null;
        assert y != null;

        Interval chX = x.getDomain().convexClosure();
        Interval chY = y.getDomain().convexClosure();
        return new MultiInterval(chX.div(chY));
    }

}
