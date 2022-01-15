package xbot.common.math;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;

/**
 * A rotation in a 2d coordinate frame, with its rotation wrapped from -pi to pi radians (-180 to 180 degrees).
 * Prefer this class over Rotation2d when representing robot pose.
 */
public class WrappedRotation2d extends Rotation2d {
  /**
   * Constructs a WrappedRotation2d with the given radian value.
   *
   * @param value The value of the angle in radians.
   */
  @JsonCreator
  public WrappedRotation2d(@JsonProperty(required = true, value = "radians") double value) {
    super(MathUtil.angleModulus(value));
  }

  /**
   * Constructs a WrappedRotation2d with the given x and y (cosine and sine) components. The x and y don't have to be normalized.
   *
   * @param x The x component or cosine of the rotation.
   * @param y The y component or sine of the rotation.
   */
  public WrappedRotation2d(double x, double y) {
    super(x, y);
  }

  /**
   * Converts a Rotation2d to the wrapped equivalent.
   * @param rotation The rotation.
   * @return The rotation with its angle wrapped from -pi to pi radians.
   */
  public static WrappedRotation2d fromRotation2d(Rotation2d rotation) {
      return new WrappedRotation2d(rotation.getCos(), rotation.getSin());
  }

  /**
   * Constructs and returns a Rotation2d with the given degree value.
   *
   * @param degrees The value of the angle in degrees.
   * @return The rotation object with the desired angle value.
   */
  public static WrappedRotation2d fromDegrees(double degrees) {
    return new WrappedRotation2d(Math.toRadians(degrees));
  }
}
