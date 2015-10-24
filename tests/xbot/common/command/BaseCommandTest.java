package xbot.common.command;

import static org.junit.Assert.*;

import org.junit.Test;

import xbot.common.command.BaseCommand;
import xbot.common.injection.BaseWPITest;

public class BaseCommandTest extends BaseWPITest {

    @Test
    public void testPuttingOnSmartDashboardDoesntCrashTest() {
        BaseCommand command = injector.getInstance(MockCommand.class);
        command.includeOnSmartDashboard("label");
        command.includeOnSmartDashboard();
    }

}