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
package org.ojalgo.function.aggregator;

import static org.ojalgo.function.implementation.ComplexFunction.*;

import org.ojalgo.ProgrammingError;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;

public abstract class ComplexAggregator {

    public static final ThreadLocal<AggregatorFunction<ComplexNumber>> CARDINALITY = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new AggregatorFunction<ComplexNumber>() {

                private int myCount = 0;

                public double doubleValue() {
                    return this.getNumber().doubleValue();
                }

                public ComplexNumber getNumber() {
                    return new ComplexNumber(myCount);
                }

                public int intValue() {
                    return myCount;
                }

                public void invoke(final ComplexNumber anArg) {
                    if (!TypeUtils.isZero(anArg.getModulus())) {
                        myCount++;
                    }
                }

                public void invoke(final double anArg) {
                    this.invoke(new ComplexNumber(anArg));
                }

                public void merge(final ComplexNumber anArg) {
                    myCount += anArg.intValue();
                }

                public ComplexNumber merge(final ComplexNumber aResult1, final ComplexNumber aResult2) {
                    return ADD.invoke(aResult1, aResult2);
                }

                public AggregatorFunction<ComplexNumber> reset() {
                    myCount = 0;
                    return this;
                }

                public Scalar<ComplexNumber> toScalar() {
                    return this.getNumber();
                }

            };
        }
    };

    public static final ThreadLocal<AggregatorFunction<ComplexNumber>> LARGEST = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new AggregatorFunction<ComplexNumber>() {

                private ComplexNumber myNumber = ComplexNumber.ZERO;

                public double doubleValue() {
                    return this.getNumber().doubleValue();
                }

                public ComplexNumber getNumber() {
                    return myNumber;
                }

                public int intValue() {
                    return this.getNumber().intValue();
                }

                public void invoke(final ComplexNumber anArg) {
                    myNumber = MAX.invoke(myNumber, ABS.invoke(anArg));
                }

                public void invoke(final double anArg) {
                    this.invoke(new ComplexNumber(anArg));
                }

                public void merge(final ComplexNumber anArg) {
                    this.invoke(anArg);
                }

                public ComplexNumber merge(final ComplexNumber aResult1, final ComplexNumber aResult2) {
                    return MAX.invoke(aResult1, aResult2);
                }

                public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ZERO;
                    return this;
                }

                public Scalar<ComplexNumber> toScalar() {
                    return this.getNumber();
                }
            };
        }
    };

    public static final ThreadLocal<AggregatorFunction<ComplexNumber>> NORM1 = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new AggregatorFunction<ComplexNumber>() {

                private ComplexNumber myNumber = ComplexNumber.ZERO;

                public double doubleValue() {
                    return this.getNumber().doubleValue();
                }

                public ComplexNumber getNumber() {
                    return myNumber;
                }

                public int intValue() {
                    return this.getNumber().intValue();
                }

                public void invoke(final ComplexNumber anArg) {
                    myNumber = myNumber.add(anArg.getModulus());
                }

                public void invoke(final double anArg) {
                    this.invoke(new ComplexNumber(anArg));
                }

                public void merge(final ComplexNumber anArg) {
                    this.invoke(anArg);
                }

                public ComplexNumber merge(final ComplexNumber aResult1, final ComplexNumber aResult2) {
                    return ADD.invoke(aResult1, aResult2);
                }

                public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ZERO;
                    return this;
                }

                public Scalar<ComplexNumber> toScalar() {
                    return this.getNumber();
                }
            };
        }
    };

    public static final ThreadLocal<AggregatorFunction<ComplexNumber>> NORM2 = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new AggregatorFunction<ComplexNumber>() {

                private ComplexNumber myNumber = ComplexNumber.ZERO;

                public double doubleValue() {
                    return this.getNumber().doubleValue();
                }

                public ComplexNumber getNumber() {
                    return new ComplexNumber(Math.sqrt(myNumber.getModulus()));
                }

                public int intValue() {
                    return this.getNumber().intValue();
                }

                public void invoke(final ComplexNumber anArg) {
                    final double tmpMod = anArg.getModulus();
                    myNumber = myNumber.add(tmpMod * tmpMod);
                }

                public void invoke(final double anArg) {
                    this.invoke(new ComplexNumber(anArg));
                }

                public void merge(final ComplexNumber anArg) {
                    this.invoke(anArg);
                }

                public ComplexNumber merge(final ComplexNumber aResult1, final ComplexNumber aResult2) {
                    return HYPOT.invoke(aResult1, aResult2);
                }

                public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ZERO;
                    return this;
                }

                public Scalar<ComplexNumber> toScalar() {
                    return this.getNumber();
                }
            };
        }
    };

    public static final ThreadLocal<AggregatorFunction<ComplexNumber>> PRODUCT = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new AggregatorFunction<ComplexNumber>() {

                private ComplexNumber myNumber = ComplexNumber.ONE;

                public double doubleValue() {
                    return this.getNumber().doubleValue();
                }

                public ComplexNumber getNumber() {
                    return myNumber;
                }

                public int intValue() {
                    return this.getNumber().intValue();
                }

                public void invoke(final ComplexNumber anArg) {
                    myNumber = myNumber.multiply(anArg);
                }

                public void invoke(final double anArg) {
                    this.invoke(new ComplexNumber(anArg));
                }

                public void merge(final ComplexNumber anArg) {
                    this.invoke(anArg);
                }

                public ComplexNumber merge(final ComplexNumber aResult1, final ComplexNumber aResult2) {
                    return MULTIPLY.invoke(aResult1, aResult2);
                }

                public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ONE;
                    return this;
                }

                public Scalar<ComplexNumber> toScalar() {
                    return this.getNumber();
                }
            };
        }
    };

    public static final ThreadLocal<AggregatorFunction<ComplexNumber>> PRODUCT2 = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new AggregatorFunction<ComplexNumber>() {

                private ComplexNumber myNumber = ComplexNumber.ONE;

                public double doubleValue() {
                    return this.getNumber().doubleValue();
                }

                public ComplexNumber getNumber() {
                    return myNumber;
                }

                public int intValue() {
                    return this.getNumber().intValue();
                }

                public void invoke(final ComplexNumber anArg) {
                    myNumber = myNumber.multiply(anArg.multiply(anArg));
                }

                public void invoke(final double anArg) {
                    this.invoke(new ComplexNumber(anArg));
                }

                public void merge(final ComplexNumber anArg) {
                    myNumber = myNumber.multiply(anArg);
                }

                public ComplexNumber merge(final ComplexNumber aResult1, final ComplexNumber aResult2) {
                    return MULTIPLY.invoke(aResult1, aResult2);
                }

                public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ONE;
                    return this;
                }

                public Scalar<ComplexNumber> toScalar() {
                    return this.getNumber();
                }
            };
        }
    };

    public static final ThreadLocal<AggregatorFunction<ComplexNumber>> SMALLEST = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new AggregatorFunction<ComplexNumber>() {

                private ComplexNumber myNumber = ComplexNumber.INFINITY;

                public double doubleValue() {
                    return this.getNumber().doubleValue();
                }

                public ComplexNumber getNumber() {
                    if (myNumber.isInfinite()) {
                        return ComplexNumber.ZERO;
                    } else {
                        return myNumber;
                    }
                }

                public int intValue() {
                    return this.getNumber().intValue();
                }

                public void invoke(final ComplexNumber anArg) {
                    if (!anArg.isZero()) {
                        myNumber = MIN.invoke(myNumber, ABS.invoke(anArg));
                    }
                }

                public void invoke(final double anArg) {
                    this.invoke(new ComplexNumber(anArg));
                }

                public void merge(final ComplexNumber anArg) {
                    this.invoke(anArg);
                }

                public ComplexNumber merge(final ComplexNumber aResult1, final ComplexNumber aResult2) {
                    return MIN.invoke(aResult1, aResult2);
                }

                public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.INFINITY;
                    return this;
                }

                public Scalar<ComplexNumber> toScalar() {
                    return this.getNumber();
                }
            };
        }
    };

    public static final ThreadLocal<AggregatorFunction<ComplexNumber>> SUM = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new AggregatorFunction<ComplexNumber>() {

                private ComplexNumber myNumber = ComplexNumber.ZERO;

                public double doubleValue() {
                    return this.getNumber().doubleValue();
                }

                public ComplexNumber getNumber() {
                    return myNumber;
                }

                public int intValue() {
                    return this.getNumber().intValue();
                }

                public void invoke(final ComplexNumber anArg) {
                    myNumber = myNumber.add(anArg);
                }

                public void invoke(final double anArg) {
                    this.invoke(new ComplexNumber(anArg));
                }

                public void merge(final ComplexNumber anArg) {
                    this.invoke(anArg);
                }

                public ComplexNumber merge(final ComplexNumber aResult1, final ComplexNumber aResult2) {
                    return ADD.invoke(aResult1, aResult2);
                }

                public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ZERO;
                    return this;
                }

                public Scalar<ComplexNumber> toScalar() {
                    return this.getNumber();
                }
            };
        }
    };

    public static final ThreadLocal<AggregatorFunction<ComplexNumber>> SUM2 = new ThreadLocal<AggregatorFunction<ComplexNumber>>() {

        @Override
        protected AggregatorFunction<ComplexNumber> initialValue() {
            return new AggregatorFunction<ComplexNumber>() {

                private ComplexNumber myNumber = ComplexNumber.ZERO;

                public double doubleValue() {
                    return this.getNumber().doubleValue();
                }

                public ComplexNumber getNumber() {
                    return myNumber;
                }

                public int intValue() {
                    return this.getNumber().intValue();
                }

                public void invoke(final ComplexNumber anArg) {
                    myNumber = myNumber.add(anArg.multiply(anArg));
                }

                public void invoke(final double anArg) {
                    this.invoke(new ComplexNumber(anArg));
                }

                public void merge(final ComplexNumber anArg) {
                    myNumber = myNumber.add(anArg);
                }

                public ComplexNumber merge(final ComplexNumber aResult1, final ComplexNumber aResult2) {
                    return ADD.invoke(aResult1, aResult2);
                }

                public AggregatorFunction<ComplexNumber> reset() {
                    myNumber = ComplexNumber.ZERO;
                    return this;
                }

                public Scalar<ComplexNumber> toScalar() {
                    return this.getNumber();
                }
            };
        }
    };

    private static final AggregatorCollection<ComplexNumber> COLLECTION = new AggregatorCollection<ComplexNumber>() {

        @Override
        public AggregatorFunction<ComplexNumber> cardinality() {
            return CARDINALITY.get().reset();
        }

        @Override
        public AggregatorFunction<ComplexNumber> largest() {
            return LARGEST.get().reset();
        }

        @Override
        public AggregatorFunction<ComplexNumber> norm1() {
            return NORM1.get().reset();
        }

        @Override
        public AggregatorFunction<ComplexNumber> norm2() {
            return NORM2.get().reset();
        }

        @Override
        public AggregatorFunction<ComplexNumber> product() {
            return PRODUCT.get().reset();
        }

        @Override
        public AggregatorFunction<ComplexNumber> product2() {
            return PRODUCT2.get().reset();
        }

        @Override
        public AggregatorFunction<ComplexNumber> smallest() {
            return SMALLEST.get().reset();
        }

        @Override
        public AggregatorFunction<ComplexNumber> sum() {
            return SUM.get().reset();
        }

        @Override
        public AggregatorFunction<ComplexNumber> sum2() {
            return SUM2.get().reset();
        }

    };

    public static AggregatorCollection<ComplexNumber> getCollection() {
        return COLLECTION;
    }

    private ComplexAggregator() {

        super();

        ProgrammingError.throwForIllegalInvocation();
    }

}
