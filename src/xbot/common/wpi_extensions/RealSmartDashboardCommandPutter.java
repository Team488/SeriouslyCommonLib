package xbot.common.wpi_extensions;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RealSmartDashboardCommandPutter implements SmartDashboardCommandPutter {

    @Override
    public void addCommandToSmartDashboard(Command command) {
       SmartDashboard.putData(command);
    }

    @Override
    public void addCommandToSmartDashboard(String label, Command command) {
        SmartDashboard.putData(label, command);
    }

}