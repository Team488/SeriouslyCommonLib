// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

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
        return "struct:Translation2d";
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
}
