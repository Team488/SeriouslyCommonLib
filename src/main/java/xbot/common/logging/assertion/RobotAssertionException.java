package xbot.common.logging.assertion;

//@SuppressWarnings("serial")
public class RobotAssertionException extends RuntimeException {
    public RobotAssertionException(String failureCauseCause) {
        super("Assertion error: " + failureCauseCause);
    }
}
