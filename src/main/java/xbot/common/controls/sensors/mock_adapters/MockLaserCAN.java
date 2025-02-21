package xbot.common.controls.sensors.mock_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.controls.io_inputs.LaserCANInputs;
import xbot.common.controls.sensors.XLaserCAN;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;

public class MockLaserCAN extends XLaserCAN {

    private double distanceMeters = Double.MAX_VALUE;

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
            DevicePolice police) {
        super(police, info, owningSystemPrefix);
    }

    public void setDistance(double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    @Override
    public void updateInputs(LaserCANInputs inputs) {
        inputs.distance = edu.wpi.first.units.Units.Meters.of(distanceMeters);
    }
}