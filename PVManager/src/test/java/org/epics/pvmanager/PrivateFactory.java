/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import org.epics.util.time.TimeDuration;

/**
 * Exports package private constructors for testing purposes.
 * @author carcassi
 */
public class PrivateFactory {
    public static <T> Collector<T> newTimeCacheCollector(Function<T> function, TimeDuration cachedPeriod) {
        return new TimedCacheCollector<T>(function, cachedPeriod);
    }
}
