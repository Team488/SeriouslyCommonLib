package xbot.common.injection;

public abstract class ElectricalContract {

    public class DeviceInfo {
        public int channel;
        public boolean inverted;

        public DeviceInfo(int channel, boolean inverted) {
            this.channel = channel;
            this.inverted = inverted;
        }
    }
}