package xbot.common.command;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;

public class BaseCommandTest extends BaseWPITest {

    @Test
    public void testPuttingOnSmartDashboardDoesntCrashTest() {
        BaseCommand command = new MockCommand();
        command.includeOnSmartDashboard("label");
        command.includeOnSmartDashboard();
    }

}