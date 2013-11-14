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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.multi;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.MultipleSelectParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.logic.primitives.BooleanParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.MapHandleSupport;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ModelInitializationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.MyGraphics2D;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.multi.MultiMapHandleGroup;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Jun 7, 2009
 * Time: 11:35:30 AM
 * <br/> The model contains multiple internal models.
 */
public class MultiModel extends Model implements MapHandleSupport {
    private static final java.util.List<String> TRACE_LABELS = Arrays.asList("Model");
    private MultipleSelectParameter models = new MultipleSelectParameter("internalmodels");
    private Map<GeneratorNode, InternalModel> nodeInternalModels = new HashMap<GeneratorNode, InternalModel>();
    private boolean extendMap = false;

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("internalmodels", models);
        parameters.put("extendmap", new BooleanParameter("extendmap", extendMap));
        return parameters;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        models = (MultipleSelectParameter) parameters.get("internalmodels");
        extendMap = ((BooleanParameter) parameters.get("extendmap")).getValue();
        extendMap();
    }

    public MapHandleSupport getMapHandleSupport() {
        return this;
    }

    public void setModelNodes(List<GeneratorNode> modelNodes) throws ModelInitializationException {
        super.setModelNodes(modelNodes);
        nodeInternalModels.clear();
        // set selected models nodes based on the weight of them
        List<InternalModel> selectedModels = getSelectedModels();
        if (selectedModels.size() == 0) {
            throw new ModelInitializationException("No internal model has been selected");
        }
        int sumWeights = 0;
        for (InternalModel selectedModel : selectedModels) {
            sumWeights += selectedModel.getNumberOfNodes();
        }
        int allotedNodes = 0;
        for (InternalModel selectedModel : selectedModels) {
            int numberOfNodes = selectedModel.getNumberOfNodes() * modelNodes.size() / sumWeights;
            selectedModel.setNumberOfNodes(numberOfNodes);
            allotedNodes += numberOfNodes;
        }
        InternalModel lastInternalModel = selectedModels.get(selectedModels.size() - 1);
        lastInternalModel.setNumberOfNodes(lastInternalModel.getNumberOfNodes() + modelNodes.size() - allotedNodes);
        allotedNodes = 0;
        for (InternalModel selectedModel : selectedModels) {
            List<GeneratorNode> nodesList = modelNodes.subList(allotedNodes, allotedNodes + selectedModel.getNumberOfNodes());
            //  System.out.println("Model: "+selectedModel + " -> "+ nodesList.size());
            for (GeneratorNode node : nodesList) {
                nodeInternalModels.put(node, selectedModel);
            }
            selectedModel.getModel().setModelNodes(nodesList);
            allotedNodes += selectedModel.getNumberOfNodes();
        }
    }

    private List<InternalModel> getSelectedModels() {
        List<Parameterable> selectedModels = models.getSelected();
        List<InternalModel> sModels = new ArrayList<InternalModel>(selectedModels.size());
        for (Parameterable selectedModel : selectedModels) {
            sModels.add((InternalModel) selectedModel);
        }
        return sModels;
    }

    @Override
    public Map<GeneratorNode, NodePainter> getNodeNodePainter() {
        NodePainter nodePainter1 = getNodePainter();
        java.util.Map<GeneratorNode, NodePainter> outputMap = new HashMap<GeneratorNode, NodePainter>();
        Location myOffset = nodePainter1.getOffset();
        for (InternalModel internalModel : getSelectedModels()) {
            Map<GeneratorNode, NodePainter> internalNodePainters = internalModel.getModel().getNodeNodePainter();
            for (GeneratorNode node : internalNodePainters.keySet()) {
                NodePainter nodePainter2 = internalNodePainters.get(node);
                nodePainter2.getOffset().translate(myOffset.getX() + internalModel.getOffset().getX(),
                        myOffset.getY() + internalModel.getOffset().getY());
                outputMap.put(node, nodePainter2);
            }
        }
        return outputMap;
    }

    @Override
    public void paintBackground(Graphics2D g) {
        g.translate(-getMap().getOrigin().getX(), -getMap().getOrigin().getY());
        //draw map
        getMap().paint(g);
        //draw internal models
        for (InternalModel internalModel : getSelectedModels()) {
            g.translate(+internalModel.getOffset().getX(), +internalModel.getOffset().getY());
            MyGraphics2D.getInstance().drawString(internalModel.toString(), 0, 0, g);
            internalModel.getModel().paintBackground(g);
            g.translate(-internalModel.getOffset().getX(), -internalModel.getOffset().getY());
        }
        g.translate(+getMap().getOrigin().getX(), +getMap().getOrigin().getY());
    }

    private void extendMap() throws InvalidParameterInputException {
        List<InternalModel> models1 = getSelectedModels();
        if (extendMap) {
            edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.Map map1 = getMap();
            int w = map1.getWidth();
            int h = map1.getHeight();
            for (InternalModel internalModel : models1) {
                edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.Map map2 = internalModel.getModel().getMap();
                Location offset = internalModel.getOffset();
                int width = map2.getWidth() + (int) offset.getX();
                int height = map2.getHeight() + (int) offset.getY();
                w = w < width ? width : w;
                h = h < height ? height : h;
            }
            Map<String, Parameter> parameters = getMap().getParameters();
            ((IntegerParameter) parameters.get("width")).setValue(w);
            ((IntegerParameter) parameters.get("height")).setValue(h);

            getMap().setParameters(parameters);
        }
    }

    @Override
    protected void initNode(GeneratorNode node) throws ModelInitializationException {

    }

    @Override
    protected void getNextStep(double timeStep, GeneratorNode node) {

    }

    public void initNodes() throws ModelInitializationException {
        List<InternalModel> models1 = getSelectedModels();
        for (InternalModel internalModel : models1) {
            internalModel.getModel().initNodes();
        }
        updateRanges();
    }

    public void updateNodes(double timeStep) {
        for (InternalModel internalModel : getSelectedModels()) {
            internalModel.getModel().updateNodes(timeStep);
        }
        updateRanges();
    }

    public MapHandleGroup getHandles() {
        MapHandleGroup masterMapHandleGroup = super.getMapHandleSupport().getHandles();
        MultiMapHandleGroup mhp = new MultiMapHandleGroup(masterMapHandleGroup.getSize(), masterMapHandleGroup, this);
        for (InternalModel internalModel : getSelectedModels()) {
            Location offsetLoc = internalModel.getOffset();
            MapHandle offset = new MapHandle((int) offsetLoc.getX(), (int) offsetLoc.getY());
            mhp.addMapHandleGroup(internalModel.getHandles(), internalModel, offset);
        }
        return mhp;
    }

    public boolean validateHandles(MapHandleGroup mhp) {
        MultiMapHandleGroup mapHandleGroup = (MultiMapHandleGroup) mhp;
        boolean valid = true;
        MapHandle masterSize = mapHandleGroup.getMasterHandleGroup().getSize();
        if (extendMap) {
            //first validate internal objects they may want to extend too!
            for (int i = 0; i < mapHandleGroup.getMapHandleGroupSize(); i++) {
                valid = mapHandleGroup.getSupporterFor(i).validateHandles(mapHandleGroup.getMapHandleGroup(i));
                if (!valid) return false;
            }
            int w = masterSize.getX();
            int h = masterSize.getY();
            for (int i = 0; i < mapHandleGroup.getMapHandleGroupSize(); i++) {
                MapHandle offset = mapHandleGroup.getOffset(i);
                MapHandle mhpSize = mapHandleGroup.getMapHandleGroup(i).getSize();
                int newX = mhpSize.getX() + offset.getX();
                int newY = mhpSize.getY() + offset.getY();
                w = w < newX ? newX : w;
                h = h < newY ? newY : h;
            }
            masterSize.setXY(w, h);
        }
        int masterX = masterSize.getX();
        int masterY = masterSize.getY();
        for (int i = 0; i < mapHandleGroup.getMapHandleGroupSize(); i++) {
            MapHandleGroup group = mapHandleGroup.getMapHandleGroup(i);
            MapHandle offset = mapHandleGroup.getOffset(i);
            valid = valid && group.getSize().getX() + offset.getX() <= masterX;
            valid = valid && group.getSize().getY() + offset.getY() <= masterY;
            valid = valid && (extendMap || mapHandleGroup.getSupporterFor(i).validateHandles(mapHandleGroup.getMapHandleGroup(i)));
            if (!valid) return false;
        }

        return valid;
    }

    public void paintUsingHandles(Graphics2D g, MapHandleGroup mhp) {
        MultiMapHandleGroup mapHandleGroup = (MultiMapHandleGroup) mhp;
        getMap().paintUsingHandles(g, mapHandleGroup.getMasterHandleGroup());

        for (int i = 0; i < mapHandleGroup.getMapHandleGroupSize(); i++) {
            MapHandle offset = mapHandleGroup.getOffset(i);
            g.translate(+offset.getX(), +offset.getY());
            MapHandleSupport mapHandleSupport = mapHandleGroup.getSupporterFor(i);
            MyGraphics2D.getInstance().drawString(mapHandleSupport.toString(), 0, 0, g);
            mapHandleSupport.paintUsingHandles(g, mapHandleGroup.getMapHandleGroup(i));
            g.translate(-offset.getX(), -offset.getY());
        }
    }

    public void fillFromHandles(MapHandleGroup mhp) throws InvalidParameterInputException {
        MultiMapHandleGroup mapHandleGroup = (MultiMapHandleGroup) mhp;
        getMap().fillFromHandles(mapHandleGroup.getMasterHandleGroup());
        List<InternalModel> internalModelsList = getSelectedModels();
        int mhpGroupSize = mapHandleGroup.getMapHandleGroupSize();
        if (mhpGroupSize != internalModelsList.size()) {
            throw new InvalidParameterInputException("Unequal number of obstacles ", models.toString(),
                    mhpGroupSize + "!=" + internalModelsList.size());
        }

        int i = 0;
        for (InternalModel internalModel : internalModelsList) {
            MapHandle mapHandle = mapHandleGroup.getOffset(i);
            internalModel.setOffset(new Location(mapHandle.getX(), mapHandle.getY()));
            mapHandleGroup.getSupporterFor(i).fillFromHandles(mapHandleGroup.getMapHandleGroup(i));
            i++;
        }
        extendMap();
    }

    public List print(GeneratorNode node, Location offset) {
        InternalModel internalModel = nodeInternalModels.get(node);
        List output = new LinkedList();
        Location origin = internalModel.getModel().getMap().getOrigin();
        Location offset2 = internalModel.getOffset();

        output.addAll(internalModel.getModel().print(node, new Location(offset.getX() + offset2.getX() - origin.getX(),
                offset.getY() + offset2.getY() - origin.getY())));
        output.add(internalModel.toString());
        return output;
    }

    public List<String> getLabels() {
        List<String> output = new LinkedList<String>();
        //todo manage inconsistent model labels
        for (InternalModel internalModel : getSelectedModels()) {
            if (output.size() > 0) {
                output.retainAll(internalModel.getModel().getLabels());
            } else {
                output.addAll(internalModel.getModel().getLabels());
            }
        }
        output.addAll(TRACE_LABELS);
        return output;
    }
}
