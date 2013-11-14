package edu.sharif.ce.dml.common.parameters.logic.parameterable.random;

import edu.sharif.ce.dml.common.parameters.logic.Parameter;
import edu.sharif.ce.dml.common.parameters.logic.exception.InvalidParameterInputException;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.ParameterableImplement;
import edu.sharif.ce.dml.common.parameters.logic.parameterable.random.levy.LevyDistribution;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleArrayParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.DoubleParameter;
import edu.sharif.ce.dml.common.parameters.logic.primitives.StringParameter;
import jsc.distributions.Distribution;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 27, 2010
 * Time: 11:39:09 AM
 */
public class RandomParameterable extends ParameterableImplement {
    protected Distribution distribution;
    protected double value = 0;
    double[] params = new double[]{0, 0};
    double bias=0;
    double scale=1;

    public Distribution getDistribution() {
        return distribution;
    }

    public void setParameters(Map<String, Parameter> parameters) throws InvalidParameterInputException {
        String distName = ((StringParameter) parameters.get("distribution")).getValue();
        params = (double[]) ((DoubleArrayParameter) parameters.get("params")).getValue();
        bias = ((DoubleParameter) parameters.get("bias")).getValue();
        scale = ((DoubleParameter) parameters.get("scale")).getValue();
        //find distribution class
        if (!distName.contains(".")) {
            if (distName.equals("Fixed")) {
                distName = FixedDistribution.class.getCanonicalName();
            } else if (distName.equals("Levy")) {
                distName = LevyDistribution.class.getCanonicalName();
            } else {
                //find it in jsc package
                distName = "jsc.distributions." + distName;
            }
        }
        //it may have one parameter constructor or two parameter
        try {
            Class<?> distClass = Class.forName(distName);
            List<Double> paramsList = new ArrayList<Double>(params.length);
            for (double param : params) {
                paramsList.add(param);
            }
            try {
                Constructor<?>[] constructors = distClass.getConstructors();
                Map<Integer, Constructor<?>> paramSizeconstructors = new TreeMap<Integer, Constructor<?>>();
                for (Constructor<?> constructor : constructors) {
                    int parameterSize = constructor.getParameterTypes().length;
                    paramSizeconstructors.put(parameterSize, constructor);
                }
                //user entered params number has priority
                if (paramSizeconstructors.containsKey(params.length)) {
                    createDistribution(paramSizeconstructors.get(params.length));
                } else {//uses the least needed parameters constructor
                    Iterator<Integer> iterator = paramSizeconstructors.keySet().iterator();
                    Integer size = iterator.next();
                    if (params.length < size) {
                        extendParams(paramsList, size);
                    }
                    createDistribution(paramSizeconstructors.get(size));
                }
            } catch (IllegalArgumentException e) {
                throw new RandomClassInitException(e.getMessage(), getName(), distClass, paramsList, e);
            } catch (InstantiationException e) {
                throw new RandomClassInitException(e.getMessage(), getName(), distClass, paramsList, e);
            } catch (IllegalAccessException e) {
                throw new RandomClassInitException(e.getMessage(), getName(), distClass, paramsList, e);
            } catch (InvocationTargetException e) {
                throw new RandomClassInitException(e.getMessage(), getName(), distClass, paramsList, e);
            }
            if (distribution == null) {
                throw new RandomClassInitException("Appropriate constructor not found for distribution" + distName, getName(), distClass, paramsList);
            }
        } catch (ClassNotFoundException e) {
            throw new InvalidParameterInputException("Class not found: " + e.getMessage(), getName(), distName, e);
        }
    }

    public void setSeed(long seed){
        distribution.setSeed(seed);
    }

    /**
     * has been implemented because I cannot pass params array to the constructor as a variable sized argument
     *
     * @param constructor
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void createDistribution(Constructor<?> constructor) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        //convert int params
        Class<?>[] paramClasses = constructor.getParameterTypes();
        Object[] convertedParams = new Object[paramClasses.length];
        int i = 0;
        for (Class<?> aClass : paramClasses) {
            if (aClass.equals(long.class)) {
                convertedParams[i] = (long) params[i];
            } else if (aClass.equals(int.class)) {
                convertedParams[i] = (int) params[i];
            } else {
                convertedParams[i] = params[i];
            }
            i++;
        }
        switch (convertedParams.length) {
            case 0:
                distribution = (Distribution) constructor.newInstance();
                break;
            case 1:
                distribution = (Distribution) constructor.newInstance(convertedParams[0]);
                break;
            case 2:
                distribution = (Distribution) constructor.newInstance(convertedParams[0], convertedParams[1]);
                break;
            case 3:
                distribution = (Distribution) constructor.newInstance(convertedParams[0], convertedParams[1], convertedParams[2]);
                break;
            case 4:
                distribution = (Distribution) constructor.newInstance(convertedParams[0], convertedParams[1], convertedParams[2], convertedParams[3]);
                break;
        }
    }

    private void extendParams(List<Double> paramsList, int extendedSize) {
        while (paramsList.size() < extendedSize) {
            paramsList.add(0d);
        }
        params = new double[paramsList.size()];
        int i = 0;
        for (Double aDouble : paramsList) {
            params[i] = aDouble;
            i++;
        }
    }

    public double initValue() {
        value = Math.abs((distribution.random()*scale)+bias);
        return value;
    }

    public double getValue() {
        return value;
    }

    public Map<String, Parameter> getParameters() {
        Map<String, Parameter> parameters = new HashMap<String, Parameter>();
        String distName = "";
        if (distribution != null) {
            distName = distribution.getClass().getName();
        }
        parameters.put("distribution", new StringParameter("distribution", distName));
        parameters.put("params", new DoubleArrayParameter("params", params));
        parameters.put("bias", new DoubleParameter("bias",Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY,0.1, bias));
        parameters.put("scale", new DoubleParameter("scale", scale));
        return parameters;
    }
}
