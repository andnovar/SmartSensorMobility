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

package edu.sharif.ce.dml.common.parameters.data;

import edu.sharif.ce.dml.common.parameters.data.ParameterableConfigFileException;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Mar 30, 2007
 * Time: 3:27:55 PM<br/>
 * Utility class to load data objects
 */
public class ParameterableConfigLoader {


    /**
     *  data objects
     */
//    private List<ParameterableData> parameterableDatas = new LinkedList<ParameterableData>();
    private CompositeDataParameter rootComposite;

    public List<ParameterableData> getParameterableDatas() {
        assert rootComposite!=null;
        Collection<GeneralDataParameter> parameterCollection = rootComposite.getParameters().values();
        List<ParameterableData> parameterableDatas = new LinkedList<ParameterableData>();
        for (GeneralDataParameter generalDataParameter : parameterCollection) {
            //the casting should be managed by dtd file
            parameterableDatas.add((ParameterableData) generalDataParameter);
        }
        return parameterableDatas;
    }

    /**
     * @return The root composite parameter of the loadded XML.
     */
    public CompositeDataParameter getRootComposite() {
        assert rootComposite!=null;
        return rootComposite;
    }

    /**
     * this method is for triggering load operation, this function fills parameters from xml file
     *
     * @param file file object that points to xml file.
     * @throws IOException
     */
    private void loadXML( File file) throws ParameterableConfigFileException {
        try {
// Load XML into JDOM Document
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new BufferedReader(new FileReader(file)));
// Turn into dataparameter objects
            loadFromElements(doc.getRootElement(),new ParameterableDocument(Math.random()+"",file));
        } catch (JDOMException e) {
            throw new ParameterableConfigFileException("Error in parsing the XML file ("+file+") with message: "+e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ParameterableConfigFileException("file ("+file+") not found");
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParameterableConfigFileException("IO exception in loading the file ("+file+")");
        }
    }

    /**
     * in fact this method load all information from xml file from passed elements
     *
     * @param element Masoud "composite" element
     * @throws ParameterableConfigFileException
     *
     */
    public void loadFromElements(Element element, ParameterableDocument document) throws ParameterableConfigFileException {
        if (element.getName().equals("rootcomposite")) {
            //todo
            rootComposite = new CompositeDataParameter();
//            CompositeDataParameter.resetLazyDataStructures();
            rootComposite.loadInitData(element,document);
//            for (GeneralDataParameter generalDataParameter : parameterableData.getParameters().values()) {
//                parameterableDatas.add((ParameterableData) generalDataParameter );
//            }
        }
    }

//    public static List<Parameterable> load(String configFile) throws Exception {
//        return load(new File(configFile));
//    }


    /**
     * loads from an XML <tt>file</tt> using JDOM library
     * @param file
     * @return
     * @throws Exception
     */
    public static ParameterableConfigLoader load(File file) throws ParameterableConfigFileException {
        ParameterableConfigLoader pcl = new ParameterableConfigLoader();
        pcl.loadXML(file);
        return pcl;
    }

    /**
     * @return instantiated Parameterable objects from {@link #getParameterableDatas()} method return dataParameters
     * @throws Exception
     */
    public List<Parameterable> instantiate() throws InvalidParameterInputException {
        List<Parameterable> parameterables = new LinkedList<Parameterable>();
        List<ParameterableData> parameterableDatas = getParameterableDatas();
        for (ParameterableData parameterableData : parameterableDatas) {
            parameterables.add(ParameterableImplement.instantiate(parameterableData,null));
        }
        return parameterables;
    }

}
