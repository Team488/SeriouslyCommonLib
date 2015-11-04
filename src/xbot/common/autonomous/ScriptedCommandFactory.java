package xbot.common.autonomous;

public interface ScriptedCommandFactory {
    public ScriptedCommandProvider getProviderForName(String commandTypeName);
}
