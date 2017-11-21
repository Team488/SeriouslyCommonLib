package xbot.common.subsystems.drive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;
import xbot.common.subsystems.drive.control_logic.HeadingAssistModule;
import xbot.common.subsystems.drive.control_logic.HeadingModule;

public class HeadingAssistModuleTest extends BaseWPITest {

    HeadingAssistModule ham;
    
    @Override
    public void setUp() {
        // TODO Auto-generated method stub
        super.setUp();
        
        HeadingModule hm = clf.createHeadingModule(pf.createPIDManager("Testo", 1000, 0, 0));
        ham = clf.createHeadingAssistModule(hm);
    }
    
    @Test
    public void testFullStateMachine() {
        step1_humanDrive();
        step2_humanStops();
        step3_timePasses();
        step4_robotRotated();
        
        // robot is returned to original position
        mockRobotIO.setGyroHeading(0);
        double power = ham.calculateHeadingPower(0);
        assertEquals(0, power, 0.001);
    }
    
    @Test
    public void interruptedAfterStep1() {
        step1_humanDrive();
        
        // system then recovers
        interruptingInput();
        
        step2_humanStops();
        step3_timePasses();
        step4_robotRotated();
    }
    
    @Test
    public void interruptedAfterStep2() {
        step1_humanDrive();
        step2_humanStops();
        
        interruptingInput();
        
        step2_humanStops();
        step3_timePasses();
        step4_robotRotated();
    }
    
    @Test
    public void interruptedAfterStep3() {
        step1_humanDrive();
        step2_humanStops();
        step3_timePasses();
        interruptingInput();
        step2_humanStops();
        step3_timePasses();
        step4_robotRotated();
    }
    
    @Test
    public void interruptedAfterStep4() {
        step1_humanDrive();
        step2_humanStops();
        step3_timePasses();
        step4_robotRotated();
        
        interruptingInput();
        step2_humanStops();
        step3_timePasses();
        step4_robotRotated();
    }
    
    private void interruptingInput() {

        double power = ham.calculateHeadingPower(.6627);
        assertEquals(.6627, power, 0.001);
        
    }
    
    private void step1_humanDrive() {

        // human is trying to rotate the robot
        double power = ham.calculateHeadingPower(1);
        assertEquals(1, power, 0.001);
        
    }
    
    private void step2_humanStops() {
        
        // human stops trying to rotate the robot
        double power = ham.calculateHeadingPower(0);
        assertEquals(0, power, 0.001);
        
    }
    
    private void step3_timePasses() {
        
        // time passes, this should "set" the desired angle
        timer.advanceTimeInSecondsBy(1);
        double power = ham.calculateHeadingPower(0);
        assertEquals(0, power, 0.001);
    }
    
    private void step4_robotRotated() {
        
        // the robot undergoes some automatic rotation
        mockRobotIO.setGyroHeading(90);
        double power = ham.calculateHeadingPower(0);
        assertEquals(-1, power, 0.001);
    }
}
