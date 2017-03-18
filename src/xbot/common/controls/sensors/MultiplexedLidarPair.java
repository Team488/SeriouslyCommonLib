package xbot.common.controls.sensors;

import java.util.TimerTask;

import org.apache.log4j.Logger;

import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.I2C.Port;

public class MultiplexedLidarPair implements DistanceSensorPair {

    private Logger log = Logger.getLogger(MultiplexedLidarPair.class);

    private byte lidarMuxIdA;
    private byte lidarMuxIdB;
    
    private I2C i2c;
    private byte[] distanceA;
    private byte[] distanceB;
    private LidarUpdater task;
    
    private final int mux_address = 0x70;
    private final int lidar_address = 0x62;
    private final int lidar_config_register = 0x00;
    private final int lidar_distance_register = 0x8f;

    private DoubleProperty lidarPollDuration;
    private java.util.Timer updater;

    private DistanceSensor sensorA;
    private DistanceSensor sensorB;

    public MultiplexedLidarPair(Port port, byte lidarMuxIdA, byte lidarMuxIdB, XPropertyManager propMan) {

        log.info("Creating MultiplexedLidarPair on port: " + port.toString());
        lidarPollDuration = propMan.createPersistentProperty("Lidar poll duration (ms)", 100d);

        this.lidarMuxIdA = lidarMuxIdA;
        this.lidarMuxIdB = lidarMuxIdB;
        
        i2c = new I2C(port, lidar_address);

        distanceA = new byte[2];
        distanceB = new byte[2];

        task = new LidarUpdater();
        updater = new java.util.Timer();
        
        sensorA = new DistanceSensor() {
            
            @Override
            public void setAveraging(boolean shouldAverage) {
                // Intentionally left blank
            }
            
            @Override
            public double getDistance() {
                return (int) Integer.toUnsignedLong(distanceA[0] << 8) + Byte.toUnsignedInt(distanceA[1]);
            }
        };
        
        sensorB = new DistanceSensor() {
            
            @Override
            public void setAveraging(boolean shouldAverage) {
                // Intentionally left blank
            }
            
            @Override
            public double getDistance() {
                return (int) Integer.toUnsignedLong(distanceB[0] << 8) + Byte.toUnsignedInt(distanceB[1]);
            }
        };

        this.start();
    }
    
    @Override
    public DistanceSensor getSensorA() {
        return sensorA;
    }
    
    @Override
    public DistanceSensor getSensorB() {
        return sensorB;
    }

    @Override
    public void start() {
        log.info("Starting Lidar polling");
        updater.schedule(task, 0);
    }

    @Override
    public void stop() {
        updater.cancel();
    }

    // Update distance variable
    protected void update() {
        i2c.write(mux_address, 1 << lidarMuxIdA);
        readDistanceFromCurrentSensor(distanceA);

        i2c.write(mux_address, 1 << lidarMuxIdB);
        readDistanceFromCurrentSensor(distanceB);
        
        Timer.delay(0.01); // Delay to prevent over polling
    }
    
    private void readDistanceFromCurrentSensor(byte[] outData) {
        i2c.write(lidar_config_register, 0x04); // Initiate measurement
        Timer.delay(0.04); // Delay for measurement to be taken
        
        i2c.read(lidar_distance_register, 2, outData); // Read in measurement
    }

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
}