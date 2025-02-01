package xbot.common.subsystems.drive;

import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.logging.RobotAssertionManager;

import java.util.ArrayList;
import java.util.List;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Seconds;
import static org.junit.Assert.assertEquals;

public class SwerveCalculatorTest extends BaseCommonLibTest {

    RobotAssertionManager assertionManager;

    @Override
    public void setUp() {
        super.setUp();
        this.assertionManager = getInjectorComponent().robotAssertionManager();
    }

    private SwerveKinematicsCalculator newCalculator(double acceleration, double vInitial, double vGoal, double vMax) {
        return new SwerveKinematicsCalculator(
                assertionManager,
                Meters.zero(),
                Meters.of(10),
                new SwervePointKinematics(acceleration, vInitial, vGoal, vMax)
        );
    }

    private void compareNodeMaps(List<CalculatorNode> map1, List<CalculatorNode> map2) {
        assertEquals(map1.size(), map2.size());
        for (int i = 0; i < map1.size(); i++) {
            assertEquals(map1.get(i).operationTime().in(Seconds), map2.get(i).operationTime().in(Seconds), 0.001);
            assertEquals(map1.get(i).operationAcceleration(), map2.get(i).operationAcceleration(), 0.001);
            assertEquals(map1.get(i).operationEndingVelocity(), map2.get(i).operationEndingVelocity(), 0.001);
        }
    }

    @Test
    public void testAccelerateMaxThenCruiseThenGoal() {
        // Accelerate to max, then cruise, then down to goal
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 0, 0, 2);
        List<CalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new CalculatorNode(4, 0.5, 2));
        nodeMap.add(new CalculatorNode(1, 0, 2));
        nodeMap.add(new CalculatorNode(4, -0.5, 0));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }

    @Test
    public void testAccelerateGoalThenMaxThenCruiseThenGoal() {
        // Accelerate to goal, then max, then cruise, then down to goal
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 0., 1, 2);
        List<CalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new CalculatorNode(2, 0.5, 1));
        nodeMap.add(new CalculatorNode(2, 0.5, 2));
        nodeMap.add(new CalculatorNode(1.5, 0, 2));
        nodeMap.add(new CalculatorNode(2, -0.5, 1));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }

    @Test
    public void testAccelerateGoalThenAccelerateThenDecelerate() {
        // Accelerate to goal, then accelerate, then decelerate, forming a peak, will not reach vMax so no cruise
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 0, 1, 5);
        List<CalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new CalculatorNode(2, 0.5, 1));
        nodeMap.add(new CalculatorNode(2.69042, 0.5, 2.34521));
        nodeMap.add(new CalculatorNode(2.69042, -0.5, 1));
        compareNodeMaps(nodeMap, calculator.getNodeMap());

    }

    @Test
    public void testAccelerateAsMuchAsPossible() {
        // Accelerate as much as possible because endingVelocity is impossible to reach
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 0, 10, 10);
        List<CalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new CalculatorNode(6.32456, 0.5, 3.16228));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }

    @Test
    public void testAccelerateThenCruiseToTheEnd() {
        // Accelerate to goal velocity, in this case will be the same as max velocity, then cruise
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 0, 3, 3);
        List<CalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new CalculatorNode(6, 0.5, 3));
        nodeMap.add(new CalculatorNode(0.33333, 0, 3));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }

    @Test
    public void testDecelerateAsMuchAsPossible() {
        // Decelerate as much as possible because endingVelocity is impossible to reach
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 8, 0, 10);
        List<CalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new CalculatorNode(1.30306, -0.5, 7.34847));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }

    @Test
    public void testAccelerateThenCruiseThenDecelerateToGoal() {
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 1, 0, 1.5);
        List<CalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new CalculatorNode(1, 0.5, 1.5));
        nodeMap.add(new CalculatorNode(4.33333, 0, 1.5));
        nodeMap.add(new CalculatorNode(1, -0.5, 1));
        nodeMap.add(new CalculatorNode(2, -0.5, 0));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }

    @Test
    public void testAccelerateThenDecelerateToGoal() {
        SwerveKinematicsCalculator calculator = newCalculator(0.5, 1, 0, 10);
        List<CalculatorNode> nodeMap = new ArrayList<>();
        nodeMap.add(new CalculatorNode(2.69042, 0.5, 2.34521));
        nodeMap.add(new CalculatorNode(2.69042, -0.5, 1));
        nodeMap.add(new CalculatorNode(2, -0.5, 0));
        compareNodeMaps(nodeMap, calculator.getNodeMap());
    }
}
