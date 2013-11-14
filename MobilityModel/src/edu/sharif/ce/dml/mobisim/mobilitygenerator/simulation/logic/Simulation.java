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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic;

import edu.sharif.ce.dml.common.data.configfilter.ConfigFileFilter;
import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.data.trace.TraceOwner;
import edu.sharif.ce.dml.common.data.trace.TraceWriter;
import edu.sharif.ce.dml.common.data.trace.config.AbstractTraceWriterConfig;
import edu.sharif.ce.dml.common.data.trace.config.NullTraceWriterConfig;
import edu.sharif.ce.dml.common.data.trace.config.TraceWriterConfig;
import edu.sharif.ce.dml.common.data.trace.filter.FileFilters;
import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.data.ParameterableConfigFileException;
import edu.sharif.ce.dml.common.parameters.data.ParameterableConfigLoader;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.SelectOneParameterable;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.common.util.FileManager;
import edu.sharif.ce.dml.common.util.InvalidRequiredInputFileException;
import edu.sharif.ce.dml.common.util.PublicConfig;
import edu.sharif.ce.dml.common.util.io.TraceUsingFactory;
import edu.sharif.ce.dml.common.util.io.loader.FileLoader;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ModelInitializationException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.filemodel.FileModel;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.shower.MiniShower;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 5, 2006
 * Time: 8:30:32 PM
 * <br/>represents a simulation in this application.
 * <br/> list of uiparameters: <br/>
 * {@link Integer} nodenumber<br/>
 * {@link Long} maxsimulationtime<br/>
 */
public class Simulation extends ParameterableImplement implements TraceOwner, WindowListener {
    protected ParameterableParameter traceWriterConfig;
    protected SelectOneParameterable model;
    protected int maxSimulationTime;
    protected int nodeNumber;
    private TraceWriter currentTraceWriter;
    private boolean simulationSetup = false;
    public static final String GRAPHICAL_CONFIG_FILE = "graphicalconfigFile2";

    ////
    private MiniShower<SnapShotData> shower;
    protected boolean isInPause = true;

    private static final List<String> traceLabels = Arrays.asList("Time");
    private SnapShotData graphicalSnapShotData;
    private static Random randomGenerator;
    private long inputSeed;

    public Simulation() {
        super();
        maxSimulationTime = 0;
        nodeNumber = 0;
        traceWriterConfig = new ParameterableParameter();
        model = new SelectOneParameterable(true);
        try {//TODO move it
            String seed = PublicConfig.getInstance().getConfigFilePropertyManager().readProperty("seed");
            inputSeed = Long.parseLong(seed.replaceAll("\\D", ""));
        } catch (InvalidRequiredInputFileException e) {
            e.printStackTrace();
        }
    }

    /**
     * don't use in set parameters
     *
     * @return
     */
    public static double getDoubleRandomNumber() {
        return randomGenerator.nextDouble();
    }

    /**
     * don't use in set parameters
     *
     * @return
     */
    public static long getLongRandomNumber() {
        return randomGenerator.nextLong();
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {

        nodeNumber = (Integer) parameters.get("nodenumber").getValue();
        maxSimulationTime = (Integer) parameters.get("maxsimulationtime").getValue();
        traceWriterConfig = (ParameterableParameter) parameters.get("tracewriterconfig");
        model = (SelectOneParameterable) parameters.get("models");
    }

    public java.util.Map<String, Parameter> getParameters() {
        java.util.Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("nodenumber", new IntegerParameter("nodenumber", nodeNumber));
        parameters.put("maxsimulationtime", new IntegerParameter("maxsimulationtime", Integer.MAX_VALUE, 0, 100, maxSimulationTime));
        parameters.put("tracewriterconfig", traceWriterConfig);
        parameters.put("models", model);

        return parameters;
    }

    public static Simulation getTemplate() throws InvalidRequiredInputFileException, ParameterableConfigFileException, InvalidParameterInputException {
        String fileName = PublicConfig.getInstance().getConfigFilePropertyManager().readProperty("configFile");
        java.util.List<Parameterable> loadedParameterables = ParameterableConfigLoader.load(
                FileManager.getInstance().getFile(fileName + ".xml", GRAPHICAL_CONFIG_FILE, true, new javax.swing.filechooser.FileFilter[]{
                        ConfigFileFilter.getXMLInstance()}, true)).instantiate();
        return (Simulation) loadedParameterables.get(0);
    }

    public static Simulation getSimulationFromTraceFile(File selectedFile) throws InvalidParameterInputException, ParameterableConfigFileException, InvalidRequiredInputFileException {
        FileLoader<SnapShotData> fileLoader = new TraceUsingFactory<SnapShotData>(FileFilters.getTraceFilters(), true).getFileLoader(selectedFile);
        Map<String, String> configurations = new TreeMap<String, String>(fileLoader.getConfigurations());
        Simulation simulation = getTemplate();
        Map<String, Parameter> parameters = simulation.getParameters();
        String name = "nodenumber";
        ((IntegerParameter) parameters.get(name)).setStringValue(configurations.get(name));
        name = "maxsimulationtime";
        ((IntegerParameter) parameters.get(name)).setStringValue(configurations.get(name));
        //model
        {
            name = "models";
            SelectOneParameterable modelParameterable = (SelectOneParameterable) parameters.get(name);
            modelParameterable.setSelected("File Model");
            FileModel fileModel = (FileModel) modelParameterable.getValue();
            fileModel.init(configurations, selectedFile);
        }
        //tracewriter config
        {
            name = "tracewriterconfig";
            ParameterableParameter traceWriterConfigParameter = new ParameterableParameter(name, new NullTraceWriterConfig());
            parameters.put(name, traceWriterConfigParameter);
        }

        simulation.setParameters(parameters);
        return simulation;
    }

    /**
     * used independently usually for graphical simulation
     *
     * @param shower2
     */
    public void setShower(MiniShower<SnapShotData> shower2) {
        this.shower = shower2;
        shower.addRestartActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        shower.addStartPauseAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isInPause) {
                    pause();
                } else {
                    isInPause = false;
                    if (simulationSetup) {
                        MyTimer.play(shower.getSpeedRatio());
                    } else {
                        simulationSetup = true;
                        MyTimer.reset();
                        System.gc();
                        setCurrentTraceWriter(getTraceWriterConfig().getNextTraceWriter(new LinkedList<StringDataParameter>(
                                Parameter.getFlatDataParameters(getParameters())), getTraceLabels()));
                        shower.setOutputFile(new File(getOutputFileName()));
                        initializeAndPlay();
                    }
                }
            }
        });
        shower.addWindowListener(this);
        edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.maps.Map map = getCurrentModel().getMap();
        shower.setSize2(map.getWidth(), map.getHeight());
    }

    public void reset() {
        if (simulationSetup) {
            pause();
        }
        graphicalSnapShotData = null;
        currentTraceWriter = null;
        simulationSetup = false;
    }

    private TraceWriter getTraceWriter() {
        return currentTraceWriter;
    }

    public void setCurrentTraceWriter(TraceWriter currentTraceWriter) {
        this.currentTraceWriter = currentTraceWriter;
    }

    /**
     * @return current {@link edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model} which this simulation works with
     */
    public Model getCurrentModel() {
        return ((Model) model.getValue());
    }

    /**
     * @return maximum iteration that this simulation will nextStep.
     */
    public long getMaxSimulationTime() {
        return maxSimulationTime;
    }

    public AbstractTraceWriterConfig getTraceWriterConfig() {
        return (AbstractTraceWriterConfig) traceWriterConfig.getValue();
    }

    /////////////////////////////simulation part

    private void pause() {
        //this code should not be here
        (getTraceWriter()).flush();
        MyTimer.pause();
        isInPause = true;
    }

    /**
     * actual work of simulation is in this method after initialization.
     */
    public void initializeAndPlay() {
        if (inputSeed == 0) {
            randomGenerator = new Random();
        } else {
            randomGenerator = new Random(inputSeed);
        }
        ParameterableImplement.resetParameters(this);
        List<GeneratorNode> nodes = new LinkedList<GeneratorNode>();
        for (int i = 0; i < nodeNumber; i++) {
            GeneratorNode node = new GeneratorNode();
            node.setName("" + i);
            nodes.add(node);
        }
        Model currentModel = getCurrentModel();
        try {
            currentModel.setModelNodes(nodes);
            currentModel.initNodes();

            if (shower != null) {//so is graphical
                MyTimer.play(this, shower.getSpeedRatio());
            } else {
                MyTimer.resetTime();
                for (int i = 1; i <= maxSimulationTime; i++) {
                    MyTimer.incTime();
                    this.updateNodes();
                }
                flushAndClose();
            }
        } catch (ModelInitializationException e) {
            if (shower != null) {
                pause();
                shower.pauseByForce();
                JOptionPane.showMessageDialog(shower, e.getMessage());
                e.printStackTrace();
            } else {
                throw new UnfinishedSimulationException(e.getMessage());
            }
        }
    }


    /**
     * it used by to update all nodes in the simulation space
     */
    public void updateNodes() {
        getCurrentModel().updateNodes((int) MyTimer.calculationTimeStep);
        writeTraces(getTraceWriter());
    }

    /**
     * updates the view of configuration panel and repainting presentation components.
     *
     * @param time current simulation time.
     */
    public void updateView(long time) {
        shower.setCurrentTime(time);
        if (graphicalSnapShotData == null) {
            graphicalSnapShotData = new SnapShotData(nodeNumber);
            for (GeneratorNode node : getCurrentModel().getModelNodes()) {
                graphicalSnapShotData.addNodeShadows(node);
            }
        }
        graphicalSnapShotData.setTime(time);
        shower.setSnapShot(graphicalSnapShotData);
        shower.repaint();
    }

    ////////////////////////////output part

    private void flushAndClose() {
        TraceWriter traceWriter = getTraceWriter();
        if (traceWriter != null) {
            traceWriter.flushAndClose();
        }
    }

    public void writeTraces(TraceWriter writer) {
        Model m = getCurrentModel();
        m.writeTraces(writer, Arrays.asList(MyTimer.getTime()), new Location(0, 0));
    }

    public String[] getTraceLabels() {
        List<String> labels = new LinkedList<String>();
        labels.addAll(traceLabels);
        labels.addAll(new GeneratorNode().getLabels());
        labels.addAll(getCurrentModel().getLabels());
        return labels.toArray(new String[labels.size()]);
    }

    public void setOutputPrefix(String s) {
        ((TraceWriterConfig) getTraceWriterConfig()).setPrefix(s);
    }

    public String getOutputFileName() {
        return currentTraceWriter.getOutputString();
    }

    /////////////////////window listener for flushing on close event

    public void windowOpened(WindowEvent e) {

    }

    public void windowClosing(WindowEvent e) {
        shower.pauseByForce();
        if (simulationSetup) {
            pause();
            flushAndClose();
        }
    }

    public void windowClosed(WindowEvent e) {

    }

    public void windowIconified(WindowEvent e) {

    }

    public void windowDeiconified(WindowEvent e) {

    }

    public void windowActivated(WindowEvent e) {

    }

    public void windowDeactivated(WindowEvent e) {

    }
}
