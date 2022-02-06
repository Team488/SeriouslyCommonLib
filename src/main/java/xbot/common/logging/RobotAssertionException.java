package xbot.common.logging;

//@SuppressWarnings("serial")
public class RobotAssertionException extends RuntimeException {
    public RobotAssertionException(String failureCauseCause) {
        super("Assertion error: " + failureCauseCause);
    }
}
