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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model;

import edu.sharif.ce.dml.common.data.entity.DataLocation;
import edu.sharif.ce.dml.common.data.entity.NodeShadow;
import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.SelectOneParameterable;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.SensorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.Simulation;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.initiallocation.LocationInitializer;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.Map;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.PassiveMap;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.exception.InvalidLocationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.poweralgorithm.PowerAlgorithm;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 22, 2006
 * Time: 10:37:29 PM
 * <br/>This class represents a mobility model. also it has some usefull method that
 * new models can override them to change default models behaviour
 * <br/> list of uiparameters: <br/>
 * {@link Integer} maxspeed<br/>
 * {@link Integer} minspeed<br/>
 * {@link Integer} maxtransition<br/>
 * {@link Integer} maxpausetime: duration in which each node stops at destination location<br/>
 */
public abstract class Model extends ParameterableImplement {


    protected List<GeneratorNode> modelNodes;
    protected List<SensorNode> sensorNodes;
    protected SelectOneParameterable mapSelect = new SelectOneParameterable(true);
    protected NodePainter nodePainter;
    protected ParameterableParameter mapEditor = new ParameterableParameter();
    protected SelectOneParameterable locationInitializerParameter = new SelectOneParameterable(true);
    protected SelectOneParameterable rangeSelect = new SelectOneParameterable(true);
    //protected double range = 0;

    protected Model() {
        super();
    }

    public int compareTo(Object o) {
        return toString().compareTo(o.toString());
    }

    public java.util.Map<String, Parameter> getParameters() {
        java.util.Map<String, Parameter> parameters = new HashMap<String, Parameter>();

        parameters.put("mapeditor", mapEditor);
        parameters.put("maps", mapSelect);
        parameters.put("locationinitializer", locationInitializerParameter);
        //parameters.put("range", new DoubleParameter("range", range));
        parameters.put("rangealgorithms", rangeSelect);
        return parameters;
    }

    public void setParameters(java.util.Map<String, Parameter> parameters) throws InvalidParameterInputException {

        mapSelect = (SelectOneParameterable) parameters.get("maps");
        mapEditor = (ParameterableParameter) parameters.get("mapeditor");
        locationInitializerParameter = (SelectOneParameterable) parameters.get("locationinitializer");
        ((MapEditor) mapEditor.getValue()).setModel(this);
        rangeSelect = (SelectOneParameterable) parameters.get("rangealgorithms");
        //range = ((DoubleParameter) parameters.get("range")).getValue();
    }

    protected double getRandomValue() {
        return Simulation.getDoubleRandomNumber();
    }

    /**
     * @return current map
     */
    public Map getMap() {
        return (Map) mapSelect.getValue();
    }

    public List<GeneratorNode> getModelNodes() {
        return modelNodes;
    }
    
    public List<SensorNode> getSensorModelNodes() {
        return sensorNodes;
    }

    /**
     * sets nodes that this model should manage their movement. models can ovveride this to initiates
     * their structures that is realated to nodes
     *
     * @param modelNodes
     */
    public void setModelNodes(List<GeneratorNode> modelNodes) throws ModelInitializationException {
        this.modelNodes = modelNodes;
        nodePainter = new NodePainter();
    }

    protected abstract void initNode(GeneratorNode node) throws ModelInitializationException;

    protected Location generateInitLocation() {
        Map map = getMap();
        LocationInitializer locationInitializer = (LocationInitializer) locationInitializerParameter.getValue();
        Location location = locationInitializer.getLocation(map.getWidth(), map.getHeight());
        try {
            ((PassiveMap) map).validateNode(location);
        } catch (InvalidLocationException e) {
            location = generateInitLocation();
        }
        return location;
    }

    protected abstract void getNextStep(double timeStep, GeneratorNode node);

    /**
     * updates all model node positions according to timeStep
     *
     * @param timeStep
     */
    public void updateNodes(double timeStep) {
        for (GeneratorNode node : getModelNodes()) {
            getNextStep(timeStep, node);
        }
        updateRanges();
    }

    protected void updateRanges() {
        ((PowerAlgorithm) rangeSelect.getValue()).setRange(getModelNodes());
    }

    /**
     * Model should initiate its nodes and set their initial location by this method
     * this method is called before nodes move after creation
     */
    public void initNodes() throws ModelInitializationException {
        for (GeneratorNode node : getModelNodes()) {
            initNode(node);
        }
        updateRanges();
    }

    public void paintBackground(Graphics2D g) {
        Map m = getMap();
        g.translate(-m.getOrigin().getX(), -m.getOrigin().getY());
        //draw map
        m.paint(g);
        g.translate(+getMap().getOrigin().getX(), +getMap().getOrigin().getY());
    }

    public java.util.Map<GeneratorNode, NodePainter> getNodeNodePainter() {
        java.util.Map<GeneratorNode, NodePainter> outputMap = new HashMap<GeneratorNode, NodePainter>();
        NodePainter nodePainter1 = getNodePainter();
        for (GeneratorNode node : getModelNodes()) {
            outputMap.put(node, new NodePainter(nodePainter1));
        }
        return outputMap;
    }

    public final void writeTraces(TraceWriter writer, List prefix, Location offset) {

        for (GeneratorNode node : getModelNodes()) {
            List trace = new LinkedList();
            trace.addAll(prefix);
            trace.addAll(print(node, offset));
            writer.writeTrace(trace);
        }
    }

    public List print(GeneratorNode node, Location offset) {
        return node.print(offset);
    }

    public List<String> getLabels() {
        return new LinkedList<String>();
    }

    public NodePainter getNodePainter() {
        return nodePainter;
    }

    /**
     * It may be called multiple times so make sure to return the same object each time
     *
     * @return
     */
    public MapHandleSupport getMapHandleSupport() {
        return getMap();
    }

    public class NodePainter {
        protected java.util.Map<GeneratorNode, Integer> nodePosition = new HashMap<GeneratorNode, Integer>();
        Location offset;

        public NodePainter() {
            Location origin = getMap().getOrigin();
            offset = new Location(-origin.getX(), -origin.getY());
        }

        public NodePainter(NodePainter n) {
            offset = new Location(n.getOffset());
            nodePosition.putAll(n.nodePosition);
        }

        public int getSize(GeneratorNode node) {
            return GeneratorNode.CIRCLE_RADIUS;
        }

        public void resetNodePositions() {
            nodePosition.clear();
        }

        public Location getOffset() {
            return offset;
        }

        public void paint(Graphics2D g, GeneratorNode node) {
            Color lastColor = g.getColor();
            g.setColor(getColor(node));
            DataLocation loc = node.getLocation();
            int size = getSize(node);
            g.fillOval(loc.getX() - size, loc.getY() - size, 2 * size, 2 * size);
            g.setColor(lastColor);
        }

        public Color getColor(GeneratorNode node) {
            Integer pos = nodePosition.get(node);
            if (pos == null) {
                pos = getModelNodes().indexOf(node);
                nodePosition.put(node, pos);
            }
            return GeneratorNode.NodeColors[pos % GeneratorNode.NodeColors.length];
        }

    }
    
    /*public class SensorPainter {
        protected java.util.Map<GeneratorNode, Integer> nodePosition = new HashMap<GeneratorNode, Integer>();
        Location offset;

        public NodePainter() {
            Location origin = getMap().getOrigin();
            offset = new Location(-origin.getX(), -origin.getY());
        }

        public NodePainter(NodePainter n) {
            offset = new Location(n.getOffset());
            nodePosition.putAll(n.nodePosition);
        }

        public int getSize(GeneratorNode node) {
            return GeneratorNode.CIRCLE_RADIUS;
        }

        public void resetNodePositions() {
            nodePosition.clear();
        }

        public Location getOffset() {
            return offset;
        }

        public void paint(Graphics2D g, GeneratorNode node) {
            Color lastColor = g.getColor();
            g.setColor(getColor(node));
            DataLocation loc = node.getLocation();
            int size = getSize(node);
            g.fillOval(loc.getX() - size, loc.getY() - size, 2 * size, 2 * size);
            g.setColor(lastColor);
        }

        public Color getColor(GeneratorNode node) {
            Integer pos = nodePosition.get(node);
            if (pos == null) {
                pos = getModelNodes().indexOf(node);
                nodePosition.put(node, pos);
            }
            return GeneratorNode.NodeColors[pos % GeneratorNode.NodeColors.length];
        }

    }*/

}
