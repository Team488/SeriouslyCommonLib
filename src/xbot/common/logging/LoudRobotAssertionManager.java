package xbot.common.logging;

/**
 * Version of RobotAssertionManager that allows exceptions to be thrown. Should
 * only be used off-robot or during controlled testing sessions; should never
 * be used in competition.
 */
public class LoudRobotAssertionManager extends RobotAssertionManager {

    @Override
    protected void handlePlatformException(RuntimeException e) {
        throw e;
    }

    @Override
    public boolean isExceptionsEnabled() {
        return true;
    }

}
