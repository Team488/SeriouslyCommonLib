package xbot.common.controls.sensors.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import xbot.common.controls.sensors.XLidarLite;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.DevicePolice;
import xbot.common.properties.PropertyFactory;

public class LidarLiteWpiAdapter extends XLidarLite{

    private I2C i2c;
    
    @AssistedFactory
    public abstract static class LidarLiteWpiAdapterFactory implements XLidarLiteFactory {
        public abstract LidarLiteWpiAdapter create(@Assisted("port") Port port, @Assisted("prefix") String prefix);
    }

    @AssistedInject
    public LidarLiteWpiAdapter(@Assisted("port") Port port, PropertyFactory propMan, DevicePolice police, @Assisted("prefix") String prefix) {
        super(port, propMan, police, prefix);

      i2c = new I2C(port, lidar_address);
    }

    @Override
    // Update distance variable
    public void update() {
        i2c.write(lidar_config_register, 0x04); // Initiate measurement
        XTimer.delay(0.04); // Delay for measurement to be taken
        i2c.read(lidar_distance_register, 2, distance); // Read in measurement
        XTimer.delay(0.01); // Delay to prevent over polling
    }

}
