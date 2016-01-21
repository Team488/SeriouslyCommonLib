package xbot.common.injection;

import java.io.File;

import com.google.inject.assistedinject.Assisted;

import xbot.common.command.scripted.ScriptedCommand;
import xbot.common.command.scripted.ScriptedCommandFactory;

public interface CommonCommandFactory {
    ScriptedCommand createScriptedCommand(
            File scriptFile,
            ScriptedCommandFactory availableCommandFactory);
    
    ScriptedCommand createScriptedCommand(
            @Assisted("manualScriptText") String manualScriptText,
            @Assisted("manualScriptName") String manualScriptName,
            ScriptedCommandFactory availableCommandFactory);
}

