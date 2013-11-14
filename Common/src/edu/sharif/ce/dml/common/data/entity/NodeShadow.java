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

package edu.sharif.ce.dml.common.data.entity;

import edu.sharif.ce.dml.common.data.trace.Tracable;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * it is a representation of a node in a loaded simulation. not have all property of a real node.
 */
public class NodeShadow implements Tracable {
    /**
     * default value of range = -1
     */
    public static final int UNDEFINED_RANGE = -1;
    public static final int CIRCLE_RADIUS = 8;
    public static final Color[] NodeColors = new Color[]{Color.red, Color.blue, Color.green, Color.magenta, Color.yellow};
    private static final java.util.List<String> traceLabels = Arrays.asList("Node", "PositionX", "PositionY", "Speed",
            "Direction Angle", "Range");
    /**
     * node maximum range
     */
    public static double MaxRange = Double.MAX_VALUE;


    /////////////defaults
    /**
     * node name. usually assumed that is integer parsable.
     */
    protected String name;
    /**
     * current location of the node
     */
    protected DataLocation location;

    // Painting constants it should be move to independent class later
    /**
     * current speed. always > 0
     */
    protected double speed;
    /**
     * current movement direction in radian
     */
    protected double direction;
    /**
     * communication range of the node. default value is {@link #UNDEFINED_RANGE}
     */
    protected double range = 0;

    public NodeShadow() {
    }

    public NodeShadow(String name, DataLocation location, double speed, double direction, double range) {
        this.name = name;
        this.location = location;
        this.speed = speed;
        this.direction = direction;
        this.range = range;
    }

    public double getRange() {
        return range;
    }

    /**
     * @param range if it was null, {@link #UNDEFINED_RANGE} will be used. Else the range will be setted to
     *              min ({@link #MaxRange}, <tt>range</tt>)
     */
    public void setRange(Double range) {
        if (range == null) {
            this.range = UNDEFINED_RANGE;
        } else {
            this.range = Math.min(range, MaxRange);
        }
    }

    public boolean isActive() {
        return true;
    }

    /**
     * assumes that the name is integer parsable.
     * usses name and some integer number to generate a stable unique value for this node <br/>
     * note that two node that has same name will have same hashcode too.
     *
     * @return
     */
    public int hashCode() {
        return (new Integer(name) * 12345) % 2345;
    }

    public String toString() {
        return name;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIntName() {
        return Integer.parseInt(name);
    }

    public DataLocation getLocation() {
        return location;
    }

    public void setLocation(DataLocation location) {
        this.location = location;
    }

    public boolean equals(Object obj) {
        assert obj instanceof NodeShadow;
        return this.name.equals(((NodeShadow) obj).name);
    }

    /**
     * draws a line from <tt>lastLoc</tt> location to the current location<br/>
     * note that it assumes the name is integer parsable
     *
     * @param g
     * @param lastLoc
     */
    public void paintFootPrint(Graphics2D g, DataLocation lastLoc) {
        g.setColor(NodeColors[Integer.parseInt(name) %
                NodeColors.length]);
        DataLocation loc = getLocation();
        g.drawLine((int) lastLoc.getX(), (int) lastLoc.getY(), (int) loc.getX(), (int) loc.getY());
    }

    /**
     * @return name, location x, y, speed, direction, and range
     */
    public java.util.List print() {
        java.util.List toWrite = new LinkedList();
        toWrite.add(name);
        toWrite.addAll(getLocation().print());
        toWrite.addAll(Arrays.asList(new Object[]{speed, direction, range}));
        return toWrite;

    }

    public List<String> getLabels() {
        return traceLabels;
    }

    public void paintTransition(Graphics2D g) {
        Color lastColor = g.getColor();
        g.setColor(Color.green);
        DataLocation loc = getLocation();
        g.drawLine(loc.getX(), loc.getY(),
                loc.getX() + (int) (5 * speed * Math.cos(direction)), loc.getY() + (int) (5 * speed * Math.sin(direction)));
        g.setColor(lastColor);
    }
}
