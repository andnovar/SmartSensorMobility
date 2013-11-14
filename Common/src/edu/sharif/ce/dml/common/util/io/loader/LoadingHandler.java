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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 27, 2007
 * Time: 7:46:06 PM
 * <br/>a handler which will be used to load datas from a file using a {@link java.io.Reader} object
 */
public interface LoadingHandler<E> {
    public final String NODE_NUMBER_KEY = "nodenumber";

    /**
     * loads configuration from file. the reader wouldn't be closed in this method. You should load all prerun
     * activities here. For example configs and labels.
     * @param reader it is better to be a fresh reader
     * @return
     */
    public abstract Map<String,String> loadConfiguration(BufferedReader reader) throws IOException;

    /**
     *
     * @param capacity initial capacity
     * @return array of objects that this class will loading it. it's data is not important, but used
     * for initialization process.
     */
    public abstract E[] getEArray(int capacity);

    /**
     * todo this method and {@link #loadConfiguration(java.io.BufferedReader)} should be moved in another class.
     * to communicate with writers.
     * @return if the data has any label.
     */
    public String[] getDataLabels();
}
