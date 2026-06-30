package xbot.common.controls.sensors.wpi_adapters;

import au.grapplerobotics.LaserCan;
import au.grapplerobotics.interfaces.LaserCanInterface;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.wpilibj.Alert;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.LaserCANInputs;
import xbot.common.controls.sensors.XLaserCAN;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.logging.AlertGroups;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Seconds;

public class LaserCANWpiAdapter extends XLaserCAN {

    protected LaserCan laserCan;
    private Distance previousMeasurement = Meters.zero();
    private Time previousMeasurementTime = Seconds.of(Double.MIN_VALUE);
    final Alert healthAlert;

    @AssistedFactory
    public abstract static class LaserCANWpiAdapterFactory implements XLaserCANFactory
    {
        public abstract LaserCANWpiAdapter create(
                @Assisted("info") DeviceInfo info,
                @Assisted("owningSystemPrefix")String owningSystemPrefix);
    }

    @AssistedInject
    public LaserCANWpiAdapter(
            @Assisted("info") DeviceInfo info,
            @Assisted("owningSystemPrefix")String owningSystemPrefix,
            DevicePolice police, DataFrameRegistry dataFrameRegistry) {
        super(police, info, owningSystemPrefix, dataFrameRegistry);
        healthAlert = new Alert(AlertGroups.DEVICE_HEALTH, "Failed to set LaserCAN configuration", Alert.AlertType.kError);
        laserCan = new LaserCan(info.channel);
        try {
            laserCan.setRangingMode(LaserCanInterface.RangingMode.SHORT);
            laserCan.setTimingBudget(LaserCanInterface.TimingBudget.TIMING_BUDGET_20MS);
        } catch (Exception e) {
            healthAlert.set(true);
        }
    }

    @Override
    public void updateInputs(LaserCANInputs inputs) {
        LaserCanInterface.Measurement measurement = laserCan.getMeasurement();
        if (measurement != null && measurement.status == LaserCan.LASERCAN_STATUS_VALID_MEASUREMENT) {
            inputs.distance = Meters.of(measurement.distance_mm / 1000.0);
            if (!inputs.distance.isEquivalent(previousMeasurement)) {
                previousMeasurementTime = XTimer.getFPGATimestampTime();
                previousMeasurement = inputs.distance;
            }
            inputs.measurementLatency = XTimer.getFPGATimestampTime().minus(previousMeasurementTime);
            inputs.isMeasurementValid = true;
        } else {
            inputs.distance = Meters.of(0);
            inputs.measurementLatency = Seconds.zero();
            inputs.isMeasurementValid = false;
        }
    }
}
