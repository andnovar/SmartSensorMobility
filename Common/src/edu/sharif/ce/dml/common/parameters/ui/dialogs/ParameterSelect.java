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

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 7, 2007
 * Time: 8:28:58 PM<br>
 * shows a combo box to user to select one of them
 */
public class ParameterSelect <E> extends JPanel{
    protected JComboBox parameterCmb;
    private CmbObservable cmbObservable = new CmbObservable();

    public ParameterSelect(String name, java.util.List<E> choices) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        JLabel nameLbl = new JLabel(name + ":");
        this.add(nameLbl);
        parameterCmb = new JComboBox(choices.toArray(new Object[choices.size()]));
        parameterCmb.setSelectedIndex(0);
        parameterCmb.addItemListener(cmbObservable);
        this.add(parameterCmb);
    }

    /**
     * @param p default selected object
     */
    public void setSelectedParameter(E p){
        parameterCmb.setSelectedItem(p);
    }

    public E getSelectedParameter() {
        return(E) parameterCmb.getSelectedItem();
    }

    /**
     * @param o will be informed if user changes selected object
     */
    public void addChangeObserver(Observer o){
        cmbObservable.addObserver(o);
    }

    /**
     * internal obserbavle to inform change in selected object.
     */
    private class CmbObservable extends Observable implements ItemListener{
        protected synchronized void setChanged() {
            super.setChanged();
        }

        public void itemStateChanged(ItemEvent e) {
            setChanged();
            notifyObservers(getSelectedParameter());
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        parameterCmb.setEnabled(enabled);
    }
}
