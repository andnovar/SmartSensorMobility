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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.multi;

import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.MapHandleSupport;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.HandleShape;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.HasConfigToolbar;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 27, 2009
 * Time: 8:29:37 AM <br/>
 * A handle group that can manage multiple internal map handle groups plus a master one
 */
public class MultiMapHandleGroup extends MapHandleGroup implements HasConfigToolbar {
    protected List<SupporterHandleGroup> mhps = new LinkedList<SupporterHandleGroup>();
    protected SupporterHandleGroup masterHandleSupport;
    protected JToolBar configToolBar;
    //Combo box in my special toolbar
    protected JComboBox selectedMhpCmb;

    protected static final OffsetHandleShape OFFSET_HANDLE_SHAPE = new OffsetHandleShape();

    public MultiMapHandleGroup( MapHandle size,
                               MapHandleGroup masterHandleGroup, MapHandleSupport masterHandleSupport) {
        super(new ArrayList<MapHandle>(), size);
        this.masterHandleSupport = new SupporterHandleGroup(masterHandleSupport, masterHandleGroup, new MapHandle(0, 0));
        this.configToolBar = new JToolBar();
        //       mhps.add(supportGroup);
    }

    public List<MapHandle> getHandles() {
        List<MapHandle> handles = new LinkedList<MapHandle>(masterHandleSupport.getHandleGroup().getHandles());
        for (SupporterHandleGroup smhp : mhps) {
            /*MapHandle mapOrigin = smhp.handleGroup.getMapOrigin();
            mapOrigin.setEnable(true);
            handles.add(mapOrigin);*/
            handles.add(smhp.getHandleGroup().getSize());
            handles.add(smhp.offset);
            handles.addAll(smhp.getHandleGroup().getHandles());
        }
        return handles;
    }

    public MapHandleGroup getMasterHandleGroup() {
        return masterHandleSupport.getHandleGroup();
    }

    public void setHandles(List<MapHandle> handles) {
        throw new RuntimeException("This map handle do not support set handle method. Use addMapHandleGroup");
        // super.setHandles(handles);
    }

    public void addMapHandleGroup(MapHandleGroup mhp, MapHandleSupport support, MapHandle offset) {
        mhps.add(new SupporterHandleGroup(support, mhp, offset));
        offset.setShape(OFFSET_HANDLE_SHAPE);
    }

    public void removeMapHandleGroup(MapHandleSupport mhpSupport) {
        int i = 0;
        for (SupporterHandleGroup mhp : mhps) {
            if (mhp.getSupporter().equals(mhpSupport)) {
                mhps.remove(i);
                break;
            }
            i++;
        }
    }

    /**
     * consider master handle group
     *
     * @param mhpI
     */
    public void removeMapHandleGroup(int mhpI) {
        mhps.remove(mhpI);
    }

    public MapHandleGroup getMapHandleGroup(int mhpI) {
        return mhps.get(mhpI).getHandleGroup();
    }

    public MapHandle getOffset(int mhpI) {
        return mhps.get(mhpI).offset;
    }

    public MapHandleSupport getSupporterFor(int mhpI) {
        return mhps.get(mhpI).supporter;
    }

    public int getMapHandleGroupSize() {
        return mhps.size();
    }

    public MapHandle getInRangeMapHandle(int x, int y) {
        MapHandle handle = null;
        for (SupporterHandleGroup mhp : mhps) {
            MapHandle offset = mhp.getOffset();
            if (offset.isInRange(x, y)) return offset;
            handle = mhp.getHandleGroup().getInRangeMapHandle(x - offset.getX(), y - offset.getY());
            if (handle != null) return handle;
        }
        handle = masterHandleSupport.getHandleGroup().getInRangeMapHandle(x, y);
        return handle;
    }

    /**
     * first tries to find a match in submaphandlegroups then its offset and at last the master map handle gourp
     *
     * @param x
     * @param y
     * @param handle
     */
    public void dragHandle(int x, int y, MapHandle handle) {
        //TODO: it is too inefficient
        //if the handle is in internal mhps change x,y
        for (SupporterHandleGroup mhp : mhps) {
            MapHandle offset = mhp.getOffset();

            MapHandleGroup mapHandleGroup = mhp.getHandleGroup();
            if (mapHandleGroup.getSize() == handle) {
                handle.setXY2(x - offset.getX(), y - offset.getY());
                return;
            }
            if (mapHandleGroup.getHandles().contains(handle)) {
                mapHandleGroup.dragHandle(x - offset.getX(), y - offset.getY(), handle);
                return;
            }
            if (handle == offset) {
                handle.setXY2(x, y);
                return;
            }
        }
        // it is in mastermaphandlegroup
        handle.setXY2(x, y);
    }

    public void paintMapHandles(Graphics2D g2) {
        masterHandleSupport.getHandleGroup().paintMapHandles(g2);

        for (SupporterHandleGroup mhp : mhps) {
            MapHandle offset = mhp.getOffset();
            offset.paintComponent(g2);
            g2.translate(+offset.getX(), +offset.getY());
            mhp.getHandleGroup().paintMapHandles(g2);
            g2.translate(-offset.getX(), -offset.getY());
        }
    }

    public JToolBar getConfigToolbar() {
        configToolBar = new JToolBar();
        java.util.List<SupporterHandleGroup> items = new LinkedList<SupporterHandleGroup>();
        if (masterHandleSupport.getHandleGroup().isAddPoint())
            items.add(masterHandleSupport);
        for (SupporterHandleGroup mhps : this.mhps) {
            if (mhps.getHandleGroup().isAddPoint()) {
                items.add(mhps);
            }
        }
        selectedMhpCmb = new JComboBox(items.toArray());
        selectedMhpCmb.setEnabled(items.size() > 0);
        configToolBar.setFloatable(false);
        configToolBar.setFocusable(false);
        configToolBar.add(new JLabel("Add handle to:"));
        configToolBar.add(selectedMhpCmb);
        return configToolBar;
    }

    public boolean isAddPoint() {
        if (masterHandleSupport.getHandleGroup().isAddPoint()) return true;
        for (SupporterHandleGroup mhp : mhps) {
            if (mhp.handleGroup.isAddPoint()) return true;
        }
        return false;
    }

    public MapHandle createMapHandle(int x, int y) {
        SupporterHandleGroup selectedshg = (SupporterHandleGroup) selectedMhpCmb.getSelectedItem();
        return selectedshg.getHandleGroup().createMapHandle(x - selectedshg.offset.getX(), y - selectedshg.offset.getY());
    }

    public void removeHandle(MapHandle handle) {
        for (SupporterHandleGroup mhp : mhps) {
            mhp.getHandleGroup().removeHandle(handle);
        }
    }

    protected class SupporterHandleGroup {
        MapHandleSupport supporter;
        MapHandleGroup handleGroup;
        MapHandle offset;

        public SupporterHandleGroup(MapHandleSupport supporter, MapHandleGroup handleGroup, MapHandle offset) {
            this.supporter = supporter;
            this.handleGroup = handleGroup;
            this.offset = offset;
        }

        public MapHandleSupport getSupporter() {
            return supporter;
        }

        public MapHandleGroup getHandleGroup() {
            return handleGroup;
        }

        public MapHandle getOffset() {
            return offset;
        }

        public String toString() {
            return supporter.toString();
        }

    }

    protected static class OffsetHandleShape extends HandleShape {
        public void paint(MapHandle handle, Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Color lastColor = g2.getColor();
            g2.setColor(Color.black);
            int widthHeight = (int) (MapHandle.SIZE / 4.0) + 1;
            g2.fillRect(handle.getX() - MapHandle.SIZE / 2, handle.getY() - widthHeight / 2, MapHandle.SIZE, widthHeight);
            g2.fillRect(handle.getX() - widthHeight / 2, handle.getY() - MapHandle.SIZE / 2, widthHeight, MapHandle.SIZE);

            if (handle.isSelected()) {
                g2.setColor(Color.black);
                g2.drawRect(handle.getX() - MapHandle.SIZE / 2, handle.getY() - MapHandle.SIZE / 2, MapHandle.SIZE, MapHandle.SIZE);
            }
            g2.setColor(lastColor);
        }
    }
}
