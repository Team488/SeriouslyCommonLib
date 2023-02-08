package xbot.common.controls.sensors;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import edu.wpi.first.wpilibj.I2C.Port;

public abstract class XLidarLite implements DistanceSensor {

    private Logger log = Logger.getLogger(XLidarLite.class);
    
    protected byte[] distance;
    private java.util.Timer updater;
    private LidarUpdater task;

    protected final int lidar_address = 0x62;
    protected final int lidar_config_register = 0x00;
    protected final int lidar_distance_register = 0x8f;

    private DoubleProperty lidarPollDuration;

    public interface XLidarLiteFactory {
        public abstract XLidarLite create(Port port, String prefix);
    }

    public XLidarLite(Port port, PropertyFactory propMan, DevicePolice police, String prefix) {

        log.info("Creating Lidar on port: " + port.toString());
        propMan.setPrefix(prefix);
        lidarPollDuration = propMan.createPersistentProperty("LidarPollDurationMs", 100d);

        distance = new byte[2];

        task = new LidarUpdater();
        updater = new java.util.Timer();

        this.start();
        police.registerDevice(DeviceType.I2C, port.value, this);
    }
    
    protected abstract void update();

    // Distance in cm
    public double getDistance() {
        return (int) Integer.toUnsignedLong(distance[0] << 8) + Byte.toUnsignedInt(distance[1]);
    }

    public double pidGet() {
        return getDistance();
    }

    // Start 10Hz polling
    public void start() {
        log.info("Starting Lidar polling");
        updater.schedule(task, 0);
    }

    public void stop() {
        updater.cancel();
    }
/*
    // Update distance variable
    public void update() {
        i2c.write(lidar_config_register, 0x04); // Initiate measurement
        Timer.delay(0.04); // Delay for measurement to be taken
        i2c.read(lidar_distance_register, 2, distance); // Read in measurement
        Timer.delay(0.01); // Delay to prevent over polling
    }*/

    // Timer task to keep distance updated
    private class LidarUpdater extends TimerTask {
        public void run() {
            while (true) {
                update();
                try {
                    Thread.sleep((long) lidarPollDuration.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setAveraging(boolean shouldAverage) {
    }

}