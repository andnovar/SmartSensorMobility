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

package edu.sharif.ce.dml.common.parameters.logic.parameterable;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;

import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 26, 2006
 * Time: 10:51:55 AM
 * <br/>Implementation of this class can initiate their properties using a parameter map.
 */
public interface Parameterable extends Comparable{

    /**
     * note that the class may not use all parameters. But at least now all needed parameters should be represent in
     * the <tt>parameters</tt> map. todo
     * @param parameters a {@link java.util.Map} that contains parameters for this object to fill its properties
     */
    public abstract void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException;

    /**
     * The return value may has more parameter than the {@link #setParameters(java.util.Map)} method can accept
     * @return a {@link java.util.Map} that contains all paramters of this parameterable object.
     */
    public java.util.Map<String, Parameter> getParameters();

    /**
     * use this string to generate toString value
     * @param name
     */
    public void setName(String name);



}
