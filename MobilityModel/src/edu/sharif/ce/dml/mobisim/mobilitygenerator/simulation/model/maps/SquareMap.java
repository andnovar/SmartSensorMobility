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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.exception.InvalidLocationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 22, 2006
 * Time: 9:36:08 PM<br/>
 * a map that validates destnode and node that is located in a square
 * <br/> list of uiparameters: <br/>
 * {@link Integer} width<br/>
 * {@link Integer} height<br/>
 */
public class SquareMap extends edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.Map implements PassiveMap, IncludableMap,IncludingMap{
    int width, height;

    public SquareMap() {
        super();
        width=0;
        height=0;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);    //To change body of overridden methods use File | Settings | File Templates.
        width = (Integer)parameters.get("width").getValue();
        height = (Integer)parameters.get("height").getValue();
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("width", new IntegerParameter("width", getWidth()));
        parameters.put("height", new IntegerParameter("height", getHeight()));
        return parameters;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void validateNode(Location loc) throws InvalidLocationException {
        validateDestNode(loc);
    }

    public void validateDestNode(Location loc) {

    }

    public void paint(Graphics2D g) {
        g.drawRect((int)mapOrigin.getX(), (int)mapOrigin.getY(), this.getWidth(), this.getHeight());
    }

    public void paintUsingHandles(Graphics2D g, MapHandleGroup mhp) {
        super.paintUsingHandles(g, mhp);
        MapHandle size = mhp.getSize();
        g.drawRect(0,0, size.getX(), size.getY());
    }

    public void fillFromHandles(MapHandleGroup mhp) throws InvalidParameterInputException {
        super.fillFromHandles(mhp);
        MapHandle size = mhp.getSize();
        java.util.Map<String,Parameter> parameters = getParameters();
        parameters.put("width", new IntegerParameter("width", size.getX()));
        parameters.put("height", new IntegerParameter("height", size.getY()));
            setParameters(parameters);
    }

    public List<MapHandle> getIncludingLocations(MapHandleGroup mhp) {
         List<MapHandle> handles = new LinkedList<MapHandle>();
        MapHandle size = mhp.getSize();
//        MapHandle origin = mhp.getMapOrigin();
  //      MapHandle border = mhp.getHandles().get(0);
        handles.add(new MapHandle(0, 0));
        handles.add(new MapHandle(size.getX() ,size.getY() ));
        return handles;
    }

        public boolean isIncluding(List<MapHandle> includableHandles, MapHandleGroup mhp) {
        MapHandle size = mhp.getSize();
        int sizeX = size.getX();
        int sizeY = size.getY();
        boolean valid = true;

        for (int i = 0; i < includableHandles.size() && valid; i++) {
            MapHandle includableHandle = includableHandles.get(i);
            int x = includableHandle.getX();
            int y = includableHandle.getY();
            valid = x >= 0 && x <= sizeX &&
                    y >=0 && y <= sizeY;
        }
        return valid;
    }
}
