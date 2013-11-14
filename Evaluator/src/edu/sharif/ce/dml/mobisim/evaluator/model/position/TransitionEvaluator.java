package edu.sharif.ce.dml.mobisim.evaluator.model.position;

import edu.sharif.ce.dml.common.data.entity.DataLocation;
import edu.sharif.ce.dml.common.data.entity.NodeShadow;
import edu.sharif.ce.dml.common.data.entity.SnapShotData;
import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Masoud
 * Date: 12/25/10
 * Time: 4:51 PM
 * <br/> find out about transitions in the trace and generate the statistics
 */
public class TransitionEvaluator extends MobilityEvaluator {
    double transitionDirectionThreshold = 0;
    double transitionSpeedThreshold = 0;
    //boolean reflectableTransition = false;
    double lastTime=0;

    Map<NodeShadow, TransitionStatistics> nodeTransitionStatisticsMap = new HashMap<NodeShadow, TransitionStatistics>();

    @Override
    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        super.setParameters(parameters);
        transitionDirectionThreshold = ((DoubleParameter) parameters.get("transitiondirectionthreshold")).getValue();
        transitionSpeedThreshold = ((DoubleParameter) parameters.get("transitionspeedthreshold")).getValue();
        //   reflectableTransition = ((BooleanParameter) parameters.get("reflectabletransition")).getValue();
    }

    @Override
    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = super.getParameters();
        parameters.put("transitiondirectionthreshold", new DoubleParameter("transitiondirectionthreshold", transitionDirectionThreshold));
        parameters.put("transitionspeedthreshold", new DoubleParameter("transitionspeedthreshold", transitionSpeedThreshold));
//        parameters.put("reflectabletransition", new BooleanParameter("reflectabletransition", reflectableTransition));
        return parameters;
    }

    @Override
    protected void evaluate(SnapShotData snapShot) {
        for (NodeShadow nodeShadow : snapShot.getNodeShadows()) {
            TransitionStatistics transitionStatistics = nodeTransitionStatisticsMap.get(nodeShadow);
            if (transitionStatistics == null) {
                //initialization
                nodeTransitionStatisticsMap.put(nodeShadow, new TransitionStatistics(new Transition(nodeShadow.getSpeed(),
                        snapShot.getTime(), nodeShadow.getLocation(), nodeShadow.getDirection(),nodeShadow.getDirection())));
                return;
            }
            //add data
            transitionStatistics.addData(nodeShadow, snapShot.getTime());
        }
        lastTime=snapShot.getTime();
    }

    @Override
    public void reset() {
        nodeTransitionStatisticsMap.clear();
    }

    public List print() {
        for (TransitionStatistics transitionStatistics : nodeTransitionStatisticsMap.values()) {
            transitionStatistics.updateStatistics(lastTime);
        }

        double averageTransitionLength = 0;
        double averageTransitionTime = 0;
        double averageTransitionDirectionChange = 0;
        double averageTransitionSpeed = 0;
        int averageNumberOfTransition = 0;

        for (TransitionStatistics transitionStatistics : nodeTransitionStatisticsMap.values()) {
            averageNumberOfTransition += transitionStatistics.getNumberOfTransitions();
            averageTransitionLength += transitionStatistics.getAverageTransitionLength();
            averageTransitionSpeed += transitionStatistics.getAverageTransitionSpeed();
            averageTransitionDirectionChange += transitionStatistics.getAverageTransitionDirectionChange();
            averageTransitionTime += transitionStatistics.getAverageTransitionTime();
        }

        int size = nodeTransitionStatisticsMap.size();
        return Arrays.asList(averageTransitionTime / size,
                averageTransitionLength / size,
                averageTransitionDirectionChange / size,
                averageTransitionSpeed / size,
                averageNumberOfTransition / size);
    }

    public List<String> getLabels() {
        return Arrays.asList("Transition Time", "Transition Length", "Transition Direction Change",
                "Transition Speed", "number of Transition");
    }

    @Override
    public String toString() {
        return "Transition Evaluation";
    }

    @Override
    public String getName() {
        return toString();
    }

    private class TransitionStatistics {
        private int numberOfTransitions = 0;
        private double transitionTimeSum = 0;
        private double transitionDirectionChangeSum = 0;
        private double transitionLengthSum = 0;
        private double transitionSpeedSum = 0;
        Transition currentTransition = null;
        Transition lastTransition = null;

        private TransitionStatistics(Transition currentTransition) {
            this.currentTransition = currentTransition;
        }

        double getAverageTransitionTime() {
            return transitionTimeSum / numberOfTransitions;
        }

        double getAverageTransitionDirectionChange() {
            return transitionDirectionChangeSum / numberOfTransitions;
        }

        double getAverageTransitionLength() {
            return transitionLengthSum / numberOfTransitions;
        }

        double getAverageTransitionSpeed() {
            return transitionSpeedSum / numberOfTransitions;
        }

        int getNumberOfTransitions() {
            return numberOfTransitions;
        }

        void setNewTransition(Transition newTransition) {
            updateStatistics(newTransition.getStartTime());
            lastTransition = currentTransition;
            currentTransition = newTransition;
        }

        private void updateStatistics(double endTime) {
            transitionTimeSum += endTime - currentTransition.getStartTime();
            transitionDirectionChangeSum += currentTransition.getDirectionChange();
            transitionSpeedSum += currentTransition.getSpeed();
            transitionLengthSum += currentTransition.getLength();
            numberOfTransitions++;
        }


        public void addData(NodeShadow nodeShadow, double time) {
            try {
                currentTransition.addNodeData(nodeShadow);
            } catch (NewTransitionNeeded newTransitionNeeded) {
                Transition newTransition = new Transition(nodeShadow.getSpeed(),
                        time, nodeShadow.getLocation(), currentTransition.getDirection(),nodeShadow.getDirection());
                setNewTransition(newTransition);
            }
        }
    }

    private class Transition {
        private double directionChange = 0;
        private double speed = 0;
        private double startTime = 0;
        private double length = 0;
        private DataLocation lastLoc;
        private int numberOfValues = 0;
        private double lastTransitionDirection = 0;

        private Transition(double speed, double startTime, DataLocation lastLoc, double lastTransitionDirection, double direction) {
            this.speed = speed;
            this.startTime = startTime;
            this.lastLoc = lastLoc;
            this.lastTransitionDirection = lastTransitionDirection;
            this.directionChange=calibrateDirection(direction-lastTransitionDirection);
            numberOfValues++;
        }

        public void addNodeData(NodeShadow nodeShadow) throws NewTransitionNeeded {
            //calculate direction change
            double newDirectionChange = nodeShadow.getDirection() - lastTransitionDirection;
            newDirectionChange = calibrateDirection(newDirectionChange);
            //check if a new transition is needed
            if (transitionSpeedThreshold > 0) {
                if (Math.abs(getSpeed() - nodeShadow.getSpeed()) > transitionSpeedThreshold) {
                    throw new NewTransitionNeeded();
                }
            }
            if (transitionDirectionThreshold > 0) {
                if (Math.abs(newDirectionChange - getDirectionChange()) > transitionDirectionThreshold) {
                    throw new NewTransitionNeeded();
                }
            }
            //add to the records
            directionChange += newDirectionChange;
            speed += nodeShadow.getSpeed();
            length += nodeShadow.getLocation().getLength(lastLoc);
            lastLoc=nodeShadow.getLocation();
            numberOfValues++;
        }

        private double calibrateDirection(double direction) {
            if (direction < 0) {
                direction += 2 * Math.PI;
            } else if (direction > 2 * Math.PI) {
                direction -= 2 * Math.PI;
            }
            if (direction < 0 || direction > 2 * Math.PI) {
                return calibrateDirection(direction);
            }
            return direction;
        }

        public double getDirectionChange() {
            return directionChange / numberOfValues;
        }

        public double getDirection() {
            return calibrateDirection(lastTransitionDirection + directionChange);
        }

        public double getSpeed() {
            return speed / numberOfValues;
        }

        public double getStartTime() {
            return startTime;
        }

        public double getLength() {
            return length;
        }
    }

    private class NewTransitionNeeded extends Exception {

    }
}

