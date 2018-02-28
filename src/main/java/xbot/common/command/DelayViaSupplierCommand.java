package xbot.common.command;

import java.util.function.Supplier;

import com.google.inject.Inject;

public class DelayViaSupplierCommand extends BaseCommand {

    Supplier<Double> waitTime;
    
    @Inject
    public DelayViaSupplierCommand() {
    }
    
    public void setDelaySupplier(Supplier<Double> source) {
        waitTime = source;
    }

    @Override
    public void initialize() {
        if (waitTime != null) {
            log.info("Setting wait time of " + waitTime.get());
            this.setTimeout(waitTime.get());
        }
    }

    @Override
    public void execute() {
    }
    
    @Override
    public boolean isFinished() {
        return this.isTimedOut();
    }
    
}
