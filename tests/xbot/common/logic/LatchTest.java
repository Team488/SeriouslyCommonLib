package xbot.common.logic;

import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

import static junit.framework.TestCase.assertTrue;

public class LatchTest {

    @Test
    public void testRisingEdge() {
        Latch latch = new Latch(false, Latch.EdgeType.RisingEdge);
        MockLatch mockLatch = new MockLatch();
        latch.addObserver(mockLatch);
        latch.setValue(true);
        assertTrue(mockLatch.getEdgeType() == Latch.EdgeType.RisingEdge);
    }

    @Test
    public void testFallingEdge() {
        Latch latch = new Latch(true, Latch.EdgeType.FallingEdge);
        MockLatch mockLatch = new MockLatch();
        latch.addObserver(mockLatch);
        latch.setValue(false);
        assertTrue(mockLatch.getEdgeType() == Latch.EdgeType.FallingEdge);
    }

    @Test
    public void testRiseFallRiseObservingRisingEdge() {
        Latch latch = new Latch(false, Latch.EdgeType.RisingEdge);
        MockLatch mockLatch = new MockLatch();
        latch.addObserver(mockLatch);
        assertTrue(mockLatch.getEdgeType() == null);
        latch.setValue(true);
        assertTrue(mockLatch.getEdgeType() == Latch.EdgeType.RisingEdge);
        latch.setValue(false);
        assertTrue(mockLatch.getEdgeType() == Latch.EdgeType.RisingEdge);
        latch.setValue(true);
        assertTrue(mockLatch.getNumTimesUpdated() == 2);
    }

    @Test
    public void testFallRiseFallObservingFallingEdge() {
        Latch latch = new Latch(true, Latch.EdgeType.FallingEdge);
        MockLatch mockLatch = new MockLatch();
        latch.addObserver(mockLatch);
        latch.setValue(false);
        assertTrue(mockLatch.getEdgeType() == Latch.EdgeType.FallingEdge);
        latch.setValue(true);
        assertTrue(mockLatch.getEdgeType() == Latch.EdgeType.FallingEdge);
        latch.setValue(false);
        assertTrue(mockLatch.getNumTimesUpdated() == 2);
    }
}

class MockLatch implements Observer {

    private Latch.EdgeType edgeType = null;
    private int numTimesUpdated = 0;

    public Latch.EdgeType getEdgeType() {
        return edgeType;
    }

    public int getNumTimesUpdated() {
        return numTimesUpdated;
    }

    @Override
    public void update(Observable o, Object arg) {
        numTimesUpdated++;
        edgeType = (Latch.EdgeType) arg;
    }
}
