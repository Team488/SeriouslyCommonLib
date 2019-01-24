package xbot.common.command;

import com.google.inject.Inject;

import xbot.common.properties.DoubleProperty;

public class TimeoutCommand extends BaseCommand {
    private DoubleProperty timeoutProperty = null;
    
    @Inject
    public TimeoutCommand() {
        
    }
    
    public void setConfigurableTimeout(double timeout) {
        this.setTimeout(timeout);
    }
    
    public void setConfigurableTimeout(DoubleProperty timeoutProp) {
        this.timeoutProperty = timeoutProp;
    }

    @Override
    public void initialize() {
        if (timeoutProperty != null) {
            this.setTimeout(timeoutProperty.get());
        }
    }

    @Override
    public void execute() {}
    
    public boolean isFinished(){
        return this.isTimedOut();
    }

}
