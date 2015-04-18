package xbot.common.wpi_extensions.mechanism_wrappers;

import edu.wpi.first.wpilibj.Solenoid;

public class SolenoidWPIAdapter implements XSolenoid {

	Solenoid solenoid;
    private boolean inverted;
    
	public SolenoidWPIAdapter(int channel) {
		this.solenoid = new Solenoid(channel);
	}
	
	@Override
	public void set(boolean on) {
		this.solenoid.set(on ^ inverted);
	}
	
	@Override
	public boolean get()
	{
	    return this.solenoid.get() ^ inverted;
	}
    
	@Override
    public void setInverted(boolean isInverted)
    {
        this.inverted = isInverted;
    }

}
