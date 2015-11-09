package xbot.common.command.scripted;

import xbot.common.command.BaseCommand;

public class ScriptedCommandCompletionLock {
    private BaseCommand commandToWatch;
    
    public ScriptedCommandCompletionLock(BaseCommand commandToWatch) {
        this.commandToWatch = commandToWatch;
    }
    
    public boolean updateLock() {
        if(!this.commandToWatch.isFinished()) {
            synchronized(this) {
                this.notifyAll();
            }
            return true;
        }
        
        return false;
    }
}
