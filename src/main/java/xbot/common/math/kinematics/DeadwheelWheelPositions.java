package xbot.common.math.kinematics;

import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.interpolation.Interpolatable;
import edu.wpi.first.units.measure.Distance;
import java.util.Objects;

/** Represents the wheel positions for a differential drive drivetrain. */
public class DeadwheelWheelPositions
        implements Interpolatable<DeadwheelWheelPositions> {
    /** Distance measured by the left side. */
    public double leftMeters;

    /** Distance measured by the right side. */
    public double rightMeters;

    /** Distance measured by the front side. */
    public double frontMeters;

    /** Distance measured by the left side. */
    public double rearMeters;

    /**
     * Constructs a DeadwheelWheelPositions.
     *
     * @param leftMeters  Distance measured by the left side.
     * @param rightMeters Distance measured by the right side.
     * @param frontMeters Distance measured by the left side.
     * @param rearMeters  Distance measured by the right side.
     */
    public DeadwheelWheelPositions(double leftMeters, double rightMeters, double frontMeters, double rearMeters) {
        this.leftMeters = leftMeters;
        this.rightMeters = rightMeters;
        this.frontMeters = frontMeters;
        this.rearMeters = rearMeters;
    }

    /**
     * Constructs a DeadwheelWheelPositions.
     *
     * @param left  Distance measured by the left side.
     * @param right Distance measured by the right side.
     * @param front Distance measured by the front side.
     * @param rear  Distance measured by the rear side.
     */
    public DeadwheelWheelPositions(Distance left, Distance right, Distance front, Distance rear) {
        this(left.in(Meters), right.in(Meters), front.in(Meters), rear.in(Meters));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DeadwheelWheelPositions other
                && Math.abs(other.leftMeters - leftMeters) < 1E-9
                && Math.abs(other.rightMeters - rightMeters) < 1E-9
                && Math.abs(other.frontMeters - frontMeters) < 1E-9
                && Math.abs(other.rearMeters - rearMeters) < 1E-9;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftMeters, rightMeters, frontMeters, rearMeters);
    }

    @Override
    public String toString() {
        return String.format(
                "DifferentialDriveWheelPositions(Left: %.2f m, Right: %.2f m, Front: %.2f, Rear: %.2f", leftMeters,
                rightMeters, frontMeters, rearMeters);
    }

    @Override
    public DeadwheelWheelPositions interpolate(
            DeadwheelWheelPositions endValue, double t) {
        return new DeadwheelWheelPositions(
                MathUtil.interpolate(this.leftMeters, endValue.leftMeters, t),
                MathUtil.interpolate(this.rightMeters, endValue.rightMeters, t),
                MathUtil.interpolate(this.frontMeters, endValue.frontMeters, t),
                MathUtil.interpolate(this.rearMeters, endValue.rearMeters, t));
    }
}
