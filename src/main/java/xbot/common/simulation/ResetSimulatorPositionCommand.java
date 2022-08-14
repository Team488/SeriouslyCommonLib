package xbot.common.simulation;

import java.util.ArrayList;

import javax.inject.Inject;

import org.json.JSONObject;

import edu.wpi.first.math.geometry.Rotation2d;
import xbot.common.command.BaseCommand;
import xbot.common.math.FieldPose;
import xbot.common.math.XYPair;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class ResetSimulatorPositionCommand extends BaseCommand {

    final WebotsClient webots;
    final BasePoseSubsystem pose;

    private FieldPose targetPose;
    SimulationPayloadDistributor distributor;

    @Inject
    public ResetSimulatorPositionCommand(WebotsClient webots, BasePoseSubsystem pose,
            SimulationPayloadDistributor distributor) {
        this.webots = webots;
        this.pose = pose;
        this.distributor = distributor;
        targetPose = new FieldPose(new XYPair(0, 0), new Rotation2d());
    }

    public void setTargetPose(FieldPose p) {
        this.targetPose = p;
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        webots.resetPosition(targetPose.getPoint().x, targetPose.getPoint().y, targetPose.getHeading().getDegrees());
        // Need to add a tiny sleep to let Webots update itself, since there are async elements on that end. If that gets ironed out, we can remove this delay.
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject response = webots.sendMotors(new ArrayList<JSONObject>());
        distributor.distributeSimulationPayload(response);
        pose.periodic();

        pose.setCurrentPosition(targetPose.getPoint().x, targetPose.getPoint().y);
        pose.setCurrentHeading(targetPose.getHeading().getDegrees());
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void execute() {
    }
}
