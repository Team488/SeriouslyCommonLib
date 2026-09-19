package xbot.common.injection.electrical_contract;

import org.wpilib.units.measure.Distance;

/**
 * This interface defines the base electrical contract
 * for robots implementing a deadwheel assisted drive based system.
 */
public interface XDeadwheelElectricalContract {
    public abstract Distance getDistanceFromCenterToOuterBumperX();
}
