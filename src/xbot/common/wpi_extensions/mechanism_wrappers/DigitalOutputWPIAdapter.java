package xbot.common.wpi_extensions.mechanism_wrappers;

import edu.wpi.first.wpilibj.DigitalOutput;

public class DigitalOutputWPIAdapter implements XDigitalOutput {
	
	DigitalOutput adapter;
	/**
	 * Create an instance of a Digital Output class. Creates a digital output
	 * given a channel.
	 *
	 * @param channel The channel to control.
	 */
	public DigitalOutputWPIAdapter (int channel) {
		adapter = new DigitalOutput(channel);
	}

	/**
	 * Get the channel of the digital input
	 *
	 * @return The GPIO channel number that this object represents.
	 */
	public int getChannel() {
		return adapter.getChannel();
	}
	
	public void set(boolean value)
	{
	    adapter.set(value);
	}
	
	public DigitalOutput getWPIDigitalOutput() {
		return adapter;
	}

}
