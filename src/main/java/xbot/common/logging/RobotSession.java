package xbot.common.logging;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.logging.WordGenerator;

@Singleton
public class RobotSession {

    WordGenerator wg;
    boolean hasStartedAuto;
    boolean hasStartedTeleop;
    String sessionId = "";

    @Inject
    public RobotSession(WordGenerator wg) {
        this.wg = wg;
        reset();
    }

    public void autoInit() {
        // Whenever autonomous mode starts, create a new ID, and reset. Then, mark that we have started autonomous.
        reset();
        hasStartedAuto = true;
    }

    public void teleopInit() {
        if (hasStartedTeleop) {
            // We've already been in teleop before. Reset.
            reset();
        }
        else if (hasStartedAuto && !hasStartedTeleop) {
            // We were in auto, and this is the first teleop session. Continue using existing ID.
            
        }
        else if (!hasStartedAuto && !hasStartedTeleop) {
            // Starting fresh from teleop (typical for local testing). Continue using the existing ID.
        }

        hasStartedTeleop = true;
    }

    public void reset() {
        hasStartedAuto = false;
        hasStartedTeleop = false;
        sessionId = wg.getRandomWordChain(2, "-");
    }

    public String getSessionId() {
        return sessionId;
    }
}