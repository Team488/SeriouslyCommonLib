package xbot.common.controls.sensors;

import com.ctre.phoenix.sensors.CANCoder;
import com.google.inject.assistedinject.Assisted;

public class CANCoderAdapter extends XAbsoluteEncoder {
    
    private CANCoder cancoder;

    public CANCoderAdapter(@Assisted("deviceId") int deviceId) {
        this.cancoder = new CANCoder(deviceId);
    }

    @Override
    public double getPosition() {
        return this.cancoder.getPosition();
    }

    @Override
    public double getAbsolutePosition() {
        return this.cancoder.getAbsolutePosition();
    }

    @Override
    public double getVelocity() {
        return this.cancoder.getVelocity();
    }

    @Override
    public void setPosition(double newPosition) {
        this.cancoder.setPosition(newPosition);
    }
}
