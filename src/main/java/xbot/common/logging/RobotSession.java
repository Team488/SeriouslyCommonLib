package xbot.common.logging;

import javax.inject.Inject;
import javax.inject.Singleton;

import xbot.common.properties.PropertyFactory;
import xbot.common.properties.StringProperty;

@Singleton
public class RobotSession {

    WordGenerator wg;
    boolean hasStartedAuto;
    boolean hasStartedTeleop;
    String sessionId = "";
    StringProperty sessionProp;
    String propertyName = "RobotSession";

    @Inject
    public RobotSession(WordGenerator wg, PropertyFactory propMan) {
        this.wg = wg;
        propMan.setTopLevelPrefix();
        sessionProp = propMan.createEphemeralProperty(propertyName, "NoSessionSetYet");
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
        sessionProp.set(sessionId);
    }

    public String getSessionId() {
        return sessionId;
    }
}