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

package edu.sharif.ce.dml.common.parameters.data;

import edu.sharif.ce.dml.common.parameters.data.ParameterableConfigFileException;
import org.jdom.Element;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2007
 * Time: 4:47:02 PM <br/>
 * represents a string parameter.  Assumes xml structure as:<br/>
 * &lt;!ATTLIST parameter<br/>
 *               name CDATA #REQUIRED<br/>
 *               value CDATA #REQUIRED<br/>
 *               class CDATA #IMPLIED><br/>
 */
public class StringDataParameter extends GeneralDataParameter {
    protected String value;

    public StringDataParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public StringDataParameter() {
    }

    public String getValue() {
        return value;
    }

    @Deprecated
    public static void loadParameters(Map<String, StringDataParameter> parametersWrapper, Element parent) {
        //load all specific parameter for parent element
        for (Object o1 : parent.getChildren()) {
            Element parameterElement = (Element) o1;
            if (parameterElement.getName().equals("parameter")) {
                parametersWrapper.put(parameterElement.getAttributeValue("name"), new StringDataParameter(parameterElement.getAttributeValue("name"),
                        parameterElement.getAttributeValue("value")));
            }
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return name + "=" + value;
    }

    public static List<StringDataParameter> createParameters(Map<String, String> data) {
        List<StringDataParameter> returnVlaue = new LinkedList<StringDataParameter>();
        for (String k : data.keySet()) {
            returnVlaue.add(new StringDataParameter(k, data.get(k)));
        }
        return returnVlaue;
    }

    public void loadInitData(Element parameterElement, ParameterableDocument document) throws ParameterableConfigFileException {
        super.loadInitData(parameterElement, document);
        value = parameterElement.getAttributeValue("value");
    }

    public List<StringDataParameter> flat() {
        return Arrays.asList(this);
    }

    public void toXML(Element e) {
        Element e2 = new Element("parameter");
        e.addContent(e2);
        e2.setAttribute("name", getName());
        e2.setAttribute("value", getValue());
    }
}
