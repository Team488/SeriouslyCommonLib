package xbot.common.command;

import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

public class BaseCommandTest extends BaseCommonLibTest {

    @Test
    public void testPuttingOnSmartDashboardDoesntCrashTest() {
        BaseCommand command = new MockCommand();
        command.includeOnSmartDashboard("label");
        command.includeOnSmartDashboard();
    }

}