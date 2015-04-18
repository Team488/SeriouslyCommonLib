package xbot.common.controls;

import edu.wpi.first.wpilibj.Timer;

public class XTimer
{
    private Timer internalTimer;
    private boolean isStarted = false;
    
    public XTimer()
    {
        internalTimer = new Timer();
    }
    
    public double getTime()
    {
        return internalTimer.get();
    }
    
    public boolean isStarted()
    {
        return isStarted;
    }
    
    public void start()
    {
        internalTimer.start();
        isStarted = true;
    }
    
    public double stop()
    {
        double result = internalTimer.get();
        internalTimer.stop();
        isStarted = false;
        return result;
    }
    
    public void reset()
    {
        internalTimer.stop();
        internalTimer.reset();
        isStarted = false;
    }
}
