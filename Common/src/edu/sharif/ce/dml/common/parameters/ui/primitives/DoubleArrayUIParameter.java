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
import javax.swing.text.DefaultFormatterFactory;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2007
 * Time: 2:35:36 PM<br/>
 * A UI component that accepts an array of double values. User enters the value as a String but after change of
 * focus a validation procedure runs and manages to extract double values from the String.
 */
public class DoubleArrayUIParameter extends NewUIParameter {

    private final JFormattedTextField textField;

    public DoubleArrayUIParameter(String name, Object defualtValue) {
        super(name);
        JLabel l = createLabel();
        JFormattedTextField.AbstractFormatter displayFormatter = new JFormattedTextField.AbstractFormatter() {
            public Object stringToValue(String text) {
                text = text.replaceAll("[^\\d\\." + ARRAY_SEPARATOR + "-]", "");
                List<String> parsedText = new LinkedList<String>(Arrays.asList(text.split(ARRAY_SEPARATOR)));
                for (java.util.Iterator it = parsedText.iterator(); it.hasNext();) {
                    String p = (String) it.next();
                    if (p.length() == 0) {
                        it.remove();
                    }
                }
                if (parsedText.size() == 0) {
                    return new Double[]{0d};
                }
                double[] valueObject = new double[parsedText.size()];
                for (int i = 0; i < parsedText.size(); i++) {
                    valueObject[i] = new Double(parsedText.get(i));
                }
                return valueObject;
            }

            public String valueToString(Object valueX) {
                if (valueX == null) {
                    return "";
                }
                StringBuffer sb = new StringBuffer();
                for (double aDouble : (double[]) valueX) {
                    sb.append(aDouble).append(ARRAY_SEPARATOR);
                }
                return sb.toString();
            }
        };
        textField = new JFormattedTextField(new DefaultFormatterFactory(displayFormatter));
        textField.setInputVerifier(new InputVerifier() {
            public boolean verify(JComponent input) {
                if (input instanceof JFormattedTextField) {
                    JFormattedTextField ftf = (JFormattedTextField) input;
                    JFormattedTextField.AbstractFormatter formatter = ftf.getFormatter();
                    if (formatter != null) {
                        String text = ftf.getText();
                        try {
                            formatter.stringToValue(text);
                            return true;
                        } catch (ParseException pe) {
                            return false;
                        }
                    }
                }
                return true;
            }

            public boolean shouldYieldFocus(JComponent input) {
                return verify(input);
            }
        });

        box.add(textField);
        l.setLabelFor(textField);
        if (defualtValue != null) {
            textField.setValue(defualtValue);
        }
    }

    /**
     * @return double[] object
     */
    public Object getValue() {
        return textField.getValue();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
    }

    /**
     * @param v a String of double values seperated by {@link #ARRAY_SEPARATOR}
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     *
     */
    public void setValue(Object v) throws InvalidUIParameterInputException {
        try {
            textField.setValue(v);
        } catch (Exception e) {
            throw new InvalidUIParameterInputException("Array Type exception",e);
        }
    }

    public String toString() {
        try {
            return textField.getFormatter().valueToString(getValue());
        } catch (ParseException e) {
            DevelopmentLogger.logger.warn(e.getMessage(),e);
        }
        return "";
    }

    public void setStringValue(String s) throws InvalidUIParameterInputException {
        try {
            setValue(textField.getFormatter().stringToValue(s));
        } catch (ParseException e) {
            e.printStackTrace();
            throw new InvalidUIParameterInputException("Unable to parse the value "+s +" in "+getName());
        }
    }

}
