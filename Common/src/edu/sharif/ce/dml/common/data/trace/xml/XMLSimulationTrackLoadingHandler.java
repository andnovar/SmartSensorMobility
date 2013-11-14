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

import edu.sharif.ce.dml.common.data.entity.DataLocation;
import edu.sharif.ce.dml.common.data.entity.NodeShadow;
import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.BooleanParameter;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkLoadingHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 28, 2007
 * Time: 5:15:45 PM<br/>
 * Responsible to load xml traces and create {@link edu.sharif.ce.dml.common.data.entity.SnapShotData} objects<br/>
 * tag structure:<br/>
 * <ul><li>Simulation</li><ul>
 * <li>Header</li><ul>
 * <li>Parameters</li><ul>
 * <li>Parameter</li></ul>
 * <li>Attributes</li></ul>
 * <li>Data</li></ul></ul>
 */
public class XMLSimulationTrackLoadingHandler implements BulkLoadingHandler<SnapShotData> {
    protected Document doc;
    protected Map<String, String> configurations;
    protected Map<String, Integer> attributeNumber;

    public List<? extends SnapShotData> bReadData(BufferedReader reader) throws IOException {
        if (doc == null) {
            try {
                SAXBuilder builder = new SAXBuilder();
                doc = builder.build(reader);
            } catch (JDOMException e) {
                e.printStackTrace();
            }
        }
        //load labels;
        attributeNumber = loadAttribitues(doc.getRootElement().getChild("Header").getChild("Attributes").getChildren());
        //load real data;
        List<SnapShotData> datas = new LinkedList<SnapShotData>();
        Iterator itr = doc.getRootElement().getChild("Data").getChildren().iterator();
        int nodeNumber = Integer.parseInt(configurations.get(NODE_NUMBER_KEY));
        long lastTime=0;
        while (itr.hasNext()) {
            SnapShotData snapShotData = new SnapShotData(nodeNumber);
            snapShotData.setTime(lastTime);
            for (int i = 0; i < nodeNumber; i++) {
                if (!itr.hasNext()) {
                    break;
                }
                Element e = (Element) itr.next();
                String[] nodeShadowString = e.getText().split(XMLTraceWriter.XMLSeparator);
                if (i==0){
                    snapShotData.setTime(Long.parseLong(nodeShadowString[attributeNumber.get("Time")]));
                    lastTime=snapShotData.getTime();
                }
                snapShotData.addNodeShadows(new NodeShadow(nodeShadowString[attributeNumber.get("Node")],
                        new DataLocation(Integer.parseInt(nodeShadowString[attributeNumber.get("PositionX")]),
                                Integer.parseInt(nodeShadowString[attributeNumber.get("PositionY")])), Double.parseDouble(nodeShadowString[attributeNumber.get("Direction Angle")]),
                        Double.parseDouble(nodeShadowString[attributeNumber.get("Speed")]),
                        attributeNumber.get("Range") != null ? Double.parseDouble(nodeShadowString[attributeNumber.get("Range")]) : NodeShadow.UNDEFINED_RANGE
                        ));
            }
            datas.add(snapShotData);
        }
        return datas;
    }

    /**
     * loads attributes from <tt>childeren</tt> list element from name attribute
     *
     * @param children
     * @return
     */
    protected Map<String, Integer> loadAttribitues(List children) {
        Map<String, Integer> labels = new HashMap<String, Integer>();
        int i = 0;
        for (Object child : children) {
            Element e = (Element) child;
            labels.put(e.getAttributeValue("name"), i++);
        }
        return labels;
    }

    public Map<String, String> loadConfiguration(BufferedReader reader) throws IOException {
        try {
// Load XML into JDOM Document
            SAXBuilder builder = new SAXBuilder();
            builder.setIgnoringElementContentWhitespace(true);
            doc = builder.build(reader);
// Turn into properties objects
            loadConfigFromElements(doc.getRootElement().getChild("Header").getChildren("Parameters"));
        } catch (JDOMException e) {
            throw new IOException(e.getMessage());
        }
        return configurations;
    }

    private void loadConfigFromElements(List elements) {
        configurations = new HashMap<String, String>();
        assert elements.size() > 0;
        XMLTraceWriter.Parameters parameters = new XMLTraceWriter.Parameters((Element) elements.get(0));
        Collection<StringDataParameter> params = parameters.getParameters();
        for (StringDataParameter param : params) {
            configurations.put(param.getName(), param.getValue());
        }
    }

    public SnapShotData[] getEArray(int capacity) {
        return new SnapShotData[capacity];
    }

    public String[] getDataLabels() {
        Set<String> attSet = attributeNumber.keySet();
        String[] labels = new String[attSet.size()];
        for (String att : attSet) {
            labels[attributeNumber.get(att)] = att;
        }
        return labels;
    }
}
