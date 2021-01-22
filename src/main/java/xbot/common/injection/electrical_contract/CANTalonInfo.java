package xbot.common.injection.electrical_contract;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

public class CANTalonInfo {
    
    public int channel;
    public boolean inverted;
    public FeedbackDevice feedbackDevice;
    public boolean feedbackDeviceInverted;

    public double simulationScalingValue;

    public CANTalonInfo(int channel){
        this.channel = channel;
    }

    public CANTalonInfo(int channel, boolean inverted) {
        this(channel);
        this.inverted = inverted;
    }

    public CANTalonInfo(int channel, boolean inverted, FeedbackDevice feedbackDevice, boolean feedbackDeviceInverted) {
        this(channel, inverted);
        this.feedbackDevice = feedbackDevice;
        this.feedbackDeviceInverted = feedbackDeviceInverted;
    }

    public CANTalonInfo(int channel, boolean inverted, FeedbackDevice feedbackDevice, boolean feedbackDeviceInverted, double simulationScalingValue) {
        this(channel, inverted, feedbackDevice, feedbackDeviceInverted);
        this.simulationScalingValue = simulationScalingValue;
    }
}