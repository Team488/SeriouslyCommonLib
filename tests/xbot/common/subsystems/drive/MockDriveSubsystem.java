package xbot.common.subsystems.drive;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.subsystems.BaseDriveSubsystem;
import xbot.common.subsystems.BaseDrivePlatform;

@Singleton
public class MockDriveSubsystem extends BaseDriveSubsystem {

    @Inject
    public MockDriveSubsystem(BaseDrivePlatform platform) {
        super(platform);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Used in testing to try out other drive platforms - the real robot
     * shouldn't need this, as it won't be hot-swapping out to a new chassis.
     * @param platform
     */
    public void setDrivePlatform(BaseDrivePlatform platform) {
        this.platform = platform;
    }
    
    public BaseDrivePlatform getDrivePlatform() {
        return platform;
    }

}
