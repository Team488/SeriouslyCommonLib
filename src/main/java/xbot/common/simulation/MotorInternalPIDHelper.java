package xbot.common.simulation;

import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Rotations;

import edu.wpi.first.units.measure.AngularVelocity;
import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;
import xbot.common.math.PIDManager;

/**
 * Utility for running simple PID logic on a MockCANMotorController in simulation.
 * Applies PID output as motor power based on the motor’s current control mode.
 */
public class MotorInternalPIDHelper {
    public static void updateInternalPID(MockCANMotorController motor, PIDManager pidManager) {
        updateInternalPIDWithGravity(motor, pidManager, 0.0);
    }

    public static void updateInternalPIDWithGravity(
            MockCANMotorController motor, PIDManager pidManager, double gravityFeedForward) {
        // based on the motor state, potentially run internal PID if need be
        if (motor.getControlMode() == MockCANMotorController.ControlMode.Position) {
            // run a simple pid to mimic the internal pid of the motor controller
            var targetPosition = motor.getTargetPosition();
            var currentPosition = motor.getPosition();
            var output = pidManager.calculate(targetPosition.in(Rotations), currentPosition.in(Rotations))
                    + gravityFeedForward;
            motor.setPower(output);
        } else {
            pidManager.reset();
        }
    }

    public static void updateInternalPIDWithVelocity(
            MockCANMotorController motor, PIDManager pidManager, AngularVelocity targetVelocity) {
        if (motor.getControlMode() == MockCANMotorController.ControlMode.Velocity) {
            var currentVelocity = motor.getVelocity();
            var output = pidManager.calculate(targetVelocity.in(RPM), currentVelocity.in(RPM));
            motor.setPower(output);
        } else {
            pidManager.reset();
        }
    }
}
