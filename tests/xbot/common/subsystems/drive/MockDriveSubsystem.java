package xbot.common.subsystems.drive;

import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.subsystems.BaseDriveSubsystem;
import xbot.common.controls.actuators.XCANTalon;

@Singleton
public class MockDriveSubsystem extends BaseDriveSubsystem {

    @Inject
    public MockDriveSubsystem() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    protected Map<XCANTalon, MotionRegistration> getAllMasterTalons() {
        // TODO Auto-generated method stub
        return null;
    }
}
