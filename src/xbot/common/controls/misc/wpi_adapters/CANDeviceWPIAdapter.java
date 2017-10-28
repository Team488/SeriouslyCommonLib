package xbot.common.controls.misc.wpi_adapters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;

import xbot.common.controls.misc.XCANDevice;
import edu.wpi.first.wpilibj.can.CANInvalidBufferException;
import edu.wpi.first.wpilibj.can.CANJNI;
import edu.wpi.first.wpilibj.can.CANMessageNotAllowedException;
import edu.wpi.first.wpilibj.can.CANMessageNotFoundException;
import edu.wpi.first.wpilibj.can.CANNotInitializedException;

public class CANDeviceWPIAdapter implements XCANDevice {
    static Logger log = Logger.getLogger(CANDeviceWPIAdapter.class);
    
    private final int inboundArbitrationId;
    private final int outboundArbitrationId;
    
    public CANDeviceWPIAdapter(int inboundArbitrationId, int outboundArbitrationId) {
        this.inboundArbitrationId = inboundArbitrationId;
        this.outboundArbitrationId = outboundArbitrationId;
    }
    
    @Override
    public void send(byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        CANJNI.FRCNetCommCANSessionMuxSendMessage(this.outboundArbitrationId, buffer, CANJNI.CAN_SEND_PERIOD_NO_REPEAT);
    }

    @Override
    public byte[] receive() {
        ByteBuffer targetMessageIdBuffer = ByteBuffer.allocateDirect(4);
        targetMessageIdBuffer.order(ByteOrder.LITTLE_ENDIAN);
        targetMessageIdBuffer.asIntBuffer().put(this.inboundArbitrationId);
        
        // A buffer is required, but we don't care about the timestamp value.
        ByteBuffer timeStamp = ByteBuffer.allocateDirect(4);
        
        try {
            ByteBuffer resultBuf = CANJNI.FRCNetCommCANSessionMuxReceiveMessage(targetMessageIdBuffer.asIntBuffer(), CANJNI.CAN_IS_FRAME_REMOTE, timeStamp);
            
            byte[] resultBytes = new byte[resultBuf.remaining()];
            resultBuf.get(resultBytes);
            
            return resultBytes;
        }
        catch (CANMessageNotFoundException e) {
            return null;
        }
        catch(CANInvalidBufferException|CANMessageNotAllowedException|CANNotInitializedException ex) {
            log.error("Exception encountered while receiving from CAN device (inbound arbid " + this.inboundArbitrationId + ")", ex);
            return null;
        }
    }
}
