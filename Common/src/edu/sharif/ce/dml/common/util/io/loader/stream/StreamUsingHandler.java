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

package edu.sharif.ce.dml.common.util.io.loader.stream;

import edu.sharif.ce.dml.common.util.io.loader.UsingHandler;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 27, 2007
 * Time: 7:46:18 PM
 * <br/>classes which implement this interface can be used as a Stream using handler.
 * note this has two method one <tt>loadData</tt> for loading the edu.sharif.ce.dml.common.data and <tt>useData</tt> which
 *  is for using edu.sharif.ce.dml.common.data and usually for time consuming works. For better performance do only loading process
 * in <tt>loadData</tt> method.
 */
public interface StreamUsingHandler<S> extends UsingHandler<S> {

    /**
     * to load edu.sharif.ce.dml.common.data. It's better to be light method.
     * @param data
     */
    public abstract void loadData(S data);

    /**
     * called when the time of using edu.sharif.ce.dml.common.data is reached
     */
    public abstract void useData();

}
