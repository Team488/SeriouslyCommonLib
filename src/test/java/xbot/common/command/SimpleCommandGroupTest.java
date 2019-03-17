package xbot.common.command;

import java.util.Arrays;

import org.junit.Test;

import xbot.common.command.SimpleCommandGroup.ExecutionType;
import xbot.common.injection.BaseWPITest;

public class SimpleCommandGroupTest extends BaseWPITest {

    @Test
    public void testBasicSerial() {
        BaseCommand command1 = injector.getInstance(MockCommand.class);
        BaseCommand command2 = injector.getInstance(MockCommand.class);

        // Just validate doesn't crash
        SimpleCommandGroup group = new SimpleCommandGroup(
            "name", 
            Arrays.asList(command1, command2), 
            ExecutionType.Serial
        );
        group.initialize();

        group.execute();

    }

    @Test
    public void testBasicParallel() {
        BaseCommand command1 = injector.getInstance(MockCommand.class);
        BaseCommand command2 = injector.getInstance(MockCommand.class);

        // Just validate doesn't crash
        SimpleCommandGroup group = new SimpleCommandGroup(
            "name", 
            Arrays.asList(command1, command2), 
            ExecutionType.Parallel
        );
        group.initialize();

        group.execute();

    }

}