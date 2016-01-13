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

    /**
     * Resets the PID controller.
     */
    public void reset()
    {
        m_prevError = 0;
        m_totalError = 0;
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
            double p, double i, double d)
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

        m_result = p * m_error + i * m_totalError + d * (m_error - m_prevError);
        m_prevError = m_error;

        if (m_result > m_maximumOutput)
        {
            m_result = m_maximumOutput;
        } else if (m_result < m_minimumOutput)
        {
            m_result = m_minimumOutput;
        }
        result = m_result;

        return result;
    }

    /**
     * This tells you if the controller has met its goal. Don't call this before
     * calling calculate!
     * 
     * @param absoluteTolerance
     *            How close the value can be before it is considered
     *            "on-target."
     */
    public boolean isOnTarget(double absoluteTolerance)
    {
        return Math.abs(m_targetInputValue - m_currentInputValue) < absoluteTolerance;
    }
}
