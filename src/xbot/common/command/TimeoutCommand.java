package xbot.common.command;

import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public class TimeoutCommand extends BaseCommand{
    DoubleProperty timeout;
    
    public TimeoutCommand(XPropertyManager propManager){
        timeout = propManager.createEphemeralProperty("initialTimeout", 0);
    }

    @Override
    public void initialize() {
        this.setTimeout(timeout.get());
    }

    @Override
    public void execute() {}
    
    public boolean isFinished(){
        return this.isTimedOut();
    }
    
    /**
     * Allows you to set a timeout with a double property. (Allows easy tuning)
     * @param timeout
     *      the timeout as a double property
     */
    public void setTimeoutProperty(DoubleProperty timeout){
        this.timeout = timeout;
    }

}
