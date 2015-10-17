package xbot.common.controls;

import static org.junit.Assert.*;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;
import xbot.common.injection.wpi_factories.WPIFactory;

import xbot.common.wpi_extensions.mechanism_wrappers.XJoystick;
public class JoystickButtonManagerTest extends BaseWPITest {

	@Test
	public void testAvailability(){
	    
	    WPIFactory factory = this.injector.getInstance(WPIFactory.class);
		XJoystick testJoystick = factory.getJoystick(1);
		
		JoystickButtonManager testButtons = new JoystickButtonManager(12,factory,testJoystick);
		
		int i = 13;
		assertTrue("Button " + i + " should be null." , null == testButtons.getifAvailable(i));
		
		i = 0;
        assertTrue("Button " + i + " should be null." , null == testButtons.getifAvailable(i));
        
        i = -1;
        assertTrue("Button " + i + " should be null." , null == testButtons.getifAvailable(i));
		
		for(int x=1;x<=12;x++){
			assertTrue("Button " + x + " should not be null." , null != testButtons.getifAvailable(x));
		}
	      for(int x=1;x<=12;x++){
	            assertTrue("Button " + x + " should be null." , null == testButtons.getifAvailable(x));
        }
		
	}
	
}
