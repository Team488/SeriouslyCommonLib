package xbot.common.subsystems.drive.swerve;

import javax.inject.Inject;

import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.BaseMotorPidSubsystem;

/**
 * Container for drive motor controller PIDs.
 */
public class SwerveDriveMotorPidSubsystem extends BaseMotorPidSubsystem {

    @Inject
    public SwerveDriveMotorPidSubsystem(PropertyFactory pf) {
        super(pf);
    }

}