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

package edu.sharif.ce.dml.common.parameters.logic.exception;

import edu.sharif.ce.dml.common.GeneralException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2007
 * Time: 2:40:51 PM<br/>
 * Throws when an exception occurs in setting the value in a {@link edu.sharif.ce.dml.common.parameters.logic.Parameter}
 */
public class InvalidParameterInputException extends GeneralException {
    private java.util.List<String> parameterPath = new LinkedList<String>();
    private String parameterName;
    private String message;
    private Object value;

    public InvalidParameterInputException(String message, String parameterName, Object value) {
        super("");
        this.message = message;
        this.parameterName = parameterName;
        this.value = value;
    }

    @Override
    public String getMessage() {
        java.util.List<String> toPrintParameterNames = new ArrayList<String>(parameterPath);
        Collections.reverse(toPrintParameterNames);
        StringBuffer sb = new StringBuffer();
        for (String toPrintParameterName : toPrintParameterNames) {
            sb.append(toPrintParameterName).append(" > ");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 3, sb.length());
        }
        return "Exception in parameter having name=" + parameterName + ", path=" + sb.toString() + " and value=" + value + " with message: " + message;
    }

    public void addToParameterPath(String name) {
        parameterPath.add(name);
    }

    protected InvalidParameterInputException(String message) {
        super(message);
        this.message = message;
    }

    protected InvalidParameterInputException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public InvalidParameterInputException(String message, String parameterName, Object value, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.parameterName = parameterName;
        this.value = value;
    }

}
