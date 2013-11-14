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
import org.jdom.Attribute;
import org.jdom.Element;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 29, 2007
 * Time: 4:25:40 PM<br/>
 * Data object that has some internal {@link edu.sharif.ce.dml.common.parameters.data.GeneralDataParameter}s and
 * has the lazy load ability. Also some default parameter may be defined for all internal child objects<br/>
 * Lazy loading is useful for reflective referencing.<br/><code>
 * Assumes xml structure as:<br/>
 * &lt;!ELEMENT composite (default?,(parameterable*|parameter*|composite*))><br/>
 * &lt;!ATTLIST composite<br/>
 * name CDATA #REQUIRED<br/>
 * lazyReference CDATA #IMPLIED<br/>
 * lazyLoadableID CDATA #IMPLIED&gt;<br/></code>
 * <tt>lazyLoadbleID</tt> is to define a referencable object, and
 * <tt>lazyReference</tt> is to reference a referenable object
 */
public class CompositeDataParameter extends GeneralDataParameter {
    /**
     * reference composite object
     */
    private CompositeDataParameter lazyComposite;
    /**
     * internal {@link edu.sharif.ce.dml.common.parameters.data.GeneralDataParameter} objects
     */
    protected Map<String, GeneralDataParameter> parameters = new HashMap<String, GeneralDataParameter>();


    /**
     * @return internal {@link edu.sharif.ce.dml.common.parameters.data.GeneralDataParameter} objects.
     */
    public Map<String, GeneralDataParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, GeneralDataParameter> parameters) {
        this.parameters = parameters;
    }

    public CompositeDataParameter() {
    }


    public static Element createRootElement() {
        Element rootElement = new Element("rootcomposite");
        rootElement.setAttribute("name", "");
        return rootElement;
    }

    public void loadInitData(Element parameterElement, ParameterableDocument document) throws ParameterableConfigFileException {
        super.loadInitData(parameterElement, document);
        /////////import part
        if (parameterElement.getAttribute("import") != null) {
            //load the external xml file
            File file = new File(parameterElement.getAttributeValue("import"));
            if (!file.isAbsolute()) {
                file = new File(getDocument().getCurrentFile().getParentFile().getPath() + "/" + parameterElement.getAttributeValue("import"));
            }
            //load the file
            ParameterableConfigLoader pcl = ParameterableConfigLoader.load(file);
            //set root element as parameterElement and copy it
            CompositeDataParameter loaddedCompositeDataParameter = pcl.getRootComposite();
            copyComposite(loaddedCompositeDataParameter);
            return;

        }
        /////////register referencable objects
        if (parameterElement.getAttribute("lazyLoadableID") != null) {
            String id = parameterElement.getAttribute("lazyLoadableID").getValue();
            this.document.getTagNameElement().put(id, this);
            Set<CompositeDataParameter> waitings = this.document.getTagNameWaitingSet().get(id);
            if (waitings != null) {
                //someone waits for me
                for (CompositeDataParameter compositeDataParameter : waitings) {
                    compositeDataParameter.lazyComposite = this;
                }
                this.document.getTagNameWaitingSet().remove(id);
            }
        }
        /////// load references
        Attribute lazyReference = parameterElement.getAttribute("lazyReference");
        if (lazyReference != null) {
            CompositeDataParameter lastLoaddedLazyComposite = this.document.getTagNameElement().get(lazyReference.getValue());
            if (lastLoaddedLazyComposite != null) {
                lazyComposite = lastLoaddedLazyComposite;
            } else {
                Set<CompositeDataParameter> waitings = this.document.getTagNameWaitingSet().get(lazyReference.getValue());
                if (waitings == null) {
                    waitings = new HashSet<CompositeDataParameter>();
                }
                waitings.add(this);
            }
        }
        Map<String, GeneralDataParameter> defaulParameters = new HashMap<String, GeneralDataParameter>();
        for (Object element : parameterElement.getChildren()) {
            Element current = (Element) element;
            String name = current.getName();
            if (name.equals("default")) {
                //////// load defaults
                for (Object element2 : current.getChildren()) {
                    Element current2 = (Element) element2;
                    String name2 = current2.getName();
                    GeneralDataParameter dataParameter;
                    if (name2.equals("composite")) {
                        //todo it should be changed to change loader by setting a loader class
                        dataParameter = new CompositeDataParameter();
                    } else if (name2.equals("parameterable")) {
                        dataParameter = new ParameterableData();
                    } else if (name2.equals("parameter")) {
                        dataParameter = new StringDataParameter();
                    } else {
                        throw new ParameterableConfigFileException("Not defined tag: " + name);
                    }
                    dataParameter.loadInitData(current2, document);
                    defaulParameters.put(dataParameter.getName(), dataParameter);
                }
            } else {
                /////load internal child objects
                GeneralDataParameter dataParameter;
                if (name.equals("composite")) {
                    //todo it should be changed to change loader by setting a loader class
                    dataParameter = new CompositeDataParameter();
                } else if (name.equals("parameterable")) {
                    dataParameter = new ParameterableData();
                    ((ParameterableData) dataParameter).setDefaultData(defaulParameters);
                } else if (name.equals("parameter")) {
                    dataParameter = new StringDataParameter();
                } else {
                    throw new ParameterableConfigFileException("Not defined tag: " + name);
                }
                dataParameter.loadInitData(current, document);
                //todo
                parameters.put(dataParameter.getName(), dataParameter);
            }
        }
    }

    /**
     * copies the data of <tt>loaddedCompositeDataParameter</tt> to <tt>this</tt> object
     *
     * @param loaddedCompositeDataParameter
     */
    private void copyComposite(CompositeDataParameter loaddedCompositeDataParameter) {
        this.lazyComposite = loaddedCompositeDataParameter.lazyComposite;
        this.parameters = loaddedCompositeDataParameter.parameters;
    }


    public List<StringDataParameter> flat() {
        List<StringDataParameter> returnValue = new LinkedList<StringDataParameter>();
        for (GeneralDataParameter generalDataParameter : parameters.values()) {
            returnValue.addAll(generalDataParameter.flat());
        }
        return returnValue;
    }

    public void toXML(Element e) {
        Element e2 = new Element("composite");
        e.addContent(e2);
        e2.setAttribute("name", getName());
        for (GeneralDataParameter generalDataParameter : parameters.values()) {
            generalDataParameter.toXML(e2);
        }
    }

    public CompositeDataParameter getLazyComposite() {
        return lazyComposite;
    }

    public void setLazyComposite(CompositeDataParameter lazyComposite) {
        this.lazyComposite = lazyComposite;
    }
}
