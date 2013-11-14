/*
 * Copyright (c) 2005-2009 by Masoud Moshref Javadi <moshref@ce.sharif.edu>, http://ce.sharif.edu/~moshref
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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ThreeD;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.data.GeneralDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.LazySelectOneParameterable;
import edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.FileParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.MapHandleSupport;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ModelInitializationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ThreeD.threedmap.ThreeDMap;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.IncludingMap;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.multi.MultiMapHandleGroup;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: masoud
 * Date: Mar 26, 2009
 * Time: 6:16:08 PM <br/>
 * It must be in this module to load dynamically
 */
public class ThreeDizerModel extends Model implements IncludingMap, MapHandleSupport {
    protected LazySelectOneParameterable twoDModel = new LazySelectOneParameterable();
    protected Map<GeneratorNode, ThreeDNode> threeDNodes = new HashMap<GeneratorNode, ThreeDNode>();
    private ParameterableParameter mapSetting = new ParameterableParameter();

    private static final java.util.List<String> TRACE_LABELS = Arrays.asList("PositionZ");

    @Override
    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        twoDModel = (LazySelectOneParameterable) parameters.get("twodmodel");
        mapSetting = (ParameterableParameter) parameters.get("outputmapsetting");
        getThreeDMap().setTwoDMap(getModel().getMap());
    }

    @Override
    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("twodmodel", twoDModel);
        parameters.put("outputmapsetting", mapSetting);

        return parameters;
    }

    @Override
    public void setModelNodes(List<GeneratorNode> modelNodes) throws ModelInitializationException {
        super.setModelNodes(modelNodes);
        getModel().setModelNodes(modelNodes);
        for (GeneratorNode modelNode : modelNodes) {
            threeDNodes.put(modelNode, new ThreeDNode(modelNode));
        }
    }

    private ThreeDMap getThreeDMap() {
        return (ThreeDMap) getMap();
    }

    public void initNodes() throws ModelInitializationException {
        writeMapSetting();
        getModel().initNodes();
        for (GeneratorNode node : getModelNodes()) {
            threeDNodes.get(node).z = getThreeDMap().getZ(node.getDoubleLocation());
        }
        updateRanges();
    }

    private void writeMapSetting() {
        File selectedFile = ((FileParameter) mapSetting.getValue()).getValue();
        Element rootElement = new Element("rootcomposite");
        rootElement.setAttribute("name", "");
        GeneralDataParameter dataParameter = ParameterableParameter.getDataParameters(getMap(), false);
        dataParameter.toXML(rootElement);

        Document outputDocument = new Document(rootElement);
        XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(selectedFile));
            outputter.output(outputDocument, outputWriter);
            outputWriter.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private Model getModel() {
        return (Model) twoDModel.getValue();
    }

    public List<GeneratorNode> getModelNodes() {
        return getModel().getModelNodes();
    }

    @Override
    protected void initNode(GeneratorNode node) throws ModelInitializationException {
        throw new ModelInitializationException("this method not should be called");
    }

    @Override
    public void getNextStep(double timeStep, GeneratorNode node) {

    }

    @Override
    public void updateNodes(double timeStep) {
        getModel().updateNodes(timeStep);
        for (GeneratorNode node : getModelNodes()) {
            threeDNodes.get(node).z = getThreeDMap().getZ(node.getDoubleLocation());
        }
        updateRanges();
    }

    public List print(GeneratorNode node, Location offset) {
        List output = new LinkedList(node.print(offset));
        output.add(threeDNodes.get(node).z);
        return output;
    }

    public List<String> getLabels() {
        return new LinkedList<String>(TRACE_LABELS);
    }

    public MapHandleSupport getMapHandleSupport() {
        return this;
    }

    public boolean isIncluding(List<MapHandle> includableHandles, MapHandleGroup mhp) {
        return false;
    }

    public MapHandleGroup getHandles() {
        MapHandleGroup masterHandleGroup = getMap().getHandles();
        MultiMapHandleGroup mhp = new MultiMapHandleGroup( masterHandleGroup.getSize(),
                masterHandleGroup, this);
        MapHandleSupport handleSupport = getModel().getMapHandleSupport();
        MapHandleGroup twoDMhp = handleSupport.getHandles();
        MapHandle offset = new MapHandle(0, 0);
        offset.setEnable(false);
        mhp.addMapHandleGroup(twoDMhp, handleSupport, offset);
        return mhp;
    }

    public boolean validateHandles(MapHandleGroup mhp) {
        MultiMapHandleGroup mapHandleGroup = (MultiMapHandleGroup) mhp;
        MapHandleGroup twoDmhp = mapHandleGroup.getMapHandleGroup(0);
        //sequence is important
        boolean valid = mapHandleGroup.getSupporterFor(0).validateHandles(twoDmhp);
        //change threedmap size
        MapHandleGroup threeDmhp = mapHandleGroup.getMasterHandleGroup();
        MapHandle twoDSize = twoDmhp.getSize();
        threeDmhp.getSize().setXY(twoDSize.getX(), twoDSize.getY());
        valid = valid && getMap().validateHandles(threeDmhp);

        return valid;
    }

    public void paintUsingHandles(Graphics2D g, MapHandleGroup mhp) {
        MultiMapHandleGroup mapHandleGroup = (MultiMapHandleGroup) mhp;
        getMap().paintUsingHandles(g, mapHandleGroup.getMasterHandleGroup());
        mapHandleGroup.getSupporterFor(0).paintUsingHandles(g, mapHandleGroup.getMapHandleGroup(0));
    }

    public void fillFromHandles(MapHandleGroup mhp) throws InvalidParameterInputException {
        MultiMapHandleGroup mapHandleGroup = (MultiMapHandleGroup) mhp;
        //sequence is important
        mapHandleGroup.getSupporterFor(0).fillFromHandles(mapHandleGroup.getMapHandleGroup(0));
        getMap().fillFromHandles(mapHandleGroup.getMasterHandleGroup());
    }

    private class ThreeDNode {
        GeneratorNode node;
        double z = 0;

        private ThreeDNode(GeneratorNode node) {
            this.node = node;
        }
    }
}
