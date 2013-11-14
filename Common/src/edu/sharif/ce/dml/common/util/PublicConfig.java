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

import java.awt.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 9, 2006
 * Time: 7:11:50 PM
 * <br/> gathered some properties that yet has not a class to save them in it.
 * note that it is a singleton class. it means that only one object can be instantiated from this class.
 * //fixme clean this file. move mobilitygenerator node related configs.
 */
public class PublicConfig {
    private static PublicConfig ourInstance = new PublicConfig();
    static final String CONFIG_FILE_NAME = "config.properties";
    public static final String CONFIG_FILE_KEY = "configfile";
    private static final String LAST_FOLDER_PARAM_NAME = "lastFolder";
    private static final String IMAGES_FOLDER_NAME="resource/images";
    private PropertyManager configFilePropertyManager;

    public String getLastFolderProperty() throws InvalidRequiredInputFileException {
        if (configFilePropertyManager==null){
            throw new InvalidRequiredInputFileException("Master config file not found");
        }
        return configFilePropertyManager.readProperty(LAST_FOLDER_PARAM_NAME);
    }

    public void setLastFolderProperty(File file) throws InvalidRequiredInputFileException {
        if (configFilePropertyManager==null){
            throw new InvalidRequiredInputFileException("Master config file not found");
        }
        configFilePropertyManager.addProperty(LAST_FOLDER_PARAM_NAME, file.getPath().replace("\\" + file.getName(), ""));
        saveConfigFile();
    }

    public void saveConfigFile() throws InvalidRequiredInputFileException {
        if (configFilePropertyManager==null){
            throw new InvalidRequiredInputFileException("Master config file not found");
        }
        configFilePropertyManager.store(CONFIG_FILE_NAME);
    }

    /**
     *
     * @return propertyManager object which contains properties in config file.
     * @throws InvalidRequiredInputFileException
     */
    public PropertyManager getConfigFilePropertyManager() throws InvalidRequiredInputFileException {
        if (configFilePropertyManager==null){
            throw new InvalidRequiredInputFileException("Master config file not found");
        }
        return configFilePropertyManager;
    }

    /**
     *
     * @return Singleton object
     */
    public static PublicConfig getInstance() {
        return ourInstance;
    }

    public String getImagesFolderName(){
        return IMAGES_FOLDER_NAME;
    }

    private PublicConfig() {
        try {
            configFilePropertyManager = new PropertyManager(CONFIG_FILE_NAME, CONFIG_FILE_KEY,false);
        } catch (InvalidRequiredInputFileException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
