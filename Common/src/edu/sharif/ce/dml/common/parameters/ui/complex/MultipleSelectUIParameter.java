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

package edu.sharif.ce.dml.common.parameters.ui.complex;

import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.OkBtnObservable;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterableConfigDialog;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterableList;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.VariableParameterableList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 21, 2007
 * Time: 10:34:44 PM<br>
 * UI component in which user can select or create parameterables from parameterable templates.
 */
public class MultipleSelectUIParameter extends NewUIParameter implements Observer {
    /**
     * selected parameterables
     */
    private List<Parameterable> selected;
    private Map<String, Parameterable> choices;
    private JButton configBtn;
    private MultipleSelectUIParameter thisObject;
    private OkBtnObservable observable = new OkBtnObservable();
    private boolean isOKClicked = false;

    public MultipleSelectUIParameter(String name, Map<String, Parameterable> choices2,
                                     final List<Parameterable> selectedParam, final boolean variableNumber, final boolean showInternalParameterables) {
        super(name);
        thisObject = this;
        selected = selectedParam;
        this.choices = choices2;
        JLabel l = createLabel();
        configBtn = new JButton("Config");
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        box.add(Box.createHorizontalStrut(1));
        box.add(configBtn, c);
        l.setLabelFor(configBtn);
        configBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (variableNumber) {
                    VariableParameterableList variableParameterableList = new VariableParameterableList(getName(), new LinkedList<Parameterable>(choices.values()), selected,showInternalParameterables);
                    variableParameterableList.addOKBtnObserver(thisObject);
                    isOKClicked = false;
                    variableParameterableList.setVisible(true);
                    if (isOKClicked) {
                        selected = variableParameterableList.getSelectedObjects();
                        observable.change(ParameterableConfigDialog.UPDATE_PARAMETERS);
                    }
                } else {
                    //selected and choices are equal objects
                    ParameterableList parameterableList = new ParameterableList(new LinkedList<Parameterable>(choices.values()),showInternalParameterables);
                    parameterableList.setSelectedObjects(selected);
                    parameterableList.addOkBtnObserver(thisObject);
                    isOKClicked = false;
                    parameterableList.setVisible(true);
                    if (isOKClicked) {
                        selected = parameterableList.getSelectedObjects();
                        observable.change(ParameterableConfigDialog.UPDATE_PARAMETERS);
                    }
                }
            }
        });
    }

    /**
     * @return selected paramterables
     */
    public List<Parameterable> getValue() {
        return selected;
    }

    /**
     * updates choices and selected parameterables.
     *
     * @param choices2
     * @param selectedParam
     */
    public void updateValues(Map<String, Parameterable> choices2,
                             final List<Parameterable> selectedParam) {
        choices = choices2;
        this.selected = selectedParam;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        configBtn.setEnabled(enabled);
    }

    public void addObserver(Observer o) {
        super.addObserver(o);
        observable.addObserver(o);
    }

    public void update(Observable o, Object arg) {
        isOKClicked = true;
    }


}
