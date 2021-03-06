/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.sample;

import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.ScatterGraph2DRendererUpdate;
import org.epics.pvmanager.graphene.ScatterGraph2DExpression;
import static org.epics.pvmanager.formula.ExpressionLanguage.formula;
import static org.epics.pvmanager.graphene.ExpressionLanguage.*;

/**
 *
 * @author carcassi
 */
public class ScatterGraphApp extends BaseGraphApp<ScatterGraph2DRendererUpdate> {
    private InterpolationScheme interpolationScheme = InterpolationScheme.NONE;

    public ScatterGraphApp() {
        dataFormulaField.setModel(new javax.swing.DefaultComboBoxModel<String>(
                new String[] { "sim://table", 
                    "=tableOf(column(\"X\", step(0, 1)), column(\"Y\", 'sim://gaussianWaveform'))", 
                    "=tableOf(column(\"X\", 'sim://sineWaveform(1,100,100,0.01)'), column(\"Y\", 'sim://sineWaveform(10,100,100,0.01)'))",
                    "=tableOf(column(\"X\", 'sim://triangleWaveform(10,100,100,0.01)'), column(\"Y\", 'sim://triangleWaveform(20,100,100,0.01)'))" }));
    }

    public InterpolationScheme getInterpolationScheme() {
        return interpolationScheme;
    }

    public void setInterpolationScheme(InterpolationScheme interpolationScheme) {
        this.interpolationScheme = interpolationScheme;
        if (graph != null) {
            graph.update(graph.newUpdate().interpolation(interpolationScheme));
        }
    }

    @Override
    protected ScatterGraph2DExpression createExpression(String dataFormula) {
        ScatterGraph2DExpression plot = scatterGraphOf(formula(dataFormula),
                    null,
                    null,
                    null);
        plot.update(plot.newUpdate().interpolation(interpolationScheme));
        return plot;
    }

    @Override
    protected void openConfigurationDialog() {
        ScatterGraphDialog dialog = new ScatterGraphDialog(new javax.swing.JFrame(), true, this);
        dialog.setTitle("Configure...");
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        main(ScatterGraphApp.class);
    }
    
}
