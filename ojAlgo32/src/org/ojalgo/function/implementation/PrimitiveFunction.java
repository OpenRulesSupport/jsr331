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

import static org.ojalgo.constant.PrimitiveMath.*;

import org.ojalgo.ProgrammingError;
import org.ojalgo.function.ConfigurableBinaryFunction;
import org.ojalgo.function.ConfigurableParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.type.TypeUtils;

/**
 * Only the primitive parameter (double) methods are actually implemented.
 * The methods with the reference type parameters (Double) should delegate
 * to the primitive methods (and do nothing else).
 * 
 * The various implementations should delegate as much as possible to
 * {@link java.lang.Math} and/or built-in Java operators.
 *
 * @author apete
 */
public final class PrimitiveFunction extends FunctionSet<Double> {

    public static final UnaryFunction<Double> ABS = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.abs(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> ACOS = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.acos(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> ACOSH = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.log(anArg + Math.sqrt((anArg * anArg) - ONE));
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final ConfigurableBinaryFunction<Double> ADD = new ConfigurableBinaryFunction<Double>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return anArg1 + anArg2;
        }

        public final Double invoke(final Double anArg1, final Double anArg2) {
            return this.invoke(anArg1.doubleValue(), anArg2.doubleValue());
        }
    };

    public static final UnaryFunction<Double> ASIN = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.asin(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> ASINH = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.log(anArg + Math.sqrt((anArg * anArg) + ONE));
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> ATAN = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.atan(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> ATANH = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.log((ONE + anArg) / (ONE - anArg)) / TWO;
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> CARDINALITY = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return TypeUtils.isZero(anArg) ? ZERO : ONE;
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> CONJUGATE = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return anArg;
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> SQRT1PX2 = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.sqrt(ONE + (anArg * anArg));
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> COS = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.cos(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> COSH = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.cosh(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final ConfigurableBinaryFunction<Double> DIVIDE = new ConfigurableBinaryFunction<Double>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return anArg1 / anArg2;
        }

        public final Double invoke(final Double anArg1, final Double anArg2) {
            return this.invoke(anArg1.doubleValue(), anArg2.doubleValue());
        }
    };

    public static final UnaryFunction<Double> EXP = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.exp(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> EXPM1 = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.expm1(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final ConfigurableBinaryFunction<Double> HYPOT = new ConfigurableBinaryFunction<Double>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return Math.hypot(anArg1, anArg2);
        }

        public final Double invoke(final Double anArg1, final Double anArg2) {
            return this.invoke(anArg1.doubleValue(), anArg2.doubleValue());
        }
    };

    public static final UnaryFunction<Double> INVERT = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return ONE / anArg;
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> LOG = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.log(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> LOG10 = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.log10(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> LOG1P = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.log1p(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final ConfigurableBinaryFunction<Double> MAX = new ConfigurableBinaryFunction<Double>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return Math.max(anArg1, anArg2);
        }

        public final Double invoke(final Double anArg1, final Double anArg2) {
            return this.invoke(anArg1.doubleValue(), anArg2.doubleValue());
        }
    };

    public static final ConfigurableBinaryFunction<Double> MIN = new ConfigurableBinaryFunction<Double>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return Math.min(anArg1, anArg2);
        }

        public final Double invoke(final Double anArg1, final Double anArg2) {
            return this.invoke(anArg1.doubleValue(), anArg2.doubleValue());
        }
    };

    public static final ConfigurableBinaryFunction<Double> MULTIPLY = new ConfigurableBinaryFunction<Double>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return anArg1 * anArg2;
        }

        public final Double invoke(final Double anArg1, final Double anArg2) {
            return this.invoke(anArg1.doubleValue(), anArg2.doubleValue());
        }
    };

    public static final UnaryFunction<Double> NEGATE = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return -anArg;
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final ConfigurableBinaryFunction<Double> POW = new ConfigurableBinaryFunction<Double>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return Math.pow(anArg1, anArg2);
        }

        public final Double invoke(final Double anArg1, final Double anArg2) {
            return this.invoke(anArg1.doubleValue(), anArg2.doubleValue());
        }
    };

    public static final ConfigurableParameterFunction<Double> POWER = new ConfigurableParameterFunction<Double>() {

        public final double invoke(final double anArg, int aParam) {

            double retVal = ONE;

            if (aParam < 0) {

                retVal = INVERT.invoke(POWER.invoke(anArg, -aParam));

            } else {

                while (aParam > 0) {

                    retVal = retVal * anArg;

                    aParam--;
                }
            }

            return retVal;
        }

        public final Double invoke(final Double anArg, final int aParam) {
            return this.invoke(anArg.doubleValue(), aParam);
        }
    };

    public static final ConfigurableParameterFunction<Double> ROOT = new ConfigurableParameterFunction<Double>() {

        public final double invoke(final double anArg, final int aParam) {

            if (aParam != 0) {
                return Math.pow(anArg, ONE / aParam);
            } else {
                throw new IllegalArgumentException();
            }
        }

        public final Double invoke(final Double anArg, final int aParam) {
            return this.invoke(anArg.doubleValue(), aParam);
        }
    };

    public static final ConfigurableParameterFunction<Double> SCALE = new ConfigurableParameterFunction<Double>() {

        public final double invoke(final double anArg, int aParam) {

            if (aParam < 0) {
                throw new ProgrammingError("Cannot have exponents smaller than zero.");
            }

            long tmpFactor = 1l;
            final long tmp10 = (long) TEN;

            while (aParam > 0) {
                tmpFactor *= tmp10;
                aParam--;
            }

            return Math.rint(tmpFactor * anArg) / tmpFactor;
        }

        public final Double invoke(final Double anArg, final int aParam) {
            return this.invoke(anArg.doubleValue(), aParam);
        }
    };

    public static final UnaryFunction<Double> SIGNUM = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.signum(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> SIN = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.sin(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> SINH = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.sinh(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> SQRT = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.sqrt(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final ConfigurableBinaryFunction<Double> SUBTRACT = new ConfigurableBinaryFunction<Double>() {

        public final double invoke(final double anArg1, final double anArg2) {
            return anArg1 - anArg2;
        }

        public final Double invoke(final Double anArg1, final Double anArg2) {
            return this.invoke(anArg1.doubleValue(), anArg2.doubleValue());
        }
    };

    public static final UnaryFunction<Double> TAN = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.tan(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> TANH = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return Math.tanh(anArg);
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    public static final UnaryFunction<Double> VALUE = new UnaryFunction<Double>() {

        public final double invoke(final double anArg) {
            return anArg;
        }

        public final Double invoke(final Double anArg) {
            return this.invoke(anArg.doubleValue());
        }
    };

    private static final PrimitiveFunction SET = new PrimitiveFunction();

    public static PrimitiveFunction getSet() {
        return SET;
    }

    private PrimitiveFunction() {
        super();
    }

    @Override
    public UnaryFunction<Double> abs() {
        return ABS;
    }

    @Override
    public UnaryFunction<Double> acos() {
        return ACOS;
    }

    @Override
    public UnaryFunction<Double> acosh() {
        return ACOSH;
    }

    @Override
    public ConfigurableBinaryFunction<Double> add() {
        return ADD;
    }

    @Override
    public UnaryFunction<Double> asin() {
        return ASIN;
    }

    @Override
    public UnaryFunction<Double> asinh() {
        return ASINH;
    }

    @Override
    public UnaryFunction<Double> atan() {
        return ATAN;
    }

    @Override
    public UnaryFunction<Double> atanh() {
        return ATANH;
    }

    @Override
    public UnaryFunction<Double> cardinality() {
        return CARDINALITY;
    }

    @Override
    public UnaryFunction<Double> conjugate() {
        return CONJUGATE;
    }

    @Override
    public UnaryFunction<Double> cos() {
        return COS;
    }

    @Override
    public UnaryFunction<Double> cosh() {
        return COSH;
    }

    @Override
    public ConfigurableBinaryFunction<Double> divide() {
        return DIVIDE;
    }

    @Override
    public UnaryFunction<Double> exp() {
        return EXP;
    }

    @Override
    public ConfigurableBinaryFunction<Double> hypot() {
        return HYPOT;
    }

    @Override
    public UnaryFunction<Double> invert() {
        return INVERT;
    }

    @Override
    public UnaryFunction<Double> log() {
        return LOG;
    }

    @Override
    public ConfigurableBinaryFunction<Double> max() {
        return MAX;
    }

    @Override
    public ConfigurableBinaryFunction<Double> min() {
        return MIN;
    }

    @Override
    public ConfigurableBinaryFunction<Double> multiply() {
        return MULTIPLY;
    }

    @Override
    public UnaryFunction<Double> negate() {
        return NEGATE;
    }

    @Override
    public ConfigurableBinaryFunction<Double> pow() {
        return POW;
    }

    @Override
    public ConfigurableParameterFunction<Double> power() {
        return POWER;
    }

    @Override
    public ConfigurableParameterFunction<Double> root() {
        return ROOT;
    }

    @Override
    public ConfigurableParameterFunction<Double> scale() {
        return SCALE;
    }

    @Override
    public UnaryFunction<Double> signum() {
        return SIGNUM;
    }

    @Override
    public UnaryFunction<Double> sin() {
        return SIN;
    }

    @Override
    public UnaryFunction<Double> sinh() {
        return SINH;
    }

    @Override
    public UnaryFunction<Double> sqrt() {
        return SQRT;
    }

    @Override
    public UnaryFunction<Double> sqrt1px2() {
        return SQRT1PX2;
    }

    @Override
    public ConfigurableBinaryFunction<Double> subtract() {
        return SUBTRACT;
    }

    @Override
    public UnaryFunction<Double> tan() {
        return TAN;
    }

    @Override
    public UnaryFunction<Double> tanh() {
        return TANH;
    }

    @Override
    public UnaryFunction<Double> value() {
        return VALUE;
    }

}
