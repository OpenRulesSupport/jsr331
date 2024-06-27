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
package org.ojalgo.type;

import java.awt.Paint;

public abstract class ChartBuilder<C extends ChartBuilder.ChartResource<?>, B extends ChartBuilder<C, B>> {

    public static interface ChartResource<T> {

        Paint getBackground();

        T getDelegate();

        int getHeight();

        String getMimeType();

        int getWidth();

        void setBackground(Paint aPaint);

        void setHeight(int aHeight);

        void setWidth(int aWidth);

        byte[] toByteArray();

    }

    public static enum Orientation {
        HORISONTAL, VERTICAL;
    }

    public static enum Priority {
        COLUMN, ROW;
    }

    private Paint myBackground = Colour.WHITE;
    private boolean myBorder = false;
    private boolean myLegend = false;
    private Orientation myOrientation = Orientation.VERTICAL;
    private Priority myPriority = Priority.COLUMN;
    private String myTitle = null;

    protected ChartBuilder() {
        super();
    }

    @SuppressWarnings("unchecked")
    public final B background(final Paint aPaint) {
        myBackground = aPaint;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public final B border(final boolean aFlag) {
        myBorder = aFlag;
        return (B) this;
    }

    public abstract C build();

    @SuppressWarnings("unchecked")
    public final B legend(final boolean aFlag) {
        myLegend = aFlag;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public final B orientation(final Orientation anOrientation) {
        myOrientation = anOrientation;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public final B priority(final Priority aPriority) {
        myPriority = aPriority;
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public final B title(final String aTitle) {
        myTitle = aTitle;
        return (B) this;
    }

    protected final Paint getBackground() {
        return myBackground;
    }

    protected final Orientation getOrientation() {
        return myOrientation;
    }

    protected final Priority getPriority() {
        return myPriority;
    }

    protected final String getTitle() {
        return myTitle;
    }

    protected final boolean isBorder() {
        return myBorder;
    }

    protected final boolean isLegend() {
        return myLegend;
    }

}
