package xbot.common.controls.sensors.wpi_adapters;

import au.grapplerobotics.LaserCan;
import au.grapplerobotics.interfaces.LaserCanInterface;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.controls.io_inputs.LaserCANInputs;
import xbot.common.controls.sensors.XLaserCAN;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;

import static edu.wpi.first.units.Units.Meters;

public class LaserCANWpiAdapter extends XLaserCAN {

    protected LaserCan laserCan;

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
            DevicePolice police) {
        super(police, info, owningSystemPrefix);
        laserCan = new LaserCan(info.channel);
    }

    @Override
    public void updateInputs(LaserCANInputs inputs) {
        LaserCanInterface.Measurement measurement = laserCan.getMeasurement();
        if (measurement != null) {
            inputs.distance = Meters.of(measurement.distance_mm / 1000.0);
        } else {
            inputs.distance = Meters.of(0);
        }
    }
}
