/*
 * Copyright (c) 2005-2008 by Masoud Moshref Javadi <moshref@ce.sharif.edu>, http://ce.sharif.edu/~moshref
 * The license.txt file describes the conditions under which this software may be distributed.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui;

import edu.sharif.ce.dml.common.parameters.data.GeneralDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterableConfigDialog;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;
import org.jdom.Element;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 25, 2007
 * Time: 6:25:39 PM<br>
 */
public class ConfigPanel2 extends JPanel implements Observer {
    private ParameterableConfigDialog parameterableConfigDialog;
    private Parameterable parameterable;


    public ConfigPanel2(Parameterable parameterable, boolean observeUIParameters, boolean observeParameters) {
        this.parameterable = parameterable;
        setLayout(new GridBagLayout());
        parameterableConfigDialog = new ParameterableConfigDialog(parameterable, true);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weighty = 0;
        this.add(parameterableConfigDialog.getMainPanel(), c);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        this.add(new JPanel(), c);
        if (observeUIParameters || observeParameters) {
            parameterableConfigDialog.addObserver(this);
        }
        
    }

    /**
     * loads the parameterable data from UI
     */
    public void updateValues() {
        try {
            parameterableConfigDialog.updateValues();
        } catch (InvalidParameterInputException e) {
            e.showMessage(this);
        }
    }

    public void toCompactXML(Element e) {
        GeneralDataParameter parameter = ParameterableParameter.getDataParameters(parameterable, false);
        parameter.toXML(e);
    }

    public Parameterable getParameterable() {
        return parameterable;
    }

    public void setEnabled(boolean e){
        super.setEnabled(e);
        parameterableConfigDialog.setEnabled(e);
    }


    public void update(Observable o, Object arg) {
        if (arg!=null){
            if ((Integer) arg ==ParameterableConfigDialog.UPDATE_PARAMETERS){
                updateValues();
            }else if ((Integer) arg ==ParameterableConfigDialog.UPDATE_UIPARAMETERS){
                updateUIFromParameterable();
            }
        }else {
            DevelopmentLogger.logger.fatal("update without argument from "+o);
            System.exit(1);
        }
    }

    public void updateUIFromParameterable() {
        parameterableConfigDialog.updateUI();
    }
}
