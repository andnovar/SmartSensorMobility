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

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 22, 2009
 * Time: 12:54:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapHandle extends ParameterableImplement {
    ;

    public static final int SIZE = 10;

    private int x, y;
    private int oldX, oldY;
    private boolean selected = false;
    private boolean dragging = false;
    private boolean enable = true;
    private HandleShape shape;
    private static final HandleShape DEFAULT_HANDLE_SHAPE = new DefaultHandleShape();

    public MapHandle(int x, int y) {
        this(x, y, true, DEFAULT_HANDLE_SHAPE);
    }

    public MapHandle(int x, int y, boolean enable) {
        this(x, y, enable, DEFAULT_HANDLE_SHAPE);
    }

    public MapHandle(int x, int y, boolean enable, HandleShape shape) {
        this.x = x;
        this.y = y;
        this.enable = enable;
        this.shape = shape;
    }


    public JToolTip createToolTip() {
        return new JToolTip();
    }

    public String getToolTipText(MouseEvent event) {
        Point mousePoint = event.getPoint();
        /*if (mousePoint.getX()<x+SIZE&& mousePoint.getX()>x-SIZE
                && mousePoint.getY()<y+SIZE && mousePoint.getY()>y-SIZE)
            return super.getToolTipText(event);
        else {
            return null;
        }*/
        return null;
    }

    public Dimension getPreferredSize() {
        return new Dimension(SIZE, SIZE);
    }

    public void updateToolTip() {
        StringBuffer sb = new StringBuffer("X=");
        sb.append(x).append(" Y=").append(y);
//        setToolTipText(sb.toString());
    }

    public Point getToolTipLocation(MouseEvent event) {
        int center = SIZE / 2;
        return new Point(center, center);
    }

    public void paintComponent(Graphics g) {
        shape.paint(this, g);
    }

    public boolean isInRange(int x, int y) {
        return x >= this.x - SIZE / 2 && x < this.x + SIZE / 2 &&
                y >= this.y - SIZE / 2 && y < this.y + SIZE / 2;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setXY(int x, int y) {
        oldX = this.x;
        oldY = this.y;
        this.x = x;
        this.y = y;
    }

    public void setXY2(int x, int y) {
        oldX = this.x;
        oldY = this.y;
        this.x = x - SIZE / 2;
        this.y = y - SIZE / 2;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        if (enable) this.dragging = dragging;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setShape(HandleShape shape) {
        this.shape = shape;
    }

    public void revert() {
        x = oldX;
        y = oldY;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {

    }

    public Map<String, Parameter> getParameters() {
        return new HashMap<String, Parameter>();
    }

    public String toString() {
        return "X: " + x + " Y: " + y;
    }

    protected static class DefaultHandleShape extends HandleShape {

        public void paint(MapHandle handle, Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Color lastColor = g2.getColor();
            g2.setColor(Color.red);
            g2.fillRect(handle.getX() - MapHandle.SIZE / 2, handle.getY() - MapHandle.SIZE / 2, MapHandle.SIZE, MapHandle.SIZE);

            if (handle.isSelected()) {
                g2.setColor(Color.black);
                g2.drawRect(handle.getX() - MapHandle.SIZE / 2, handle.getY() - MapHandle.SIZE / 2, MapHandle.SIZE, MapHandle.SIZE);
            }
            g2.setColor(lastColor);
        }
    }
}
