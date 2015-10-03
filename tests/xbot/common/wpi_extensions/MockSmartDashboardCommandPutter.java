package xbot.common.wpi_extensions;

import edu.wpi.first.wpilibj.command.Command;

public class MockSmartDashboardCommandPutter implements SmartDashboardCommandPutter {

    @Override
    public void addCommandToSmartDashboard(Command command) {
        // intentionally left blank as the SmartDashboard isn't available off the robot right now
        
    }

    @Override
    public void addCommandToSmartDashboard(String label, Command command) {
        // intentionally left blank as the SmartDashboard isn't available off the robot right now
        
    }

}