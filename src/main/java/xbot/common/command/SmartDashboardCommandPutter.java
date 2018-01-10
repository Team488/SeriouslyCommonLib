package xbot.common.command;

import edu.wpi.first.wpilibj.command.Command;

public interface SmartDashboardCommandPutter {
    public void addCommandToSmartDashboard(Command command);
    public void addCommandToSmartDashboard(String label, Command command);
}