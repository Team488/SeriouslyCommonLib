package xbot.common.subsystems.drive.swerve;

import javax.inject.Inject;

import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.BaseMotorPidSubsystem;

/**
 * Container for steering motor controller PIDs.
 */
public class SwerveSteeringMotorPidSubsystem extends BaseMotorPidSubsystem {

    @Inject
    public SwerveSteeringMotorPidSubsystem(PropertyFactory pf) {
        super(pf, 0.5, 0.0, 0.0, 0.0, -1.0, 1.0, 0.02, 0.05);
    }

}