package xbot.common.logic;

import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

import static junit.framework.TestCase.assertEquals;

public class LatchTest {

    @Test
    public void testRisingEdge() {
        Latch latch = new Latch(false, Latch.EdgeType.RisingEdge);
        LatchTestObserver latchTestObserver = new LatchTestObserver();
        latch.addObserver(latchTestObserver);
        assertEquals(latchTestObserver.getLastUpdateEdgeType(), null);
        latch.setValue(true);
        assertEquals(latchTestObserver.getLastUpdateEdgeType(), Latch.EdgeType.RisingEdge);
    }

    @Test
    public void testFallingEdge() {
        Latch latch = new Latch(true, Latch.EdgeType.FallingEdge);
        LatchTestObserver latchTestObserver = new LatchTestObserver();
        latch.addObserver(latchTestObserver);
        latch.setValue(false);
        assertEquals(latchTestObserver.getLastUpdateEdgeType(), Latch.EdgeType.FallingEdge);
    }

    @Test
    public void testRiseFallRiseObservingRisingEdge() {
        Latch latch = new Latch(false, Latch.EdgeType.RisingEdge);
        LatchTestObserver latchTestObserver = new LatchTestObserver();
        latch.addObserver(latchTestObserver);
        assertEquals(latchTestObserver.getLastUpdateEdgeType(), null);
        latch.setValue(true);
        assertEquals(latchTestObserver.getLastUpdateEdgeType(), Latch.EdgeType.RisingEdge);
        latch.setValue(false);
        assertEquals(latchTestObserver.getLastUpdateEdgeType(), Latch.EdgeType.RisingEdge);
        latch.setValue(true);
        assertEquals(latchTestObserver.getNumTimesUpdated(), 2);
    }

    @Test
    public void testFallRiseFallObservingFallingEdge() {
        Latch latch = new Latch(true, Latch.EdgeType.FallingEdge);
        LatchTestObserver latchTestObserver = new LatchTestObserver();
        latch.addObserver(latchTestObserver);
        latch.setValue(false);
        assertEquals(latchTestObserver.getLastUpdateEdgeType(), Latch.EdgeType.FallingEdge);
        latch.setValue(true);
        assertEquals(latchTestObserver.getLastUpdateEdgeType(), Latch.EdgeType.FallingEdge);
        latch.setValue(false);
        assertEquals(latchTestObserver.getNumTimesUpdated(), 2);
    }
}

class LatchTestObserver implements Observer {

    private Latch.EdgeType lastUpdateEdgeType = null;
    private int numTimesUpdated = 0;

    public Latch.EdgeType getLastUpdateEdgeType() {
        return lastUpdateEdgeType;
    }

    public int getNumTimesUpdated() {
        return numTimesUpdated;
    }

    @Override
    public void update(Observable o, Object arg) {
        numTimesUpdated++;
        lastUpdateEdgeType = (Latch.EdgeType) arg;
    }
}
