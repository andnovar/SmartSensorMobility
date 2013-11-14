


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

import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 2, 2007
 * Time: 5:05:39 PM<br>
 * A selection component by which user can select a {@link edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable}
 * object and config it using a config button.
 */
public class ParameterableSelect extends ParameterSelect<Parameterable> {
    private Map<Parameterable, ParameterableConfigDialog> parameterableDialog= new HashMap<Parameterable, ParameterableConfigDialog>();
    private List<Observer> earlyObservers = new LinkedList<Observer>();
   // private final JButton configBtn;

    public ParameterableSelect(String name, java.util.List<Parameterable> parameterables, final boolean showInternalParameterables) {
        super(name,parameterables);
    }

    /**
     * o will be informed if internal objects updates using dialog
     * @param o
     */
    public void addDialogObserver(Observer o){
        earlyObservers.add(o);
        for (ParameterableConfigDialog parameterableConfigDialog : parameterableDialog.values()) {
            parameterableConfigDialog.addOKObserver(o);
        }
    }

}
