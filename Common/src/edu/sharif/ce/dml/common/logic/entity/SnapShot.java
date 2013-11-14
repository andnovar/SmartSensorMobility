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
import edu.sharif.ce.dml.common.data.entity.SnapShotData;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 1, 2007
 * Time: 10:32:09 AM<br>
 */
public class SnapShot extends SnapShotData {
    private Node[] nodes;
    private final ConnectionCalculator connectionCalculator;

    /**
     * @param nodeNumber number of nodes
     */
    public SnapShot(int nodeNumber) {
        super(nodeNumber);
        connectionCalculator = new ConnectionCalculator();
    }

    public Node[] getNodeShadows() {
        return nodes;
    }

    @Override
    public NodeShadow getNodeShadow(String name) {
        for (NodeShadow nodeShadow : nodes) {
            if (nodeShadow.getName().equals(name)){
                return nodeShadow;
            }
        }
        return null;
    }

    /**
     * loads edu.sharif.ce.dml.common.data from <tt>nodeShadows</tt> array. so Node objects reference will not change.
     *
     * @param nodeShadows
     */
    public void setNodesData(NodeShadow[] nodeShadows) {
        if (nodes == null) {
            nodes = new Node[nodeShadows.length];
            for (int i = 0; i < nodeShadows.length; i++) {
                nodes[i] = createNode(nodeShadows[i]);
            }
            for (Node node : nodes) {
                node.createLinks(nodes,getTime());
            }
        } else {
            for (int i = 0; i < nodes.length; i++) {
                nodes[i].loadData(nodeShadows[i]);
            }
            for (Node node : nodes) {
                node.updateLinks(nodes,getTime());
            }
        }
    }

    protected Node createNode(NodeShadow node) {
        return new Node(node);
    }

    /**
     * @return if the graph of nodes in this snapshot is fully connected.
     */
    public boolean isConnected() {
        return connectionCalculator.isConnected();
    }

    /**
     * calculates fully connectedness of the graph of nodes in this snapshot
     */
    private class ConnectionCalculator {
        private long lastTimeConnectionCalculatedTime = -1;
        private boolean lastTimeConnectionCalculated = false;
        private Set<Node> markedNodes;


        /**
         * @return if nodes in this snapshot of nodes ,all (Active Nodes) are connected.
         */
        public boolean isConnected() {
            if (lastTimeConnectionCalculatedTime == getTime()) {
                return lastTimeConnectionCalculated;
            }
            markedNodes = new HashSet<Node>();
            //find an active node
            Node startNode = null;
            for (Node node : nodes) {
                if (node.isActive()) {
                    startNode = node;
                    break;
                }
            }
            if (startNode != null) {
                dfs(startNode);

                int activeNodes = 0;
                for (Node node : nodes) {
                    activeNodes += node.isActive() ? 1 : 0;
                }
                lastTimeConnectionCalculated = markedNodes.size() >= activeNodes;
            } else {
                lastTimeConnectionCalculated = true; // no active node
            }
            lastTimeConnectionCalculatedTime = getTime();
            return lastTimeConnectionCalculated;
        }

        /**
         * runs a DFS on the tree of nodes.
         * sets <code>MarkedNode</code> to nodes that can bee seen from <code>currentNode</code>
         *
         * @param currentNode
         */
        private void dfs(Node currentNode) {
            markedNodes.add(currentNode);
            for (Node node : nodes) {
                if (currentNode.hasLinkto(node) && !markedNodes.contains(node)) {
                    dfs(node);
                }
            }
        }
    }
}
