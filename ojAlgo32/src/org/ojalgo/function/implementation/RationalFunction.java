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
package org.ojalgo.function.implementation;

import java.math.BigDecimal;

import org.ojalgo.function.ConfigurableBinaryFunction;
import org.ojalgo.function.ConfigurableParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.scalar.RationalNumber;
import org.ojalgo.type.TypeUtils;

/**
 * RationalFunction
 *
 * @author apete
 */
public final class RationalFunction extends FunctionSet<RationalNumber> {

    public static final UnaryFunction<RationalNumber> ABS = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {
            if (anArg.compareTo(RationalNumber.ZERO) == -1) {
                return anArg.negate();
            } else {
                return anArg;
            }
        }
    };

    public static final UnaryFunction<RationalNumber> ACOS = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {

            final BigDecimal tmpArg = TypeUtils.toBigDecimal(anArg);

            final BigDecimal tmpRet = BigFunction.ACOS.invoke(tmpArg);

            return TypeUtils.toRationalNumber(tmpRet);
        }
    };

    public static final UnaryFunction<RationalNumber> ACOSH = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {

            final RationalNumber tmpNmbr = anArg.multiply(anArg).subtract(RationalNumber.ONE);

            return RationalFunction.LOG.invoke(anArg.add(RationalFunction.SQRT.invoke(tmpNmbr)));
        }
    };

    public static final ConfigurableBinaryFunction<RationalNumber> ADD = new ConfigurableBinaryFunction<RationalNumber>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new RationalNumber(anArg1), new RationalNumber(anArg2)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg1, final RationalNumber anArg2) {
            return anArg1.add(anArg2);
        }
    };

    public static final UnaryFunction<RationalNumber> ASIN = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {

            final BigDecimal tmpArg = TypeUtils.toBigDecimal(anArg);

            final BigDecimal tmpRet = BigFunction.ASIN.invoke(tmpArg);

            return TypeUtils.toRationalNumber(tmpRet);
        }
    };

    public static final UnaryFunction<RationalNumber> ASINH = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {

            final RationalNumber tmpNmbr = anArg.multiply(anArg).add(RationalNumber.ONE);

            return RationalFunction.LOG.invoke(anArg.add(RationalFunction.SQRT.invoke(tmpNmbr)));
        }
    };

    public static final UnaryFunction<RationalNumber> ATAN = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {

            final BigDecimal tmpArg = TypeUtils.toBigDecimal(anArg);

            final BigDecimal tmpRet = BigFunction.ATAN.invoke(tmpArg);

            return TypeUtils.toRationalNumber(tmpRet);
        }
    };

    public static final UnaryFunction<RationalNumber> ATANH = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {

            final RationalNumber tmpNmbr = anArg.add(RationalNumber.ONE).divide(RationalNumber.ONE.subtract(anArg));

            return RationalFunction.LOG.invoke(tmpNmbr).divide(RationalNumber.TWO);
        }
    };

    public static final UnaryFunction<RationalNumber> CARDINALITY = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {
            return TypeUtils.isZero(anArg.getModulus()) ? RationalNumber.ZERO : RationalNumber.ONE;
        }
    };

    public static final UnaryFunction<RationalNumber> CONJUGATE = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {
            return anArg.conjugate();
        }
    };

    public static final UnaryFunction<RationalNumber> COS = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {

            final BigDecimal tmpArg = TypeUtils.toBigDecimal(anArg);

            final BigDecimal tmpRet = BigFunction.COS.invoke(tmpArg);

            return TypeUtils.toRationalNumber(tmpRet);
        }
    };

    public static final UnaryFunction<RationalNumber> COSH = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {
            return (EXP.invoke(anArg).add(EXP.invoke(anArg.negate()))).divide(RationalNumber.TWO);
        }
    };

    public static final ConfigurableBinaryFunction<RationalNumber> DIVIDE = new ConfigurableBinaryFunction<RationalNumber>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new RationalNumber(anArg1), new RationalNumber(anArg2)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg1, final RationalNumber anArg2) {
            return anArg1.divide(anArg2);
        }
    };

    public static final UnaryFunction<RationalNumber> EXP = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {

            final BigDecimal tmpArg = TypeUtils.toBigDecimal(anArg);

            final BigDecimal tmpRet = BigFunction.EXP.invoke(tmpArg);

            return TypeUtils.toRationalNumber(tmpRet);
        }
    };

    public static final ConfigurableBinaryFunction<RationalNumber> HYPOT = new ConfigurableBinaryFunction<RationalNumber>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new RationalNumber(anArg1), new RationalNumber(anArg2)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg1, final RationalNumber anArg2) {
            return new RationalNumber(Math.hypot(anArg1.getModulus(), anArg2.getModulus()));
        }
    };

    public static final UnaryFunction<RationalNumber> INVERT = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {
            return anArg.invert();
        }
    };

    public static final UnaryFunction<RationalNumber> LOG = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {

            final BigDecimal tmpArg = TypeUtils.toBigDecimal(anArg);

            final BigDecimal tmpRet = BigFunction.LOG.invoke(tmpArg);

            return TypeUtils.toRationalNumber(tmpRet);
        }
    };

    public static final ConfigurableBinaryFunction<RationalNumber> MAX = new ConfigurableBinaryFunction<RationalNumber>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new RationalNumber(anArg1), new RationalNumber(anArg2)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg1, final RationalNumber anArg2) {

            RationalNumber retVal = null;

            if (anArg1.getModulus() >= anArg2.getModulus()) {
                retVal = anArg1;
            } else {
                retVal = anArg2;
            }

            return retVal;
        }
    };

    public static final ConfigurableBinaryFunction<RationalNumber> MIN = new ConfigurableBinaryFunction<RationalNumber>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new RationalNumber(anArg1), new RationalNumber(anArg2)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg1, final RationalNumber anArg2) {

            RationalNumber retVal = null;

            if (anArg1.getModulus() <= anArg2.getModulus()) {
                retVal = anArg1;
            } else {
                retVal = anArg2;
            }

            return retVal;
        }
    };

    public static final ConfigurableBinaryFunction<RationalNumber> MULTIPLY = new ConfigurableBinaryFunction<RationalNumber>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new RationalNumber(anArg1), new RationalNumber(anArg2)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg1, final RationalNumber anArg2) {
            return anArg1.multiply(anArg2);
        }
    };

    public static final UnaryFunction<RationalNumber> NEGATE = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {
            return anArg.negate();
        }
    };

    public static final ConfigurableBinaryFunction<RationalNumber> POW = new ConfigurableBinaryFunction<RationalNumber>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new RationalNumber(anArg1), new RationalNumber(anArg2)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg1, final RationalNumber anArg2) {
            return RationalFunction.EXP.invoke(anArg2.multiply(RationalFunction.LOG.invoke(anArg1)));
        }
    };

    public static final ConfigurableParameterFunction<RationalNumber> POWER = new ConfigurableParameterFunction<RationalNumber>() {

        public final double invoke(final double anArg, final int aParam) {
            return this.invoke(new RationalNumber(anArg), aParam).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg, final int aParam) {

            final BigDecimal tmpArg = TypeUtils.toBigDecimal(anArg);

            final BigDecimal tmpRet = BigFunction.POWER.invoke(tmpArg, aParam);

            return TypeUtils.toRationalNumber(tmpRet);
        }
    };

    public static final ConfigurableParameterFunction<RationalNumber> ROOT = new ConfigurableParameterFunction<RationalNumber>() {

        public final double invoke(final double anArg, final int aParam) {
            return this.invoke(new RationalNumber(anArg), aParam).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg, final int aParam) {

            final BigDecimal tmpArg = TypeUtils.toBigDecimal(anArg);

            final BigDecimal tmpRet = BigFunction.ROOT.invoke(tmpArg, aParam);

            return TypeUtils.toRationalNumber(tmpRet);
        }
    };

    public static final ConfigurableParameterFunction<RationalNumber> SCALE = new ConfigurableParameterFunction<RationalNumber>() {

        public final double invoke(final double anArg, final int aParam) {
            return this.invoke(new RationalNumber(anArg), aParam).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg, final int aParam) {

            final BigDecimal tmpArg = TypeUtils.toBigDecimal(anArg);

            final BigDecimal tmpRet = BigFunction.SCALE.invoke(tmpArg, aParam);

            return TypeUtils.toRationalNumber(tmpRet);
        }
    };

    public static final UnaryFunction<RationalNumber> SIGNUM = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {
            return anArg.signum();
        }
    };

    public static final UnaryFunction<RationalNumber> SIN = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {

            final BigDecimal tmpArg = TypeUtils.toBigDecimal(anArg);

            final BigDecimal tmpRet = BigFunction.SIN.invoke(tmpArg);

            return TypeUtils.toRationalNumber(tmpRet);
        }
    };

    public static final UnaryFunction<RationalNumber> SINH = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {
            return (RationalFunction.EXP.invoke(anArg).subtract(RationalFunction.EXP.invoke(anArg.negate()))).divide(RationalNumber.TWO);
        }
    };

    public static final UnaryFunction<RationalNumber> SQRT = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {

            final BigDecimal tmpArg = TypeUtils.toBigDecimal(anArg);

            final BigDecimal tmpRet = BigFunction.SQRT.invoke(tmpArg);

            return TypeUtils.toRationalNumber(tmpRet);
        }
    };

    public static final UnaryFunction<RationalNumber> SQRT1PX2 = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {
            return SQRT.invoke(RationalNumber.ONE.add(anArg.multiply(anArg)));
        }
    };

    public static final ConfigurableBinaryFunction<RationalNumber> SUBTRACT = new ConfigurableBinaryFunction<RationalNumber>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new RationalNumber(anArg1), new RationalNumber(anArg2)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg1, final RationalNumber anArg2) {
            return anArg1.subtract(anArg2);
        }
    };

    public static final UnaryFunction<RationalNumber> TAN = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {

            final BigDecimal tmpArg = TypeUtils.toBigDecimal(anArg);

            final BigDecimal tmpRet = BigFunction.TAN.invoke(tmpArg);

            return TypeUtils.toRationalNumber(tmpRet);
        }
    };

    public static final UnaryFunction<RationalNumber> TANH = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {

            RationalNumber retVal;

            final RationalNumber tmpPlus = RationalFunction.EXP.invoke(anArg);
            final RationalNumber tmpMinus = RationalFunction.EXP.invoke(anArg.negate());

            final RationalNumber tmpDividend = tmpPlus.subtract(tmpMinus);
            final RationalNumber tmpDivisor = tmpPlus.add(tmpMinus);

            if (tmpDividend.equals(tmpDivisor)) {
                retVal = RationalNumber.ONE;
            } else if (tmpDividend.equals(tmpDivisor.negate())) {
                retVal = RationalNumber.ONE.negate();
            } else {
                retVal = tmpDividend.divide(tmpDivisor);
            }

            return retVal;
        }
    };

    public static final UnaryFunction<RationalNumber> VALUE = new UnaryFunction<RationalNumber>() {

        public final double invoke(final double anArg) {
            return this.invoke(new RationalNumber(anArg)).doubleValue();
        }

        public final RationalNumber invoke(final RationalNumber anArg) {
            return anArg;
        }
    };

    private static final RationalFunction SET = new RationalFunction();

    public static RationalFunction getSet() {
        return SET;
    }

    private RationalFunction() {
        super();
    }

    @Override
    public UnaryFunction<RationalNumber> abs() {
        return ABS;
    }

    @Override
    public UnaryFunction<RationalNumber> acos() {
        return ACOS;
    }

    @Override
    public UnaryFunction<RationalNumber> acosh() {
        return ACOSH;
    }

    @Override
    public ConfigurableBinaryFunction<RationalNumber> add() {
        return ADD;
    }

    @Override
    public UnaryFunction<RationalNumber> asin() {
        return ASIN;
    }

    @Override
    public UnaryFunction<RationalNumber> asinh() {
        return ASINH;
    }

    @Override
    public UnaryFunction<RationalNumber> atan() {
        return ATAN;
    }

    @Override
    public UnaryFunction<RationalNumber> atanh() {
        return ATANH;
    }

    @Override
    public UnaryFunction<RationalNumber> cardinality() {
        return CARDINALITY;
    }

    @Override
    public UnaryFunction<RationalNumber> conjugate() {
        return CONJUGATE;
    }

    @Override
    public UnaryFunction<RationalNumber> cos() {
        return COS;
    }

    @Override
    public UnaryFunction<RationalNumber> cosh() {
        return COSH;
    }

    @Override
    public ConfigurableBinaryFunction<RationalNumber> divide() {
        return DIVIDE;
    }

    @Override
    public UnaryFunction<RationalNumber> exp() {
        return EXP;
    }

    @Override
    public ConfigurableBinaryFunction<RationalNumber> hypot() {
        return HYPOT;
    }

    @Override
    public UnaryFunction<RationalNumber> invert() {
        return INVERT;
    }

    @Override
    public UnaryFunction<RationalNumber> log() {
        return LOG;
    }

    @Override
    public ConfigurableBinaryFunction<RationalNumber> max() {
        return MAX;
    }

    @Override
    public ConfigurableBinaryFunction<RationalNumber> min() {
        return MIN;
    }

    @Override
    public ConfigurableBinaryFunction<RationalNumber> multiply() {
        return MULTIPLY;
    }

    @Override
    public UnaryFunction<RationalNumber> negate() {
        return NEGATE;
    }

    @Override
    public ConfigurableBinaryFunction<RationalNumber> pow() {
        return POW;
    }

    @Override
    public ConfigurableParameterFunction<RationalNumber> power() {
        return POWER;
    }

    @Override
    public ConfigurableParameterFunction<RationalNumber> root() {
        return ROOT;
    }

    @Override
    public ConfigurableParameterFunction<RationalNumber> scale() {
        return SCALE;
    }

    @Override
    public UnaryFunction<RationalNumber> signum() {
        return SIGNUM;
    }

    @Override
    public UnaryFunction<RationalNumber> sin() {
        return SIN;
    }

    @Override
    public UnaryFunction<RationalNumber> sinh() {
        return SINH;
    }

    @Override
    public UnaryFunction<RationalNumber> sqrt() {
        return SQRT;
    }

    @Override
    public UnaryFunction<RationalNumber> sqrt1px2() {
        return SQRT1PX2;
    }

    @Override
    public ConfigurableBinaryFunction<RationalNumber> subtract() {
        return SUBTRACT;
    }

    @Override
    public UnaryFunction<RationalNumber> tan() {
        return TAN;
    }

    @Override
    public UnaryFunction<RationalNumber> tanh() {
        return TANH;
    }

    @Override
    public UnaryFunction<RationalNumber> value() {
        return VALUE;
    }

}
