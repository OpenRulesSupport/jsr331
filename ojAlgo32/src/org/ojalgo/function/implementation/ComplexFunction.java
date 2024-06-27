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

import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.ConfigurableBinaryFunction;
import org.ojalgo.function.ConfigurableParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.TypeUtils;

public final class ComplexFunction extends FunctionSet<ComplexNumber> {

    public static final UnaryFunction<ComplexNumber> ABS = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {
            return new ComplexNumber(anArg.getModulus());
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> ACOS = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {

            ComplexNumber tmpNmbr = ComplexFunction.doInvSinAndCosPart1(anArg);

            tmpNmbr = anArg.add(ComplexNumber.I.multiply(tmpNmbr));

            return ComplexFunction.doInvSinAndCosPart2(tmpNmbr);
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> ACOSH = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {

            final ComplexNumber tmpNmbr = anArg.multiply(anArg).subtract(PrimitiveMath.ONE);

            return ComplexFunction.LOG.invoke(anArg.add(ComplexFunction.SQRT.invoke(tmpNmbr)));
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<ComplexNumber> ADD = new ConfigurableBinaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg1, final ComplexNumber anArg2) {
            return anArg1.add(anArg2);
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new ComplexNumber(anArg1), new ComplexNumber(anArg2)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> ASIN = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {

            ComplexNumber tmpNmbr = ComplexFunction.doInvSinAndCosPart1(anArg);

            tmpNmbr = ComplexNumber.I.multiply(anArg).add(tmpNmbr);

            return ComplexFunction.doInvSinAndCosPart2(tmpNmbr);
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> ASINH = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {

            final ComplexNumber tmpNmbr = anArg.multiply(anArg).add(PrimitiveMath.ONE);

            return ComplexFunction.LOG.invoke(anArg.add(ComplexFunction.SQRT.invoke(tmpNmbr)));
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> ATAN = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {

            final ComplexNumber tmpNmbr = ComplexNumber.I.add(anArg).divide(ComplexNumber.I.subtract(anArg));

            return ComplexFunction.LOG.invoke(tmpNmbr).multiply(ComplexNumber.I).divide(PrimitiveMath.TWO);
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> ATANH = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {

            final ComplexNumber tmpNmbr = anArg.add(PrimitiveMath.ONE).divide(ComplexNumber.ONE.subtract(anArg));

            return ComplexFunction.LOG.invoke(tmpNmbr).divide(PrimitiveMath.TWO);
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> CARDINALITY = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {
            return TypeUtils.isZero(anArg.getModulus()) ? ComplexNumber.ZERO : ComplexNumber.ONE;
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> CONJUGATE = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {
            return anArg.conjugate();
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> COS = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {
            return ComplexFunction.COSH.invoke(anArg.multiply(ComplexNumber.I));
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> COSH = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {
            return (ComplexFunction.EXP.invoke(anArg).add(ComplexFunction.EXP.invoke(anArg.negate()))).divide(PrimitiveMath.TWO);
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<ComplexNumber> DIVIDE = new ConfigurableBinaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg1, final ComplexNumber anArg2) {
            return anArg1.divide(anArg2);
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new ComplexNumber(anArg1), new ComplexNumber(anArg2)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> EXP = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {

            final double retMod = Math.exp(anArg.getReal());
            final double retArg = anArg.getImaginary();

            return ComplexNumber.makePolar(retMod, retArg);
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<ComplexNumber> HYPOT = new ConfigurableBinaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg1, final ComplexNumber anArg2) {
            return new ComplexNumber(Math.hypot(anArg1.getModulus(), anArg2.getModulus()));
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new ComplexNumber(anArg1), new ComplexNumber(anArg2)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> INVERT = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {
            return POWER.invoke(anArg, -1);
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> SQRT1PX2 = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {
            return SQRT.invoke(ComplexNumber.ONE.add(anArg.multiply(anArg)));
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> LOG = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {

            final double retRe = Math.log(anArg.getModulus());
            final double retIm = anArg.getArgument();

            return ComplexNumber.makeRectangular(retRe, retIm);
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<ComplexNumber> MAX = new ConfigurableBinaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg1, final ComplexNumber anArg2) {

            ComplexNumber retVal = null;

            if (anArg1.getModulus() >= anArg2.getModulus()) {
                retVal = anArg1;
            } else {
                retVal = anArg2;
            }

            return retVal;
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new ComplexNumber(anArg1), new ComplexNumber(anArg2)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<ComplexNumber> MIN = new ConfigurableBinaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg1, final ComplexNumber anArg2) {

            ComplexNumber retVal = null;

            if (anArg1.getModulus() <= anArg2.getModulus()) {
                retVal = anArg1;
            } else {
                retVal = anArg2;
            }

            return retVal;
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new ComplexNumber(anArg1), new ComplexNumber(anArg2)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<ComplexNumber> MULTIPLY = new ConfigurableBinaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg1, final ComplexNumber anArg2) {
            return anArg1.multiply(anArg2);
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new ComplexNumber(anArg1), new ComplexNumber(anArg2)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> NEGATE = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {
            return anArg.negate();
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<ComplexNumber> POW = new ConfigurableBinaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg1, final ComplexNumber anArg2) {
            return ComplexFunction.EXP.invoke(anArg2.multiply(ComplexFunction.LOG.invoke(anArg1)));
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new ComplexNumber(anArg1), new ComplexNumber(anArg2)).doubleValue();
        }
    };

    public static final ConfigurableParameterFunction<ComplexNumber> POWER = new ConfigurableParameterFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg, final int aParam) {

            final double retMod = PrimitiveFunction.POWER.invoke(anArg.getModulus(), aParam);
            final double retArg = anArg.getArgument() * aParam;

            return ComplexNumber.makePolar(retMod, retArg);
        }

        public final double invoke(final double anArg, final int aParam) {
            return this.invoke(new ComplexNumber(anArg), aParam).doubleValue();
        }
    };

    public static final ConfigurableParameterFunction<ComplexNumber> ROOT = new ConfigurableParameterFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg, final int aParam) {

            if (aParam != 0) {

                final double tmpExp = PrimitiveMath.ONE / aParam;

                final double retMod = Math.pow(anArg.getModulus(), tmpExp);
                final double retArg = anArg.getArgument() * tmpExp;

                return ComplexNumber.makePolar(retMod, retArg);

            } else {

                throw new IllegalArgumentException();
            }
        }

        public final double invoke(final double anArg, final int aParam) {
            return this.invoke(new ComplexNumber(anArg), aParam).doubleValue();
        }
    };

    public static final ConfigurableParameterFunction<ComplexNumber> SCALE = new ConfigurableParameterFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg, final int aParam) {
            final double tmpRe = PrimitiveFunction.SCALE.invoke(anArg.getReal(), aParam);
            final double tmpIm = PrimitiveFunction.SCALE.invoke(anArg.getImaginary(), aParam);
            return ComplexNumber.makeRectangular(tmpRe, tmpIm);
        }

        public final double invoke(final double anArg, final int aParam) {
            return this.invoke(new ComplexNumber(anArg), aParam).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> SIGNUM = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {
            return anArg.signum();
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> SIN = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {
            return ComplexFunction.SINH.invoke(anArg.multiply(ComplexNumber.I)).multiply(ComplexNumber.I.negate());
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> SINH = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {
            return (ComplexFunction.EXP.invoke(anArg).subtract(ComplexFunction.EXP.invoke(anArg.negate()))).divide(PrimitiveMath.TWO);
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> SQRT = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {

            final double retMod = Math.sqrt(anArg.getModulus());
            final double retArg = anArg.getArgument() * PrimitiveMath.HALF;

            return ComplexNumber.makePolar(retMod, retArg);
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final ConfigurableBinaryFunction<ComplexNumber> SUBTRACT = new ConfigurableBinaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg1, final ComplexNumber anArg2) {
            return anArg1.subtract(anArg2);
        }

        public final double invoke(final double anArg1, final double anArg2) {
            return this.invoke(new ComplexNumber(anArg1), new ComplexNumber(anArg2)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> TAN = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {
            return ComplexFunction.TANH.invoke(anArg.multiply(ComplexNumber.I)).multiply(ComplexNumber.I.negate());
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> TANH = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {

            ComplexNumber retVal;

            final ComplexNumber tmpPlus = ComplexFunction.EXP.invoke(anArg);
            final ComplexNumber tmpMinus = ComplexFunction.EXP.invoke(anArg.negate());

            final ComplexNumber tmpDividend = tmpPlus.subtract(tmpMinus);
            final ComplexNumber tmpDivisor = tmpPlus.add(tmpMinus);

            if (tmpDividend.equals(tmpDivisor)) {
                retVal = ComplexNumber.ONE;
            } else if (tmpDividend.equals(tmpDivisor.negate())) {
                retVal = ComplexNumber.ONE.negate();
            } else {
                retVal = tmpDividend.divide(tmpDivisor);
            }

            return retVal;
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    public static final UnaryFunction<ComplexNumber> VALUE = new UnaryFunction<ComplexNumber>() {

        public final ComplexNumber invoke(final ComplexNumber anArg) {
            return anArg;
        }

        public final double invoke(final double anArg) {
            return this.invoke(new ComplexNumber(anArg)).doubleValue();
        }
    };

    private static final ComplexFunction SET = new ComplexFunction();

    public static ComplexFunction getSet() {
        return SET;
    }

    private static ComplexNumber doInvSinAndCosPart1(final ComplexNumber aNumber) {
        return SQRT.invoke(ComplexNumber.ONE.subtract(ComplexFunction.POWER.invoke(aNumber, 2)));
    }

    private static ComplexNumber doInvSinAndCosPart2(final ComplexNumber aNumber) {
        return LOG.invoke(aNumber).multiply(ComplexNumber.I).negate();
    }

    private ComplexFunction() {
        super();
    }

    @Override
    public UnaryFunction<ComplexNumber> abs() {
        return ABS;
    }

    @Override
    public UnaryFunction<ComplexNumber> acos() {
        return ACOS;
    }

    @Override
    public UnaryFunction<ComplexNumber> acosh() {
        return ACOSH;
    }

    @Override
    public ConfigurableBinaryFunction<ComplexNumber> add() {
        return ADD;
    }

    @Override
    public UnaryFunction<ComplexNumber> asin() {
        return ASIN;
    }

    @Override
    public UnaryFunction<ComplexNumber> asinh() {
        return ASINH;
    }

    @Override
    public UnaryFunction<ComplexNumber> atan() {
        return ATAN;
    }

    @Override
    public UnaryFunction<ComplexNumber> atanh() {
        return ATANH;
    }

    @Override
    public UnaryFunction<ComplexNumber> cardinality() {
        return CARDINALITY;
    }

    @Override
    public UnaryFunction<ComplexNumber> conjugate() {
        return CONJUGATE;
    }

    @Override
    public UnaryFunction<ComplexNumber> cos() {
        return COS;
    }

    @Override
    public UnaryFunction<ComplexNumber> cosh() {
        return COSH;
    }

    @Override
    public ConfigurableBinaryFunction<ComplexNumber> divide() {
        return DIVIDE;
    }

    @Override
    public UnaryFunction<ComplexNumber> exp() {
        return EXP;
    }

    @Override
    public ConfigurableBinaryFunction<ComplexNumber> hypot() {
        return HYPOT;
    }

    @Override
    public UnaryFunction<ComplexNumber> invert() {
        return INVERT;
    }

    @Override
    public UnaryFunction<ComplexNumber> log() {
        return LOG;
    }

    @Override
    public ConfigurableBinaryFunction<ComplexNumber> max() {
        return MAX;
    }

    @Override
    public ConfigurableBinaryFunction<ComplexNumber> min() {
        return MIN;
    }

    @Override
    public ConfigurableBinaryFunction<ComplexNumber> multiply() {
        return MULTIPLY;
    }

    @Override
    public UnaryFunction<ComplexNumber> negate() {
        return NEGATE;
    }

    @Override
    public ConfigurableBinaryFunction<ComplexNumber> pow() {
        return POW;
    }

    @Override
    public ConfigurableParameterFunction<ComplexNumber> power() {
        return POWER;
    }

    @Override
    public ConfigurableParameterFunction<ComplexNumber> root() {
        return ROOT;
    }

    @Override
    public ConfigurableParameterFunction<ComplexNumber> scale() {
        return SCALE;
    }

    @Override
    public UnaryFunction<ComplexNumber> signum() {
        return SIGNUM;
    }

    @Override
    public UnaryFunction<ComplexNumber> sin() {
        return SIN;
    }

    @Override
    public UnaryFunction<ComplexNumber> sinh() {
        return SINH;
    }

    @Override
    public UnaryFunction<ComplexNumber> sqrt() {
        return SQRT;
    }

    @Override
    public UnaryFunction<ComplexNumber> sqrt1px2() {
        return SQRT1PX2;
    }

    @Override
    public ConfigurableBinaryFunction<ComplexNumber> subtract() {
        return SUBTRACT;
    }

    @Override
    public UnaryFunction<ComplexNumber> tan() {
        return TAN;
    }

    @Override
    public UnaryFunction<ComplexNumber> tanh() {
        return TANH;
    }

    @Override
    public UnaryFunction<ComplexNumber> value() {
        return VALUE;
    }

}
