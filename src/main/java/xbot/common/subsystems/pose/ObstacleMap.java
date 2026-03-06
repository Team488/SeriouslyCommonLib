package xbot.common.subsystems.pose;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.Distance;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;

import java.util.List;
import java.util.Optional;

public abstract class ObstacleMap {
    private final List<IFieldObstacle> fieldObstacles;
    private final Distance robotRadius;

    public ObstacleMap(List<IFieldObstacle> fieldObstacles, XSwerveDriveElectricalContract electricalContract) {
        this.fieldObstacles = fieldObstacles;
        this.robotRadius = electricalContract.getRadiusOfRobot();
    }

    public Optional<IFieldObstacle> closestObstacle(Pose2d pose, boolean allowToughTerrain) {
        if (this.fieldObstacles.size() == 0) {
            return Optional.empty();
        }

        var translation = pose.getTranslation();
        var obstacleLocations = this.fieldObstacles.stream()
            .filter(obstacle -> !obstacle.isToughTerrain() || !allowToughTerrain)
            .map(obstacle -> obstacle.center())
            .toList();

        var closestObstacleLocation = translation.nearest(obstacleLocations);
        return this.fieldObstacles.stream()
            .filter(obstacle -> !obstacle.isToughTerrain() || !allowToughTerrain)
            .filter(obstacle -> obstacle.center() == closestObstacleLocation)
            .findFirst();
    }

    public boolean doesRobotPathIntersect(Translation2d start, Translation2d end, boolean allowToughTerrain) {
        return this.fieldObstacles.stream()
            .filter(obstacle -> !obstacle.isToughTerrain() || !allowToughTerrain)
            .anyMatch(obstacle -> obstacle.doesRobotPathIntersect(start, end, this.robotRadius));
    }
}
