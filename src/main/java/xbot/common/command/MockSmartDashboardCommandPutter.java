package xbot.common.command;

import javax.inject.Inject;

public class MockSmartDashboardCommandPutter implements SmartDashboardCommandPutter {

    @Inject
    public MockSmartDashboardCommandPutter() {}

    @Override
    public void addCommandToSmartDashboard(BaseCommand command) {
        // intentionally left blank as the SmartDashboard isn't available off the robot right now
        
    }

    @Override
    public void addCommandToSmartDashboard(String label, BaseCommand command) {
        // intentionally left blank as the SmartDashboard isn't available off the robot right now
        
    }

}