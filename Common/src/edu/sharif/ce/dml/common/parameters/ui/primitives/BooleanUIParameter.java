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


import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.ui.InvalidUIParameterInputException;
import edu.sharif.ce.dml.common.parameters.ui.NewUIParameter;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2007
 * Time: 2:35:57 PM<br/>
 * Boolean ui component
 */
public class BooleanUIParameter extends NewUIParameter {
    /**
     * internal checkbox.
     */
    private JCheckBox checkBox;

    public BooleanUIParameter(String name, boolean value) {
        super(name);
        JLabel l = createLabel();
        checkBox = new JCheckBox("", value);
        box.add(checkBox);
        l.setLabelFor(checkBox);
    }

    public Boolean getValue() {
        return checkBox.isSelected();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        checkBox.setEnabled(enabled);
    }

    /**
     *
     * @param v a {@link Boolean} object
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     */
    public void setValue(Object v) throws InvalidUIParameterInputException {
        try {
            checkBox.setSelected((Boolean) v);
        } catch (Exception e) {
            throw new InvalidUIParameterInputException("Boolean Type exception",e);
        }

    }

    /**
     * @return if user selected the checkbox
     */
    public String toString() {
        return Boolean.toString(checkBox.isSelected());
    }

    public void setStringValue(String S) throws InvalidUIParameterInputException {
        if (S != null) {
            String s = S.toLowerCase();
            setValue(s.equals("t") || s.equals("yes") || s.equals("y") || s.equals("true") ||
                    s.equals("1"));
            return;
        }
        setValue(false);
    }
}
