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

import edu.sharif.ce.dml.common.util.io.loader.LoadingHandler;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Oct 28, 2007
 * Time: 6:57:50 PM
 */
public interface StreamLoadingHandler<E> extends LoadingHandler<E> {
    /**
     * Loads data from a file and returns one object of type <tt>E</tt>
     * @param reader
     * @return
     * @throws java.io.IOException
     */
    public abstract E sReadData(BufferedReader reader) throws IOException;
}
