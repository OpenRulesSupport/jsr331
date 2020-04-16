package jp.kobe_u.sugar.csp;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import jp.kobe_u.sugar.SugarConstants;
import jp.kobe_u.sugar.SugarException;

/**
 * This class implements a comparison literal of CSP.
 * The comparison represents the condition "linearSum != 0".
 * @see CSP
 * @see LinearSum
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class LinearNeLiteral extends LinearLiteral {

    /**
     * Constructs a new comparison literal of given linear expression.
     * @param linearSum the linear expression
     */
    public LinearNeLiteral(LinearSum linearSum) {
        super(linearSum);
        cmp = "ne";
    }
    
    @Override
    public int[] getBound(IntegerVariable v) throws SugarException {
        int lb = v.getDomain().getLowerBound();
        int ub = v.getDomain().getUpperBound();
        if (lb > ub)
            return null;
        return new int[] { lb, ub };
    }

    @Override
    public boolean isSimple() {
        return false;
    }
    
    @Override
    public boolean isValid() throws SugarException {
        IntegerDomain d = linearSum.getDomain();
        return ! d.contains(0);
    }
    
    @Override
    public boolean isUnsatisfiable() throws SugarException {
        IntegerDomain d = linearSum.getDomain();
        return d.size() == 1 && d.contains(0);
    }
    
    @Override
    public int propagate() throws SugarException {
        if (linearSum.size() == 0) {
            return 0;
        }
        int removed = 0;
        for (IntegerVariable v : linearSum.getCoef().keySet()) {
            int lb = v.getDomain().getLowerBound();
            int ub = v.getDomain().getUpperBound();
            int a = linearSum.getA(v);
            if (a > 0) {  
                IntegerDomain d1 = linearSum.getDomainExcept(v).neg().div(a);
                int b = d1.getLowerBound();
                if (d1.size() == 1 && b == lb) {
                    removed += v.bound(lb + 1, ub);
                } else if (d1.size() == 1 && b == ub) {
                    removed += v.bound(lb, ub - 1);
                }
            } else if (a < 0) {
                IntegerDomain d1 = linearSum.getDomainExcept(v).div(- a);
                int b = d1.getLowerBound();
                if (d1.size() == 1 && b == lb) {
                    removed += v.bound(lb + 1, ub);
                } else if (d1.size() == 1 && b == ub) {
                    removed += v.bound(lb, ub - 1);
                }
            }
        }
        return removed;
    }

    /* (non-Javadoc)
     * @see Literal#isSatisfied()
     */
    @Override
    public boolean isSatisfied() {
        return linearSum.getValue() != 0;
    }
}
