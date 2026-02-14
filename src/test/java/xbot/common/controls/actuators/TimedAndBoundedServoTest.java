package xbot.common.controls.actuators;

import edu.wpi.first.wpilibj.MockTimer;
import org.junit.Before;
import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertEquals;

public class TimedAndBoundedServoTest extends BaseCommonLibTest {

    MockTimer mockTimer;

    @Before
    public void setUp() {
        super.setUp();
        mockTimer = (MockTimer)getInjectorComponent().timerImplementation();
    }

    @Test
    public void testAbsolutePosition() {
        var factory = getInjectorComponent().servoFactory();
        var servo = factory.create(999, "test");
        servo.set(0.3);

        var timedServo = new TimedAndBoundedServo(
                servo,
                0.2,
                0.8,
                3
        );
        mockTimer.setTimeInSeconds(0);

        timedServo.setAbsoluteTargetPosition(0.8);
        mockTimer.advanceTimeInSecondsBy(1);
        assertEquals(0.5, timedServo.getAbsoluteCurrentPosition(), 0.001);
    }

    @Test
    public void testAbsoluteAndNormalized() {
        var factory = getInjectorComponent().servoFactory();
        var servo = factory.create(999, "test");
        servo.set(0.2);

        var timedServo = new TimedAndBoundedServo(
                servo,
                0.2,
                0.8,
                3
        );
        mockTimer.setTimeInSeconds(0);

        timedServo.setNormalizedTargetPosition(1.0);
        mockTimer.advanceTimeInSecondsBy(15);
        assertEquals(0.8, timedServo.getAbsoluteCurrentPosition(), 0.001);

        timedServo.setNormalizedTargetPosition(0.5);
        mockTimer.advanceTimeInSecondsBy(1.5);
        assertEquals(0.5, timedServo.getNormalizedCurrentPosition(), 0.001);

        timedServo.setAbsoluteTargetPosition(0.3);
        mockTimer.advanceTimeInSecondsBy(0.5);
        assertEquals(0.4, timedServo.getAbsoluteCurrentPosition(), 0.001);
    }
}
