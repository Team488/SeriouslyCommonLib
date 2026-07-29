package xbot.common.command;

import javax.inject.Inject;

//import org.wpilib.command2.Command;
import org.wpilib.smartdashboard.SmartDashboard;

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