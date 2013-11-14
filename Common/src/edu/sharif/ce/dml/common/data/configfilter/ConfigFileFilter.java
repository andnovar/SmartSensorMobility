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

package edu.sharif.ce.dml.common.data.configfilter;

import edu.sharif.ce.dml.common.data.trace.filter.SingletonFileFilter;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Jun 12, 2008
 * Time: 8:01:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigFileFilter extends SingletonFileFilter{
    private static ConfigFileFilter xmlInstance = new ConfigFileFilter("xml");
    private static ConfigFileFilter propertiesInstance = new ConfigFileFilter("properties");
    private String postfix="xml";
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().matches(".*\\."+postfix+"$");
    }

    public String getDescription() {
        return "Program Configuration Files";
    }

    public static ConfigFileFilter getPropertiesInstance(){
        return propertiesInstance;
    }

    public static ConfigFileFilter getXMLInstance(){
        return xmlInstance;
    }

    protected ConfigFileFilter(String p) {
        this.postfix = p;
    }

    public ConfigFileFilter() {
    }
}
