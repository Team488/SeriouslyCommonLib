package xbot.common.command.scripted;

/**
 * An interface which describes a factory which translates command
 * type names into {@link ScriptedCommandProvider}s.
 *
 */
public interface ScriptedCommandFactory {
    public ScriptedCommandProvider getProviderForName(String commandTypeName);
}
