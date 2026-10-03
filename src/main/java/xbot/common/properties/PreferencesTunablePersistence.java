package xbot.common.properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.wpilib.preferences.Preferences;

/**
 * Stores important tunables in WPILib Preferences.
 */
@Singleton
public class PreferencesTunablePersistence implements TunablePersistence {

    @Inject
    public PreferencesTunablePersistence() {
    }

    @Override
    public Double getDouble(String key) {
        return Preferences.containsKey(key) ? Preferences.getDouble(key, 0) : null;
    }

    @Override
    public void setDouble(String key, double value) {
        Preferences.setDouble(key, value);
    }

    @Override
    public Boolean getBoolean(String key) {
        return Preferences.containsKey(key) ? Preferences.getBoolean(key, false) : null;
    }

    @Override
    public void setBoolean(String key, boolean value) {
        Preferences.setBoolean(key, value);
    }

    @Override
    public String getString(String key) {
        return Preferences.containsKey(key) ? Preferences.getString(key, null) : null;
    }

    @Override
    public void setString(String key, String value) {
        Preferences.setString(key, value);
    }

    @Override
    public void remove(String key) {
        Preferences.remove(key);
    }
}
