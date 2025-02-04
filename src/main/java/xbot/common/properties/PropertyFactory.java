package xbot.common.properties;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.units.measure.Distance;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.Property.PropertyLevel;
import xbot.common.properties.Property.PropertyPersistenceType;

/**
 * A factory for creating properties. This is the preferred way to create properties.
 */
public class PropertyFactory {

    protected Logger log;
    private final XPropertyManager propertyManager;
    private String prefix = "";
    private final RobotAssertionManager assertionManager;
    private boolean prefixSet;
    private PropertyLevel defaultLevel = PropertyLevel.Important;

    @Inject
    PropertyFactory(XPropertyManager propertyManager, RobotAssertionManager assertionManager) {
        this.propertyManager = propertyManager;
        this.assertionManager = assertionManager;
        log = LogManager.getLogger(PropertyFactory.class);
    }

    /**
     * Sets the prefix for all properties created by this factory.
     * This or another prefix setting method must be called before any properties are created.
     * @param prefix The prefix to use for all properties created by this factory.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
        prefixSet = true;
    }

    /**
     * Sets the prefix for all properties created by this factory.
     * This or another prefix setting method must be called before any properties are created.
     * @param prefixSource The prefix source to use for the prefix.
     */
    public void setPrefix(IPropertySupport prefixSource) {
        this.prefix = prefixSource.getPrefix();
        prefixSet = true;
    }

    /**
     * Appends a prefix to the current prefix for all properties created by this factory.
     * This or another prefix setting method must be called before any properties are created.
     * @param toAppend The prefix to append to the current prefix.
     */
    public void appendPrefix(String toAppend) {
        prefix = prefix + "/" + toAppend;
        prefixSet = true;
    }

    /**
     * Sets the prefix for all properties created by this factory to the top level.
     * This or another prefix setting method must be called before any properties are created.
     */
    public void setTopLevelPrefix() {
        prefix = "";
        prefixSet = true;
    }

    /**
     * Gets the prefix for all properties created by this factory.
     * @return The prefix for all properties created by this factory.
     */
    public String getPrefix() {
        return this.prefix;
    }

    public String getCleanPrefix() {
        String cleanPrefix = null;
        if(this.prefix == null || this.prefix.isEmpty()) {
            cleanPrefix = "/";
        }
        else if (prefix.charAt(prefix.length() -1) == '/')
        {
            // If somebody already put a slash as a trailing character, then we don't have much to do.
            cleanPrefix = prefix;
        } else {
            cleanPrefix = this.getPrefix() + "/";
        }

        cleanPrefix = cleanPrefix.replaceAll("/+", "/");
        return cleanPrefix;
    }

    private void checkPrefixSet() {
        if (!prefixSet) {
            assertionManager.fail("You should always call setPrefix() on PropertyFactory before creating new properties; "
            + "otherwise, all properties will be at the root level."
            + "If you meant to have the property at the root level, call setTopLevelPrefix() first."
            );
        }
    }

    /**
     * Sets the default property level.
     * @param level The property level.
     */
    public void setDefaultLevel(PropertyLevel level) {
        this.defaultLevel = level;
    }

    /**
     * Method for creating a double persistent property.
     * @param suffix The suffix for the property.
     * @param defaultValue The default value for the property.
     * @return The property.
     */
    public BooleanProperty createPersistentProperty(String suffix, boolean defaultValue) {
        checkPrefixSet();
        return new BooleanProperty(getCleanPrefix(), suffix, defaultValue, this.propertyManager, defaultLevel);
    }

    /**
     * Method for creating a double persistent property.
     * @param suffix The suffix for the property.
     * @param defaultValue The default value for the property.
     * @param level The property level.
     * @return The property.
     */
    public BooleanProperty createPersistentProperty(String suffix, boolean defaultValue, PropertyLevel level) {
        checkPrefixSet();
        return new BooleanProperty(getCleanPrefix(), suffix, defaultValue, this.propertyManager, level);
    }

    /**
     * Method for creating a double persistent property.
     * @param suffix The suffix for the property.
     * @param defaultValue The default value for the property.
     * @return The property.
     */
    public StringProperty createPersistentProperty(String suffix, String defaultValue) {
        checkPrefixSet();
        return new StringProperty(getCleanPrefix(), suffix, defaultValue, this.propertyManager, defaultLevel);
    }

    /**
     * Method for creating a double persistent property.
     * @param suffix The suffix for the property.
     * @param defaultValue The default value for the property.
     * @param level The property level.
     * @return The property.
     */
    public StringProperty createPersistentProperty(String suffix, String defaultValue, PropertyLevel level) {
        checkPrefixSet();
        return new StringProperty(getCleanPrefix(), suffix, defaultValue, this.propertyManager, level);
    }

    /**
     * Method for creating a double persistent property.
     * @param suffix The suffix for the property.
     * @param defaultValue The default value for the property.
     * @return The property.
     */
    public DoubleProperty createPersistentProperty(String suffix, double defaultValue) {
        checkPrefixSet();
        return new DoubleProperty(getCleanPrefix(), suffix, defaultValue, this.propertyManager, defaultLevel);
    }

    /**
     * Method for creating a double persistent property.
     * @param suffix The suffix for the property.
     * @param defaultValue The default value for the property.
     * @param level The property level.
     * @return The property.
     */
    public DoubleProperty createPersistentProperty(String suffix, double defaultValue, PropertyLevel level) {
        checkPrefixSet();
        return new DoubleProperty(getCleanPrefix(), suffix, defaultValue, this.propertyManager, level);
    }

    public DistanceProperty createPersistentProperty(String suffix, Distance defaultValue) {
        return this.createPersistentProperty(suffix, defaultValue, defaultLevel);
    }

    public DistanceProperty createPersistentProperty(String suffix, Distance defaultValue, PropertyLevel level) {
        checkPrefixSet();
        return new DistanceProperty(getCleanPrefix(), suffix, defaultValue, this.propertyManager, level);
    }

}