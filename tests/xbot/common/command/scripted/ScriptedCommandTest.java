package xbot.common.command.scripted;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import xbot.common.command.XScheduler;
import xbot.common.command.scripted.ScriptedCommand;
import xbot.common.command.scripted.TempCommandFactory.ExecutionCounterCommandProvider;
import xbot.common.injection.BaseWPITest;

public class ScriptedCommandTest extends BaseScriptedCommandTest {
    static Logger log = Logger.getLogger(ScriptedCommandTest.class);

    @Test
    public void testBasicCommandExecution() {
        ScriptedCommand scriptedCommand = new ScriptedCommand(
                "robot.requireCommands('CounterCommand');\n"
                + "robot.invokeCounterCommand();\n",
                "TestScript",
                scriptedCommandFactory);
        
        scheduler.add(scriptedCommand);
        
        for(int numLoops = 0; !scriptedCommand.isFinished() && numLoops < 40; numLoops++) {
            scheduler.run();
            sleepThread(50);
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
        
        for(int numLoops = 0; !scriptedCommand.hasReachedCheckpoint("commandInvoked") && numLoops < 40; numLoops++) {
            scheduler.run();
            sleepThread(50);
        }
        
        assertTrue(scriptedCommand.hasReachedCheckpoint("commandInvoked"));
        
        // Once we know that it hit the checkpoint, give it a chance to run any registered commands
        scheduler.run();
        scheduler.run();
        
        ExecutionCounterCommand lastCommand = getLastCounterCommand();
        assertCounterExecuted(lastCommand);
        
        scriptedCommand.interrupted();
    }
    
}
