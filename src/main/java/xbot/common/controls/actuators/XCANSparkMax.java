package xbot.common.controls.actuators;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.ExternalFollower;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.REVLibError;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;

import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public abstract class XCANSparkMax {

    protected int deviceId;
    protected String prefix = "";
    PropertyFactory propertyFactory;

    final DoubleProperty kPprop;
    final DoubleProperty kIprop;
    final DoubleProperty kDprop;
    final DoubleProperty kIzProp;
    final DoubleProperty kFFprop;
    final DoubleProperty kMaxOutputProp;
    final DoubleProperty kMinOutoutProp;

    final DoubleProperty percentProp;
    final DoubleProperty voltageProp;
    final DoubleProperty currentProp;

    protected final String policeTicket;

    protected boolean firstPeriodicCall = true;

    public abstract static class XCANSparkMaxFactory {
        public abstract XCANSparkMax create(
                DeviceInfo deviceInfo,
                String owningSystemPrefix,
                String name,
                XCANSparkMaxPIDProperties defaultPIDProperties);

        public XCANSparkMax create(DeviceInfo deviceInfo, String owningSystemPrefix, String name) {
            return create(deviceInfo, owningSystemPrefix, name, new XCANSparkMaxPIDProperties());
        }
    }

    protected XCANSparkMax(
            DeviceInfo deviceInfo,
            String owningSystemPrefix,
            String name,
            PropertyFactory pf,
            DevicePolice police,
            XCANSparkMaxPIDProperties defaultPIDProperties) {
        this.deviceId = deviceInfo.channel;
        this.propertyFactory = pf;
        this.propertyFactory.setPrefix(owningSystemPrefix);
        this.propertyFactory.appendPrefix(name);
        prefix = pf.getPrefix();
        policeTicket = police.registerDevice(DeviceType.CAN, deviceId, this);

        kPprop = pf.createPersistentProperty("kP", defaultPIDProperties.p);
        kIprop = pf.createPersistentProperty("kI", defaultPIDProperties.i);
        kDprop = pf.createPersistentProperty("kD", defaultPIDProperties.d);
        kIzProp = pf.createPersistentProperty("kIzone", defaultPIDProperties.iZone);
        kFFprop = pf.createPersistentProperty("kFeedForward", defaultPIDProperties.feedForward);
        kMaxOutputProp = pf.createPersistentProperty("kMaxOutput", defaultPIDProperties.maxOutput);
        kMinOutoutProp = pf.createPersistentProperty("kMinOutput", defaultPIDProperties.minOutput);

        percentProp = pf.createEphemeralProperty("Percent", 0);
        voltageProp = pf.createEphemeralProperty("Voltage", 0);
        currentProp = pf.createEphemeralProperty("Current", 0);
    }

    ///
    // Our own methods
    ///

    public String getPrefix() {
        return prefix;
    }

    private void setAllProperties() {
        setP(kPprop.get());
        setI(kIprop.get());
        setD(kDprop.get());
        setIZone(kIzProp.get());
        setFF(kFFprop.get());
        setOutputRange(kMinOutoutProp.get(), kMaxOutputProp.get());
    }

    public void periodic() {
        if (firstPeriodicCall) {
            setAllProperties();
            firstPeriodicCall = false;
        }
        kPprop.hasChangedSinceLastCheck((value) -> setP(value));
        kIprop.hasChangedSinceLastCheck((value) -> setI(value));
        kDprop.hasChangedSinceLastCheck((value) -> setD(value));
        kIzProp.hasChangedSinceLastCheck((value) -> setIZone(value));
        kFFprop.hasChangedSinceLastCheck((value) -> setFF(value));
        kMaxOutputProp.hasChangedSinceLastCheck((value) -> setOutputRange(kMinOutoutProp.get(), value));
        kMinOutoutProp.hasChangedSinceLastCheck((value) -> setOutputRange(value, kMaxOutputProp.get()));

        percentProp.set(getAppliedOutput());
        voltageProp.set(getAppliedOutput() * getBusVoltage());
        currentProp.set(getOutputCurrent());
    }

    /**** Speed Controller Interface ****/
    /**
     * Common interface for setting the speed of a speed controller.
     *
     * @param speed The speed to set. Value should be between -1.0 and 1.0.
     */
    public abstract void set(double speed);

    /**
     * Sets the voltage output of the SpeedController. This is equivillant to a call
     * to SetReference(output, rev::ControlType::kVoltage). The behavior of this
     * call differs slightly from the WPILib documetation for this call since the
     * device internally sets the desired voltage (not a compensation value). That
     * means that this *can* be a 'set-and-forget' call.
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
    public abstract boolean getInverted();

    /**
     * Common interface for disabling a motor.
     */
    public abstract void disable();

    public abstract void stopMotor();

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
     * @return REVLibError Set to REVLibError.kOK if successful
     *
     */
    public abstract REVLibError setSmartCurrentLimit(int limit);

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
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError setSmartCurrentLimit(int stallLimit, int freeLimit);

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
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError setSmartCurrentLimit(int stallLimit, int freeLimit, int limitRPM);

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
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError setSecondaryCurrentLimit(double limit);

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
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError setSecondaryCurrentLimit(double limit, int chopCycles);

    /**
     * Sets the idle mode setting for the SPARK MAX.
     *
     * @param mode Idle mode (coast or brake).
     *
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError setIdleMode(IdleMode mode);

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
    public abstract IdleMode getIdleMode();

    /**
     * Sets the voltage compensation setting for all modes on the SPARK MAX and
     * enables voltage compensation.
     *
     * @param nominalVoltage Nominal voltage to compensate output to
     *
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError enableVoltageCompensation(double nominalVoltage);

    /**
     * Disables the voltage compensation setting for all modes on the SPARK MAX.
     *
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError disableVoltageCompensation();

    /**
     * Get the configured voltage compensation nominal voltage value
     *
     * @return The nominal voltage for voltage compensation mode.
     */
    public abstract double getVoltageCompensationNominalVoltage();

    /**
     * Sets the ramp rate for open loop control modes.
     *
     * This is the maximum rate at which the motor controller's output is allowed to
     * change.
     *
     * @param rate Time in seconds to go from 0 to full throttle.
     *
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError setOpenLoopRampRate(double rate);

    /**
     * Sets the ramp rate for closed loop control modes.
     *
     * This is the maximum rate at which the motor controller's output is allowed to
     * change.
     *
     * @param rate Time in seconds to go from 0 to full throttle.
     *
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError setClosedLoopRampRate(double rate);

    /**
     * Get the configured open loop ramp rate
     *
     * This is the maximum rate at which the motor controller's output is allowed to
     * change.
     *
     * @return ramp rate time in seconds to go from 0 to full throttle.
     */
    public abstract double getOpenLoopRampRate();

    /**
     * Get the configured closed loop ramp rate
     *
     * This is the maximum rate at which the motor controller's output is allowed to
     * change.
     *
     * @return ramp rate time in seconds to go from 0 to full throttle.
     */
    public abstract double getClosedLoopRampRate();

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
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError follow(final XCANSparkMax leader);

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
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError follow(final XCANSparkMax leader, boolean invert);

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
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError follow(ExternalFollower leader, int deviceID);

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
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError follow(ExternalFollower leader, int deviceID, boolean invert);

    /**
     * Returns whether the controller is following another controller
     *
     * @return True if this device is following another controller false otherwise
     */
    public abstract boolean isFollower();

    /**
     * @return All fault bits as a short
     */
    public abstract short getFaults();

    /**
     * @return All sticky fault bits as a short
     */
    public abstract short getStickyFaults();

    /**
     * Get the value of a specific fault
     *
     * @param faultID The ID of the fault to retrive
     *
     * @return True if the fault with the given ID occurred.
     */
    public abstract boolean getFault(FaultID faultID);

    /**
     * Get the value of a specific sticky fault
     *
     * @param faultID The ID of the sticky fault to retrive
     *
     * @return True if the sticky fault with the given ID occurred.
     */
    public abstract boolean getStickyFault(FaultID faultID);

    /**
     * @return The voltage fed into the motor controller.
     */
    public abstract double getBusVoltage();

    /**
     * @return The motor controller's applied output duty cycle.
     */
    public abstract double getAppliedOutput();

    /**
     * @return The motor controller's output current in Amps.
     */
    public abstract double getOutputCurrent();

    /**
     * @return The motor temperature in Celsius.
     */
    public abstract double getMotorTemperature();

    /**
     * Clears all sticky faults.
     *
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError clearFaults();

    /**
     * Writes all settings to flash.
     *
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError burnFlash();

    /**
     * Sets timeout for sending CAN messages with SetParameter* and GetParameter*
     * calls. These calls will block for up to this amoutn of time before returning
     * a timeout erro. A timeout of 0 will make the SetParameter* calls
     * non-blocking, and instead will check the response in a separate thread. With
     * this configuration, any error messages will appear on the drivestration but
     * will not be returned by the GetLastError() call.
     *
     * @param milliseconds The timeout in milliseconds.
     *
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError setCANTimeout(int milliseconds);

    /**
     * Enable soft limits
     *
     * @param direction the direction of motion to restrict
     * 
     * @param enable    set true to enable soft limits
     * 
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError enableSoftLimit(SoftLimitDirection direction, boolean enable);

    /**
     * Set the soft limit based on position. The default unit is rotations, but will
     * match the unit scaling set by the user.
     * 
     * Note that this value is not scaled internally so care must be taken to make
     * sure these units match the desired conversion
     *
     * @param direction the direction of motion to restrict
     * 
     * @param limit     position soft limit of the controller
     * 
     * @return REVLibError Set to REVLibError.kOK if successful
     */
    public abstract REVLibError setSoftLimit(SoftLimitDirection direction, float limit);

    /**
     * Get the soft limit setting in the controller
     *
     * @param direction the direction of motion to restrict
     * 
     * @return position soft limit setting of the controller
     */
    public abstract double getSoftLimit(SoftLimitDirection direction);

    /**
     * @param direction The direction of the motion to restrict
     * 
     * @return true if the soft limit is enabled.
     */
    public abstract boolean isSoftLimitEnabled(SoftLimitDirection direction);

    /**
     * All device errors are tracked on a per thread basis for all devices in that
     * thread. This is meant to be called immediately following another call that
     * has the possibility of returning an error to validate if an error has
     * occurred.
     * 
     * @return the last error that was generated.
     */
    public abstract REVLibError getLastError();

    public abstract REVLibError restoreFactoryDefaults();

    ///
    // CAN Encoder Block
    ///
    public abstract double getPosition();

    public abstract double getVelocity();

    public abstract REVLibError setPosition(double position);

    public abstract REVLibError setPositionConversionFactor(double factor);

    public abstract REVLibError setVelocityConversionFactor(double factor);

    public abstract double getPositionConversionFactor();

    public abstract double getVelocityConversionFactor();

    public abstract REVLibError setAverageDepth(int depth);

    public abstract int getAverageDepth();

    public abstract REVLibError setMeasurementPeriod(int period_us);

    public abstract int getMeasurementPeriod();

    public abstract int getCountsPerRevolution();

    public abstract REVLibError setEncoderInverted(boolean inverted);

    ///
    // CAN PID Controller
    ///

    public abstract REVLibError setP(double gain);

    public abstract REVLibError setP(double gain, int slotID);

    public abstract REVLibError setI(double gain);

    public abstract REVLibError setI(double gain, int slotID);

    public abstract REVLibError setD(double gain);

    public abstract REVLibError setD(double gain, int slotID);

    public abstract REVLibError setDFilter(double gain);

    public abstract REVLibError setDFilter(double gain, int slotID);

    public abstract REVLibError setFF(double gain);

    public abstract REVLibError setFF(double gain, int slotID);

    // CHECKSTYLE:OFF
    public abstract REVLibError setIZone(double iZone);

    public abstract REVLibError setIZone(double iZone, int slotID);

    // CHECKSTYLE:ON
    public abstract REVLibError setOutputRange(double min, double max);

    public abstract REVLibError setOutputRange(double min, double max, int slotID);

    public abstract double getP();

    public abstract double getP(int slotID);

    public abstract double getI();

    public abstract double getI(int slotID);

    public abstract double getD();

    public abstract double getD(int slotID);

    public abstract double getDFilter(int slotID);

    public abstract double getFF();

    public abstract double getFF(int slotID);

    public abstract double getIZone();

    public abstract double getIZone(int slotID);

    public abstract double getOutputMin();

    public abstract double getOutputMin(int slotID);

    public abstract double getOutputMax();

    public abstract double getOutputMax(int slotID);

    public abstract REVLibError setSmartMotionMaxVelocity(double maxVel, int slotID);

    public abstract REVLibError setSmartMotionMaxAccel(double maxAccel, int slotID);

    public abstract REVLibError setSmartMotionMinOutputVelocity(double minVel, int slotID);

    public abstract REVLibError setSmartMotionAllowedClosedLoopError(double allowedErr, int slotID);

    public abstract double getSmartMotionMaxVelocity(int slotID);

    public abstract double getSmartMotionMaxAccel(int slotID);

    public abstract double getSmartMotionMinOutputVelocity(int slotID);

    public abstract double getSmartMotionAllowedClosedLoopError(int slotID);

    public abstract REVLibError setIMaxAccum(double iMaxAccum, int slotID);

    public abstract double getIMaxAccum(int slotID);

    public abstract REVLibError setIAccum(double iAccum);

    public abstract double getIAccum();

    public abstract REVLibError setReference(double value, ControlType ctrl);

    public abstract REVLibError setReference(double value, ControlType ctrl, int pidSlot);

    public abstract REVLibError setReference(double value, ControlType ctrl, int pidSlot, double arbFeedforward);

    public abstract REVLibError setReference(double value, ControlType ctrl, int pidSlot, double arbFeedforward,
            ArbFFUnits arbFFUnits);

    /// Get true value. Should not be called in competition code.
    public abstract CANSparkMax getInternalSparkMax();

    public abstract void setForwardLimitSwitch(com.revrobotics.SparkMaxLimitSwitch.Type switchType, boolean enabled);

    public abstract void setReverseLimitSwitch(com.revrobotics.SparkMaxLimitSwitch.Type switchType, boolean enabled);

    public abstract boolean getForwardLimitSwitchPressed(com.revrobotics.SparkMaxLimitSwitch.Type switchType);

    public abstract boolean getReverseLimitSwitchPressed(com.revrobotics.SparkMaxLimitSwitch.Type switchType);
}