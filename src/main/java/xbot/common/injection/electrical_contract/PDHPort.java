package xbot.common.injection.electrical_contract;

/**
 * Represents the available ports on a Power Distribution Hub (PDH).
 * REV PDH has 20 ports (0-19), CTRE PDH has 24 ports (0-23).
 * Using the common 20-port standard here.
 */
public enum PDHPort {
    PDH00(0),
    PDH01(1),
    PDH02(2),
    PDH03(3),
    PDH04(4),
    PDH05(5),
    PDH06(6),
    PDH07(7),
    PDH08(8),
    PDH09(9),
    PDH10(10),
    PDH11(11),
    PDH12(12),
    PDH13(13),
    PDH14(14),
    PDH15(15),
    PDH16(16),
    PDH17(17),
    PDH18(18),
    PDH19(19);

    private final int portNumber;

    PDHPort(int portNumber) {
        this.portNumber = portNumber;
    }

    public int getPortNumber() {
        return portNumber;
    }

    @Override
    public String toString() {
        return String.format("PDH%02d", portNumber);
    }
}
