package xbot.common.controls;

public interface XSolenoid extends XBaseIO {
	public void set(boolean on);

    public boolean get();

    void setInverted(boolean isInverted);
}
