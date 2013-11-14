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

package edu.sharif.ce.dml.common.data.trace.filenamegenerator;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.parameters.logic.primitives.BooleanParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.StringParameter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Jun 11, 2008
 * Time: 2:15:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleFileName extends ParameterableImplement implements FileNameGenerator{
    private String seed;
    private boolean numberIterate;

    private String prefix, postfix;
    private int currentIterate=1;

    public SimpleFileName() {
        super();
        prefix="";
        postfix="";
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        this.seed = ((StringParameter) parameters.get("seed")).getValue();
        this.numberIterate = ((BooleanParameter)parameters.get("numberiterate")).getValue();
    }

    public Map<String, Parameter> getParameters() {
        Map<String,Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("seed",new StringParameter("seed",seed));
        parameters.put("numberiterate", new BooleanParameter("numberiterate",numberIterate));
        return parameters;
    }

    public String getNextFileName() {
        return prefix+seed+(numberIterate?currentIterate++:"")+postfix;
    }

    public void setPrefix(String s) {
        prefix =s;
    }

    public void setPostfix(String s) {
        postfix = s;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPostfix() {
        return postfix;
    }
}
