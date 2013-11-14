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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.freeway.maps;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleArrayParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.IncludableMap;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.sequential.SequentialMapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.sequential.SequentialMapHandleGroup;

import java.awt.*;
import java.util.*;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 24, 2006
 * Time: 11:58:32 PM
 * <br/>an implementation of freewaymap which has <code>lanenum</code> lane. it is an active map which
 * itself generates valid location for {@link edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model}. In this map nodes will loop
 * after reaching the last point in their lane the will rotate to opposite lane.
 * in this implementation locations will jump through lanes only at the end of each lane.
 * so forward and backward lane numbers are equal.
 * <br/> list of uiparameters: <br/>
 * {@link Integer} width<br/>
 * {@link Integer} height<br/>
 * {@link Integer} pointix<br/>
 * {@link Integer} pointiy:
 * where i should be an {@link Integer} >= 0, <b>note</b> that point location should be unique.<br/>
 * {@link Integer} lanenum : forward = backward lane number.<br/>
 * {@link Integer} dir12space<br/>
 * {@link Integer} dir1lanespace<br/>
 * {@link Integer} dir2lanespace<br/>
 */
public class FreeWayWithLaneMap extends edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.Map implements IncludableMap {
    private int width, height;
    private List<Lane> lanes;
    private int dir1Num, dir12Space, dir1LaneSpace, dir2LaneSpace;

    public FreeWayWithLaneMap() {
        super();
        width = 0;
        height = 0;
        dir1Num = 0;
        dir12Space = 0;
        dir1LaneSpace = 0;
        dir2LaneSpace = 0;
        lanes = new LinkedList<Lane>();
    }

    /**
     * initiates map uiparameters and creates points on lanes, such that point uiparameters
     * will be first lane and other lanes will be constructed according to
     * <code>dir12space,dir1lanespace</code> and <code>dir2lanespace</code>
     *
     * @param parameters a {@link java.util.Map} that contains uiparameters for this map to fill the properties
     */
    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        width = (Integer) parameters.get("width").getValue();
        height = (Integer) parameters.get("height").getValue();
        dir1Num = (Integer) parameters.get("lanenum").getValue();
        dir12Space = (Integer) parameters.get("dir12space").getValue();
        dir1LaneSpace = (Integer) parameters.get("dir1lanespace").getValue();
        dir2LaneSpace = (Integer) parameters.get("dir2lanespace").getValue();

        double[] pointsX = (double[]) parameters.get("pointsX").getValue();
        double[] pointsY = (double[]) parameters.get("pointsY").getValue();
        Location[] tempPoints = new Location[pointsX.length];
        for (int i = 0; i < pointsY.length; i++) {
            tempPoints[i] = new Location(pointsX[i], pointsY[i]);
        }
        //initializing firstLane
        Lane firstLane = new Lane();
        double length = 0;
        TreeMap<Double, Location> lengthToLocation = new TreeMap<Double, Location>();
        lengthToLocation.put(length, tempPoints[0]);
        for (int j = 1; j < tempPoints.length; j++) {
            length += tempPoints[j].getLength(tempPoints[j - 1]);
            lengthToLocation.put(length, tempPoints[j]);
        }
        firstLane.setDistanceToLocation(lengthToLocation);
        lanes = new ArrayList<Lane>();
        lanes.add(firstLane);


        for (int i = 1; i < dir1Num; i++) {
            CreateLane(tempPoints, dir1LaneSpace * i);
        }
        List<Location> temp = new ArrayList<Location>(Arrays.asList(tempPoints));
        Collections.reverse(temp);
        tempPoints = temp.toArray(new Location[temp.size()]);
        for (int i = 0; i < dir1Num; i++) {
            CreateLane(tempPoints, (dir1Num - 1) * dir1LaneSpace + dir12Space + i * dir2LaneSpace);
        }
    }

    private void CreateLane(Location[] tempPoints, int distance) {
        TreeMap<Double, Location> lengthToLocation;
        double length;
        Lane lane = new Lane();
        Location location1, location2;
        lengthToLocation = new TreeMap<Double, Location>();
        {
            //init first point in this lane
            length = 0;
            Location firstDirectionCheck = new Location(tempPoints[0].getX() + distance,
                    tempPoints[0].getY());
            if (tempPoints[0].getY() == tempPoints[1].getY()) {
                firstDirectionCheck = new Location(tempPoints[0].getX(),
                        tempPoints[0].getY() + distance);
            }
            location1 = calculateLanePoint(getMirrorPoint(tempPoints[1], tempPoints[0]), tempPoints[0], tempPoints[1],
                    distance, firstDirectionCheck);
            lengthToLocation.put(length, location1);
        }

        //init other points in this lane
        for (int i1 = 1; i1 < tempPoints.length - 1; i1++) {
            location2 = calculateLanePoint(tempPoints[i1 - 1], tempPoints[i1], tempPoints[i1 + 1],
                    distance, location1);
            length += location2.getLength(location1);
            lengthToLocation.put(length, location2);
            location1 = location2;
        }
        {
            //last point
            int i1 = tempPoints.length - 1;
            location2 = calculateLanePoint(tempPoints[i1 - 1], tempPoints[i1],
                    getMirrorPoint(tempPoints[i1 - 1], tempPoints[i1]), distance, location1);
            length += location2.getLength(location1);
            lengthToLocation.put(length, location2);
        }


        lane.setDistanceToLocation(lengthToLocation);
        lanes.add(lane);
    }

    /**
     * find sign of plane created by p0,p1,p2
     *
     * @param p0
     * @param p1
     * @param p2
     * @return whether, in traveling from the first to the second
     *         to the third point, we turn counterclockwise (+1) or not (-1)
     */
    private int ccw(Location p0, Location p1, Location p2) {
        double dx1, dx2, dy1, dy2;

        dx1 = p1.getX() - p0.getX();
        dy1 = p1.getY() - p0.getY();
        dx2 = p2.getX() - p0.getX();
        dy2 = p2.getY() - p0.getY();

        if (dx1 * dy2 > dy1 * dx2)
            return +1;
        if (dx1 * dy2 < dy1 * dx2)
            return -1;
        if ((dx1 * dx2 < 0) || (dy1 * dy2 < 0))
            return -1;
        if ((dx1 * dx1 + dy1 * dy1) < (dx2 * dx2 + dy2 * dy2))
            return +1;
        return 0;
    }


    protected Location getMirrorPoint(Location loc, Location origin) {
        return new Location(2 * origin.getX() - loc.getX(), 2 * origin.getY() - loc.getY());
    }

    /**
     * finds next lane point which has distance=<tt>distance</tt> and is in the same direction of line <tt>loc1</tt>
     * - <tt>loc2</tt> as <tt>directionCheckPoint</tt>
     *
     * @param loc1
     * @param loc2
     * @param loc3
     * @param distance
     * @param directionCheckPoint
     * @return
     */
    protected Location calculateLanePoint(Location loc1, Location loc2, Location loc3, int distance, Location directionCheckPoint) {
        //if three points is on a same line: y3-y1 = (y2-y1)/(x2-x1)(x3-x1)
        if (loc1.getX() == loc2.getX() && loc2.getX() == loc3.getX() // special situation
                || Math.abs(loc3.getY() - loc1.getY() - (loc2.getY() - loc1.getY()) / (loc2.getX() - loc1.getX()) * (loc3.getX() - loc1.getX())) < 0.0001) {
            //find perpendicular line
            double locPX = loc2.getX() + (Math.random() + 1) * 1; //this random has nothing to do with the way the algorithm runs
            Location locP = new Location(0, 0);
            if (loc2.getY() == loc1.getY()) {
                locP.pasteCoordination(locPX, loc1.getY());
            } else {
                locP.pasteCoordination(locPX, -(loc2.getX() - loc1.getX()) / (loc2.getY() - loc1.getY()) *
                        (locPX - loc2.getX()) + loc2.getY());
            }
            Location[] hits = circleIntersection(locP.getX() - loc2.getX(), locP.getY() - loc2.getY(), 0, 0, distance);
            hits[0].pasteCoordination(hits[0].getX() + loc2.getX(), hits[0].getY() + loc2.getY());
            hits[1].pasteCoordination(hits[1].getX() + loc2.getX(), hits[1].getY() + loc2.getY());
            //select a point
            int checkDirection = ccw(loc1, loc2, directionCheckPoint);
            if (checkDirection == ccw(loc1, loc2, hits[0])) {
                return hits[0];
            } else {
                return hits[1];
            }
        } else {

            int z = 1;
            // find two points with distance z on line loc1loc2 and loc2loc3
            double loc1loc2Distance = loc1.getLength(loc2);
            double loc3loc2Distance = loc3.getLength(loc2);
            Location o = new Location((z / loc1loc2Distance) * (loc1.getX() - loc2.getX()) + loc2.getX(),
                    (z / loc1loc2Distance) * (loc1.getY() - loc2.getY()) + loc2.getY());
            Location p = new Location((z / loc3loc2Distance) * (loc3.getX() - loc2.getX()) + loc2.getX(),
                    (z / loc3loc2Distance) * (loc3.getY() - loc2.getY()) + loc2.getY());
            Location bisector = new Location((o.getX() + p.getX()) / 2, (o.getY() + p.getY()) / 2);

            //find distance on the bisector line
            double bisectorDistance = bisector.getLength(loc2) * distance / distanceToLine(loc1, loc2, bisector);
            //find point on bisector with specified distance
            Location[] hits = circleIntersection(bisector.getX() - loc2.getX(), bisector.getY() - loc2.getY(), 0, 0, bisectorDistance);
            hits[0].pasteCoordination(hits[0].getX() + loc2.getX(), hits[0].getY() + loc2.getY());
            hits[1].pasteCoordination(hits[1].getX() + loc2.getX(), hits[1].getY() + loc2.getY());
            //select a point
            int checkDirection = ccw(loc1, loc2, directionCheckPoint);
            if (checkDirection == ccw(loc1, loc2, hits[0])) {
                return hits[0];
            } else {
                return hits[1];
            }
        }
    }

    private Location[] circleIntersection(double x1, double y1, double x2, double y2, double r) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dr = sqrt(pow(dx, 2) + pow(dy, 2));
        double dD = x1 * y2 - x2 * y1;
        double temp1 = sqrt(pow(r, 2) * pow(dr, 2) - pow(dD, 2));
        if (pow(r, 2) * pow(dr, 2) - pow(dD, 2) <= 0) {
            DevelopmentLogger.logger.debug(pow(r, 2) * pow(dr, 2) - pow(dD, 2) + " : " +
                    x1 + " : " + y1 + " : " + x2 + " : " + y2);
        }
        assert pow(r, 2) * pow(dr, 2) - pow(dD, 2) > 0 : "less than two intersection!";
        double sign = Math.signum(dy);
        sign = sign == 0 ? 1 : sign;
        double temp2 = sign * dx * temp1;
        double xHit1 = (dD * dy + temp2) / pow(dr, 2);
        double xHit2 = (dD * dy - temp2) / pow(dr, 2);
        temp2 = Math.abs(dy) * temp1;
        double yHit1 = (-dD * dx + temp2) / pow(dr, 2);
        double yHit2 = (-dD * dx - temp2) / pow(dr, 2);
        return new Location[]{new Location(xHit1, yHit1), new Location(xHit2, yHit2)};
    }

    private double distanceToLine(Location loc1, Location loc2, Location loc) {
        //from http://mathworld.wolfram.com/Point-LineDistance2-Dimensional.html
        return Math.abs((loc2.getX() - loc1.getX()) * (loc1.getY() - loc.getY()) -
                (loc1.getX() - loc.getX()) * (loc2.getY() - loc1.getY())) /
                loc2.getLength(loc1);
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("lanenum", new IntegerParameter("lanenum", dir1Num));
        parameters.put("dir12space", new IntegerParameter("dir12space", dir12Space));
        parameters.put("dir1lanespace", new IntegerParameter("dir1lanespace", dir1LaneSpace));
        parameters.put("dir2lanespace", new IntegerParameter("dir2lanespace", dir2LaneSpace));
        parameters.put("width", new IntegerParameter("width", getWidth()));
        parameters.put("height", new IntegerParameter("height", getHeight()));


        if (lanes.size() > 0) {
            int i = 0;
            double[] pointsX = new double[lanes.get(0).getDistanceToLocation().values().size()];
            double[] pointsY = new double[lanes.get(0).getDistanceToLocation().values().size()];
            for (Location point : lanes.get(0).getDistanceToLocation().values()) {
                pointsX[i] = point.getX();
                pointsY[i] = point.getY();
                i++;
            }

            parameters.put("pointsX", new DoubleArrayParameter("pointsX", pointsX));
            parameters.put("pointsY", new DoubleArrayParameter("pointsY", pointsY));
        } else {
            parameters.put("pointsX", new DoubleArrayParameter("pointsX", new double[0]));
            parameters.put("pointsY", new DoubleArrayParameter("pointsY", new double[0]));
        }
        return parameters;
    }

    /**
     * @return Number of points that can be a destination location.
     */
    public int getPointsNumber() {
        int pointsNumbers = 0;
        for (Lane lane : lanes) {
            pointsNumbers += lane.getDistanceToLocation().size();
        }
        return pointsNumbers;
    }

    /**
     * generates the location of destination location and fills the initial location of a point
     *
     * @param initialLocation     the holder of the initialLocation in the map
     * @param laneNumber          the lane number that this the sourceLoc, destLoc and initiallocation will be in it
     * @param distancePassedRatio ratio of distance that initiallocation will be far from start of lane
     * @param sourceNodeLoc       the location that initialLocation will be after that
     * @return the location of destNode location
     */
    public Location generateDestnodePointandLocation(int laneNumber, double distancePassedRatio,
                                                     Location sourceNodeLoc, Location initialLocation) {
        Lane lane = lanes.get(laneNumber);
        Location destNodeLocation = new Location(0, 0);
        double distanceRatio =
                lane.getLastLocation(distancePassedRatio * lane.getLaneLength(), destNodeLocation, sourceNodeLoc);

        initialLocation.setX(sourceNodeLoc.getX() + (destNodeLocation.getX() - sourceNodeLoc.getX()) * distanceRatio);
        initialLocation.setY(sourceNodeLoc.getY() + (destNodeLocation.getY() - sourceNodeLoc.getY()) * distanceRatio);
        return destNodeLocation;
    }

    /**
     * @param laneNumber  lane index in which this location is located in.
     * @param location    current location.
     * @param destNodeLoc destination location that will be filled
     * @return new lane number if the lane number of the location changes (after reaching a lane end)<br/>
     *         or -1 if <code>location</code> is not a point in the map valid points
     */
    public int getNextDestNode(int laneNumber, Location location, Location destNodeLoc) {
        Lane lane = lanes.get(laneNumber);
        for (Iterator it = lane.getDistanceToLocation().values().iterator(); it.hasNext();) {
            Location destLocation = (Location) it.next();
            if (destLocation.equals(location)) {
                if (!it.hasNext()) {
                    int newLaneIndex = (laneNumber + lanes.size() / 2) % lanes.size();
                    Lane newLane = lanes.get(newLaneIndex);
                    destNodeLoc.pasteCoordination(newLane.getDistanceToLocation().values().iterator().next());
                    return newLaneIndex;
                }
                destNodeLoc.pasteCoordination((Location) it.next());
                return laneNumber;
            }
        }
        return -1;
    }

    /**
     * @return a set of all possible destination point in this map
     */
    public Set<Location> generateAllPosiblePointLocation() {
        Set<Location> points = new HashSet<Location>();
        //iterates on lanes
        for (Lane lane : lanes) {
            for (Location location : lane.getDistanceToLocation().values()) {
                points.add(location);
            }
        }
        return points;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void paint(Graphics2D g) {
        Color tempColor = g.getColor();
        g.setColor(Color.blue);
        for (Lane lane : lanes) {
            Iterator<Location> iterator = lane.getDistanceToLocation().values().iterator();
            Location lastLocation = iterator.next();
            while (iterator.hasNext()) {
                Location location = iterator.next();
                g.drawLine((int) lastLocation.getX(), (int) lastLocation.getY(),
                        (int) location.getX(), (int) location.getY());
                lastLocation = location;
            }
        }
        g.setColor(tempColor);
    }

    /**
     * @return number of lanes in the map
     */
    public double getLanesNum() {
        return lanes.size();
    }

    public MapHandleGroup getHandles() {
        MapHandleGroup superMapHandleGroup = super.getHandles();
        SequentialMapHandleGroup mhp = new SequentialMapHandleGroup(new LinkedList<MapHandle>(), superMapHandleGroup.getSize());
        List<MapHandle> handles = mhp.getHandles();
        int i = 0;
        for (Location location : lanes.get(0).getDistanceToLocation().values()) {
            handles.add(new SequentialMapHandle((int) location.getX(), (int) location.getY(), i++));
        }
        mhp.setAddPoint(true);
        mhp.sortHandles();
        return mhp;
    }

    public boolean validateHandles(MapHandleGroup mhp) {
        List<MapHandle> handles = mhp.getHandles();
        MapHandle size = mhp.getSize();
        boolean valid = super.validateHandles(mhp);
        for (MapHandle handle : handles) {
            valid = valid && handle.getX() <= size.getX() && handle.getY() <= size.getY() &&
                    handle.getX() >= 0 && handle.getY() >= 0;
        }
        if (valid) {
            ((SequentialMapHandleGroup) mhp).sortHandles();
        }
        return valid;
    }

    public void paintUsingHandles(Graphics2D g, MapHandleGroup mhp) {
        super.paintUsingHandles(g, mhp);
        Iterator<MapHandle> iterator = mhp.getHandles().iterator();
        MapHandle lastLocation = iterator.next();
        while (iterator.hasNext()) {
            MapHandle location = iterator.next();
            g.drawLine((int) lastLocation.getX(), (int) lastLocation.getY(),
                    (int) location.getX(), (int) location.getY());
            lastLocation = location;
        }
    }

    public void fillFromHandles(MapHandleGroup mhp) throws InvalidParameterInputException {
        java.util.Map<String, Parameter> parameters = getParameters();
        List<MapHandle> handles = mhp.getHandles();
        MapHandle size = mhp.getSize();
        double[] pointsX = new double[handles.size()];
        double[] pointsY = new double[handles.size()];
        for (int i = 0; i < handles.size(); i++) {
            MapHandle handle = handles.get(i);
            pointsX[i] = handle.getX();
            pointsY[i] = handle.getY();
        }
        parameters.put("pointsX", new DoubleArrayParameter("pointsX", pointsX));
        parameters.put("pointsY", new DoubleArrayParameter("pointsY", pointsY));
        parameters.put("width", new IntegerParameter("width", size.getX()));
        parameters.put("height", new IntegerParameter("height", size.getX()));
        setParameters(parameters);

    }

    public List<MapHandle> getIncludingLocations(MapHandleGroup mhp) {
        List<MapHandle> handles = new LinkedList<MapHandle>();
        for (MapHandle handle : mhp.getHandles()) {
            for (int i = 0; i < dir1Num; i++) {
                handles.add(new MapHandle(handle.getX() + i * dir1LaneSpace, handle.getY()));
                handles.add(new MapHandle(handle.getX() + dir1Num * dir1LaneSpace + dir12Space + i * dir2LaneSpace,
                        handle.getY()));
            }
        }
        return handles;
    }

    /**
     * Represents a lane in this map.
     */
    private class Lane {
        /**
         * stores distance of a location from start point and maps it to that location
         */
        java.util.TreeMap<Double, Location> distanceToLocation;

        public Map<Double, Location> getDistanceToLocation() {
            return distanceToLocation;
        }

        public void setDistanceToLocation(TreeMap<Double, Location> distanceToLocation) {
            this.distanceToLocation = distanceToLocation;
        }

        /**
         * @return get this lane length = length from first point to last point
         */
        public double getLaneLength() {
            return distanceToLocation.lastKey();
        }

        /**
         * @param distance         distance that the point is after start of this lane
         * @param destNodeLocation next node after distance from start of lane
         * @param sourceLoc        last node before distance from start of lane
         * @return ratio of distance passed from sourceLoc to destNodeLocation
         */
        public Double getLastLocation(double distance, Location destNodeLocation, Location sourceLoc) {
            List<Double> distances = new ArrayList<Double>();
            for (Double aDouble : distanceToLocation.keySet()) {
                distances.add(aDouble);
            }
            int index = Collections.binarySearch(distances, distance);
            if (index < 0) {
                index = -index - 2;
            }
            destNodeLocation.pasteCoordination(distanceToLocation.get(distances.get(index + 1)));
            sourceLoc.pasteCoordination(distanceToLocation.get(distances.get(index)));
            return (distance - distances.get(index)) / (distances.get(index + 1) - distances.get(index));
        }
    }

}
