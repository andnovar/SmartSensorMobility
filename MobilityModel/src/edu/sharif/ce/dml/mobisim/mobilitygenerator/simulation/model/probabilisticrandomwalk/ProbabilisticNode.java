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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.probabilisticrandomwalk;

import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.Simulation;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.ReflectiveMap;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Feb 8, 2007
 * Time: 1:14:01 PM
 */
public class ProbabilisticNode {
    private Random randomGenerator;

    int depth = 0;

    GeneratorNode node;
    LinkedList<Double> xLocations = new LinkedList<Double>();
    LinkedList<Double> yLocations = new LinkedList<Double>();
    private State xState, yState;
    MyListIterator<Double> xIndex, yIndex;
    double x, y;

    private final static int GO_PREV_LOC = -1;
    private final static int STOP_CURRENT_LOC = 0;
    private final static int GO_NEXT_LOC = 1;
    private final static State[] states;
    private double speed;

    static {
        states = new State[3];
        states[0] = new State(GO_PREV_LOC);
        states[1] = new State(STOP_CURRENT_LOC);
        states[2] = new State(GO_NEXT_LOC);
        states[0].addLink(states[0], 0.7f);
        states[0].addLink(states[1], 0.3f);
        states[1].addLink(states[0], 0.5f);
        states[1].addLink(states[2], 0.5f);
        states[2].addLink(states[2], 0.7f);
        states[2].addLink(states[1], 0.3f);
        states[0].setReverseState(states[2]);
        states[1].setReverseState(states[1]);
        states[2].setReverseState(states[0]);
        State.neutral = states[1];
    }

    public ProbabilisticNode(GeneratorNode node) {
        this.node = node;
        xState = states[2];
        yState = states[2];
        xIndex = new MyListIterator<Double>(xLocations);
        yIndex = new MyListIterator<Double>(yLocations);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * calculates angle of moving according to previous and current position.<br/>
     * note that if previous position not exists, it preditcts it
     *
     * @param speed
     * @param timeStep
     * @return
     */
    private double calculateAngle(double speed, double timeStep) {
        if (!xIndex.hasPrev()) {
            predictPrevious(speed, timeStep);
        }
        if (!yIndex.hasPrev()) {
            predictPrevious(speed, timeStep);
        }
        return Location.calculateRadianAngle(x - xIndex.getPrev(),
                y - yIndex.getPrev());
    }

    /**
     * sets location of this ProbabilisticNode node to <code>loc</code>
     *
     * @param loc
     */
    public void putLocation(Location loc) {
        xIndex.addToLast(loc.getX());
        yIndex.addToLast(loc.getY());
        x = loc.getX();
        y = loc.getY();
    }

    private void predictPrevious(double speed, double timeStep) {
        double x1 = xLocations.getFirst();
        double y1 = yLocations.getFirst();
        double radianAngle = Location.calculateRadianAngle(x1 - xLocations.get(1),
                y1 - yLocations.get(1));
        double newX = Math.cos(radianAngle) * speed * timeStep + x1;
        if (xIndex.hasPrev()) {
            //if Y positions doesn't exists
            //so preserve current xIndext position
//            int lastIndex = xIndex.nextIndex();
//            xLocations.addFirst(newX);
//            xIndex = xLocations.listIterator(lastIndex + 1);
        } else {
            //else xIndex is also at first of its list
            xIndex.addToFirst(newX);
        }

        double newY = Math.sin(radianAngle) * speed * timeStep + y1;
        if (yIndex.hasPrev()) {
            //if X position doesn't exists
            //so preserve current yIndext position
//            int lastIndex = yIndex.nextIndex();
//            yLocations.addFirst(newY);
//            yIndex = yLocations.listIterator(lastIndex + 1);
        } else {
            //else yIndex is also at first of its list
            yIndex.addToFirst(newY);
        }
    }

    public Location generateNextLoc(double timeStep, ReflectiveMap map) {

        xState = xState.getNextState();
        yState = yState.getNextState();
        calculateNextLocation(timeStep, map);
//        calculateNextLocation(timeStep, map);
        return new Location(x, y);
    }

    /**
     * the main method that calculates new location according to new xState and yState
     *
     * @param timeStep
     * @param map
     */
    private void calculateNextLocation(double timeStep, ReflectiveMap map) {
        depth++;

        double angle = calculateAngle(speed, timeStep);

        //maintains indexes because of map borders
//        int lastXIndex = xIndex.previousIndex();
//        int lastYIndex = yIndex.previousIndex();


        ///////////////////////////////////////for X
        if (xState.index == GO_NEXT_LOC) {
            //should go to next position
            if (xIndex.hasNext()) {
                x = xIndex.goToNext();
            } else {
                //if it is at end of chain generate a new
                x = Math.cos(angle) * speed * timeStep + x;
                xIndex.addToLast(x);
            }
        } else if (xState.index == GO_PREV_LOC) {
            //it should go to previous position
            if (!xIndex.hasPrev()) {
                //if it is at head of chain predict
                predictPrevious(speed, timeStep);
            }
            x = xIndex.goToPrev();
        }

        ////////////////////////////////////////////for Y
        if (yState.index == GO_NEXT_LOC) {
            //should go to next position
            if (yIndex.hasNext()) {
                y = yIndex.goToNext();
            } else {
                //if it is at end of chain generate a new
                y = Math.sin(angle) * speed * timeStep + y;
                yIndex.addToLast(y);
            }

        } else if (yState.index == GO_PREV_LOC) {
            //it should go to previous position
            if (!yIndex.hasPrev()) {
                predictPrevious(speed, timeStep);
            }
            y = yIndex.goToPrev();
        }

        ///now check map borders
        if (map.isHitBorder(node.getDoubleLocation(), new Location(x, y), new Location(0, 0)) != null) {
            //reverse states and rerun this algorithm
            xState = xState.getReverseState();
            yState = yState.getReverseState();
            if (xState.index == GO_NEXT_LOC) {
                x = xIndex.goToNext();
            } else if (xState.index == GO_PREV_LOC) {
                x = xIndex.goToPrev();
            }
            if (yState.index == GO_NEXT_LOC) {
                y = yIndex.goToNext();
            } else if (yState.index == GO_PREV_LOC) {
                y = yIndex.goToPrev();
            }

//            if (map.isHitBorder(node.getTrack().getLocation(), new Location(x, y), new Location(0, 0)) != null) {
            if (depth >= 2) {
                xState = State.neutral;
                yState = State.neutral;
            }else{
                xState = xState.getNextState();
                yState = yState.getNextState();
            }

            calculateNextLocation(timeStep, map);
//            }

            /*       x=lastLocation.getX();
          y=lastLocation.getY();
          //!!!!!!!!!!!!!!1 has performance penalty!!
          xIndex = xLocations.listIterator(lastXIndex);
          yIndex = yLocations.listIterator(lastYIndex);*/
        }


        depth--;
    }


    private static class State {
        static State neutral;
        List<StateLink> links = new ArrayList<StateLink>(2);
        int index;
        State reverseState;

        public State(int index) {
            this.index = index;
        }


        public State getReverseState() {
            return reverseState;
        }

        public void setReverseState(State reverseState) {
            this.reverseState = reverseState;
        }

        /**
         * @return next state according to probability of each state link using random function
         */
        public State getNextState() {
            //what about random seed!!
            double r = Simulation.getDoubleRandomNumber();
            return links.get((Math.signum(r - links.get(0).probability)) >= 0 ? 1 : 0).destination;
        }

        public void addLink(State s, float p) {
            links.add(new StateLink(p, s));
        }

        private class StateLink {
            float probability;
            State destination;

            public StateLink(float probability, State destination) {
                this.probability = probability;
                this.destination = destination;
            }
        }
    }

    private class MyListIterator<S> {
        private ListIterator<S> l;

        public MyListIterator(LinkedList<S> list) {
            l = list.listIterator();
        }

        public void goTo(LinkedList<S> list, int index) {
            l = list.listIterator(index);
        }

        public S getPrev() {
            l.previous();
            S returnValue = l.previous();
            l.next();
            l.next();
            return returnValue;
        }

        public S getNext() {
            S returnValue = l.next();
            l.previous();
            return returnValue;
        }

        public S goToPrev() {
            l.previous();
            S returnValue = l.previous();
            l.next();
            return returnValue;
        }

        public S goToNext() {
            return l.next();
        }

        public boolean hasPrev() {
            l.previous();
            boolean b = l.hasPrevious();
            l.next();
            return b;
        }

        public boolean hasNext() {
            return l.hasNext();
        }

        public void addToLast(S s) {
            if (l.hasNext()) {
                throw new RuntimeException();
            }
            l.add(s);
        }

        public void addToFirst(S s) {
            if (hasPrev()) {
                throw new RuntimeException();
            }
            l.previous();
            l.add(s);
            l.next();
        }

    }
}
