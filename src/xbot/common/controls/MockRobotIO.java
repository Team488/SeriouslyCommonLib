package xbot.common.controls;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.controls.actuators.mock_adapters.MockCANTalon;

@Singleton
public class MockRobotIO {

    public static final double BUS_VOLTAGE = 12;
    public static final double NOMINAL_MOTOR_CURRENT = 20;
    
    private static Logger log = Logger.getLogger(MockRobotIO.class);

    Map<Integer, Double> pwms = new HashMap<Integer, Double>();
    Map<Integer, Integer> analogs = new HashMap<Integer, Integer>();
    Map<Integer, Double> analogVoltages = new HashMap<Integer, Double>();
    Map<Integer, Boolean> solenoids = new HashMap<Integer, Boolean>();
    Map<Integer, Boolean> digitalValues = new HashMap<Integer, Boolean>();
    
    /**
     * Index of CAN-based Talons used for follower control lookup. Power
     * is accessible through the PWM interface by using the negated device
     * ID as the PWM channel.
     */
    Map<Integer, MockCANTalon> canTalons = new HashMap<>();

    double gyroHeading;
    double gyroRoll;
    double gyroPitch;
    double gyroHeadingAngularVelocity;

    @Inject
    public MockRobotIO() {

    }

    public double getPWM(int channel) {
        return pwms.getOrDefault(channel, 0.0);
    }

    public void setPWM(int channel, double value) {
        if (!Double.isFinite(value)) {
            log.warn("PWM value was either infinite or NaN! Sanitized to zero.");
            value = 0;
        }

        pwms.put(channel, Math.max(-1, Math.min(1.0, value)));
    }

    // This returns a 12-bit number representing an analog measurement. That's relatively
    // complicated.
    @Deprecated
    public int getAnalog(int channel) {
        return analogs.getOrDefault(channel, 0);
    }

    public void setAnalogVoltage(int channel, double voltage) {
        analogVoltages.put(channel, voltage);
    }

    public double getAnalogVoltage(int channel) {
        return analogVoltages.getOrDefault(channel, 0.0);
    }

    public void setSolenoid(int channel, boolean on) {
        solenoids.put(channel, on);
    }

    public boolean getSolenoid(int channel) {
        return solenoids.getOrDefault(channel, false);
    }

    public void setGyroHeading(double heading) {
        gyroHeading = heading;
    }

    public double getGyroHeading() {
        return gyroHeading;
    }
    
    public void setGyroHeadingAngularVelocity(double angularVelocityInDegrees) {
        gyroHeadingAngularVelocity = angularVelocityInDegrees;
    }
    
    public double getGyroHeadingAngularVelocity() {
        return gyroHeadingAngularVelocity;
    }

    public void setGyroRoll(double roll) {
        gyroRoll = roll;
    }

    public double getGyroRoll() {
        return gyroRoll;
    }

    public void setGyroPitch(double pitch) {
        gyroPitch = pitch;
    }

    public double getGyroPitch() {
        return gyroPitch;
    }

    public boolean getDigital(int channel) {
        return digitalValues.get(channel);
    }

    public void setDigital(int channel, boolean value) {
        digitalValues.put(channel, value);
    }
    
    public MockCANTalon getCANTalon(int deviceId) {
        return canTalons.getOrDefault(deviceId, null);
    }
    
    public void setCANTalon(int deviceId, MockCANTalon talon) {
        canTalons.put(deviceId, talon);
    }
    
}
