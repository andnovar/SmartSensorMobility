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

package edu.sharif.ce.dml.common.parameters.ui.dialogs;


import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.ui.GraphicalStandAloneObject;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.complex.GeneralUIParameter;
import edu.sharif.ce.dml.common.ui.forms.DialogTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 30, 2007
 * Time: 6:06:32 PM
 * <br/> a general class which gets a {@link edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable} and used it for making a dialog.
 * users can use its mainPanel to import the generated configpanel in another ui.
 */
public class ParameterableConfigDialog extends DialogTemplate {
    //a change in up parameters for updating parameters
    public static final int UPDATE_PARAMETERS = 0;
    //a chnage in parameters for updating ui parameters
    public static final int UPDATE_UIPARAMETERS = 1;

    /**
     * The Masoud UIParameter object
     */
    private final NewUIParameter uiParameter;
    /**
     * The panel that has configurations without ok and cancel buttons.
     */
    private JPanel mainPanel = new JPanel(new GridBagLayout());
    /**
     * embedded parameterable reference.
     */
    private final Parameterable parameterable;

    private final OkBtnObservable okObservable;

    public ParameterableConfigDialog(final Parameterable parameterable, boolean showInternalParameterables) throws HeadlessException {
        super(null, parameterable.toString(), true);
        this.parameterable = parameterable;
        okObservable = new OkBtnObservable();
        JPanel rootPanel = new JPanel(new BorderLayout());
        this.setContentPane(rootPanel);
        {
            if (parameterable instanceof GraphicalStandAloneObject) {
                uiParameter = ((GraphicalStandAloneObject) parameterable).getUIParameter(true);
            } else {
                uiParameter = new GeneralUIParameter(parameterable.toString(), parameterable.getParameters().values(), showInternalParameterables);
            }
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 1;
            c.fill = GridBagConstraints.BOTH;
            mainPanel.add(uiParameter, c);
            rootPanel.add(mainPanel, BorderLayout.NORTH);
        }
        Box v = Box.createVerticalBox();
        rootPanel.add(v, BorderLayout.SOUTH);
        v.add(new JSeparator());
        Box h = Box.createHorizontalBox();
        v.add(h);
        h.add(Box.createHorizontalGlue());
        JButton cancelBtn = new JButton("Cancel");
        h.add(cancelBtn);
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        h.add(Box.createHorizontalStrut(10));
        JButton okBtn = new JButton("OK");
        h.add(okBtn);
        okBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    updateValues();
                    okObservable.change(parameterable);
                    dispose();
                } catch (InvalidParameterInputException e1) {
                    e1.showMessage(thisDialog);
                }
            }
        });

        this.pack();
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    /**
     * users can use its mainPanel to import the generated configpanel in another ui.
     *
     * @return
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * updates the parameters of embedded parametrable from UI values. If users used
     * {@link #getMainPanel()} they can use this method to update the parameterable configs.
     */
    public void updateValues() throws InvalidParameterInputException {
        if (parameterable instanceof GraphicalStandAloneObject) {
            ((GraphicalStandAloneObject) parameterable).setUIParameterValue(uiParameter);
        } else {
            ((GeneralUIParameter) uiParameter).updateParameter(parameterable);
        }
    }

    public void addOKObserver(Observer o) {
        okObservable.addObserver(o);
    }

    /**
     * adds an observer to the observers of UIParameter.
     *
     * @param o
     */
    public void addObserver(Observer o) {
        uiParameter.addObserver(o);
    }

    public void setEnabled(boolean b) {
        super.setEnabled(b);
        uiParameter.setEnabled(b);
    }

    public void updateUI() {
        try {
            if (parameterable instanceof GraphicalStandAloneObject) {
                ((GraphicalStandAloneObject) parameterable).updateUIParameter(uiParameter);
            } else {
                ((GeneralUIParameter) uiParameter).updateUIParameter(parameterable);
            }
        } catch (InvalidParameterInputException e) {
            e.showMessage(this);
        }
    }
}
