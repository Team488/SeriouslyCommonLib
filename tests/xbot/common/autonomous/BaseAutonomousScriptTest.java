package xbot.common.autonomous;

import static org.junit.Assert.*;

import org.junit.Test;

public class BaseAutonomousScriptTest {

    @Test
    public void test() {
        AutonomousScriptedCommand scriptedCommand = new AutonomousScriptedCommand(new TestScriptedCommandFactory());
        scriptedCommand.executeScriptFromString("robot.requireCommands('Foo'); robot.invokeFoo();");
    }

}
