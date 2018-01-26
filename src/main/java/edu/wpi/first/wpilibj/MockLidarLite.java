package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.I2C.Port;
import xbot.common.controls.sensors.XLidarLite;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.properties.XPropertyManager;

public class MockLidarLite extends XLidarLite {

    @Inject
    public MockLidarLite(@Assisted("port") Port port, XPropertyManager propMan, DevicePolice police) {
        super(port, propMan, police);
    }

    @Override
    protected void update() {
        // no-op, but still need to delay to prevent high speed loop
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }        
    }
    
    public void setTestDistance(int testDistance) {
        byte[] fakeDistance = new byte[2];
        fakeDistance[0] = (byte)(testDistance >> 8);
        fakeDistance[1] = (byte)(testDistance % 256);
    }

}
