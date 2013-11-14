package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.obstaclemap;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.MultipleSelectParameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.logic.primitives.BooleanParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.MapHandleSupport;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.SquareReflectMap;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.exception.InvalidLocationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.MyGraphics2D;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandle;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.multi.MultiMapHandleGroup;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: 12/29/10
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ObstacleMap extends SquareReflectMap {
    protected MultipleSelectParameter obstacles = new MultipleSelectParameter("obstacles");
    boolean extendMap;

    @Override
    public java.util.Map<String, Parameter> getParameters() {
        java.util.Map<String, Parameter> parameters = super.getParameters();
        parameters.put("obstacles", obstacles);
        parameters.put("extendmap", new BooleanParameter("extendmap", extendMap));
        return parameters;
    }

    @Override
    public void setParameters(java.util.Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        obstacles = (MultipleSelectParameter) parameters.get("obstacles");
        extendMap = ((BooleanParameter) parameters.get("extendmap")).getValue();
        extendMap();
    }

    @Override
    public void validateNode(Location loc) throws InvalidLocationException {
        super.validateNode(loc);
        for (Obstacle obstacle : getObstacles()) {
            if (obstacle.isInside(loc)) {
                throw new InvalidLocationException(obstacle.toString());
            }
        }
    }

    @Override
    public Location isHitBorder(final Location loc1, Location loc2, Location mirror) {
        Location superHit = super.isHitBorder(loc1, loc2, mirror);
        Force superForce = new Force();
        if (superHit != null) {
            superForce = new Force(superHit, mirror.getX() - loc2.getX(), mirror.getY() - loc2.getY());
        }


        Set<Force> forces = new TreeSet<Force>(new Comparator<Force>() {
            public int compare(Force o1, Force o2) {
                return (int) (loc1.getLength(o1.getX(), o1.getY()) - loc1.getLength(o2.getX(), o2.getY()));
            }
        });
        if (superForce.isValid()) {
            forces.add(superForce);
        }
        for (Obstacle obstacle : getObstacles()) {
            Force f = obstacle.isHitBorder(loc1, loc2);
            if (f.isValid()) {
                forces.add(f);
            }
        }
        if (forces.size() == 0) {
            return null;
        }
        Force outputForce = new Force(forces.iterator().next().getHitPoint(), 0, 0);
        for (Force force : forces) {
            if (force.getHitPoint().getLength(outputForce.getHitPoint()) == 0) {
                outputForce.add(force);
            }
        }
        mirror.pasteCoordination(loc2);
        mirror.translate(outputForce.getX(), outputForce.getY());
        return outputForce.getHitPoint();
    }

    private List<Obstacle> getObstacles() {
        List<Obstacle> output = new LinkedList<Obstacle>();
        for (Parameterable parameterable : obstacles.getSelected()) {
            output.add((Obstacle) parameterable);
        }
        return output;
    }

    @Override
    public void paint(Graphics2D g) {
        super.paint(g);
        for (Obstacle obstacle : getObstacles()) {
            obstacle.paint(g);
        }
    }

    @Override
    public MapHandleGroup getHandles() {
        MapHandleGroup masterMapHandleGroup = super.getHandles();
        MultiMapHandleGroup mhp = new MultiMapHandleGroup(masterMapHandleGroup.getSize(), masterMapHandleGroup, this);
        for (Obstacle obstacle : getObstacles()) {
            Location offsetLoc = obstacle.getOffset();
            MapHandle offset = new MapHandle((int) offsetLoc.getX(), (int) offsetLoc.getY());
            mhp.addMapHandleGroup(obstacle.getHandles(), obstacle, offset);
        }
        return mhp;
    }

    public void paintUsingHandles(Graphics2D g, MapHandleGroup mhp) {
        MultiMapHandleGroup mapHandleGroup = (MultiMapHandleGroup) mhp;
        super.paintUsingHandles(g, mapHandleGroup.getMasterHandleGroup());

        for (int i = 0; i < mapHandleGroup.getMapHandleGroupSize(); i++) {
            MapHandle offset = mapHandleGroup.getOffset(i);
            g.translate(+offset.getX(), +offset.getY());
            MapHandleSupport mapHandleSupport = mapHandleGroup.getSupporterFor(i);
            mapHandleSupport.paintUsingHandles(g, mapHandleGroup.getMapHandleGroup(i));
            MyGraphics2D.getInstance().drawString(mapHandleSupport.toString(), 0, 0, g);
            g.translate(-offset.getX(), -offset.getY());
        }
    }

    private void extendMap() throws InvalidParameterInputException {
        List<Obstacle> obstacles1 = getObstacles();
        if (extendMap) {
            int w = getWidth();
            int h = getHeight();
            for (Obstacle obstacle : obstacles1) {
                Location offset = obstacle.getOffset();
                int width = obstacle.getWidth() + (int) offset.getX();
                int height = obstacle.getHeight() + (int) offset.getY();
                w = w < width ? width : w;
                h = h < height ? height : h;
            }
            java.util.Map<String, Parameter> parameters = getParameters();
            ((IntegerParameter) parameters.get("width")).setValue(w);
            ((IntegerParameter) parameters.get("height")).setValue(h);

            super.setParameters(parameters);
        }
    }

    public void fillFromHandles(MapHandleGroup mhp) throws InvalidParameterInputException {
        MultiMapHandleGroup mapHandleGroup = (MultiMapHandleGroup) mhp;
        super.fillFromHandles(mapHandleGroup.getMasterHandleGroup());
        int mhpGroupSize = mapHandleGroup.getMapHandleGroupSize();
        List<Obstacle> obstaclesList = getObstacles();
        if (mhpGroupSize != obstaclesList.size()) {
            throw new InvalidParameterInputException("Unequal number of obstacles ", obstacles.toString(),
                    mhpGroupSize + "!=" + obstaclesList.size());
        }
        int i = 0;
        for (Obstacle obstacle : obstaclesList) {
            MapHandle mapHandle = mapHandleGroup.getOffset(i);
            obstacle.setOffset(new Location(mapHandle.getX(), mapHandle.getY()));
            mapHandleGroup.getSupporterFor(i).fillFromHandles(mapHandleGroup.getMapHandleGroup(i));
            i++;
        }
        extendMap();
    }

    public boolean validateHandles(MapHandleGroup mhp) {
        MultiMapHandleGroup mapHandleGroup = (MultiMapHandleGroup) mhp;
        boolean valid = true;
        MapHandle masterSize = mapHandleGroup.getMasterHandleGroup().getSize();
        if (extendMap) {
            //first validate internal objects they may want to extend too!
            for (int i = 0; i < mapHandleGroup.getMapHandleGroupSize(); i++) {
                valid = mapHandleGroup.getSupporterFor(i).validateHandles(mapHandleGroup.getMapHandleGroup(i));
                if (!valid) return false;
            }
            int w = masterSize.getX();
            int h = masterSize.getY();
            for (int i = 0; i < mapHandleGroup.getMapHandleGroupSize(); i++) {
                MapHandle offset = mapHandleGroup.getOffset(i);
                MapHandle mhpSize = mapHandleGroup.getMapHandleGroup(i).getSize();
                int newX = mhpSize.getX() + offset.getX();
                int newY = mhpSize.getY() + offset.getY();
                w = w < newX ? newX : w;
                h = h < newY ? newY : h;
            }
            masterSize.setXY(w, h);
        }
        int masterX = masterSize.getX();
        int masterY = masterSize.getY();
        for (int i = 0; i < mapHandleGroup.getMapHandleGroupSize(); i++) {
            MapHandleGroup group = mapHandleGroup.getMapHandleGroup(i);
            MapHandle offset = mapHandleGroup.getOffset(i);
            valid = valid && group.getSize().getX() + offset.getX() <= masterX;
            valid = valid && group.getSize().getY() + offset.getY() <= masterY;
            valid = valid && (extendMap || mapHandleGroup.getSupporterFor(i).validateHandles(mapHandleGroup.getMapHandleGroup(i)));
            if (!valid) return false;
        }

        return valid;
    }

}
