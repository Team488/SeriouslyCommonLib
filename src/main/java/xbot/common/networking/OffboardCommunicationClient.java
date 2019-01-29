package xbot.common.networking;

import java.util.function.Consumer;

public interface OffboardCommunicationClient {
    public void start();
    public void setNewPacketHandler(Consumer<String> handler);
}