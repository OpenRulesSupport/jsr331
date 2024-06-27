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

import static org.ojalgo.constant.BigMath.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.ojalgo.constant.BigMath;
import org.ojalgo.function.ConfigurableBinaryFunction;
import org.ojalgo.function.ConfigurableParameterFunction;
import org.ojalgo.function.UnaryFunction;

/**
 * Only the reference type parameter (BigDecimal) methods are actually
 * implemented. The methods with the primitive parameters (double) should
 * create a BigDecimal and then delegate to the primitive methods
 * (and do nothing else).
 * 
 * If possible the implementations should be pure BigDecimal arithmatic
 * without rounding. If rounding is necessary MathContext.DECIMAL128
 * should be used. If BigDecimal arithmatic is not possible at all the
 * implementation should delegate to PrimitiveFunction.
 *
 * @author apete
 */
public final class BigFunction extends FunctionSet<BigDecimal> {

    public static final UnaryFunction<BigDecimal> ABS = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return anArg.abs();
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> ACOS = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.ACOS.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> ACOSH = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.ACOSH.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<BigDecimal> ADD = new ConfigurableBinaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg1, final BigDecimal anArg2) {
            return anArg1.add(anArg2);
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(BigDecimal.valueOf(anArg1), BigDecimal.valueOf(anArg2)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> ASIN = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.ASIN.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> ASINH = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.ASINH.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> ATAN = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.ATAN.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> ATANH = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.ATANH.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> CARDINALITY = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return anArg.signum() == 0 ? ZERO : ONE;
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> CONJUGATE = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return anArg;
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> COS = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.COS.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> COSH = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.COSH.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<BigDecimal> DIVIDE = new ConfigurableBinaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg1, final BigDecimal anArg2) {
            return anArg1.divide(anArg2, CONTEXT); // Very slow!
            //return anArg1.divide(anArg2, CONTEXT.getPrecision(), CONTEXT.getRoundingMode()); // Faster...
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(BigDecimal.valueOf(anArg1), BigDecimal.valueOf(anArg2)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> EXP = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.EXP.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> EXPM1 = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.EXPM1.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<BigDecimal> HYPOT = new ConfigurableBinaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg1, final BigDecimal anArg2) {
            return SQRT.invoke(anArg1.multiply(anArg1).add(anArg2.multiply(anArg2)));
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(BigDecimal.valueOf(anArg1), BigDecimal.valueOf(anArg2)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> INVERT = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return DIVIDE.invoke(ONE, anArg);
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> SQRT1PX2 = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return SQRT.invoke(ONE.add(anArg.multiply(anArg)));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> LOG = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.LOG.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> LOG10 = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.LOG10.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> LOG1P = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.LOG1P.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<BigDecimal> MAX = new ConfigurableBinaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg1, final BigDecimal anArg2) {
            return anArg1.max(anArg2);
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(BigDecimal.valueOf(anArg1), BigDecimal.valueOf(anArg2)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<BigDecimal> MIN = new ConfigurableBinaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg1, final BigDecimal anArg2) {
            return anArg1.min(anArg2);
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(BigDecimal.valueOf(anArg1), BigDecimal.valueOf(anArg2)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<BigDecimal> MULTIPLY = new ConfigurableBinaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg1, final BigDecimal anArg2) {
            return anArg1.multiply(anArg2);
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(BigDecimal.valueOf(anArg1), BigDecimal.valueOf(anArg2)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> NEGATE = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return anArg.negate();
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<BigDecimal> POW = new ConfigurableBinaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg1, final BigDecimal anArg2) {
            return BigDecimal.valueOf(PrimitiveFunction.POW.invoke(anArg1.doubleValue(), anArg2.doubleValue()));
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(BigDecimal.valueOf(anArg1), BigDecimal.valueOf(anArg2)).doubleValue();
        }
    };

    public static final ConfigurableParameterFunction<BigDecimal> POWER = new ConfigurableParameterFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg, final int aParam) {
            return anArg.pow(aParam);
        }

        public final double invoke(final double anArg, final int aParam) {
            return this.invoke(BigDecimal.valueOf(anArg), aParam).doubleValue();
        }
    };

    public static final ConfigurableParameterFunction<BigDecimal> ROOT = new ConfigurableParameterFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg, final int aParam) {
            if (aParam <= 0) {
                throw new IllegalArgumentException();
            } else if (aParam == 1) {
                return anArg;
            } else if (aParam == 2) {
                return SQRT.invoke(anArg);
            } else {
                return BigDecimal.valueOf(PrimitiveFunction.ROOT.invoke(anArg.doubleValue(), aParam));
            }
        }

        public final double invoke(final double anArg, final int aParam) {
            return this.invoke(BigDecimal.valueOf(anArg), aParam).doubleValue();
        }
    };

    public static final ConfigurableParameterFunction<BigDecimal> SCALE = new ConfigurableParameterFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg, final int aParam) {
            return anArg.setScale(aParam, CONTEXT.getRoundingMode());
        }

        public final double invoke(final double anArg, final int aParam) {
            return this.invoke(BigDecimal.valueOf(anArg), aParam).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> SIGNUM = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            switch (anArg.signum()) {
            case 1:
                return ONE;
            case -1:
                return ONE.negate();
            default:
                return ZERO;
            }
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> SIN = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.SIN.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> SINH = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.SINH.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> SQRT = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {

            final double tmpArg = anArg.doubleValue();
            BigDecimal retVal = BigMath.ZERO;
            if (!Double.isInfinite(tmpArg) && !Double.isNaN(tmpArg)) {
                retVal = BigDecimal.valueOf(Math.sqrt(tmpArg));
            }

            final int tmpScale = CONTEXT.getPrecision();
            final RoundingMode tmpRoundingMode = CONTEXT.getRoundingMode();

            BigDecimal tmpShouldBeZero;
            while ((tmpShouldBeZero = retVal.multiply(retVal).subtract(anArg).setScale(tmpScale, tmpRoundingMode)).signum() != 0) {
                retVal = retVal.subtract(tmpShouldBeZero.divide(TWO.multiply(retVal), CONTEXT));
            }

            return retVal;
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<BigDecimal> SUBTRACT = new ConfigurableBinaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg1, final BigDecimal anArg2) {
            return anArg1.subtract(anArg2);
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(BigDecimal.valueOf(anArg1), BigDecimal.valueOf(anArg2)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> TAN = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.TAN.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> TANH = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return BigDecimal.valueOf(PrimitiveFunction.TANH.invoke(anArg.doubleValue()));
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<BigDecimal> VALUE = new UnaryFunction<BigDecimal>() {

        public final BigDecimal invoke(final BigDecimal anArg) {
            return anArg;
        }

        public final double invoke(final double anArg) {
            return this.invoke(BigDecimal.valueOf(anArg)).doubleValue();
        }
    };

    private static final MathContext CONTEXT = MathContext.DECIMAL128;
    private static final BigFunction SET = new BigFunction();

    public static BigFunction getSet() {
        return SET;
    }

    private BigFunction() {
        super();
    }

    @Override
    public UnaryFunction<BigDecimal> abs() {
        return ABS;
    }

    @Override
    public UnaryFunction<BigDecimal> acos() {
        return ACOS;
    }

    @Override
    public UnaryFunction<BigDecimal> acosh() {
        return ACOSH;
    }

    @Override
    public ConfigurableBinaryFunction<BigDecimal> add() {
        return ADD;
    }

    @Override
    public UnaryFunction<BigDecimal> asin() {
        return ASIN;
    }

    @Override
    public UnaryFunction<BigDecimal> asinh() {
        return ASINH;
    }

    @Override
    public UnaryFunction<BigDecimal> atan() {
        return ATAN;
    }

    @Override
    public UnaryFunction<BigDecimal> atanh() {
        return ATANH;
    }

    @Override
    public UnaryFunction<BigDecimal> cardinality() {
        return CARDINALITY;
    }

    @Override
    public UnaryFunction<BigDecimal> conjugate() {
        return CONJUGATE;
    }

    @Override
    public UnaryFunction<BigDecimal> cos() {
        return COS;
    }

    @Override
    public UnaryFunction<BigDecimal> cosh() {
        return COSH;
    }

    @Override
    public ConfigurableBinaryFunction<BigDecimal> divide() {
        return DIVIDE;
    }

    @Override
    public UnaryFunction<BigDecimal> exp() {
        return EXP;
    }

    @Override
    public ConfigurableBinaryFunction<BigDecimal> hypot() {
        return HYPOT;
    }

    @Override
    public UnaryFunction<BigDecimal> invert() {
        return INVERT;
    }

    @Override
    public UnaryFunction<BigDecimal> log() {
        return LOG;
    }

    @Override
    public ConfigurableBinaryFunction<BigDecimal> max() {
        return MAX;
    }

    @Override
    public ConfigurableBinaryFunction<BigDecimal> min() {
        return MIN;
    }

    @Override
    public ConfigurableBinaryFunction<BigDecimal> multiply() {
        return MULTIPLY;
    }

    @Override
    public UnaryFunction<BigDecimal> negate() {
        return NEGATE;
    }

    @Override
    public ConfigurableBinaryFunction<BigDecimal> pow() {
        return POW;
    }

    @Override
    public ConfigurableParameterFunction<BigDecimal> power() {
        return POWER;
    }

    @Override
    public ConfigurableParameterFunction<BigDecimal> root() {
        return ROOT;
    }

    @Override
    public ConfigurableParameterFunction<BigDecimal> scale() {
        return SCALE;
    }

    @Override
    public UnaryFunction<BigDecimal> signum() {
        return SIGNUM;
    }

    @Override
    public UnaryFunction<BigDecimal> sin() {
        return SIN;
    }

    @Override
    public UnaryFunction<BigDecimal> sinh() {
        return SINH;
    }

    @Override
    public UnaryFunction<BigDecimal> sqrt() {
        return SQRT;
    }

    @Override
    public UnaryFunction<BigDecimal> sqrt1px2() {
        return SQRT1PX2;
    }

    @Override
    public ConfigurableBinaryFunction<BigDecimal> subtract() {
        return SUBTRACT;
    }

    @Override
    public UnaryFunction<BigDecimal> tan() {
        return TAN;
    }

    @Override
    public UnaryFunction<BigDecimal> tanh() {
        return TANH;
    }

    @Override
    public UnaryFunction<BigDecimal> value() {
        return VALUE;
    }

}
