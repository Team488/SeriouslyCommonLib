package xbot.common.command;

import javax.inject.Inject;

//import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RealSmartDashboardCommandPutter implements SmartDashboardCommandPutter {

    @Inject
    public RealSmartDashboardCommandPutter() {}

    @Override
    public void addCommandToSmartDashboard(BaseCommand command) {
       SmartDashboard.putData(command);
    }

    @Override
    public void addCommandToSmartDashboard(String label, BaseCommand command) {
        SmartDashboard.putData(label, command);
    }

}