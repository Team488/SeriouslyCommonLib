package xbot.common.wpi_extensions;

import static org.junit.Assert.*;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;

public class BaseCommandTest extends BaseWPITest {

    @Test
    public void testPuttingOnSmartDashboardDoesntCrashTest() {
        BaseCommand command = injector.getInstance(TestBaseCommand.class);
        command.includeOnSmartDashboard("label");
        command.includeOnSmartDashboard();
    }

}

class TestBaseCommand extends BaseCommand {
    @Override
    public void initialize() {
        
        
    }

    @Override
    public void execute() {
       
    }
}