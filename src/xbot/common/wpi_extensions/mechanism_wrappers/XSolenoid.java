package xbot.common.wpi_extensions.mechanism_wrappers;

public interface XSolenoid {
	public void set(boolean on);

    public boolean get();

    void setInverted(boolean isInverted);
}
