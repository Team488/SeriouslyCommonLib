package xbot.common.command.scripted;

import org.apache.log4j.Logger;

import xbot.common.command.BaseCommand;

public class ScriptedCommandCompletionLock {
    static Logger log = Logger.getLogger(ScriptedCommandCompletionLock.class);
    
    private BaseCommand commandToWatch;
    
    public ScriptedCommandCompletionLock(BaseCommand commandToWatch) {
        this.commandToWatch = commandToWatch;
    }
    
    public boolean updateLock() {
        if(this.commandToWatch.isFinished()) {
            synchronized(this) {
                this.notifyAll();
            }
            return true;
        }
        
        return false;
    }
}
