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
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.exception.InvalidLocationException;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Dec 23, 2006
 * Time: 9:24:45 PM
 */
public interface PassiveMap {
    /**
     * this method validates if a destNode is valid in the map
     * this method can change the loc positions
     *
     * @param loc
     * @throws InvalidLocationException if location <code>loc</code> is invalid according to this map
     */
    public abstract void validateDestNode(Location loc) throws InvalidLocationException;

    /**
     * validates if a node position is valid in the map or not
     *
     * @param loc
     * @throws InvalidLocationException if location <code>loc</code> is invalid according to this map
     */
    public abstract void validateNode(Location loc) throws InvalidLocationException;
}
