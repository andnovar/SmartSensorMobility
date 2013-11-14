package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.obstaclemap;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleArrayParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.MapHandleSupport;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.MyGraphics2D;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.HandleShape;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: 12/29/10
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Obstacle implements MapHandleSupport {
    private String name;
    private Location offset = new Location(0, 0);
    protected double rotation;

    protected static final RotationMapHandleShape ROTATION_MAP_HANDLE_SHAPE = new RotationMapHandleShape();

    protected abstract void paint2(Graphics2D g);

    protected Obstacle() {
    }

    protected Force isHitBorder(Location start1, Location end1) {
        //get new coordination based on the offset
        int w = getWidth() / 2;
        int h = getHeight() / 2;

        Location start = new Location(start1);
        Location end = new Location(end1);
        start.translate(-offset.getX(), -offset.getY());
        end.translate(-offset.getX(), -offset.getY());
        start.rotate(-rotation, w, h);
        end.rotate(-rotation, w, h);
        Force hit = hit(start, end);
        if (!hit.isValid()) {
            return hit;
        }

        //get back the force into the real coordination
        hit.rotate(rotation, w, h);
        hit.translate(offset.getX(), offset.getY());
        return hit;
    }

    protected abstract Force hit(Location start, Location end);

    public abstract int getWidth();

    public abstract int getHeight();

    protected abstract void paintUsingHandles2(Graphics2D g, MapHandleGroup mhp);

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Object o) {
        return name.compareTo(o.toString());
    }

    @Override
    public String toString() {
        return name;
    }

    public Location getOffset() {
        return new Location(offset);
    }

    public void setOffset(Location loc) {
        offset.pasteCoordination(loc);
    }

    public void paint(Graphics2D g) {
        g.translate(offset.getX(), offset.getY());
        MyGraphics2D.getInstance().drawString(toString(), 0, 0, g);
        g.rotate(rotation, getWidth() / 2, getHeight() / 2);
        paint2(g);
        g.rotate(-rotation, getWidth() / 2, getHeight() / 2);
        g.translate(-offset.getX(), -offset.getY());
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("rotation", new DoubleParameter("rotation", rotation));
        parameters.put("offset", new DoubleArrayParameter
                ("offset", new double[]{offset.getX(), offset.getY()}));
        return parameters;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        rotation = ((DoubleParameter) parameters.get("rotation")).getValue();
        double[] iap = (double[]) ((DoubleArrayParameter) parameters.get("offset")).getValue();
        offset.setX(iap[0]);
        offset.setY(iap[1]);
    }

    public MapHandleGroup getHandles() {
        MapHandle size = new MapHandle(getWidth(), getHeight());
        double radius = Math.sqrt(Math.pow(size.getX() / 2, 2) + Math.pow(size.getY() / 2, 2));
        MapHandle rotationHandle = new RotationMapHandle((int) (radius * Math.cos(rotation)) + size.getX() / 2,
                (int) (radius * Math.sin(rotation)) + size.getY() / 2, rotation);
        return new MapHandleGroup(new ArrayList<MapHandle>(Arrays.asList(rotationHandle)), size);
    }

    public void fillFromHandles(MapHandleGroup mhp) throws InvalidParameterInputException {
        MapHandle rotationHandle = mhp.getHandles().get(0);
        MapHandle size = mhp.getSize();
        rotation = Location.calculateRadianAngle(rotationHandle.getX() - size.getX() / 2, rotationHandle.getY() - size.getY() / 2);
        while (rotation < 0) {
            rotation += Math.PI * 2;
        }
    }

    public void paintUsingHandles(Graphics2D g, MapHandleGroup mhp) {
        MapHandle rotationHandle = mhp.getHandles().get(0);
        MapHandle size = mhp.getSize();
        double rotationAngle = Location.calculateRadianAngle(rotationHandle.getX() - size.getX() / 2, rotationHandle.getY() - size.getY() / 2);
        //rotationAngle -= Math.PI / 2;
        g.rotate(rotationAngle, size.getX() / 2, size.getY() / 2);
        paintUsingHandles2(g, mhp);
        g.rotate(-rotationAngle, size.getX() / 2, size.getY() / 2);
    }

    public boolean validateHandles(MapHandleGroup mhp) {
        //update rotation based on the size

        RotationMapHandle rotationHandle = (RotationMapHandle) mhp.getHandles().get(0);
        MapHandle size = mhp.getSize();
        if (rotationHandle.isDragging()) {
            double rotationAngle = Location.calculateRadianAngle(rotationHandle.getX() - size.getX() / 2, rotationHandle.getY() - size.getY() / 2);
            rotationHandle.setRotation(rotationAngle);

        }
        double radius = Math.sqrt(Math.pow(size.getX() / 2, 2) + Math.pow(size.getY() / 2, 2));
        double rotationAngle = rotationHandle.getRotation();
        rotationHandle.setXY((int) (radius * Math.cos(rotationAngle)) + size.getX() / 2,
                (int) (radius * Math.sin(rotationAngle)) + size.getY() / 2);
        boolean valid = true;
        valid = valid && size.getX() >= 0 && size.getY() >= 0;
        return valid;
    }

    class RotationMapHandle extends MapHandle {
        private double rotation;

        public RotationMapHandle(int x, int y, double rotation) {
            super(x, y, true, ROTATION_MAP_HANDLE_SHAPE);
            this.rotation = rotation;
        }

        public double getRotation() {
            return rotation;
        }

        public void setRotation(double rotation) {
            this.rotation = rotation;
        }


    }

    protected static class RotationMapHandleShape extends HandleShape {

        @Override
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

    public boolean isInside(Location loc) {
        Location loc2 = new Location(loc);
        loc2.translate(-offset.getX(), -offset.getY());
        loc2.rotate(rotation, getWidth() / 2, getHeight() / 2);
        return checkIsInside(loc2);
    }

    protected abstract boolean checkIsInside(Location loc);

}

class Force {
    private double x;
    private double y;

    private Location hitPoint;
    private boolean valid = false;

    Force() {
    }

    Force(Location hitPoint, double x, double y) {
        this.hitPoint = hitPoint;
        this.y = y;
        this.x = x;
        valid = true;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Location getHitPoint() {
        return hitPoint;
    }

    public boolean isValid() {
        return valid;
    }

    public void add(Force force) {
        if ((int) force.getHitPoint().getLength(getHitPoint()) == 0) {
            x += force.getX();
            y += force.getY();
        }
    }

    public void rotate(double rotation, int rotationX, int rotationY) {
        Location loc = new Location(x, y);
        loc.rotate(rotation, 0, 0);
        x = loc.getX();
        y = loc.getY();
        hitPoint.rotate(rotation, rotationX, rotationY);

    }

    public void translate(double translateX, double translateY) {

        hitPoint.translate(translateX, translateY);
    }
}
