package xbot.common.subsystems.basexcantalonsystems;

import xbot.common.command.BaseSetpointSubsystem;
import xbot.common.command.PeriodicDataSource;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.logic.Latch;
import xbot.common.logic.Latch.EdgeType;
import xbot.common.math.MathUtils;
import xbot.common.math.PIDManager;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.StringProperty;

public abstract class BaseElevatorSubsystem extends BaseSetpointSubsystem implements PeriodicDataSource {

    public enum PowerRestrictionReason {
        FullPowerAvailable, LowerLimitSwitch, UpperLimitSwitch, Uncalibrated, AboveMaxPosition, NearMaxPosition,
        BelowMinPosition, NearMinPosition,
    }

    XCANTalon master;

    final StringProperty elevatorRestrictionReasonProp;
    private final Latch calibrationLatch;
    private boolean isCalibrated;
    private double calibrationOffset;
    private double targetInDomainUnits;

    public BaseElevatorSubsystem(PropertyFactory propFactory, String prefix) {
        log.info("Creation started.");
        log.info("Creating properties using the implementation class's name...");
        propFactory.setPrefix(prefix);
        elevatorRestrictionReasonProp = propFactory.createEphemeralProperty("PowerRestrictionReason", "No Reason Yet");
        log.info("Properties complete.");
        calibrationLatch = new Latch(false, EdgeType.RisingEdge, edge -> {
            if (edge == EdgeType.RisingEdge) {
                calibrateHere();
            }
        });

        log.info("Creation complete.");
    }

    public double getTargetInDomainUnits() {
        return targetInDomainUnits;
    }

    public abstract XCANTalon getMasterTalon();

    protected abstract void setDevicePower(double power);
    protected abstract void insanelyDangerousSetDevicePower(double power);

    public abstract double getMaximumPositionValueInDomainUnits();
    public abstract double getMinimumPositionValueInDomainUnits();
    public abstract double getTicksPerDomainUnit();

    protected abstract boolean isSystemReady();

    public abstract boolean lowerLimitHit();
    public abstract boolean upperLimitHit();

    public abstract double getUncalibratedPower();

    public abstract PIDManager getPositionalPidManager();
    public abstract PIDManager getVelocityPidManager();

    public boolean getIsCalibrated() {
        return isCalibrated;
    }

    public void calibrateHere() {
        calibrateAt(getMasterTalon().getSelectedSensorPosition(0));
    }

    public int getCurrentTicks() {
        return getMasterTalon().getSelectedSensorPosition(0);
    }

    public void calibrateAt(int lowestPosition) {
        log.info("Calibrating system with lowest position of " + lowestPosition);
        calibrationOffset = lowestPosition;
        isCalibrated = true;

        getMasterTalon().configReverseSoftLimitThreshold(lowestPosition, 0);

        // calculate the upper limit and set safeties.
        double mechanismRangeInUnits = getMaximumPositionValueInDomainUnits() - getMinimumPositionValueInDomainUnits();
        int tickRange = (int) (getTicksPerDomainUnit() * mechanismRangeInUnits);
        int upperLimit = tickRange + lowestPosition;

        log.info("Upper limit set at: " + upperLimit);
        getMasterTalon().configForwardSoftLimitThreshold(upperLimit, 0);

        setSoftLimitsEnabled(true);

        setTargetInDomainUnits(getCurrentPositionInDomainUnits());
    }

    public void setTargetInDomainUnits(double targetInDomainUnits) {
        this.targetInDomainUnits = targetInDomainUnits;
    }

    public void setCurrentPositionAsTargetPosition() {
        setTargetInDomainUnits(getCurrentPositionInDomainUnits());
    }

    public double getCurrentPositionInDomainUnits() {
        if (Math.abs(getTicksPerDomainUnit()) < 0.001 ) {
            return getCurrentTicks();
        }
        return ((getCurrentTicks() - calibrationOffset) / getTicksPerDomainUnit()) 
        + getMinimumPositionValueInDomainUnits();
    }

    public double getCurrentVelocityInTicks() {
        return getMasterTalon().getSelectedSensorVelocity(0);
    }

    public double getCurrentVelocityInDomainUnits() {
        if (Math.abs(getTicksPerDomainUnit()) < 0.001 ) {
            return getCurrentVelocityInTicks();
        }
        return getCurrentVelocityInTicks() / getTicksPerDomainUnit();
    }

    public void setSoftLimitsEnabled(boolean on) {
        getMasterTalon().configReverseSoftLimitEnable(on, 0);
        getMasterTalon().configForwardSoftLimitEnable(on, 0);
    }

    public void insanelyDangerousSetPower(double power) {
        setSoftLimitsEnabled(false);
        insanelyDangerousSetDevicePower(power);
    }

    public void setPower(double power) {

        if (!isSystemReady()) {
            return;
        }

        PowerRestrictionReason reason = PowerRestrictionReason.FullPowerAvailable;
        calibrationLatch.setValue(lowerLimitHit());

        // If the lower-bound sensor is hit, then we need to prevent the mechanism from
        // lowering any further.
        if (lowerLimitHit()) {
            power = MathUtils.constrainDouble(power, 0, 1);
            reason = PowerRestrictionReason.LowerLimitSwitch;
        }
        // If the upper-bound sensor is hit, then we need to prevent the mechanism from rising any further.
        if (upperLimitHit()) {
            power = MathUtils.constrainDouble(power, -1, 0.1);
            reason = PowerRestrictionReason.UpperLimitSwitch;
        }

        // If the elevator is not calibrated, then maximum power should be constrained.
        if (!isCalibrated) {
            power = MathUtils.constrainDouble(power, -getUncalibratedPower(), getUncalibratedPower());
            reason = PowerRestrictionReason.Uncalibrated;
        }

        if (isCalibrated) {
            // if we are above the max, only go down.
            double currentPositionInDomainUnits = getCurrentPositionInDomainUnits();
            if (currentPositionInDomainUnits > getMaximumPositionValueInDomainUnits()) {
                power = MathUtils.constrainDouble(power, -1, 0.1);
                reason = PowerRestrictionReason.AboveMaxPosition;
            }
            // if we are below the min, can only go up.
            if (currentPositionInDomainUnits < getMinimumPositionValueInDomainUnits()) {
                power = MathUtils.constrainDouble(power, 0, 1);
                reason = PowerRestrictionReason.BelowMinPosition;
            }
        }
        setDevicePower(power);
        setRestrictionReason(reason.toString());
    }

    private void setRestrictionReason(String reason) {
        elevatorRestrictionReasonProp.set(reason);
    }

    @Override
    public void updatePeriodicData() {
        getMasterTalon().updateTelemetryProperties();
    }

}