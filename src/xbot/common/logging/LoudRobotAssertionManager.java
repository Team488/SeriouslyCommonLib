package xbot.common.logging;

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
