package xbot.common.properties;

/**
 * Typed persistence used by managed tunables.
 */
public interface TunablePersistence {
    Double getDouble(String key);

    void setDouble(String key, double value);

    Boolean getBoolean(String key);

    void setBoolean(String key, boolean value);

    String getString(String key);

    void setString(String key, String value);

    void remove(String key);
}
