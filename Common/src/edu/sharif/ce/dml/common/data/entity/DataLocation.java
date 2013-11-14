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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 22, 2006
 * Time: 9:03:23 PM
 * <br/>represent a location in simulation space. note that it is an Integer version of it.
 */
public class DataLocation implements Tracable {
    private int x,y;

    /**
     * create a new location from x and y coordinations.
     * @param x
     * @param y
     */
    public DataLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * creates a new location from loc Location coordinations
     * @param loc
     */
    public DataLocation(DataLocation loc) {
        pasteCoordiantion(loc);
    }

    public int getX() {
        return x;
    }

    /**
     *
     * @return a unique int for each location in space. note that two location with same coordination
     *  will have same hashcode.
     */
    public int hashCode() {
        return Integer.parseInt(new StringBuilder().append((int)x).append((int)y).append((int)getLength(0, 0)).toString());
    }

    public void setX(int x) {
        this.x = x;
    }

    /**
     *
     * @param x
     * @param y
     * @return distance from this location to location with x and y coordinations
     */
    public double getLength(double x,double y){
        return Math.sqrt(Math.pow(x-this.x,2)+Math.pow(y-this.y,2));
    }
    public double getLength(DataLocation dest){
        return Math.sqrt(Math.pow(dest.x-this.x,2)+Math.pow(dest.y-this.y,2));
        //for performance
//        return getLength(dest.getX(), dest.getY());
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     *
     * @return X: xcoordination, Y: ycoordination
     */
    public String toString() {
        return "X: "+this.getX()+",Y: "+this.getY();
    }

    /**
     * pastes coordination of loc location to this object. so this.equals(loc) will be True.
     * @param loc
     */
    public void pasteCoordiantion(DataLocation loc){
        pasteCoordiantion(loc.getX(),loc.getY());
    }

    /**
     * sets coordination of this location object according to x and y coordination.
     * @param x
     * @param y
     */
    public void pasteCoordiantion(int x,int y){
        this.setX(x);
        this.setY(y);
    }

    /**
     *
     * @param o
     * @return this.getLength(o) ==0
     */
    public boolean equals(Object o) {
        return this.getLength((DataLocation)o)==0;
    }

    public static Comparator<DataLocation> getXComparator (){
        return new xComparator();
    }

    public static Comparator<DataLocation> getYComparator (){
        return new yComparator();
    }

    /**
     *
     * @return x and y coordination
     */
    public List print() {
        return Arrays.asList(new Object[]{getX(),getY()});
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getLabels() {
        return Arrays.asList("PositionX","PositionY");
    }

    /**
     * moves the coordination according to the origin coordination<br/>
     * calls {@link #transform(int, int)}
     * @param origin
     */
    public void transform(DataLocation origin){
        transform(origin.getX(),origin.getY());
    }

    public void transform(int x, int y){
        this.x+=x;
        this.y+=y;
    }
    /**
     * comparing according to x coordination.
     */
    private static class xComparator implements Comparator <DataLocation> {
        public int compare(DataLocation o1, DataLocation o2) {
            return (int)(o1.getX()-o2.getX());
        }
    }

    /**
     * comparing according to y coordination
     */
    private static class yComparator implements Comparator <DataLocation> {
        public int compare(DataLocation o1, DataLocation o2) {
            return (int)(o1.getY()-o2.getY());
        }
    }
}
