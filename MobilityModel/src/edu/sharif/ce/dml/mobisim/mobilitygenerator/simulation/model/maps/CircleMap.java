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
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 23, 2006
 * Time: 2:14:06 AM<br/>
 * a map that only validates destnodes which is located on a circle stroke
 * <br/> list of uiparameters: <br/>
 * {@link Integer} radius<br/>
 */
public class CircleMap extends edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.Map implements PassiveMap, IncludableMap {
    private int radius;

    public CircleMap() {
        radius = 0;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);    //To change body of overridden methods use File | Settings | File Templates.
        radius = (Integer) parameters.get("radius").getValue();
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("radius", new IntegerParameter("radius", radius));
        return parameters;
    }

    public int getHeight() {
        return 2 * radius;
    }

    public int getWidth() {
        return 2 * radius;
    }

    public void validateNode(Location loc) throws InvalidLocationException {
        validateDestNode(loc);
    }

    public void validateDestNode(Location loc) throws InvalidLocationException {
        double xdif = Math.sqrt(Math.pow(radius, 2) - Math.pow(radius - loc.getY(), 2));
        if ((int) loc.getY() % 2 == 0) {
            loc.setX(radius - xdif);
        } else {
            loc.setX(radius + xdif);
        }
    }

    public void paint(Graphics2D g) {
        g.drawOval((int) mapOrigin.getX(), (int) mapOrigin.getY(), 2 * radius, 2 * radius);
    }

    public boolean validateHandles(MapHandleGroup mhp) {
        MapHandle size = mhp.getSize();
        boolean valid = super.validateHandles(mhp);
        if (valid) {
            size.setXY(Math.min(size.getX(), size.getY()), Math.min(size.getX(), size.getY()));
        }
        return valid;
    }

    public void paintUsingHandles(Graphics2D g, MapHandleGroup mhp) {
        super.paintUsingHandles(g, mhp);
        MapHandle size = mhp.getSize();
        g.drawOval(0, 0, size.getX(), size.getY());
    }

    public void fillFromHandles(MapHandleGroup mhp) throws InvalidParameterInputException {
        super.fillFromHandles(mhp);
        MapHandle size = mhp.getSize();
        Map<String, Parameter> parameters = getParameters();
        parameters.put("radius", new IntegerParameter("radius", size.getX() / 2));
        setParameters(parameters);
    }

    public java.util.List<MapHandle> getIncludingLocations(MapHandleGroup mhp) {
        java.util.List<MapHandle> handles = new LinkedList<MapHandle>();
        MapHandle size = mhp.getSize();
        handles.add(new MapHandle(0, size.getY() / 2));
        handles.add(new MapHandle(size.getX(), size.getY() / 2));
        handles.add(new MapHandle(size.getX() / 2, 0));
        handles.add(new MapHandle(size.getX() / 2, size.getY()));
        return handles;
    }
}
