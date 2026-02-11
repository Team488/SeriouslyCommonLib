package xbot.common.command;

import java.util.ArrayList;
import java.util.List;

public final class DataFrameRegistry {
    private static final List<BaseSubsystem> subsystems = new ArrayList<>();
    public static void registerSubsystem(BaseSubsystem s) {
        subsystems.add(s);
    }
    public static List<BaseSubsystem> getAllSubsystems() {
        return subsystems;
    }
}