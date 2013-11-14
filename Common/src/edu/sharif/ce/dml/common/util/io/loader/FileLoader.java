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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 27, 2007
 * Time: 7:07:37 PM
 * <br/> represents a loader of <tt>E</tt> object ,that loads edu.sharif.ce.dml.common.data from a file.
 * <br/> It loads edu.sharif.ce.dml.common.data with deligationg loading function to {@link edu.sharif.ce.dml.common.util.io.loader.LoadingHandler} whith the same type.
 */
public abstract class FileLoader<E> {
    protected LoadingHandler<E> loadingHandler;
    private Map<String, String> configurations;
    protected File file;

    protected FileLoader(LoadingHandler<E> loadingHandler, File file) {
        this.loadingHandler = loadingHandler;
        this.file = file;
        if (!file.exists()) {
            throw new RuntimeException("Cannot Open File :" + file.getPath());
        }
        loadConfiguration();
    }

    /**
     * loads first line file edu.sharif.ce.dml.common.parameters and parses it into <code>configurations</code> as a {@link Map}.
     * loading process will be deligate to <tt>loadingHandler</tt> object.
     */
    public void loadConfiguration() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            configurations = loadingHandler.loadConfiguration(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LoadingHandler<E> getLoadingHandler() {
        return loadingHandler;
    }

    /**
     *
     * @return configuration which is loadid from first line of the file
     */
    public Map<String, String> getConfigurations() {
        return configurations;
    }

}
