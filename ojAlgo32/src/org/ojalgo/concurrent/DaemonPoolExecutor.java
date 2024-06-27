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
package org.ojalgo.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ojalgo.OjAlgoUtils;

public final class DaemonPoolExecutor extends ThreadPoolExecutor {

    public static final DaemonPoolExecutor INSTANCE = new DaemonPoolExecutor(OjAlgoUtils.ENVIRONMENT.processors, Integer.MAX_VALUE, 2L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), DaemonFactory.INSTANCE);

    public static final DaemonPoolExecutor makeSingle() {
        return new DaemonPoolExecutor(1, 1, Long.MAX_VALUE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), DaemonFactory.INSTANCE);
    }

    private DaemonPoolExecutor(final int newCorePoolSize, final int newMaximumPoolSize, final long newKeepAliveTime, final TimeUnit newUnit, final BlockingQueue<Runnable> newWorkQueue, final ThreadFactory newThreadFactory) {
        super(newCorePoolSize, newMaximumPoolSize, newKeepAliveTime, newUnit, newWorkQueue, newThreadFactory);
    }

    private DaemonPoolExecutor(final int newCorePoolSize, final int newMaximumPoolSize, final long newKeepAliveTime, final TimeUnit newUnit, final BlockingQueue<Runnable> newWorkQueue, final ThreadFactory newThreadFactory, final RejectedExecutionHandler newHandler) {
        super(newCorePoolSize, newMaximumPoolSize, newKeepAliveTime, newUnit, newWorkQueue, newThreadFactory, newHandler);
    }

    public int countActiveDaemons() {
        return this.getActiveCount();
    }

    public int countExistingDaemons() {
        return this.getPoolSize();
    }

    public int countIdleDaemons() {
        return this.getPoolSize() - this.getActiveCount();
    }

    public int countPotentialDaemons() {
        return OjAlgoUtils.ENVIRONMENT.threads - this.getActiveCount();
    }

    public boolean isDaemonAvailable() {

        final int tmpCPUs = OjAlgoUtils.ENVIRONMENT.threads;
        final int tmpExisting = this.countExistingDaemons();

        if (tmpExisting < tmpCPUs) {

            return true;

        } else {

            final int tmpActive = this.countActiveDaemons();

            return (tmpActive < tmpCPUs) || ((tmpExisting <= (tmpCPUs + tmpCPUs)) && (tmpActive < tmpExisting));
        }
    }

}
