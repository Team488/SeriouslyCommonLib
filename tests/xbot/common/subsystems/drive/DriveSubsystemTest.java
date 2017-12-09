package xbot.common.subsystems.drive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.BaseWPITest;
import xbot.common.math.XYPair;
import xbot.common.subsystems.BaseDriveSubsystem;

public class DriveSubsystemTest extends BaseWPITest {

    MockDriveSubsystem drive;
    
    @Override
    public void setUp() {
        super.setUp();
        
        this.drive = (MockDriveSubsystem)injector.getInstance(BaseDriveSubsystem.class);
    }
    
    protected double getOutputPercent(XCANTalon t) {
        return t.getOutputVoltage() / MockRobotIO.BUS_VOLTAGE;
    }
}
