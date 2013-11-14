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
import edu.sharif.ce.dml.common.data.trace.TraceOwner;
import edu.sharif.ce.dml.common.data.trace.TraceWriter;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Feb 25, 2007
 * Time: 11:05:20 PM
 * <br/>
 * a snap shot of nodes in an iteration at a specific time.<br/>
 * it can generate traces for internal objects and write it.
 */
public class SnapShotData implements Tracable, TraceOwner {
    /**
     * list of nodes in this snapshot
     */
    final protected NodeShadow[] nodeShadows;
    /**
     * snapshot's current time.
     */
    private long time;

    private int lastAddedNodeShadow = 0;

    protected int timeStep;

    protected static final List<String> traceLabels = Arrays.asList("Time");

    public long getTime() {
        return time;
    }

    public NodeShadow[] getNodeShadows() {
        return nodeShadows;
    }

    public void addNodeShadows(NodeShadow nodeShadow) {
        if (lastAddedNodeShadow < nodeShadows.length)
            nodeShadows[lastAddedNodeShadow++] = nodeShadow;
    }

    private void delAllNodeShadows(){
        for (int i = 0; i < nodeShadows.length; i++) {
            nodeShadows[i]=null;
        }
        lastAddedNodeShadow=0;
    }

    public NodeShadow getNodeShadow(String name){
        for (NodeShadow nodeShadow : nodeShadows) {
            if (nodeShadow.getName().equals(name)){
                return nodeShadow;
            }
        }
        return null;
    }

    public void paintFootPrint(Graphics2D g, SnapShotData lastSnapShotData) {
        NodeShadow[] nodeShadows1 = getNodeShadows();
        NodeShadow[] nodeShadows2 = lastSnapShotData.getNodeShadows();
        for (int i = 0; i < nodeShadows1.length; i++) {
            nodeShadows1[i].paintFootPrint(g, nodeShadows2[i].getLocation());
        }
    }

    public int getNodeShadowsLength() {
        return getNodeShadows().length;
    }

    public void setTime(long time) {
        timeStep = (int) (time - this.time);
        this.time = time;
    }

    public SnapShotData(int nodeNumber) {
        nodeShadows = new NodeShadow[nodeNumber];
    }

    /**
     * @return only this object's values not the internals'
     */
    public List print() {
        return Arrays.asList(time);
    }

    /**
     * @return only this object's values not the internals'
     */
    public List<String> getLabels() {
        return traceLabels;
    }

    public int getTimeStep() {
        return timeStep;
    }

    public void writeTraces(TraceWriter writer) {
        for (NodeShadow nodeShadow : getNodeShadows()) {
            List toWrite = new LinkedList(print());
            toWrite.addAll(nodeShadow.print());
            writer.writeTrace(toWrite);
        }
    }

    public String[] getTraceLabels() {
        List<String> labels = new LinkedList<String>();
        labels.addAll(getLabels());
        labels.addAll(new NodeShadow().getLabels());
        return labels.toArray(new String[0]);
    }

    public SnapShotData deepClone(SnapShotData inputClone){
        SnapShotData clone;
        if (inputClone==null){
            clone = new SnapShotData(getNodeShadowsLength());
        }else{
            clone=inputClone;
            clone.delAllNodeShadows();
        }
        clone.setTime(getTime());

        for (NodeShadow nodeShadow : getNodeShadows()) {
            clone.addNodeShadows(new NodeShadow(nodeShadow.getName(),nodeShadow.getLocation(),nodeShadow.getSpeed(),
                    nodeShadow.getDirection(),nodeShadow.getRange()));
        }
        clone.addNodeShadows(new NodeShadow());
        return clone;
    }
}