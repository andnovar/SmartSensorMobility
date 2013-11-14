package edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.filemodel;

import edu.sharif.ce.dml.common.data.entity.NodeShadow;
import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.data.trace.filter.FileFilters;
import edu.sharif.ce.dml.common.logic.entity.Location;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.ParameterableParameter;
import edu.sharif.ce.dml.common.parameters.logic.complex.SelectOneParameterable;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.FileParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.IntegerParameter;
import edu.sharif.ce.dml.common.util.io.TraceUsingFactory;
import edu.sharif.ce.dml.common.util.io.loader.User;
import edu.sharif.ce.dml.common.util.io.loader.bulk.BulkUsingHandler;
import edu.sharif.ce.dml.common.util.io.loader.stream.StreamUsingHandler;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.logic.GeneratorNode;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.Model;
import edu.sharif.ce.dml.mobisim.mobilitygenerator.simulation.model.ModelInitializationException;

import java.io.File;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 7, 2010
 * Time: 12:54:52 PM
 */
public class FileModel extends Model {
    private ParameterableParameter fileParameter = new ParameterableParameter();
    private long currentTime = 1;
    private NullUsingHandler nullUsingHandler;

    @Override
    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("inputfile", fileParameter);
        return parameters;
    }

    @Override
    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        fileParameter = (ParameterableParameter) parameters.get("inputfile");
    }

    public void init(Map<String, String> configurations, File inputFile) throws InvalidParameterInputException {
        Map<String, Parameter> parameters = getParameters();
        String name;
        //set initializer to nullinitializer
        {
            name = "locationinitializer";
            SelectOneParameterable locInit = (SelectOneParameterable) parameters.get(name);
            locInit.setSelected("Null");
        }
        //set map
        {
            //it has only one choice and is selected in the template
            Map<String, Parameter> mapParameters = getMap().getParameters();
            name = "width";
            ((IntegerParameter) mapParameters.get(name)).setStringValue(configurations.get(name));
            name = "height";
            ((IntegerParameter) mapParameters.get(name)).setStringValue(configurations.get(name));
            getMap().setParameters(mapParameters);
        }
        //set inputfile
        {
            name = "inputfile";
            FileParameter inputFileParameter = (FileParameter) parameters.get(name).getValue();
            inputFileParameter.setValue(inputFile);
        }
        setParameters(parameters);
    }

    @Override
    protected void initNode(GeneratorNode node) throws ModelInitializationException {
        SnapShotData currentSnapShotData = nullUsingHandler.data[(int) currentTime - 1];
        NodeShadow[] nodeShadows = currentSnapShotData.getNodeShadows();
        NodeShadow nodeShadow = nodeShadows[Integer.parseInt(node.getName())];
        node.setLocation(new Location(nodeShadow.getLocation()));
        node.setSpeed(nodeShadow.getSpeed());
        node.setDirection(nodeShadow.getDirection());
    }

    @Override
    protected void getNextStep(double timeStep, GeneratorNode node) {
        
    }

    @Override
    public void updateNodes(double timeStep) {
        currentTime += timeStep;
        if (nullUsingHandler.data.length >= currentTime) {
            SnapShotData currentSnapShotData = nullUsingHandler.data[(int) currentTime - 1];
            NodeShadow[] nodeShadows = currentSnapShotData.getNodeShadows();
            for (GeneratorNode node : modelNodes) {
                NodeShadow nodeShadow = nodeShadows[node.getIntName()];
                Location loc = node.getDoubleLocation();
                loc.pasteCoordination(nodeShadow.getLocation());
                node.setDirection(nodeShadow.getDirection());
                node.setSpeed(nodeShadow.getSpeed());
                node.setRange(nodeShadow.getRange());
            }
        }
        updateRanges();
    }

    @Override
    public void initNodes() throws ModelInitializationException {
        currentTime = 1;
        nullUsingHandler = new NullUsingHandler();
        User<SnapShotData> user = new TraceUsingFactory<SnapShotData>(FileFilters.getTraceFilters(), false).
                getDataUser(getInputFile(), nullUsingHandler);
        //todo
        user.run();
        super.initNodes();
    }

    private File getInputFile() {
        return ((FileParameter) fileParameter.getValue()).getValue();
    }

    private class NullUsingHandler implements StreamUsingHandler<SnapShotData>, BulkUsingHandler<SnapShotData> {
        private SnapShotData[] data;

        public void use(SnapShotData[] data) {
            this.data = data;
        }

        public void loadData(SnapShotData data) {
        }

        public void useData() {
        }

        public void setConfiguration(Map<String, String> conf) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void stopLoading() {
        }

        public void endLoading() {
        }

        public void startLoading() {
        }
    }

}
