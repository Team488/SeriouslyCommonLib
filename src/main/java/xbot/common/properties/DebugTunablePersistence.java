package xbot.common.properties;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Dedicated in-memory persistence for debug tunables.
 */
@Singleton
public class DebugTunablePersistence extends InMemoryTunablePersistence {
    @Inject
    public DebugTunablePersistence() {
        super();
    }
}
