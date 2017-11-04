package xbot.common.command;

import xbot.common.properties.DoubleProperty;

public class TimeoutCommand extends BaseCommand{
    DoubleProperty timeout;
    
    public TimeoutCommand(DoubleProperty timeout){
        this.timeout = timeout;
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

}
