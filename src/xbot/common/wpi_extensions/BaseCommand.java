package xbot.common.wpi_extensions;

import edu.wpi.first.wpilibj.command.Command;

public abstract class BaseCommand extends Command {
	
	@Override
	public abstract void initialize();

	@Override
	public abstract void execute();
	
	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void end() {
		
	}

	@Override
	public void interrupted() {
		this.end();
	}

}
