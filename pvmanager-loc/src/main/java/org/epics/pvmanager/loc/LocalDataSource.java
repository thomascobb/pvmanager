/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.loc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.ChannelReadRecipe;
import org.epics.pvmanager.ChannelWriteRecipe;
import org.epics.pvmanager.DataSource;
import org.epics.pvmanager.ReadRecipe;
import org.epics.pvmanager.WriteRecipe;
import org.epics.pvmanager.vtype.DataTypeSupport;
import org.epics.pvmanager.util.FunctionParser;
import org.epics.util.array.ArrayDouble;

/**
 * Data source for locally written data. Each instance of this
 * data source will have its own separate channels and values.
 *
 * @author carcassi
 */
public final class LocalDataSource extends DataSource {

    static {
        // Install type support for the types it generates.
        DataTypeSupport.install();
    }

    /**
     * Creates a new data source.
     */
    public LocalDataSource() {
        super(true);
    }

    private final String CHANNEL_SYNTAX_ERROR_MESSAGE = 
            "Syntax for local channel must be either name, name(Double) or name(String) (e.g \"foo\", \"foo(2.0)\" or \"foo(\"bar\")";
    
    @Override
    protected ChannelHandler createChannel(String channelName) {
        // Parse the channel name
        List<Object> parsedTokens = parseName(channelName);
        
        LocalChannelHandler channel = new LocalChannelHandler(parsedTokens.get(0).toString());
        return channel;
    }
    
    private List<Object> parseName(String channelName) {
        return FunctionParser.parseFunctionWithScalarOrArrayArguments(".+", channelName, CHANNEL_SYNTAX_ERROR_MESSAGE);
    }

    @Override
    protected String channelHandlerLookupName(String channelName) {
        List<Object> parsedTokens = parseName(channelName);
        return parsedTokens.get(0).toString();
    }
    
    private void initialize(String channelName) {
        List<Object> parsedTokens = parseName(channelName);

        if (parsedTokens.size() > 1) {
            LocalChannelHandler channel = (LocalChannelHandler) getChannels().get(channelHandlerLookupName(channelName));
            if (channel != null) {
                channel.setInitialValue(parsedTokens.get(1));
            }
        }
    }

    @Override
    public void connectRead(ReadRecipe readRecipe) {
        super.connectRead(readRecipe);
        
        // Initialize all values
        for (ChannelReadRecipe channelReadRecipe : readRecipe.getChannelReadRecipes()) {
            initialize(channelReadRecipe.getChannelName());
        }
    }

    @Override
    public void connectWrite(WriteRecipe writeRecipe) {
        super.connectWrite(writeRecipe);
        
        // Initialize all values
        for (ChannelWriteRecipe channelWriteRecipe : writeRecipe.getChannelWriteRecipes()) {
            initialize(channelWriteRecipe.getChannelName());
        }
    }

}
