package xbot.common.logic;

import xbot.common.controls.sensors.XTimer;
import xbot.common.logic.Latch.EdgeType;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public class WatchdogTimer {
    private double lastKick = Double.NEGATIVE_INFINITY;
    private Latch latch;
    
    private BooleanProperty isUpProp;
    private DoubleProperty timeSinceKickProp;
    
    private final double timeout;
    private Runnable onUp = null;
    private Runnable onDown = null;
    private XTimer timer;
    
    private WatchdogTimer(double timeout, XTimer timer) {
        this.timer = timer;
        this.timeout = timeout;
        latch = new Latch(false, EdgeType.Both, this::handleLatchUpdate);
    }
    
    public WatchdogTimer(double timeout, String name, XPropertyManager propMan, XTimer timer) {
        this(timeout, timer);
        this.isUpProp = propMan.createEphemeralProperty(name + "/Is up?", false);
        this.timeSinceKickProp = propMan.createEphemeralProperty(name + "/Time since kick", Double.POSITIVE_INFINITY);
    }

    public WatchdogTimer(double timeout, Runnable onUp, Runnable onDown, XTimer timer) {
        this(timeout, timer);
        this.onUp = onUp;
        this.onDown = onDown;
    }
    
    public WatchdogTimer(double timeout, Runnable onUp, Runnable onDown, String name, XPropertyManager propMan, XTimer timer) {
        this(timeout, name, propMan, timer);
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
        lastKick = timer.getFPGATimestamp();
    }
    
    public void check() {
        double now = timer.getFPGATimestamp();
        double timeSinceKick = now - lastKick;
        boolean isUp = Double.isFinite(timeSinceKick) && timeSinceKick <= timeout;
        
        latch.setValue(isUp);
        if (this.isUpProp != null && this.timeSinceKickProp != null) {
            isUpProp.set(isUp);
            timeSinceKickProp.set(timeSinceKick);
        }
    }
}
