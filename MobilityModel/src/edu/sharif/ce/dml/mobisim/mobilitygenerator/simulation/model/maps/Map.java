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
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.MapHandleSupport;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 22, 2006
 * Time: 8:58:43 PM<br/>
 * This is an abstract class that represents a Map in
 * a {@link edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model} usually models have
 * a high coupling with this class and subclasses
 */
public abstract class Map extends ParameterableImplement implements Comparable, MapHandleSupport {
    private static int mapsNumber = 0;
    //a unique id for each map that is used in compare to and hashcode
    private int mapNumber = 0;

    protected final float dash1[] = {10.0f};
    protected final BasicStroke dashed = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, dash1, 0.0f);

    /**
     * assumed that its x and y is int
     */
    protected Location mapOrigin;


    /**
     * @param parameters a {@link java.util.Map} that contains parameters for this map to fill the properties
     */
    public void setParameters(java.util.Map<String, Parameter> parameters) throws InvalidParameterInputException {

    }

    /**
     * @return a {@link java.util.Map} that contains all parameters of this map.
     */
    public java.util.Map<String, Parameter> getParameters() {
        java.util.Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        int width = getWidth();
        int height = getHeight();
        parameters.put("width", new IntegerParameter("width", width));
        parameters.put("height", new IntegerParameter("height", height));
        return parameters;

    }


    protected Map() {
        this.mapNumber = mapsNumber++;
        mapOrigin = new Location(0, 0);
    }

    /**
     * compares each map according to private mapNumber field.
     *
     * @param o
     * @return true: if two map are identical <br/> false: if they are not same.
     */
    public int compareTo(Object o) {
        int sortOutput = mapNumber - ((Map) o).mapNumber;
        if (sortOutput == 0) return super.compareTo(o);
        return sortOutput;
    }

    /**
     * @return The name of map class
     */
//    public String toString() {
//        String[] names = this.getClass().getName().split("\\.");
//        return names[names.length - 1];
//    }
    public int hashCode() {
        String s = mapNumber + "" + super.hashCode();
        return Integer.parseInt(s.substring(0, s.length() > 6 ? 6 : s.length()));
    }

    /**
     * this method used usually for painting and should shows the maximum height that this map uses
     *
     * @return the max height of map
     */
    public abstract int getHeight();

    /**
     * this method used usually for painting and should shows the maximum width that this map uses
     *
     * @return the max width of map
     */
    public abstract int getWidth();


    /**
     * paints everything that is related to the map
     *
     * @param g
     */
    public abstract void paint(Graphics2D g);

//    /**
//     *
//     * @return uiparameters that width and height parameter has been added to it.
//     */
//    public String getPrintableParamters() {
//        //because some maps do not use width and height.
//        java.util.Map<String, Parameter> parameters = getParameters();
//        if (!parameters.keySet().contains("width")){
//            parameters.put("width",new IntegerParameter("width",this.getWidth()));
//        }
//        if (!parameters.keySet().contains("height")){
//            parameters.put("height",new IntegerParameter("height",this.getHeight()));
//        }
//        return this.printParameters(parameters);
//    }

    public Location getOrigin() {
        return mapOrigin;
    }

    public MapHandleGroup getHandles() {
        ArrayList<MapHandle> handles = new ArrayList<MapHandle>();
        return new MapHandleGroup(handles,
                new MapHandle(getWidth(), getHeight()));
    }

    public boolean validateHandles(MapHandleGroup mhp) {
        MapHandle size = mhp.getSize();
        return size.getX() > 0 && size.getY() > 0;
    }

    public void paintUsingHandles(Graphics2D g, MapHandleGroup mhp) {

    }

    public void fillFromHandles(MapHandleGroup mhp) throws InvalidParameterInputException {

    }

}
