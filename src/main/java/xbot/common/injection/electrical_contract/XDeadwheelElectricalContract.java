package xbot.common.injection.electrical_contract;

import edu.wpi.first.units.measure.Distance;

/**
 * This interface defines the base electrical contract
 * for robots implementing a differential drive based system.
 */
public interface XDeadwheelElectricalContract {
    public abstract Distance getDistanceFromCenterToOuterBumperX();
}
