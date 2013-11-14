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

package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.control;

import edu.sharif.ce.dml.common.logic.worker.MultiTaskSwingWorker;
import edu.sharif.ce.dml.common.logic.worker.ProcessInstance;
import edu.sharif.ce.dml.common.parameters.data.ParameterableConfigFileException;
import edu.sharif.ce.dml.common.parameters.data.ParameterableConfigLoader;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.util.InvalidRequiredInputFileException;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.configcreation.model.Scenario;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.configcreation.model.Scenarios;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.Simulation;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.ui.TreeUIForm;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: Nov 4, 2006
 * Time: 9:31:29 PM
 * <br/> The main class of Appliation that loads data files and runs simulation(s) according to parameters.
 */
public class SimulationRunner {


    /**
     * runs graphical simulation using the properties file uiparameters.
     */
    public Window runGraphical() throws InvalidRequiredInputFileException, ParameterableConfigFileException, InvalidParameterInputException {
        return runGraphicalSimulation();
    }


    /**
     * uses constructors of each Model and Map to create the corresponding.<br/>
     * it is like Factory Method Pattern
     */
    private Window runGraphicalSimulation() throws InvalidRequiredInputFileException, ParameterableConfigFileException, InvalidParameterInputException {

        Simulation simulation = Simulation.getTemplate();
        TreeUIForm treeUIForm = new TreeUIForm("Mobility Generator Configuration",simulation,true);
        treeUIForm.setVisible(true);

        return treeUIForm;
    }

    /**
     * uses constroctors of each Model and Map to create the corresponding.<br/>
     * it is like Factory Method Pattern
     *
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void runTextualSimulation(String scenarioFileName) throws InvalidRequiredInputFileException, ParameterableConfigFileException {

        try {
            ParameterableConfigLoader pcl2 = ParameterableConfigLoader.load(new File(scenarioFileName));
            Scenarios scenariosParameterable = (Scenarios) pcl2.instantiate().get(0);
            List<Scenario> scenarios = scenariosParameterable.getScenarios();
            int numberOfIterations = 0;
            for (Scenario scenario : scenarios) {
                numberOfIterations+=scenario.getNumberOfIterations();
            }
            //create and nextStep worker
            if (numberOfIterations > 0) {
                new ScenarioWorker(numberOfIterations, scenarios).execute();
            }
        } catch (InvalidParameterInputException e) {
            e.printStackTrace();
        }


    }

    public class ScenarioWorker extends MultiTaskSwingWorker {
        List<Scenario> scenarios = new LinkedList<Scenario>();

        protected ScenarioWorker(int noOfTasks, List<Scenario> scenarios) {
            super(noOfTasks);
            this.scenarios = scenarios;
        }

        protected void doWork() throws Exception {
            for (Scenario scenario : scenarios) {
                scenario.run(this);
            }
        }

        public void publishScenario(ProcessInstance processInstance) {
            publish(processInstance);
        }
    }

}
