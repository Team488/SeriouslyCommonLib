package xbot.common.autonomous;

import edu.wpi.first.wpilibj.command.Command;

public class TempCommandFactory implements ScriptedCommandFactory {
    private ExecutionCounterCommandProvider lastExecutionCounterCommandProvider;
    
    @Override
    public ScriptedCommandProvider getProviderForName(String commandTypeName) {
        if(commandTypeName.equals("CounterCommand"))
            return this.lastExecutionCounterCommandProvider = new ExecutionCounterCommandProvider();
        
        return null;
    }
    
    public ExecutionCounterCommandProvider getLastExecutionCounterCommandProvider() {
        return this.lastExecutionCounterCommandProvider;
    }
    
    public class ExecutionCounterCommandProvider implements ScriptedCommandProvider {
        private ExecutionCounterCommand lastCommand;
        
        @Override
        public Command get() {
            return lastCommand = new ExecutionCounterCommand();
        }
        
        public ExecutionCounterCommand getLastCommand() {
            // TODO: Replace this 'last' thing with a name param sent in by the script
            return lastCommand;
        }
    }

}
