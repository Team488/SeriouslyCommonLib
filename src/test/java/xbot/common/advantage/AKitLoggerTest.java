package xbot.common.advantage;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for AKitLogger. These tests are not comprehensive, but they do test the main functionality of the logger.
 */
public class AKitLoggerTest {
    @Test
    public void testWithLogLevelInvocations() {
        AKitLogger logger = new AKitLogger("TestLogger");

        AtomicInteger infoLogCount = new AtomicInteger();
        AtomicInteger debugLogCount = new AtomicInteger();

        AKitLogger.setGlobalLogLevel(AKitLogger.LogLevel.INFO);
        logger.withLogLevel(AKitLogger.LogLevel.DEBUG, () -> {
            debugLogCount.getAndIncrement();
            fail("This should not be hit because the log level is DEBUG");
        });
        logger.withLogLevel(AKitLogger.LogLevel.INFO, infoLogCount::getAndIncrement);

        AKitLogger.setGlobalLogLevel(AKitLogger.LogLevel.DEBUG);
        logger.withLogLevel(AKitLogger.LogLevel.DEBUG, debugLogCount::getAndIncrement);
        logger.withLogLevel(AKitLogger.LogLevel.INFO, infoLogCount::getAndIncrement);

        assertEquals(1, debugLogCount.get());
        assertEquals(2, infoLogCount.get());
    }

    @Test
    public void testSetLogLevel() {
        AKitLogger logger = new AKitLogger("TestLogger");

        logger.setLogLevel(AKitLogger.LogLevel.DEBUG);
        assertEquals(AKitLogger.LogLevel.DEBUG, logger.getLogLevel());

        logger.setLogLevel(AKitLogger.LogLevel.INFO);
        assertEquals(AKitLogger.LogLevel.INFO, logger.getLogLevel());
    }
}
