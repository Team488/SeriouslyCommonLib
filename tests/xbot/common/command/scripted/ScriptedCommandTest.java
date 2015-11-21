package xbot.common.command.scripted;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Test;

import xbot.common.command.scripted.ScriptedCommand;

public class ScriptedCommandTest extends BaseScriptedCommandTest {
    static Logger log = Logger.getLogger(ScriptedCommandTest.class);
    
    final int loopWaitIncrement = 10;
    
    @Test(timeout=10000)
    public void testCommandCheckpoints() {
        ScriptedCommand scriptedCommand = commonCommandFactory.createScriptedCommand(
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
        ScriptedCommand scriptedCommand = commonCommandFactory.createScriptedCommand(
                "robot.requireCommands('CounterCommand');\n"
                + "var invokedCommand = robot.invokeCounterCommand(50);\n"
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
    
    @Test(timeout=10000)
    public void testCommandFromFile() {
        ScriptedCommand scriptedCommand = commonCommandFactory.createScriptedCommand(
                new File("scripts/WaitForCounter.js"),
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
    
    @Test(timeout=10000)
    public void testCommandFromNonexistentFile() {
        ScriptedCommand scriptedCommand = commonCommandFactory.createScriptedCommand(
                new File("this/does/not/exist.abc"),
                scriptedCommandFactory);
        
        scheduler.add(scriptedCommand);

        scheduler.run();
        scheduler.run();
        
        scriptedCommand.interrupted();
        
        // Should complete w/o exceptions
    }
    
    @Test(timeout=10000)
    public void testRequireNonexistentCommand() {
        ScriptedCommand scriptedCommand = commonCommandFactory.createScriptedCommand(
                "robot.requireCommands('NonexistentCommand');"
                + "robot.invokeNonexistentCommand()",
                "TestScript",
                scriptedCommandFactory);
        
        scheduler.add(scriptedCommand);

        scheduler.run();
        scheduler.run();
        
        scriptedCommand.interrupted();
        
        // Should complete w/o exceptions
    }
}
