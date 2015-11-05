package xbot.common.autonomous;

import edu.wpi.first.wpilibj.command.Command;

public class TestScriptedCommandFactory implements ScriptedCommandFactory {

    @Override
    public ScriptedCommandProvider getProviderForName(String commandTypeName) {
        return new TestScriptedCommandProviderA();
    }
    
    public class TestScriptedCommandProviderA implements ScriptedCommandProvider {

        @Override
        public Command get() {
            return null;
        }
    }

}
