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
    
    final int loopTimeoutIterations = 60;
    final int loopWaitIncrement = 50;

    @Test
    public void testBasicCommandExecution() {
        ScriptedCommand scriptedCommand = new ScriptedCommand(
                "robot.requireCommands('CounterCommand');\n"
                + "robot.invokeCounterCommand();\n",
                "TestScript",
                scriptedCommandFactory);
        
        scheduler.add(scriptedCommand);
        
        for(int numLoops = 0; !scriptedCommand.isFinished() && numLoops < loopTimeoutIterations; numLoops++) {
            scheduler.run();
            sleepThread(loopWaitIncrement);
        }
        
        scheduler.run();
        scheduler.run();
        
        ExecutionCounterCommand lastCommand = getLastCounterCommand();
        assertCounterExecuted(lastCommand);
        
        scriptedCommand.interrupted();
    }

    @Test
    public void testCommandCheckpoints() {
        ScriptedCommand scriptedCommand = new ScriptedCommand(
                "robot.requireCommands('CounterCommand');\n"
                + "robot.invokeCounterCommand();\n"
                + "robot.checkpointReached('commandInvoked');\n"
                + "while(true);", // Prevent the command from exiting -- should be forcefully killed
                "TestScript",
                scriptedCommandFactory);
        
        scheduler.add(scriptedCommand);
        
        for(int numLoops = 0; !scriptedCommand.hasReachedCheckpoint("commandInvoked") && numLoops < loopTimeoutIterations; numLoops++) {
            scheduler.run();
            sleepThread(loopWaitIncrement);
        }
        
        assertTrue(scriptedCommand.hasReachedCheckpoint("commandInvoked"));
        
        // Once we know that it hit the checkpoint, give it a chance to run any registered commands
        scheduler.run();
        scheduler.run();
        
        ExecutionCounterCommand lastCommand = getLastCounterCommand();
        assertCounterExecuted(lastCommand);
        
        scriptedCommand.interrupted();
    }
    
    @Test
    public void testCommandWaiting() {
        ScriptedCommand scriptedCommand = new ScriptedCommand(
                "robot.requireCommands('CounterCommand');\n"
                + "var invokedCommand = robot.invokeCounterCommand(1);\n"
                + "invokedCommand.waitForCompletion();",
                "TestScript",
                scriptedCommandFactory);
        
        scheduler.add(scriptedCommand);
        
        int numLoops = 0;
        for(; !scriptedCommand.isFinished() && numLoops < loopTimeoutIterations; numLoops++) {
            scheduler.run();
            sleepThread(loopWaitIncrement);
        }
        
        // If it took the whole timeout period, we can assume that the wait wouldn't ever finish
        assertTrue(numLoops < loopTimeoutIterations);
        
        ExecutionCounterCommand lastCommand = getLastCounterCommand();
        assertCounterExecuted(lastCommand);
        
        scriptedCommand.interrupted();
    }
}
