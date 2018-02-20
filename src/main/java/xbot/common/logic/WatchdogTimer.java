package xbot.common.logic;

import edu.wpi.first.wpilibj.Timer;
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
    
    private WatchdogTimer(double timeout) {
        this.timeout = timeout;
        latch = new Latch(false, EdgeType.Both, this::handleLatchUpdate);
    }
    
    public WatchdogTimer(double timeout, String name, XPropertyManager propMan) {
        this(timeout);
        this.isUpProp = propMan.createEphemeralProperty(name + "/Is up?", false);
        this.timeSinceKickProp = propMan.createEphemeralProperty(name + "/Time since kick", Double.POSITIVE_INFINITY);
    }

    public WatchdogTimer(double timeout, Runnable onUp, Runnable onDown) {
        this(timeout);
        this.onUp = onUp;
        this.onDown = onDown;
    }
    
    public WatchdogTimer(double timeout, Runnable onUp, Runnable onDown, String name, XPropertyManager propMan) {
        this(timeout, name, propMan);
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
        lastKick = Timer.getFPGATimestamp();
    }
    
    public void check() {
        double now = Timer.getFPGATimestamp();
        double timeSinceKick = now - lastKick;
        boolean isUp = Double.isFinite(timeSinceKick) && timeSinceKick <= timeout;
        
        latch.setValue(isUp);
        if (this.isUpProp != null && this.timeSinceKickProp != null) {
            isUpProp.set(isUp);
            timeSinceKickProp.set(timeSinceKick);
        }
    }
}
