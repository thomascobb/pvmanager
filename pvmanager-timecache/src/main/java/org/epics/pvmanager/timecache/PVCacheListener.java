/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.timecache;

import java.util.SortedSet;

/**
 * {@link PVCache} listener.
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public interface PVCacheListener {

	/** Informs that a new set of {@link Data} is available in storage. */
	public void newData(SortedSet<Data> datas);

}
