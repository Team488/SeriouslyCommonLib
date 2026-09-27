package xbot.common.properties;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.wpilib.tunable.Tunable;
import org.wpilib.tunable.TunableBoolean;
import org.wpilib.tunable.TunableDouble;
import org.wpilib.units.Measure;
import org.wpilib.units.Unit;

import xbot.common.logging.Pluralizer;
import xbot.common.logging.RobotAssertionManager;

/**
 * Creates directly consumable WPILib tunables managed by {@link TunableManager}.
 */
public class TunableFactory {
    private static final Logger log = LogManager.getLogger(TunableFactory.class);

    private final TunableManager manager;
    private final RobotAssertionManager assertionManager;
    private String prefix = "";
    private boolean prefixSet;
    private TunableLevel defaultLevel = TunableLevel.Important;

    @Inject
    public TunableFactory(TunableManager manager, RobotAssertionManager assertionManager) {
        this.manager = manager;
        this.assertionManager = assertionManager;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        prefixSet = true;
    }

    public void setPrefix(IPropertySupport prefixSource) {
        setPrefix(prefixSource.getPrefix());
    }

    public void appendPrefix(String toAppend) {
        prefix = prefix + "/" + toAppend;
        prefixSet = true;
    }

    public void setTopLevelPrefix() {
        prefix = "";
        prefixSet = true;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getCleanPrefix() {
        String cleanPrefix;
        if (prefix == null || prefix.isEmpty()) {
            cleanPrefix = "/";
        } else if (prefix.endsWith("/")) {
            cleanPrefix = prefix;
        } else {
            cleanPrefix = prefix + "/";
        }
        return cleanPrefix.replaceAll("/+", "/");
    }

    public void setDefaultLevel(TunableLevel level) {
        defaultLevel = level;
    }

    public TunableDouble createDouble(String suffix, double defaultValue) {
        return createDouble(suffix, defaultValue, defaultLevel);
    }

    public TunableDouble createDouble(
            String suffix,
            double defaultValue,
            TunableLevel level) {
        return manager.createDouble(fullKey(suffix), defaultValue, level);
    }

    public TunableBoolean createBoolean(String suffix, boolean defaultValue) {
        return createBoolean(suffix, defaultValue, defaultLevel);
    }

    public TunableBoolean createBoolean(
            String suffix,
            boolean defaultValue,
            TunableLevel level) {
        return manager.createBoolean(fullKey(suffix), defaultValue, level);
    }

    public Tunable<String> createString(String suffix, String defaultValue) {
        return createString(suffix, defaultValue, defaultLevel);
    }

    public Tunable<String> createString(
            String suffix,
            String defaultValue,
            TunableLevel level) {
        return manager.createString(fullKey(suffix), defaultValue, level);
    }

    public <M extends Measure<U>, U extends Unit> TunableMeasure<M, U> createMeasure(
            String suffix,
            M defaultValue) {
        return createMeasure(suffix, defaultValue, defaultLevel);
    }

    public <M extends Measure<U>, U extends Unit> TunableMeasure<M, U> createMeasure(
            String suffix,
            M defaultValue,
            TunableLevel level) {
        String unitSuffix = "-in-" + Pluralizer.pluralize(defaultValue.unit().name());
        return manager.createMeasure(fullKey(suffix + unitSuffix), defaultValue, level);
    }

    private String fullKey(String suffix) {
        checkPrefixSet();
        return sanitize(getCleanPrefix() + suffix);
    }

    private void checkPrefixSet() {
        if (!prefixSet) {
            assertionManager.fail(
                    "Call setPrefix() on TunableFactory before creating tunables, "
                            + "or call setTopLevelPrefix() for root tunables.");
        }
    }

    private String sanitize(String key) {
        String sanitizedKey = key.replace(",", "").replace("\n", "");
        if (!sanitizedKey.equals(key)) {
            log.warn(
                    "Tunable '{}' contained illegal characters and was sanitized to '{}'",
                    key,
                    sanitizedKey);
        }
        return sanitizedKey;
    }
}
