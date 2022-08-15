package edu.wpi.first.wpilibj;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.I2C.Port;
import xbot.common.controls.sensors.XLidarLite;
import xbot.common.injection.DevicePolice;
import xbot.common.properties.PropertyFactory;

public class MockLidarLite extends XLidarLite {

    @AssistedFactory
    public abstract static class MockLidarLiteFactory implements XLidarLiteFactory {
        public abstract MockLidarLite create(@Assisted("port") Port port, @Assisted("prefix") String prefix);
    }

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
