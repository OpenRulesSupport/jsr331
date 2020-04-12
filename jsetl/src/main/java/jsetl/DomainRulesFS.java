package jsetl;

import jsetl.annotation.NotNull;
import jsetl.exception.Fail;
import jsetl.exception.Failure;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Instances of this class provide methods to update and deal with domains of {@code SetLVar}s when solving
 * constraints like equality, inclusion, union, labeling,...
 */
class DomainRulesFS {

    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Reference to an instance of solver.
     */
    private final Solver solver;

    /**
     * Handler for equality constraints solutions.
     */
    private final RwRulesEq eqHandler;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Create an instance of domain rules over finite sets using the given solver reference.
     * @param solver reference to the solver.
     */
    protected
    DomainRulesFS(@NotNull Solver solver) {
        assert solver != null;
        this.solver = solver;
        this.eqHandler = new RwRulesEq(solver);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Domain rule used to make the domain of {@code setLVar} smaller by intersecting it with {@code domain}.
     * @param setLVar setLVar whose domain is to update.
     * @param domain setLVar whose intersection with {@code setLVar.getDomain()} will be the new domain.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if the intersection is empty.
     */
    protected void
    domainRule(@NotNull SetLVar setLVar, @NotNull SetInterval domain, @NotNull AConstraint aConstraint)
      {
        assert setLVar != null;
        assert domain != null;
        assert aConstraint != null;

        SetInterval oldDomain = setLVar.getDomain();
        SetInterval newDomain = oldDomain.intersect(domain);
        if (!oldDomain.equals(newDomain))
            updateDomains(setLVar, newDomain, aConstraint);
    }

    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param intLVar element.
     * @param setLVar set.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if some of the updated domains become empty.
     */
    protected void
    inRule(@NotNull IntLVar intLVar, @NotNull SetLVar setLVar, @NotNull AConstraint aConstraint)
      {
        assert intLVar != null;
        assert setLVar != null;
        assert aConstraint != null;

        if (setLVar.isBound())
            inRule(intLVar, setLVar.getValue(), aConstraint);
        else if (intLVar.isBound()) {
            Integer value = (Integer) intLVar.getValue();
            SetInterval dom = setLVar.getDomain();
            MultiInterval newGlb = dom.getGlb();
            if (newGlb.add(value))
                updateDomains(setLVar, new SetInterval(newGlb, dom.getLub()), aConstraint);
            aConstraint.setSolved(true);
        }
        else {
            DomainRulesFD handler = new DomainRulesFD(solver);
            handler.domainRule(intLVar, setLVar.getDomain().getLub(), aConstraint);
        }
    }

    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param intLVar element.
     * @param setLVar set.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if some of the updated domains become empty.
     */
    protected void
    ninRule(@NotNull IntLVar intLVar, @NotNull SetLVar setLVar, @NotNull AConstraint aConstraint)
      {
        assert intLVar != null;
        assert setLVar != null;
        assert aConstraint != null;

        if (setLVar.isBound())
            ninRule(intLVar, setLVar.getValue(), aConstraint);
        else if (intLVar.isBound()) {
            Integer value = (Integer) intLVar.getValue();
            SetInterval dom = setLVar.getDomain();
            MultiInterval newLub = dom.getLub();
            if (newLub.remove(value))
                updateDomains(setLVar, new SetInterval(dom.getGlb(), newLub), aConstraint);
            aConstraint.setSolved(true);
        }
        else {
            DomainRulesFD handler = new DomainRulesFD(solver);
            MultiInterval dom = setLVar.getDomain().getGlb().complement();
            handler.domainRule((IntLVar) intLVar, dom, aConstraint);
        }
    }

    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param x first set.
     * @param y second set.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if some of the updated domains become empty.
     */
    protected void
    eqRule(@NotNull SetLVar x, @NotNull SetLVar y, @NotNull AConstraint aConstraint)
      {
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        domainRule(x, y.getDomain(), aConstraint);
        aConstraint.setSolved(true);
    }

    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param setLVar first set.
     * @param multiInterval second set.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if the updated domain becomes empty.
     */
    protected void
    eqRule(@NotNull SetLVar setLVar, @NotNull MultiInterval multiInterval, @NotNull AConstraint aConstraint) {
        assert setLVar != null;
        assert multiInterval != null;
        assert aConstraint != null;

        if (setLVar.getDomain().contains(multiInterval))
            setLVar.setDomain(new SetInterval(multiInterval));
        else
            solver.fail(aConstraint);
    }
    
    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param x complement set.
     * @param y complemented set.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if some of the updated domains become empty.
     */
    protected void
    complRule(@NotNull SetLVar x, @NotNull SetLVar y, @NotNull AConstraint aConstraint)
      {
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        if (x.isBound() && y.isBound())
            if (x.getValue().equals(lubComplement(y))) {
                aConstraint.setSolved(true);
                return;
            }
            else
                solver.fail(aConstraint);
        if (x.equals(y) && !SetInterval.universe().isEmpty())
            solver.fail(aConstraint);
        MultiInterval newGlb = lubComplement(x);
        MultiInterval newLub = glbComplement(x);
        SetInterval newDomain = new SetInterval(newGlb, newLub);
        domainRule(y, newDomain, aConstraint);
        newGlb = lubComplement(y);
        newLub = glbComplement(y);
        newDomain = new SetInterval(newGlb, newLub);
        domainRule(x, newDomain, aConstraint);
    }

    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param x first set.
     * @param y second set.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if some of the updated domains become empty.
     */
    protected void
    disjRule(@NotNull SetLVar x, @NotNull SetLVar y, @NotNull AConstraint aConstraint)
      {
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        if (x.isBound())
            disjRule(y, x.getValue(), aConstraint);
        else
            if (y.isBound())
                disjRule(x, y.getValue(), aConstraint);
        if (x.equals(y))
            eqHandler.eq(new AConstraint(Environment.eqCode, x,
                    new MultiInterval()));
        SetInterval dom = new SetInterval(y.getDomain().getGlb(), 
                                          y.getDomain().getLub()
                                    .diff(x.getDomain().getGlb()));
        domainRule(y, dom, aConstraint);
        dom = new SetInterval(x.getDomain().getGlb(), 
                              x.getDomain().getLub()
                        .diff(y.getDomain().getGlb()));
        domainRule(x, dom, aConstraint);
        if (x.isBound() || y.isBound())
            aConstraint.setSolved(true);
    }

    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param x first set.
     * @param y second set.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if the updated domain becomes empty.
     */
    protected void
    disjRule(@NotNull SetLVar x, @NotNull MultiInterval y, @NotNull AConstraint aConstraint)
      {
        assert x != null;
        assert y != null;
        assert aConstraint != null;
        if (x.isBound())
            if (x.getValue().intersect(y).isEmpty())
                aConstraint.setSolved(true);
            else
                solver.fail(aConstraint);
        else {
            SetInterval dom = new SetInterval(x.getDomain().getGlb(), 
                                              x.getDomain()
                                              .getLub().diff(y));
            domainRule(x, dom, aConstraint);
            aConstraint.setSolved(true);
        }
    }

    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param x first set.
     * @param y second set.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if some of the updated domains become empty.
     */
    protected void
    subsetRule(@NotNull SetLVar x, @NotNull SetLVar y, @NotNull AConstraint aConstraint)
      {
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        if (y.isBound())
            if(x.isBound())
                if (x.getValue().subset(y.getValue())) {
                    aConstraint.setSolved(true);
                    return;
                }
                else
                    solver.fail(aConstraint);
            else
                subsetRule(x, y.getValue(), aConstraint);
        if (x.card().equals(y.card())) {
            eqHandler.eq(new AConstraint(Environment.eqCode, x, y));
            aConstraint.setSolved(true);
            return;
        }
        SetInterval domX = x.getDomain();
        SetInterval domY = y.getDomain();
        if (domX.getLub().subset(domY.getGlb())) {
            aConstraint.setSolved(true);
            return;
        }
        SetInterval dom = new SetInterval(x.getDomain().getGlb()
                                   .union(y.getDomain().getGlb()), 
                                          y.getDomain().getLub());
        domainRule(y, dom, aConstraint);
        dom = new SetInterval(x.getDomain().getGlb(),
                              x.getDomain().getLub()
                   .intersect(y.getDomain().getLub()));
        domainRule(x, dom, aConstraint);
        if (x.isBound() || y.isBound())
            aConstraint.setSolved(true);
    }

    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param x first set.
     * @param y second set.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if the updated domain becomes empty.
     */
    protected void
    subsetRule(@NotNull SetLVar x, @NotNull MultiInterval y, @NotNull AConstraint aConstraint)
      {
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        if (x.isBound())
            if (x.getValue().subset(y))
                aConstraint.setSolved(true);
            else
                solver.fail(aConstraint);
        else {
            SetInterval domX = x.getDomain();
            SetInterval dom = new SetInterval(domX.getGlb(), 
                                              y.intersect(domX.getLub()));
            domainRule(x, dom, aConstraint);
            aConstraint.setSolved(true);
        }
    }
    
    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param set set.
     * @param cardinality size of the set.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if some of the updated domains become empty.
     */
    protected void
    sizeRule(@NotNull SetLVar set, @NotNull IntLVar cardinality, @NotNull AConstraint aConstraint)
      {
        assert set != null;
        assert cardinality != null;
        assert aConstraint != null;

        if (set.isBound()) {
            int size = set.getValue().size();
            eqHandler.eq(new AConstraint(Environment.eqCode, cardinality, size));
            solver.storeUnchanged = false;
            aConstraint.setSolved(true);
        }
        else if (cardinality.isBound()) {
            MultiInterval glb = set.getDomain().getGlb();
            MultiInterval lub = set.getDomain().getLub();
            int value = cardinality.getValue();
            if (value == glb.size()) { 
                eqHandler.eq(new AConstraint(Environment.eqCode, set, glb));
                solver.storeUnchanged = false;
                aConstraint.setSolved(true);
            }
            else if (value == lub.size()) {
                eqHandler.eq(new AConstraint(Environment.eqCode, set, lub));
                solver.storeUnchanged = false;
                aConstraint.setSolved(true);
            }
        }
    }
    
    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param z result of intersection.
     * @param x first argument of intersection.
     * @param y second argument of intersection.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if some of the updated domains become empty.
     */
    protected void
    intersRule(@NotNull SetLVar z, @NotNull SetLVar x, @NotNull SetLVar y, @NotNull AConstraint aConstraint)
      {
        assert z != null;
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        if (x.equals(y)) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z, y));
            aConstraint.setSolved(true);
            return;
        }
        if (x.equals(z) || y.equals(z)) {
            aConstraint.setSolved(true);
            return;
        }
        if (x.isBound() && y.isBound()) {
            MultiInterval valX = x.getValue();
            MultiInterval valY = y.getValue();
            eqHandler.eq(new AConstraint(Environment.eqCode, z,
                    valX.intersect(valY)));
            aConstraint.setSolved(true);
            return;
        }
        SetInterval domX = x.getDomain();
        SetInterval domY = y.getDomain();
        SetInterval domZ = z.getDomain();
        if (domX.getLub().subset(domY.getGlb())) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z, x));
            aConstraint.setSolved(true);
            return;
        }
        if (domY.getLub().subset(domX.getGlb())) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z, y));
            aConstraint.setSolved(true);
            return;
        }
        SetInterval dom = new SetInterval (
                domX.getGlb().intersect(domY.getGlb()),
                domZ.getLub()
        );
        domainRule(z, dom, aConstraint);
        if (z.isBound()) { 
            if (x.isBound()) {
                dom = new SetInterval (
                    domY.getGlb(),
                    domY.getLub().diff(x.getValue().diff(z.getValue()))
                );
                domainRule(y, dom, aConstraint);
            }
            if (y.isBound()) {
                dom = new SetInterval (
                    domX.getGlb(),
                    domX.getLub().diff(y.getValue().diff(z.getValue()))
                );
            domainRule(x, dom, aConstraint);
            }
        }
    }

    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param z result of union.
     * @param x first argument of union.
     * @param y second argument of union.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if some of the updated domains become empty.
     */
    protected void
    unionRule(@NotNull SetLVar z, @NotNull SetLVar x, @NotNull SetLVar y, @NotNull AConstraint aConstraint)
      {
        assert z != null;
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        if (x.equals(y)) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z, x));
            aConstraint.setSolved(true);
            return;
        }
        if (x.equals(z) || y.equals(z)) {
            aConstraint.setSolved(true);
            return;
        }
        if (x.isBound() && y.isBound()) {
            MultiInterval valX = x.getValue();
            MultiInterval valY = y.getValue();
            eqHandler.eq(new AConstraint(Environment.eqCode, z,
                    valX.union(valY)));
            aConstraint.setSolved(true);
            return;
        }
        SetInterval domX = x.getDomain();
        SetInterval domY = y.getDomain();
        SetInterval domZ = z.getDomain();
        if (domX.getLub().subset(domY.getGlb())) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z, y));
            aConstraint.setSolved(true);
            return;
        }
        if (domY.getLub().subset(domX.getGlb())) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z, x));
            aConstraint.setSolved(true);
            return;
        }
        SetInterval dom = new SetInterval (
                domZ.getGlb(),
                domX.getLub().union(domY.getLub())
        );
        domainRule(z, dom, aConstraint);
        if (z.isBound()) { 
            if (x.isBound()) {
                dom = new SetInterval (
                    domY.getGlb().union(z.getValue().diff(x.getValue())),
                    domY.getLub()
                ); 
                domainRule(y, dom, aConstraint);
            }
            if (y.isBound()) {
                dom = new SetInterval (
                    domX.getGlb().union(z.getValue().diff(y.getValue())),
                    domX.getLub()
                ); 
                domainRule(x, dom, aConstraint);
            }
        }
    }

    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param z result of difference.
     * @param x first argument of difference.
     * @param y second argument of difference.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if some of the updated domains become empty.
     */
    protected void
    diffRule(@NotNull SetLVar z, @NotNull SetLVar x, @NotNull SetLVar y, @NotNull AConstraint aConstraint)
      {
        assert z != null;
        assert x != null;
        assert y != null;
        assert aConstraint != null;

        if (x.equals(y)) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z,
                    new MultiInterval()));
            aConstraint.setSolved(true);
            return;
        }
        if (x.equals(z)) {
            aConstraint.setSolved(true);
            return;
        }
        if (x.isBound() && y.isBound()) {
            MultiInterval valX = x.getValue();
            MultiInterval valY = y.getValue();
            eqHandler.eq(new AConstraint(Environment.eqCode, z,
                    valX.diff(valY)));
            aConstraint.setSolved(true);
            return;
        }
        SetInterval domX = x.getDomain();
        SetInterval domY = y.getDomain();
        SetInterval domZ = z.getDomain();
        if (domX.getLub().intersect(
            domY.getLub()).isEmpty()) {
            eqHandler.eq(new AConstraint(Environment.eqCode, z, y));
            aConstraint.setSolved(true);
            return;
        }
        SetInterval dom;
        dom = new SetInterval (
            domZ.getGlb().union(domX.getGlb().diff(domY.getLub())),
            domZ.getLub()
        );
        domainRule(z, dom, aConstraint);
        if (z.isBound()) {
            if (x.isBound()) {
                dom = new SetInterval (
                    domY.getGlb().union(x.getValue().diff(z.getValue())),
                    domY.getLub()
                );
                domainRule(y, dom, aConstraint);
            }
            if (y.isBound()) {
                dom = new SetInterval (
                    domX.getGlb(),
                    domX.getLub().intersect(
                            domY.getLub().union(domZ.getLub()))
                );
                domainRule(x , dom, aConstraint);
            }
        }
    }

    /**
     * Labels the parameter set using the given labeling options.
     * @param setLVar set to label.
     * @param labelingOptions labeling options to use.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if the labeling fails.
     */
    protected void
    labelRule(@NotNull SetLVar setLVar, @NotNull LabelingOptions labelingOptions, @NotNull AConstraint aConstraint)
      {
        assert setLVar != null;
        assert labelingOptions != null;
        assert aConstraint != null;

        if (setLVar.isBound()) {
            aConstraint.setSolved(true);
            return;
        }
        if (!solver.storeUnchanged)
            return;
        switch (aConstraint.alternative) {
        case 0:
            SetInterval dom = setLVar.getDomain();
            MultiInterval tmp = dom.getLub().diff(dom.getGlb());
            if (tmp.isEmpty())
                return;
            int el = labelingOptions.getIntValue(tmp);
            aConstraint.argument3 = el;
            solver.backtracking.addChoicePoint(aConstraint);
            if (labelingOptions.valueForSetLVar == SetHeuristic.FIRST_IN) {
                dom.getGlb().add(el);
                updateDomains(setLVar, dom, aConstraint);
            }
            else {
                dom.getLub().remove(el);
                updateDomains(setLVar, dom, aConstraint);
            }
            return;
        case 1:
            aConstraint.alternative = 0;
            el = (Integer) aConstraint.argument3;
            dom = setLVar.getDomain();
            if (labelingOptions.valueForSetLVar == SetHeuristic.FIRST_IN) {
                dom.getLub().remove(el);
                updateDomains(setLVar, dom, aConstraint);
            }
            else {
                dom.getGlb().add(el);
                updateDomains(setLVar, dom, aConstraint);
            }
            labelRule(setLVar, labelingOptions, aConstraint);
        }
    }

    /**
     * Labels the variables in {@code setLVars} using the given labeling options.
     * @param setLVars list of sets to label. None of its entries can be {@code null}.
     * @param labelingOptions labeling options to use.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if the labeling fails.
     */
    protected void 
    labelRule(@NotNull ArrayList<SetLVar> setLVars, @NotNull LabelingOptions labelingOptions, @NotNull AConstraint aConstraint) {
        assert setLVars != null;
        assert labelingOptions != null;
        assert aConstraint != null;
        assert setLVars.stream().noneMatch(Objects::isNull);

        if (!solver.storeUnchanged)
            return;
        SetLVar var = labelingOptions.getSetVariable(setLVars);
        setLVars.remove(var);
        if(!solver.check(new AConstraint(Environment.labelCode, var, labelingOptions)))
            solver.fail(aConstraint);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PRIVATE METHODS //////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Updates the domain of {@code set} by setting it to the parameter {@code domain}.
     * Cardinality of {@code set} is updated too.
     * @param set set whose domain is to update.
     * @param domain new domain.
     * @param aConstraint constraint that asked the update.
     * @throws Fail if {@code domain} is empty or the resolution of equality fails.
     */
    private void
    updateDomains(@NotNull SetLVar set, @NotNull SetInterval domain, @NotNull AConstraint aConstraint)
              {
        assert set != null;
        assert domain != null;
        assert aConstraint != null;

        if (domain.isEmpty())
            solver.fail(aConstraint);
        set.setDomain(domain);
        MultiInterval glb = domain.getGlb();
        MultiInterval lub = domain.getLub();
        solver.storeUnchanged = false;
        if (domain.isSingleton()) {
            eqHandler.eq(new AConstraint(Environment.eqCode, set.card(),
                    glb.size()));
            eqHandler.eq(new AConstraint(Environment.eqCode, set, glb));
        }
        else {
            IntLVar newSize = new IntLVar(glb.size(), lub.size());
            eqHandler.eq(new AConstraint(Environment.eqCode, set.card(),
                    newSize));
        }
    }

    /**
     * Handles the parameter constraint and possibly updates the domain accordingly.
     * @param intLVar element.
     * @param multiInterval set.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if the updated domain becomes empty.
     */
    private void
    inRule(@NotNull IntLVar intLVar, @NotNull MultiInterval multiInterval, @NotNull AConstraint aConstraint)
              {
        assert intLVar != null;
        assert multiInterval != null;
        assert aConstraint != null;

        if (intLVar.isBound()) {
            if (!multiInterval.contains(intLVar.getValue()))
                solver.fail(aConstraint);
        }
        else {
            DomainRulesFD handler = new DomainRulesFD(solver);
            handler.domainRule(intLVar, multiInterval, aConstraint);
            solver.storeUnchanged = false;
        }
        aConstraint.setSolved(true);
    }

    /**
     * Handles the parameter constraint and possibly updates the domains accordingly.
     * @param intLVar element.
     * @param multiInterval multiInterval.
     * @param aConstraint atomic constraint that requested the application of the rule.
     * @throws Fail if the updated domain becomes empty.
     */
    private void
    ninRule(@NotNull IntLVar intLVar, @NotNull MultiInterval multiInterval, @NotNull AConstraint aConstraint)
              {
        assert intLVar != null;
        assert multiInterval != null;
        assert aConstraint != null;

        if (intLVar.isBound()) {
            if (multiInterval.contains(intLVar.getValue()))
                solver.fail(aConstraint);
        }
        else  {
            DomainRulesFD handler = new DomainRulesFD(solver);
            handler.domainRule(intLVar, multiInterval.complement(), aConstraint);
            solver.storeUnchanged = false;
        }
        aConstraint.setSolved(true);
    }

    /**
     * Constructs and returns a multi-interval which is the complement of the greatest
     * lower bound of the domain of the parameter {@code setLVar}.
     * @param setLVar a integer variable finite set.
     * @return the constructed multi-interval.
     */
    private static @NotNull MultiInterval
    glbComplement(@NotNull SetLVar setLVar) {
        assert setLVar != null;

        MultiInterval result = SetInterval.SUP.diff(setLVar.getDomain().getGlb());
        assert result != null;
        return result;
    }

    /**
     * Constructs and returns a multi-interval which is the complement of the least upper
     * bound of the domain of the parameter {@code setLVar}.
     * @param setLVar a integer variable finite set.
     * @return the constructed multi-interval.
     */
    private static @NotNull MultiInterval
    lubComplement(@NotNull SetLVar setLVar) {
        assert setLVar != null;

        MultiInterval result = SetInterval.SUP.diff(setLVar.getDomain().getLub());
        assert result != null;
        return result;
    }
}
