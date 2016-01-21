package xbot.common.command.scripted;

import xbot.common.command.BaseCommand;


/**
 * An interface which describes a provider which translates configuration
 * options into a Command instance.
 *
 */
public interface ScriptedCommandProvider {
    public BaseCommand get(Object[] parameters);
}
