package xbot.common.math;

public class DoubleInterpolator {
    double[] inputVariables = {0};
    double[] outputVariables = {0};

    public DoubleInterpolator(double[] inputVariables, double[] outputVariables){
        this.inputVariables = inputVariables;
        this.outputVariables = outputVariables;
    }

    public DoubleInterpolator(){
        this.inputVariables = getInputVariables();
        this.outputVariables = getOutputVariables();
    }

    //why am i doing algebra :sob:
    public double getYIntercept(double slope, double x1, double y1){
        return  y1 - slope * x1;
    }


    //estimates the RPM we need to fire at our distance based on prerecorded data
    public double getInterpolatedOutputVariable(double inputVariable) {
        double secantLineSlope;
        double yIntercept;

        for (int i = 0; i < inputVariables.length - 1; i++) {
            //logic to find where currentPosition lies in the array
            if (inputVariables[i] == inputVariable){
                return outputVariables[i];
            }
            //bandage case
            if(inputVariable == inputVariables[inputVariables.length-1]){
                return outputVariables[inputVariables.length - 1];
            }
            else if (inputVariables[i] < inputVariable && inputVariable < inputVariables[i + 1]) {
                //secant line calculator
                secantLineSlope =
                        (outputVariables[i + 1] - outputVariables[i]) / (inputVariables[i + 1] - inputVariables[i]);
                yIntercept = getYIntercept(secantLineSlope, inputVariables[i], outputVariables[i]);
                return secantLineSlope * inputVariable + yIntercept;
            }
        }
        //returns ZERO if our current distance is further than the greatest range tested on the robot
        return 0;
    }

    public double[] getOutputVariables() {
        return outputVariables;
    }

    public double[] getInputVariables() { return inputVariables; }
}
