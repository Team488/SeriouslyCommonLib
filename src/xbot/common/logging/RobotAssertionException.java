package xbot.common.logging;

public class RobotAssertionException extends RuntimeException {
    public RobotAssertionException(String failureCauseCause) {
        super("Assertion error: " + failureCauseCause);
    }
}
