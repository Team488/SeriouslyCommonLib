package xbot.common.logging;

public class SafeRobotAssertionException extends RuntimeException {
    public SafeRobotAssertionException(String faliureCauseCause) {
        super("Assertion error: " + faliureCauseCause);
    }
}
