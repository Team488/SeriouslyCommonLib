package xbot.common.controls.sensors.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.LaserCANInputs;
import xbot.common.controls.sensors.XLaserCAN;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.logging.AlertGroups;

import static org.wpilib.units.Units.Meters;
import static org.wpilib.units.Units.Seconds;

import org.wpilib.driverstation.Alert;

// TODO(2027 migration): GrappleRobotics has not published a 2027-alpha-compatible libgrapplefrc
// vendordep yet (checked https://github.com/GrappleRobotics/LaserCAN/releases, latest is
// v2025.1.0). Stubbed out until one is available; reports as permanently disconnected/invalid.
public class LaserCANWpiAdapter extends XLaserCAN {

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
        healthAlert = new Alert(AlertGroups.DEVICE_HEALTH,
                "LaserCAN is unsupported on WPILib 2027 (no vendor build yet)", Alert.Level.HIGH);
        healthAlert.set(true);
    }

    @Override
    public void updateInputs(LaserCANInputs inputs) {
        inputs.distance = Meters.of(0);
        inputs.measurementLatency = Seconds.zero();
        inputs.isMeasurementValid = false;
    }
}
