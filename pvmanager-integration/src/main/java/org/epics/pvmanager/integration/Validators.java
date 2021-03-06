/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.integration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author carcassi
 */
public class Validators {
    public static Validator cycleValidator(final VTypeMatchMask mask, final List<Object> matches) {
        return new Validator() {

            @Override
            public List<String> validate(List<Object> values) {
                for (int startCycle = 0; startCycle < matches.size(); startCycle++) {
                    if (matchCycle(mask, startCycle, matches, values)) {
                        return Collections.emptyList();
                    }
                }
                return Arrays.asList("no cyclical match within " + matches + " (was " + values + ")");
            }
        };
    }
    
    static boolean matchCycle(VTypeMatchMask mask, int start, List<Object> expected, List<Object> actual) {
        int currentExpected = start;
        for (int currentActual = 0; currentActual < actual.size(); currentActual++) {
            if (mask.match(expected.get(currentExpected), actual.get(currentActual)) != null) {
                return false;
            }
            currentExpected++;
            if (currentExpected == expected.size()) {
                currentExpected = 0;
            }
        }
        return true;
    }
}
