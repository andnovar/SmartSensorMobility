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

import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.OkBtnObservable;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterableConfigDialog;
import edu.sharif.ce.dml.common.parameters.ui.dialogs.ParameterableSelect;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 16, 2007
 * Time: 1:20:54 PM <br>
 * UI implementation for {@link edu.sharif.ce.dml.common.parameters.logic.complex.SelectOneParameterable}
 */
public class SelectOneUIParameter extends NewUIParameter implements Observer {
    /**
     * UI combobox
     */
    private ParameterableSelect parameterableSelect;

    /////////////// snhow configs section
    /**
     * a map that saves reference to each parameterable config panel if its configs should be shown.
     */
    private Map<Parameterable, ParameterableConfigDialog> parameterablePanel;
    /**
     * container panel
     */
    private JPanel container;
    /**
     * last selected parameterable. its to manage multi call of itemchangelistener.
     */
    private Parameterable lastSelected;

    /**
     * if embedded parameterable configs should be shown.
     */
    private boolean showConfigs;
    private static GridBagConstraints c3;

    /**
     * the object who should be informed if the selected parameterable configs changed.<br/> it used to set for each
     * panel that will be created.
     */
    private Observer changeObserver;

    private SelectOneObservable observable = new SelectOneObservable();
    private boolean showInternalParameterables;

    static {
        c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.BOTH;
        c3.anchor = GridBagConstraints.FIRST_LINE_START;
        c3.weighty = 1;
        c3.weightx = 1;
    }

    /**
     * @param name
     * @param choices     all choices
     * @param selected    selected parameterable
     * @param showConfigs
     */
    public SelectOneUIParameter(String name, List<Parameterable> choices, Parameterable selected, boolean showConfigs,
                                boolean showInternalParameterables) {
        super(name);
        this.showConfigs = showConfigs;
        this.showInternalParameterables=showInternalParameterables;
        parameterableSelect = new ParameterableSelect(name, choices,showInternalParameterables);
        JPanel p = new JPanel(new GridBagLayout());
        box.add(p);
        ///
        GridBagConstraints c2 = new GridBagConstraints();
        c2.anchor = GridBagConstraints.FIRST_LINE_START;
        c2.fill = GridBagConstraints.BOTH;
        c2.weightx = 1;
        c2.gridx = 0;
        ///
        p.add(parameterableSelect, c2);
        if (showConfigs) {
            parameterablePanel = new HashMap<Parameterable, ParameterableConfigDialog>();
            container = new JPanel(new GridBagLayout());
//            p.add(Box.createVerticalStrut(10));
            p.add(container, c2);
            parameterableSelect.addChangeObserver(this);
            parameterableSelect.addDialogObserver(this);
            updateConfigPanel(selected);
        }
        parameterableSelect.setSelectedParameter(selected);
    }


    public Object getValue() {
        Parameterable value = parameterableSelect.getSelectedParameter();
        if (showConfigs) {
            ParameterableConfigDialog configPanel = parameterablePanel.get(value);
            if (configPanel != null) {
                try {
                    configPanel.updateValues();//todo it should not be here
                } catch (InvalidParameterInputException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    /**
     * selects the v parameterable in UI.
     *
     * @param v a parameterable object which is among choices
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     *
     */
    public void setValue(Object v) throws InvalidParameterInputException {
        parameterableSelect.setSelectedParameter((Parameterable) v);
    }

    /**
     * @return selected object toString()
     */
    public String toString() {
        return parameterableSelect.getSelectedParameter().toString();
    }

    public void update(Observable o, Object arg) {
        Parameterable selectedParameterable = (Parameterable) arg;
        if (o instanceof OkBtnObservable) {
            //should update my panels because user used config dialog
            updateParameterablePanels();
        } else {
            //should update parameters because some ui components updated
            updateConfigPanel(selectedParameterable);
            observable.change(ParameterableConfigDialog.UPDATE_PARAMETERS);
        }
    }

    public void updateParameterablePanels() {
        if (showConfigs)
            for (ParameterableConfigDialog parameterableConfigDialog : parameterablePanel.values()) {
                parameterableConfigDialog.updateUI();
            }
    }

    /**
     * updates ui components according to <tt> selectedParameterable</tt>
     *
     * @param selectedParameterable
     */
    private void updateConfigPanel(Parameterable selectedParameterable) {
        if (lastSelected != selectedParameterable) {
            lastSelected = selectedParameterable;
            ParameterableConfigDialog configPanel = parameterablePanel.get(selectedParameterable);
            if (configPanel == null) {
                configPanel = new ParameterableConfigDialog(selectedParameterable, showInternalParameterables);
                parameterablePanel.put(selectedParameterable, configPanel);
               /* if (changeObserver != null) {
                    configPanel.addObserver(changeObserver);
                }*/
            }
            container.removeAll();

            container.add(configPanel.getMainPanel(), c3);
            //container.setBorder(BorderFactory.createTitledBorder(lastSelected.toString()));
            container.invalidate();
            Object w = getTopLevelAncestor();
            if (w != null) {
                ((Window) w).pack();
            }
        }
    }

    public void addObserver(Observer o) {
        observable.addObserver(o);
        changeObserver = o;
        if (parameterablePanel != null) {
            for (ParameterableConfigDialog parameterableConfigDialog : parameterablePanel.values()) {
                parameterableConfigDialog.addObserver(o);
            }
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        parameterableSelect.setEnabled(enabled);
        if (parameterablePanel != null) {
            for (ParameterableConfigDialog parameterableConfigDialog : parameterablePanel.values()) {
                parameterableConfigDialog.setEnabled(enabled);
            }
        }
    }

    private static class SelectOneObservable extends Observable {
        protected synchronized void setChanged() {
            super.setChanged();
        }
        public void change(Object arg){
            setChanged();
            notifyObservers(arg);
        }
    }
}
