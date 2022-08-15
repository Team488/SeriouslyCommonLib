package xbot.common.properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.Preferences;

/**
 * @author John
 * 
 * This saves properties to the robot using WPI's built-in Preferences library:
 * http://first.wpi.edu/FRC/roborio/release/docs/java/edu/wpi/first/wpilibj/Preferences.html
 * 
 * This stores them in a simple key-value pair on the robot, in a file called
 * /home/lvuser/networktables.ini
 * 
 */
@Singleton
public class PreferenceStorage implements PermanentStorage {

    protected static Logger log = Logger.getLogger(PreferenceStorage.class);
    boolean fastMode = false;
    
    @Inject
    public PreferenceStorage() {}

    @Override
    public void setDouble(String key, double value) {
        Preferences.setDouble(key, value);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        Preferences.setBoolean(key, value);
    }

    @Override
    public void setString(String key, String value) {
        Preferences.setString(key, value);
    }

    @Override
    public Double getDouble(String key) {
        if (Preferences.containsKey(key))
        {
            return Preferences.getDouble(key, 0);
        }
        return null;
    }

    @Override
    public Boolean getBoolean(String key) {
        if (Preferences.containsKey(key))
        {
            return Preferences.getBoolean(key, false);
        }
        return null;
    }

    @Override
    public String getString(String key) {
        if (Preferences.containsKey(key))
        {
            return Preferences.getString(key, null);
        }
        return null;
    }

    @Override
    public void clear() {
        Preferences.getKeys().clear();
    }

    @Override
    public void setFastMode(boolean on) {
    }

    @Override
    public void remove(String key) {
        if (Preferences.containsKey(key))
        {
            Preferences.remove(key);
        }
    }
}
