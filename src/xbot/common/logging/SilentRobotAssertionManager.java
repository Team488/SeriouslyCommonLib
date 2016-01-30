package xbot.common.logging;

public class SilentRobotAssertionManager extends RobotAssertionManager {

    @Override
    protected void handlePlatformException(RuntimeException e) {
        // Don't do anything: we don't need to throw
    }

    @Override
    public boolean isExceptionsEnabled() {
        return false;
    }

}
