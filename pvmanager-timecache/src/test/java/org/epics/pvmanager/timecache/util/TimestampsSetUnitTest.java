/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.timecache.util;

import java.text.ParseException;

import org.epics.pvmanager.timecache.util.IntervalsList;
import org.epics.pvmanager.timecache.util.TimestampsSet;
import org.epics.util.time.TimeDuration;
import org.epics.util.time.Timestamp;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link TimestampsSet}: transform a list of {@link Timestamp} to an
 * {@link IntervalsList}.
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class TimestampsSetUnitTest {

	/**
	 * Test that {@link IntervalsList} result of {@link TimestampsSet}
	 * transformation algorithm contains the specified intervals.
	 */
	@Test
	public void testToIntervalsList() {
		try {
			TimestampsSet set = new TimestampsSet();
			set.setTolerance(TimeDuration.ofMinutes(5));

			set.add(null);
			IntervalsList result = set.toIntervalsList();
			Assert.assertEquals(0, result.getSize());

			set.add(UnitTestUtils.timestampOf("16:00"));
			result = set.toIntervalsList();
			Assert.assertEquals(1, result.getSize());
			Assert.assertTrue(result.contains(UnitTestUtils.timeIntervalOf("16:00", "16:05")));

			set.add(UnitTestUtils.timestampOf("16:01"));
			set.add(UnitTestUtils.timestampOf("16:05"));
			set.add(UnitTestUtils.timestampOf("16:10"));
			set.add(UnitTestUtils.timestampOf("16:30"));
			set.add(UnitTestUtils.timestampOf("16:35"));
			result = set.toIntervalsList();
			Assert.assertEquals(2, result.getSize());
			Assert.assertTrue(result.contains(UnitTestUtils.timeIntervalOf("16:00", "16:10")));
			Assert.assertTrue(result.contains(UnitTestUtils.timeIntervalOf("16:30", "16:35")));

		} catch (ParseException e) {
			Assert.fail(e.getMessage());
		}
	}

}
