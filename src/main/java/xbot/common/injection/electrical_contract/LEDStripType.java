package xbot.common.injection.electrical_contract;

import com.ctre.phoenix6.signals.StripTypeValue;

public enum LEDStripType {
    GRB,
    RGB,
    BRG,
    GRBW,
    RGBW,
    BRGW;

    public StripTypeValue toPhoenixStripTypeValue() {
        return switch (this) {
            case GRB -> StripTypeValue.GRB;
            case RGB -> StripTypeValue.RGB;
            case BRG -> StripTypeValue.BRG;
            case GRBW -> StripTypeValue.GRBW;
            case RGBW -> StripTypeValue.RGBW;
            case BRGW -> StripTypeValue.BRGW;
        };
    }
}
