package javax.constraints.impl.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.constraints.impl.Problem;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.converter.Converter;
import jp.kobe_u.sugar.csp.BooleanVariable;
import jp.kobe_u.sugar.csp.CSP;
import jp.kobe_u.sugar.csp.IntegerVariable;
import jp.kobe_u.sugar.encoder.Encoder;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class Sat4jSolver {
    public CSP sugarCSP;
    public Converter sugarConverter;
    public Encoder encoder;
    public Sat4jProblem sat4jProblem;
    public ISolver sat4j;

    public Sat4jSolver(Problem problem) {
        sugarCSP = problem.sugarCSP;
        sugarConverter = problem.sugarConverter;
        sat4jProblem = new Sat4jProblem();
        sat4j = SolverFactory.newDefault();
        encoder = new Encoder(sugarCSP);
    }

    public class Sat4jProblem extends jp.kobe_u.sugar.encoder.Problem {
        List<IConstr> added = new ArrayList<IConstr>();
        boolean isUnsatisfiable = false;

        @Override
        public void clear() throws SugarException {
            super.clear();
            added = new ArrayList<IConstr>();
            isUnsatisfiable = false;
        }

        @Override
        public void commit() throws SugarException {
            super.commit();
            added = new ArrayList<IConstr>();
        }

        @Override
        public void cancel() throws SugarException {
            super.cancel();
            for (IConstr c : added)
                sat4j.removeConstr(c);
            added = new ArrayList<IConstr>();
            isUnsatisfiable = false;
        }

        @Override
        public void done() throws SugarException {
        }

        @Override
        public void addVariables(int n) {
            variablesCount += n;
            sat4j.newVar(sat4j.nVars() + n);
            sat4j.setExpectedNumberOfClauses(100000);
        }

        @Override
        public void addNormalizedClause(int[] clause) {
            try {
                VecInt lits = new VecInt(clause);
                IConstr c = sat4j.addClause(lits);
                if (c != null && added != null)
                    added.add(c);
            } catch (ContradictionException e) {
                isUnsatisfiable = true;
            }
        }
    }
    
    public void init() throws SugarException {
        sat4jProblem = new Sat4jProblem();
        // sat4jProblem.clear();
        sat4j = SolverFactory.newDefault();
        encoder = new Encoder(sugarCSP);
    }

    public void commit() throws SugarException {
        sugarCSP.commit();
        sat4jProblem.commit();
    }

    public void cancel() throws SugarException {
        sugarCSP.cancel();
        sat4jProblem.cancel();
    }

    public void encodeDelta() throws IOException, SugarException {
        encoder.encodeDelta();
    }

    public boolean encode() throws SugarException {
        sugarCSP.propagate();
        if (sugarCSP.isUnsatisfiable())
            return false;
        sugarCSP.simplify();
        encoder.encode(sat4jProblem);
        return true;
    }

    public void decode() {
        BitSet bitSet = new BitSet();
        for (int i = 1; i <= sat4j.nVars(); i++)
            bitSet.set(i, sat4j.model(i));
        for (IntegerVariable v : sugarCSP.getIntegerVariables())
            v.decode(bitSet);
        for (BooleanVariable b : sugarCSP.getBooleanVariables())
            b.decode(bitSet);
    }
    
    public boolean satSolve() throws SugarException {
        try {
            if (sat4jProblem.isUnsatisfiable)
                return false;
            boolean result = sat4j.isSatisfiable();
            return result;
        } catch (TimeoutException e) {
            throw new SugarException(e.getMessage(), e);
        }
    }
    
    public boolean find() throws SugarException {
        boolean result = satSolve();
        if (result) {
            decode();
        }
        return result;
    }

}
