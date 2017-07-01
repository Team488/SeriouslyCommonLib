package xbot.common.properties;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.Preferences;

public class PreferenceStorage implements PermanentStorage {

    protected static Logger log;
    
    @Override
    public void setDouble(String key, double value) {
        Preferences.getInstance().putDouble(key, value);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        Preferences.getInstance().putBoolean(key, value);
    }

    @Override
    public void setString(String key, String value) {
        Preferences.getInstance().putString(key, value);
    }

    @Override
    public Double getDouble(String key) {
        if (Preferences.getInstance().containsKey(key))
        {
            return Preferences.getInstance().getDouble(key, 0);
        }
        return null;
    }

    @Override
    public Boolean getBoolean(String key) {
        if (Preferences.getInstance().containsKey(key))
        {
            return Preferences.getInstance().getBoolean(key, false);
        }
        return null;
    }

    @Override
    public String getString(String key) {
        if (Preferences.getInstance().containsKey(key))
        {
            return Preferences.getInstance().getString(key, null);
        }
        return null;
    }

    @Override
    public void clear() {
        // Can't figure out how to clear it - could not figure out how to make Java happy
        // with the untyped Vector output from the library
    }
}
