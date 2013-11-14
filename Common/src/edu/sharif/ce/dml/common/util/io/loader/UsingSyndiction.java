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

package edu.sharif.ce.dml.common.util.io.loader;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 27, 2007
 * Time: 10:45:16 PM
 * <br/> implementation will be noticed when stop/start/end event is necessary
 */
public interface UsingSyndiction {

    /**
     * runs when a stop (pause) is necessary in loading/using process
     */
    public abstract void stopLoading();

    /**
     * runs when loading/using process should be endded
     */
    public abstract void endLoading();

    /**
     * runs when loading/using process should start again after stopping
     */
    public abstract void startLoading();
}
