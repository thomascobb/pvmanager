/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.epics.pvmanager;

import java.util.Arrays;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author carcassi
 */
public class NewQueueCollectorTest {
    
    public NewQueueCollectorTest() {
    }

    @Test
    public void inputOutput() throws InterruptedException {
        NewQueueCollector<Integer> collector = new NewQueueCollector<>(5);
        assertThat(collector.getValue().size(), equalTo(0));
        collector.setValue(0);
        assertThat(collector.getValue(), equalTo(Arrays.asList(0)));
        assertThat(collector.getValue().size(), equalTo(0));
        collector.setValue(1);
        collector.setValue(2);
        collector.setValue(3);
        assertThat(collector.getValue(), equalTo(Arrays.asList(1,2,3)));
        assertThat(collector.getValue().size(), equalTo(0));
        collector.setValue(1);
        collector.setValue(2);
        collector.setValue(3);
        collector.setValue(4);
        collector.setValue(5);
        collector.setValue(6);
        assertThat(collector.getValue(), equalTo(Arrays.asList(2,3,4,5,6)));
        assertThat(collector.getValue().size(), equalTo(0));
    }
}
