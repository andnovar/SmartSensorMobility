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

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Jun 12, 2008
 * Time: 6:37:11 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.sharif.ce.dml.common.parameters.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * represents a XML config document, used to manage referencable objects and information about current file
 */
public class ParameterableDocument {
    private   static Map<String, ParameterableDocument> nameDocument = new HashMap<String, ParameterableDocument>();

    /**
     * composites that is waiting for a referencable object to be loadded
     */
    private  Map<String, Set<CompositeDataParameter>> tagNameWaitingSet = new HashMap<String, Set<CompositeDataParameter>>();

    /**
     * referencable objects
     */
    private  Map<String, CompositeDataParameter> tagNameElement = new HashMap<String, CompositeDataParameter>();

    private File currentFile;

    /**
     * resets data structures that is used to manage lazy loading.
     */
    void resetLazyDataStructures() {
        tagNameElement = new HashMap<String, CompositeDataParameter>();
        tagNameWaitingSet = new HashMap<String, Set<CompositeDataParameter>>();
    }

    public ParameterableDocument(String name, File currentFile) {
        this.currentFile = currentFile;
        nameDocument.put(name, this);
    }

    public static ParameterableDocument getParameterableDocument(String name){
        return nameDocument.get(name);
    }

    public Map<String, Set<CompositeDataParameter>> getTagNameWaitingSet() {
        return tagNameWaitingSet;
    }

    public Map<String, CompositeDataParameter> getTagNameElement() {
        return tagNameElement;
    }

    public File getCurrentFile() {
        return currentFile;
    }
}
