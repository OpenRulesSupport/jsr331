package jsetl;

import jsetl.annotation.NotNull;

/**
 * Rewrite rules for constraints on binary relations ({@code LRel, LMap}).
 * Implemented constraints include pointwise composition, range, amd so on.
 */
class RwRulesBR extends LibConstraintsRules {

    //////////////////////////////////////////////////////
    ////////////////// DATA MEMBERS //////////////////////
    //////////////////////////////////////////////////////

    /**
     * This handler is used to execute immediately equality constraints.
     */
    private RwRulesEq eqHandler;


    //////////////////////////////////////////////////////
    ////////////////// CONSTRUCTORS //////////////////////
    //////////////////////////////////////////////////////

    /**
     * Constructs an instance of rewrite rules and stores a reference to the solver.
     * @param solver reference to the solver.
     */
    public RwRulesBR(@NotNull Solver solver) {
        super(solver);
        eqHandler = new RwRulesEq(solver);
    }


    //////////////////////////////////////////////////////
    ////////////////// PROTECTED METHODS /////////////////
    //////////////////////////////////////////////////////

    /**
     * Uses the appropriate method to solve the given atomic constraint.
     * @param aConstraint atomic constraint to solve.
     * @return {@code true} if a rule was applied, {@code false} otherwise.
     * @throws jsetl.exception.Fail if the constraint is unsatisfiable.
     */
    @Override
    protected boolean solveConstraint(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;

        if (aConstraint.constraintKindCode == Environment.pfRanCode || aConstraint.constraintKindCode == Environment.brRanCode)
            	range(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.pfDomCode)
            	mapDomain(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.brDomCode)
        		relationDomain(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.pfCompCode)
            	mapComp(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.brCompCode)
        		relComp(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.idCode)
                id(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.invCode)
                inv(aConstraint);
        else if (aConstraint.constraintKindCode == Environment.pfunCode)
        	pfun(aConstraint);
        else if(aConstraint.constraintKindCode == Environment.subsetCompCode)
            subsetComp(aConstraint);
        else if(aConstraint.constraintKindCode == Environment.compSubsetCode)
            compSubset(aConstraint);
        else 
                return false;
        return true;
    }

    /**
     * Rewrite rule for the domain of logical maps (dom(R,A): the domain of R is A).
     * The first argument of the constraint is the logical map.
     * The second argument of the constraint is its domain.
     * @param aConstraint: the constraint to be rewritten.
     * @throws jsetl.exception.Fail if the constraint is not satisfiable.
     */
    protected void mapDomain(@NotNull AConstraint aConstraint)  {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet;
        assert aConstraint.argument2 instanceof LSet;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.pfDomCode;
        
        manageEquChains(aConstraint);

        LSet a = (LSet) aConstraint.argument2;
        LSet r = (LSet) aConstraint.argument1;

        // dom(r,r) or dom(r,{})
        if(a.equals(r) || (a.isInitialized() && a.isEmpty())){
            aConstraint.argument1 = r;
            aConstraint.argument2 = LMap.empty();
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;

            return;
        }

        //dom({},a)
        if(r.isInitialized() && r.isEmpty()){
            aConstraint.argument2 = a;
            aConstraint.argument1 = LMap.empty();
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;

            return;
        }

        if(r.isInitialized()){

            LPair tmp = (LPair)r.getOne();
            LSet rest = new LSet();
            solver.add(r.eq(rest.ins(tmp)));
            solver.add(rest.ncontains(tmp));

            LSet rs = new LSet();
            Object t = tmp.getFirst();
            LSet tmpDom;
            if(t != null){
                tmpDom  = rs.ins(t);
            }else{
                LVar x = new LVar();
                tmpDom = rs.ins(x);
                solver.add(tmp.eq(new LPair(x, new LVar())));
            }

            solver.add(new AConstraint(Environment.eqCode, a, tmpDom));
            aConstraint.argument1 = rest;
            aConstraint.argument2 = rs;
            solver.storeUnchanged = false;
            return;
        }


        if(a.isInitialized()){

            Object tmp = a.getOne();
            LSet rest = new LSet();
            LMap rs = new LMap();
            solver.add(a.eq(rest.ins(tmp)));
            LMap tmpMap = rs.ins(new LPair(tmp,new LVar()));
            solver.add(new AConstraint(Environment.eqCode, r, tmpMap));
            solver.add(new AConstraint(Environment.ninCode, tmp, rest));
            aConstraint.argument1 = rs;
            aConstraint.argument2 = rest;
            solver.storeUnchanged = false;
            return;
        }

    }

    /**
     * Rewrite rule for the domain of logical binary relations (dom(R,A): the domain of R is A).
     * The first argument of the constraint is the logical relation.
     * The second argument of the constraint is its domain.
     * @param aConstraint atomic constraint to rewrite.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void relationDomain(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet;
        assert aConstraint.argument2 instanceof LSet;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.brDomCode;

        manageEquChains(aConstraint);

        LSet r = (LSet) aConstraint.argument1;
        LSet a = (LSet) aConstraint.argument2;

        if(a.equals(r) || a.isInitialized() && a.isEmpty()){

            aConstraint.argument2 = LRel.empty();
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        }

        if(r.isInitialized() && r.isEmpty()){

            aConstraint.argument1 = LSet.empty();
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        }

        if(!r.isInitialized() && a.isInitialized() && !a.isEmpty()
                && a.countAllElements() == 1) {

            LRel app = LRel.empty().ins(new LPair(a.getOne(),a.getOne()));
            LPair newPair = new LPair(a.getOne(),new LVar());
            LRel N = new LRel();
            LRel newMap = N.ins(newPair);
            solver.add(new AConstraint(Environment.brCompCode, app, N,N));
            aConstraint.argument1 = r;
            aConstraint.argument2 = newMap;
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        }

        if(!r.isInitialized() && a.isInitialized() && !a.isEmpty()) {

            LSet app = LSet.empty().ins(a.getOne());
            LSet ra = (LSet) a.removeOne();
            LRel N1 = new LRel();
            LRel N2 = new LRel();
            solver.add(new AConstraint(Environment.brDomCode, N1, app));
            solver.add(new AConstraint(Environment.unionCode, N1, N2,r));
            aConstraint.argument1 = N2;
            aConstraint.argument2 = ra;
            relationDomain(aConstraint);
            solver.storeUnchanged = false;
            return;
        }

        if(r.isInitialized()){
            LPair tmp = (LPair)r.getOne();
            LSet rest = r.removeOne();
            LSet N1 = new LSet();
            LSet tmpDomain = N1.ins(tmp.getFirst());
            solver.add(new AConstraint(Environment.eqCode, a, tmpDomain));

            aConstraint.argument1 = rest;
            aConstraint.argument2 = N1;
            relationDomain(aConstraint);
            solver.storeUnchanged = false;
        }

    }

    /**
     * Rewrite rule for the range of logical binary relations and logical maps (ran(R,A): the range of R is A).
     * The first argument of the constraint is the logical relation or logical map.
     * The second argument of the constraint is its domain.
     * @param aConstraint atomic constraint to rewrite.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void range(@NotNull AConstraint aConstraint)  {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet;
        assert aConstraint.argument2 instanceof LSet;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.pfRanCode || aConstraint.constraintKindCode == Environment.brRanCode;

        manageEquChains(aConstraint);

        LSet r = (LSet) aConstraint.argument1;
        LSet a = (LSet) aConstraint.argument2;

        if(a.equals(r) || a.isInitialized() && a.isEmpty()){

            aConstraint.argument2 = LRel.empty();
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        }

        if(r.isInitialized() && r.isEmpty()){

            aConstraint.argument1 = LSet.empty();
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        }

        // RECURSIVE CASES
        if(!r.isInitialized() && a.isInitialized() && !a.isEmpty() &&
                a.countAllElements()==1 && a.getTail().isInitialized()) {

            LPair newPair = new LPair(new LVar(),a.getOne());

            LRel app;
            LRel N;
            LRel newMap;


            // change type of variable depending on the actual kind of constraint (range of relations or
            // range of maps.
            if(aConstraint.constraintKindCode == Environment.brRanCode) {
                app = LRel.empty().ins(new LPair(a.getOne(),a.getOne()));
                N = new LRel();
                newMap = N.ins(newPair);

                solver.add(new AConstraint(Environment.brCompCode, N, app,N));
            }
            else {
                app = LMap.empty().ins(new LPair(a.getOne(),a.getOne()));
                N = new LMap();
                newMap = N.ins(newPair);
                solver.add(new AConstraint(Environment.pfCompCode, N, app,N));
            }


            aConstraint.argument1 = r;
            aConstraint.argument2 = newMap;
            aConstraint.constraintKindCode = Environment.eqCode;

            solver.storeUnchanged = false;
            return;
        }

        if(!r.isInitialized() && a.isInitialized() && !a.isEmpty()) {

            LSet app = LSet.empty().ins(a.getOne());
            LSet ra = (LSet) a.removeOne();
            LRel N1 = new LRel();
            LRel N2 = new LRel();
            solver.add(new AConstraint(Environment.pfRanCode, N1, app));

            solver.add(new AConstraint(Environment.unionCode, N1, N2,r));
            aConstraint.argument1 = N2;
            aConstraint.argument2 = ra;
            range(aConstraint);
            solver.storeUnchanged = false;
            return;
        }

        if(r.isInitialized()){

            LPair tmp = (LPair)r.getOne();
            LSet rest = r.removeOne();
            LSet N1 = new LSet();
            LSet tmpRange = N1.ins(tmp.getSecond());
            solver.add(new AConstraint(Environment.eqCode, a, tmpRange));

            aConstraint.argument1 = rest;
            aConstraint.argument2 = N1;
            range(aConstraint);
            solver.storeUnchanged = false;
        }

    }

    /**
     * Rewrite rule for the pfun (partial function) constraint (pfun(A): A is a partial function).
     * The first argument of the constraint is an {@code LSet} that
     * must denote a partial function.
     * @param aConstraint atomic constraint to rewrite.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void pfun(AConstraint aConstraint)  {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet;
        assert aConstraint.argument2 == null;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.pfunCode;

        manageEquChains(aConstraint);

        LSet lSet = (LSet) aConstraint.argument1;

        // lSet = {}
        if(lSet.isInitialized() && lSet.isEmpty()){
            aConstraint.setSolved(true);
            return;
        }

        // lSet = {(x1,y1),...,(xn,yn)}
        if(lSet.isInitialized() && LMap.isDomainGround(lSet))
            if(LMap.pfunCheck(lSet.toArrayList())){
                aConstraint.setSolved(true);
                return;
            }
            else
                solver.fail(aConstraint);


        // lSet = {(x,y)| R}
        if(lSet.isInitialized()) {

            LPair head = (LPair) lSet.getOne();

            LMap R1 = new LMap();
            LSet D = new LSet();

            AConstraint un = new AConstraint(Environment.eqCode, lSet, R1.ins(head));
            solver.add(un);

            solver.add(R1.dom(D));
            solver.add(head.nin(R1));
            solver.add(D.ncontains(head.getFirst()));

            aConstraint.argument1 =R1;

            solver.storeUnchanged = false;
        }
    }

    /**
     * Rewrite rule for the composition of logical maps (comp(R,S,Q): R composed with S equals Q).
     * @param aConstraint: the constraint to be rewritten.
     * @throws jsetl.exception.Fail if the constraint is not satisfiable.
     */
    protected void mapComp(@NotNull AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet;
        assert aConstraint.argument2 instanceof LSet;
        assert aConstraint.argument3 instanceof LSet;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.pfCompCode;

        manageEquChains(aConstraint);

        LSet r = (LSet) aConstraint.argument1;
        LSet s = (LSet) aConstraint.argument2;
        LSet q = (LSet) aConstraint.argument3;

        if(r.isInitialized() && r.isEmpty()){
            solver.add(new AConstraint(Environment.eqCode, q, LMap.empty()));
            aConstraint.setSolved(true);
            return;
        }

        if(s.isInitialized() && s.isEmpty()){
            solver.add(new AConstraint(Environment.eqCode, q, LMap.empty()));
            aConstraint.setSolved(true);
            return;
        }

        if(q.isInitialized() && q.isEmpty() && s.isInitialized() && !s.isEmpty() && r.isInitialized() && !r.isEmpty()){

            LSet rr = new LSet();
            LSet ds = new LSet();

            solver.add(new AConstraint(Environment.pfRanCode, r, rr));
            solver.add(new AConstraint(Environment.pfDomCode, s, ds));

            solver.add(new AConstraint(Environment.disjCode, rr, ds));

            aConstraint.setSolved(true);
            return;

        }

        if(q.isInitialized() && !q.isEmpty()) {

            LPair hq = (LPair) q.getOne();
            LSet rq = q.removeOne();

            LMap N1= new LMap();
            LMap N2 = new LMap();

            LVar n = new LVar();
            LPair newRLPair = new LPair(hq.getFirst(),n);
            LMap appR = N1.ins(newRLPair);

            LPair newSLPair = new LPair(n,hq.getSecond());
            LMap appS = N2.ins(newSLPair);

            solver.add(new AConstraint(Environment.eqCode, r, appR));
            solver.add(new AConstraint(Environment.eqCode, s, appS));

            aConstraint.argument1 = N1;
            aConstraint.argument3 = rq;

            mapComp(aConstraint);

            solver.storeUnchanged = false;
            return;
        }

        if(r.isInitialized() && !r.isEmpty() && !q.isInitialized()){

            LPair hr = (LPair)r.getOne();
            LSet rr = r.removeOne();


            switch (aConstraint.alternative) {
                case 0:
                    solver.addChoicePoint(aConstraint);

                    LVar n = new LVar();

                    LPair yn = new LPair(hr.getSecond(),n);
                    LPair xn = new LPair(hr.getFirst(),n);

                    LMap N1 = new LMap();
                    LMap N2 = new LMap();

                    solver.add(new AConstraint(Environment.eqCode, s, N1.ins(yn)));
                    solver.add(new AConstraint(Environment.eqCode, q, N2.ins(xn)));

                    aConstraint.argument1 = rr;
                    aConstraint.argument3 = N2;
                    mapComp(aConstraint);
                    solver.storeUnchanged = false;
                    return;
                case 1:
                    aConstraint.alternative = 0;

                    LSet doms = new LSet();

                    LMap N1_ = new LMap();

                    solver.add(new AConstraint(Environment.pfDomCode, s, doms));
                    solver.add(new AConstraint(Environment.ninCode, hr.getSecond(), N1_));

                    aConstraint.argument1 = rr;

                    mapComp(aConstraint);
                    solver.storeUnchanged = false;
                    return;
            }
        }
    }

    /**
     * Rewrite rule for the composition of logical relations (comp(R,S,Q): R composed with S equals Q).
     * @param aConstraint: the constraint to be rewritten.
     * @throws jsetl.exception.Fail if the constraint is not satisfiable.
     */
    protected void relComp(AConstraint aConstraint) {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet;
        assert aConstraint.argument2 instanceof LSet;
        assert aConstraint.argument3 instanceof LSet;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.brCompCode;

        manageEquChains(aConstraint);

        LSet r = (LSet) aConstraint.argument1;
        LSet s = (LSet) aConstraint.argument2;
        LSet q = (LSet) aConstraint.argument3;

        if(solver.getOptimizationOptions().areFastCompRulesEnabled()){
            solver.add(new AConstraint(Environment.subsetCompCode, r, s,q));
            solver.add(new AConstraint(Environment.compSubsetCode, r, s, q));
            aConstraint.setSolved(true);
            return;
        }

        if(r.isInitialized() && r.isEmpty()){

            solver.add(new AConstraint(Environment.eqCode, q, LRel.empty()));
            aConstraint.setSolved(true);
            return;
        }

        if(s.isInitialized() && s.isEmpty()){

            solver.add(new AConstraint(Environment.eqCode, q, LRel.empty()));
            aConstraint.setSolved(true);
            return;
        }

        if(r.isInitialized() && s.isInitialized() && !r.isEmpty() && !s.isEmpty() &&
                r.countAllElements() == 1 && s.countAllElements() == 1 &&
                r.getTail().isInitialized()
                && s.getTail().isInitialized()) {

            LPair Rpair = (LPair)r.getOne();
            LPair Spair = (LPair)s.getOne();

            switch (aConstraint.alternative) {
                case 0:
                    solver.addChoicePoint(aConstraint);

                    LRel rel = LRel.empty().ins(new LPair(Rpair.getFirst(),Spair.getSecond()));

                    solver.add(new AConstraint(Environment.eqCode, Rpair.getSecond(), Spair.getFirst()));
                    solver.add(new AConstraint(Environment.eqCode, q, rel));
                    aConstraint.setSolved(true);
                    return;

                case 1:

                    LRel emptyRel = LRel.empty();

                    solver.add(new AConstraint(Environment.neqCode, Rpair.getSecond(), Spair.getFirst()));
                    solver.add(new AConstraint(Environment.eqCode, q, emptyRel));

                    aConstraint.setSolved(true);
                    return;
            }
        }

        if(q.isInitialized() && q.isEmpty() && r.isInitialized() && s.isInitialized()) {
            LPair Rpair = (LPair)r.getOne();
            LPair Spair = (LPair)s.getOne();

            LSet resR = r.removeOne();
            LSet resS = s.removeOne();

            LRel appR = LRel.empty().ins(Rpair);
            LRel appS = LRel.empty().ins(Spair);

            LRel emptyLRel = LRel.empty();

            solver.add(new AConstraint(Environment.neqCode, Rpair.getSecond(), Spair.getFirst()));
            solver.add(new AConstraint(Environment.brCompCode, appR, resS,emptyLRel));
            solver.add(new AConstraint(Environment.brCompCode, resR, appS,emptyLRel));

            aConstraint.argument1 = resR;
            aConstraint.argument2 = resS;
            aConstraint.argument3 = emptyLRel;

            relComp(aConstraint);
            solver.storeUnchanged = false;
            return;
        }


        if(r.isInitialized() && !r.isEmpty() &&
                s.isInitialized() && !s.isEmpty() &&
                q.isInitialized() && !q.isEmpty() &&
                s.removeOne().equals(q.removeOne())
                && r.removeOne().isEmpty()){

            LPair rPair = (LPair) r.getOne();
            LPair sPair = (LPair) s.getOne();
            LPair qPair = (LPair) q.getOne();

            if(rPair.getFirst().equals(rPair.getSecond()) &&
                    rPair.getFirst().equals(sPair.getFirst()) &&
                    rPair.getFirst().equals(qPair.getFirst()) &&
                    sPair.getSecond().equals(qPair.getSecond())){

                aConstraint.argument2 = s.removeOne();
                aConstraint.argument3 = q.removeOne();

                aConstraint.alternative = 0;

                solver.storeUnchanged = false;
                return;
            }
        }

        if(s.isInitialized() && !s.isEmpty() &&
                r.isInitialized() && !r.isEmpty() &&
                q.isInitialized() && !q.isEmpty() &&
                r.removeOne().equals(q.removeOne())
                && s.removeOne().isEmpty()){

            LPair rPair = (LPair) r.getOne();
            LPair sPair = (LPair) s.getOne();
            LPair qPair = (LPair) q.getOne();

            if(sPair.getFirst().equals(sPair.getSecond()) &&
                    rPair.getSecond().equals(sPair.getFirst()) &&
                    sPair.getSecond().equals(qPair.getSecond()) &&
                    rPair.getFirst().equals(qPair.getFirst())){

                aConstraint.argument1 = r.removeOne();
                aConstraint.argument3 = q.removeOne();

                aConstraint.alternative = 0;

                solver.storeUnchanged = false;
                return;
            }
        }


        if(!q.isInitialized() && r.isInitialized() && s.isInitialized() ) {
            LPair Rpair = (LPair)r.getOne();
            LPair Spair = (LPair)s.getOne();

            LSet resR = r.removeOne();
            LSet resS = s.removeOne();

            LRel appR = LRel.empty().ins(Rpair);
            LRel appS = LRel.empty().ins(Spair);

            LRel N1 = new LRel();
            LRel N2 = new LRel();
            LRel N3 = new LRel();
            LRel N4 = new LRel();

            LRel Nunion1 = new LRel();
            LRel Nunion2 = new LRel();

            solver.add(new AConstraint(Environment.brCompCode, appR, appS,N1));
            solver.add(new AConstraint(Environment.brCompCode, appR, resS,N2));
            solver.add(new AConstraint(Environment.brCompCode, resR, appS,N3));

            solver.add(new AConstraint(Environment.unionCode, N1, N2,Nunion1));
            solver.add(new AConstraint(Environment.unionCode, Nunion1, N3,Nunion2));
            solver.add(new AConstraint(Environment.unionCode, Nunion2, N4,q));

            aConstraint.argument1 = resR;
            aConstraint.argument2 = resS;
            aConstraint.argument3 = N4;

            relComp(aConstraint);
            solver.storeUnchanged = false;
            return;
        }


        if(q.isInitialized() && !q.isEmpty()) {

            LPair Qpair = new LPair(new LVar(), new LVar());
            solver.add(solver.indexOf(aConstraint)+1, new AConstraint(Environment.eqCode, Qpair, q.getOne()));
            solver.storeUnchanged = false;
            LSet resQ = (LSet)q.removeOne();

            LRel N1 = new LRel();
            LRel N2 = new LRel();
            LRel N3 = new LRel();
            LRel N4 = new LRel();
            LRel N5 = new LRel();

            LRel Nx = new LRel();
            LRel Nz = new LRel();
            LRel Nst = new LRel();
            LRel Nrt = new LRel();

            LVar n = new LVar();
            LPair app1 = new LPair(Qpair.getFirst(),n);
            LPair app2 = new LPair(n,Qpair.getSecond());

            LPair appxx = new LPair(Qpair.getFirst(),Qpair.getFirst());
            LPair appzz = new LPair(Qpair.getSecond(),Qpair.getSecond());

            LRel ZZ = LRel.empty().ins(appzz);
            LRel XX = LRel.empty().ins(appxx);

            LRel appNx = N1.ins(app1);
            solver.add(N1.ncontains(app1));
            LRel appNz = N2.ins(app2);
            solver.add(N2.ncontains(app2));

            LRel Nunion1 = new LRel();

            solver.add(app1.in(r));
            solver.add(app2.in(s));


            solver.add(new AConstraint(Environment.eqCode, Nx, appNx));
            solver.add(new AConstraint(Environment.eqCode, Nz, appNz));
            solver.add(new AConstraint(Environment.brCompCode, XX, N1,N1));
            solver.add(new AConstraint(Environment.brCompCode, N2, ZZ,N2));
            solver.add(new AConstraint(Environment.brCompCode, Nx, Nst,N3));
            solver.add(new AConstraint(Environment.brCompCode, Nrt, Nz,N4));
            solver.add(new AConstraint(Environment.unionCode, N3, N4,Nunion1));
            solver.add(new AConstraint(Environment.unionCode, Nunion1, N5,resQ));
            solver.add(new AConstraint(Environment.unionCode, Nx, Nrt,r));
            solver.add(new AConstraint(Environment.unionCode, Nz, Nst,s));

            aConstraint.argument1 = Nrt;
            aConstraint.argument2 = Nst;
            aConstraint.argument3 = N5;

            relComp(aConstraint);
            solver.storeUnchanged = false;

            return;
        }
    }

    /**
     * Rewrite rule for the identity of logical binary relations (id(R,A): R is the identity over the set A).
     * @param aConstraint atomic constraint to rewrite.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void id(@NotNull AConstraint aConstraint)  {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet;
        assert aConstraint.argument2 instanceof LSet;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.idCode;

        manageEquChains(aConstraint);

        LSet r = (LSet) aConstraint.argument1;
        LSet a = (LSet) aConstraint.argument2;

        if(!r.isInitialized() && r.equals(a)) {
            aConstraint.argument2 = LRel.empty();
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        }

        //id(r,{})
        if(a.isInitialized() && a.isEmpty()) {

            aConstraint.argument2 = LRel.empty();
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        }

        //id({},a)
        if(r.isInitialized() && r.isEmpty()) {

            aConstraint.argument1 = LRel.empty();
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        }

        if(r.isInitialized() && !r.isEmpty()) {
            LPair hr = (LPair)r.getOne();
            LSet rr = r.removeOne();

            LSet appSet = new LSet().ins(hr.getFirst());

            solver.add(new AConstraint(Environment.eqCode, hr.getFirst(), hr.getSecond()));
            solver.add(new AConstraint(Environment.eqCode, a, appSet));

            aConstraint.argument1 = rr;
            aConstraint.argument2 = appSet.removeOne();

            id(aConstraint);

            solver.storeUnchanged = false;
            return;
        }

        if(a.isInitialized() && !a.isEmpty()) {
            Object ha = a.getOne();
            LSet ra = a.removeOne();

            LPair appPair = new LPair(ha,ha);
            LRel appMap = new LRel().ins(appPair);

            solver.add(new AConstraint(Environment.eqCode, r, appMap));

            aConstraint.argument1 = appMap.removeOne();
            aConstraint.argument2 = ra;

            id(aConstraint);

            solver.storeUnchanged = false;
        }
    }


    /**
     * Rewrite rule for the inverse of logical binary relations (inv(R,S): R is the inverse of S).
     * @param aConstraint atomic constraint to rewrite.
     * @throws jsetl.exception.Fail if inconsistencies are found.
     */
    protected void inv(@NotNull AConstraint aConstraint)  {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet;
        assert aConstraint.argument2 instanceof LSet;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.invCode;

        manageEquChains(aConstraint);

        LSet r = (LSet) aConstraint.argument1;
        LSet s = (LSet) aConstraint.argument2;

        // BASE CASES
        if(s.initialized && s.isEmpty()) {

            aConstraint.argument2 = LRel.empty();
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        }
        if(r.isInitialized() && r.isEmpty()) {

            aConstraint.argument1 = LRel.empty();
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        }

        // RECURSIVE CASES
        if(r.isInitialized() && !r.isEmpty()) {

            LPair hr = (LPair) r.getOne();
            LSet rr = r.removeOne();

            LPair pairApp = new LPair(hr.getSecond(),hr.getFirst());
            LRel mapApp = new LRel().ins(pairApp);

            solver.add(solver.indexOf(aConstraint)+1, new AConstraint(Environment.eqCode, s, mapApp));

            aConstraint.argument1 = rr;
            aConstraint.argument2 = mapApp.removeOne();

            inv(aConstraint);

            solver.storeUnchanged = false;
            return;
        }

        if(s.isInitialized() && !s.isEmpty()) {

            LPair hs = (LPair) s.getOne();
            LSet rs = s.removeOne();

            LPair pairApp = new LPair(hs.getSecond(),hs.getFirst());
            LRel mapApp = new LRel().ins(pairApp);

            solver.add(solver.indexOf(aConstraint)+1, new AConstraint(Environment.eqCode, r, mapApp));

            aConstraint.argument1 = mapApp.removeOne();
            aConstraint.argument2 = rs;
            inv(aConstraint);
            solver.storeUnchanged = false;
        }
    }

    /**
     * Rewrite rule for the composition subset of logical relations (compSubset(R,S,Q): (R composed with S) subset or equals Q).
     * @param aConstraint: the constraint to be rewritten.
     * @throws jsetl.exception.Fail if the constraint is not satisfiable.
     */
    private void compSubset(@NotNull AConstraint aConstraint)  {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet;
        assert aConstraint.argument2 instanceof LSet;
        assert aConstraint.argument3 instanceof LSet;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.compSubsetCode;

        manageEquChains(aConstraint);

        LSet r = (LSet) aConstraint.argument1;
        LSet s = (LSet) aConstraint.argument2;
        LSet t = (LSet) aConstraint.argument3;

        if(r.isBoundAndEmpty() || s.isBoundAndEmpty()){
            aConstraint.setSolved(true);
            return;
        }

        if(!r.isInitialized() || !s.isInitialized())
            return;

        Object obj1 = r.getOne();
        LPair xa;
        if(obj1 instanceof LPair && ((LPair) obj1).isInitialized())
            xa = (LPair) obj1;
        else {
            xa = new LPair(new LVar(), new LVar());
            solver.add(new AConstraint(Environment.eqCode, xa, obj1));
        }

        Object obj2 = s.getOne();
        LPair by;
        if(obj2 instanceof LPair && ((LPair) obj2).isInitialized())
            by = (LPair) obj2;
        else {
            by = new LPair(new LVar(), new LVar());
            solver.add(new AConstraint(Environment.eqCode, by, obj2));
        }

        Object x = xa.getFirst();
        Object a = xa.getSecond();
        Object b = by.getFirst();
        Object y = by.getSecond();

        solver.add(new AConstraint(
                Environment.compSubsetCode, r.removeOne(),
                LRel.empty().ins(by),
                t
        ));

        solver.add(new AConstraint(
                Environment.compSubsetCode, LRel.empty().ins(xa),
                s.removeOne(),
                t
        ));

        solver.add(new AConstraint(
                Environment.compSubsetCode, r.removeOne(),
                s.removeOne(),
                t
        ));

        boolean flag = true;
        if(t.isInitialized())
            for(Object o : t){
                if(o instanceof LPair && ((LPair) o).isInitialized()){
                    LPair p = (LPair) o;
                    if(LObject.equals(x, p.getFirst()) && LObject.equals(p.getSecond(), y)){
                        flag = false;
                        break;
                    }
                }
            }
            if(LObject.equals(a,b))
                aConstraint.alternative = 1;
        if(flag)
        switch(aConstraint.getAlternative()){
            case 0:
                if(!t.isBoundAndEmpty() && (!LObject.isGround(a) || !LObject.isGround(b)))
                    solver.addChoicePoint(aConstraint);
                AConstraint neq = new AConstraint(Environment.neqCode, a, b);
                solver.add(solver.indexOf(aConstraint) + 1, neq);
                eqHandler.neq(neq);
                aConstraint.setSolved(true);
                solver.storeUnchanged = false;

                return;
            case 1:
                if(t.isBoundAndEmpty())
                    solver.fail(aConstraint);

                AConstraint eq = new AConstraint(Environment.eqCode, a, b);
                solver.add(solver.indexOf(aConstraint) + 1, eq);
                eqHandler.eq(eq);

                solver.add(new LPair(x,y).in(t));
                solver.storeUnchanged = false;
                aConstraint.setSolved(true);
                return;
        }
        else
            aConstraint.setSolved(true);
    }

    /**
     * Rewrite rule for the subset composition of logical relations (subsetComp(R,S,T): T subset or equals (R composed with S)).
     * @param aConstraint: the constraint to be rewritten.
     * @throws jsetl.exception.Fail if the constraint is not satisfiable.
     */
    private void subsetComp(@NotNull AConstraint aConstraint)  {
        assert aConstraint != null;
        assert aConstraint.argument1 instanceof LSet;
        assert aConstraint.argument2 instanceof LSet;
        assert aConstraint.argument3 instanceof LSet;
        assert aConstraint.argument4 == null;
        assert aConstraint.constraintKindCode == Environment.subsetCompCode;

        manageEquChains(aConstraint);

        LSet r = (LSet) aConstraint.argument1;
        LSet s = (LSet) aConstraint.argument2;
        LSet t = (LSet) aConstraint.argument3;

        if(t.isBoundAndEmpty()){
            aConstraint.setSolved(true);
            return;
        }

        if(r.isBoundAndEmpty() || s.isBoundAndEmpty()){
            aConstraint.argument1 = t;
            aConstraint.argument2 = LRel.empty();
            aConstraint.argument3 = null;
            aConstraint.alternative = 0;
            aConstraint.constraintKindCode = Environment.eqCode;
            solver.storeUnchanged = false;
            return;
        }

        if(t.isInitialized()){
            Object obj =  t.getOne();
            LPair xy;
            if(obj instanceof LPair && ((LPair) obj).isInitialized())
                xy = (LPair) obj;
            else {
                xy = new LPair(new LVar(), new LVar());
                AConstraint eq = new AConstraint(Environment.eqCode, xy, obj);
                solver.add(eq);
                eqHandler.eq(eq);
            }
            Object x = xy.getFirst();
            Object y = xy.getSecond();
            boolean flag = true;

            if(obj instanceof LPair) {
                LPair p = (LPair) obj;
                for (Object o1 : r) {
                    if(!flag)
                        break;

                    if(!(o1 instanceof LPair))
                        continue;

                    LPair p1 = (LPair) o1;
                    if(!p1.isInitialized() || !LObject.equals(p1.getFirst(), p.getFirst()))
                        continue;

                    for (Object o2 : s) {
                        if (o2 instanceof LPair) {
                            LPair p2 = (LPair) o2;
                            if (p2.isInitialized() && LObject.equals(p2.getSecond(), p.getSecond()) && LObject.equals(p1.getSecond(), p2.getFirst())) {
                                flag = false;
                                break;
                            }
                        }
                    }

                }
            }


            if(flag){
                LVar n = new LVar();
                solver.add(new LPair(x,n).in(r));
                solver.add(new LPair(n,y).in(s));
            }


            aConstraint.argument3 = t.removeOne();
            aConstraint.alternative = 0;

            solver.storeUnchanged = false;
            return;
        }
        else{
            switch (aConstraint.getAlternative()){
                case 0:
                    solver.addChoicePoint(aConstraint);
                    aConstraint.argument3 = null;
                    aConstraint.argument4 = null;
                    aConstraint.argument1 = t;
                    aConstraint.argument2 = LRel.empty();
                    aConstraint.constraintKindCode = Environment.eqCode;
                    eqHandler.eq(aConstraint);
                case 1:
            }
        }

    }
    
}
