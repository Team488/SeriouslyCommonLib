package xbot.common.math;

import edu.wpi.first.util.struct.Struct;

import java.nio.ByteBuffer;

public class XYPairStruct implements Struct<XYPair> {
    @Override
    public Class<XYPair> getTypeClass() {
        return XYPair.class;
    }

    @Override
    public String getTypeString() {
        return "struct:XYPair";
    }

    @Override
    public int getSize() {
        return kSizeDouble * 2;
    }

    @Override
    public String getSchema() {
        return "double x;double y";
    }

    @Override
    public XYPair unpack(ByteBuffer bb) {
        double x = bb.getDouble();
        double y = bb.getDouble();
        return new XYPair(x, y);
    }

    @Override
    public void pack(ByteBuffer bb, XYPair value) {
        bb.putDouble(value.x);
        bb.putDouble(value.y);
    }

    @Override
    public String getTypeName() {
        return "XYPair";
    }
}
