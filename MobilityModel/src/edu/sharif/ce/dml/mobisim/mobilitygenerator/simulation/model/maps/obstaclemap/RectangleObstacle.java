package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.obstaclemap;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;

import java.awt.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: 12/29/10
 * Time: 12:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class RectangleObstacle extends Obstacle {

    int width, height;

    @Override
    public void paint2(Graphics2D g) {
        g.drawRect(0, 0, (int) width, (int) height);
    }


    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void paintUsingHandles2(Graphics2D g, MapHandleGroup mhp) {
        MapHandle size = mhp.getSize();
        g.drawRect(0, 0, size.getX(), size.getY());
    }

    public void fillFromHandles(MapHandleGroup mhp) throws InvalidParameterInputException {
        super.fillFromHandles(mhp);
        MapHandle size = mhp.getSize();
        width = size.getX();
        height = size.getY();
    }

    @Override
    protected boolean checkIsInside(Location loc) {
        return loc.getX() > 0 && loc.getX() < width
                && loc.getY() > 0 && loc.getY() < height;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        width = ((IntegerParameter) parameters.get("width")).getValue();
        height = ((IntegerParameter) parameters.get("height")).getValue();
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("width", new IntegerParameter("width", width));
        parameters.put("height", new IntegerParameter("height", height));
        return parameters;
    }


    @Override
    protected Force hit(Location start, Location end) {
        Location enterPoint = new Location(0, 0);
        //compute dir
        float normalizationFactor = (float) end.getLength(start);
        if (normalizationFactor == 0) {
            return new Force();
        }
        float[] dir = new float[]{(float) ((end.getX() - start.getX()) / normalizationFactor),
                (float) ((end.getY() - start.getY()) / normalizationFactor)};
        boolean hit = rayAABBIntersect(start, dir, normalizationFactor, new float[]{0, 0},
                new float[]{getWidth(), getHeight()}, enterPoint);
        if (hit) {
            double x = 0;
            double y = 0;
            if (enterPoint.getX() == 0 || enterPoint.getX() == getWidth()) {
                x = 2 * (enterPoint.getX() - end.getX());
            }
            if (enterPoint.getY() == 0 || enterPoint.getY() == getHeight()) {
                y = 2 * (enterPoint.getY() - end.getY());
            }
            return new Force(enterPoint, x, y);
        } else {
            return new Force();
        }

    }


    // *untested code* from http://www.gamedev.net/community/forums/topic.asp?topic_id=505066
// ray-aabb test along one axis
    boolean rayAABBIntersect1D(float start, float dir, float length, float min, float max, float[] enterExit) {
        // ray parallel to direction
        if (Math.abs(dir) < 1.0E-8)
            return (start > min && start < max);

        // intersection params
        float t0, t1;
        t0 = (min - start) / dir;
        t1 = (max - start) / dir;

        // sort intersections
        if (t0 > t1) {
            float temp = t0;
            t0 = t1;
            t1 = temp;
        }

        // reduce interval
        if (t0 > enterExit[0]) enterExit[0] = t0;
        if (t1 < enterExit[1]) enterExit[1] = t1;

        // ray misses the box
        if (enterExit[1] < enterExit[0])
            return false;

        // intersections outside ray boundaries
        if (enterExit[1] <= 0.0f || enterExit[0] >= length)
            return false;

        return true;
    }

    boolean rayAABBIntersect(Location start, float[] dir, float length, float[] min, float[] max, Location pEnter) {
        float[] enterExit = new float[]{Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY};

        if (!rayAABBIntersect1D((float) start.getX(), dir[0], length, min[0], max[0], enterExit))
            return false;

        if (!rayAABBIntersect1D((float) start.getY(), dir[1], length, min[1], max[1], enterExit))
            return false;

        pEnter.pasteCoordination(start.getX() + dir[0] * enterExit[0], start.getY() + dir[1] * enterExit[0]);
        //pExit  = start + dir * exit;
        return true;
    }

}
