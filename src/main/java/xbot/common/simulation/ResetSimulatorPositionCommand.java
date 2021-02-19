package xbot.common.simulation;

import com.google.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.math.ContiguousHeading;
import xbot.common.math.FieldPose;
import xbot.common.math.XYPair;

public class ResetSimulatorPositionCommand extends BaseCommand {

    final WebotsClient webots;

    private FieldPose targetPose;

    @Inject
    public ResetSimulatorPositionCommand(WebotsClient webots) {
        this.webots = webots;
        targetPose = new FieldPose(new XYPair(0,0), new ContiguousHeading());
    }

    public void setTargetPose(FieldPose p) {
        this.targetPose = p;
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        webots.resetPosition(targetPose.getPoint().x, targetPose.getPoint().y, targetPose.getHeading().getValue());
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void execute() {
    }
}
