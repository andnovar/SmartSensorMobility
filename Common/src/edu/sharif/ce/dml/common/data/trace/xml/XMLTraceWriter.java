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

package edu.sharif.ce.dml.common.data.trace.xml;

import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.util.io.output.BufferOutputWriter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 24, 2007
 * Time: 11:32:59 AM
 */
public class XMLTraceWriter extends TraceWriter {
    private Collection<StringDataParameter> parameters;
    private String[] dataLabels;
    private List<Collection> traces = new LinkedList<Collection>();

    public final static String XMLSeparator = ",";

    public XMLTraceWriter() {
        super();
    }

    public XMLTraceWriter(Collection<StringDataParameter> parameters, String[] dataLabels,
                          BufferOutputWriter writer, String outputString) {
        super(parameters, dataLabels, writer, outputString);
        init(parameters,dataLabels,outputString);
    }

    public void init(Collection<StringDataParameter> parameters, String[] dataLabels, String outputString) {
        traces=new LinkedList<Collection>();
        writer = BufferOutputWriter.createRandomWriter();
        String outputString2;
        if (outputString.contains(".")){
            outputString2 = outputString.replaceAll("\\.[^\\.]*$", ".xml");
        }else {
            outputString2 = outputString+".xml";
        }
        writer.setOutputString(outputString2);
        this.dataLabels = dataLabels;
        this.parameters = parameters;
    }

    public void writeTrace(Collection trace) {
        traces.add(trace);
    }

    public void flush() {

    }

    public void flushAndClose() {

        Element rootElement = new Element("Simulation");
        //creating header section
        Element header = new Element("Header");
        rootElement.addContent(header);
        header.addContent(new Parameters("Parameters", "Parameter", parameters));

        Element Attributes = new Element("Attributes");
        header.addContent(Attributes);
        for (String dataLabel : dataLabels) {
            Element AtributeElement = new Element("Attribute");
            AtributeElement.setAttribute("name", dataLabel);
            Attributes.addContent(AtributeElement);
        }

        //creating edu.sharif.ce.dml.common.data section
        Element dataElement = new Element("Data");
        rootElement.addContent(dataElement);
        for (Collection trace : traces) {
            Element rowElement = new Element("t");
            dataElement.addContent(rowElement);
            StringBuilder rowContent = new StringBuilder();
            for (Object aTrace : trace) {
                rowContent.append(valueToString(aTrace)).append(XMLSeparator);
            }
            rowContent.deleteCharAt(rowContent.length() - 1);
            rowElement.addContent(rowContent.toString());

        }

        Document outputDocument = new Document(rootElement);
        XMLOutputter outputer = new XMLOutputter(Format.getCompactFormat());
        try {
            outputer.output(outputDocument, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * a snap shot of nodes in an iteration at specific time.
     */
    static class Parameters extends Element {

        public Parameters(String tagName) {
            super(tagName);
        }

        public Parameters(String tagName, String childName, Collection<StringDataParameter> attributes) {
            this(tagName);
            setParametersElement(childName, attributes);
        }

        public Parameters(String tagName, String childName, String[] names, Object[] values) {
            this(tagName);
            for (int i = 0; i < names.length; i++) {
                addContent(new XMLParameter(childName, names[i], values[i]));
            }
        }

        public void setParametersElement(String childName, Collection<StringDataParameter> parameters) {
            this.removeContent();
            for (StringDataParameter parameter : parameters) {
                addContent(new XMLParameter(childName, parameter.getName(), parameter.getValue()));
            }
        }


        public Parameters(Element e) {
            super();
            for (Object o : e.getContent()) {
                Element e2 = (Element) o ;
                addContent(new XMLParameter("Parameter",e2.getAttributeValue("name"),e2.getAttributeValue("value")));
            }
//            setContent(e.getContent());
//            setAttributes(e.getAttributes());
            setName(e.getName());
            setNamespace(e.getNamespace());

        }

        public Collection<StringDataParameter> getParameters() {
            List<StringDataParameter> parameters = new LinkedList<StringDataParameter>();
            for (Object e : getChildren()) {
                parameters.add(XMLParameter.getDataParameter((Element) e));
            }
            return parameters;
        }

        static class XMLParameter extends Element {

            public XMLParameter(String tagName, String name, Object value) {
                super(tagName);
                setAttribute("name", name);
                setAttribute("value", valueToString(value));
            }            

            public StringDataParameter getDataParameter() {
                return new StringDataParameter(getAttributeValue("name"), getAttributeValue("value"));
            }

            public static StringDataParameter getDataParameter(Element e) {
                return new StringDataParameter(e.getAttributeValue("name"), e.getAttributeValue("value"));
            }
        }
    }
        @Override
    public String getOutputString() {
        return writer.getOutputString();
    }
}
