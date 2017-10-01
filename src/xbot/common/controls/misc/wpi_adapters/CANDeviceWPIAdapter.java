package xbot.common.controls.misc.wpi_adapters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import xbot.common.controls.misc.XCANDevice;
import edu.wpi.first.wpilibj.can.CANJNI;

public class CANDeviceWPIAdapter implements XCANDevice {

    private final int arbitrationId;
    public CANDeviceWPIAdapter(int arbitrationId) {
        this.arbitrationId = arbitrationId;
    }
    
    @Override
    public void send(byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        CANJNI.FRCNetCommCANSessionMuxSendMessage(this.arbitrationId, buffer, CANJNI.CAN_SEND_PERIOD_NO_REPEAT);
    }

    @Override
    public byte[] receive() {
        ByteBuffer targetMessageIdBuffer = ByteBuffer.allocateDirect(4);
        targetMessageIdBuffer.order(ByteOrder.LITTLE_ENDIAN);
        targetMessageIdBuffer.asIntBuffer().put(this.arbitrationId);
        
        // A buffer is required, but we don't care about the timestamp value.
        ByteBuffer timeStamp = ByteBuffer.allocateDirect(4);
        
        ByteBuffer resultBuf = CANJNI.FRCNetCommCANSessionMuxReceiveMessage(targetMessageIdBuffer.asIntBuffer(), CANJNI.CAN_IS_FRAME_REMOTE, timeStamp);
        
        if(resultBuf == null) {
            return null;
        }
        
        byte[] resultBytes = new byte[resultBuf.remaining()];
        resultBuf.get(resultBytes);
        
        return resultBytes;
    }

}
