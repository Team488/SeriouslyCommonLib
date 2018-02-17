/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Alex
 */
public class SmartDashboardTableWrapper implements ITableProxy {

    private static final Logger log = Logger.getLogger(SmartDashboardTableWrapper.class);
    boolean fastMode = false;

    public void setDouble(String key, double value) {
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
        if (SmartDashboard.getKeys().contains(key)) {
            return Double.valueOf(SmartDashboard.getNumber(key, 0));
        } else  {
            return null;
        }
    }

    public void setBoolean(String key, boolean value) {
        if (!fastMode) {
            SmartDashboard.putBoolean(key, value);
        }
    }

    public Boolean getBoolean(String key) {
        if (SmartDashboard.getKeys().contains(key)) {
            return Boolean.valueOf(SmartDashboard.getBoolean(key, false));
        } else  {
            return null;
        }
    }

    public void setString(String key, String value) {
        if (!fastMode) {
            SmartDashboard.putString(key, value);
        }
    }

    public String getString(String key) {
        if (SmartDashboard.getKeys().contains(key)) {
            return SmartDashboard.getString(key, "");
        } else  {
            return null;
        }
    }

    @Override
    public void setFastMode(boolean on) {
        fastMode = on;
    }

}