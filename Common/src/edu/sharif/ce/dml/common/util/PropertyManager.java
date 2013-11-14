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

package edu.sharif.ce.dml.common.util;

import edu.sharif.ce.dml.common.data.configfilter.ConfigFileFilter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Aug 23, 2006
 * Time: 1:10:09 PM<br/>
 * A class for loading a properties file and read its properities
 */

public class PropertyManager {
    public String fileName;
    private Properties properties;

    public PropertyManager(String fileName) throws InvalidRequiredInputFileException {
        this(fileName, "", false);
    }

    public PropertyManager(String fileName, String title, boolean savePath) throws InvalidRequiredInputFileException {
        properties = new Properties();
        File f = FileManager.getInstance().getFile(fileName, title, savePath,
                new javax.swing.filechooser.FileFilter[]{ConfigFileFilter.getPropertiesInstance()}, true);
        this.fileName = f.getPath();

        try {
            properties.load(new FileInputStream(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void finalize() throws Throwable {

        super.finalize();
    }

    /**
     * Saves this object properties into the file with path <tt>outputFileName</tt>.
     * @param outputFileName
     * @return if it can save into the file
     */
    public boolean store(String outputFileName) {
        try {
            if (outputFileName == null) {
                outputFileName = fileName;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(outputFileName);
            properties.store(fileOutputStream, null);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * adds a property with name = <tt>key</tt> and value=<tt>value</tt>.
     * @param key
     * @param value
     */
    public void addProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * a public function to load a properity from a properties file
     *
     * @param propertyName key name for that property
     * @return a String value of that property
     */
    public String readProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    /**
     * @param fileName     name of property file
     * @param propertyName
     * @return read property value
     */
    public static String readProperty(String fileName, String propertyName) {
        Properties propertiesFile = new Properties();
        try {
            propertiesFile.load(new FileInputStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return propertiesFile.getProperty(propertyName);
    }

    public Map<String,String> getAllProperties(){
        Map<String,String> output = new HashMap<String, String>();
        for (String s : properties.stringPropertyNames()) {
            output.put(s,properties.getProperty(s));
        }
        return output;
    }

}
