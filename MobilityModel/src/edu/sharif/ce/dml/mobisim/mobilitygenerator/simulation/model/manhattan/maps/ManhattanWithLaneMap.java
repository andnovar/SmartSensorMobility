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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.manhattan.maps;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.BooleanParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerArrayParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.IncludableMap;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.exception.NodeOutOfMap;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.HandleShape;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 27, 2006
 * Time: 10:07:51 AM
 * <br/>This is an implementation of Manhattan mobility model with two lane streets.
 * nodes will reflect at end of map on appropiate lane. in the crosspoints
 * nodes can not rotate 180 degree. and rotating to left in crosspoints do not take
 * a time. also in the crosspoints proiority of running is not implemented.
 * also nodes can not overpass each other. it is an active map which
 * itself generates valid location for {@link edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model}.
 * <br/> list of uiparameters: <br/>
 * {@link Integer} width<br/>
 * {@link Integer} height<br/>
 * {@link Integer} hlinei: its horizental line<br/>
 * {@link Integer} vlinei: its vertical line<br/>
 * {@link Integer} dir12lanespace: space between forward lane and backward lane<br/>
 * where i should be an {@link Integer} >= 0, <b>note</b> that point location should be unique.
 */
public class ManhattanWithLaneMap extends edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.Map implements IncludableMap {
    private int height, width;
    private int dir12LaneSpace;
    private Integer[] horizentalLines;//= new int[]{50,250,450,};
    private Integer[] verticalLines;//= new int[] {50,250,450,};
    private CrossPoint[][] crossPoints;
    private static final int LEFT = 0;
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;

    public ManhattanWithLaneMap() {
        super();
        width = 0;
        height = 0;
        dir12LaneSpace = 0;
        horizentalLines = new Integer[0];
        verticalLines = new Integer[0];
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        width = (Integer) parameters.get("width").getValue();
        height = (Integer) parameters.get("height").getValue();
        dir12LaneSpace = (Integer) parameters.get("dir12lanespace").getValue();

        //loads horizontal lines
        int[] tempHLinesArr = (int[]) parameters.get("hlines").getValue();

        List<Integer> tempHLines = new ArrayList<Integer>();
        for (Integer tempHLine : tempHLinesArr) {
            tempHLines.add(tempHLine);
            tempHLines.add(tempHLine + dir12LaneSpace);
        }
        horizentalLines = tempHLines.toArray(new Integer[tempHLines.size()]);
        //loads vertical lines
        int[] tempVLinesArr = (int[]) parameters.get("vlines").getValue();

        List<Integer> tempVLines = new ArrayList<Integer>();
        for (Integer tempVLine : tempVLinesArr) {
            tempVLines.add(tempVLine);
            tempVLines.add(tempVLine + dir12LaneSpace);
        }
        verticalLines = tempVLines.toArray(new Integer[tempVLines.size()]);

        //sort verticalLines and horizontalLines because parameter map can be unsorted
        Arrays.sort(verticalLines);
        Arrays.sort(horizentalLines);

        //todo it can be more efficient
        crossPoints = new CrossPoint[verticalLines.length + 2][horizentalLines.length + 2];
        for (int i = 0; i < verticalLines.length; i++) {
            crossPoints[i + 1][0] = new CrossPoint(i + 1, 0);
            crossPoints[i + 1][horizentalLines.length + 1] = new CrossPoint(i + 1, horizentalLines.length + 1);

        }
        for (int i = 0; i < horizentalLines.length; i++) {
            for (int j = 0; j < verticalLines.length; j++) {
                crossPoints[j + 1][i + 1] = new CrossPoint(j + 1, i + 1);
            }
            crossPoints[0][i + 1] = new CrossPoint(0, i + 1);
            crossPoints[verticalLines.length + 1][i + 1] = new CrossPoint(verticalLines.length + 1, i + 1);
        }

    }


    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("width", new IntegerParameter("width", getWidth()));
        parameters.put("height", new IntegerParameter("height", getHeight()));
        parameters.put("dir12lanespace", new IntegerParameter("dir12lanespace", dir12LaneSpace));

        List<Integer> tempHLines = new ArrayList<Integer>();
        for (int j = 0; j < horizentalLines.length; j += 2) {
            tempHLines.add(horizentalLines[j]);
        }
        parameters.put("hlines", new IntegerArrayParameter("hlines", tempHLines.toArray(new Integer[0])));

        List<Integer> tempVLines = new ArrayList<Integer>();
        for (int j = 0; j < verticalLines.length; j += 2) {
            tempVLines.add(verticalLines[j]);
        }
        parameters.put("vlines", new IntegerArrayParameter("vlines", tempVLines.toArray(new Integer[0])));

        return parameters;
    }

    /**
     * @return number of horizental lines in this map.
     */
    public int getHorizentalLines() {
        return horizentalLines.length;
    }

    /**
     * @return number of vertical lines in this map.
     */
    public int getVerticalLines() {
        return verticalLines.length;
    }


    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public void paint(Graphics2D g) {
        Color lastColor = g.getColor();
        g.setColor(Color.blue);
        for (int hLine : horizentalLines) {
            g.drawLine(0, hLine, getWidth(), hLine);
        }
        for (int vLine : verticalLines) {
            g.drawLine(vLine, 0, vLine, getHeight());
        }

        g.setColor(lastColor);
    }

    /**
     * @param vPointsRatio
     * @param hPointsRatio
     * @return a point which is <code>vPointsRatio</code> far in horizental axis and
     *         <code>hPointRatio</code> far in vertiacl axis.
     */
    public Location getPoint(double vPointsRatio, double hPointsRatio) {
        return new Location(verticalLines[(int) (getVerticalLines() * vPointsRatio)],
                horizentalLines[(int) (getHorizentalLines() * hPointsRatio)]);
    }


    /**
     * generates the location of destination location and fills the initial location of a point
     *
     * @param point           any crossPoint in the map
     * @param source          if the point location is the location of source or destination
     * @param distanceRatio   distance Ratio of node from the point between 0-1
     * @param initialLocation the holder of the initialLocation in the map, it will be filled
     * @return the location of destNode location
     */
    public Location generateDestnodePointandLocation(
            Location point, boolean source, double distanceRatio, Location initialLocation) {
        int vIndex = Arrays.binarySearch(verticalLines, (int) point.getX());
        int hIndex = Arrays.binarySearch(horizentalLines, (int) point.getY());
        if (source) {
            //the point is source node, the dest node should be located
            if (vIndex % 2 == 1 && hIndex % 2 == 0) {
                //south east
                //it goes right
                Location destNodeLoc = crossPoints[vIndex + 2][hIndex + 1].getLocation();
                initialLocation.pasteCoordination(point.getX() + distanceRatio * (destNodeLoc.getX() - point.getX()), point.getY());
                return destNodeLoc;
            } else if (vIndex % 2 == 1 && hIndex % 2 == 1) {
                //north east
                //it goes up
                Location destNodeLoc = crossPoints[vIndex + 1][hIndex + 2].getLocation();
                initialLocation.pasteCoordination(point.getX(), point.getY() + distanceRatio * (destNodeLoc.getY() - point.getY()));
                return destNodeLoc;
            } else if (vIndex % 2 == 0 && hIndex % 2 == 1) {
                //north west
                //it goes left
                Location destNodeLoc = crossPoints[vIndex][hIndex + 1].getLocation();
                initialLocation.pasteCoordination(point.getX() + distanceRatio * (destNodeLoc.getX() - point.getX()), point.getY());
                return destNodeLoc;
            } else if (vIndex % 2 == 0 && hIndex % 2 == 0) {
                //south west
                //it goes down
                Location destNodeLoc = crossPoints[vIndex + 1][hIndex].getLocation();
                initialLocation.pasteCoordination(point.getX(), point.getY() + distanceRatio * (destNodeLoc.getY() - point.getY()));
                return destNodeLoc;
            }
        } else {
            //the point is dest node, the sourceNode should be located
            if (vIndex % 2 == 1 && hIndex % 2 == 0) {
                //south east
                //it goes up
                Location sourceNodeLoc = crossPoints[vIndex + 1][hIndex].getLocation();
                initialLocation.pasteCoordination(point.getX(), point.getY() + distanceRatio * (sourceNodeLoc.getY() - point.getY()));
                return point;
            } else if (vIndex % 2 == 1 && hIndex % 2 == 1) {
                //north east
                //it goes left
                Location sourceNodeLoc = crossPoints[vIndex + 2][hIndex + 1].getLocation();
                initialLocation.pasteCoordination(point.getX() + distanceRatio * (sourceNodeLoc.getX() - point.getX()), point.getY());
                return point;
            } else if (vIndex % 2 == 0 && hIndex % 2 == 1) {
                //north west
                //it goes down
                Location sourceNodeLoc = crossPoints[vIndex + 1][hIndex + 2].getLocation();
                initialLocation.pasteCoordination(point.getX(), point.getY() + distanceRatio * (sourceNodeLoc.getY() - point.getY()));
                return point;
            } else if (vIndex % 2 == 0 && hIndex % 2 == 0) {
                //south west
                //it goes right
                Location sourceNodeLoc = crossPoints[vIndex][hIndex + 1].getLocation();
                initialLocation.pasteCoordination(point.getX() + distanceRatio * (sourceNodeLoc.getX() - point.getX()), point.getY());
                return point;
            }
        }
        return null;
    }

    /**
     * generates destination location such that destination location and sourceLoc not be in one
     * direction of location.
     *
     * @param destTransitionNum >=0 and &lt; 4 which represents the selected direction for destination
     * @param location          current location which sould be a possible (valid) point in this map.
     * @return new destionation location.
     * @throws NodeOutOfMap if location not be a possible(valid) point in this map.
     */
    public Location getNextDestNode(int destTransitionNum, Location location) throws NodeOutOfMap {
        int vIndex = Arrays.binarySearch(verticalLines, (int) location.getX());
        int hIndex = Arrays.binarySearch(horizentalLines, (int) location.getY());
        boolean isInVerticalLine = vIndex >= 0;
        boolean isInHorizentalLine = hIndex >= 0;
        if (isInVerticalLine && isInHorizentalLine) {
            //it is on cross point
            List<CrossPoint> neighbors = getLegalNeighbor(++hIndex, ++vIndex);
            if (vIndex % 2 == 0 && hIndex % 2 == 1) {
                //south east
                neighbors.remove(3);
                if (destTransitionNum == 2) {//turn left
                    location.pasteCoordination(crossPoints[vIndex][hIndex + 1].getLocation());
                }
            } else if (vIndex % 2 == 0 && hIndex % 2 == 0) {
                //north east
                neighbors.remove(0);
                if (destTransitionNum == 2) {//turn left
                    location.pasteCoordination(crossPoints[vIndex - 1][hIndex].getLocation());
                }
            } else if (vIndex % 2 == 1 && hIndex % 2 == 0) {
                //north west
                neighbors.remove(1);
                if (destTransitionNum == 0) {//turn left
                    location.pasteCoordination(crossPoints[vIndex][hIndex - 1].getLocation());
                }

            } else if (vIndex % 2 == 1 && hIndex % 2 == 1) {
                //south west
                neighbors.remove(2);
                if (destTransitionNum == 1) {//turn left
                    location.pasteCoordination(crossPoints[vIndex + 1][hIndex].getLocation());
                }
            }

            return neighbors.get(destTransitionNum).getLocation();
        } else if (vIndex == -1 && isInHorizentalLine) {
            //it is on begin of one horizentalLines
            location.setY(horizentalLines[hIndex - 1]);
            return new Location(verticalLines[0], horizentalLines[hIndex - 1]);
        } else if (vIndex == -getVerticalLines() - 1 && isInHorizentalLine) {
            //it is on end of one horizentalLines
            location.setY(horizentalLines[hIndex + 1]);
            return new Location(verticalLines[getVerticalLines() - 1], horizentalLines[hIndex + 1]);
        } else if (isInVerticalLine && hIndex == -1) {
            //it is on begin of one verticalLine
            location.setX(verticalLines[vIndex + 1]);
            return new Location(verticalLines[vIndex + 1], horizentalLines[0]);
        } else if (isInVerticalLine && hIndex == -getHorizentalLines() - 1) {
            //it is on end of one verticalLine
            location.setX(verticalLines[vIndex - 1]);
            return new Location(verticalLines[vIndex - 1], horizentalLines[getHorizentalLines() - 1]);
        }
        throw new NodeOutOfMap(location.toString());
    }


    /**
     * @return a set of all possible (valid) destination location in this map.
     */
    public Set<Location> generateAllPosiblePointLocation() {
        Set<Location> points = new HashSet<Location>();
        for (int vLine : verticalLines) {
            points.add(new Location(vLine, 0));
            points.add(new Location(vLine, getHeight()));
        }
        for (int hLine : horizentalLines) {
            points.add(new Location(0, hLine));
            points.add(new Location(getWidth(), hLine));
            for (int vLine : verticalLines) {
                points.add(new Location(vLine, hLine));
            }
        }
        return points;
    }

    /**
     * @param crossPoint
     * @return all crosspoints which has one hop distance from <code>crossPoint</code>
     */
    private List<CrossPoint> getLegalNeighbor(CrossPoint crossPoint) {
        return getLegalNeighbor(crossPoint.hIndex, crossPoint.vIndex);
    }

    /**
     * @param hIndex
     * @param vIndex
     * @return all crosspoints which has one hop distance from <code>crossPoint</code> in position
     *         vIndex, hIndex.
     */
    private List<CrossPoint> getLegalNeighbor(int hIndex, int vIndex) {
        if (hIndex > 0 && vIndex > 0 & hIndex <= getHorizentalLines() && vIndex <= getVerticalLines()) {
            List<CrossPoint> neighbors = new ArrayList<CrossPoint>();
            neighbors.add(crossPoints[vIndex + vIndex % 2 + 1][hIndex + hIndex % 2 - 1]);//rightDown
            neighbors.add(crossPoints[vIndex + vIndex % 2][hIndex + hIndex % 2 + 1]);//topRight
            neighbors.add(crossPoints[vIndex + vIndex % 2 - 2][hIndex + hIndex % 2]);//leftTop
            neighbors.add(crossPoints[vIndex + vIndex % 2 - 1][hIndex + hIndex % 2 - 2]);//downLeft
            return neighbors;
        }
        return null;
    }

    public MapHandleGroup getHandles() {
        MapHandleGroup mhp1 = super.getHandles();
        ManhattanMapHandleGroup mhp = new ManhattanMapHandleGroup(mhp1.getHandles(), mhp1.getSize());
        mhp.setAddPoint(true);
        List<MapHandle> handles = mhp.getHandles();
        for (int i = 0; i < horizentalLines.length; i += 2) {
            Integer horizentalLine = horizentalLines[i];
            handles.add(new ManhattanMapHandle(1, horizentalLine, true));
        }
        for (int i = 0; i < verticalLines.length; i += 2) {
            Integer verticalLine = verticalLines[i];
            handles.add(new ManhattanMapHandle(verticalLine, 1, false));
        }
        return mhp;
    }

    public boolean validateHandles(MapHandleGroup mhp) {
        boolean valid = super.validateHandles(mhp);

        List<MapHandle> handles = mhp.getHandles();
        MapHandle size = mhp.getSize();
        for (MapHandle mapHandle : handles) {
            if (((ManhattanMapHandle) mapHandle).horizental) {
                valid = valid && mapHandle.getY() <= size.getY() - dir12LaneSpace && mapHandle.getY() >= 0;
            } else {
                valid = valid && mapHandle.getX() <= size.getX() - dir12LaneSpace && mapHandle.getX() >= 0;
            }
        }
        if (valid) {
            for (MapHandle handle : handles) {
                if (((ManhattanMapHandle) handle).horizental) {
                    handle.setXY(1, handle.getY());
                } else {
                    handle.setXY(handle.getX(), 1);
                }
            }
        }
        return valid;
    }

    public void paintUsingHandles(Graphics2D g, MapHandleGroup mhp) {
        super.paintUsingHandles(g, mhp);
        List<MapHandle> handles = mhp.getHandles();
        MapHandle size = mhp.getSize();
        for (MapHandle mapHandle : handles) {
            if (((ManhattanMapHandle) mapHandle).horizental) {
                g.drawLine(0, mapHandle.getY(), size.getX(), mapHandle.getY());
            } else {
                g.drawLine(mapHandle.getX(), 0, mapHandle.getX(), size.getY());
            }
        }
    }

    public void fillFromHandles(MapHandleGroup mhp) throws InvalidParameterInputException {
        super.fillFromHandles(mhp);
        java.util.Map<String, Parameter> parameters = getParameters();
        List<MapHandle> handles = mhp.getHandles();
        MapHandle size = mhp.getSize();
        List<Integer> xArray = new LinkedList<Integer>();
        List<Integer> yArray = new LinkedList<Integer>();
        for (MapHandle handle : handles) {
            if (((ManhattanMapHandle) handle).horizental) {
                yArray.add(handle.getY());
            } else {
                xArray.add(handle.getX());
            }
        }
        parameters.put("hlines", new IntegerArrayParameter("hlines", yArray.toArray(new Integer[yArray.size()])));
        parameters.put("vlines", new IntegerArrayParameter("vlines", xArray.toArray(new Integer[xArray.size()])));
        parameters.put("width", new IntegerParameter("width", size.getX()));
        parameters.put("height", new IntegerParameter("height", size.getX()));
            setParameters(parameters);
    }

    public List<MapHandle> getIncludingLocations(MapHandleGroup mhp) {
        List<MapHandle> outputHandles = new LinkedList<MapHandle>();
        List<MapHandle> handles = mhp.getHandles();
        MapHandle size = mhp.getSize();
        for (MapHandle mapHandle : handles) {
            if (((ManhattanMapHandle) mapHandle).horizental) {
                int y = mapHandle.getY();
                outputHandles.add(new MapHandle(0, y));
                outputHandles.add(new MapHandle(size.getX(), y));
                outputHandles.add(new MapHandle(0, y + dir12LaneSpace));
                outputHandles.add(new MapHandle(size.getX(), y + dir12LaneSpace));
            } else {
                int x = mapHandle.getX();
                outputHandles.add(new MapHandle(x, 0));
                outputHandles.add(new MapHandle(x, size.getY()));
                outputHandles.add(new MapHandle(x + dir12LaneSpace, 0));
                outputHandles.add(new MapHandle(x + dir12LaneSpace, size.getY()));
            }
        }
        return outputHandles;
    }


    private static class ManhattanMapHandle extends MapHandle {
        boolean horizental = false;
        private boolean lastHorizental;

        protected static final ManhattanHorizentalHandleShape HORIZENTAL_HANDLE_SHAPE = new ManhattanHorizentalHandleShape();
        protected static final ManhattanVerticalHandleShape VERTICAL_HANDLE_SHAPE = new ManhattanVerticalHandleShape();

        public ManhattanMapHandle(int x, int y, boolean horizental) {
            super(x, y, true, horizental ? HORIZENTAL_HANDLE_SHAPE : VERTICAL_HANDLE_SHAPE);
            this.horizental = horizental;
        }

        public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
            super.setParameters(parameters);
            lastHorizental = horizental;
            horizental = ((BooleanParameter) parameters.get("horizontal")).getValue();
            if (lastHorizental && !horizental) {
                setXY(getY(), 1);
            } else if (!lastHorizental && horizental) {
                setXY(1, getX());
            }
            setShape(horizental ? HORIZENTAL_HANDLE_SHAPE : VERTICAL_HANDLE_SHAPE);
        }

        public void revert() {
            super.revert();
            boolean lastHorizental2 = horizental;
            horizental = lastHorizental;
            lastHorizental = lastHorizental2;
            /*if (lastHorizental && !horizental) {  //commented because of super.revert()
                setXY(getY(),1);
            }else if (!lastHorizental && horizental){
                setXY(1,getX());
            }*/
        }

        public Map<String, Parameter> getParameters() {
            Map<String, Parameter> parameters = super.getParameters();
            parameters.put("horizontal", new BooleanParameter("horizontal", horizental));
            return parameters;
        }

        protected static class ManhattanHorizentalHandleShape extends HandleShape {

            public void paint(MapHandle handle, Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                Color lastColor = g2.getColor();
                g2.setColor(Color.red);
                int widthHeight = (int) (MapHandle.SIZE / 3.0) + 1;
                int left = handle.getX() - MapHandle.SIZE / 2;
                int right = handle.getX() + MapHandle.SIZE / 2;
                int top = handle.getY() + MapHandle.SIZE / 2;
                int center = handle.getY();
                int down = handle.getY() - MapHandle.SIZE / 2;
                g2.fillPolygon(new int[]{left, right,
                        left, left,
                        right - widthHeight, left},
                        new int[]{top, center,
                                down, down + widthHeight,
                                center, top - widthHeight}, 6);

                if (handle.isSelected()) {
                    g2.setColor(Color.black);
                    g2.drawRect(handle.getX() - MapHandle.SIZE / 2, handle.getY() - MapHandle.SIZE / 2, MapHandle.SIZE, MapHandle.SIZE);
                }
                g2.setColor(lastColor);

            }
        }

        protected static class ManhattanVerticalHandleShape extends HandleShape {

            public void paint(MapHandle handle, Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                Color lastColor = g2.getColor();
                g2.setColor(Color.red);
                int widthHeight = (int) (MapHandle.SIZE / 3.0) + 1;
                int left = handle.getX() - MapHandle.SIZE / 2;
                int center = handle.getX();
                int right = handle.getX() + MapHandle.SIZE / 2;
                int top = handle.getY() + MapHandle.SIZE / 2;
                int down = handle.getY() - MapHandle.SIZE / 2;
                g2.fillPolygon(new int[]{left, center,
                        right, right - widthHeight,
                        center, left + widthHeight},
                        new int[]{down, top,
                                down, down,
                                top - widthHeight, down}, 6);

                if (handle.isSelected()) {
                    g2.setColor(Color.black);
                    g2.drawRect(handle.getX() - MapHandle.SIZE / 2, handle.getY() - MapHandle.SIZE / 2, MapHandle.SIZE, MapHandle.SIZE);
                }
                g2.setColor(lastColor);
            }
        }
    }

    private static class ManhattanMapHandleGroup extends MapHandleGroup {

        public ManhattanMapHandleGroup(List<MapHandle> handles, MapHandle size) {
            super(handles, size);
        }

        public MapHandle createMapHandle(int x, int y) {
            ManhattanMapHandle mapHandle = new ManhattanMapHandle(x, y, false);
            handles.add(mapHandle);
            return mapHandle;
        }
    }


    /**
     * represents a cross point in the map which is where a vertical line crosses a hoizental line.
     */
    private class CrossPoint {
        int vIndex, hIndex;

        /**
         * @param obj
         * @return if vIndex and hIndex of crosspoints are equal.
         */
        public boolean equals(Object obj) {
            CrossPoint crossPoint = (CrossPoint) obj;
            return crossPoint.vIndex == vIndex && crossPoint.hIndex == hIndex;
        }

        public String toString() {
            return vIndex + ":" + hIndex + "=" + getLocation();
        }

        public CrossPoint(int vIndex, int hIndex) {
            this.vIndex = vIndex;
            this.hIndex = hIndex;
        }

        /**
         * @return actual location of this crossPoint on the map according to cartesian coordinates.
         */
        public Location getLocation() {
            try {
                if (vIndex == 0) {
                    return new Location(0, horizentalLines[hIndex - 1]);
                } else if (vIndex == verticalLines.length + 1) {
                    return new Location(getWidth(), horizentalLines[hIndex - 1]);
                } else if (hIndex == 0) {
                    return new Location(verticalLines[vIndex - 1], 0);
                } else if (hIndex == horizentalLines.length + 1) {
                    return new Location(verticalLines[vIndex - 1], getHeight());
                }
                return new Location(verticalLines[vIndex - 1], horizentalLines[hIndex - 1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                return new Location(0, 0);
            }
        }
    }
}
