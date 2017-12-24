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

    private double lastPacketReceivedTimeStamp = Timer.getFPGATimestamp();
    private double previousDisconnectionTimeStamp = -1;

    public BaseConnectionMonitorSubsystem(XPropertyManager propertyManager) {
        timeOut = propertyManager.createPersistentProperty("ConnectionMontiorTimeOut", 1.0);
        connectionLatch.addObserver(this);
    }

    public synchronized void setLastPacketReceivedTimeStamp(double currentTimeStamp) {
        connectionLatch.setValue((currentTimeStamp - lastPacketReceivedTimeStamp) < timeOut.get());
        this.lastPacketReceivedTimeStamp = currentTimeStamp;
    }

    public double getPreviousDisconnectionTime() {
        return previousDisconnectionTimeStamp;
    }

    @Override
    public void update(Observable o, Object arg) {
        log.warn("The DriverStation has been disconnected for greater than " + timeOut + " seconds");
        previousDisconnectionTimeStamp = lastPacketReceivedTimeStamp;
    }
}
