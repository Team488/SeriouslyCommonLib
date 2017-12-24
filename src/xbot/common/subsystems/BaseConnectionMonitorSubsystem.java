package xbot.common.subsystems;

import edu.wpi.first.wpilibj.Timer;
import xbot.common.command.BaseSubsystem;
import xbot.common.logic.Latch;

import java.util.Observable;
import java.util.Observer;

public abstract class BaseConnectionMonitorSubsystem extends BaseSubsystem implements Observer {

    private double timeOut;
    private final Latch connectionLatch = new Latch(true, Latch.EdgeType.FallingEdge);

    private double lastPacketReceivedTimeStamp = Timer.getFPGATimestamp();
    private double disconnectionTime = -1;

    public BaseConnectionMonitorSubsystem() {
        connectionLatch.addObserver(this);
    }

    public void setTimeOut(double timeOut) {
        this.timeOut = timeOut;
    }

    public synchronized void setLastPacketReceivedTimeStamp(double currentTimeStamp) {
        connectionLatch.setValue((currentTimeStamp - lastPacketReceivedTimeStamp) < timeOut);
        this.lastPacketReceivedTimeStamp = currentTimeStamp;
    }

    public double getPreviousDisconnectionTime() {
        return disconnectionTime;
    }

    @Override
    public void update(Observable o, Object arg) {
        log.warn("The DriverStation has been disconnected for greater than " + timeOut + " seconds");
        disconnectionTime = lastPacketReceivedTimeStamp;
    }
}
