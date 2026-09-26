package xbot.common.logic;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import org.wpilib.tunable.TunableDouble;

import xbot.common.math.MathUtils;
import xbot.common.math.PIDManager;
import xbot.common.properties.TunableFactory;

public class VelocityThrottleModule {

    final PIDManager velocityPid;
    final TunableDouble throttleUpperLimitTunable;
    final TunableDouble throttleLowerLimitTunable;
    private double throttle;
    
    @AssistedFactory
    public abstract static class VelocityThrottleModuleFactory {
        public abstract VelocityThrottleModule create(
            @Assisted("name") String name,
            @Assisted("velocityPid") PIDManager velocityPid);
    }

    @AssistedInject
    public VelocityThrottleModule(
            @Assisted("name") String name,
            @Assisted("velocityPid") PIDManager velocityPid,
            TunableFactory tunableFactory) {
        this.velocityPid = velocityPid;
        tunableFactory.setPrefix(name + "/ThrottleModule");
        throttleUpperLimitTunable = tunableFactory.createDouble("ThrottleUpperLimit", 1);
        throttleLowerLimitTunable = tunableFactory.createDouble("ThrottleLowerLimit", -1);
    }
    
    public void setThrottleLimits(double lowerLimit, double upperLimit) {
        throttleUpperLimitTunable.set(upperLimit);
        throttleLowerLimitTunable.set(lowerLimit);
    }
    
    public void reset() {
        throttle = 0;
        velocityPid.reset();
    }
    
    public double calculateThrottle(double goalSpeed, double currentSpeed) {
        double throttleDelta = velocityPid.calculate(goalSpeed, currentSpeed);
        throttle += throttleDelta;
        throttle = MathUtils.constrainDouble(
                throttle,
                throttleLowerLimitTunable.get(),
                throttleUpperLimitTunable.get());
        return throttle;
    }
}
