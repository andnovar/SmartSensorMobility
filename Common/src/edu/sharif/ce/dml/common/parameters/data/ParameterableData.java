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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 29, 2007
 * Time: 4:25:40 PM<br/>
 * contains settings for a configurable object.<br/>
 * &lt;!ELEMENT parameterable (parameter|composite|parameterable)*><br/>
 * &lt;!ATTLIST parameterable<br/>
				name CDATA #REQUIRED<br/>
                class CDATA #REQUIRED><br/>
 */
public class ParameterableData extends GeneralDataParameter {

    private Class
//            <? extends Parameterable>
            parameterableClass;
    /**
     * internal parameters
     */
    protected Map<String, GeneralDataParameter> parameters = new HashMap<String, GeneralDataParameter>();

    /**
     * @return configurable object class.
     */
    public Class
//            <? extends Parameterable>
    getParameterableClass() {
        return parameterableClass;
    }

    public void setParameterableClass(Class
//                    <? extends Parameterable>
            parameterableClass) {
        this.parameterableClass = parameterableClass;
    }

    public Map<String, GeneralDataParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, GeneralDataParameter> parameters) {
        this.parameters = parameters;
    }

    public void loadInitData(Element parameterElement, ParameterableDocument document) throws ParameterableConfigFileException {
        super.loadInitData(parameterElement, document);
        try {
            //load information from each parameterable tag in xml file
            setParameterableClass(
//                    (Class<? extends Parameterable>)
                    Class.forName(parameterElement.getAttributeValue("class")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ParameterableConfigFileException("error in loading parameterable with class " + parameterElement.getAttributeValue("class")+
                    " with name="+getName());
        }
        for (Object element : parameterElement.getChildren()) {
            Element current = (Element) element;
            String tagName = current.getName();
            GeneralDataParameter dataParameter = null;
            if (tagName.equals("composite")) {
                //todo it should be changed to change loader by setting a loader class
                dataParameter = new CompositeDataParameter();
            } else if (tagName.equals("parameterable")) {
                dataParameter = new ParameterableData();
            } else if (tagName.equals("parameter")) {
                dataParameter = new StringDataParameter();
            } else {
                assert false;
            }
            dataParameter.loadInitData(current, document);
            //todo
            parameters.put(dataParameter.getName(), dataParameter);
        }
    }

    public void toXML(Element e) {
        Element e2 = new Element("parameterable");
        e.addContent(e2);
        e2.setAttribute("name", getName());
        e2.setAttribute("class", parameterableClass.getName());
        for (GeneralDataParameter generalDataParameter : parameters.values()) {
            generalDataParameter.toXML(e2);
        }
    }

    public List<StringDataParameter> flat() {
        List<StringDataParameter> returnValue = new LinkedList<StringDataParameter>();
        for (GeneralDataParameter generalDataParameter : parameters.values()) {
            returnValue.addAll(generalDataParameter.flat());
        }
        return returnValue;
    }


    public void setDefaultData(Map<String, GeneralDataParameter> defaultParameters) {
        for (String keyS : defaultParameters.keySet()) {
            GeneralDataParameter v = this.parameters.get(keyS);
            if (v == null) {
                this.parameters.put(keyS, defaultParameters.get(keyS));
            }
        }
    }
}
