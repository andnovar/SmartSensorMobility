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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ThreeD.threedmap;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 27, 2009
 * Time: 11:41:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class Point extends ParameterableImplement {
    double x, y, z;
    boolean isGrid;

    public String toString() {
        return "X:"+x+" Y:"+y+" Z:"+z;
    }

    public Point(double x, double y, double z,boolean isGrid) {
        super();
        this.x = x;
        this.z = z;
        this.y = y;
        this.isGrid = isGrid;
    }

    public Point(double x, double y, double z) {
        this(x,y,z,false);
    }

    public Point() {
        this(0, 0, 0);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double get2DDistance(Point p) {
        return Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2));
    }

    public double get2DDistance(Location l) {
        return Math.sqrt(Math.pow(x - l.getX(), 2) + Math.pow(y - l.getY(), 2));
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point)) return false;

        Point point = (Point) o;

        if (Double.compare(point.x, x) != 0) return false;
        if (Double.compare(point.y, y) != 0) return false;

        return true;
    }

    public void setGrid(boolean grid) {
        isGrid = grid;
    }

    public boolean isGrid() {
        return isGrid;
    }

    public int hashCode() {
        int result;
        long temp;
        temp = x != +0.0d ? Double.doubleToLongBits(x) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = y != +0.0d ? Double.doubleToLongBits(y) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        x = ((DoubleParameter) parameters.get("x")).getValue();
        y = ((DoubleParameter) parameters.get("y")).getValue();
        z = ((DoubleParameter) parameters.get("z")).getValue();
    }

    public java.util.Map<String, Parameter> getParameters() {
        java.util.Map<String, Parameter> parameters = new HashMap<String, Parameter>(3);
        parameters.put("x", new DoubleParameter("x", x));
        parameters.put("y", new DoubleParameter("y", y));
        parameters.put("z", new DoubleParameter("z", z));
        return parameters;
    }

}
