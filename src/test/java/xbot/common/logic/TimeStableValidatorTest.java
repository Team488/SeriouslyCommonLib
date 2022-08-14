package xbot.common.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

public class TimeStableValidatorTest extends BaseCommonLibTest {

    TimeStableValidator tsv;

    @Override
    public void setUp() {
        super.setUp();
        tsv = new TimeStableValidator(1);
    }

    @Test
    public void simpleTest() {
        boolean result = tsv.checkStable(true);
        assertFalse(result);

        timer.advanceTimeInSecondsBy(0.5);
        result = tsv.checkStable(true);
        assertFalse(result);

        timer.advanceTimeInSecondsBy(0.6);
        result = tsv.checkStable(true);
        assertTrue(result);
    }

    @Test
    public void negativeWindow() {
        tsv = new TimeStableValidator(-1);

        boolean result = tsv.checkStable(true);
        assertTrue(result);
    }

    @Test
    public void trueFalseTrue() {
        boolean result = tsv.checkStable(true);
        assertFalse(result);

        timer.advanceTimeInSecondsBy(0.5);
        result = tsv.checkStable(false);
        assertFalse(result);

        timer.advanceTimeInSecondsBy(0.6);
        result = tsv.checkStable(true);
        assertFalse(result);

        timer.advanceTimeInSecondsBy(1.1);
        result = tsv.checkStable(true);
        assertTrue(result);
    }

    @Test
    public void stableThenUnstable() {
        boolean result = tsv.checkStable(true);
        assertFalse(result);

        timer.advanceTimeInSecondsBy(1.1);
        result = tsv.checkStable(true);
        assertTrue(result);

        result = tsv.checkStable(false);
        assertFalse(result);

        result = tsv.checkStable(true);
        assertFalse(result);

        timer.advanceTimeInSecondsBy(1.1);
        result = tsv.checkStable(true);
        assertTrue(result);
    }
}