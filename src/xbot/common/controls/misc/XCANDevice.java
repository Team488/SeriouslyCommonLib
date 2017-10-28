package xbot.common.controls.misc;

public interface XCANDevice {
    void send(byte[] data);
    byte[] receive();
}
