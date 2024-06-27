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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access1D;
import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.ConfigurableBinaryFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorCollection;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.BigAggregator;
import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.function.multiary.CompoundFunction;
import org.ojalgo.function.multiary.LinearFunction;
import org.ojalgo.function.multiary.MultiaryFunction;
import org.ojalgo.function.multiary.QuadraticFunction;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.store.ZeroStore;
import org.ojalgo.type.context.NumberContext;

/**
 * <p>
 * Think of an Expression as one constraint or a component to the objective
 * function.
 * An expression becomes a linear expression as soon as you set a linear factor.
 * Setting a quadratic factor turns it into a quadratic expression. If you set
 * both linear and quadratic factors it is a compound expression, and if you set
 * neither it is an empty expression. Currently the solvers supplied by ojAlgo
 * can only handle linear constraint expressions. The objective function can be
 * linear, quadratic or compound. Empty expressions makes no sense...
 * </p>
 * <p>
 * An expression is turned into a constraint by setting a lower and/or upper
 * limit. Use {@linkplain ModelEntity#lower(BigDecimal)},
 * {@linkplain ModelEntity#upper(BigDecimal)} or
 * {@linkplain ModelEntity#level(BigDecimal)}. An expression is made part of
 * (contributing to) the objective function by setting a contribution weight.
 * Use {@linkplain ModelEntity#weight(BigDecimal)}. The contribution weight can
 * be set to anything except zero (0.0). Often you may just want to set it to
 * one (1.0). Other values can be used to balance multiple expressions
 * constributing to the objective function.
 * </p>
 * 
 * @author apete
 */
public final class Expression extends ModelEntity<Expression> {

    public static final class Index implements Comparable<Index> {

        public final int index;

        public Index(final int anIndex) {

            super();

            index = anIndex;
        }

        public int compareTo(final Index ref) {
            return index - ref.index;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Index)) {
                return false;
            }
            final Index other = (Index) obj;
            if (index != other.index) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + index;
            return result;
        }

    }

    public static final class RowColumn implements Comparable<RowColumn> {

        public final int row;
        public final int column;

        public RowColumn(final int aRow, final int aCol) {

            super();

            row = aRow;
            column = aCol;
        }

        public int compareTo(final RowColumn ref) {

            if (column == ref.column) {

                return row - ref.row;

            } else {

                return column - ref.column;
            }
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final RowColumn other = (RowColumn) obj;
            if (column != other.column) {
                return false;
            }
            if (row != other.row) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + column;
            result = (prime * result) + row;
            return result;
        }

    }

    private transient int myAdjustmentExponent = Integer.MIN_VALUE;
    private final HashMap<Index, BigDecimal> myLinear = new HashMap<Index, BigDecimal>();
    private final ExpressionsBasedModel myModel;

    private final HashMap<RowColumn, BigDecimal> myQuadratic = new HashMap<RowColumn, BigDecimal>();

    @SuppressWarnings("unused")
    private Expression(final String aName) {

        this(aName, null);

        ProgrammingError.throwForIllegalInvocation();
    }

    Expression(final String aName, final ExpressionsBasedModel aModel) {

        super(aName);

        myModel = aModel;

        ProgrammingError.throwIfNull(myModel);
    }

    public BigDecimal evaluate(final Access1D<BigDecimal> point) {

        BigDecimal retVal = BigMath.ZERO;

        BigDecimal tmpFactor;

        for (final RowColumn tmpKey : this.getQuadraticFactorKeys()) {
            tmpFactor = this.getQuadraticFactor(tmpKey);
            retVal = retVal.add(point.get(tmpKey.row).multiply(tmpFactor).multiply(point.get(tmpKey.column)));
        }

        for (final Index tmpKey : this.getLinearFactorKeys()) {
            tmpFactor = this.getLinearFactor(tmpKey);
            retVal = retVal.add(point.get(tmpKey.index).multiply(tmpFactor));
        }

        return retVal;
    }

    public double evaluateLessThanZero(final Access1D<?> point) {

        final double tmpBody = this.evaluateBody(point);

        if (this.isUpperLimitSet()) {
            return tmpBody - this.getAdjustedUpperLimit();
        } else if (this.isLowerLimitSet()) {
            return this.getAdjustedLowerLimit() - tmpBody;
        } else {
            return tmpBody;
        }
    }

    public double evaluateMoreThanZero(final Access1D<?> point) {

        final double tmpBody = this.evaluateBody(point);

        if (this.isLowerLimitSet()) {
            return tmpBody - this.getAdjustedLowerLimit();
        } else if (this.isUpperLimitSet()) {
            return this.getAdjustedUpperLimit() - tmpBody;
        } else {
            return tmpBody;
        }
    }

    public MatrixStore<Double> getAdjustedGradient(final Access1D<?> point) {

        final PrimitiveDenseStore retVal = PrimitiveDenseStore.FACTORY.makeZero(myModel.countVariables(), 1);

        final ConfigurableBinaryFunction<Double> tmpBaseFunc = PrimitiveFunction.ADD;
        double tmpAdjustedFactor;
        UnaryFunction<Double> tmpModFunc;
        for (final RowColumn tmpKey : this.getQuadraticFactorKeys()) {
            tmpAdjustedFactor = this.getAdjustedQuadraticFactor(tmpKey);
            tmpModFunc = tmpBaseFunc.second(tmpAdjustedFactor * point.doubleValue(tmpKey.column));
            retVal.modifyOne(tmpKey.row, 0, tmpModFunc);
            tmpModFunc = tmpBaseFunc.second(tmpAdjustedFactor * point.doubleValue(tmpKey.row));
            retVal.modifyOne(tmpKey.column, 0, tmpModFunc);
        }

        for (final Index tmpKey : this.getLinearFactorKeys()) {
            tmpAdjustedFactor = this.getAdjustedLinearFactor(tmpKey);
            tmpModFunc = tmpBaseFunc.second(tmpAdjustedFactor);
            retVal.modifyOne(tmpKey.index, 0, tmpModFunc);
        }

        return retVal;
    }

    public MatrixStore<Double> getAdjustedHessian() {

        final int tmpCountVariables = myModel.countVariables();
        final PrimitiveDenseStore retVal = PrimitiveDenseStore.FACTORY.makeZero(tmpCountVariables, tmpCountVariables);

        final ConfigurableBinaryFunction<Double> tmpBaseFunc = PrimitiveFunction.ADD;
        UnaryFunction<Double> tmpModFunc;
        for (final RowColumn tmpKey : this.getQuadraticFactorKeys()) {
            tmpModFunc = tmpBaseFunc.second(this.getAdjustedQuadraticFactor(tmpKey));
            retVal.modifyOne(tmpKey.row, tmpKey.column, tmpModFunc);
            retVal.modifyOne(tmpKey.column, tmpKey.row, tmpModFunc);
        }

        return retVal;
    }

    public double getAdjustedLinearFactor(final Index aKey) {
        return this.getLinearFactor(aKey, true).doubleValue();
    }

    public double getAdjustedLinearFactor(final int aVar) {
        return this.getAdjustedLinearFactor(new Index(aVar));
    }

    public double getAdjustedLinearFactor(final Variable aVar) {
        return this.getAdjustedLinearFactor(myModel.indexOf(aVar));
    }

    public double getAdjustedQuadraticFactor(final int aVar1, final int aVar2) {
        return this.getAdjustedQuadraticFactor(new RowColumn(aVar1, aVar2));
    }

    public double getAdjustedQuadraticFactor(final RowColumn aKey) {
        return this.getQuadraticFactor(aKey, true).doubleValue();
    }

    public double getAdjustedQuadraticFactor(final Variable aVar1, final Variable aVar2) {
        return this.getAdjustedQuadraticFactor(myModel.indexOf(aVar1).index, myModel.indexOf(aVar2).index);
    }

    public BigDecimal getLinearFactor(final Index aKey) {
        return this.getLinearFactor(aKey, false);
    }

    public BigDecimal getLinearFactor(final int aVar) {
        return this.getLinearFactor(new Index(aVar));
    }

    public BigDecimal getLinearFactor(final Variable aVar) {
        return this.getLinearFactor(myModel.indexOf(aVar));
    }

    public Set<Expression.Index> getLinearFactorKeys() {
        return myLinear.keySet();
    }

    public BigDecimal getQuadraticFactor(final int aVar1, final int aVar2) {
        return this.getQuadraticFactor(new RowColumn(aVar1, aVar2));
    }

    public BigDecimal getQuadraticFactor(final RowColumn aKey) {
        return this.getQuadraticFactor(aKey, false);
    }

    public BigDecimal getQuadraticFactor(final Variable aRowVar, final Variable aColVar) {
        return this.getQuadraticFactor(myModel.indexOf(aRowVar).index, myModel.indexOf(aColVar).index);
    }

    public Set<Expression.RowColumn> getQuadraticFactorKeys() {
        return myQuadratic.keySet();
    }

    public boolean isAnyLinearFactorNonZero() {
        return myLinear.size() > 0;
    }

    public boolean isAnyQuadraticFactorNonZero() {
        return myQuadratic.size() > 0;
    }

    public boolean isFunctionCompound() {
        return this.isAnyQuadraticFactorNonZero() && this.isAnyLinearFactorNonZero();
    }

    public boolean isFunctionEmpty() {
        return !this.isAnyQuadraticFactorNonZero() && !this.isAnyLinearFactorNonZero();
    }

    public boolean isFunctionLinear() {
        return !this.isAnyQuadraticFactorNonZero() && this.isAnyLinearFactorNonZero();
    }

    public boolean isFunctionQuadratic() {
        return this.isAnyQuadraticFactorNonZero() && !this.isAnyLinearFactorNonZero();
    }

    public void setLinearFactor(final Index aKey, final BigDecimal aValue) {
        if (aValue.signum() != 0) {
            myLinear.put(aKey, aValue);
        } else {
            myLinear.remove(aKey);
        }
        myAdjustmentExponent = Integer.MIN_VALUE;
    }

    public void setLinearFactor(final int aVar, final BigDecimal aValue) {
        this.setLinearFactor(new Index(aVar), aValue);
    }

    public void setLinearFactor(final Variable aVar, final BigDecimal aValue) {
        this.setLinearFactor(myModel.indexOf(aVar), aValue);
    }

    public void setQuadraticFactor(final int aVar1, final int aVar2, final BigDecimal aValue) {
        this.setQuadraticFactor(new RowColumn(aVar1, aVar2), aValue);
    }

    public void setQuadraticFactor(final RowColumn aKey, final BigDecimal aValue) {
        if (aValue.signum() != 0) {
            myQuadratic.put(aKey, aValue);
        } else {
            myQuadratic.remove(aKey);
        }
        myAdjustmentExponent = Integer.MIN_VALUE;
    }

    public void setQuadraticFactor(final Variable aVar1, final Variable aVar2, final BigDecimal aValue) {
        this.setQuadraticFactor(myModel.indexOf(aVar1).index, myModel.indexOf(aVar2).index, aValue);
    }

    public MultiaryFunction<Double> toFunction() {

        if (this.isFunctionCompound()) {
            return this.getCompoundFunction();
        } else if (this.isFunctionQuadratic()) {
            return this.getQuadraticFunction();
        } else if (this.isFunctionLinear()) {
            return this.getLinearFunction();
        } else {
            return new MultiaryFunction<Double>() {

                public int dim() {
                    return myModel.countVariables();
                }

                public MatrixStore<Double> getGradient(final Access1D<?> anArg) {
                    return ZeroStore.makePrimitive(myModel.countVariables(), 1);
                }

                public MatrixStore<Double> getGradient(final double[] anArg) {
                    return ZeroStore.makePrimitive(myModel.countVariables(), 1);
                }

                public MatrixStore<Double> getGradient(final List<? extends Number> anArg) {
                    return ZeroStore.makePrimitive(myModel.countVariables(), 1);
                }

                public MatrixStore<Double> getGradient(final MatrixStore<Double> anArg) {
                    return ZeroStore.makePrimitive(myModel.countVariables(), 1);
                }

                public MatrixStore<Double> getGradient(final Number[] anArg) {
                    return ZeroStore.makePrimitive(myModel.countVariables(), 1);
                }

                public MatrixStore<Double> getHessian(final Access1D<?> anArg) {
                    final int tmpCountVariables = myModel.countVariables();
                    return ZeroStore.makePrimitive(tmpCountVariables, tmpCountVariables);
                }

                public MatrixStore<Double> getHessian(final double[] anArg) {
                    final int tmpCountVariables = myModel.countVariables();
                    return ZeroStore.makePrimitive(tmpCountVariables, tmpCountVariables);
                }

                public MatrixStore<Double> getHessian(final List<? extends Number> anArg) {
                    final int tmpCountVariables = myModel.countVariables();
                    return ZeroStore.makePrimitive(tmpCountVariables, tmpCountVariables);
                }

                public MatrixStore<Double> getHessian(final MatrixStore<Double> anArg) {
                    final int tmpCountVariables = myModel.countVariables();
                    return ZeroStore.makePrimitive(tmpCountVariables, tmpCountVariables);
                }

                public MatrixStore<Double> getHessian(final Number[] anArg) {
                    final int tmpCountVariables = myModel.countVariables();
                    return ZeroStore.makePrimitive(tmpCountVariables, tmpCountVariables);
                }

                public Double invoke(final Access1D<?> anArg) {
                    return PrimitiveMath.ZERO;
                }

                public Double invoke(final double[] anArg) {
                    return PrimitiveMath.ZERO;
                }

                public Double invoke(final List<? extends Number> anArg) {
                    return PrimitiveMath.ZERO;
                }

                public Double invoke(final MatrixStore<Double> anArg) {
                    return PrimitiveMath.ZERO;
                }

                public Double invoke(final Number[] anArg) {
                    return PrimitiveMath.ZERO;
                }

            };
        }
    }

    public boolean validateSolution(final Access1D<BigDecimal> aSolution, final NumberContext aContext) {

        final BigDecimal tmpValue = this.evaluate(aSolution);

        return this.validateValue(tmpValue, aContext);
    }

    private final BigDecimal convert(final BigDecimal value, final boolean adjusted) {

        if (value != null) {

            if (adjusted) {

                final int tmpAdjExp = this.getAdjustmentExponent();

                if (tmpAdjExp != 0) {

                    return value.movePointRight(tmpAdjExp);

                } else {

                    return value;
                }

            } else {

                return value;
            }

        } else {

            return BigMath.ZERO;
        }
    }

    private double evaluateBody(final Access1D<?> point) {

        double retVal = PrimitiveMath.ZERO;

        double tmpAdjustedFactor;

        for (final RowColumn tmpKey : this.getQuadraticFactorKeys()) {
            tmpAdjustedFactor = this.getAdjustedQuadraticFactor(tmpKey);
            retVal += point.doubleValue(tmpKey.row) * tmpAdjustedFactor * point.doubleValue(tmpKey.column);
        }

        for (final Index tmpKey : this.getLinearFactorKeys()) {
            tmpAdjustedFactor = this.getAdjustedLinearFactor(tmpKey);
            retVal += point.doubleValue(tmpKey.index) * tmpAdjustedFactor;
        }

        return retVal;
    }

    protected void appendMiddlePart(final StringBuilder aStringBuilder, final List<BigDecimal> aCurrentState) {

        aStringBuilder.append(this.getName());
        aStringBuilder.append(": ");
        aStringBuilder.append(OptimisationUtils.DISPLAY.enforce(this.toFunction().invoke(aCurrentState)));

        if (this.isObjective()) {
            aStringBuilder.append(" (");
            aStringBuilder.append(OptimisationUtils.DISPLAY.enforce(this.getContributionWeight()));
            aStringBuilder.append(")");
        }
    }

    @Override
    protected int getAdjustmentExponent() {

        if (myAdjustmentExponent == Integer.MIN_VALUE) {

            final AggregatorCollection<BigDecimal> tmpCollection = BigAggregator.getCollection();
            final AggregatorFunction<BigDecimal> tmpLargestAggr = tmpCollection.largest();
            final AggregatorFunction<BigDecimal> tmpSmallestAggr = tmpCollection.smallest();

            for (final BigDecimal tmpLinearFactor : myLinear.values()) {
                tmpLargestAggr.invoke(tmpLinearFactor);
                tmpSmallestAggr.invoke(tmpLinearFactor);
            }

            for (final BigDecimal tmpQuadraticFactor : myQuadratic.values()) {
                tmpLargestAggr.invoke(tmpQuadraticFactor);
                tmpSmallestAggr.invoke(tmpQuadraticFactor);
            }

            final BigDecimal tmpLowerLimit = this.getLowerLimit();
            if (tmpLowerLimit != null) {
                tmpLargestAggr.invoke(tmpLowerLimit);
                tmpSmallestAggr.invoke(tmpLowerLimit);
            }

            final BigDecimal tmpUpperLimit = this.getUpperLimit();
            if (tmpUpperLimit != null) {
                tmpLargestAggr.invoke(tmpUpperLimit);
                tmpSmallestAggr.invoke(tmpUpperLimit);
            }

            myAdjustmentExponent = OptimisationUtils.getAdjustmentFactorExponent(tmpLargestAggr, tmpSmallestAggr);
        }

        return myAdjustmentExponent;
    }

    void appendToString(final StringBuilder aStringBuilder, final List<BigDecimal> aCurrentState) {

        this.appendLeftPart(aStringBuilder);
        if (aCurrentState != null) {
            this.appendMiddlePart(aStringBuilder, aCurrentState);
        } else {
            this.appendMiddlePart(aStringBuilder);
        }
        this.appendRightPart(aStringBuilder);
    }

    final CompoundFunction<Double> getCompoundFunction() {

        final CompoundFunction<Double> retVal = CompoundFunction.makePrimitive(myModel.countVariables());

        if (this.isAnyQuadraticFactorNonZero()) {
            for (final Entry<RowColumn, BigDecimal> tmpEntry : myQuadratic.entrySet()) {
                retVal.setQuadraticFactor(tmpEntry.getKey().row, tmpEntry.getKey().column, tmpEntry.getValue().doubleValue());
            }
        }

        if (this.isAnyLinearFactorNonZero()) {
            for (final Entry<Index, BigDecimal> tmpEntry : myLinear.entrySet()) {
                retVal.setLinearFactor(tmpEntry.getKey().index, tmpEntry.getValue().doubleValue());
            }
        }

        return retVal;
    }

    BigDecimal getLinearFactor(final Index key, final boolean adjusted) {
        return this.convert(myLinear.get(key), adjusted);
    }

    final LinearFunction<Double> getLinearFunction() {

        final LinearFunction<Double> retVal = LinearFunction.makePrimitive(myModel.countVariables());

        if (this.isAnyLinearFactorNonZero()) {
            for (final Entry<Index, BigDecimal> tmpEntry : myLinear.entrySet()) {
                retVal.setFactor(tmpEntry.getKey().index, tmpEntry.getValue().doubleValue());
            }
        }

        return retVal;
    }

    BigDecimal getQuadraticFactor(final RowColumn key, final boolean adjusted) {
        return this.convert(myQuadratic.get(key), adjusted);
    }

    final QuadraticFunction<Double> getQuadraticFunction() {

        final QuadraticFunction<Double> retVal = QuadraticFunction.makePrimitive(myModel.countVariables());

        if (this.isAnyQuadraticFactorNonZero()) {
            for (final Entry<RowColumn, BigDecimal> tmpEntry : myQuadratic.entrySet()) {
                retVal.setFactor(tmpEntry.getKey().row, tmpEntry.getKey().column, tmpEntry.getValue().doubleValue());
            }
        }

        return retVal;
    }
}
