package xbot.common.command.scripted;

import edu.wpi.first.wpilibj.command.Command;

public class ExecutionCounterCommand extends Command {
    private int initCount, execCount;
    public boolean isFinished = false;

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
    protected void initialize() {
        this.initCount++;

    }

    @Override
    protected void execute() {
        this.execCount++;

    }

    @Override
    protected boolean isFinished() {
        return isFinished;
    }

    @Override
    protected void end() {

    }

    @Override
    protected void interrupted() {

    }

}
