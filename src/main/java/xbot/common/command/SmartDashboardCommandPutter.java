package xbot.common.command;

public interface SmartDashboardCommandPutter {
    public void addCommandToSmartDashboard(BaseCommand command);
    public void addCommandToSmartDashboard(String label, BaseCommand command);
}