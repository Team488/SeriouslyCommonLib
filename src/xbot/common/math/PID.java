package xbot.common.math;

/**
 * This PID was extracted from WPILib. It has all the same functionality, but
 * does not run on its own separate thread.
 */
public class PID
{
    private double m_error = 0.0;
    private double m_maximumOutput = 1.0; // |maximum output|
    private double m_minimumOutput = -1.0; // |minimum output|
    private double m_result;
    private double m_prevError;
    private double m_totalError;
    private double m_targetInputValue;
    private double m_currentInputValue;
    
    private double m_derivativeValue;
    private boolean errorIsSmall = false;
    private boolean derivativeIsSmall = false;
    double errorTolerance = -1;
    double derivativeTolerance = -1;

    /**
     * Resets the PID controller.
     */
    public void reset()
    {
        m_prevError = 0;
        m_totalError = 0;
        errorIsSmall = false;
        derivativeIsSmall = false;
    }
    
    /**
     * Set how close the error can be before it is considered
     * "on-target."
     *            
     * This is in the same units as your current and goal values.
     */
    public void setErrorTolerance(double errorTolerance) {
        setTolerances(errorTolerance, this.derivativeTolerance);
    }
    
    /**
     * Set how small the derivative of the error can be before it is considered
     * "on-target."
     *            
     * This is roughly in the same units as your current and goal values,
     * but per 1/20th of a second.
     *            
     * so if you wanted a minimum rotation speed of 5 degrees per second,
     * this tolerance would need to be 0.25.     * 
     */
    public void setErrorDerivativeTolerance(double errorDerivativeTolerance) {
        setTolerances(this.errorTolerance, errorDerivativeTolerance);
    }
    
    public void setTolerances(double errorTolerance, double derivativeTolerance) {
        this.errorTolerance = errorTolerance;
        this.derivativeTolerance = derivativeTolerance;
    }

    /**
     * Calculates the output value given P,I,D, a process variable and a goal
     * 
     * @param goal
     *            What value you are trying to achieve
     * @param current
     *            What the value under observation currently is
     * @param p
     *            Proportionate response
     * @param i
     *            Integral response.
     * @param d
     *            Derivative response.
     * @param f
     *            Feed-forward response            
     * @return The output value to achieve goal
     */
    public double calculate(double goal, double current,
            double p, double i, double d, double f)
    {        
        m_targetInputValue = goal;
        m_currentInputValue = current;
        double result;
        m_error = m_targetInputValue - m_currentInputValue;

        if (i != 0)
        {
            double potentialIGain = (m_totalError + m_error) * i;
            if (potentialIGain < m_maximumOutput)
            {
                if (potentialIGain > m_minimumOutput)
                {
                    m_totalError += m_error;
                } else
                {
                    m_totalError = m_minimumOutput / i;
                }
            } else
            {
                m_totalError = m_maximumOutput / i;
            }
        }

        m_derivativeValue = m_error - m_prevError;
        m_result = p * m_error + i * m_totalError + d * (m_derivativeValue) + f * goal;
        m_prevError = m_error;

        if (m_result > m_maximumOutput)
        {
            m_result = m_maximumOutput;
        } else if (m_result < m_minimumOutput)
        {
            m_result = m_minimumOutput;
        }
        result = m_result;
        
        errorIsSmall = Math.abs(m_targetInputValue - m_currentInputValue) < errorTolerance;
        derivativeIsSmall = Math.abs(m_derivativeValue) < derivativeTolerance;

        return result;
    }
    
    /**
     * Calculates the output value given P,I,D, a process variable and a goal
     * 
     * @param goal
     *            What value you are trying to achieve
     * @param current
     *            What the value under observation currently is
     * @param p
     *            Proportionate response
     * @param i
     *            Integral response.
     * @param d
     *            Derivative response.
     * @return The output value to achieve goal
     */
    public double calculate(double goal, double current,
            double p, double i, double d) {
        return calculate(goal, current, p, i, d, 0);
    }
    
    /**
     * Check for all conditions where the tolerance is above 0. Since tolerances default to
     * -1, it will skip any unassigned ones.
     */
    public boolean isOnTarget() {
        boolean checkError = errorTolerance > 0;
        boolean checkDerivative = derivativeTolerance > 0;
        
        boolean isOnTarget = true;
        
        if (checkError) {
            isOnTarget &= errorIsSmall;
        }
        if (checkDerivative) {
            isOnTarget &= derivativeIsSmall;
        }
        
        return isOnTarget;
    }
}
