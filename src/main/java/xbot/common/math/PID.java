package xbot.common.math;

import xbot.common.controls.sensors.XTimer;

/**
 * This PID was extracted from WPILib. It has all the same functionality, but
 * does not run on its own separate thread.
 */
public class PID
{
    public enum OffTargetReason {
        Unknown,
        OffTargetNotConfigured,
        ErrorTooLarge,
        DerivativeTooLarge,
        NotTimeStable,
        OnTarget
    }

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
    double errorTolerance = 0;
    double derivativeTolerance = 0;
    
    private boolean checkErrorThreshold = false;
    private boolean checkDerivativeThreshold = false;
    private boolean checkTimeThreshold = false;
    private boolean waitingToStabilize = false;
    private double timeToleranceInSeconds = 0;
    
    private double startTime = 0;
    
    private boolean onTarget = false;
    private OffTargetReason offTargetReason = OffTargetReason.Unknown;


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
     * Set tolerances for the PID system.
     * @param errorTolerance
     *  How close the error can be before it is considered
     *  "on-target."
     *
     *  Negative values will cause that constraint to skipped when checking
     *  isOnTarget().
     *
     *  This is in the same units as your current and goal values.
     * @param derivativeTolerance
     *  Set how small the derivative of the error can be before it is considered
     *  "on-target."
     *
     *  This is roughly in the same units as your current and goal values,
     *  but per 1/20th of a second.
     *
     *  e.g. if you wanted a minimum rotation speed of 5 degrees per second,
     *  this tolerance would need to be 0.25.  
     */
    public void setTolerances(double errorTolerance, double derivativeTolerance, double timeToleranceInSeconds) {
        this.errorTolerance = errorTolerance;
        this.derivativeTolerance = derivativeTolerance;
        this.timeToleranceInSeconds = timeToleranceInSeconds;
    }
    
    /**
     * Controls whether or not the tolerances are checked as part of isOnTarget().
     */
    public void setShouldCheckTolerances(boolean checkError, boolean checkDerivative, boolean checkTime) {
        checkErrorThreshold = checkError;
        checkDerivativeThreshold = checkDerivative;
        checkTimeThreshold = checkTime;
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
            double p, double i, double d, double f, double iZone)
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
        m_result = p * m_error + d * (m_derivativeValue) + f * goal;
        if(iZone <= 0 || Math.abs(m_error) < iZone) {
            m_result +=  i * m_totalError;
        }
        m_prevError = m_error;

        if (m_result > m_maximumOutput)
        {
            m_result = m_maximumOutput;
        } else if (m_result < m_minimumOutput)
        {
            m_result = m_minimumOutput;
        }
        result = m_result;
        
        errorIsSmall = checkErrorThreshold && Math.abs(m_targetInputValue - m_currentInputValue) < errorTolerance;
        derivativeIsSmall = checkDerivativeThreshold && Math.abs(m_derivativeValue) < derivativeTolerance;
        
        checkIsOnTarget();
        
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
     * @param f
     *            Feed-forward response.
     * @return The output value to achieve goal
     */
    public double calculate(double goal, double current,
            double p, double i, double d, double f) {
        return calculate(goal, current, p, i, d, f, 0);
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
     * With the addition of a time threshold, "onTarget" needs to be updated every tick, since
     * isOnTarget() should return true if all conditions are met EVEN IF it's the first time
     * being called.
     * 
     * While that's unlikely due to our command infrastructure, it's still possible, and the PID
     * class should be able to handle that scenario.
     * 
     * As a result, this checkIsOnTarget method was added, which does all the onTarget calculations
     * during every calculate() call. the isOnTarget() method just returns a stored class-level variable.
     */
    private void checkIsOnTarget() {
        if (!checkErrorThreshold && !checkDerivativeThreshold && !checkTimeThreshold) {
            // No tolerances are enabled, but isOnTarget is being called anyway. We still need to return something.
            // In this case, we return FALSE, as it promotes robot action (the command using this will complete
            // its activity, even if it doesn't signal that it is done to allow other actions to proceed).
            onTarget = false;
            offTargetReason = OffTargetReason.OffTargetNotConfigured;
            return;
        }
        
        // Assume we are on target, and then look for reasons we may not be.
        offTargetReason = OffTargetReason.OnTarget;
        onTarget = true;
        
        if (checkDerivativeThreshold) {
            onTarget &= derivativeIsSmall;
            offTargetReason = derivativeIsSmall ? offTargetReason : OffTargetReason.DerivativeTooLarge;
        }
        if (checkErrorThreshold) {
            onTarget &= errorIsSmall;
            offTargetReason = errorIsSmall ? offTargetReason : OffTargetReason.ErrorTooLarge;
        }        
        
        if (checkTimeThreshold) {
            if(onTarget){
                if(waitingToStabilize == false){
                    waitingToStabilize = true;
                    startTime = XTimer.getFPGATimestamp();
                    onTarget = false;
                    offTargetReason = OffTargetReason.NotTimeStable;
                } else {
                    onTarget =  (XTimer.getFPGATimestamp() - startTime) > timeToleranceInSeconds;
                    offTargetReason = onTarget ? OffTargetReason.OnTarget : OffTargetReason.NotTimeStable;
                }
            } else {
                waitingToStabilize = false;
                onTarget = false;
            }
        }
    }
    
    public boolean isOnTarget() {
        return onTarget;
    }

    /**
     * Returns an enum explaining the reason the PID is not on target. 
     * It has the following priority list if there are multiple reasons (higher ones overrule lower ones):
     * - Too much error
     * - Too much derivative
     * - Waiting for time stability
     * - On Target
     * @return
     */
    public OffTargetReason getOffTargetReason() {
        return offTargetReason;
    }
}