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

package edu.sharif.ce.dml.common.data.trace.filter;

import javax.swing.filechooser.FileFilter;


/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Jun 12, 2008
 * Time: 12:21:26 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SingletonFileFilter extends FileFilter {

    /**
     * implemented to pretend singleton behaivour (because it may be created by reflection)
     * @param o
     * @return true if the references are equal or they are from same class
     */
    public boolean equals(Object o) {
        return this == o || o.getClass().equals(getClass());
    }

    public int hashCode() {
        return getClass().getName().hashCode()*3;
    }
}
