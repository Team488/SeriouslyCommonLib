package xbot.common.controls.actuators;

import xbot.common.controls.XBaseIO;

public interface XDigitalOutput extends XBaseIO {
    
	public void set(boolean value);
	
}
