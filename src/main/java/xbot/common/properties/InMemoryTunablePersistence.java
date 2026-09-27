package xbot.common.properties;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Non-persistent storage for debug tunables and tests.
 */
@Singleton
public class InMemoryTunablePersistence implements TunablePersistence {
    private final Map<String, Object> values = new HashMap<>();

    @Inject
    public InMemoryTunablePersistence() {
    }

    @Override
    public Double getDouble(String key) {
        return (Double) values.get(key);
    }

    @Override
    public void setDouble(String key, double value) {
        values.put(key, value);
    }

    @Override
    public Boolean getBoolean(String key) {
        return (Boolean) values.get(key);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        values.put(key, value);
    }

    @Override
    public String getString(String key) {
        return (String) values.get(key);
    }

    @Override
    public void setString(String key, String value) {
        values.put(key, value);
    }

    @Override
    public void remove(String key) {
        values.remove(key);
    }

    public void clear() {
        values.clear();
    }
}
