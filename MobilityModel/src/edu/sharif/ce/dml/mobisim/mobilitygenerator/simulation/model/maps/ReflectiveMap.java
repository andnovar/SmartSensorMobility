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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps;

import edu.sharif.ce.dml.common.logic.entity.Location;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Dec 23, 2006
 * Time: 9:24:55 PM
 * <br/>this kind of maps usually gives some knowledges about their border and also has the reflection and bouncing property
 */
public interface ReflectiveMap {

    /**
     * checks if a node in its transaction hitted the border of map or not
     *
     * @param loc1   first position of the node
     * @param loc2   next position of the node
     * @param mirror it will be filled with the mirror point of the nextstep
     * @return the point where the node hitted the border <br/> null: if it doesn't hit
     */
    public Location isHitBorder(Location loc1, Location loc2, Location mirror);

}
