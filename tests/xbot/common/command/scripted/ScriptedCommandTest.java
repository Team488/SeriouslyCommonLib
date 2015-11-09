package xbot.common.command.scripted;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import xbot.common.command.XScheduler;
import xbot.common.command.scripted.ScriptedCommand;
import xbot.common.command.scripted.TestCommandFactory.ExecutionCounterCommandProvider;
import xbot.common.injection.BaseWPITest;

public class ScriptedCommandTest extends BaseScriptedCommandTest {
    static Logger log = Logger.getLogger(ScriptedCommandTest.class);
    
    final int loopWaitIncrement = 10;

    @Test(timeout=10000)
    public void testBasicCommandExecution() {
        ScriptedCommand scriptedCommand = new ScriptedCommand(
                "robot.requireCommands('CounterCommand');\n"
                + "robot.invokeCounterCommand();\n",
                "TestScript",
                scriptedCommandFactory);
        
        scheduler.add(scriptedCommand);
        
        while(!scriptedCommand.isFinished()) {
            scheduler.run();
            sleepThread(loopWaitIncrement);
        }
        
        scheduler.run();
        scheduler.run();
        
        ExecutionCounterCommand lastCommand = assertLastCounterCommand();
        assertCounterExecuted(lastCommand);
        
        scriptedCommand.interrupted();
    }

    @Test(timeout=10000)
    public void testCommandCheckpoints() {
        ScriptedCommand scriptedCommand = new ScriptedCommand(
                "robot.requireCommands('CounterCommand');\n"
                + "robot.invokeCounterCommand();\n"
                + "robot.checkpointReached('commandInvoked');\n"
                + "while(true);", // Prevent the command from exiting -- should be forcefully killed
                "TestScript",
                scriptedCommandFactory);
        
        scheduler.add(scriptedCommand);
        
        while(!scriptedCommand.hasReachedCheckpoint("commandInvoked")) {
            scheduler.run();
            sleepThread(loopWaitIncrement);
        }
        
        assertTrue(scriptedCommand.hasReachedCheckpoint("commandInvoked"));
        
        // Once we know that it hit the checkpoint, give it a chance to run any registered commands
        scheduler.run();
        scheduler.run();
        
        ExecutionCounterCommand lastCommand = assertLastCounterCommand();
        assertCounterExecuted(lastCommand);
        
        scriptedCommand.interrupted();
    }
    
    @Test(timeout=10000)
    public void testCommandWaiting() {
        ScriptedCommand scriptedCommand = new ScriptedCommand(
                "robot.requireCommands('CounterCommand');\n"
                + "var invokedCommand = robot.invokeCounterCommand(10);\n"
                + "invokedCommand.waitForCompletion();",
                "TestScript",
                scriptedCommandFactory);
        
        scheduler.add(scriptedCommand);
        
        while(!scriptedCommand.isFinished()) {
            scheduler.run();
            sleepThread(loopWaitIncrement);
        }
        
        assertTrue(scriptedCommand.isFinished());
        ExecutionCounterCommand lastCommand = assertLastCounterCommand();
        assertCounterExecuted(lastCommand);
        
        scriptedCommand.interrupted();
    }
}
