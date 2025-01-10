package xbot.common.subsystems.drive;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SwerveCalculatorTest {

    // startPosition, endPosition, maximumAcceleration, startingVelocity, goalVelocity, maximumVelocity
    private SwerveKinematicsCalculator newCalculator(double a, double vInitial, double vGoal, double vMax) {
        return new SwerveKinematicsCalculator(0, 10, new SwervePointKinematics(a, vInitial, vGoal, vMax));
    }

    private void compareNodeMaps(List<SwerveCalculatorNode> map1, List<SwerveCalculatorNode> map2) {
        assertEquals(map1.size(), map2.size());
        for (int i = 0; i < map1.size(); i++) {
            assertEquals(map1.get(i).operationTime, map2.get(i).operationTime, 0.001);
            assertEquals(map1.get(i).operationAcceleration, map2.get(i).operationAcceleration, 0.001);
            assertEquals(map1.get(i).operationEndingVelocity, map2.get(i).operationEndingVelocity, 0.001);
        }
    }

    @Test
    public void testAccelerateMaxThenCruiseThenGoal() {
        // Accelerate to max, then cruise, then down to goal
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 0, 0, 2);
        List<SwerveCalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new SwerveCalculatorNode(4, 0.5, 2));
        nodeMap.add(new SwerveCalculatorNode(1, 0, 2));
        nodeMap.add(new SwerveCalculatorNode(4, -0.5, 0));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }

    @Test
    public void testAccelerateGoalThenMaxThenCruiseThenGoal() {
        // Accelerate to goal, then max, then cruise, then down to goal
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 0., 1, 2);
        List<SwerveCalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new SwerveCalculatorNode(2, 0.5, 1));
        nodeMap.add(new SwerveCalculatorNode(2, 0.5, 2));
        nodeMap.add(new SwerveCalculatorNode(1.5, 0, 2));
        nodeMap.add(new SwerveCalculatorNode(2, -0.5, 1));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }

    @Test
    public void testAccelerateGoalThenAccelerateThenDecelerate() {
        // Accelerate to goal, then accelerate, then decelerate, forming a peak, will not reach vMax so no cruise
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 0, 1, 5);
        List<SwerveCalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new SwerveCalculatorNode(2, 0.5, 1));
        nodeMap.add(new SwerveCalculatorNode(2.69042, 0.5, 2.34521));
        nodeMap.add(new SwerveCalculatorNode(2.69042, -0.5, 1));
        compareNodeMaps(nodeMap, calculator.getNodeMap());

    }

    @Test
    public void testAccelerateAsMuchAsPossible() {
        // Accelerate as much as possible because endingVelocity is impossible to reach
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 0, 10, 10);
        List<SwerveCalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new SwerveCalculatorNode(6.32456, 0.5, 3.16228));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }

    @Test
    public void testAccelerateThenCruiseToTheEnd() {
        // Accelerate to goal velocity, in this case will be the same as max velocity, then cruise
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 0, 3, 3);
        List<SwerveCalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new SwerveCalculatorNode(6, 0.5, 3));
        nodeMap.add(new SwerveCalculatorNode(0.33333, 0, 3));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }

    @Test
    public void testDecelerateAsMuchAsPossible() {
        // Decelerate as much as possible because endingVelocity is impossible to reach
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 8, 0, 10);
        List<SwerveCalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new SwerveCalculatorNode(1.30306, -0.5, 7.34847));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }

    @Test
    public void testAccelerateThenCruiseThenDecelerateToGoal() {
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 1, 0, 1.5);
        List<SwerveCalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new SwerveCalculatorNode(1, 0.5, 1.5));
        nodeMap.add(new SwerveCalculatorNode(4.33333, 0, 1.5));
        nodeMap.add(new SwerveCalculatorNode(1, -0.5, 1));
        nodeMap.add(new SwerveCalculatorNode(2, -0.5, 0));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }

    @Test
    public void testAccelerateThenDecelerateToGoal() {
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 1, 0, 10);
        List<SwerveCalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new SwerveCalculatorNode(2.69042, 0.5, 2.34521));
        nodeMap.add(new SwerveCalculatorNode(2.69042, -0.5, 1));
        nodeMap.add(new SwerveCalculatorNode(2, -0.5, 0));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }
}
