package edu.wpi.first.wpilibj;

public class MockDigitalInput implements xbot.common.wpi_extensions.mechanism_wrappers.XDigitalInput {

	protected boolean value;
	
	public MockDigitalInput(int channel) {
	}
	
	public void set_value(boolean value) {
		this.value = value;
	}
	
	public boolean get() {
		return value;
	}

}
