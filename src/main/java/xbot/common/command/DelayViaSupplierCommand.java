package xbot.common.command;

import java.util.function.Supplier;

import com.google.inject.Inject;

import edu.wpi.first.wpilibj2.command.WaitCommand;

public class DelayViaSupplierCommand extends WaitCommand {

    Supplier<Double> waitTime;
    
    @Inject
    public DelayViaSupplierCommand(Supplier<Double> source) {
        super(0);
    }
    
    public void setDelaySupplier(Supplier<Double> source) {
        waitTime = source;
    }

    @Override
    public boolean isFinished() {
        return m_timer.hasPeriodPassed(waitTime.get());
    }
}
