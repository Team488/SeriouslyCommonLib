package xbot.common.simulation;

import com.google.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.math.ContiguousHeading;
import xbot.common.math.FieldPose;
import xbot.common.math.XYPair;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class ResetSimulatorPositionCommand extends BaseCommand {

    final WebotsClient webots;
    final BasePoseSubsystem pose;

    private FieldPose targetPose;

    @Inject
    public ResetSimulatorPositionCommand(WebotsClient webots, BasePoseSubsystem pose) {
        this.webots = webots;
        this.pose = pose;
        targetPose = new FieldPose(new XYPair(0,0), new ContiguousHeading());
    }

    public void setTargetPose(FieldPose p) {
        this.targetPose = p;
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        webots.resetPosition(targetPose.getPoint().x, targetPose.getPoint().y, targetPose.getHeading().getValue());
        pose.setCurrentPosition(targetPose.getPoint().x, targetPose.getPoint().y);
        pose.setCurrentHeading(targetPose.getHeading().getValue());
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void execute() {
    }
}
