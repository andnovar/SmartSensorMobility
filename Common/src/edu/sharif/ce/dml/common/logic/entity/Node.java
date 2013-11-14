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

package edu.sharif.ce.dml.common.logic.entity;

import edu.sharif.ce.dml.common.data.entity.NodeShadow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 1, 2007
 * Time: 10:31:59 AM<br>
 * Adds some logic activities to <tt>NodeShadow</tt> classes
 */
public class Node extends NodeShadow {

    private Map<Node, Link> links;

    public Node(NodeShadow nodeData) {
        this();
        loadData(nodeData);
    }

    public Node() {
        links = new HashMap<Node, Link>();
    }

    /**
     * loads the node information using <tt>nodeData</tt> without changing Node reference.
     *
     * @param nodeData
     */
    public void loadData(NodeShadow nodeData) {
        setLocation(nodeData.getLocation());
        setSpeed(nodeData.getSpeed());
        setName(nodeData.getName());
        setDirection(nodeData.getDirection());
        if (nodeData.getRange() < 0) {
            if (this.range < 0) {
                //first time in simulation
                //lastOptimalRange =
                setRange(MaxRange);
            }
        } else {
            //change from file is loaded
            setRange(nodeData.getRange());
        }
    }

    public void createLinks(Node[] nodes, long time) {
        for (Node node : nodes) {
            if (!node.equals(this)) {
                links.put(node, new Link(this, node, time));
            }
        }
        updateLinks(nodes, time);
    }

    public void updateLinks(Node[] nodes, long time) {
        for (Node node : nodes) {
            Link link = links.get(node);
            if (link != null)
                link.setActive(isActive() && node.isActive() &&
                        this.isIn1WayRange(node), time);
        }
    }

    public int get1WayDegree() {
        int activeLinks = 0;
        for (Link link : links.values()) {
            if (link.isActive()) activeLinks++;
        }
        return activeLinks;
    }

    public int get2WayDegree() {
        int activeLinks = 0;
        for (Node node : links.keySet()) {
            Link link = links.get(node);
            if (link.isActive() && node.hasLinkto(this)) {
                activeLinks++;
            }
        }
        return activeLinks;
    }


    public boolean hasLinkto(Node node) {
        Link link = links.get(node);
        return link != null && link.isActive();
    }


    /**
     * checks if <tt>node</tt> is in range of this node
     *
     * @param node
     * @param range
     * @return
     */
    public boolean isIn1WayRange(Node node, double range) {
        return (range >= location.getLength(node.location));
    }

    private boolean isIn1WayRange(Node node) {
        //is implemented for performance
        return (range >= location.getLength(node.location));
    }

    /**
     * checks if <tt>node</tt> is in range of this node, and also this node is
     * is in range of <tt>node</tt>
     *
     * @param node
     * @return
     */
    private boolean isInRange(Node node) {
        return node.isIn1WayRange(this, node.range) &&
                isIn1WayRange(node, range);
    }

    public static double convertRangetoPower(double range) {
        return Math.pow(range, 2);
    }

    public double[] computeAverageLinkDuration(long time) {
        double totalValue = 0;
        int totalCount = 0;
        for (Link link : links.values()) {
            if (link.computeTotalDuration(time) > 0) {
                totalCount++;
                totalValue += link.computeTotalDuration(time)*1.0 / (link.getDisconnection() + 1);
            }
        }
        return new double[]{totalValue, totalCount};
    }

    public double[] computeLinkDisconnection(long time) {
        double sum = 0;
        int count = 0;
        for (Link link : links.values()) {
            if (link.computeTotalDuration(time) > 0) {
                sum += link.getDisconnection();
                count++;
            }
        }
        return new double[]{sum, count};
    }

    public double[] computeLinkIntermeetingTime(long time) {
        double sum = 0;
        int count = 0;
        for (Link link : links.values()) {
            if (link.computeIntermeetingTime(time) > 0) {
                sum += link.computeIntermeetingTime(time)*1.0 / (link.getDisconnection() + 1);
                count++;
            }
        }
        return new double[]{sum, count};
    }

    public List<Node> getNeighbors() {
        List<Node> output = new LinkedList<Node>();
        for (Node node : links.keySet()) {
            Link link = links.get(node);
            if (link.isActive()) {
                output.add(node);
            }
        }
        return output;
    }

}
