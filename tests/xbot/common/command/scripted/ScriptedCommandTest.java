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
    
    final int loopWaitIncrement = 60;

    @Test
    public void testBasicCommandExecution() {
        ScriptedCommand scriptedCommand = new ScriptedCommand(
                "robot.requireCommands('CounterCommand');\n"
                + "robot.invokeCounterCommand();\n",
                "TestScript",
                scriptedCommandFactory);
        
        scheduler.add(scriptedCommand);
        
        for(int numLoops = 0; !scriptedCommand.isFinished(); numLoops++) {
            scheduler.run();
            sleepThread(loopWaitIncrement);
        }
        
        scheduler.run();
        scheduler.run();
        
        ExecutionCounterCommand lastCommand = assertLastCounterCommand();
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
        ExecutionCounterCommand lastCommand = null;
        
        // Loop until it either reaches the checkpoint (good) or the command has been
        // executed more than 10 times (probably won't ever hit checkpoint)
        while(!scriptedCommand.hasReachedCheckpoint("commandInvoked")
                && (lastCommand == null || lastCommand.getExecCount() <= 10)) {
            scheduler.run();
            sleepThread(loopWaitIncrement);
            
            lastCommand = getLastCounterCommand();
        }
        
        assertTrue(scriptedCommand.hasReachedCheckpoint("commandInvoked"));
        
        // Once we know that it hit the checkpoint, give it a chance to run any registered commands
        scheduler.run();
        scheduler.run();
        
        lastCommand = assertLastCounterCommand();
        assertCounterExecuted(lastCommand);
        
        scriptedCommand.interrupted();
    }
    
    @Test
    public void testCommandWaiting() {
        // TODO: Re-write this test to be 100% sure that the wait method waited successfully
        ScriptedCommand scriptedCommand = new ScriptedCommand(
                "robot.requireCommands('CounterCommand');\n"
                + "var invokedCommand = robot.invokeCounterCommand(1);\n"
                + "invokedCommand.waitForCompletion();",
                "TestScript",
                scriptedCommandFactory);
        
        scheduler.add(scriptedCommand);
        
        ExecutionCounterCommand lastCommand;
        
        // Loop until either the script finishes executing or the counter command has
        // been finished for more than 10 iterations
        int loopsSinceCounterFinished = 0;
        while(!scriptedCommand.isFinished() && loopsSinceCounterFinished <= 10) {
            scheduler.run();
            sleepThread(loopWaitIncrement);
            
            lastCommand = getLastCounterCommand();
            if(lastCommand != null && lastCommand.isFinished())
                loopsSinceCounterFinished++;
        }
        
        assertTrue(scriptedCommand.isFinished());
        lastCommand = assertLastCounterCommand();
        assertCounterExecuted(lastCommand);
        
        scriptedCommand.interrupted();
    }
}
