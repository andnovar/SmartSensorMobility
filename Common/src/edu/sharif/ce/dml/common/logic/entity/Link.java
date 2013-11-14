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

package edu.sharif.ce.dml.common.logic.entity;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Aug 22, 2009
 * Time: 12:09:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class Link {
    private Node node1;
    private Node node2;
    private boolean isActive;
    private long totalUpTime = 0;
    private boolean tUpReady = false;
    private boolean tDownReady = false;
    private long lastOnTime = 0;
    private long disconnection = 0;
    private long totalDownTime;
    private long lastDownTime = 0;

    public Link(Node node1, Node node2, long time) {
        this(node1, node2, false, time);
    }

    public Link(Node node1, Node node2, boolean active, long time) {
        this.node1 = node1;
        this.node2 = node2;
        totalUpTime = 0;
        isActive = false;
        lastDownTime = time;
        setActive(active, time);
    }

    public Node getNode1() {
        return node1;
    }

    public void setNode1(Node node1) {
        this.node1 = node1;
    }

    public Node getNode2() {
        return node2;
    }

    public void setNode2(Node node2) {
        this.node2 = node2;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active, long time) {
        if (!isActive && active) {
            lastOnTime = time;
            totalDownTime += time - lastDownTime;
        } else if (isActive && !active) {
            totalUpTime += time - lastOnTime;
            lastDownTime = time;
            disconnection++;
        }
        isActive = active;
    }

    public long computeTotalDuration(long time) {
        if (isActive && !tUpReady) {
            totalUpTime += time - lastOnTime;
        }
        tUpReady = true;
        return totalUpTime;
    }

    public long computeIntermeetingTime(long time) {
        if (!isActive && !tDownReady) {
            totalDownTime += time - lastDownTime;
        }
        tDownReady = true;
        return totalDownTime;
    }

    public long getDisconnection() {
        return disconnection;
    }


}
