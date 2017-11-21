package xbot.common.subsystems.drive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;
import xbot.common.math.PIDManager;
import xbot.common.subsystems.drive.control_logic.HeadingModule;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class HeadingModuleTest extends BaseWPITest {

    HeadingModule headingModule;
    BasePoseSubsystem pose;
    
    @Override
    public void setUp() {
        // TODO Auto-generated method stub
        super.setUp();
        
        PIDManager pid = pf.createPIDManager("Testo", 100, 0, 0);
        pid.setErrorThreshold(.03);
        pid.setEnableErrorThreshold(true);
        
        headingModule = clf.createHeadingModule(pid);
        pose = injector.getInstance(BasePoseSubsystem.class);
    }     
    
    @Test
    public void testTurnLeft() {
        mockRobotIO.setGyroHeading(0);
        double power = headingModule.calculateHeadingPower(90);
        assertEquals(1, power, 0.001);
        
        mockRobotIO.setGyroHeading(179);
        power = headingModule.calculateHeadingPower(-179);
        assertEquals(1, power, 0.001);
    }
    
    @Test
    public void testTurnRight() {
        mockRobotIO.setGyroHeading(0);
        double power = headingModule.calculateHeadingPower(-90);
        assertEquals(-1, power, 0.001);
        
        mockRobotIO.setGyroHeading(-179);
        power = headingModule.calculateHeadingPower(179);
        assertEquals(-1, power, 0.001);
    }
    
    @Test
    public void onTarget() {
        mockRobotIO.setGyroHeading(0);
        headingModule.reset();
        assertFalse(headingModule.isOnTarget());
        
        headingModule.calculateHeadingPower(1);
        assertTrue(headingModule.isOnTarget());
    }
}
