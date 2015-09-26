package xbot.common.wpi_extensions.mechanism_wrappers;

public interface XSolenoid extends XBaseIO {
	public void set(boolean on);

    public boolean get();

    void setInverted(boolean isInverted);
}
