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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model;

import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.map.MapHandleGroup;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: May 27, 2009
 * Time: 9:12:47 AM
 * <br/> This interface provides basic fuctions to have interaction with an object which can
 * be configured using MapHandleGroup objects
 */
public interface MapHandleSupport extends Parameterable {

    /**
     *It may called multiple times before calling other functions.
     * Therefore, it should be stateless
     * @return a handle group.
     */
     MapHandleGroup getHandles();

    /**
     * should not change handles of mhp but can change handles' properties
     * @param mhp
     * @return
     */
     boolean validateHandles(MapHandleGroup mhp);

    /**
     * should not change handles of mhp
     * @param g
     * @param mhp
     */
     void paintUsingHandles(Graphics2D g,MapHandleGroup mhp);

    /**
     * should not change handles of mhp but can change handles' propoerties
     * @param mhp
     */
     void fillFromHandles(MapHandleGroup mhp) throws InvalidParameterInputException;
    
}
