package xbot.common.command;

//import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RealSmartDashboardCommandPutter implements SmartDashboardCommandPutter {

    @Override
    public void addCommandToSmartDashboard(BaseCommand command) {
       SmartDashboard.putData(command);
    }

    @Override
    public void addCommandToSmartDashboard(String label, BaseCommand command) {
        SmartDashboard.putData(label, command);
    }

}