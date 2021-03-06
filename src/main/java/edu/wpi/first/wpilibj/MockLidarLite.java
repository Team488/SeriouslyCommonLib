package edu.wpi.first.wpilibj;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import edu.wpi.first.wpilibj.I2C.Port;
import xbot.common.controls.sensors.XLidarLite;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.properties.PropertyFactory;

public class MockLidarLite extends XLidarLite {

    @AssistedInject
    public MockLidarLite(@Assisted("port") Port port, PropertyFactory propMan, DevicePolice police, @Assisted("prefix") String prefix) {
        super(port, propMan, police, prefix);
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
