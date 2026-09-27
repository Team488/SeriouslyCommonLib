package xbot.common.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.littletonrobotics.junction.Logger;

import org.wpilib.tunable.Tunable;
import org.wpilib.tunable.TunableBoolean;
import org.wpilib.tunable.TunableDouble;
import org.wpilib.units.Measure;
import org.wpilib.units.Unit;

import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.logging.RobotAssertionException;
import xbot.common.logging.RobotAssertionManager;

/**
 * Owns persistence, publication, and AdvantageKit input logging for tunables.
 */
@Singleton
public class TunableManager implements DataFrameRefreshable {
    public static final String IN_MEMORY_PERSISTENCE_NAME = "InMemoryTunablePersistence";
    public static final String SHOW_DEBUG_KEY = "Tunables/ShowDebugTunables";
    public static final String AKIT_LOG_NAMESPACE = "TunableMirror/";

    private static final org.apache.logging.log4j.Logger log =
            LogManager.getLogger(TunableManager.class);

    private final TunablePersistence persistentStorage;
    private final TunablePersistence debugStorage;
    private final RobotAssertionManager assertionManager;
    private final boolean replay;
    private final List<ManagedTunableBinding> bindings = new ArrayList<>();
    private final Map<String, ManagedTunableBinding> bindingsByKey = new HashMap<>();
    private final TunableBoolean showDebugTunables;

    @Inject
    public TunableManager(
            TunablePersistence persistentStorage,
            @Named(IN_MEMORY_PERSISTENCE_NAME) TunablePersistence debugStorage,
            RobotAssertionManager assertionManager) {
        this(persistentStorage, debugStorage, assertionManager, Logger.hasReplaySource());
    }

    TunableManager(
            TunablePersistence persistentStorage,
            TunablePersistence debugStorage,
            RobotAssertionManager assertionManager,
            boolean replay) {
        this.persistentStorage = persistentStorage;
        this.debugStorage = debugStorage;
        this.assertionManager = assertionManager;
        this.replay = replay;

        BooleanTunableBinding showDebugBinding = new BooleanTunableBinding(
                SHOW_DEBUG_KEY,
                false,
                TunableLevel.Important,
                replay,
                persistentStorage,
                this::applyDebugPublication);
        register(showDebugBinding);
        this.showDebugTunables = showDebugBinding.tunable();
    }

    public boolean isReplay() {
        return replay;
    }

    public TunableBoolean getShowDebugTunables() {
        return showDebugTunables;
    }

    TunableDouble createDouble(String key, double defaultValue, TunableLevel level) {
        DoubleTunableBinding binding = new DoubleTunableBinding(
                key,
                defaultValue,
                level,
                replay,
                storageFor(level),
                this::applyDebugPublication);
        register(binding);
        return binding.tunable();
    }

    TunableBoolean createBoolean(String key, boolean defaultValue, TunableLevel level) {
        BooleanTunableBinding binding = new BooleanTunableBinding(
                key,
                defaultValue,
                level,
                replay,
                storageFor(level),
                this::applyDebugPublication);
        register(binding);
        return binding.tunable();
    }

    Tunable<String> createString(String key, String defaultValue, TunableLevel level) {
        StringTunableBinding binding = new StringTunableBinding(
                key,
                defaultValue,
                level,
                replay,
                storageFor(level),
                this::applyDebugPublication);
        register(binding);
        return binding.tunable();
    }

    <M extends Measure<U>, U extends Unit> TunableMeasure<M, U> createMeasure(
            String key,
            M defaultValue,
            TunableLevel level) {
        MeasureTunableBinding<M, U> binding = new MeasureTunableBinding<>(
                key,
                defaultValue,
                level,
                replay,
                storageFor(level),
                this::applyDebugPublication);
        register(binding);
        return binding.tunable();
    }

    private TunablePersistence storageFor(TunableLevel level) {
        return level == TunableLevel.Important ? persistentStorage : debugStorage;
    }

    private void register(ManagedTunableBinding binding) {
        if (bindingsByKey.containsKey(binding.key())) {
            fail("Duplicate tunable key: " + binding.key());
            throw new IllegalArgumentException("Duplicate tunable key: " + binding.key());
        }
        bindingsByKey.put(binding.key(), binding);
        bindings.add(binding);
        if (binding.level() == TunableLevel.Important || showDebugTunables.get()) {
            publish(binding);
        }
    }

    private void publish(ManagedTunableBinding binding) {
        if (!binding.publish()) {
            fail("WPILib rejected duplicate tunable publication for key: " + binding.key());
            throw new IllegalStateException(
                    "WPILib rejected duplicate tunable publication for key: " + binding.key());
        }
    }

    private void fail(String message) {
        log.error(message);
        assertionManager.fail(message);
    }

    private void applyDebugPublication() {
        boolean shouldPublish = !replay && showDebugTunables.get();
        for (ManagedTunableBinding binding : bindings) {
            if (binding.level() != TunableLevel.Debug) {
                continue;
            }
            if (shouldPublish) {
                publishDebug(binding);
            } else {
                binding.remove();
            }
        }
    }

    private void publishDebug(ManagedTunableBinding binding) {
        if (binding.publish()) {
            return;
        }

        String message =
                "WPILib rejected duplicate debug tunable publication for key: " + binding.key();
        log.error(message);
        try {
            assertionManager.fail(message);
        } catch (RobotAssertionException e) {
            log.error("Debug tunable publication failure was reported", e);
        }
    }

    @Override
    public void refreshDataFrame() {
        for (ManagedTunableBinding binding : bindings) {
            binding.refreshDataFrame();
        }
        applyDebugPublication();
    }
}
