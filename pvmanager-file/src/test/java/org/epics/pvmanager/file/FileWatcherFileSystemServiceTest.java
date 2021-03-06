/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import org.epics.pvmanager.test.CountDownPVReaderListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.epics.pvmanager.PVReaderEvent;
import static org.junit.Assert.*;
import org.junit.*;
import org.epics.util.time.TimeDuration;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author carcassi
 */
public class FileWatcherFileSystemServiceTest {
    
    @Before
    public void setUp() {
        exec = Executors.newSingleThreadScheduledExecutor();
    }

    @After
    public void tearDown() {
        exec.shutdownNow();
    }
    
    private ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    private CountDownLatch latch;
    private Runnable task = new Runnable() {

        @Override
        public void run() {
            latch.countDown();
        }
    };
    
    @Test
    public void udpateFile() throws Exception {
        CountDownPVReaderListener listener = new CountDownPVReaderListener(1, PVReaderEvent.VALUE_MASK);
        File filename = File.createTempFile("file.", ".csv");
        PrintWriter writer = new PrintWriter(filename);
        writer.println("Name,Value");
        writer.println("Andrew,34");
        writer.println("Bob,12");
        writer.close();
        
        latch = new CountDownLatch(1);
        FileWatcherFileSystemService service = new FileWatcherFileSystemService(exec, TimeDuration.ofMillis(100));
        service.addWatcher(filename, task);
        
        latch.await(1000, TimeUnit.MILLISECONDS);
        assertThat(latch.getCount(), equalTo(1L));
        
        writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
        writer.println("Charlie,71");
        writer.close();
        
        latch.await(1000, TimeUnit.MILLISECONDS);
        assertThat(latch.getCount(), equalTo(0L));
    }

}