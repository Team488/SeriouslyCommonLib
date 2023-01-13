/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Alex
 */
@Singleton
public class SmartDashboardTableWrapper implements ITableProxy {

    private static final Logger log = Logger.getLogger(SmartDashboardTableWrapper.class);
    boolean fastMode = false;
    private TableProxy fastTable;

    @Inject
    public SmartDashboardTableWrapper() {
        fastTable = new TableProxy();
    }

    public void setDouble(String key, double value) {
        fastTable.setDouble(key, value);
        if (!fastMode) {
            SmartDashboard.putNumber(key, value);
        }
    }

    public void clear() {
        // Do not clear the smart dashboard - the SmartDashboard doesn't really have a mechanism
        // to clear it, so instead we'll just put an angry warning message!
        log.warn("Somebody attempted to clear the SmartDashboard. This will never work - why are you doing this?!");
    }

    public Double getDouble(String key) {
        if (!fastMode) {
            if (SmartDashboard.containsKey(key)) {
                return Double.valueOf(SmartDashboard.getNumber(key, 0));
            } else {
                return null;
            }
        }
        return fastTable.getDouble(key);
    }

    public void setBoolean(String key, boolean value) {
        fastTable.setBoolean(key, value);
        if (!fastMode) {
            SmartDashboard.putBoolean(key, value);
        }
    }

    public Boolean getBoolean(String key) {
        if (!fastMode) {
            if (SmartDashboard.containsKey(key)) {
                return Boolean.valueOf(SmartDashboard.getBoolean(key, false));
            } else {
                return null;
            }
        }
        return fastTable.getBoolean(key);
    }

    public void setString(String key, String value) {
        fastTable.setString(key, value);
        if (!fastMode) {
            SmartDashboard.putString(key, value);
        }
    }

    public String getString(String key) {
        if (!fastMode) {
            if (SmartDashboard.containsKey(key)) {
                return SmartDashboard.getString(key, "");
            } else {
                return null;
            }
        }
        return fastTable.getString(key);
    }

    @Override
    public void setFastMode(boolean on) {
        fastMode = on;
    }

    public void remove(String key) {
        if (SmartDashboard.containsKey(key)) {
            SmartDashboard.getEntry(key).unpublish();
        }
    }
}