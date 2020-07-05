package jsetl;

import jsetl.annotation.NotNull;

import java.util.*;

/**
 * Rewrite rules used to solve equality and inequality constraints.
 */
class RwRulesEq extends LibConstraintsRules {

    //////////////////////////////////////////////////////
    ////////////////// CONSTRUCTORS //////////////////////
    //////////////////////////////////////////////////////

    /**
     * Constructs an instance of rewrite rules for (in)equality and stores a reference to the solver.
     * @param solver reference to the solver.
     */
    public RwRulesEq(@NotNull SolverClass solver) {
        super(solver);
        assert solver != null;
    }


    //////////////////////////////////////////////////////
    ////////////////// PROTECTED METHODS /////////////////
    //////////////////////////////////////////////////////

    /**
     * Uses the appropriate method to solve the given atomic constraint.
     * @param aConstraint atomic constraint to solve.
     * @return {@code true} the constraint has been handled, {@code false} otherwise.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    @Override
    protected boolean solveConstraint(@NotNull AConstraint aConstraint){
        assert aConstraint != null;

        if (aConstraint.constraintKindCode == Environment.eqCode)
            eq(aConstraint);        // equality
        else if (aConstraint.constraintKindCode == Environment.neqCode)
            neq(aConstraint);       // inequality
        else if (aConstraint.constraintKindCode == Environment.selectCode)
            select(aConstraint);    // non-deterministic choice - internal constraint
        else
            return false;
        
        return true;
    }

    /**
     * Selects the appropriate method to call for solving equality constraints depending on the arguments types.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    protected void eq(@NotNull AConstraint aConstraint)   {
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument4 == null;

        manageEquChains(aConstraint);

        if (aConstraint.argument3 instanceof LSet)
            eqSet((LSet)aConstraint.argument1, (LSet)aConstraint.argument2, (LSet)aConstraint.argument3, aConstraint);
        else
            if(aConstraint.argument1 instanceof Ris && aConstraint.argument2 instanceof LSet || aConstraint.argument2 instanceof Ris && aConstraint.argument1 instanceof LSet) //ris = lset or lset = ris
                eqRis((LSet) aConstraint.argument1, (LSet) aConstraint.argument2, aConstraint); //at least one Ris
        else
        if (aConstraint.argument1 instanceof LVar && aConstraint.argument2 instanceof LVar)
            eqLvar((LVar)aConstraint.argument1, (LVar)aConstraint.argument2, aConstraint);       // lvar = lvar
        else if (aConstraint.argument1 instanceof LVar)
            eqLvarObj((LVar)aConstraint.argument1, aConstraint.argument2, aConstraint);          // lvar = any
        else if (aConstraint.argument2 instanceof LVar)
            eqLvarObj((LVar)aConstraint.argument2, aConstraint.argument1, aConstraint);          // any = lvar
        else if (aConstraint.argument1 instanceof CP && aConstraint.argument2 instanceof CP)
            eqCP((CP) aConstraint.argument1, (CP) aConstraint.argument2, aConstraint);          // cp = cp
        else if (aConstraint.argument1 instanceof CP && aConstraint.argument2 instanceof LSet)
            eqCP((CP) aConstraint.argument1, (LSet) aConstraint.argument2, aConstraint);        // cp = lset
        else if (aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof CP) {
            Object tmp = aConstraint.argument1;                         // lset = cp
            aConstraint.argument1 = aConstraint.argument2;
            aConstraint.argument2 = tmp;
            eq(aConstraint);
        }
        else if (aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof LSet) {
            eqSet((LSet)aConstraint.argument1, (LSet)aConstraint.argument2, aConstraint);        // lset = lset
        }
        else if (aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof Set) {
            LSet aux = new LSet((Set<?>)aConstraint.argument2);         // lset = set
            eqSet((LSet)aConstraint.argument1, aux, aConstraint);
        }
        else if (aConstraint.argument1 instanceof Set && aConstraint.argument2 instanceof LSet) {
            LSet aux = new LSet((Set<?>)aConstraint.argument1);         // set = lset
            eqSet(aux, (LSet)aConstraint.argument2, aConstraint);
        }
        else if (aConstraint.argument1 instanceof LList && aConstraint.argument2 instanceof LList)
            eqList((LList)aConstraint.argument1, (LList)aConstraint.argument2, aConstraint);     // llist = llist
        else if (aConstraint.argument1 instanceof LList && aConstraint.argument2 instanceof List) {
            LList aux = new LList((List<?>)aConstraint.argument2);      // llist = list
            eqList((LList)aConstraint.argument1, aux, aConstraint);
        }
        else if (aConstraint.argument1 instanceof List && aConstraint.argument2 instanceof LList) {
            LList aux = new LList((List<?>)aConstraint.argument1);      // list = llist
            eqList(aux, (LList)aConstraint.argument2, aConstraint);
        }
        else
            eqObj(aConstraint.argument1, aConstraint.argument2, aConstraint);                    // any = any
        return;
    }

    /**
     * Selects the appropriate method to call for solving inequality constraints depending on the arguments types.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    protected void neq(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.neqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        
        manageEquChains(aConstraint);

        if(aConstraint.argument1 instanceof LList || aConstraint.argument2 instanceof LList)
            neqList(aConstraint.argument1, aConstraint.argument2, aConstraint);
        else if (aConstraint.argument1 instanceof LVar && aConstraint.argument2 instanceof LVar)
            neqLvar((LVar)aConstraint.argument1, (LVar)aConstraint.argument2, aConstraint); // X != Y), X and Y Lvar
        else if (aConstraint.argument1 instanceof LVar)
            neqLvarObj((LVar)aConstraint.argument1, aConstraint.argument2, aConstraint);    // X != t
        else if (aConstraint.argument2 instanceof LVar)
            neqLvarObj((LVar)aConstraint.argument2, aConstraint.argument1, aConstraint);    // t != X
        else if (aConstraint.argument1 instanceof CP && aConstraint.argument2 instanceof LSet)
            neqCP((CP) aConstraint.argument1, (LSet)aConstraint.argument2, aConstraint);
        else if(aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof CP) {
            Object tmp = aConstraint.argument1;
            aConstraint.argument1 = aConstraint.argument2;
            aConstraint.argument2 = tmp;
            neqCP((CP) aConstraint.argument1, (LSet) aConstraint.argument2, aConstraint);
        }
        else if (aConstraint.argument1 instanceof Ris && aConstraint.argument2 instanceof LSet)
            neqRis((Ris) aConstraint.argument1, (LSet)aConstraint.argument2, aConstraint);
        else if(aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof Ris) {
            Object tmp = aConstraint.argument1;
            aConstraint.argument1 = aConstraint.argument2;
            aConstraint.argument2 = tmp;
            neqRis((Ris) aConstraint.argument1, (LSet) aConstraint.argument2, aConstraint);
        }
        else if (aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof LSet)
            neqSet((LSet)aConstraint.argument1, (LSet)aConstraint.argument2, aConstraint);  // S != R, S and R LSet
        else if (aConstraint.argument1 instanceof LSet && aConstraint.argument2 instanceof Set) {
            LSet aux = new LSet((Set<?>)aConstraint.argument2);       // S != r, S LSet, r Set
            neqSet((LSet)aConstraint.argument1, aux, aConstraint);
        }
        else if (aConstraint.argument1 instanceof Set && aConstraint.argument2 instanceof LSet) {
            LSet aux = new LSet((Set<?>)aConstraint.argument1);       // r != S, S LSet, r Set
            neqSet(aux, (LSet)aConstraint.argument2, aConstraint);
        }
        else
            neqObj(aConstraint.argument1, aConstraint.argument2, aConstraint); // t.neq(aConstraint)
    }
    
    /**
     * Rewrite rule for select constraint. Used only within the equality rewriting rules.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void select(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.selectCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;
        assert aConstraint.argument3 != null;
        assert aConstraint.argument4 == null;
        
        manageEquChains(aConstraint);
        
        select((LSet) aConstraint.argument2, (LSet) aConstraint.argument3, aConstraint);
    }


    //////////////////////////////////////////////////////
    ////////////////// PRIVATE METHODS ///////////////////
    //////////////////////////////////////////////////////

    /**
     * Equality rule for logical variables.
     * If both are uninitialized one of them is bound to the other using its {@code equ} field.
     * If one is uninitialized and the other is initialized the uninitialized one is linked to the
     * initialized one using the uninitialized {@code equ} field.
     * If both variables are initialized their values are checked for equality.
     * @param lVar1 first logical variable.
     * @param lVar2 second logical variable.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void
    eqLvar(@NotNull LVar lVar1, @NotNull LVar lVar2, @NotNull AConstraint aConstraint) {
        assert lVar1 != null;
        assert lVar2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if (lVar1 == lVar2) {
            aConstraint.setSolved(true);
            return;
        }

        else if (lVar1.isInitialized())
            if (lVar2.isInitialized()) {
                // lVar1 and lVar2 instantiated
                aConstraint.argument1 = lVar1.val;
                aConstraint.argument2 = lVar2.val;
                eq(aConstraint);
                return;
            }
            else {
                // lVar1 instantiated and lVar2 not instantiated
                if (lVar2 instanceof IntLVar)
                    solver.domainRulesFD.eqRule((IntLVar) lVar2,
                            (Integer) lVar1.val, aConstraint);
                else if (lVar2 instanceof SetLVar)
                    solver.domainRulesFS.eqRule((SetLVar) lVar2,
                            (MultiInterval) lVar1.val, aConstraint);
                if(lVar2 instanceof IntLVar && !(lVar1 instanceof IntLVar)){
                    aConstraint.setSolved(true);
                    return;
                }

                lVar2.setInitialized(true);
                lVar2.equ = lVar1;
                aConstraint.argument2 = lVar1;
                aConstraint.setSolved(true);
                solver.storeUnchanged = false;
                return;
            }
        else if (lVar2.isInitialized()) {
            // lVar1 not instantiated and lVar2 instantiated
            if (lVar1 instanceof IntLVar)
                solver.domainRulesFD.eqRule((IntLVar) lVar1, (Integer) lVar2.val, aConstraint);
            else if (lVar1 instanceof SetLVar)
                solver.domainRulesFS.eqRule((SetLVar) lVar1, (MultiInterval) lVar2.val, aConstraint);
            if(lVar1 instanceof IntLVar && !(lVar2 instanceof IntLVar)){
                aConstraint.setSolved(true);
                return;
            }
            lVar1.setInitialized(true);
            lVar1.equ = lVar2;
            aConstraint.argument1 = lVar2;
            aConstraint.setSolved(true);
            solver.storeUnchanged = false;
            return;
        }
        else {
            // lVar1 and lVar2 not instantiated
            if (lVar2.name.charAt(0) == '?' || lVar2.name.charAt(0) == 'N') {
                if (lVar1 instanceof IntLVar && lVar2 instanceof IntLVar)
                    solver.domainRulesFD.eqRule((IntLVar) lVar1, (IntLVar) lVar2, aConstraint);
                else if (lVar1 instanceof SetLVar && lVar2 instanceof SetLVar)
                    solver.domainRulesFS.eqRule((SetLVar) lVar1, (SetLVar) lVar2, aConstraint);
                if(lVar2 instanceof IntLVar && !(lVar1 instanceof IntLVar)){
                    lVar1.setEqu(lVar2);
                    lVar1.setInitialized(true);
                    aConstraint.setSolved(true);
                    return;
                }
                lVar2.setInitialized(true);
                lVar2.equ = lVar1;
                aConstraint.argument2 = lVar1;
                aConstraint.setSolved(true);
            }
            else {
                if (lVar1 instanceof IntLVar && lVar2 instanceof IntLVar) {
                    solver.domainRulesFD.eqRule((IntLVar) lVar2, (IntLVar) lVar1, aConstraint);
                }
                else if (lVar1 instanceof SetLVar && lVar2 instanceof SetLVar){
                    solver.domainRulesFS.eqRule((SetLVar) lVar2, (SetLVar) lVar1, aConstraint);
                }
                if(lVar1 instanceof IntLVar && !(lVar2 instanceof IntLVar)){
                    lVar2.setEqu(lVar1);
                    lVar2.setInitialized(true);
                    aConstraint.setSolved(true);
                    return;
                }
                lVar1.setInitialized(true);
                lVar1.equ = lVar2;
                aConstraint.argument1 = lVar2;
                aConstraint.setSolved(true);
            }
            solver.storeUnchanged = false;
            return;
        }

    }

    /**
     * Equality rule for one logical variable and one object.
     * If the logical variable is uninitialized it becomes initialized and its {@code value} field stores
     * a reference to {@code object}. If the logical variable is initialized a comparison between its {@code value}
     * field and {@code object} is made.
     * @param lVar logical variable.
     * @param object object.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    private void eqLvarObj(@NotNull LVar lVar, @NotNull Object object, @NotNull AConstraint aConstraint) {
        assert lVar != null;
        assert object != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if (lVar.isInitialized()) {
            aConstraint.argument1 = lVar.val;
            aConstraint.argument2 = object;
            eq(aConstraint);
            solver.storeUnchanged = false;
            return;
        }
        else {
            if (lVar instanceof IntLVar) {
                if (object instanceof Integer)
                    solver.domainRulesFD.eqRule((IntLVar) lVar, (Integer) object, aConstraint);
                else
                    solver.fail(aConstraint); }
            else if (lVar instanceof SetLVar){
                if (object instanceof MultiInterval)
                    solver.domainRulesFS.eqRule((SetLVar) lVar, (MultiInterval) object, aConstraint);
                else
                    solver.fail(aConstraint); }
            lVar.setValue(object);
            lVar.setInitialized(true);
            aConstraint.argument1 = lVar;
            aConstraint.argument2 = object;
            aConstraint.setSolved(true);
            solver.storeUnchanged = false;
            return;
        }
    }

    /**
     * Rewrite rule for equality of a cartesian product with a logical lSet.
     * @param cp cartesian product.
     * @param lSet logical lSet.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    private void eqCP(@NotNull CP cp, @NotNull LSet lSet, @NotNull AConstraint aConstraint)   {
        assert cp != null;
        assert lSet != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        // X x Y = {}
        if (lSet.isInitialized() && lSet.isEmpty()) {
            switch(aConstraint.alternative) {
                case 0:     // --- > X = {}
                    solver.addChoicePoint(aConstraint);
                    aConstraint.argument1 = cp.getFirstSet();
                    aConstraint.constraintKindCode = Environment.eqCode;
                    aConstraint.argument2 = LSet.empty();
                    aConstraint.alternative = 0;
                    solver.storeUnchanged = false;
                    return;
                case 1:     // ---> Y = {}
                    aConstraint.argument1 = cp.getSecondSet();
                    aConstraint.constraintKindCode = Environment.eqCode;
                    aConstraint.argument2 = LSet.empty();
                    aConstraint.alternative = 0;
                    solver.storeUnchanged = false;
                    return;
            }
        }

        // X x {} = Z ---> Z = {}
        // {} x Y = Z ---> Z = {}
        else if (cp.isInitialized() && cp.isEmpty()) {
            aConstraint.argument1 = lSet;
            aConstraint.constraintKindCode = Environment.eqCode;
            aConstraint.argument2 = LSet.empty();
            solver.storeUnchanged = false;
            return;
        }
        else if(cp.isInitialized() && cp.isGround() && !lSet.isInitialized()) {
            LSet exp = cp.expand();
            aConstraint.argument1 = exp;
            aConstraint.constraintKindCode = Environment.eqCode;
            aConstraint.argument2 = lSet;

            solver.storeUnchanged = false;
            return;
        }
        //X x Y = {z u Z} --->
        else if (lSet.isInitialized() && !lSet.isEmpty()){
            Object o = lSet.getOne();

            LVar n1 = new LVar();
            LVar n2 = new LVar();
            LPair z = new LPair(n1,n2);
            aConstraint.argument1 = o;
            aConstraint.constraintKindCode = Environment.eqCode;            // z = (n1,n2)
            aConstraint.argument2 = z;

            LSet N1 = new LSet();
        
            solver.add(new AConstraint(Environment.eqCode, cp.getFirstSet(), N1.ins(n1))); // X = {n1 u N1}

            LSet N2 = new LSet();
           
            solver.add(new AConstraint(Environment.eqCode, cp.getSecondSet(), N2.ins(n2))); // Y = {n2 u N2}

            solver.add(new ConstraintClass(new AConstraint(
                    Environment.unionCode, new CP(LSet.empty().ins(n1), N2),
                    new CP(N1, N2.ins(n2)),
                    lSet.removeOne())));  //un({n1} x N2, N1 x {n2 u N2}, Z}

            solver.storeUnchanged = false;
            return;
        }
    }

    /**
     * Rewrite rule for equality between two cartesian products.
     * @param cp1 first cartesian product.
     * @param cp2 second cartesian product.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail  if inconsistencies are found.
     */
    private void eqCP(@NotNull CP cp1, @NotNull CP cp2, @NotNull AConstraint aConstraint) {
        assert cp1 != null;
        assert cp2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        //W x X = Y x Z ---> W = Y && X = Z
        if(cp1.isInitialized() || cp2.isInitialized()) {
            aConstraint.argument1 = cp1.getFirstSet();
            aConstraint.constraintKindCode = Environment.eqCode;
            aConstraint.argument2 = cp2.getFirstSet();
            solver.add(new AConstraint(Environment.eqCode, cp1.getSecondSet(), cp2.getSecondSet()));
            solver.storeUnchanged = false;
            return;
        }
    }

    /**
     * Rule number 1 rewrite rule for equality in L_RIS.
     * @param lSet1 first argument.
     * @param lSet2 second argument.
     * @param aConstraint equality atomic constraint.
     * @return {@code true} if the rule was applied, {@code false} otherwise.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private boolean risEqRule1(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint){
        assert lSet1 != null;
        assert lSet2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if(lSet1.isInitialized() && lSet1.isEmpty() && lSet2.isInitialized() && lSet2.isEmpty()){
            aConstraint.setSolved(true);
            return true;
        }
        return false;
    }

    /**
     * Rule number 3 rewrite rule for equality in L_RIS.
     * @param lSet1 first argument.
     * @param lSet2 second argument.
     * @param aConstraint equality atomic constraint.
     * @return {@code true} if the rule was applied, {@code false} otherwise.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private boolean risEqRule3(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint){
        assert lSet1 != null;
        assert lSet2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if(lSet1 instanceof Ris && lSet1.isInitialized() && !lSet1.isEmpty())
            return false;
        if((lSet1.isInitialized() || lSet1 instanceof Ris) && (!lSet2.isInitialized() && !(lSet2 instanceof Ris))) {
            Object tmp = aConstraint.argument1;
            aConstraint.argument1 = aConstraint.argument2;
            aConstraint.argument2 = tmp;
            solver.storeUnchanged = false;
            return true;
        }
        else
            return false;
    }

    /**
     * Rule number 6 rewrite rule for equality in L_RIS.
     * @param lSet1 first argument.
     * @param lSet2 second argument.
     * @param aConstraint equality atomic constraint.
     * @return {@code true} if the rule was applied, {@code false} otherwise.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private boolean risEqRule6(@NotNull LSet lSet1 , @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if(!(lSet1.isInitialized() || lSet1 instanceof Ris) && !lSet2.occurs(lSet1)) {
            if(lSet2 instanceof Ris && lSet2.isInitialized() && !lSet2.isEmpty())
                return false;
            else if(lSet2.isInitialized() && lSet2.isEmpty()){
                aConstraint.argument1 = lSet1;
                aConstraint.argument2 = LSet.empty();
                eq(aConstraint);
                return true;
            }

            lSet1.equ = lSet2;
            lSet1.setInitialized(true);
            aConstraint.setSolved(true);
            solver.storeUnchanged = false;
            return true;
        }
        return false;
    }

    /**
     * Rule number 7 rewrite rule for equality in L_RIS.
     * @param lSet1 first argument.
     * @param lSet2 second argument.
     * @param aConstraint equality atomic constraint.
     * @return {@code true} if the rule was applied, {@code false} otherwise.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private boolean risEqRule7(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if(lSet1.isInitialized() && !(lSet1 instanceof Ris) && !lSet1.isEmpty() && lSet2.isInitialized() && lSet2.isEmpty()) {
            solver.fail(aConstraint);
            return true;
        }
        else
            return false;
    }

    /**
     * Rule number 8 rewrite rule for equality in L_RIS.
     * @param lSet1 first argument.
     * @param lSet2 second argument.
     * @param aConstraint equality atomic constraint.
     * @return {@code true} if the rule was applied, {@code false} otherwise.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private boolean risEqRule8(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if(lSet2.isInitialized() && !(lSet2 instanceof Ris) && !lSet2.isEmpty() && lSet1.isInitialized() && lSet1.isEmpty()) {
            solver.fail(aConstraint);
            return true;
        }
        else
            return false;
    }

    /**
     * Rule number 11 rewrite rule for equality in L_RIS.
     * @param lSet1 first argument.
     * @param lSet2 second argument.
     * @param aConstraint equality atomic constraint.
     * @return {@code true} if the rule was applied, {@code false} otherwise.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private boolean risEqRule11(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint){
        assert lSet1 != null;
        assert lSet2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if(lSet1 instanceof Ris && lSet1.isInitialized() && !lSet1.isEmpty() && lSet2.isInitialized() && lSet2.isEmpty()) {
            Ris ris1 = (Ris) aConstraint.argument1;
            Object d = ris1.getDomain().getOne();

            solver.add(ris1.notF(d));
            LSet newDomain = ris1.getDomain().removeOne();
            Ris newRis = new Ris(ris1.getControlTerm(), newDomain, ris1.getFilter(), ris1.getPattern(), ris1.getDummyVariables());
            solver.store.rewrite(aConstraint, newRis.eq(lSet2));
            solver.storeUnchanged = false;
            return true;
        }
        else
            return false;
    }

    /**
     * Rule number 12 rewrite rule for equality in L_RIS.
     * @param lSet1 first argument.
     * @param lSet2 second argument.
     * @param aConstraint equality atomic constraint.
     * @return {@code true} if the rule was applied, {@code false} otherwise.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private boolean risEqRule12(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if(lSet1 instanceof Ris && lSet1.isInitialized() && !lSet1.isEmpty()) {
            Ris ris1 = (Ris) aConstraint.argument1;


            LSet domain = ris1.getDomain();
            Object d = domain.getOne();
            LSet domainRest = domain.removeOne();
            ConstraintClass c = new ConstraintClass();
            ConstraintClass f = ris1.F(d);
            Object pattern = ris1.P(d, c);
            ConstraintClass notF = ris1.notF(d);
            Ris restRis = new Ris(ris1.getControlTerm(), domainRest, ris1.getFilter(), ris1.getPattern(), ris1.getDummyVariables());

            ConstraintClass newConstraint;
            if (c.isEmpty())
                newConstraint = (f.and(restRis.ins(pattern).eq(lSet2))).or(notF.and(restRis.eq(lSet2)));
            else
                newConstraint = (f.and(c.and(restRis.ins(pattern).eq(lSet2)))).or(notF.and(restRis.eq(lSet2)));

            solver.store.rewrite(aConstraint, newConstraint);
            return true;
        }
        else
            return false;
    }

    /**
     * Rule number 13A rewrite rule for equality in L_RIS.
     * @param lSet1 first argument.
     * @param lSet2 second argument.
     * @param aConstraint equality atomic constraint.
     * @return {@code true} if the rule was applied, {@code false} otherwise.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private boolean risEqRule13(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if(lSet1 instanceof Ris && !lSet1.isInitialized() && (lSet2.isInitialized() && !(lSet2 instanceof Ris) && !lSet2.isEmpty())) {
            LSet tail2 = lSet2.getTail();
            if (tail2 instanceof Ris)
                tail2 = ((Ris) tail2).getDomain().getTail();
            if (((Ris) lSet1).getDomain().equals(tail2)) {
                Ris ris1 = (Ris) aConstraint.argument1;
                LSet domain = ris1.getDomain();
                LSet N;
                Object d = ris1.getNewControlTerm();
                if (domain instanceof IntLSet) {
                    N = new IntLSet();
                } else {
                    N = new LSet();
                }

                Object y = lSet2.getOne();
                ConstraintClass filterApply = ris1.F(d);

                ConstraintClass c = new ConstraintClass();
                Object pattern = ris1.P(d, c);

                Ris newRis = new Ris(ris1.getControlTerm(), N, ris1.getFilter(), ris1.getPattern(), ris1.getDummyVariables());
                ConstraintClass newConstraint;
                LSet newN = N;
                for (Object object : lSet2)
                    if (object != y)
                        newN = newN.ins(object);
                if (c.isEmpty())
                    newConstraint = domain.eq(N.ins(d)).
                            and(filterApply).
                            and(new ConstraintClass(new AConstraint(Environment.eqCode, y, pattern))).
                            and(newRis.eq(newN));
                else
                    newConstraint = c.and(domain.eq(N.ins(d))).
                            and(filterApply).
                            and(new ConstraintClass(new AConstraint(Environment.eqCode, y, pattern))).
                            and(newRis.eq(newN));
                solver.store.rewrite(aConstraint, newConstraint);
                solver.storeUnchanged = false;
                return true;
            }
            else
                return false;
        }
        else
            return false;
    }

    /**
     * Rule number 13 rewrite rule for equality in L_RIS.
     * @param lSet1 first argument.
     * @param lSet2 second argument.
     * @param aConstraint equality atomic constraint.
     * @return {@code true} if the rule was applied, {@code false} otherwise.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private boolean risEqRule14(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if(lSet1 instanceof Ris && !lSet1.isInitialized() && (lSet2.isInitialized() && !lSet2.isEmpty() && !(lSet2 instanceof Ris))) {
            Ris ris1 = (Ris) aConstraint.argument1;
            LSet domain = ris1.getDomain();
            LSet E;
            Object d = ris1.getNewControlTerm();
            if (domain instanceof IntLSet) {
                E = new IntLSet();
            } else {
                E = new LSet();
            }

            Object y = lSet2.getOne();

            ConstraintClass filterApply = ris1.F(d);
            ConstraintClass c = new ConstraintClass();
            Object pattern = ris1.P(d, c);

            Ris newRis = new Ris(ris1.getControlTerm(), E, ris1.getFilter(), ris1.getPattern(), ris1.getDummyVariables());
            ConstraintClass newConstraint;
            if (c.isEmpty())
                newConstraint = domain.eq(E.ins(d)).
                        and(filterApply).
                        and(new ConstraintClass(new AConstraint(Environment.eqCode, y, pattern))).
                        and(newRis.eq(lSet2.removeOne()));
            else
                newConstraint = c.and(domain.eq(E.ins(d))).
                        and(filterApply).
                        and(new ConstraintClass(new AConstraint(Environment.eqCode, y, pattern))).
                        and(newRis.eq(lSet2.removeOne()));
            solver.store.rewrite(aConstraint, newConstraint);
            solver.storeUnchanged = false;
            return true;
        }
        else return false;
    }

    /**
     * Rule number 15 rewrite rule for equality in L_RIS.
     * @param lSet1 first argument.
     * @param lSet2 second argument.
     * @param aConstraint equality atomic constraint.
     * @return {@code true} if the rule was applied, {@code false} otherwise.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private boolean risEqRule15(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if(lSet2 instanceof Ris){
            LSet D = ((Ris)lSet2).getDomain();
            if((D.isInitialized() || D instanceof Ris) && !D.isBoundAndEmpty() || !(lSet1 instanceof Ris) && lSet1.isInitialized() && !lSet1.isEmpty()){
                Object tmp = aConstraint.argument1;
                aConstraint.argument1 = aConstraint.argument2;
                aConstraint.argument2 = tmp;
                solver.storeUnchanged = false;
                return true;
            }
            else return false;

        }
        else return false;
    }



    /**
     * Rewrite rule for equality when at least one of the arguments is a RIS.
     * @param lSet1 first argument.
     * @param lSet2 second argument.
     * @param aConstraint equality atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     * @author Andrea Fois
     */
    private void eqRis(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if (!(lSet1 instanceof LSet) || !(lSet2 instanceof LSet))
            solver.fail(aConstraint);

        if (dealWithRisExpansion(aConstraint)){
            eq(aConstraint);
            return;
        }

        if(risEqRule1(lSet1,lSet2, aConstraint))return;
        else if(risEqRule3(lSet1,lSet2,aConstraint))return;
        else if(risEqRule6(lSet1,lSet2,aConstraint))return;
        else if(risEqRule7(lSet1,lSet2,aConstraint))return;
        else if(risEqRule8(lSet1,lSet2,aConstraint))return;
        else if(risEqRule11(lSet1,lSet2,aConstraint))return;
        else if(risEqRule12(lSet1,lSet2,aConstraint))return;
        else if(risEqRule13(lSet1,lSet2,aConstraint))return;
        else if(risEqRule14(lSet1,lSet2,aConstraint))return;
        else if(risEqRule15(lSet1,lSet2,aConstraint))return;
        else return; // solved form


    }

    /**
     * Rewrite rule for equality between logical sets.
     * @param lSet1 first argument.
     * @param lSet2 second argument.
     * @param aConstraint equality atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void eqSet(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        switch (aConstraint.alternative) {
            case 0: // (1)
                if (lSet1 == lSet2) {
                    aConstraint.setSolved(true);
                    return;
                }
                else
                if (!lSet1.isInitialized() && !lSet2.isInitialized()) {
                lSet2.setInitialized(true);
                lSet2.setEqu(lSet1);
                aConstraint.argument2 = lSet1;
                aConstraint.setSolved(true);
                solver.storeUnchanged = false;
                return;
            }
            else if (!lSet1.isInitialized()) { // (2)
                if (lSet2.isBoundAndEmpty()) {

                    lSet1.setInitialized(true);

                    lSet1.setEqu(lSet2);
                    solver.storeUnchanged = false;
                    return;
                }
                else if (lSet2.occurs(lSet1)) {
                    solver.fail(aConstraint);   // for nested sets only
                    return;
                }
                else { // (6)
                    if((lSet2.getTail() instanceof Ris) && lSet2.getTail().occurs(lSet1))
                        return;
                    if (lSet2.getTail() == lSet1 && !lSet2.isEmpty()) {
                        // X.eq({t0,...,tn|X})

                        LSet N = new LSet();
                        LSet M = new LSet(lSet2.toArrayList(),N);
                        aConstraint.argument2 = M;
                        eqSet(lSet1, M, aConstraint);  // X.eq( {t0, ..., tn|M})
                        solver.storeUnchanged = false;
                        return;
                    }

                    // X.eq( {t0, .., tn|N})
                    else {
                        lSet1.setInitialized(true);
                        lSet1.setEqu(lSet2);
                        aConstraint.setSolved(true);
                        solver.storeUnchanged = false;
                        return;
                    }
                }
            } else if (!lSet2.isInitialized()) { // (2)
                if (lSet1.isBoundAndEmpty()) {
                    lSet2.setInitialized(true);
                    lSet2.setEqu(lSet1);
                    aConstraint.setSolved(true);
                    solver.storeUnchanged = false;
                    return;
                } else if (lSet1.occurs(lSet2)) {
                    solver.fail(aConstraint);   // for nested sets only
                    return;
                } else { // (6)
                    // {t1,...,tn|X}.eq(X)
                    if((lSet2.getTail() instanceof Ris) && lSet2.getTail().occurs(lSet1))
                        return;
                    if (lSet1.getTail() == lSet2 && !lSet1.isEmpty()) {
                        LSet N = new LSet();
                        LSet M = new LSet(lSet1.toArrayList(),N);
                        aConstraint.argument1 = M;
                        eqSet(M, lSet2, aConstraint);
                        solver.storeUnchanged = false;
                        return;
                    } else {
                        // t0, .., tn|N}.eq(X)
                        lSet2.setInitialized(true);
                        lSet2.setEqu(lSet1);
                        aConstraint.setSolved(true);
                        solver.storeUnchanged = false;
                        return;
                    }
                }
            } else {
                if (!(lSet1.rest instanceof Ris) && (!(lSet2.rest instanceof Ris)) &&
                        lSet1.isGround() && lSet2.isGround()      // GROUND CASES
                        ) {
                    if (lSet1.equals(lSet2)) {
                        aConstraint.setSolved(true);}
                    else
                        solver.fail(aConstraint);
                    return;
                }
             else if (lSet1.occurs(lSet2) || lSet2.occurs(lSet1)) {
                    solver.fail(aConstraint);    // for nested sets only
                    return;
                }
             else {
                    if (lSet1.isEmpty() && lSet2.isEmpty()) {
                        aConstraint.setSolved(true);
                        return;
                        }
                    else
                        if (lSet1.isEmpty() || lSet2.isEmpty()) {
                            solver.fail(aConstraint);
                            return;
                        } // (9)
                        else
                            // {t|S}.eq({t'|S'})
                            if (lSet1.getTail() != lSet2.getTail()
                                    || (lSet1.getTail().isBoundAndEmpty() &&
                                    lSet2.getTail().isBoundAndEmpty())) {
                                aConstraint.alternative = 1;
                                eqSet(lSet1, lSet2, aConstraint);
                                return;
                            } // (10)
                            else
                                // {t1,...,tn|X}.eq({s1,...,sm|X})
                                if (lSet1.getTail() == lSet2.getTail()
                                        && !lSet1.getTail().isBoundAndEmpty()) {
                                    aConstraint.alternative = 5;
                                    eqSet(lSet1, lSet2, aConstraint);

                                    return;
                                }
                    }
                }
            case 1: // {t|S}.eq( {t'|S'}) (9)(i)
                solver.addChoicePoint(aConstraint);
                aConstraint.argument1 = lSet1.getOne(); // t.eq(t')
                aConstraint.argument2 = lSet2.getOne();
                aConstraint.alternative = 0;
                AConstraint se0 = new AConstraint(
                        Environment.eqCode, lSet1.removeOne(), lSet2.removeOne());
                solver.add(solver.indexOf(aConstraint) + 1, se0); // S.eq(S')
                eq(aConstraint);
                solver.storeUnchanged = false;
                return;
            case 2: // {t|S}.eq( {t'|S'}) (9)(ii)
                solver.addChoicePoint(aConstraint);
                aConstraint.argument1 = lSet1.getOne(); // t.eq(t')
                aConstraint.argument2 = lSet2.getOne();
                aConstraint.alternative = 0;
                AConstraint se1 = new AConstraint(
                        Environment.eqCode, lSet1, lSet2.removeOne());
                solver.add(solver.indexOf(aConstraint) + 1, se1);
                eq(aConstraint);
                solver.storeUnchanged = false;
                return;
            case 3: // {t|S}.eq( {t'|S'}) (9)(iii)
                if(!solver.getOptimizationOptions().areSetUnificationOptimizationsEnabled() ||
                        !LObject.equals(lSet1.getOne(), lSet2.getOne()))
                    solver.addChoicePoint(aConstraint);
                aConstraint.argument1 = lSet1.getOne(); // t.eq(t')
                aConstraint.argument2 = lSet2.getOne();
                aConstraint.alternative = 0;
                AConstraint se2 = new AConstraint(
                        Environment.eqCode, lSet1.removeOne(), lSet2);
                solver.add(solver.indexOf(aConstraint) + 1, se2);
                eq(aConstraint);
                solver.storeUnchanged = false;
                return;
            case 4: // {t|S}.eq( {t'|S'}) (9)(iv)
                LSet n = new LSet();
                aConstraint.argument1 = lSet1.removeOne();
                aConstraint.argument2 = n.ins(lSet2.getOne());
                aConstraint.alternative = 0;
                AConstraint se3 = new AConstraint(Environment.eqCode, n.ins(lSet1.getOne()),
                        lSet2.removeOne());
                solver.add(solver.indexOf(aConstraint) + 1, se3);
                solver.storeUnchanged = false;

                eq(aConstraint);
                return;
            case 5: // {t1, .., tn|X}.eq( {s1, .., sm|X}) (10) (i, ii, iii)
                solver.addChoicePoint(aConstraint);
                LVar set2_elem = new LVar();
                LSet w = new LSet();
                AConstraint s5 = new AConstraint(
                        Environment.selectCode, set2_elem, lSet2, w); // select
                AConstraint ss5 = new AConstraint(Environment.eqCode, set2_elem,
                        lSet1.getOne());   // eq
                solver.add(solver.indexOf(aConstraint), s5);
                solver.add(solver.indexOf(aConstraint), ss5);
                aConstraint.argument3 = w;
                aConstraint.alternative = 7;        // eq/3
                solver.storeUnchanged = false;
                return;
            case 6: // {t1, .., tn|X}.eq( {s1, .., sm|X}) (10) (iv)
                LSet N = new LSet("N");
                aConstraint.argument1 = lSet1.getTail();
                aConstraint.argument2 = N.ins(lSet1.getOne());
                aConstraint.alternative = 0;

                LSet set1_less = lSet1.removeOne();
                LSet set1_new = (LSet)set1_less.concat(N);
                LSet set2_new = (LSet)lSet2.concat(N);
                AConstraint s6 = new AConstraint(Environment.eqCode, set1_new, set2_new );
                solver.add(solver.indexOf(aConstraint) + 1, s6);
                solver.storeUnchanged = false;
                eq(aConstraint);

                return;
        }

    }

    /**
     * Rewrite rule for equality between logical sets with three arguments, used only within the eq rewriting rules
     * @param lSet1 first argument
     * @param lSet2 second argument
     * @param newLSet temporary new logical set.
     * @param aConstraint equality atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void eqSet(@NotNull LSet lSet1, @NotNull LSet lSet2,
                       @NotNull LSet newLSet, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet2 != null;
        assert newLSet != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        switch (aConstraint.alternative) {
            case 7: // {t1, .., tn|X}.eq( {s1, .., sm|X}) (10) (i)
                solver.addChoicePoint(aConstraint);
                aConstraint.argument1 = lSet1.removeOne();
                aConstraint.argument2 = newLSet;
                aConstraint.argument3 = null;
                aConstraint.alternative = 0;
                eq(aConstraint);
                return;
            case 8: // {t1, .., tn|X}.eq( {s1, .., sm|X}) (10) (ii)
                solver.addChoicePoint(aConstraint);
                aConstraint.argument1 = lSet1;
                aConstraint.argument2 = newLSet;
                aConstraint.argument3 = null;
                aConstraint.alternative = 0;
                eq(aConstraint);
                return;
            case 9: // {t1, .., tn|X}.eq( {s1, .., sm|X}) (10) (iii)
                aConstraint.argument1 = lSet1.removeOne();
                aConstraint.argument2 = lSet2;
                aConstraint.argument3 = null;
                aConstraint.alternative = 0;
                eq(aConstraint);
                return;
        }
    }

    /**
     * Rewrite rule for select constraint.
     * @param lSet second argument.
     * @param rest rest of set.
     * @param aConstraint equality atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void select(@NotNull LSet lSet, @NotNull LSet rest, @NotNull AConstraint aConstraint) {
        assert lSet != null;
        assert rest != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.selectCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if (lSet.countAllElements() == 0) {
            solver.fail(aConstraint);
            return;
        } else {
            switch (aConstraint.alternative) {
                case 0:
                    solver.addChoicePoint(aConstraint);
                    aConstraint.argument2 = lSet.getOne(); // lVar.eq(LSet.first())
                    aConstraint.argument3 = null;
                    aConstraint.constraintKindCode = Environment.eqCode;
                    eq(aConstraint);
                    // rest.eq(LSet.sub())
                    AConstraint s0 = new AConstraint(
                            Environment.eqCode, rest, (LSet) lSet.removeOne());
                    solver.add(solver.indexOf(aConstraint) + 1, s0);
                    solver.storeUnchanged = false;
                    return;
                case 1:
                    LSet v = new LSet();
                    aConstraint.alternative = 0;
                    aConstraint.argument2 = lSet.removeOne();
                    aConstraint.argument3 = v;
                    AConstraint s1 = new AConstraint(Environment.eqCode, rest, v
                            .ins(lSet.getOne()));
                    solver.add(solver.indexOf(aConstraint), s1);
                    solver.storeUnchanged = false;
                    return;
            }
        }
    }

    /**
     * Rewrite rule for equality between logical lists.
     * @param lList1 first argument.
     * @param lList2 second argument.
     * @param aConstraint equality atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void eqList(@NotNull LList lList1, @NotNull LList lList2, @NotNull AConstraint aConstraint) {
        assert lList1 != null;
        assert lList2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if (lList1 == lList2) {
            aConstraint.setSolved(true);
            return;
        } else if (!lList1.isInitialized() && !lList2.isInitialized()) {
            lList2.setInitialized(true);
            lList2.equ = lList1;
            aConstraint.argument2 = lList1;
            solver.storeUnchanged = false;
            aConstraint.setSolved(true);
            return;
        } else if (!lList1.isInitialized()) {
            if (lList2.occurs(lList1)
                    || (lList2.getTail() == lList1 && !lList2.isBoundAndEmpty())) {
                solver.fail(aConstraint);
                return;
            } else {
                lList1.setInitialized(true);
                lList1.equ = lList2;
                aConstraint.argument1 = lList2;
                solver.storeUnchanged = false;
                aConstraint.setSolved(true);
                return;
            }
        } else if (!lList2.isInitialized()) {
            if (lList1.occurs(lList2)
                    || (lList1.getTail() == lList2 && !lList1.isBoundAndEmpty())) {
                solver.fail(aConstraint);
                return;
            } else {

                lList2.setInitialized(true);
                lList2.equ = lList1;
                aConstraint.argument2 = lList1;
                solver.storeUnchanged = false;
                aConstraint.setSolved(true);
                return;
            }
        }
        else
        {
            if (lList1.occurs(lList2) || lList2.occurs(lList1))
            {
                solver.fail(aConstraint);
                return;
            }
            else
            {
                if (lList1.isEmpty() && lList2.isEmpty())
                {
                    aConstraint.setSolved(true);
                    return;
                }
                else if (lList1.isEmpty() || lList2.isEmpty())
                {
                    solver.fail(aConstraint);
                    return;
                }
                else
                {   Iterator<Object> iterator1 = lList1.iterator();
                    Iterator<Object> iterator2 = lList2.iterator();
                    while(iterator1.hasNext() && iterator2.hasNext()){
                        Object obj1 = iterator1.next();
                        Object obj2 = iterator2.next();
                        if(LObject.isGround(obj1) && LObject.isGround(obj2) && !LObject.equals(obj1,obj2))
                            solver.fail(aConstraint);
                    }

                    aConstraint.argument1 = lList1.get(0);
                    aConstraint.argument2 = lList2.get(0);
                    AConstraint se0 = new AConstraint(Environment.eqCode, lList1.removeOne(),
                            lList2.removeOne());
                    solver.add(solver.indexOf(aConstraint) + 1, se0);
                    eq(aConstraint);
                    solver.storeUnchanged = false;
                    return;
                }
            }
        }
    }

    /**
     * Rewrite rule for equality between generic objects.
     * @param object1 first argument.
     * @param object2 second argument.
     * @param aConstraint equality atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void eqObj(@NotNull Object object1, @NotNull Object object2, @NotNull AConstraint aConstraint) {
        assert object1 != null;
        assert object2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.eqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if (object1.equals(object2)) {
            aConstraint.setSolved(true);
        } else
            solver.fail(aConstraint);
    }


    /**
     * Rewrite rule for inequality between a cartesian product and a logical lSet.
     * @param cp cartesian product.
     * @param lSet logical set.
     * @param aConstraint inequality atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void neqCP(@NotNull CP cp, @NotNull LSet lSet, @NotNull AConstraint aConstraint) {
        assert cp != null;
        assert lSet != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.neqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if(lSet.isInitialized() && cp.isInitialized()) {
            Object n = lSet.getOne();
            if(n instanceof LPair) {
                switch(aConstraint.alternative) {
                    case 0:
                        solver.addChoicePoint(aConstraint);
                        aConstraint.argument1 = n;
                        aConstraint.constraintKindCode = Environment.inCode;
                        aConstraint.argument2 = cp;
                        solver.add(new AConstraint(Environment.ninCode, n, lSet));
                        solver.storeUnchanged = false;
                        break;
                    case 1:
                        aConstraint.alternative = 0;
                        aConstraint.argument1 = n;
                        aConstraint.constraintKindCode = Environment.ninCode;
                        aConstraint.argument2 = cp;
                        solver.add(new AConstraint(Environment.inCode, n, lSet));
                        solver.storeUnchanged = false;
                        break;
                }
                return;
            }
        }
    }

    /**
     * Rewrite rule for inequality between a RIS and a logical lSet.
     * @param ris restricted intensional set.
     * @param lSet logical lSet.
     * @param aConstraint inequality atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void neqRis(@NotNull Ris ris, @NotNull LSet lSet, @NotNull AConstraint aConstraint) {
        assert ris != null;
        assert lSet != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.neqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        //{D | F(x) @ P(x)} != A

        if (dealWithRisExpansion(aConstraint)){
            neq(aConstraint);
            return;
        }

        LSet domain = ris.getDomain();

        //{{} | F(x) @ P(x)} != {} ----> FALSE
        if(domain.isInitialized() && lSet.isInitialized() && domain.isEmpty() && lSet.isEmpty()) {
            solver.fail(aConstraint);
            return;
        }

        //{{} | F(x) @ P(x)} != A ^ A isn't Empty ----> TRUE
        else if(domain.isInitialized() && lSet.isInitialized() && !(lSet instanceof Ris) && domain.isEmpty() && !lSet.isEmpty()) {
            aConstraint.setSolved(true);
            return;
        }

        //{x in D | F(x) @ P(x)} != A
        else {
            switch(aConstraint.alternative) {
                case 0:
                    // ----> y in {D | F @ P} ^ y nin A
                    solver.addChoicePoint(aConstraint);
                    Object y = ris.getNewControlTerm();
                    aConstraint.argument2 = ris;
                    aConstraint.argument1 = y;
                    aConstraint.constraintKindCode = Environment.inCode;

                    solver.add(lSet.ncontains(y));

                    solver.storeUnchanged = false;
                    return;
                case 1:
                    // ----> y nin {D | F @ P} ^ y in A
                    aConstraint.alternative = 0;
                    Object y1 = ris.getNewControlTerm();
                    aConstraint.argument2 = ris;
                    aConstraint.argument1 = y1;
                    aConstraint.constraintKindCode = Environment.ninCode;

                    solver.add(lSet.contains(y1));

                    solver.storeUnchanged = false;
                    return;
            }
        }
    }

    /**
     * Rewrite rule for inequality between logical variables.
     * @param lVar1 first logical variable.
     * @param lVar2 second logical variable.
     * @param aConstraint inequality atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void neqLvar(@NotNull LVar lVar1, @NotNull LVar lVar2, @NotNull AConstraint aConstraint)
              {
        if (lVar1 == lVar2) // (3)
            solver.fail(aConstraint);
        else if (lVar1.isInitialized())
            if (lVar2.isInitialized()) {
                // lVar1 and lVar2 instanced. (4)
                aConstraint.argument1 = lVar1.val;
                aConstraint.argument2 = lVar2.val;
                neq(aConstraint);
                solver.storeUnchanged = false;
                return;
            }
            else {
                // lVar1 instanced and lVar2 not instanced.
                neqLvarObj(lVar2, lVar1.val, aConstraint);
            }
        else if (lVar2.isInitialized()) {
            // lVar1 not instanced and lVar2 instanced.
            neqLvarObj(lVar1, lVar2.val, aConstraint);
        }
    }

    /**
     * Rewrite rule for inequality between a logical variable and a generic object (NOT a logical object).
     * @param lVar logical variable.
     * @param object generic object.
     * @param aConstraint inequality atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void neqLvarObj(@NotNull LVar lVar, @NotNull Object object, @NotNull AConstraint aConstraint) {
        assert lVar != null;
        assert object != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.neqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if (lVar.isInitialized())
            if (lVar.val.equals(object))
                solver.fail(aConstraint);
            else {
                aConstraint.setSolved(true);
                return;
            }
        else if (lVar instanceof IntLVar && ! (object instanceof LSet)) {
            try {
                solver.domainRulesFD.neqRule((IntLVar) lVar, (Integer) object, aConstraint);
            }catch(Throwable th) {
                th.printStackTrace();
            }
            aConstraint.setSolved(true);
            return;
        }
        else if (lVar instanceof BoolLVar) {
            Boolean b = (Boolean) object;
            aConstraint.constraintKindCode = Environment.eqCode;
            eqLvarObj(lVar, !b, aConstraint);
            aConstraint.setSolved(true);
            return;
        }
    }

    /**
     * Rewrite rule for inequality between logical sets.
     * @param lSet1 first logical set.
     * @param lSet2 second logical set.
     * @param aConstraint inequality atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void neqSet(@NotNull LSet lSet1, @NotNull LSet lSet2, @NotNull AConstraint aConstraint) {
        assert lSet1 != null;
        assert lSet2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.neqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if (lSet1 == lSet2) { // (3)
            solver.fail(aConstraint);
            return;
        } // (7)
        else if (lSet2.isInitialized() && lSet2.getTail() == lSet1
                && !lSet1.isInitialized()) { // X.neq( {t1, t2, .., tn|X})
            if (lSet2.occurs(lSet1)) {
                aConstraint.setSolved(true); // for nested sets only
                return;
            }
            else {
                if (aConstraint.alternative < 2) aConstraint.alternative = 2;
                switch (aConstraint.alternative) {
                    case 2: // (7) (i)
                        if (lSet2.countAllElements() > 0) {
                            solver.addChoicePoint(aConstraint);
                        }
                        aConstraint.argument1 = lSet2.getOne(); // t1.nin(X)
                        aConstraint.argument2 = lSet1;
                        aConstraint.constraintKindCode = Environment.ninCode;
                        aConstraint.alternative = 0;
                        solver.storeUnchanged = false;
                        return;
                    case 3: // (7)
                        aConstraint.argument1 = lSet1;
                        aConstraint.argument2 = lSet2.removeOne(); // X.neq( {t2, .., tn|X})
                        aConstraint.constraintKindCode = Environment.neqCode;
                        aConstraint.alternative = 2;
                        neq(aConstraint);
                        solver.storeUnchanged = false;
                        return;
                }
            }
        } // (7)
        else if (lSet1.isInitialized() && lSet1.getTail() == lSet2
                && !lSet2.isInitialized()) { // {t1, t2, .., tn|X}.neq(X)
            if (lSet1.occurs(lSet2)) {
                aConstraint.setSolved(true);
                return;
            }
            else {
                if (aConstraint.alternative < 4) aConstraint.alternative = 4;
                switch (aConstraint.alternative) {
                    case 4: // (7) (i)
                        if (lSet1.countAllElements() > 0)
                            solver.addChoicePoint(aConstraint);

                        aConstraint.argument1 = lSet1.getOne(); // t1.nin(X)
                        aConstraint.argument2 = lSet2;
                        aConstraint.constraintKindCode = Environment.ninCode;
                        aConstraint.alternative = 0;
                        solver.storeUnchanged = false;
                        return;
                    case 5: // (7)
                        aConstraint.argument1 = lSet1.removeOne(); // {t2, .., tn|X}.neq(X)
                        aConstraint.argument2 = lSet2;
                        aConstraint.constraintKindCode = Environment.neqCode;
                        aConstraint.alternative = 4;
                        neq(aConstraint);
                        solver.storeUnchanged = false;
                        return;
                }
            }
        } else if (!lSet1.isInitialized() || !lSet2.isInitialized())
            return;              // solved form
        else {
            if (lSet1.isEmpty() && lSet2.isEmpty()) {
                solver.fail(aConstraint);
                return;
            } else if (lSet1.isEmpty()
                    || lSet2.isEmpty()) {
                aConstraint.setSolved(true);
                return;
            } else if (lSet1.occurs(lSet2) || lSet2.occurs(lSet1)) {
                aConstraint.setSolved(true);
                return;
            } else {
                switch (aConstraint.alternative) {
                    case 0: // (8) (i)
                        solver.addChoicePoint(aConstraint);
                        LVar n = new LVar();
                        aConstraint.argument1 = n; // n.in(lSet1)
                        aConstraint.argument2 = lSet1;
                        aConstraint.constraintKindCode = Environment.inCode;
                        AConstraint se0 = new AConstraint(Environment.ninCode, n,
                                lSet2); // n.nin(lSet2)
                        solver.add(se0);
                        solver.storeUnchanged = false;
                        return;
                    case 1: // (8) (ii)
                        LVar m = new LVar();
                        aConstraint.argument1 = m; // n.nin(lSet1)
                        aConstraint.argument2 = lSet1;
                        aConstraint.constraintKindCode = Environment.ninCode;
                        aConstraint.alternative = 0;
                        AConstraint se1 = new AConstraint(Environment.inCode, m,
                                lSet2); // n.in(lSet2)
                        solver.add(se1);
                        solver.storeUnchanged = false;
                        return;
                }
            }
        }
    }

    /**
     * Rewrite rule for inequality of lists.
     * @param object1 first argument.
     * @param object2 second argument.
     * @param aConstraint atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void neqList(@NotNull Object object1, @NotNull Object object2, @NotNull AConstraint aConstraint) {
        assert object1 != null;
        assert object2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.neqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if(object1 instanceof List)
            object1 = new LList((List)object1);
        if(object2 instanceof List)
            object2 = new LList((List)object2);
        if(!(object1 instanceof LObject && object2 instanceof LObject)){
            aConstraint.setSolved(true);
            return;
        }
        LObject lo1 = (LObject) object1;
        LObject lo2 = (LObject) object2;
        LList l1, l2;
        if(lo1 instanceof LList)
            l1 = (LList) lo1;
        else
            l1 = (LList) (lo1).getValue();

        if(lo2 instanceof LList)
            l2 = (LList) lo2;
        else
            l2 = (LList) (lo2).getValue();
        if(LObject.equals(l1, l2))
            solver.fail(aConstraint);
        if(l1.isBound() && l1.occurs(l2) || l2.isBound() && l2.occurs(l1)){
            aConstraint.setSolved(true);
            return;
        }
        if(!l1.isBound() || !l2.isBound())
            return;
        if(l1.isEmpty() && !l2.isEmpty()) {
            aConstraint.setSolved(true);
            return;
        }
        if(l2.isEmpty() && !l1.isEmpty()){
            aConstraint.setSolved(true);
            return;
        }
        if(l1.equals(l2))
            solver.fail(aConstraint);
        if(l1.isGround() && l2.isGround()){
            aConstraint.setSolved(true);
            return;
        }
        aConstraint.setSolved(true);
        boolean flag = true;
        Iterator<Object> iterator1 = l1.iterator();
        Iterator<Object> iterator2 = l2.iterator();
        while(iterator1.hasNext() && iterator2.hasNext()){
            Object obj1 = iterator1.next();
            Object obj2 = iterator2.next();
            if(!LObject.isGround(obj1) || !LObject.isGround(obj2))
                continue;

            if(!LObject.equals(obj1,obj2))
                return;

        }


            AConstraint neq1 = new AConstraint(
                    Environment.neqCode, l1.getOne(),
                    l2.getOne()
            );
            AConstraint neq2 = new AConstraint(
                    Environment.neqCode, l1.removeOne(),
                    l2.removeOne()
            );

            solver.add(neq1.or(neq2));

        solver.storeUnchanged = false;

    }

    /**
     * Rewrite rule for inequality between generic objects (not logical objects).
     * @param object1 first generic object.
     * @param object2 second generic object.
     * @param aConstraint inequality atomic constraint.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    private void neqObj(@NotNull Object object1, @NotNull Object object2, @NotNull AConstraint aConstraint) {
        assert object1 != null;
        assert object2 != null;
        assert aConstraint != null;
        assert aConstraint.constraintKindCode == Environment.neqCode;
        assert aConstraint.argument1 != null;
        assert aConstraint.argument2 != null;

        if (object1.equals(object2))
            solver.fail(aConstraint);
        else
            aConstraint.setSolved(true);
        return;
    }

}
