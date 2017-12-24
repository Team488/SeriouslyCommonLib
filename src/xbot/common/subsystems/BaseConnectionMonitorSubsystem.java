package xbot.common.subsystems;

import edu.wpi.first.wpilibj.Timer;
import xbot.common.command.BaseSubsystem;
import xbot.common.logic.Latch;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

import java.util.Observable;
import java.util.Observer;

public abstract class BaseConnectionMonitorSubsystem extends BaseSubsystem implements Observer {

    private DoubleProperty timeOut;
    private final Latch connectionLatch = new Latch(true, Latch.EdgeType.FallingEdge);

    private double lastPacketReceivedTimestamp = Timer.getFPGATimestamp();
    private double previousDisconnectionTimestamp = -1;

    public BaseConnectionMonitorSubsystem(XPropertyManager propertyManager) {
        log.info("Creating");
        timeOut = propertyManager.createPersistentProperty("ConnectionMontiorTimeOut", 1.0);
        connectionLatch.addObserver(this);
    }

    public synchronized void setLastPacketReceivedTimestamp(double currentTimestamp) {
        connectionLatch.setValue((currentTimestamp - lastPacketReceivedTimestamp) < timeOut.get());
        this.lastPacketReceivedTimestamp = currentTimestamp;
    }

    public double getPreviousDisconnectionTime() {
        return previousDisconnectionTimestamp;
    }

    @Override
    public void update(Observable o, Object arg) {
        log.warn("The Driver Station has been disconnected for greater than " + timeOut + " Second(s)");
        previousDisconnectionTimestamp = lastPacketReceivedTimestamp;
    }
}
