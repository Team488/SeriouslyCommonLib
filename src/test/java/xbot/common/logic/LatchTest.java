package xbot.common.logic;

import static junit.framework.TestCase.assertEquals;

import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import xbot.common.logic.Latch.EdgeType;

public class LatchTest {

    private LatchTestObserver latchTestObserver;

    @Before
    public void setUp() {
        latchTestObserver = new LatchTestObserver();
    }

    @After
    public void tearDown() {
        latchTestObserver = null;
    }

    @Test
    public void testRisingEdge() {
        Latch latch = new Latch(false, Latch.EdgeType.RisingEdge);
        latch.addObserver(latchTestObserver);
        verifyEdgeType(null);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(1);
    }

    @Test
    public void testFallingEdge() {
        Latch latch = new Latch(true, Latch.EdgeType.FallingEdge);
        latch.addObserver(latchTestObserver);
        verifyEdgeType(null);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
    }

    @Test
    public void testRiseFallRiseObservingRisingEdge() {
        Latch latch = new Latch(false, Latch.EdgeType.RisingEdge);
        latch.addObserver(latchTestObserver);
        verifyEdgeType(null);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(true);
        verifyTimesUpdateWasCalled(2);
    }

    @Test
    public void testFallRiseFallObservingFallingEdge() {
        Latch latch = new Latch(true, Latch.EdgeType.FallingEdge);
        latch.addObserver(latchTestObserver);
        verifyEdgeType(null);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(false);
        verifyTimesUpdateWasCalled(2);
    }

    @Test
    public void testFallRiseRiseFallRiseFallObservingRisingEdge() {
        Latch latch = new Latch(false, Latch.EdgeType.RisingEdge);
        latch.addObserver(latchTestObserver);
        verifyEdgeType(null);
        latch.setValue(false);
        verifyEdgeType(null);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(2);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(2);
    }

    @Test
    public void testFallRiseRiseFallRiseFallObservingFallingEdge() {
        Latch latch = new Latch(true, Latch.EdgeType.FallingEdge);
        latch.addObserver(latchTestObserver);
        verifyEdgeType(null);
        latch.setValue(true);
        verifyEdgeType(null);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(2);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(2);
    }

    @Test
    public void testRiseFallRiseObservingBothEdges() {
        Latch latch = new Latch(true, Latch.EdgeType.Both);
        latch.addObserver(latchTestObserver);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(2);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(3);
    }

    private void verifyTimesUpdateWasCalled(int expected) {
        assertEquals(expected, latchTestObserver.getNumTimesUpdated());
    }

    private void verifyEdgeType(Latch.EdgeType expected) {
        assertEquals(expected, latchTestObserver.getLastUpdateEdgeType());
    }

    class LatchTestObserver implements Consumer<EdgeType> {

        private Latch.EdgeType lastUpdateEdgeType = null;
        private int numTimesUpdated = 0;

        public Latch.EdgeType getLastUpdateEdgeType() {
            return lastUpdateEdgeType;
        }

        public int getNumTimesUpdated() {
            return numTimesUpdated;
        }

        @Override
        public void accept(EdgeType e) {
            numTimesUpdated++;
            lastUpdateEdgeType = e;
        }
    }
}
