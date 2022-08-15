package xbot.common.networking;

import java.util.function.Consumer;

public interface XZeromqListener {

    public interface XZeromqListenerFactory {
        XZeromqListener create(String connectionString, String topic);
    }

    public void start();
    public void setNewPacketHandler(Consumer<String> handler);
}