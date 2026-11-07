package xbot.common.logging.assertion;

import javax.inject.Inject;

/**
 * Assertion handler that allows exceptions to be thrown. Should
 * only be used off-robot or during controlled testing sessions; should never
 * be used in competition.
 */
public class LoudRobotAssertionHandler extends RobotAssertionHandler {

    @Inject
    public LoudRobotAssertionHandler() {}

    @Override
    protected void handlePlatformException(RuntimeException e) {
        throw e;
    }

    @Override
    public boolean isExceptionsEnabled() {
        return true;
    }

}
