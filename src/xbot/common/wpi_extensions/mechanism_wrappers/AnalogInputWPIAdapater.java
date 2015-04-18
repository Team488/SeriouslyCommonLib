package xbot.common.wpi_extensions.mechanism_wrappers;

import edu.wpi.first.wpilibj.AnalogInput;

public class AnalogInputWPIAdapater implements XAnalogInput {
	AnalogInput input;
	
	public AnalogInputWPIAdapater(int channel) {
		input = new AnalogInput(channel);
	}

	public int getValue() {
		return input.getValue();
	}
	
	public double getVoltage()
	{
		return input.getVoltage();
	}
	
	public double getAverageVoltage()
	{
		return input.getAverageVoltage();
	}
	
	public void setAverageBits(int bits)
	{
		input.setAverageBits(bits);
	}
	
	public AnalogInput getInternalDevice()
	{
	    return input;
	}

    @Override
    public int getChannel() {
        return input.getChannel();
    }

    @Override
    public boolean getAsDigital(double threshold) {
        return getVoltage() >= threshold;
    }
}


