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
import edu.sharif.ce.dml.common.parameters.logic.complex.LazySelectOneParameterable;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleArrayParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.MapHandleSupport;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Jun 7, 2009
 * Time: 12:06:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class InternalModel extends ParameterableImplement implements MapHandleSupport{
    private LazySelectOneParameterable model= new LazySelectOneParameterable();
    private Location offset = new Location(0,0);
    private int numberOfNodes=0;

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        model = (LazySelectOneParameterable) parameters.get("internalmodel");
        numberOfNodes = ((IntegerParameter) parameters.get("internalnodes")).getValue();
        double[] iap = (double[]) ((DoubleArrayParameter) parameters.get("offset")).getValue();
        offset.setX(iap[0]);
        offset.setY(iap[1]);
    }

    public Map<String, Parameter> getParameters() {
        Map<String,Parameter > parameters = new HashMap<String, Parameter>();
        parameters.put("internalmodel",model);
        parameters.put("offset", new DoubleArrayParameter
                ("offset", new double[]{offset.getX(), offset.getY()}));
        parameters.put("internalnodes",new IntegerParameter("internalnodes",numberOfNodes));
        return parameters;
    }

    public Location getOffset() {
        return offset;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public Model getModel() {
        return (Model)model.getValue();
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public void setOffset(Location offset) {
        this.offset = offset;
    }

    public MapHandleSupport getMapHandleSupport(){
        return this;
    }

    public MapHandleGroup getHandles() {
        return getModel().getMapHandleSupport().getHandles();
    }

    public boolean validateHandles(MapHandleGroup mhp) {
        return getModel().getMapHandleSupport().validateHandles(mhp);
    }

    public void paintUsingHandles(Graphics2D g, MapHandleGroup mhp) {
         getModel().getMapHandleSupport().paintUsingHandles(g,mhp);
    }

    public void fillFromHandles(MapHandleGroup mhp) throws InvalidParameterInputException {
        getModel().getMapHandleSupport().fillFromHandles(mhp);
    }
}
