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
import edu.sharif.ce.dml.common.parameters.logic.complex.MultipleSelectParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.Map;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.HandleShape;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: masoud
 * Date: Mar 26, 2009
 * Time: 6:23:40 PM
 */
public class ThreeDMap extends Map {
    private Map twoDMap;
    private MultipleSelectParameter points = new MultipleSelectParameter("points");
    private final List<Point> pointsList = new LinkedList<Point>();

    private Block[][] blocks;
    private java.util.Map<Location, Double> pointsCache = new HashMap<Location, Double>();
    double gridSize = 0;
    double cachePrecision = 0;
    private static final double DEFAULT_Z = 0;
    private int width;
    private int height;

    @Override
    public void setParameters(java.util.Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        points = (MultipleSelectParameter) parameters.get("points");
        gridSize = ((DoubleParameter) parameters.get("gridsize")).getValue();
        cachePrecision = ((DoubleParameter) parameters.get("cacheprecision")).getValue();
        width = ((IntegerParameter) parameters.get("width")).getValue();
        height = ((IntegerParameter) parameters.get("height")).getValue();
        fillPointsList();
    }

    public void setTwoDMap(Map twoDMap) {
        this.twoDMap = twoDMap;
        width = getWidth();
        height = getHeight();
        mapOrigin = getTwoDMap().getOrigin();
        fillPointsList();
    }

    private void fillPointsList() {

        pointsList.clear();
        for (Parameterable parameterable : points.getSelected()) {
            pointsList.add((Point) parameterable);
        }

        //insert default points on grid nodes if there is not
        for (int x = 0; x < getWidth() / gridSize; x++) {
            for (int y = 0; y < getHeight() / gridSize; y++) {
                createPoint(x * gridSize, y * gridSize);
            }
        }
        //add last row and column nodes
        {
            for (int x = 0; x < getWidth() / gridSize; x++) {
                createPoint(x * gridSize, getHeight());
            }
            for (int y = 0; y < getHeight() / gridSize; y++) {
                createPoint(getWidth(), y * gridSize);
            }
            createPoint(getWidth(), getHeight());

        }

        Collections.sort(pointsList, new YXComparator());
        int rows = (int) (getWidth() / gridSize);
        int cols = (int) (getHeight() / gridSize);
        blocks = new Block[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                blocks[i][j] = new Block();
            }
        }

        for (Point point : pointsList) {
            for (Block block : getBlock(point.x, point.y)) {
                block.addPoint(point);
            }
        }
    }

    private void createPoint(double x, double y) {
        Point p = new Point(x, y, DEFAULT_Z, true);
        // in list it is not efficient!
        int index = pointsList.indexOf(p);
        if (index < 0) {
            pointsList.add(p);
        } else {
            pointsList.get(index).setGrid(true);
        }
    }

    private List<Block> getBlock(double x, double y) {
        List<Block> output = new LinkedList<Block>();
        int row = (int) (x / gridSize);
        int col = (int) (y / gridSize);
        if (col < blocks[0].length) {
            if (row < blocks.length) {
                output.add(blocks[row][col]);
            }
            if (row > 0 && y % gridSize == 0) {
                output.add(blocks[row - 1][col]);
            }
        }
        if (col > 0 && x % gridSize == 0) {
            if (row < blocks.length) {
                output.add(blocks[row][col - 1]);
            }
            if (row > 0 && y % gridSize == 0) {
                output.add(blocks[row - 1][col - 1]);
            }
        }
        return output;
    }


    @Override
    public java.util.Map<String, Parameter> getParameters() {

        java.util.Map<String, Parameter> parameters = super.getParameters();
        parameters.put("points", points);
        parameters.put("gridsize", new DoubleParameter("gridsize",  gridSize));
        //I removed max value restriction (getwidth||getheight) as they return 0 in initialization
        parameters.put("cacheprecision", new DoubleParameter("cacheprecision", cachePrecision));

        return parameters;
    }

    private Map getTwoDMap() {
        return twoDMap;
    }

    public List<Point> getPointsList() {
        return pointsList;
    }

    public int getHeight() {
        if (getTwoDMap() != null) {
            return getTwoDMap().getHeight();
        } else {
            return height;
        }
    }

    public int getWidth() {
        if (getTwoDMap() != null) {
            return getTwoDMap().getWidth();
        } else {
            return width;
        }
    }

    public void paint(Graphics2D g) {
        getTwoDMap().paint(g);
    }

    public double getZ(Location loc) {
        // check pointsList
        double x = loc.getX();
        double y = loc.getY();
        Point point = new Point(x, y, DEFAULT_Z);
        int pos = Collections.binarySearch(pointsList, point, new YXComparator());
        if (pos >= 0) {
            return pointsList.get(pos).getZ();
        }
        // check cache
        Location cacheLocation = new Location((int) (x / cachePrecision) * cachePrecision,
                (int) (y / cachePrecision) * cachePrecision);
        Double cacheZ = pointsCache.get(cacheLocation);
        if (cacheZ != null) {
            return cacheZ;
        }

        //find the triangle
        //find the block points
        // nodes on four or three blocks are in pointsList!!
        // TODO what about nodes in two blocks!
        List<Block> pointBlocks = getBlock(point.x, point.y);
        List<Point> tempList = new ArrayList<Point>();
        for (Block pointBlock : pointBlocks) {
            tempList.addAll(pointBlock.points);
        }

        Point[] triangle = getTriangle(tempList, point);
        Point p1 = triangle[0];
        Point p2 = triangle[1];
        Point p3 = triangle[2];
        /*Iterator<Point> itr = tempList.iterator();
        Point p1 = itr.next();
        Point p2 = itr.next();
        Point p3 = itr.next();*/


        double z = p1.z;
        if (!(p1.z == p2.z && p1.z == p3.z)) {
            //create the plane using the three points
            //find two vector
            double[] p1p2 = new double[]{p2.x - p1.x, p2.y - p1.y, p2.z - p1.z};
            double[] p1p3 = new double[]{p3.x - p1.x, p3.y - p1.y, p3.z - p1.z};
            //cross
            //    i j   k
            //n=[ a b   c ]
            //    x y   z
            double[] n = new double[]{p1p2[1] * p1p3[2] - p1p2[2] * p1p3[1],
                    -(p1p2[0] * p1p3[2] - p1p2[2] * p1p3[0]),
                    p1p2[0] * p1p3[1] - p1p2[1] * p1p3[0]};
            z = p1.z - ((n[0] * (x - p1.x) + n[1] * (y - p1.y)) / n[2]);
//            System.out.println(p1 + " : " + p2 + " : " + p3 + " --> for " + loc + " = " + z);
        }
        //add to cache
        pointsCache.put(cacheLocation, z);
        return z;
    }

    private Point[] getTriangle(List<Point> tempList, Point point) {
        Collections.sort(tempList, new DistanceSort(point));
        //check all triangles
        for (Point point1 : tempList) {
            for (Point point2 : tempList) {
                if (point1.equals(point2)) continue;
                for (Point point3 : tempList) {
                    if (point3.equals(point1) || point3.equals(point2)) continue;
// Compute vectors
                    double[] v0 = new double[]{point3.x - point1.x, point3.y - point1.y};
                    double[] v1 = new double[]{point2.x - point1.x, point2.y - point1.y};
                    double[] v2 = new double[]{point.x - point1.x, point.y - point1.y};

// Compute dot products
                    double dot00 = v0[0] * v0[0] + v0[1] * v0[1];
                    double dot01 = v0[0] * v1[0] + v0[1] * v1[1];
                    double dot02 = v0[0] * v2[0] + v0[1] * v2[1];
                    double dot11 = v1[0] * v1[0] + v1[1] * v1[1];
                    double dot12 = v1[0] * v2[0] + v1[1] * v2[1];

// Compute barycentric coordinates
                    double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
                    double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
                    double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
                    if ((u > 0) && (v > 0) && (u + v < 1)) return new Point[]{point1, point2, point3};

/*
Barycentric Technique
 from http://www.blackpawn.com/texts/pointinpoly/default.html
// Compute vectors
                    v0 = C - A
                    v1 = B - A
                    v2 = P - A

// Compute dot products
                    dot00 = dot(v0, v0)
                    dot01 = dot(v0, v1)
                    dot02 = dot(v0, v2)
                    dot11 = dot(v1, v1)
                    dot12 = dot(v1, v2)

// Compute barycentric coordinates
                    invDenom = 1 / (dot00 * dot11 - dot01 * dot01)
                    u = (dot11 * dot02 - dot01 * dot12) * invDenom
                    v = (dot00 * dot12 - dot01 * dot02) * invDenom

// Check if point is in triangle
                    return (u > 0) && (v > 0) && (u + v < 1)
*/
                }
            }
        }
        assert false;
        return new Point[0];
    }

    public double getGridSize() {
        return gridSize;
    }

    public MapHandleGroup getHandles() {
        MapHandleGroup mhp1 = super.getHandles();
        MapHandle sizeMapHandle = mhp1.getSize();
        sizeMapHandle.setEnable(false);
        ThreeDMapHandleGroup mapHandleGroup = new ThreeDMapHandleGroup(mhp1.getHandles(), sizeMapHandle);
        List<MapHandle> handles = mapHandleGroup.getHandles();
        for (Point point : pointsList) {
            boolean isGrid = point.isGrid();
            handles.add(new ThreeDMapHandle((int) point.getX(), (int) point.getY(), (int) point.getZ(), !isGrid,
                    isGrid ? ThreeDMapHandle.THREE_D_GRID_HANDLE_SHAPE : ThreeDMapHandle.THREE_D_HANDLE_SHAPE, isGrid));
        }
        return mapHandleGroup;
    }

    public boolean validateHandles(MapHandleGroup mhp) {
        boolean valid = super.validateHandles(mhp);
        MapHandle mapSize = mhp.getSize();
        // check no nondefault handle leave out of map
        for (MapHandle handle : mhp.getHandles()) {
            ThreeDMapHandle threeDMapHandle = (ThreeDMapHandle) handle;
            if (threeDMapHandle.getZ() != DEFAULT_Z && (threeDMapHandle.getX() > mapSize.getX()
                    || threeDMapHandle.getY() > mapSize.getY())) {
                return false;
            }
            //add, remove, change points
            //todo
        }
        return valid;
    }

    public void paintUsingHandles(Graphics2D g, MapHandleGroup mhp) {
        super.paintUsingHandles(g, mhp);
        List<MapHandle> handles = mhp.getHandles();
        for (MapHandle mapHandle : handles) {
            g.drawOval(mapHandle.getX() - 5, mapHandle.getY() - 5, 10, 10);
        }
    }

    public void fillFromHandles(MapHandleGroup mhp) throws InvalidParameterInputException {
        super.fillFromHandles(mhp);
        java.util.Map<String, Parameter> parameters = getParameters();
        MultipleSelectParameter points = (MultipleSelectParameter) parameters.get("points");
        List<MapHandle> handles = mhp.getHandles();
        List<Point> pointsList = new LinkedList<Point>();
        for (MapHandle handle : handles) {
            ThreeDMapHandle threeDMapHandle = (ThreeDMapHandle) handle;
            //only add points that has different z value from default z
            if (!threeDMapHandle.isGrid() || threeDMapHandle.getZ() != DEFAULT_Z) {
                pointsList.add(new Point(threeDMapHandle.getX(), threeDMapHandle.getY(), threeDMapHandle.getZ()));
            }
        }
        points.setValue(pointsList);
        setParameters(parameters);
        MapHandleGroup mapHandleGroup2 = getHandles();
        mhp.setHandles(mapHandleGroup2.getHandles());
    }

    protected static class ThreeDMapHandleGroup extends MapHandleGroup {
        public ThreeDMapHandleGroup(List<MapHandle> handles, MapHandle size) {
            super(handles, size);
            setAddPoint(true);
        }

        public MapHandle createMapHandle(int x, int y) {
            MapHandle mapHandle = new ThreeDMapHandle(x, y, (int) DEFAULT_Z, true, ThreeDMapHandle.THREE_D_HANDLE_SHAPE, false);
            handles.add(mapHandle);
            return mapHandle;
        }
    }

    protected static class ThreeDMapHandle extends MapHandle {
        protected static final ThreeDHandleShape THREE_D_HANDLE_SHAPE = new ThreeDHandleShape();
        protected static final ThreeDGridHandleShape THREE_D_GRID_HANDLE_SHAPE = new ThreeDGridHandleShape();
        protected double z = DEFAULT_Z;
        protected boolean isGrid;

        public ThreeDMapHandle(int x, int y, int z, boolean enabled, HandleShape shape, boolean isGrid) {
            super(x, y, enabled, shape);
            this.z = z;
            this.isGrid = isGrid;
        }

        public double getZ() {
            return z;
        }

        public java.util.Map<String, Parameter> getParameters() {
            java.util.Map<String, Parameter> parameters = super.getParameters();
            parameters.put("z", new DoubleParameter("z", z));
            return parameters;
        }

        public void setParameters(java.util.Map<String, Parameter> parameters) throws InvalidParameterInputException {
            super.setParameters(parameters);
            z = ((DoubleParameter) parameters.get("z")).getValue();
        }

        public String toString() {
            return super.toString() + " Z:" + z;
        }

        public boolean isGrid() {
            return isGrid;
        }
    }

    protected static class ThreeDHandleShape extends HandleShape {
        public void paint(MapHandle handle, Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Color lastColor = g2.getColor();
            g2.setColor(Color.green);
            g2.fillOval(handle.getX() - MapHandle.SIZE / 2, handle.getY() - MapHandle.SIZE / 2, MapHandle.SIZE, MapHandle.SIZE);
            if (handle.isSelected()) {
                g2.setColor(Color.black);
                g2.drawRect(handle.getX() - MapHandle.SIZE / 2, handle.getY() - MapHandle.SIZE / 2, MapHandle.SIZE, MapHandle.SIZE);
            }
            g2.setColor(lastColor);
        }
    }

    protected static class ThreeDGridHandleShape extends HandleShape {

        public void paint(MapHandle handle, Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Color lastColor = g2.getColor();
            int widthHeight = (int) (MapHandle.SIZE / 3.0) + 1;
            g2.setColor(Color.green);
            g2.fillOval(handle.getX() - MapHandle.SIZE / 2, handle.getY() - MapHandle.SIZE / 2, MapHandle.SIZE, MapHandle.SIZE);
            g2.setColor(Color.white);
            g2.fillOval(handle.getX() - widthHeight, handle.getY() - widthHeight,
                    widthHeight * 2, widthHeight * 2);
            if (handle.isSelected()) {
                g2.setColor(Color.black);
                g2.drawRect(handle.getX() - MapHandle.SIZE / 2, handle.getY() - MapHandle.SIZE / 2, MapHandle.SIZE, MapHandle.SIZE);
            }
            g2.setColor(lastColor);
        }
    }

    public static class YXComparator implements Comparator<Point> {
        public int compare(Point o1, Point o2) {
            int yDist = (int) (o1.y - o2.y);
            if (yDist != 0) return yDist;
            return (int) (o1.x - o2.x);
        }
    }

    public static Comparator<Point> getYXComparator() {
        return new YXComparator();
    }

    public static class DistanceSort implements Comparator<Point> {
        private Point reference;

        public DistanceSort(Point reference) {
            this.reference = reference;
        }

        public int compare(Point o1, Point o2) {
            return (int) (o1.get2DDistance(reference) - o2.get2DDistance(reference));
        }
    }


    private class Block {
        List<Point> points;

        private Block() {
            points = new LinkedList<Point>();
        }

        void addPoint(Point p) {
            points.add(p);
        }
    }
}
