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

package edu.sharif.ce.dml.common.parameters.ui.primitives;

import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 22, 2007
 * Time: 10:15:45 PM<br>
 * a text box to enter a String value
 */
public class StringUIParameter extends NewUIParameter {
    private JTextField textField;

    public StringUIParameter(String name,String value) {
        super(name);
        JLabel l = createLabel();
        textField = new JTextField();
        textField.setText(value);
        textField.setColumns(20);
        box.add(textField);
        l.setLabelFor(textField);
    }

    public String getValue() {
        return textField.getText();
    }

    public String toString() {
        return textField.getText();
    }

    public void setValue(String s){
        textField.setText(s);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
    }
}
