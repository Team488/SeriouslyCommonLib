package xbot.common.subsystems.drive;

import javax.inject.Inject;

import edu.wpi.first.wpilibj.util.Color;
import xbot.common.math.FieldPose;
import xbot.common.math.XYPair;
import xbot.common.properties.PropertyFactory;
import xbot.common.simulation.WebotsClient;
import xbot.common.subsystems.drive.control_logic.HeadingModule.HeadingModuleFactory;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class SimulatedPurePursuitCommand extends ConfigurablePurePursuitCommand {

    private final WebotsClient webots;

    @Inject
    public SimulatedPurePursuitCommand(HeadingModuleFactory headingModuleFactory, BasePoseSubsystem pose, BaseDriveSubsystem drive,
    PropertyFactory propMan, WebotsClient webots) {
        super(headingModuleFactory, pose, drive, propMan);
        this.webots = webots;
    }

    @Override
    public void execute() {
        super.execute();

        // Draw a line from the robot to the "Rabbit"
        webots.drawLine("RabbitLine", poseSystem.getCurrentFieldPose().getPoint(), chaseData.rabbit.getPoint(), Color.kGreen, 60);

        // Draw a line from the Rabbit to the goal point
        webots.drawLine("PoseLine", chaseData.rabbit.getPoint(), chaseData.target.getPoint(), Color.kYellow, 60);

        // Draw something at all the planned points
        // Could be two lines of different color, using the pointAlongPose feature
        int i = 1;
        for (RabbitPoint plannedPoint : this.getPlannedPointsToVisit()) {
            FieldPose ahead = plannedPoint.pose.getPointAlongPoseLine(12);
            FieldPose behind = plannedPoint.pose.getPointAlongPoseLine(-12);

            webots.drawLine("PlannedPointAhead"+i, ahead.getPoint(), plannedPoint.pose.getPoint(), Color.kBlue, 60);
            webots.drawLine("PlannedPointBehind"+i, behind.getPoint(), plannedPoint.pose.getPoint(), Color.kRed, 60);

            i++;
        }
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        // Clear out all the nav markers when this command is no longer active
        for (int i = 0; i < this.getPlannedPointsToVisit().size(); i++){
            webots.drawLine("PlannedPointAhead"+i, new XYPair(0,0), new XYPair(0,0), Color.kBlue, 60);
            webots.drawLine("PlannedPointBehind"+i, new XYPair(0,0), new XYPair(0,0), Color.kRed, 60);
            i++;
        }
    }
}