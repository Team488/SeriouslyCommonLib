package xbot.common.command;

public class MockSmartDashboardCommandPutter implements SmartDashboardCommandPutter {

    @Override
    public void addCommandToSmartDashboard(BaseCommand command) {
        // intentionally left blank as the SmartDashboard isn't available off the robot right now
        
    }

    @Override
    public void addCommandToSmartDashboard(String label, BaseCommand command) {
        // intentionally left blank as the SmartDashboard isn't available off the robot right now
        
    }

}