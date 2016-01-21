package xbot.common.command.scripted;

import org.apache.log4j.Logger;

import xbot.common.command.BaseCommand;

public class ExecutionCounterCommand extends BaseCommand {
    static Logger log = Logger.getLogger(ExecutionCounterCommand.class);
    
    private int initCount, execCount;
    private Integer initLimit = null, execLimit = null;

    public void setInitLimit(int initLimit) {
        this.initLimit = new Integer(initLimit);
    }

    public void setExecLimit(int execLimit) {
        this.execLimit = new Integer(execLimit);
    }
    
    public int getInitCount() {
        return initCount;
    }
    
    public int getExecCount() {
        return this.execCount;
    }
    
    public boolean hasBeenInitialized() {
        return initCount > 0;
    }
    
    public boolean hasBeenExecuted() {
        return execCount > 0;
    }
    
    @Override
    public void initialize() {
        log.debug("Initialize");
        
        this.execCount = 0;
        if(initLimit == null || initCount < initLimit)
            this.initCount++;

    }

    @Override
    public void execute() {
        log.debug("Execute");
        
        if(execLimit == null || execCount < execLimit)
            this.execCount++;

    }

    @Override
    public boolean isFinished() {
        // TODO: Figure out if we need the separate null check
        // ... >= might do it for us and return the expected result
        return (initLimit != null && initCount >= initLimit)
                || (execLimit != null && execCount >= execLimit);
    }

    @Override
    public void end() {
    }

    @Override
    public void interrupted() {

    }

}
