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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 23, 2009
 * Time: 3:10:53 PM
 * <br/> A default handle group that contains some handles and can be set to have variable number of handles
 */
public class MapHandleGroup {
    protected List<MapHandle> handles;
    private MapHandle size;
    private boolean addPoint;

    protected static final SizeHandleShape SIZE_HANDLE_SHAPE = new SizeHandleShape();

    public MapHandleGroup(List<MapHandle> handles, MapHandle size) {
        this(handles, size, false);
    }

    public MapHandleGroup(List<MapHandle> handles, MapHandle size, boolean addPoint) {
        this.handles = handles;
        this.size = size;
        this.addPoint = addPoint;
        this.size.setShape(SIZE_HANDLE_SHAPE);
    }

    public List<MapHandle> getHandles() {
        return handles;
    }

    public boolean isAddPoint() {
        return addPoint;
    }


    public void setHandles(List<MapHandle> handles) {
        this.handles = handles;
    }

    public void setAddPoint(boolean addPoint) {
        this.addPoint = addPoint;
    }

    public MapHandle createMapHandle(int x, int y) {
        MapHandle mapHandle = new MapHandle(x, y);
        handles.add(mapHandle);
        return mapHandle;
    }


    public MapHandle getSize() {
        return size;
    }

    public java.util.List<MapHandle> getPaintableMapHandles() {
        java.util.List<MapHandle> handles = new LinkedList<MapHandle>();
        handles.add(getSize());
        handles.addAll(getHandles());
        return Collections.unmodifiableList(handles);
    }

    /**
     *
     * @param handle it may be not really in this group so you must check if it is not in yours ignore it
     */
    public void removeHandle(MapHandle handle) {
        handles.remove(handle);
    }

    public void Movehandles(MapHandle offset) {
        for (MapHandle handle : handles) {
            handle.setXY(handle.getX() + offset.getX(), handle.getY() + offset.getY());
        }
    }

    public MapHandle getInRangeMapHandle(int x, int y) {
        if (size.isInRange(x, y)) return size;
        for (MapHandle handle : handles) {
            if (handle.isInRange(x, y)) {
                return handle;
            }
        }
        return null;
    }

    public void paintMapHandles(Graphics2D g2) {
        size.paintComponent(g2);

        for (MapHandle mapHandle : handles) {
            mapHandle.paintComponent(g2);
        }
    }

    public void dragHandle(int x, int y, MapHandle handle) {
        handle.setXY2(x, y);
    }

    protected static class SizeHandleShape extends HandleShape {

        public void paint(MapHandle handle, Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Color lastColor = g2.getColor();
            g2.setColor(Color.blue);
            g2.fillRect(handle.getX() - MapHandle.SIZE / 2, handle.getY() + MapHandle.SIZE / 4, MapHandle.SIZE, (int)(MapHandle.SIZE/4.0)+1);
            g2.fillRect(handle.getX() + MapHandle.SIZE / 4, handle.getY() - MapHandle.SIZE / 2, (int)(MapHandle.SIZE/4.0)+1, MapHandle.SIZE);
            if (handle.isSelected()) {
                g2.setColor(Color.black);
                g2.drawRect(handle.getX() - MapHandle.SIZE / 2, handle.getY() - MapHandle.SIZE / 2, MapHandle.SIZE, MapHandle.SIZE);
            }
            g2.setColor(lastColor);
        }
    }


}
