/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.timecache.query;

import java.util.List;

import org.epics.util.time.TimeInterval;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VType;

/**
 * Represents a chunk of data.
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public interface QueryData {

	/**
	 * The time range where the data is defined
	 */
	public TimeInterval getTimeInterval();

	/**
	 * The number of elements. <p> Both data and timestamps will have this
	 * number of elements.
	 */
	public int getCount();

	/**
	 * The time for each element.
	 */
	public List<Timestamp> getTimestamps();

	public List<VType> getData();

}
