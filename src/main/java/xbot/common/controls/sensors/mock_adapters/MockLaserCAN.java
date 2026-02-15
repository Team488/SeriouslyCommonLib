package xbot.common.controls.sensors.mock_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.LaserCANInputs;
import xbot.common.controls.sensors.XLaserCAN;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;

import static edu.wpi.first.units.Units.Seconds;

public class MockLaserCAN extends XLaserCAN {

    private double distanceMeters = Double.MAX_VALUE;
    private double measurementTime = Double.MIN_VALUE;

    @AssistedFactory
    public abstract static class MockLaserCANFactory implements XLaserCANFactory {
        public abstract MockLaserCAN create(
                @Assisted("info") DeviceInfo info,
                @Assisted("owningSystemPrefix") String owningSystemPrefix);
    }

    @AssistedInject
    public MockLaserCAN(
            @Assisted("info") DeviceInfo info,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            DevicePolice police, DataFrameRegistry dataFrameRegistry) {
        super(police, info, owningSystemPrefix, dataFrameRegistry);
    }

    public void setDistance(double distanceMeters) {
        this.distanceMeters = distanceMeters;
        this.measurementTime = XTimer.getFPGATimestamp();
    }

    @Override
    public void updateInputs(LaserCANInputs inputs) {
        inputs.isMeasurementValid = true;
        inputs.distance = edu.wpi.first.units.Units.Meters.of(distanceMeters);
        inputs.measurementLatency = Seconds.of(XTimer.getFPGATimestamp() - measurementTime);
    }
}