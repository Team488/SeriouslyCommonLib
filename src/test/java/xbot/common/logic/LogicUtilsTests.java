package xbot.common.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogicUtilsTests {

    @Test
    public void testAnyOf() {
        assertFalse(LogicUtils.anyOf());
        assertTrue(LogicUtils.anyOf(true, true));
        assertTrue(LogicUtils.anyOf(true, false));
        assertTrue(LogicUtils.anyOf(false, true));
        assertFalse(LogicUtils.anyOf(false, false));
    }

    @Test
    public void testAllOf() {
        assertTrue(LogicUtils.allOf());
        assertTrue(LogicUtils.allOf(true, true));
        assertFalse(LogicUtils.allOf(true, false));
        assertFalse(LogicUtils.allOf(false, true));
        assertFalse(LogicUtils.allOf(false, false));
    }
}
