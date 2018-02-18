package xbot.common.math;

public class MathUtils
{
    public static double constrainDouble(double value, double lowerBound, double upperBound)
    {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }
    
    public static int constrainInt(int value, int lowerBound, int upperBound)
    {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }
    
    public static double scaleDouble(double value, double oldMin, double oldMax, double newMin, double newMax)
    {
        value -= oldMin;
        value /= oldMax - oldMin;
        value *= newMax - newMin;
        value += newMin;
        return value;
    }
    
    public static double constrainDoubleToRobotScale(double value) {
        return constrainDouble(value, -1, 1);
    }
    
    public static double squareAndRetainSign(double value) {
        double squared = Math.pow(value, 2);
        return value < 0 ? -squared : squared;
    }
}
