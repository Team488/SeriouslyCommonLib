package xbot.common.logic;

import xbot.common.controls.sensors.XTimer;
import xbot.common.logic.Latch.EdgeType;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public class WatchdogTimer {
    private double lastKick = Double.NEGATIVE_INFINITY;
    private Latch latch;
    
    private boolean isUp;
    private double timeSinceKick;
    
    private final double timeout;
    private Runnable onUp = null;
    private Runnable onDown = null;
    
    private WatchdogTimer(double timeout) {
        this.timeout = timeout;
        latch = new Latch(false, EdgeType.Both, this::handleLatchUpdate);
    }

    public WatchdogTimer(double timeout, Runnable onUp, Runnable onDown) {
        this(timeout);
        this.onUp = onUp;
        this.onDown = onDown;
    }
    
    private void handleLatchUpdate(EdgeType edge) {
        if (edge == EdgeType.RisingEdge && onUp != null) {
            onUp.run();
        }
        else if (edge == EdgeType.FallingEdge && onDown != null) {
            onDown.run();
        }
    }
    
    public void kick() {
        lastKick = XTimer.getFPGATimestamp();
    }
    
    public void check() {
        double now = XTimer.getFPGATimestamp();
        timeSinceKick = now - lastKick;
        isUp = Double.isFinite(timeSinceKick) && timeSinceKick <= timeout;
        latch.setValue(isUp);
    }
}
