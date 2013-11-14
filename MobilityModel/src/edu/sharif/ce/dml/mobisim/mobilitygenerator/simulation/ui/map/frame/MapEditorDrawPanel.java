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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.frame;

import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.MapHandleSupport;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.ConfigPanel2;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2009
 * Time: 10:46:30 AM
 */
class MapEditorDrawPanel extends JPanel {
    private MapHandleSupport mhpSupport;
    private MapHandle selectedHandle = null;
    private MapHandle draggingHandle = null;
    private JPanel handleConfigPanel;
    private ConfigPanel2 currentConfigPanel;

    private HandleManamgementState state;

    private DrawCanvas drawCanvas;

    private MapHandle origin;

    ////////////////Dragging the window
    private Point dragStrat;
    private MapHandle dragOrigin=new MapHandle(0,0);
    private boolean isMapDragging ;
    /////////////////////////////

    enum HandleManamgementState {
        MOVE,
        ADD,
        DELETE
    }

    private float zoomFactor = 1f;
    private MapEditorDrawPanel thisPanel;

    private JLabel statusTxt;
    private MapHandleGroup mhp;

    private JButton handleUpdateBtn = new JButton("Update");

    private final int STATUS_INFO = 0;
    private final int STATUS_ERROR = 1;

    public MapEditorDrawPanel(MapHandleSupport mhs, HandleManamgementState state2, JPanel handleConfigPanel2, MapHandleGroup mhp1) {
        this.mhpSupport = mhs;
        this.handleConfigPanel = handleConfigPanel2;
        mhp = mhp1;
        this.state = state2;
        setOpaque(true);
        setLayout(new BorderLayout());
        thisPanel = this;
        statusTxt = new JLabel();
        this.add(statusTxt, BorderLayout.PAGE_END);
        origin = new MapHandle(0, mhp.getSize().getY());
        drawCanvas = new DrawCanvas();
        this.add(drawCanvas, BorderLayout.CENTER);
        MouseAdapter ma = new HandleMouseAdapter();

        this.addMouseListener(ma);
        this.addMouseMotionListener(ma);

        handleUpdateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentConfigPanel.updateValues();
                if (!mhpSupport.validateHandles(mhp)) {
                    selectedHandle.revert();
                    JOptionPane.showMessageDialog(thisPanel, "The parameter cannot be changed", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
                thisPanel.repaint();
            }
        });
    }

    public void setZoomFactor(float zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    public void fillMap() {
        try {
            mhpSupport.fillFromHandles(mhp);
        } catch (InvalidParameterInputException e) {
            e.showMessage(this);
        }
    }

    public void updateFromMap() {
        mhp = mhpSupport.getHandles();
        repaint();
    }

    public void setMapDragging(boolean mapDragging) {
        isMapDragging = mapDragging;
    }

    public void setState(HandleManamgementState state) {
        this.state = state;
    }

    private class HandleMouseAdapter extends MouseAdapter {

        public void mouseDragged(MouseEvent e2) {
            super.mousePressed(e2);
            if (isMapDragging) {
                origin.setXY((int)(-(e2.getX()-dragStrat.getX())+dragOrigin.getX()),
                        (int)(e2.getY()-dragStrat.getY()+dragOrigin.getY()));
                thisPanel.repaint();
            } else {
                int h = origin.getY();
                int x = e2.getX() + origin.getX();
                int y = h - e2.getY();
                if (selectedHandle != null && selectedHandle.isEnable()) {
                    if (selectedHandle.equals(origin) || selectedHandle.equals(mhp.getInRangeMapHandle(x, y))) {
                        draggingHandle = selectedHandle;
                    }
                    if (draggingHandle != null) {
                        draggingHandle.setDragging(true);
                        mhp.dragHandle(x, y, draggingHandle);
                        boolean valid = mhpSupport.validateHandles(mhp);
                        if (!valid) {
                            draggingHandle.revert();
                            setStatusTxt("Position of a handle is invalid, try moving it or set it using the config panel in the right", STATUS_ERROR);
                        } else {
                            setStatusTxt("X: " + x + ", Y: " + y + " (" + "X: " + draggingHandle.getX() + ", Y: " + draggingHandle.getY() + ")", STATUS_INFO);
                        }
                        thisPanel.repaint();
                    }
                }
            }
        }

        public void mousePressed(MouseEvent e2) {
            super.mousePressed(e2);
            if (isMapDragging) {
                dragStrat = e2.getPoint();
                dragOrigin.setXY(origin.getX(),origin.getY());
            } else {
                int h = origin.getY();
                int x = e2.getX() + origin.getX();
                int y = h - e2.getY();

                /*java.util.List<MapHandle> handles = mhp.getPaintableMapHandles();
                int i = 0;
                for (MapHandle mapHandle : handles) {
                    System.out.println("handle " + i + " = " + mapHandle.getX() + " : " + mapHandle.getY());
                    i++;
                }*/

                MapHandle handle = mhp.getInRangeMapHandle(x, y);
                if (handle != null) {
                    handle.setSelected(true);
                    setSelectedHandle(handle);
                } else {
                    if (selectedHandle != null) {
                        selectedHandle.setSelected(false);
                        selectedHandle = null;
                        if (draggingHandle != null)
                            draggingHandle.setDragging(false);
                        draggingHandle = null;
                    }
                    if (state == HandleManamgementState.ADD) {
                        MapHandle newHandle = mhp.createMapHandle(x, y);
                        if (!mhpSupport.validateHandles(mhp)) {
                            mhp.removeHandle(newHandle);
                        }
                    }
                }
                thisPanel.repaint();
            }
        }

        public void mouseMoved(MouseEvent e2) {
            int h = origin.getY();
            int x = e2.getX() + origin.getX();
            int y = h - e2.getY();
            setStatusTxt("X: " + x + ", Y: " + y, STATUS_INFO);
        }
    }

    private void setStatusTxt(String text, int type) {
        if (type == STATUS_ERROR) {
            statusTxt.setForeground(Color.red);
        } else if (type == STATUS_INFO) {
            statusTxt.setForeground(Color.black);
        }
        statusTxt.setText(text);
    }

    private void setSelectedHandle(MapHandle handle) {
        if (selectedHandle != null) {
            selectedHandle.setSelected(false);
            if (!handle.equals(selectedHandle)) {
                if (draggingHandle!=null){
                    draggingHandle.setDragging(false);
                }
                draggingHandle = null;
            }
        }
        if (state == HandleManamgementState.MOVE) {
            selectedHandle = handle;
            handleConfigPanel.removeAll();
            Box v = Box.createVerticalBox();
            handleConfigPanel.add(v);
            currentConfigPanel = new ConfigPanel2(selectedHandle, false, true);
            v.add(currentConfigPanel);
            v.add(Box.createVerticalStrut(10));
            v.add(handleUpdateBtn);
        } else if (state == HandleManamgementState.DELETE) {
            mhp.removeHandle(handle);
        }
    }


    private class DrawCanvas extends JPanel {


        private DrawCanvas() {
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            final java.util.List<MapHandle> handles = mhp.getPaintableMapHandles();
            //draw model and its mhpSupport and nodes and ...
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.scale(zoomFactor, zoomFactor);
            g2.transform(new AffineTransform(1, 0, 0, -1, 0, 0));
            g2.translate(-origin.getX(), -origin.getY());
            mhpSupport.paintUsingHandles(g2, mhp);
            mhp.paintMapHandles(g2);
            g2.translate(+origin.getX(), +origin.getY());
            g2.transform(new AffineTransform(1, 0, 0, -1, 0, 0));
            g2.scale(1 / zoomFactor, 1 / zoomFactor);
        }
    }
}

