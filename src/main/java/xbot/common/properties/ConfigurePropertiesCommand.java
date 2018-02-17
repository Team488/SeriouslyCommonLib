package xbot.common.properties;

import xbot.common.command.BaseCommand;

public class ConfigurePropertiesCommand extends BaseCommand {

    ITableProxy table;
    boolean fastMode;
    
    public ConfigurePropertiesCommand(ITableProxy table) {
        this.table = table;
    }
    
    public void setFastMode(boolean on) {
        fastMode = on;
    }
    
    @Override
    public void initialize() {
        log.info("Initializing");
        table.setFastMode(fastMode);
    }

    @Override
    public void execute() {
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
