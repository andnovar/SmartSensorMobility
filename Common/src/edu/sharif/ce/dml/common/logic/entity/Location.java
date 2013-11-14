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

package edu.sharif.ce.dml.common.logic.entity;

import edu.sharif.ce.dml.common.data.entity.DataLocation;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 22, 2006
 * Time: 9:03:23 PM <br/>
 * represents a location in simulation space with two coordinates.
 */
public class Location {
    public static final float COORDINATION_PRECISION = 10000.0f;

    private double x, y;

    /**
     * create a new location from x and y coordination.
     *
     * @param x
     * @param y
     */
    public Location(double x, double y) {
        setX(x);
        setY(y);
    }

    /**
     * creates a new location from loc Location coordination
     *
     * @param loc
     */
    public Location(Location loc) {
        pasteCoordination(loc);
    }

    public Location(DataLocation location) {
        pasteCoordination(location);
    }

    public double getX() {
        return x;
    }

    public int hashCode() {
        int result;
        long temp;
        temp = getX() != +0.0d ? Double.doubleToLongBits(getX()) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = getY() != +0.0d ? Double.doubleToLongBits(getY()) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public void setX(double x) {
        this.x = Math.round(x * COORDINATION_PRECISION) / COORDINATION_PRECISION;
    }

    /**
     * @param x
     * @param y
     * @return distance from this location to location with x and y coordination
     */
    public double getLength(double x, double y) {
        return Math.sqrt(Math.pow(x - this.getX(), 2) + Math.pow(y - this.getY(), 2));
    }

    public double getLength(Location dest) {
        return getLength(dest.getX(), dest.getY());
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = Math.round(y * COORDINATION_PRECISION) / COORDINATION_PRECISION;
    }

    public String toString() {
        return "X: " + this.getX() + ", Y: " + this.getY();
    }

    /**
     * pastes coordination of loc location to this object. so this.equals(loc) will be True.
     *
     * @param loc
     */
    public void pasteCoordination(Location loc) {
        pasteCoordination(loc.getX(), loc.getY());
    }

    /**
     * sets coordination of this location object according to x and y coordination.
     *
     * @param x
     * @param y
     */
    public void pasteCoordination(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;

        if (Double.compare(location.x, x) != 0) return false;
        if (Double.compare(location.y, y) != 0) return false;

        return true;
    }

    public static Comparator<Location> getXComparator() {
        return new xComparator();
    }

    public static Comparator<Location> getYComparator() {
        return new yComparator();
    }

    public List print(Location offset) {
        return Arrays.asList((int) (x + offset.getX()), (int) (y + offset.getY()));
    }

    public void translate(double x, double y) {
        setX(this.x + x);
        setY(this.y + y);
    }

    public void pasteCoordination(DataLocation location) {
        this.setX(location.getX());
        this.setY(location.getY());
    }

    public void rotate(double rotation, int rotationX, int rotationY) {
        translate(-rotationX, -rotationY);
        double tempX=x;
        setX(Math.cos(rotation) * x - Math.sin(rotation) * y);
        setY(Math.sin(rotation) * tempX + Math.cos(rotation) * y);
        translate(rotationX, rotationY);
    }

    /**
     * comparing according to x coordination.
     */
    private static class xComparator implements Comparator<Location> {
        public int compare(Location o1, Location o2) {
            return (int) (o1.getX() - o2.getX());
        }
    }

    /**
     * comparing according to y coordination
     */
    private static class yComparator implements Comparator<Location> {
        public int compare(Location o1, Location o2) {
            return (int) (o1.getY() - o2.getY());
        }
    }

    public static double calculateRadianAngle(Location l1, Location l2) {
        return calculateRadianAngle(l2.getX() - l1.getX(), l2.getY() - l1.getY());
    }

    public static double calculateRadianAngle(double x, double y) {
        if (x == 0 && y == 0) {
            return 0;
        }
        double tempAngle = Math.atan(y / x);
        if (x > 0 && y < 0) {
            return tempAngle + 2 * Math.PI;
        }
        if (x < 0) {
            return tempAngle + Math.PI;
        }
        return tempAngle;
    }
}
