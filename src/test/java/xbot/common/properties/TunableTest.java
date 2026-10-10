package xbot.common.properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.wpilib.tunable.MockTunableBackend;
import org.wpilib.tunable.Tunable;
import org.wpilib.tunable.TunableBoolean;
import org.wpilib.tunable.TunableDouble;
import org.wpilib.tunable.TunableRegistry;

import xbot.common.logging.LoudRobotAssertionManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TunableTest {
    private InMemoryTunablePersistence persistence;
    private TunableFactory tunableFactory;
    private MockTunableBackend tunableBackend;

    @BeforeEach
    public void setUp() {
        TunableRegistry.reset();
        tunableBackend = new MockTunableBackend();
        TunableRegistry.registerBackend("", tunableBackend);
        persistence = new InMemoryTunablePersistence();
        TunableManager manager = new TunableManager(
                persistence,
                new InMemoryTunablePersistence(),
                new LoudRobotAssertionManager(),
                false);
        tunableFactory = new TunableFactory(manager, new LoudRobotAssertionManager());
        tunableFactory.setTopLevelPrefix();
    }

    @AfterEach
    public void tearDown() {
        TunableRegistry.reset();
    }

    @Test
    public void defaultsAreReturnedAndPersisted() {
        TunableDouble number = tunableFactory.createDouble("speed", 1.0);
        TunableBoolean bool = tunableFactory.createBoolean("isTrue", true);
        Tunable<String> string = tunableFactory.createString("string", "teststring");

        assertEquals(1.0, number.get(), 0.001);
        assertTrue(bool.get());
        assertEquals("teststring", string.get());
        assertEquals(1.0, persistence.getDouble("/speed"), 0.001);
        assertTrue(persistence.getBoolean("/isTrue"));
        assertEquals("teststring", persistence.getString("/string"));
    }

    @Test
    public void programmaticChangesPersist() {
        TunableDouble number = tunableFactory.createDouble("speed", 1.0);
        TunableBoolean bool = tunableFactory.createBoolean("isTrue", true);
        Tunable<String> string = tunableFactory.createString("string", "teststring");

        number.set(0.5);
        bool.set(false);
        string.set("test2");

        assertEquals(0.5, persistence.getDouble("/speed"), 0.001);
        assertFalse(persistence.getBoolean("/isTrue"));
        assertEquals("test2", persistence.getString("/string"));
    }

    @Test
    public void remoteChangesPersist() {
        TunableDouble number = tunableFactory.createDouble("speed", 1.0);

        tunableBackend.setDouble("/speed", 0.25);
        TunableRegistry.update();

        assertEquals(0.25, number.get(), 0.001);
        assertEquals(0.25, persistence.getDouble("/speed"), 0.001);
    }

    @Test
    public void debugTunablesPublishAndRemoveWithoutBecomingPersistent() {
        TunableDouble debug = tunableFactory.createDouble("debug", 2.0, TunableLevel.Debug);

        assertThrows(IllegalArgumentException.class, () -> tunableBackend.getDouble("/debug"));

        tunableBackend.setBoolean(TunableManager.SHOW_DEBUG_KEY, true);
        TunableRegistry.update();
        assertEquals(2.0, tunableBackend.getDouble("/debug"), 0.001);

        debug.set(3.0);
        assertNull(persistence.getDouble("/debug"));
        tunableBackend.setBoolean(TunableManager.SHOW_DEBUG_KEY, false);
        TunableRegistry.update();
        assertThrows(IllegalArgumentException.class, () -> tunableBackend.getDouble("/debug"));
    }

    @Test
    public void duplicateKeysFailLoudly() {
        tunableFactory.createDouble("duplicate", 1.0);
        assertThrows(
                RuntimeException.class,
                () -> tunableFactory.createDouble("duplicate", 2.0));
    }

    @Test
    public void replayConstructionAndSetsDoNotWritePersistence() {
        InMemoryTunablePersistence replayPersistence = new InMemoryTunablePersistence();
        TunableManager replayManager = new TunableManager(
                replayPersistence,
                new InMemoryTunablePersistence(),
                new LoudRobotAssertionManager(),
                true);
        TunableFactory replayFactory =
                new TunableFactory(replayManager, new LoudRobotAssertionManager());
        replayFactory.setTopLevelPrefix();

        TunableDouble replayValue = replayFactory.createDouble("replay", 4.0);
        replayValue.set(8.0);

        assertEquals(4.0, replayValue.get(), 0.001);
        assertNull(replayPersistence.getDouble("/replay"));
    }
}
