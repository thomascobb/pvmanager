/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.jca;

import org.epics.vtype.VEnum;
import org.epics.vtype.VByteArray;
import org.epics.vtype.VStringArray;
import org.epics.vtype.VDouble;
import org.epics.vtype.VShortArray;
import org.epics.vtype.VFloatArray;
import org.epics.vtype.VInt;
import org.epics.vtype.VDoubleArray;
import org.epics.vtype.VString;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.VFloat;
import org.epics.vtype.VByte;
import org.epics.vtype.VShort;
import org.epics.vtype.VIntArray;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Channel;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.dbr.*;
import gov.aps.jca.event.*;
import java.util.Arrays;
import org.epics.pvmanager.ValueCache;
import org.epics.pvmanager.ValueCacheImpl;
import org.epics.util.array.CollectionNumbers;
import org.epics.util.time.Timestamp;
import org.epics.vtype.VTypeToString;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author carcassi
 */
public class JCAVTypeAdapterSetTest {
    
    public JCAVTypeAdapterSetTest() {
    }
    
    public static JCAConnectionPayload mockJCAConnectionPayload(final DBRType dbrType, final int count, final ConnectionState connState) {
        return mockJCAConnectionPayload("default", dbrType, count, connState);
    }
    
    public static JCAConnectionPayload mockJCAConnectionPayload(final String name, final DBRType dbrType, final int count, final ConnectionState connState) {
        return mockJCAConnectionPayload(name, dbrType, count, connState, false);
    }
    
    public static JCAConnectionPayload mockJCAConnectionPayload(final String name, final DBRType dbrType, final int count, final ConnectionState connState, final boolean longString) {
        Channel channel = mock(Channel.class);
        when(channel.getName()).thenReturn(name);
        when(channel.getFieldType()).thenReturn(dbrType);
        when(channel.getElementCount()).thenReturn(count);
        when(channel.getConnectionState()).thenReturn(connState);
        JCADataSource dataSource = mock(JCADataSource.class);
        JCAChannelHandler handler = mock(JCAChannelHandler.class);
        when(handler.getJcaDataSource()).thenReturn(dataSource);
        when(handler.isLongString()).thenReturn(longString);

        return new JCAConnectionPayload(handler, channel, null);
    }

    @Test
    public void DBRFloatToVFloat1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRFloatToVFloat;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRFloatToVFloat2() {
        ValueCache<VFloat> cache = new ValueCacheImpl<VFloat>(VFloat.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRFloatToVFloat;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRFloatToVFloat3() {
        ValueCache<String> cache = new ValueCacheImpl<String>(String.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRFloatToVFloat;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRFloatToVFloat4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRFloatToVFloat;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Float value = createDBRTimeFloat(new float[]{3.25F}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VFloat.class));
        VFloat converted = (VFloat) cache.readValue();
        assertThat(converted.getValue(), equalTo(3.25F));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo("VFloat[3.25, MINOR(HIGH_ALARM), 1970/01/15 01:56:07.000]"));
    }

    @Test
    public void DBRFloatToVFloat5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRFloatToVFloat;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Float value = createDBRTimeFloat(new float[]{3.25F}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VFloat.class));
        VFloat converted = (VFloat) cache.readValue();
        assertThat(converted.getValue(), equalTo(3.25F));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    @Test
    public void DBRDoubleToVDouble1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRDoubleToVDouble;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRDoubleToVDouble2() {
        ValueCache<VDouble> cache = new ValueCacheImpl<VDouble>(VDouble.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRDoubleToVDouble;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRDoubleToVDouble3() {
        ValueCache<String> cache = new ValueCacheImpl<String>(String.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRDoubleToVDouble;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRDoubleToVDouble4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRDoubleToVDouble;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Double value = createDBRTimeDouble(new double[]{3.25F}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VDouble.class));
        VDouble converted = (VDouble) cache.readValue();
        assertThat(converted.getValue(), equalTo(3.25));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo("VDouble[3.25, MINOR(HIGH_ALARM), 1970/01/15 01:56:07.000]"));
    }

    @Test
    public void DBRDoubleToVDouble5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRDoubleToVDouble;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Double value = createDBRTimeDouble(new double[]{3.25F}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VDouble.class));
        VDouble converted = (VDouble) cache.readValue();
        assertThat(converted.getValue(), equalTo(3.25));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    @Test
    public void DBRByteToVByte1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVByte;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Byte.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Byte.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRByteToVByte2() {
        ValueCache<VByte> cache = new ValueCacheImpl<VByte>(VByte.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVByte;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Byte.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Byte.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRByteToVByte3() {
        ValueCache<String> cache = new ValueCacheImpl<String>(String.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVByte;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Byte.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Byte.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRByteToVByte4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVByte;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Byte.TYPE, 1, ConnectionState.CONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Byte value = createDBRTimeByte(new byte[]{32}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VByte.class));
        VByte converted = (VByte) cache.readValue();
        assertThat(converted.getValue(), equalTo((byte) 32));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo("VByte[32, MINOR(HIGH_ALARM), 1970/01/15 01:56:07.000]"));
    }

    @Test
    public void DBRByteToVByte5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVByte;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Byte.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Byte value = createDBRTimeByte(new byte[]{32}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VByte.class));
        VByte converted = (VByte) cache.readValue();
        assertThat(converted.getValue(), equalTo((byte) 32));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    @Test
    public void DBRShortToVShort1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRShortToVShort;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Short.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Short.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRShortToVShort2() {
        ValueCache<VShort> cache = new ValueCacheImpl<VShort>(VShort.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRShortToVShort;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Short.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Short.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRShortToVShort3() {
        ValueCache<String> cache = new ValueCacheImpl<String>(String.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRShortToVShort;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Short.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Short.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRShortToVShort4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRShortToVShort;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Short.TYPE, 1, ConnectionState.CONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Short value = createDBRTimeShort(new short[]{32}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VShort.class));
        VShort converted = (VShort) cache.readValue();
        assertThat(converted.getValue(), equalTo((short) 32));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo("VShort[32, MINOR(HIGH_ALARM), 1970/01/15 01:56:07.000]"));
    }

    @Test
    public void DBRShortToVShort5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRShortToVShort;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Short.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Short value = createDBRTimeShort(new short[]{32}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VShort.class));
        VShort converted = (VShort) cache.readValue();
        assertThat(converted.getValue(), equalTo((short) 32));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    @Test
    public void DBRIntToVInt1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRIntToVInt;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Int.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Int.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRIntToVInt2() {
        ValueCache<VInt> cache = new ValueCacheImpl<VInt>(VInt.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRIntToVInt;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Int.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Int.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRIntToVInt3() {
        ValueCache<String> cache = new ValueCacheImpl<String>(String.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRIntToVInt;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Int.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Int.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRIntToVInt4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRIntToVInt;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Int.TYPE, 1, ConnectionState.CONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Int value = createDBRTimeInt(new int[]{32}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VInt.class));
        VInt converted = (VInt) cache.readValue();
        assertThat(converted.getValue(), equalTo(32));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo("VInt[32, MINOR(HIGH_ALARM), 1970/01/15 01:56:07.000]"));
    }

    @Test
    public void DBRIntToVInt5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRIntToVInt;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Int.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Int value = createDBRTimeInt(new int[]{32}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VInt.class));
        VInt converted = (VInt) cache.readValue();
        assertThat(converted.getValue(), equalTo(32));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    @Test
    public void DBRStringToVString1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRStringToVString;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_String.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_String.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRStringToVString2() {
        ValueCache<VString> cache = new ValueCacheImpl<VString>(VString.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRStringToVString;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_String.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_String.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRStringToVString3() {
        ValueCache<String> cache = new ValueCacheImpl<String>(String.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRStringToVString;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_String.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_String.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRStringToVString4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRStringToVString;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_String.TYPE, 1, ConnectionState.CONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_String value = createDBRTimeString(new String[]{"32"}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(null, event));
        
        assertThat(cache.readValue(), instanceOf(VString.class));
        VString converted = (VString) cache.readValue();
        assertThat(converted.getValue(), equalTo("32"));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.toString(), equalTo("VString[32, MINOR(HIGH_ALARM), 1970/01/15 01:56:07.000]"));
    }

    @Test
    public void DBRStringToVString5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRStringToVString;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_String.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_String value = createDBRTimeString(new String[]{"32"}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(null, event));
        
        assertThat(cache.readValue(), instanceOf(VString.class));
        VString converted = (VString) cache.readValue();
        assertThat(converted.getValue(), equalTo("32"));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    @Test
    public void DBRByteToVString1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVString;
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv.NAME$", DBR_Byte.TYPE, 1, ConnectionState.CONNECTED, true)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv", DBR_Byte.TYPE, 1, ConnectionState.CONNECTED, true)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv.NAME", DBR_Byte.TYPE, 1, ConnectionState.CONNECTED, false)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv$", DBR_Byte.TYPE, 1, ConnectionState.CONNECTED, false)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv.$", DBR_Byte.TYPE, 1, ConnectionState.CONNECTED, true)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv.NAME$", DBR_Byte.TYPE, 5, ConnectionState.CONNECTED, true)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv.NAME$", DBR_Double.TYPE, 1, ConnectionState.CONNECTED, true)), equalTo(0));
    }

    @Test
    public void DBRByteToVString2() {
        ValueCache<VString> cache = new ValueCacheImpl<VString>(VString.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVString;
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv.NAME$", DBR_Byte.TYPE, 1, ConnectionState.CONNECTED, true)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv.NAME$", DBR_Byte.TYPE, 5, ConnectionState.CONNECTED, true)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv.NAME$", DBR_Double.TYPE, 1, ConnectionState.CONNECTED, true)), equalTo(0));
    }

    @Test
    public void DBRByteToVString3() {
        ValueCache<VByteArray> cache = new ValueCacheImpl<VByteArray>(VByteArray.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVString;
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv.NAME$", DBR_String.TYPE, 1, ConnectionState.CONNECTED, true)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv.NAME$", DBR_String.TYPE, 5, ConnectionState.CONNECTED, true)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv.NAME$", DBR_Double.TYPE, 1, ConnectionState.CONNECTED, true)), equalTo(0));
    }

    @Test
    public void DBRByteToVString4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVString;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload("mypv.NAME$", DBR_Byte.TYPE, 20, ConnectionState.CONNECTED, true);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        byte[] data = "Testing".getBytes();
        data = Arrays.copyOf(data, data.length + 1);
        DBR_TIME_Byte value = createDBRTimeByte(data, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(null, event));
        
        assertThat(cache.readValue(), instanceOf(VString.class));
        VString converted = (VString) cache.readValue();
        assertThat(converted.getValue(), equalTo("Testing"));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.toString(), equalTo("VString[Testing, MINOR(HIGH_ALARM), 1970/01/15 01:56:07.000]"));
    }

    @Test
    public void DBRByteToVString5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVString;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload("mypv.NAME$", DBR_String.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        byte[] data = "Testing".getBytes();
        data = Arrays.copyOf(data, data.length + 1);
        DBR_TIME_Byte value = createDBRTimeByte(data, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(null, event));
        
        assertThat(cache.readValue(), instanceOf(VString.class));
        VString converted = (VString) cache.readValue();
        assertThat(converted.getValue(), equalTo("Testing"));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    @Test
    public void DBREnumToVEnum1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBREnumToVEnum;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Enum.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Enum.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBREnumToVEnum2() {
        ValueCache<VEnum> cache = new ValueCacheImpl<VEnum>(VEnum.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBREnumToVEnum;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Enum.TYPE, 1, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Enum.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBREnumToVEnum3() {
        ValueCache<String> cache = new ValueCacheImpl<String>(String.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBREnumToVEnum;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Enum.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Enum.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBREnumToVEnum4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBREnumToVEnum;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Enum.TYPE, 1, ConnectionState.CONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Enum value = createDBRTimeEnum(new short[]{2}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_LABELS_Enum meta = createMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VEnum.class));
        VEnum converted = (VEnum) cache.readValue();
        assertThat(converted.getValue(), equalTo("Two"));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.toString(), equalTo("VEnum[Two(2), MINOR(HIGH_ALARM), 1970/01/15 01:56:07.000]"));
    }

    @Test
    public void DBREnumToVEnum5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBREnumToVEnum;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Enum.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Enum value = createDBRTimeEnum(new short[]{2}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_LABELS_Enum meta = createMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VEnum.class));
        VEnum converted = (VEnum) cache.readValue();
        assertThat(converted.getValue(), equalTo("Two"));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    @Test
    public void DBRFloatToVFloatArray1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRFloatToVFloatArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 5, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRFloatToVFloatArray2() {
        ValueCache<VFloatArray> cache = new ValueCacheImpl<VFloatArray>(VFloatArray.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRFloatToVFloatArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 5, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRFloatToVFloatArray3() {
        ValueCache<String> cache = new ValueCacheImpl<String>(String.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRFloatToVFloatArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRFloatToVFloatArray4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRFloatToVFloatArray;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Float value = createDBRTimeFloat(new float[]{3.25F, 3.75F, 4.25F}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VFloatArray.class));
        VFloatArray converted = (VFloatArray) cache.readValue();
        assertThat(CollectionNumbers.doubleArrayCopyOf(converted.getData()), equalTo(new double[]{3.25, 3.75, 4.25}));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo("VFloatArray[[3.25, 3.75, 4.25], size 3, MINOR(HIGH_ALARM), 1970/01/15 01:56:07.000]"));
    }

    @Test
    public void DBRFloatToVFloatArray5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRFloatToVFloatArray;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Float value = createDBRTimeFloat(new float[]{3.25F}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VFloatArray.class));
        VFloatArray converted = (VFloatArray) cache.readValue();
        assertThat(CollectionNumbers.doubleArrayCopyOf(converted.getData()), equalTo(new double[]{3.25}));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    @Test
    public void DBRDoubleToVDoubleArray1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRDoubleToVDoubleArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 5, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRDoubleToVDoubleArray2() {
        ValueCache<VDoubleArray> cache = new ValueCacheImpl<VDoubleArray>(VDoubleArray.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRDoubleToVDoubleArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 5, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRDoubleToVDoubleArray3() {
        ValueCache<String> cache = new ValueCacheImpl<String>(String.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRDoubleToVDoubleArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Float.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRDoubleToVDoubleArray4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRDoubleToVDoubleArray;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Double value = createDBRTimeDouble(new double[]{3.25, 3.75, 4.25}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VDoubleArray.class));
        VDoubleArray converted = (VDoubleArray) cache.readValue();
        assertThat(CollectionNumbers.doubleArrayCopyOf(converted.getData()), equalTo(new double[]{3.25, 3.75, 4.25}));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo("VDoubleArray[[3.25, 3.75, 4.25], size 3, MINOR(HIGH_ALARM), 1970/01/15 01:56:07.000]"));
    }

    @Test
    public void DBRDoubleToVDoubleArray5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRDoubleToVDoubleArray;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Double value = createDBRTimeDouble(new double[]{3.25F}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VDoubleArray.class));
        VDoubleArray converted = (VDoubleArray) cache.readValue();
        assertThat(CollectionNumbers.doubleArrayCopyOf(converted.getData()), equalTo(new double[]{3.25}));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    @Test
    public void DBRByteToVByteArray1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVByteArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Byte.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Byte.TYPE, 5, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload("mypv.NAME$", DBR_Byte.TYPE, 5, ConnectionState.CONNECTED, true)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRByteToVByteArray2() {
        ValueCache<VByteArray> cache = new ValueCacheImpl<VByteArray>(VByteArray.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVByteArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Byte.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Byte.TYPE, 5, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRByteToVByteArray3() {
        ValueCache<String> cache = new ValueCacheImpl<String>(String.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVByteArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Byte.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Byte.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRByteToVByteArray4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVByteArray;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Byte.TYPE, 1, ConnectionState.CONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Byte value = createDBRTimeByte(new byte[]{3, 4, 5}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VByteArray.class));
        VByteArray converted = (VByteArray) cache.readValue();
        assertThat(CollectionNumbers.doubleArrayCopyOf(converted.getData()), equalTo(new double[]{3, 4, 5}));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo("VByteArray[[3, 4, 5], size 3, MINOR(HIGH_ALARM), 1970/01/15 01:56:07.000]"));
    }

    @Test
    public void DBRByteToVByteArray5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRByteToVByteArray;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Byte.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Byte value = createDBRTimeByte(new byte[]{3}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VByteArray.class));
        VByteArray converted = (VByteArray) cache.readValue();
        assertThat(CollectionNumbers.doubleArrayCopyOf(converted.getData()), equalTo(new double[]{3}));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    @Test
    public void DBRShortToVShortArray1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRShortToVShortArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Short.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Short.TYPE, 5, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRShortToVShortArray2() {
        ValueCache<VShortArray> cache = new ValueCacheImpl<VShortArray>(VShortArray.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRShortToVShortArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Short.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Short.TYPE, 5, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRShortToVShortArray3() {
        ValueCache<String> cache = new ValueCacheImpl<String>(String.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRShortToVShortArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Short.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Short.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRShortToVShortArray4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRShortToVShortArray;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Short.TYPE, 1, ConnectionState.CONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Short value = createDBRTimeShort(new short[]{3, 4, 5}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VShortArray.class));
        VShortArray converted = (VShortArray) cache.readValue();
        assertThat(CollectionNumbers.doubleArrayCopyOf(converted.getData()), equalTo(new double[]{3, 4, 5}));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
    }

    @Test
    public void DBRShortToVShortArray5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRShortToVShortArray;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Short.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Short value = createDBRTimeShort(new short[]{3}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VShortArray.class));
        VShortArray converted = (VShortArray) cache.readValue();
        assertThat(CollectionNumbers.doubleArrayCopyOf(converted.getData()), equalTo(new double[]{3}));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    @Test
    public void DBRIntToVIntArray1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRIntToVIntArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Int.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Int.TYPE, 5, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRIntToVIntArray2() {
        ValueCache<VIntArray> cache = new ValueCacheImpl<VIntArray>(VIntArray.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRIntToVIntArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Int.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Int.TYPE, 5, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRIntToVIntArray3() {
        ValueCache<String> cache = new ValueCacheImpl<String>(String.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRIntToVIntArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Int.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Int.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRIntToVIntArray4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRIntToVIntArray;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Int.TYPE, 1, ConnectionState.CONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Int value = createDBRTimeInt(new int[]{3, 4, 5}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VIntArray.class));
        VIntArray converted = (VIntArray) cache.readValue();
        assertThat(CollectionNumbers.doubleArrayCopyOf(converted.getData()), equalTo(new double[]{3, 4, 5}));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo("VIntArray[[3, 4, 5], size 3, MINOR(HIGH_ALARM), 1970/01/15 01:56:07.000]"));
    }

    @Test
    public void DBRIntToVIntArray5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRIntToVIntArray;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_Int.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_Int value = createDBRTimeInt(new int[]{3}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VIntArray.class));
        VIntArray converted = (VIntArray) cache.readValue();
        assertThat(CollectionNumbers.doubleArrayCopyOf(converted.getData()), equalTo(new double[]{3}));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.getUpperDisplayLimit(), equalTo(10.0));
        assertThat(converted.getUpperCtrlLimit(), equalTo(8.0));
        assertThat(converted.getUpperAlarmLimit(), equalTo(6.0));
        assertThat(converted.getUpperWarningLimit(), equalTo(4.0));
        assertThat(converted.getLowerWarningLimit(), equalTo(-4.0));
        assertThat(converted.getLowerAlarmLimit(), equalTo(-6.0));
        assertThat(converted.getLowerCtrlLimit(), equalTo(-8.0));
        assertThat(converted.getLowerDisplayLimit(), equalTo(-10.0));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    @Test
    public void DBRStringToVStringArray1() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRStringToVStringArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_String.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_String.TYPE, 5, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRStringToVStringArray2() {
        ValueCache<VStringArray> cache = new ValueCacheImpl<VStringArray>(VStringArray.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRStringToVStringArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_String.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_String.TYPE, 5, ConnectionState.CONNECTED)), equalTo(1));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRStringToVStringArray3() {
        ValueCache<String> cache = new ValueCacheImpl<String>(String.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRStringToVStringArray;
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_String.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_String.TYPE, 5, ConnectionState.CONNECTED)), equalTo(0));
        assertThat(adapter.match(cache, mockJCAConnectionPayload(DBR_Double.TYPE, 1, ConnectionState.CONNECTED)), equalTo(0));
    }

    @Test
    public void DBRStringToVStringArray4() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRStringToVStringArray;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_String.TYPE, 1, ConnectionState.CONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_String value = createDBRTimeString(new String[]{"Zero", "One", "Two"}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VStringArray.class));
        VStringArray converted = (VStringArray) cache.readValue();
        assertThat(converted.getData(), equalTo(Arrays.asList("Zero", "One", "Two")));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.MINOR));
        assertThat(converted.getAlarmName(), equalTo("HIGH_ALARM"));
        assertThat(converted.getTimestamp(), equalTo(timestamp));
        assertThat(converted.toString(), equalTo("VStringArray[[Zero, One, Two], size 3, MINOR(HIGH_ALARM), 1970/01/15 01:56:07.000]"));
    }

    @Test
    public void DBRStringToVStringArray5() {
        ValueCache<Object> cache = new ValueCacheImpl<Object>(Object.class);
        JCATypeAdapter adapter = JCAVTypeAdapterSet.DBRStringToVStringArray;
        
        JCAConnectionPayload connPayload = mockJCAConnectionPayload(DBR_String.TYPE, 1, ConnectionState.DISCONNECTED);
        Timestamp timestamp = Timestamp.of(1234567,1234);
        DBR_TIME_String value = createDBRTimeString(new String[]{"Only"}, Severity.MINOR_ALARM, Status.HIGH_ALARM, timestamp);
        DBR_CTRL_Double meta = createNumericMetadata();
        MonitorEvent event = new MonitorEvent(connPayload.getChannel(), value, CAStatus.NORMAL);
        
        adapter.updateCache(cache, connPayload, new JCAMessagePayload(meta, event));
        
        assertThat(cache.readValue(), instanceOf(VStringArray.class));
        VStringArray converted = (VStringArray) cache.readValue();
        assertThat(converted.getData(), equalTo(Arrays.asList("Only")));
        assertThat(converted.getAlarmSeverity(), equalTo(AlarmSeverity.UNDEFINED));
        assertThat(converted.getAlarmName(), equalTo("Disconnected"));
        assertThat(converted.getTimestamp(), equalTo(connPayload.getEventTime()));
        assertThat(converted.toString(), equalTo(VTypeToString.toString(converted)));
    }

    public static DBR_CTRL_Double createNumericMetadata() {
        DBR_CTRL_Double meta = new DBR_CTRL_Double();
        meta.setUpperDispLimit(10);
        meta.setUpperCtrlLimit(8);
        meta.setUpperAlarmLimit(6);
        meta.setUpperWarningLimit(4);
        meta.setLowerWarningLimit(-4);
        meta.setLowerAlarmLimit(-6);
        meta.setLowerCtrlLimit(-8);
        meta.setLowerDispLimit(-10);
        return meta;
    }

    public static DBR_LABELS_Enum createMetadata() {
        DBR_LABELS_Enum meta = new DBR_LABELS_Enum();
        meta.setLabels(new String[] {"Zero", "One", "Two", "Three"});
        return meta;
    }

    public static DBR_TIME_Float createDBRTimeFloat(float[] data, gov.aps.jca.dbr.Severity severity, gov.aps.jca.dbr.Status status, org.epics.util.time.Timestamp timestamp) {
        DBR_TIME_Float value = new DBR_TIME_Float(data);
        value.setSeverity(severity);
        value.setStatus(status);
        value.setTimeStamp(new TimeStamp(timestamp.getSec() - DataUtils.TS_EPOCH_SEC_PAST_1970, timestamp.getNanoSec()));
        return value;
    }

    public static DBR_TIME_Double createDBRTimeDouble(double[] data, gov.aps.jca.dbr.Severity severity, gov.aps.jca.dbr.Status status, org.epics.util.time.Timestamp timestamp) {
        DBR_TIME_Double value = new DBR_TIME_Double(data);
        value.setSeverity(severity);
        value.setStatus(status);
        value.setTimeStamp(new TimeStamp(timestamp.getSec() - DataUtils.TS_EPOCH_SEC_PAST_1970, timestamp.getNanoSec()));
        return value;
    }

    public static DBR_TIME_Byte createDBRTimeByte(byte[] data, gov.aps.jca.dbr.Severity severity, gov.aps.jca.dbr.Status status, org.epics.util.time.Timestamp timestamp) {
        DBR_TIME_Byte value = new DBR_TIME_Byte(data);
        value.setSeverity(severity);
        value.setStatus(status);
        value.setTimeStamp(new TimeStamp(timestamp.getSec() - DataUtils.TS_EPOCH_SEC_PAST_1970, timestamp.getNanoSec()));
        return value;
    }

    public static DBR_TIME_Short createDBRTimeShort(short[] data, gov.aps.jca.dbr.Severity severity, gov.aps.jca.dbr.Status status, org.epics.util.time.Timestamp timestamp) {
        DBR_TIME_Short value = new DBR_TIME_Short(data);
        value.setSeverity(severity);
        value.setStatus(status);
        value.setTimeStamp(new TimeStamp(timestamp.getSec() - DataUtils.TS_EPOCH_SEC_PAST_1970, timestamp.getNanoSec()));
        return value;
    }

    public static DBR_TIME_Int createDBRTimeInt(int[] data, gov.aps.jca.dbr.Severity severity, gov.aps.jca.dbr.Status status, org.epics.util.time.Timestamp timestamp) {
        DBR_TIME_Int value = new DBR_TIME_Int(data);
        value.setSeverity(severity);
        value.setStatus(status);
        value.setTimeStamp(new TimeStamp(timestamp.getSec() - DataUtils.TS_EPOCH_SEC_PAST_1970, timestamp.getNanoSec()));
        return value;
    }

    public static DBR_TIME_String createDBRTimeString(String[] data, gov.aps.jca.dbr.Severity severity, gov.aps.jca.dbr.Status status, org.epics.util.time.Timestamp timestamp) {
        DBR_TIME_String value = new DBR_TIME_String(data);
        value.setSeverity(severity);
        value.setStatus(status);
        value.setTimeStamp(new TimeStamp(timestamp.getSec() - DataUtils.TS_EPOCH_SEC_PAST_1970, timestamp.getNanoSec()));
        return value;
    }

    public static DBR_TIME_Enum createDBRTimeEnum(short[] data, gov.aps.jca.dbr.Severity severity, gov.aps.jca.dbr.Status status, org.epics.util.time.Timestamp timestamp) {
        DBR_TIME_Enum value = new DBR_TIME_Enum(data);
        value.setSeverity(severity);
        value.setStatus(status);
        value.setTimeStamp(new TimeStamp(timestamp.getSec() - DataUtils.TS_EPOCH_SEC_PAST_1970, timestamp.getNanoSec()));
        return value;
    }
}
