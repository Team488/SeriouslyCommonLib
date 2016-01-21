package xbot.common.command;

import org.apache.log4j.Logger;

public class CancellableCommandWrapper extends BaseCommand {
    static Logger log = Logger.getLogger(CancellableCommandWrapper.class);
    
    private BaseCommand wrappedCommand;
    private boolean shouldCancel = false;
    
    public CancellableCommandWrapper(BaseCommand wrappedCommand) {
        this.wrappedCommand = wrappedCommand;
    }
    
    private void manualCancel() {
        log.info("Manually cancelling command " + wrappedCommand);
        super.cancel();
    }
    
    @Override
    public void cancel() {
        super.cancel();
        this.shouldCancel = true;
    }
    
    @Override
    public void initialize() {
        if(shouldCancel) {
            manualCancel();
        }
        else {
            wrappedCommand.initialize();
        }
    }

    @Override
    public void execute() {
        if(shouldCancel) {
            manualCancel();
        }
        else {
            wrappedCommand.execute();
        }
    }
    
    @Override
    public boolean isFinished() {
        if(shouldCancel) {
            return true;
        }
        else {
            return wrappedCommand.isFinished();
        }
    }
    
    @Override
    public void end() {
        if(!shouldCancel) {
            wrappedCommand.end();
        }
        
        shouldCancel = false;
    }
    
    @Override
    public void interrupted() {
        if(!shouldCancel) {
            wrappedCommand.interrupted();
        }
        
        shouldCancel = false;
    }
}
