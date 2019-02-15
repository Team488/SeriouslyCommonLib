package xbot.common.logic;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.math.MathUtils;
import xbot.common.math.PIDManager;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public class VelocityThrottleModule {

    final PIDManager velocityPid;
    final DoubleProperty throttleUpperLimitProp;
    final DoubleProperty throttleLowerLimitProp;
    private double throttle;
    
    @Inject
    public VelocityThrottleModule(@Assisted("name") String name, @Assisted("velocityPid") PIDManager velocityPid, PropertyFactory propMan) {
        this.velocityPid = velocityPid;
        propMan.setPrefix(name);
        throttleUpperLimitProp = propMan.createPersistentProperty("ThrottleModule/ThrottleUpperLimit", 1);
        throttleLowerLimitProp = propMan.createPersistentProperty("ThrottleModule/ThrottleLowerLimit", -1);
    }
    
    public void setThrottleLimits(double lowerLimit, double upperLimit) {
        throttleUpperLimitProp.set(upperLimit);
        throttleLowerLimitProp.set(lowerLimit);
    }
    
    public void reset() {
        throttle = 0;
        velocityPid.reset();
    }
    
    public double calculateThrottle(double goalSpeed, double currentSpeed) {
        double throttleDelta = velocityPid.calculate(goalSpeed, currentSpeed);
        throttle += throttleDelta;
        throttle = MathUtils.constrainDouble(throttle, throttleLowerLimitProp.get(), throttleUpperLimitProp.get());
        return throttle;
    }
}
