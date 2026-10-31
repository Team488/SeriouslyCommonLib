package xbot.common.logging.assertion;

/**
 * Static entry point for robot assertions. Assertions are logged without throwing until a handler is configured.
 * Use a {@link RobotAssertionHandler} instance directly when an object needs its own failure policy.
 */
public final class RobotAssertionManager {
    private static volatile RobotAssertionHandler assertionHandler = new SilentRobotAssertionHandler();

    private RobotAssertionManager() {}

    /**
     * Selects the handler used by all subsequent assertions. Configure before creating robot systems or test fixtures.
     * A null handler is reported through the current handler and leaves the configuration unchanged.
     * @param handler the platform-specific assertion handler
     */
    public static void configure(RobotAssertionHandler handler) {
        if (handler == null) {
            throwException("Assertion handler must not be null");
        } else {
            assertionHandler = handler;
        }
    }

    public static void throwException(RuntimeException exception) {
        assertionHandler.throwException(exception);
    }

    public static void throwException(String message, Throwable cause) {
        assertionHandler.throwException(message, cause);
    }

    public static void throwException(String message) {
        throwException(message, new Exception());
    }

    public static boolean isExceptionsEnabled() {
        return assertionHandler.isExceptionsEnabled();
    }

    public static void fail(String message) {
        assertionHandler.fail(message);
    }

    public static void assertTrue(boolean value, String assertionFailureCause) {
        assertionHandler.assertTrue(value, assertionFailureCause);
    }
}
