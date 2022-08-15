package xbot.common.logging;

import javax.inject.Inject;

/**
 * Version of RobotAssertionManager that prevents throwing of exceptions. Should
 * be used on-robot in competitions.
 */
public class SilentRobotAssertionManager extends RobotAssertionManager {

    @Inject
    public SilentRobotAssertionManager() {}

    @Override
    protected void handlePlatformException(RuntimeException e) {
        // Don't do anything: we don't need to throw
    }

    @Override
    public boolean isExceptionsEnabled() {
        return false;
    }

}
