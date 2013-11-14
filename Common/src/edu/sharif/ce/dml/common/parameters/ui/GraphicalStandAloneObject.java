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

package edu.sharif.ce.dml.common.parameters.ui;

import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 16, 2007
 * Time: 10:06:09 PM<br>
 * Each object which want to create and manage its UI should implement this interface.
 */
public interface GraphicalStandAloneObject {

    /**
     * @return corresponding uiparameter
     * @param showInternalParameterables
     */
    public NewUIParameter getUIParameter(boolean showInternalParameterables);

    /**
     * sets user entered parameter values. updates this object.
     * @param uiParameter
     * @throws edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException
     */
    public void setUIParameterValue(NewUIParameter uiParameter) throws InvalidParameterInputException;


    public void updateUIParameter(NewUIParameter uiParameter) throws InvalidParameterInputException;
}
