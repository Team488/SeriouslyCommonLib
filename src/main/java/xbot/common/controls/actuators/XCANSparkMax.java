package xbot.common.controls.actuators;

import com.revrobotics.AlternateEncoderType;
import com.revrobotics.CANAnalog;
import com.revrobotics.CANAnalog.AnalogMode;
import com.revrobotics.CANDigitalInput;
import com.revrobotics.CANError;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ExternalFollower;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.EncoderType;

import xbot.common.controls.sensors.XEncoder;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;
import xbot.common.properties.PropertyFactory;


public  abstract class XCANSparkMax {

    protected int deviceId;
    PropertyFactory propertyFactory;

    public XCANSparkMax(int deviceId, PropertyFactory propertyFactory, DevicePolice police) {
        this.deviceId = deviceId;
        this.propertyFactory = propertyFactory;
        police.registerDevice(DeviceType.CAN, deviceId);
    }

    /**** Speed Controller Interface ****/
	/**
	 * Common interface for setting the speed of a speed controller.
	 *
	 * @param speed The speed to set. Value should be between -1.0 and 1.0.
	 */
    public abstract void set(double speed);

    /**
     * Sets the voltage output of the SpeedController.  This is equivillant to 
     * a call to SetReference(output, rev::ControlType::kVoltage). The behavior
     * of this call differs slightly from the WPILib documetation for this call
     * since the device internally sets the desired voltage (not a compensation value).
     * That means that this *can* be a 'set-and-forget' call.
     *
     * @param outputVolts The voltage to output.
     */
	public abstract void setVoltage(double outputVolts);

	/**
	 * Common interface for getting the current set speed of a speed controller.
	 *
	 * @return The current set speed. Value is between -1.0 and 1.0.
	 */
	public abstract double get();

	/**
	 * Common interface for inverting direction of a speed controller.
	 *
	 * This call has no effect if the controller is a follower.
	 *
	 * @param isInverted The state of inversion, true is inverted.
	 */
	public abstract void setInverted(boolean isInverted);

	/**
	 * Common interface for returning the inversion state of a speed controller.
	 * 
     * This call has no effect if the controller is a follower.
	 *
	 * @return isInverted The state of inversion, true is inverted.
	 */
	public abstract boolean getInverted() ;

    /**
     * Common interface for disabling a motor.
     */
    public abstract void disable() ;


    public abstract void stopMotor() ;


    public abstract void pidWrite(double output) ;

    /******* Extended Functions *******/
    /**
     * Returns and object for interfacing with the encoder connected to the 
     * encoder pins or front port of the SPARK MAX.
     * 
     * The default encoder type is assumed to be the hall effect for brushless.
     * This can be modified for brushed DC to use a quadrature encoder.
     * 
     * Assumes that the encoder the is integrated encoder, configured as:
     * EncoderType.kHallEffect, 0 counts per revolution.
     * 
     * @return An object for interfacing with the integrated encoder.
     */
    public abstract XEncoder getEncoder() ;

    /**
     * Returns and object for interfacing with the encoder connected to the 
     * encoder pins or front port of the SPARK MAX.
     * 
     * The default encoder type is assumed to be the hall effect for brushless.
     * This can be modified for brushed DC to use a quadrature encoder.
     * 
     * @param sensorType The encoder type for the motor: kHallEffect or kQuadrature
     * @param counts_per_rev The counts per revolution of the encoder
     * @return An object for interfacing with an encoder
     */
    public abstract XEncoder getEncoder(EncoderType sensorType, int counts_per_rev) ;

    /**
     * Returns an object for interfacing with an encoder connected to the alternate 
     * data port configured pins. This is defined as :
     * 
     * Mutli-function Pin: Encoder A
     * Limit Switch Reverse: Encoder B
     * 
     * This call will disable the limit switch inputs
     * @return Returns an object for interfacing with an encoder connected to the alternate 
     * data port configured pins
     */
    public abstract XEncoder getAlternateEncoder() ;

    /**
     * Returns an object for interfacing with an encoder connected to the alternate 
     * data port configured pins. This is defined as :
     * 
     * Mutli-function Pin: Encoder A
     * Limit Switch Reverse: Encoder B
     * 
     * This call will disable the limit switch inputs.
     * 
     * @param counts_per_rev the Counts per revolution of the encoder
     * @param sensorType The encoder type for the motor: currently only kQuadrature
     * @return Returns an object for interfacing with an encoder connected to the alternate 
     * data port configured pins
     */
    public abstract XEncoder getAlternateEncoder(AlternateEncoderType sensorType, int counts_per_rev) ;

    /**
     * @param mode The mode of the analog sensor, either absolute or relative
     * @return An object for interfacing with a connected analog sensor.
     */
    public abstract CANAnalog getAnalog(AnalogMode mode) ;

    /**
     * @return An object for interfacing with the integrated PID controller.
     */
    public abstract CANPIDController getPIDController() ;

    /**
     * @return An object for interfacing with the integrated forward limit switch.
     *
     * @param polarity Whether the limit switch is normally open or normally closed.
     */
    public abstract CANDigitalInput getForwardLimitSwitch(CANDigitalInput.LimitSwitchPolarity polarity) ;

    /**
     * @return An object for interfacing with the integrated reverse limit switch.
     *
     * @param polarity Whether the limit switch is normally open or normally closed.
     */
    public abstract CANDigitalInput getReverseLimitSwitch(CANDigitalInput.LimitSwitchPolarity polarity) ;

    /**
     * Sets the current limit in Amps.
     *
     * The motor controller will reduce the controller voltage output to avoid
     * surpassing this limit. This limit is enabled by default and used for
     * brushless only. This limit is highly recommended when using the NEO brushless
     * motor.
     *
     * The NEO Brushless Motor has a low internal resistance, which can mean large
     * current spikes that could be enough to cause damage to the motor and
     * controller. This current limit provides a smarter strategy to deal with high
     * current draws and keep the motor and controller operating in a safe region.
     *
     * @param limit The current limit in Amps.
     *
     * @return CANError Set to CANError.kOK if successful
     *
     */
    public abstract CANError setSmartCurrentLimit(int limit) ;

    /**
     * Sets the current limit in Amps.
     *
     * The motor controller will reduce the controller voltage output to avoid
     * surpassing this limit. This limit is enabled by default and used for
     * brushless only. This limit is highly recommended when using the NEO brushless
     * motor.
     *
     * The NEO Brushless Motor has a low internal resistance, which can mean large
     * current spikes that could be enough to cause damage to the motor and
     * controller. This current limit provides a smarter strategy to deal with high
     * current draws and keep the motor and controller operating in a safe region.
     *
     * The controller can also limit the current based on the RPM of the motor in a
     * linear fashion to help with controllability in closed loop control. For a
     * response that is linear the entire RPM range leave limit RPM at 0.
     *
     * @param stallLimit The current limit in Amps at 0 RPM.
     * @param freeLimit  The current limit at free speed (5700RPM for NEO).
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError setSmartCurrentLimit(int stallLimit, int freeLimit) ;

    /**
     * Sets the current limit in Amps.
     *
     * The motor controller will reduce the controller voltage output to avoid
     * surpassing this limit. This limit is enabled by default and used for
     * brushless only. This limit is highly recommended when using the NEO brushless
     * motor.
     *
     * The NEO Brushless Motor has a low internal resistance, which can mean large
     * current spikes that could be enough to cause damage to the motor and
     * controller. This current limit provides a smarter strategy to deal with high
     * current draws and keep the motor and controller operating in a safe region.
     *
     * The controller can also limit the current based on the RPM of the motor in a
     * linear fashion to help with controllability in closed loop control. For a
     * response that is linear the entire RPM range leave limit RPM at 0.
     *
     * @param stallLimit The current limit in Amps at 0 RPM.
     * @param freeLimit  The current limit at free speed (5700RPM for NEO).
     * @param limitRPM   RPM less than this value will be set to the stallLimit, RPM
     *                   values greater than limitRPM will scale linearly to
     *                   freeLimit
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError setSmartCurrentLimit(int stallLimit, int freeLimit, int limitRPM) ;

    /**
     * Sets the secondary current limit in Amps.
     *
     * The motor controller will disable the output of the controller briefly if the
     * current limit is exceeded to reduce the current. This limit is a simplified
     * 'on/off' controller. This limit is enabled by default but is set higher than
     * the default Smart Current Limit.
     *
     * The time the controller is off after the current limit is reached is
     * determined by the parameter limitCycles, which is the number of PWM cycles
     * (20kHz). The recommended value is the default of 0 which is the minimum time
     * and is part of a PWM cycle from when the over current is detected. This
     * allows the controller to regulate the current close to the limit value.
     *
     * The total time is set by the equation
     *
     * <code>
     * t = (50us - t0) + 50us * limitCycles
     * t = total off time after over current
     * t0 = time from the start of the PWM cycle until over current is detected
     * </code>
     *
     *
     * @param limit The current limit in Amps.
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError setSecondaryCurrentLimit(double limit) ;

    /**
     * Sets the secondary current limit in Amps.
     *
     * The motor controller will disable the output of the controller briefly if the
     * current limit is exceeded to reduce the current. This limit is a simplified
     * 'on/off' controller. This limit is enabled by default but is set higher than
     * the default Smart Current Limit.
     *
     * The time the controller is off after the current limit is reached is
     * determined by the parameter limitCycles, which is the number of PWM cycles
     * (20kHz). The recommended value is the default of 0 which is the minimum time
     * and is part of a PWM cycle from when the over current is detected. This
     * allows the controller to regulate the current close to the limit value.
     *
     * The total time is set by the equation
     *
     * <code>
     * t = (50us - t0) + 50us * limitCycles
     * t = total off time after over current
     * t0 = time from the start of the PWM cycle until over current is detected
     * </code>
     *
     *
     * @param limit      The current limit in Amps.
     * @param chopCycles The number of additional PWM cycles to turn the driver off
     *                   after overcurrent is detected.
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError setSecondaryCurrentLimit(double limit, int chopCycles) ;

    /**
     * Sets the idle mode setting for the SPARK MAX.
     *
     * @param mode Idle mode (coast or brake).
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError setIdleMode(IdleMode mode) ;

    /**
     * Gets the idle mode setting for the SPARK MAX.
     *
     * This uses the Get Parameter API and should be used infrequently. This
     * function uses a non-blocking call and will return a cached value if the
     * parameter is not returned by the timeout. The timeout can be changed by
     * calling SetCANTimeout(int milliseconds)
     *
     * @return IdleMode Idle mode setting
     */
    public abstract IdleMode getIdleMode() ;

    /**
     * Sets the voltage compensation setting for all modes on the SPARK MAX and
     * enables voltage compensation.
     *
     * @param nominalVoltage Nominal voltage to compensate output to
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError enableVoltageCompensation(double nominalVoltage) ;

    /**
     * Disables the voltage compensation setting for all modes on the SPARK MAX.
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError disableVoltageCompensation() ;

    /**
     * Get the configured voltage compensation nominal voltage value
     *
     * @return The nominal voltage for voltage compensation mode.
     */
    public abstract double getVoltageCompensationNominalVoltage() ;

    /**
     * Sets the ramp rate for open loop control modes.
     *
     * This is the maximum rate at which the motor controller's output is allowed to
     * change.
     *
     * @param rate Time in seconds to go from 0 to full throttle.
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError setOpenLoopRampRate(double rate) ;

    /**
     * Sets the ramp rate for closed loop control modes.
     *
     * This is the maximum rate at which the motor controller's output is allowed to
     * change.
     *
     * @param rate Time in seconds to go from 0 to full throttle.
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError setClosedLoopRampRate(double rate) ;

    /**
     * Get the configured open loop ramp rate
     *
     * This is the maximum rate at which the motor controller's output is allowed to
     * change.
     *
     * @return ramp rate time in seconds to go from 0 to full throttle.
     */
    public abstract double getOpenLoopRampRate() ;

    /**
     * Get the configured closed loop ramp rate
     *
     * This is the maximum rate at which the motor controller's output is allowed to
     * change.
     *
     * @return ramp rate time in seconds to go from 0 to full throttle.
     */
    public abstract double getClosedLoopRampRate() ;

    /**
     * Causes this controller's output to mirror the provided leader.
     *
     * Only voltage output is mirrored. Settings changed on the leader do not affect
     * the follower.
     * 
     * The motor will spin in the same direction as the leader. This can be changed
     * by passing a true constant after the leader parameter.
     * 
     * Following anything other than a CAN SPARK MAX is not officially supported.
     *
     * @param leader The motor controller to follow.
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError follow(final CANSparkMax leader) ;

    /**
     * Causes this controller's output to mirror the provided leader.
     *
     * Only voltage output is mirrored. Settings changed on the leader do not affect
     * the follower.
     * 
     * Following anything other than a CAN SPARK MAX is not officially supported.
     *
     * @param leader The motor controller to follow.
     * @param invert Set the follower to output opposite of the leader
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError follow(final CANSparkMax leader, boolean invert) ;

    /**
     * Causes this controller's output to mirror the provided leader.
     *
     * Only voltage output is mirrored. Settings changed on the leader do not affect
     * the follower.
     * 
     * The motor will spin in the same direction as the leader. This can be changed
     * by passing a true constant after the deviceID parameter.
     * 
     * Following anything other than a CAN SPARK MAX is not officially supported.
     *
     * @param leader   The type of motor controller to follow (Talon SRX, Spark Max,
     *                 etc.).
     * @param deviceID The CAN ID of the device to follow.
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError follow(ExternalFollower leader, int deviceID) ;

    /**
     * Causes this controller's output to mirror the provided leader.
     *
     * Only voltage output is mirrored. Settings changed on the leader do not affect
     * the follower.
     * 
     * Following anything other than a CAN SPARK MAX is not officially supported.
     *
     * @param leader   The type of motor controller to follow (Talon SRX, Spark Max,
     *                 etc.).
     * @param deviceID The CAN ID of the device to follow.
     *
     * @param invert   Set the follower to output opposite of the leader
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError follow(ExternalFollower leader, int deviceID, boolean invert) ;

    /**
     * Returns whether the controller is following another controller
     *
     * @return True if this device is following another controller false otherwise
     */
    public abstract boolean isFollower() ;

    /**
     * @return All fault bits as a short
     */
    public abstract short getFaults() ;

    /**
     * @return All sticky fault bits as a short
     */
    public abstract short getStickyFaults() ;

    /**
     * Get the value of a specific fault
     *
     * @param faultID The ID of the fault to retrive
     *
     * @return True if the fault with the given ID occurred.
     */
    public abstract boolean getFault(FaultID faultID) ;

    /**
     * Get the value of a specific sticky fault
     *
     * @param faultID The ID of the sticky fault to retrive
     *
     * @return True if the sticky fault with the given ID occurred.
     */
    public abstract boolean getStickyFault(FaultID faultID) ;

    /**
     * @return The voltage fed into the motor controller.
     */
    public abstract double getBusVoltage() ;

    /**
     * @return The motor controller's applied output duty cycle.
     */
    public abstract double getAppliedOutput() ;

    /**
     * @return The motor controller's output current in Amps.
     */
    public abstract double getOutputCurrent() ;

    /**
     * @return The motor temperature in Celsius.
     */
    public abstract double getMotorTemperature() ;

    /**
     * Clears all sticky faults.
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError clearFaults() ;

    /**
     * Writes all settings to flash.
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError burnFlash() ;

    /**
     * Sets timeout for sending CAN messages with SetParameter* and GetParameter* calls.
     * These calls will block for up to this amoutn of time before returning a timeout
     * erro. A timeout of 0 will make the SetParameter* calls non-blocking, and instead
     * will check the response in a separate thread. With this configuration, any
     * error messages will appear on the drivestration but will not be returned by the
     * GetLastError() call.
     *
     * @param milliseconds The timeout in milliseconds.
     *
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError setCANTimeout(int milliseconds) ;

    /**
     * Enable soft limits
     *
     * @param direction the direction of motion to restrict
     * 
     * @param enable set true to enable soft limits
     * 
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError enableSoftLimit(SoftLimitDirection direction, boolean enable) ;

    /**
     * Set the soft limit based on position. The default unit is
     * rotations, but will match the unit scaling set by the user.
     * 
     * Note that this value is not scaled internally so care must
     * be taken to make sure these units match the desired conversion
     *
     * @param direction the direction of motion to restrict
     * 
     * @param limit position soft limit of the controller
     * 
     * @return CANError Set to CANError.kOK if successful
     */
    public abstract CANError setSoftLimit(SoftLimitDirection direction, float limit) ;

    /**
     * Get the soft limit setting in the controller
     *
     * @param direction the direction of motion to restrict
     * 
     * @return position soft limit setting of the controller
     */
    public abstract double getSoftLimit(SoftLimitDirection direction) ;

    /**
     * @param direction The direction of the motion to restrict
     * 
     * @return true if the soft limit is enabled.
     */
    public abstract boolean isSoftLimitEnabled(SoftLimitDirection direction) ;

    /**
     * All device errors are tracked on a per thread basis for all
     * devices in that thread. This is meant to be called 
     * immediately following another call that has the possibility 
     * of returning an error to validate if an  error has occurred. 
     * 
     * @return the last error that was generated.
     */
	public abstract CANError getLastError();
}