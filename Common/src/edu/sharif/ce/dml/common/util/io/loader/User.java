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

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 27, 2007
 * Time: 7:36:16 PM
 * <br/> An object which uses edu.sharif.ce.dml.common.data from a loader object.
 * You can use it as a thread or as a function which calss run method
 */
public abstract class User<S> extends Thread implements UsingSyndiction{
    protected FileLoader<S> fileLoader;
    protected UsingHandler<S> usingHandler;

    /**
     *
     * @param fileLoader a file loader which should be able to load <tt>S</tt> edu.sharif.ce.dml.common.data.
     */
    protected User( FileLoader<S> fileLoader,UsingHandler<S> usingHandler) {
        this.fileLoader = fileLoader;
        this.usingHandler = usingHandler;
    }

    public  void run(){
        loadConfigurations();
    }

    public void loadConfigurations() {
        usingHandler.setConfiguration(fileLoader.getConfigurations());
    }

    /**
     * deligates loadded configuration from first line of the file
     * @return configuration of the loaded file
     */
    public Map<String, String> getConfigurations(){
        return fileLoader.getConfigurations();
    }

    public LoadingHandler<S> getLoadingHandler(){
        return fileLoader.getLoadingHandler();
    }

}
