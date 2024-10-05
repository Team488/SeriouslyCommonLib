package xbot.common.subsystems.drive.swerve;

public class SwerveSuggestedRotation {

    /**
     * Used for SwerveDriveRotationAdvisor to pass back values in one of two ways:
     * DesiredHeading (The heading you want to be at)
     * HeadingPower (The power of which you should be rotating)
     */
    public double value;
    public RotationGoalType type;

    public enum RotationGoalType {
        DesiredHeading,
        HumanControlHeadingPower
    }

    public SwerveSuggestedRotation(double value, RotationGoalType type) {
        this.value = value;
        this.type = type;
    }

    public SwerveSuggestedRotation() {
        this.value = 0;
        this.type = RotationGoalType.HumanControlHeadingPower;
    }
}
