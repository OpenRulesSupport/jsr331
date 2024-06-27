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
package org.ojalgo.scalar;

import java.math.BigDecimal;

import org.ojalgo.type.context.NumberContext;

/**
 * {@linkplain Scalar} was originally an abstraction of a matrix element
 * designed to be used in matrix related code. It is still used for
 * this to some extent, but now {@linkplain Scalar} primarily is the common
 * interface that defines {@linkplain ComplexNumber} and
 * {@linkplain RationalNumber}.
 *
 * @author apete
 */
public interface Scalar<N extends Number> extends Comparable<N> {

    /**
     * @see #add(double)
     * @see #add(Number)
     * @see #divide(double)
     * @see #divide(Number)
     * @see #multiply(double)
     * @see #multiply(Number)
     * @see #subtract(double)
     * @see #subtract(Number)
     */
    Scalar<N> add(double aNmbr);

    /**
     * @see #add(double)
     * @see #add(Number)
     * @see #divide(double)
     * @see #divide(Number)
     * @see #multiply(double)
     * @see #multiply(Number)
     * @see #subtract(double)
     * @see #subtract(Number)
     */
    Scalar<N> add(N aNmbr);

    /**
     * @see #conjugate()
     * @see #invert()
     * @see #negate()
     */
    Scalar<N> conjugate();

    /**
     * @see #add(double)
     * @see #add(Number)
     * @see #divide(double)
     * @see #divide(Number)
     * @see #multiply(double)
     * @see #multiply(Number)
     * @see #subtract(double)
     * @see #subtract(Number)
     */
    Scalar<N> divide(double aNmbr);

    /**
     * @see #add(double)
     * @see #add(Number)
     * @see #divide(double)
     * @see #divide(Number)
     * @see #multiply(double)
     * @see #multiply(Number)
     * @see #subtract(double)
     * @see #subtract(Number)
     */
    Scalar<N> divide(N aNmbr);

    double doubleValue();

    Scalar<N> enforce(NumberContext aCntxt);

    boolean equals(Scalar<?> aSclr);

    /**
     * @see #getArgument()
     * @see #getImaginary()
     * @see #getModulus()
     * @see #getReal()
     */
    double getArgument();

    /**
     * @see #getArgument()
     * @see #getImaginary()
     * @see #getModulus()
     * @see #getReal()
     */
    double getImaginary();

    /**
     * @see #getArgument()
     * @see #getImaginary()
     * @see #getModulus()
     * @see #getReal()
     */
    double getModulus();

    N getNumber();

    /**
     * @see #getArgument()
     * @see #getImaginary()
     * @see #getModulus()
     * @see #getReal()
     */
    double getReal();

    /**
     * @see #conjugate()
     * @see #invert()
     * @see #negate()
     */
    Scalar<N> invert();

    /**
     * @return true if this is equal to its own modulus (non-negative 
     * real part and no imaginary part); otherwise false.
     * 
     * @see #isAbsolute()
     * @see #isInfinite()
     * @see #isNaN()
     * @see #isReal()
     * @see #isPositive()
     * @see #isZero()
     */
    boolean isAbsolute();

    /**
     * @see #isAbsolute()
     * @see #isInfinite()
     * @see #isNaN()
     * @see #isReal()
     * @see #isPositive()
     * @see #isZero()
     */
    boolean isInfinite();

    /**
     * @see #isAbsolute()
     * @see #isInfinite()
     * @see #isNaN()
     * @see #isReal()
     * @see #isPositive()
     * @see #isZero()
     */
    boolean isNaN();

    /**
     * Strictly Positive, and definately real.
     * 
     * Real, as defined by {@link #isReal()}, not zero, as defined by {@link #isZero()}, and > 0.0.
     *  
     * @return true if the real part is strictly positive (not zero)
     * and the imaginary part negligible; otherwise false.
     * 
     * @see #isAbsolute()
     * @see #isInfinite()
     * @see #isNaN()
     * @see #isReal()
     * @see #isPositive()
     * @see #isZero()
     */
    boolean isPositive();

    /**
     * @return true if there is the imaginary part is negligible; otherwise false.
     * 
     * @see #isAbsolute()
     * @see #isInfinite()
     * @see #isNaN()
     * @see #isReal()
     * @see #isPositive()
     * @see #isZero()
     */
    boolean isReal();

    /**
     * Intends to capture if a scalar is numerically/practically zero,
     * and in a way that is concistent between different implementations.
     * The potential exactness of {@link BigScalar} and
     * {@link RationalNumber} should not be reflected here.
     * 
     * @return true if the modulus is (practically) zero; otherwise false.
     * 
     * @see #isAbsolute()
     * @see #isInfinite()
     * @see #isNaN()
     * @see #isReal()
     * @see #isPositive()
     * @see #isZero()
     */
    boolean isZero();

    /**
     * @see #add(double)
     * @see #add(Number)
     * @see #divide(double)
     * @see #divide(Number)
     * @see #multiply(double)
     * @see #multiply(Number)
     * @see #subtract(double)
     * @see #subtract(Number)
     */
    Scalar<N> multiply(double aNmbr);

    /**
     * @see #add(double)
     * @see #add(Number)
     * @see #divide(double)
     * @see #divide(Number)
     * @see #multiply(double)
     * @see #multiply(Number)
     * @see #subtract(double)
     * @see #subtract(Number)
     */
    Scalar<N> multiply(N aNmbr);

    /**
     * @see #conjugate()
     * @see #invert()
     * @see #negate()
     */
    Scalar<N> negate();

    /**
     * @see #power(int)
     * @see #root(int)
     */
    Scalar<N> power(int anExp);

    /**
     * @see #power(int)
     * @see #root(int)
     */
    Scalar<N> root(int anExp);

    Scalar<N> round(NumberContext aCntxt);

    Scalar<N> signum();

    /**
     * @see #add(double)
     * @see #add(Number)
     * @see #divide(double)
     * @see #divide(Number)
     * @see #multiply(double)
     * @see #multiply(Number)
     * @see #subtract(double)
     * @see #subtract(Number)
     */
    Scalar<N> subtract(double aNmbr);

    /**
     * @see #add(double)
     * @see #add(Number)
     * @see #divide(double)
     * @see #divide(Number)
     * @see #multiply(double)
     * @see #multiply(Number)
     * @see #subtract(double)
     * @see #subtract(Number)
     */
    Scalar<N> subtract(N aNmbr);

    BigDecimal toBigDecimal();

    ComplexNumber toComplexNumber();

    String toPlainString(NumberContext aCntxt);

    RationalNumber toRationalNumber();

    String toString(NumberContext aCntxt);

}
