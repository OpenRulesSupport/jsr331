/*
 * Copyright 2003-2012 Optimatika (www.optimatika.se)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.ojalgo.optimisation;

import static org.ojalgo.constant.BigMath.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
import org.ojalgo.optimisation.integer.IntegerSolver;
import org.ojalgo.optimisation.linear.LinearSolver;
import org.ojalgo.optimisation.linear.mps.MathProgSysModel;
import org.ojalgo.optimisation.quadratic.QuadraticSolver;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;

/**
 * <p>
 * Lets you construct optimisation problems by combining mathematical
 * expressions (in terms of variables). Each expression or variable can be a
 * constraint and/or contribute to the objective function. An expression or
 * variable is turned into a constraint by setting a lower and/or upper limit.
 * Use {@linkplain ModelEntity#lower(BigDecimal)},
 * {@linkplain ModelEntity#upper(BigDecimal)} or
 * {@linkplain ModelEntity#level(BigDecimal)}.
 * An expression or variable is made part of (contributing to) the objective
 * function by setting a contribution weight. Use
 * {@linkplain ModelEntity#weight(BigDecimal)}.
 * </p>
 * <p>
 * You may think of variables as simple (the simplest possible) expressions,
 * and of expressions as weighted combinations of variables. They are both
 * model entities and it is as such they can be turned into constraints and set
 * to contribute to the objective function. Alternatively you may choose to
 * disregard the fact that variables are model entities and simply treat them as
 * index values. In this case everything (constraints and objective) needs to be
 * defined using expressions.
 * </p>
 * Basic instructions:
 * <ol>
 * <li>Define (create) a set of variables. Set contribution weights and lower/upper limits as needed.</li>
 * <li>Create a model using that set of variables.</li>
 * <li>Add expressions to the model. The model is the expression
 * factory. Set contribution weights and lower/upper limits as needed.</li>
 * <li>Instanciate a solver using the model. (Call {@linkplain #getDefaultSolver()})</li>
 * <li>Solve!</li>
 * </ol>
 * There are some restrictions on the models you can create:
 * <ul>
 * <li>No quadratic constraints</li>
 * <li>If the model is completely linear then you cannot have lower bounds
 * smaller than zero. Currently if you set a lowwer limit smaller than zero you'll
 * get an exception, and if you don't set the lower limit at all it is assumed
 * to be zero. (You should NOT rely on that assumption!)</li>
 * </ul>
 * The plan is that future versions should not have any restrictions like these.
 * 
 * @author apete
 */
public final class ExpressionsBasedModel extends AbstractModel<GenericSolver> {

    private static final String NEW_LINE = "\n";

    private static final String START_END = "############################################\n";

    public static ExpressionsBasedModel make(final MathProgSysModel aModel) {

        final MathProgSysModel.Column[] tmpActCols = aModel.getActivatorVariableColumns();
        final MathProgSysModel.Column[] tmpNegCols = aModel.getNegativeVariableColumns();
        final MathProgSysModel.Column[] tmpPosCols = aModel.getPositiveVariableColumns();
        final MathProgSysModel.Row[] tmpAllRows = aModel.getExpressionRows();

        Arrays.sort(tmpActCols);
        Arrays.sort(tmpNegCols);
        Arrays.sort(tmpPosCols);
        Arrays.sort(tmpAllRows);

        final int tmpCountActCols = tmpActCols.length;
        final int tmpCountNegCols = tmpNegCols.length;
        final int tmpCountPosCols = tmpPosCols.length;
        final int tmpCountAllRows = tmpAllRows.length;

        // Define various local variables
        MathProgSysModel.Row tmpRow;
        MathProgSysModel.Column tmpCol;
        Variable tmpVar;
        Expression tmpExpr;
        int tmpIndex;

        // Create the LinearModel variables
        final Variable[] tmpVariables = new Variable[tmpCountActCols + tmpCountNegCols + tmpCountPosCols];
        for (int i = 0; i < tmpCountActCols; i++) {

            tmpCol = tmpActCols[i];
            tmpVar = Variable.makeBinary(tmpCol.getNameForActivator());

            tmpVariables[i] = tmpVar;
        }
        for (int i = 0; i < tmpCountNegCols; i++) {

            tmpCol = tmpNegCols[i];
            tmpVar = new Variable(tmpCol.getNameForNegativePart());

            final BigDecimal tmpLowerLimit = (tmpCol.isUpperLimitSet() && !tmpCol.needsActivator()) ? tmpCol.getUpperLimit().negate().max(ZERO) : ZERO;
            final BigDecimal tmpUpperLimit = tmpCol.isLowerLimitSet() ? tmpCol.getLowerLimit().negate() : null;

            tmpVar.lower(tmpLowerLimit).upper(tmpUpperLimit).integer(tmpCol.isInteger());

            tmpVariables[tmpCountActCols + i] = tmpVar;
        }
        for (int i = 0; i < tmpCountPosCols; i++) {

            tmpCol = tmpPosCols[i];
            tmpVar = new Variable(tmpCol.getNameForPositivePart());

            final BigDecimal tmpLowerLimit = (tmpCol.isLowerLimitSet() && !tmpCol.needsActivator()) ? tmpCol.getLowerLimit().max(ZERO) : ZERO;
            final BigDecimal tmpUpperLimit = tmpCol.getUpperLimit();

            tmpVar.lower(tmpLowerLimit).upper(tmpUpperLimit).integer(tmpCol.isInteger());

            tmpVariables[tmpCountActCols + tmpCountNegCols + i] = tmpVar;
        }

        // Instantiate the LinearModel
        final ExpressionsBasedModel retVal = new ExpressionsBasedModel(tmpVariables);

        final Expression[] tmpExpressions = new Expression[tmpCountAllRows];
        final String[] tmpExpressionNames = new String[tmpCountAllRows];

        for (int i = 0; i < tmpCountAllRows; i++) {
            tmpRow = tmpAllRows[i];
            tmpExpr = retVal.addExpression(tmpRow.getName());
            tmpExpr.lower(tmpRow.getLowerLimit());
            tmpExpr.upper(tmpRow.getUpperLimit());
            tmpExpr.weight(tmpRow.getContributionWeight());
            tmpExpressions[i] = tmpExpr;
            tmpExpressionNames[i] = tmpExpr.getName();
        }

        final Expression[] tmpActExpressions = new Expression[tmpCountActCols];
        final String[] tmpActExpressionNames = new String[tmpCountActCols];

        for (int i = 0; i < tmpCountActCols; i++) {
            tmpCol = tmpActCols[i];
            tmpExpr = retVal.addExpression(tmpCol.getName());
            tmpExpr.lower(ZERO);
            tmpActExpressions[i] = tmpExpr;
            tmpActExpressionNames[i] = tmpExpr.getName();

            tmpIndex = Arrays.binarySearch(tmpPosCols, tmpCol);
            if (tmpIndex != -1) {
                tmpExpr.setLinearFactor(i, tmpCol.getLowerLimit().negate());
                tmpExpr.setLinearFactor(tmpCountActCols + tmpCountNegCols + tmpIndex, ONE);
            }
        }

        for (int i = 0; i < tmpCountNegCols; i++) {
            tmpCol = tmpNegCols[i];
            tmpVar = tmpVariables[tmpCountActCols + i];
            for (final String tmpRowKey : tmpCol.getElementKeys()) {
                tmpIndex = Arrays.binarySearch(tmpExpressionNames, tmpRowKey);
                if (tmpIndex != -1) {
                    tmpExpressions[tmpIndex].setLinearFactor(tmpCountActCols + i, tmpCol.getRowValue(tmpRowKey).negate());
                }
            }
        }
        for (int i = 0; i < tmpCountPosCols; i++) {
            tmpCol = tmpPosCols[i];
            tmpVar = tmpVariables[tmpCountActCols + tmpCountNegCols + i];
            for (final String tmpRowKey : tmpCol.getElementKeys()) {
                tmpIndex = Arrays.binarySearch(tmpExpressionNames, tmpRowKey);
                if (tmpIndex != -1) {
                    tmpExpressions[tmpIndex].setLinearFactor(tmpCountActCols + tmpCountNegCols + i, tmpCol.getRowValue(tmpRowKey));
                }
            }
        }

        return retVal;
    }

    /**
     * @deprecated v33 Use {@link #make(MathProgSysModel)} instead
     */
    @Deprecated
    public static ExpressionsBasedModel makeInstance(final MathProgSysModel aModel) {
        return ExpressionsBasedModel.make(aModel);
    }

    private final HashMap<String, Expression> myExpressions = new HashMap<String, Expression>();
    private final HashMap<String, Expression.Index> myIndexLookup = new HashMap<String, Expression.Index>();
    private transient Expression myObjectiveExpression = null;
    private final ArrayList<Variable> myVariables = new ArrayList<Variable>();

    private transient ArrayList<Variable> myPositiveVariables = null;

    private transient ArrayList<Variable> myNegativeVariables = null;

    public ExpressionsBasedModel(final Collection<? extends Variable> someVariables) {

        super();

        for (final Variable tmpVariable : someVariables) {
            this.addVariable(tmpVariable);
        }
    }

    public ExpressionsBasedModel(final Variable[] someVariables) {

        super();

        for (final Variable tmpVariable : someVariables) {
            this.addVariable(tmpVariable);
        }
    }

    @SuppressWarnings("unused")
    private ExpressionsBasedModel() {

        super();

        ProgrammingError.throwForIllegalInvocation();
    }

    protected ExpressionsBasedModel(final ExpressionsBasedModel aModel) {

        super();

        for (final Variable tmpVariable : aModel.getVariables()) {
            this.addVariable(tmpVariable.copy());
        }

        for (final Expression tmpExpression : aModel.getExpressions()) {
            myExpressions.put(tmpExpression.getName(), tmpExpression);
        }

        this.setMinimisation(aModel.isMinimisation());
    }

    public Expression addExpression(final String aName) {

        final Expression retVal = new Expression(aName, this);

        myExpressions.put(aName, retVal);

        return retVal;
    }

    /**
     * If both quadraticParams and linearParams are null you may just as well call
     * {@linkplain #addExpression(String)}.
     * 
     * @param aName Expression name
     * @param quadraticParams null allowed
     * @param linearParams null allowed
     * @return A new Expression with the params set
     */
    public Expression addExpression(final String aName, final Access2D<?> quadraticParams, final Access1D<?> linearParams) {

        final Expression retVal = this.addExpression(aName);

        if (quadraticParams != null) {
            for (int j = 0; j < quadraticParams.getColDim(); j++) {
                for (int i = 0; i < quadraticParams.getRowDim(); i++) {
                    retVal.setQuadraticFactor(i, j, TypeUtils.toBigDecimal(quadraticParams.get(i, j)));
                }
            }
        }

        if (linearParams != null) {
            for (int i = 0; i < linearParams.size(); i++) {
                retVal.setLinearFactor(i, TypeUtils.toBigDecimal(linearParams.get(i)));
            }
        }

        return retVal;
    }

    public void addVariable(final Variable aVariable) {
        myVariables.add(aVariable);
        if (myIndexLookup.put(aVariable.getName(), new Expression.Index(myVariables.size() - 1)) != null) {
            throw new IllegalArgumentException("Variable name not unique!");
        }
    }

    public void addVariables(final Collection<? extends Variable> someVariables) {
        for (final Variable tmpVariable : someVariables) {
            this.addVariable(tmpVariable);
        }
    }

    public ExpressionsBasedModel copy() {
        return new ExpressionsBasedModel(this);
    }

    public int countExpressions() {
        return myExpressions.size();
    }

    public int countVariables() {
        return myVariables.size();
    }

    public GenericSolver getDefaultSolver() {

        this.flushCaches();

        try {
            this.validate();
        } catch (final ValidationException anException) {
            throw new IllegalArgumentException(anException);
        }

        if (this.isAnyVariableInteger()) {

            return IntegerSolver.make(this);

        } else {

            final Expression tmpObjFunc = this.getObjectiveExpression();

            if (tmpObjFunc.isAnyQuadraticFactorNonZero()) {

                return QuadraticSolver.make(this);

            } else {

                return LinearSolver.make(this);
            }
        }
    }

    public Expression getExpression(final String aName) {
        return myExpressions.get(aName);
    }

    public Expression[] getExpressions() {
        return myExpressions.values().toArray(new Expression[myExpressions.size()]);
    }

    public boolean[] getIntegers() {

        final int tmpLength = myVariables.size();

        final boolean[] retVal = new boolean[tmpLength];

        for (int i = 0; i < tmpLength; i++) {
            retVal[i] = myVariables.get(i).isInteger();
        }

        return retVal;
    }

    public List<Variable> getNegativeVariables() {

        if (myNegativeVariables == null) {

            myNegativeVariables = new ArrayList<Variable>();

            for (final Variable tmpVariable : myVariables) {
                if (!tmpVariable.isLowerLimitSet() || (tmpVariable.getLowerLimit().signum() == -1)) {
                    myNegativeVariables.add(tmpVariable);
                }
            }
        }

        return myNegativeVariables;
    }

    public Expression getObjectiveExpression() {

        if (myObjectiveExpression == null) {

            myObjectiveExpression = new Expression("Obj Expr", this);

            Variable tmpVariable;
            for (int i = 0; i < myVariables.size(); i++) {
                tmpVariable = myVariables.get(i);

                if (tmpVariable.isObjective()) {
                    myObjectiveExpression.setLinearFactor(i, tmpVariable.getContributionWeight());
                }
            }

            BigDecimal tmpOldVal = null;
            BigDecimal tmpDiff = null;
            BigDecimal tmpNewVal = null;

            for (final Expression tmpExpression : myExpressions.values()) {

                if (tmpExpression.isObjective()) {

                    final BigDecimal tmpContributionWeight = tmpExpression.getContributionWeight();
                    final boolean tmpNotOne = tmpContributionWeight.compareTo(ONE) != 0; // To avoid multiplication by 1.0

                    if (tmpExpression.isAnyLinearFactorNonZero()) {
                        for (final Expression.Index tmpKey : tmpExpression.getLinearFactorKeys()) {
                            tmpOldVal = myObjectiveExpression.getLinearFactor(tmpKey);
                            tmpDiff = tmpExpression.getLinearFactor(tmpKey);
                            tmpNewVal = tmpOldVal.add(tmpContributionWeight.multiply(tmpDiff));
                            myObjectiveExpression.setLinearFactor(tmpKey, tmpNewVal);
                        }
                    }

                    if (tmpExpression.isAnyQuadraticFactorNonZero()) {
                        for (final Expression.RowColumn tmpKey : tmpExpression.getQuadraticFactorKeys()) {
                            tmpOldVal = myObjectiveExpression.getQuadraticFactor(tmpKey);
                            tmpDiff = tmpExpression.getQuadraticFactor(tmpKey);
                            tmpNewVal = tmpOldVal.add(tmpContributionWeight.multiply(tmpDiff));
                            myObjectiveExpression.setQuadraticFactor(tmpKey, tmpNewVal);
                        }
                    }
                }
            }
        }

        return myObjectiveExpression;
    }

    public List<Variable> getPositiveVariables() {

        if (myPositiveVariables == null) {

            myPositiveVariables = new ArrayList<Variable>();

            for (final Variable tmpVariable : myVariables) {
                if ((!tmpVariable.isUpperLimitSet() || (tmpVariable.getUpperLimit().signum() == 1))
                        || (tmpVariable.isEqualityConstraint() && (tmpVariable.getUpperLimit().signum() == 0))) {
                    myPositiveVariables.add(tmpVariable);
                }
            }
        }

        return myPositiveVariables;
    }

    public BigDecimal getValue() {
        return TypeUtils.toBigDecimal(this.getObjectiveExpression().toFunction().invoke(this.getCurrent()));
    }

    public Variable getVariable(final int anIndex) {
        return myVariables.get(anIndex);
    }

    public Variable[] getVariables() {
        return myVariables.toArray(new Variable[myVariables.size()]);
    }

    public Access1D<BigDecimal> getVariableValues() {

        final int tmpSize = myVariables.size();

        final Array1D<BigDecimal> retVal = Array1D.BIG.makeZero(tmpSize);

        for (int i = 0; i < tmpSize; i++) {
            retVal.set(i, myVariables.get(i).getValue());
        }

        return retVal;
    }

    public Expression.Index indexOf(final Variable aVariable) {
        return myIndexLookup.get(aVariable.getName());
    }

    public int indexOfNegativeVariable(final int index) {
        return this.getNegativeVariables().indexOf(myVariables.get(index));
    }

    public int indexOfPositiveVariable(final int index) {
        return this.getPositiveVariables().indexOf(myVariables.get(index));
    }

    /**
     * @deprecated v32 Use {@link #indexOf(Variable)} instead
     */
    @Deprecated
    public int indexOfVariable(final String aName) {
        return myIndexLookup.get(aName).index;
    }

    public boolean isAnyExpressionQuadratic() {

        boolean retVal = false;

        // final int tmpLength = myExpressions.size();

        //        for (int i = 0; !retVal && (i < tmpLength); i++) {
        //            retVal |= myExpressions.get(i).hasQuadratic();
        //        }

        String tmpType;
        for (final Iterator<String> tmpIterator = myExpressions.keySet().iterator(); !retVal && tmpIterator.hasNext();) {
            tmpType = tmpIterator.next();
            retVal |= myExpressions.get(tmpType).isAnyQuadraticFactorNonZero();
        }

        return retVal;
    }

    public boolean isAnyVariableInteger() {

        boolean retVal = false;

        final int tmpLength = myVariables.size();

        for (int i = 0; !retVal && (i < tmpLength); i++) {
            retVal |= myVariables.get(i).isInteger();
        }

        return retVal;
    }

    public Optimisation.Result maximise() {

        this.setMaximisation(true);

        return this.getDefaultSolver().solve();
    }

    public Optimisation.Result minimise() {

        this.setMinimisation(true);

        return this.getDefaultSolver().solve();
    }

    public ExpressionsBasedModel relax() {

        final int tmpLength = myVariables.size();
        for (int i = 0; i < tmpLength; i++) {
            myVariables.get(i).integer(false);
        }

        return this;
    }

    public Expression[] selectExpressionsGeneralEquality() {

        final List<Expression> tmpList = new ArrayList<Expression>();

        for (final Expression tmpExpression : myExpressions.values()) {
            if (tmpExpression.isEqualityConstraint()) {
                tmpList.add(tmpExpression);
            }
        }

        return tmpList.toArray(new Expression[tmpList.size()]);
    }

    public Expression[] selectExpressionsGeneralLower() {

        final List<Expression> tmpList = new ArrayList<Expression>();

        for (final Expression tmpExpression : myExpressions.values()) {
            if (tmpExpression.isLowerConstraint()) {
                tmpList.add(tmpExpression);
            }
        }

        return tmpList.toArray(new Expression[tmpList.size()]);
    }

    public Expression[] selectExpressionsGeneralUpper() {

        final List<Expression> tmpList = new ArrayList<Expression>();

        for (final Expression tmpExpression : myExpressions.values()) {
            if (tmpExpression.isUpperConstraint()) {
                tmpList.add(tmpExpression);
            }
        }

        return tmpList.toArray(new Expression[tmpList.size()]);
    }

    public Expression[] selectExpressionsNegativeEquality() {

        final List<Expression> tmpList = new ArrayList<Expression>();

        for (final Expression tmpExpression : myExpressions.values()) {
            if (tmpExpression.isEqualityConstraint() && (tmpExpression.getUpperLimit().signum() == -1)) {
                tmpList.add(tmpExpression);
            }
        }

        return tmpList.toArray(new Expression[tmpList.size()]);
    }

    public Expression[] selectExpressionsNegativeLower() {

        final List<Expression> tmpList = new ArrayList<Expression>();

        for (final Expression tmpExpression : myExpressions.values()) {
            if (tmpExpression.isLowerConstraint() && (tmpExpression.getLowerLimit().signum() != 1)) {
                tmpList.add(tmpExpression);
            }
        }

        return tmpList.toArray(new Expression[tmpList.size()]);
    }

    public Expression[] selectExpressionsNegativeUpper() {

        final List<Expression> tmpList = new ArrayList<Expression>();

        for (final Expression tmpExpression : myExpressions.values()) {
            if (tmpExpression.isUpperConstraint() && (tmpExpression.getUpperLimit().signum() == -1)) {
                tmpList.add(tmpExpression);
            }
        }

        return tmpList.toArray(new Expression[tmpList.size()]);
    }

    public Expression[] selectExpressionsPositiveEquality() {

        final List<Expression> tmpList = new ArrayList<Expression>();

        for (final Expression tmpExpression : myExpressions.values()) {
            if (tmpExpression.isEqualityConstraint() && (tmpExpression.getUpperLimit().signum() != -1)) {
                tmpList.add(tmpExpression);
            }
        }

        return tmpList.toArray(new Expression[tmpList.size()]);
    }

    public Expression[] selectExpressionsPositiveLower() {

        final List<Expression> tmpList = new ArrayList<Expression>();

        for (final Expression tmpExpression : myExpressions.values()) {
            if (tmpExpression.isLowerConstraint() && (tmpExpression.getLowerLimit().signum() == 1)) {
                tmpList.add(tmpExpression);
            }
        }

        return tmpList.toArray(new Expression[tmpList.size()]);
    }

    public Expression[] selectExpressionsPositiveUpper() {

        final List<Expression> tmpList = new ArrayList<Expression>();

        for (final Expression tmpExpression : myExpressions.values()) {
            if (tmpExpression.isUpperConstraint() && (tmpExpression.getUpperLimit().signum() != -1)) {
                tmpList.add(tmpExpression);
            }
        }

        return tmpList.toArray(new Expression[tmpList.size()]);
    }

    public Variable[] selectVariablesGeneralEquality() {

        final List<Variable> tmpList = new ArrayList<Variable>();

        for (final Variable tmpVariable : myVariables) {
            if (tmpVariable.isEqualityConstraint()) {
                tmpList.add(tmpVariable);
            }
        }

        return tmpList.toArray(new Variable[tmpList.size()]);
    }

    public Variable[] selectVariablesGeneralLower() {

        final List<Variable> tmpList = new ArrayList<Variable>();

        for (final Variable tmpVariable : myVariables) {
            if (tmpVariable.isLowerConstraint()) {
                tmpList.add(tmpVariable);
            }
        }

        return tmpList.toArray(new Variable[tmpList.size()]);
    }

    public Variable[] selectVariablesGeneralUpper() {

        final List<Variable> tmpList = new ArrayList<Variable>();

        for (final Variable tmpVariable : myVariables) {
            if (tmpVariable.isUpperConstraint()) {
                tmpList.add(tmpVariable);
            }
        }

        return tmpList.toArray(new Variable[tmpList.size()]);
    }

    public Variable[] selectVariablesNegativeEquality() {

        final List<Variable> tmpList = new ArrayList<Variable>();

        for (final Variable tmpVariable : myVariables) {
            if (tmpVariable.isEqualityConstraint() && (tmpVariable.getLowerLimit().signum() == -1)) {
                tmpList.add(tmpVariable);
            }
        }

        return tmpList.toArray(new Variable[tmpList.size()]);
    }

    public Variable[] selectVariablesNegativeLower() {

        final List<Variable> tmpList = new ArrayList<Variable>();

        for (final Variable tmpVariable : myVariables) {
            if (tmpVariable.isLowerConstraint() && (tmpVariable.getLowerLimit().signum() == -1)) {
                tmpList.add(tmpVariable);
            }
        }

        return tmpList.toArray(new Variable[tmpList.size()]);
    }

    public Variable[] selectVariablesNegativeUpper() {

        final List<Variable> tmpList = new ArrayList<Variable>();

        for (final Variable tmpVariable : myVariables) {
            if (tmpVariable.isUpperConstraint() && (tmpVariable.getUpperLimit().signum() == -1)) {
                tmpList.add(tmpVariable);
            }
        }

        return tmpList.toArray(new Variable[tmpList.size()]);
    }

    public Variable[] selectVariablesPositiveEquality() {

        final List<Variable> tmpList = new ArrayList<Variable>();

        for (final Variable tmpVariable : myVariables) {
            if (tmpVariable.isEqualityConstraint() && (tmpVariable.getUpperLimit().signum() != -1)) {
                tmpList.add(tmpVariable);
            }
        }

        return tmpList.toArray(new Variable[tmpList.size()]);
    }

    public Variable[] selectVariablesPositiveLower() {

        final List<Variable> tmpList = new ArrayList<Variable>();

        for (final Variable tmpVariable : myVariables) {
            if (tmpVariable.isLowerConstraint() && (tmpVariable.getLowerLimit().signum() == 1)) {
                tmpList.add(tmpVariable);
            }
        }

        return tmpList.toArray(new Variable[tmpList.size()]);
    }

    public Variable[] selectVariablesPositiveUpper() {

        final List<Variable> tmpList = new ArrayList<Variable>();

        for (final Variable tmpVariable : myVariables) {
            if (tmpVariable.isUpperConstraint() && (tmpVariable.getUpperLimit().signum() == 1)) {
                tmpList.add(tmpVariable);
            }
        }

        return tmpList.toArray(new Variable[tmpList.size()]);
    }

    /**
     * @deprecated v33 Use {@linkplain #getVariable(int)} instead
     */
    @Deprecated
    public void setLowerLimitOnVariable(final int aVariableIndex, final BigDecimal aLimit) {
        this.getVariable(aVariableIndex).lower(aLimit);
    }

    /**
     * @deprecated v33 Use {@linkplain #getVariable(int)} instead
     */
    @Deprecated
    public void setUpperLimitOnVariable(final int aVariableIndex, final BigDecimal aLimit) {
        this.getVariable(aVariableIndex).upper(aLimit);
    }

    @Override
    public String toString() {

        final StringBuilder retVal = new StringBuilder(START_END);

        for (final Variable tmpVariable : myVariables) {
            tmpVariable.appendToString(retVal);
            retVal.append(NEW_LINE);
        }

        for (final Expression tmpExpression : myExpressions.values()) {
            tmpExpression.appendToString(retVal, this.getCurrent());
            retVal.append(NEW_LINE);
        }

        return retVal.append(START_END).toString();
    }

    public boolean validate() throws ValidationException {

        boolean retVal = true;

        for (final Variable tmpVariable : myVariables) {
            retVal &= tmpVariable.validateConfiguration();
        }

        for (final Expression tmpExpression : myExpressions.values()) {
            retVal &= tmpExpression.validateConfiguration();
        }

        for (final Expression tmpExpression : this.getExpressions()) {
            if (tmpExpression.isConstraint() && tmpExpression.isAnyQuadraticFactorNonZero()) {
                throw new ValidationException("Cannot handle quadratic constraints!");
            }
        }

        return retVal;
    }

    public boolean validateSolution(final Access1D<BigDecimal> aSolution, final NumberContext aContext) {

        final int tmpSize = myVariables.size();

        boolean retVal = tmpSize == aSolution.size();

        for (int i = 0; retVal && (i < tmpSize); i++) {
            retVal &= myVariables.get(i).validateValue(aSolution.get(i), aContext);
        }

        for (final Expression tmpExpression : myExpressions.values()) {
            retVal &= retVal && tmpExpression.validateSolution(aSolution, aContext);
        }

        return retVal;
    }

    public boolean validateSolution(final NumberContext aContext) {

        final int tmpSize = myVariables.size();

        boolean retVal = true;

        for (int i = 0; retVal && (i < tmpSize); i++) {
            retVal &= myVariables.get(i).validateState(aContext);
        }

        final Access1D<BigDecimal> tmpSolution = this.getVariableValues();

        for (final Expression tmpExpression : myExpressions.values()) {
            retVal &= retVal && tmpExpression.validateSolution(tmpSolution, aContext);
        }

        return retVal;
    }

    protected void flushCaches() {
        myObjectiveExpression = null;
        myPositiveVariables = null;
        myNegativeVariables = null;
    }

    protected List<BigDecimal> getCurrent() {

        final List<BigDecimal> retVal = new ArrayList<BigDecimal>();

        BigDecimal tmpVal;
        for (int i = 0; i < myVariables.size(); i++) {

            tmpVal = myVariables.get(i).getValue();
            if (tmpVal != null) {
                retVal.add(i, tmpVal);
            } else {
                retVal.add(i, ZERO);
            }
        }

        return retVal;
    }

}
