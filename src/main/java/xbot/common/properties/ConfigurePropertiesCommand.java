package xbot.common.properties;

import com.google.inject.Inject;

import xbot.common.command.BaseCommand;

public class ConfigurePropertiesCommand extends BaseCommand {

    ITableProxy table;
    boolean fastMode;
    
    @Inject
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
