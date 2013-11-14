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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.configcreation.model;

import edu.sharif.ce.dml.common.logic.worker.ProcessInstance;
import edu.sharif.ce.dml.common.parameters.data.StringDataParameter;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.LazySelectOneParameterable;
import edu.sharif.ce.dml.common.parameters.logic.complex.MultipleSelectParameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.SelectOneParameterable;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.Parameterable;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.VariableParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.StringParameter;
import edu.sharif.ce.dml.common.util.DevelopmentLogger;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.control.SimulationRunner;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.Simulation;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 28, 2007
 * Time: 7:58:56 AM<br>
 */
public class Scenario extends ParameterableImplement implements Observer {
    private MultipleSelectParameter variables = new MultipleSelectParameter("variables");
    private ParameterableParameter simulationParameter = new ParameterableParameter();
    private int numberOfRun;
    private String title = "";

    public Scenario(Simulation simulation, int numberOfRun, String title) {
        this(new ParameterableParameter("simulationParameter", simulation), numberOfRun, title);
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        variables = (MultipleSelectParameter) parameters.get("variables");
        setSimulation((ParameterableParameter) parameters.get("simulationParameter"));
        numberOfRun = (Integer) parameters.get("numberofrun").getValue();
        title = (String) parameters.get("title").getValue();
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        parameters.put("simulationParameter", simulationParameter);
        parameters.put("variables", variables);
        parameters.put("numberofrun", new IntegerParameter("numberofrun", numberOfRun));
        parameters.put("title", new StringParameter("title", title));
        return parameters;
    }

    public Scenario() {
    }

    private void setSimulation(ParameterableParameter simulation) {
        this.simulationParameter = simulation;
        variables.setChoices(getAllVariableParameters(getSimulation().getCurrentModel()));
    }

    public Scenario(ParameterableParameter simulationParameter, int numberOfRun, String title) {
        setSimulation(simulationParameter);
        this.numberOfRun = numberOfRun;
        this.title = title;
        setName(title);
    }

    public String getTitle() {
        return title;
    }

    public void update(Observable o, Object arg) {
        //update variables
        variables.setChoices(getAllVariableParameters(getSimulation().getCurrentModel()));
    }

    private Map<String, Parameterable> getAllVariableParameters(Parameterable p) {
        Map<String, Parameterable> returnParams = new HashMap<String, Parameterable>();
        Map<String, Parameter> params = p.getParameters();
        //todo it should be codded in each parameter
        //todo it only works for simulationParameter objects
        for (String name : params.keySet()) {
            Parameter parameter = params.get(name);
            if (parameter instanceof DoubleParameter) {
                returnParams.put(name, new VariableParameter((DoubleParameter) parameter));
            }
            if (parameter instanceof IntegerParameter) {
                returnParams.put(name, new VariableParameter((IntegerParameter) parameter));
            } else if (parameter instanceof SelectOneParameterable) {
                returnParams.putAll(getAllVariableParameters(((SelectOneParameterable) parameter).getValue()));
            } else if (parameter instanceof LazySelectOneParameterable) {
                returnParams.putAll(getAllVariableParameters(((LazySelectOneParameterable) parameter).getValue()));
            }

        }
        return returnParams;
    }

    public int getNumberOfIterations() {
        List<Parameterable> selectedVariables = variables.getSelected();
        if (selectedVariables.size() == 0) {
            return numberOfRun;
        }
        int num = Integer.MAX_VALUE;
        for (Parameterable parameterable : variables.getSelected()) {
            num = Math.min(((VariableParameter) parameterable).getNumberOfRun(), num);
        }
        return num * numberOfRun;
    }

    public void run(SimulationRunner.ScenarioWorker scenarioWorker) {
        if (variables.getSelected().size() == 0) {
            Simulation simulation = getSimulation();
            Map<String, Parameter> simulationParams = simulation.getParameters();
            //update simulationParameter output file
            //nextStep simulationParameter
            for (int i = 0; i < numberOfRun; i++) {
                boolean success = false;
                long initTime = System.currentTimeMillis();
                simulation.setOutputPrefix(i + "-");
                try {
                    simulation.setParameters(simulationParams);
                    simulation.setCurrentTraceWriter(simulation.getTraceWriterConfig().getNextTraceWriter(
                            new LinkedList<StringDataParameter>(Parameter.getFlatDataParameters(getParameters())),
                            simulation.getTraceLabels()));

                    simulation.initializeAndPlay();
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                scenarioWorker.publishScenario(new ProcessInstance(simulation.getOutputFileName(),
                        success ? ProcessInstance.ProcessResult.success : ProcessInstance.ProcessResult.failure,
                        System.currentTimeMillis() - initTime));
            }
        } else {
            //have variables
            TreeMap<VariableParameter, Iterator> variableParameters = new TreeMap<VariableParameter, Iterator>();
            List<Parameterable> selectedVariables = variables.getSelected();
            StringBuffer variablesName = new StringBuffer();
            {
                //create variables concatenated name and variables list
                for (Parameterable parameterable : selectedVariables) {
                    variableParameters.put((VariableParameter) parameterable, ((VariableParameter) parameterable).iterator());
                    variablesName.append(parameterable.toString()).append("~");
                }
                variablesName.delete(variablesName.length() - 1, variablesName.length());
            }
            Simulation mySimulation = getSimulation();
            Map<String, Parameter> simulationParams = null;

//            String FirstTraceFileName = ((StringParameter) simulationParams.get("tracefilename")).getValue();
            while (true) {
                //find should be updated values
                Map<String, String> shouldBeUpdatedParameters = new HashMap<String, String>();
                boolean end = false;
                StringBuffer variableValueString = new StringBuffer();
                for (VariableParameter variableParameter : variableParameters.keySet()) {
                    Iterator iterator = variableParameters.get(variableParameter);
                    if (!iterator.hasNext()) {
                        //to match to minimum variable
                        end = true;
                        break;
                    }

                    String variableValue = iterator.next().toString();
                    shouldBeUpdatedParameters.put(variableParameter.toString(), variableValue);
                    variableValueString.append(variableValue).append("-");
                }
                if (end) {
                    return;
                }
                variableValueString.delete(variableValueString.length() - 1, variableValueString.length());
                //update parameterable data
                String prefix = "-" + variableValueString.toString().replaceAll("\\.", ",") + " ";
                try {
                    setParameterDeep(shouldBeUpdatedParameters, mySimulation);
                    simulationParams = mySimulation.getParameters();
                    simulationParams.put("Variables", new StringParameter("Variables", variablesName.toString()));
                    //update simulationParameter output file
                    //nextStep simulationParameter
                    for (int i = 0; i < numberOfRun; i++) {
                        long initTime = 0;
                        boolean success = false;
                        try {
                            initTime = System.currentTimeMillis();
                            mySimulation.setParameters(simulationParams);
                            mySimulation.setOutputPrefix(i + prefix);
                            mySimulation.setCurrentTraceWriter(mySimulation.getTraceWriterConfig().getNextTraceWriter(
                                    new LinkedList<StringDataParameter>(Parameter.getFlatDataParameters(getParameters())),
                                    mySimulation.getTraceLabels()));

                            mySimulation.initializeAndPlay();
                            success = true;
                        } catch (InvalidParameterInputException e) {
                            e.printStackTrace();
                        }

                        scenarioWorker.publishScenario(new ProcessInstance(mySimulation.getOutputFileName(),
                                success ? ProcessInstance.ProcessResult.success : ProcessInstance.ProcessResult.failure,
                                System.currentTimeMillis() - initTime));
                    }
                } catch (InvalidParameterInputException e) {
                    DevelopmentLogger.logger.error("cannot set variable values to some parameters", e);
                    for (int i = 0; i < numberOfRun; i++) {
                        scenarioWorker.publishScenario(new ProcessInstance(i + prefix,
                                false ? ProcessInstance.ProcessResult.success : ProcessInstance.ProcessResult.failure,
                                0));
                    }
                }

            }
        }
    }

    private void setParameterDeep(Map<String, String> shouldBeUpdated, Parameterable parameterable) throws InvalidParameterInputException {
        Map<String, Parameter> params = parameterable.getParameters();
        //todo it should be codded in each parameter
        //todo it only works for simulationParameter objects
        for (String keyName : shouldBeUpdated.keySet()) {
            Parameter parameter = params.get(keyName);
            if (parameter != null) {
                if (parameter instanceof DoubleParameter) {
                    ((DoubleParameter) parameter).setValue(new Double(shouldBeUpdated.get(keyName)));
                } else if (parameter instanceof IntegerParameter) {
                    ((IntegerParameter) parameter).setValue((int) Double.parseDouble(shouldBeUpdated.get(keyName)));
                }
            }
        }
        for (Parameter parameter : params.values()) {
            if (parameter instanceof SelectOneParameterable) {
                setParameterDeep(shouldBeUpdated, ((SelectOneParameterable) parameter).getValue());
            } else if (parameter instanceof LazySelectOneParameterable) {
                setParameterDeep(shouldBeUpdated, ((LazySelectOneParameterable) parameter).getValue());
            }
        }
        parameterable.setParameters(params);
    }

    private Simulation getSimulation() {
        return (Simulation) simulationParameter.getValue();
    }
}
