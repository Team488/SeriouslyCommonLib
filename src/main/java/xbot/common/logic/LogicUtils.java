package xbot.common.logic;

/**
 * Utility methods for working with logic.
 */
public final class LogicUtils {
    /**
     * Returns true if any of the given values are true.
     * @apiNote This is helpful if Checkstyle complains about boolean expression complexity.
     * @param values The values to check.
     *               If no values are given, this method will return false.
     * @return True if any of the given values are true.
     */
    public static boolean anyOf(boolean... values) {
        for (boolean value : values) {
            if (value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if all the given values are true.
     * @apiNote This is helpful if Checkstyle complains about boolean expression complexity.
     * @param values The values to check.
     *               If no values are given, this method will return true.
     * @return True if all the given values are true.
     */
    public static boolean allOf(boolean... values) {
        for (boolean value : values) {
            if (!value) {
                return false;
            }
        }
        return true;
    }
}
