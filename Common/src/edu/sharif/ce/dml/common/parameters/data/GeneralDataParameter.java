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

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 15, 2007
 * Time: 8:05:07 PM<br/>
 * Data entities which has a unique name.
 */
public abstract class GeneralDataParameter {
    protected String name="";


    protected ParameterableDocument document;

    /**
     * loads and constructs this object and subobjects from an XML tree.
     * @param parameterElement
     * @param document
     * @throws ParameterableConfigFileException
     */
    public void loadInitData(Element parameterElement, ParameterableDocument document) throws ParameterableConfigFileException{
        name = parameterElement.getAttributeValue("name");
        this.document=document;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * compares according to <tt>name</tt>
     *
     * @param o
     * @return
     */
    public int compareTo(Object o) {
        return this.name.compareTo(((StringDataParameter) o).getName());
    }

    public boolean equals(Object obj) {
        return this.name.equals(((StringDataParameter) obj).getName());
    }

    /**
     * @return <tt>name</tt> hashcode
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * todo: parameters with similar name is confusing in flattern version.
     * @return flatten version of the tree that this node is Masoud of it.
     */
    public abstract List<StringDataParameter> flat();

    /**
     * converts this tree to XML tree as a child of <tt>e</tt> element.
     * @param e
     */
    public abstract void toXML(Element e);

    public ParameterableDocument getDocument() {
        return document;
    }
}
