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
    
    private final int mux_address = 0x70;
    private final int lidar_address = 0x62;
    private final int lidar_config_register = 0x00;
    private final int lidar_distance_register = 0x8f;

    private DistanceSensor sensorA;
    private DistanceSensor sensorB;

    public MultiplexedLidarPair(Port port, byte lidarMuxIdA, byte lidarMuxIdB, XPropertyManager propMan) {

        log.info("Creating MultiplexedLidarPair on port: " + port.toString());
        
        this.lidarMuxIdA = lidarMuxIdA;
        this.lidarMuxIdB = lidarMuxIdB;
        
        i2c = new I2C(port, lidar_address);

        distanceA = new byte[2];
        distanceB = new byte[2];
        
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
        
        initiateMeasurements();
    }
    
    @Override
    public DistanceSensor getSensorA() {
        return sensorA;
    }
    
    @Override
    public DistanceSensor getSensorB() {
        return sensorB;
    }
    
    private void initiateMeasurements() {
        i2c.write(mux_address, 1 << lidarMuxIdA);
        i2c.write(lidar_config_register, 0x04);
        i2c.write(mux_address, 1 << lidarMuxIdB);
        i2c.write(lidar_config_register, 0x04);
    }

    @Override
    public void update() {
        i2c.write(mux_address, 1 << lidarMuxIdA);
        i2c.read(lidar_distance_register, 2, distanceA);
        i2c.write(mux_address, 1 << lidarMuxIdB);
        i2c.read(lidar_distance_register, 2, distanceB);
        
        initiateMeasurements();
    }
}