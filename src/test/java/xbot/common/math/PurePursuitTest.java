package xbot.common.math;

import java.util.Timer;
import java.util.TimerTask;

import xbot.common.injection.BaseWPITest;

public class PurePursuitTest extends BaseWPITest {

    double target_distance = 5;
    protected Timer asyncTimer;
    protected AsyncLinearIntervalJob asyncJob;
    protected double periodMultiplier = 1;
    PlanarEngine engine;
    FieldPose goalOne;
    FieldPose goalTwo;
    boolean pastOne;
        
    public static class PursuitEnvironmentState {
        public FieldPose robot;
        public FieldPose goal;
        public FieldPose rabbit;
        public int loops;
        public double rabbitAngle;
        public double turnPower;
        
        
        public PursuitEnvironmentState(FieldPose robot, FieldPose goal, FieldPose rabbit, int loops, double rabbitAngle, double turnPower) {
            this.robot = robot;
            this.goal = goal;
            this.loops = loops;
            this.rabbit = rabbit;
            this.rabbitAngle = rabbitAngle;
            this.turnPower = turnPower;
        }
    }
    
    public static interface AsyncLinearIntervalJob {
        void onNewStep(PursuitEnvironmentState envState);
    }
    
    public void setAsAsync(AsyncLinearIntervalJob asyncIntervalJob) {
        this.asyncJob = asyncIntervalJob;
    }
        
    public void setAsyncPeriodMultiplier(double newMultiplier) {
        if(Math.abs(this.periodMultiplier - newMultiplier) > 0.01) {
            this.periodMultiplier = newMultiplier;
            
            asyncTimer.cancel();
            startAsyncTimer();
        }
    }
    
    public void vizRun() {
                
        engine = new PlanarEngine();
        goalOne = new FieldPose(new XYPair(10, 70), new ContiguousHeading(90));
        goalTwo = new FieldPose(new XYPair(50, 0), new ContiguousHeading(-90));
        
        startAsyncTimer();    
    }
    
    public void startAsyncTimer() {
        
        
        
        asyncTimer = new Timer();
        asyncTimer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                                        
                    FieldPose target = goalOne;
                    if (pastOne) {
                        target = goalTwo; 
                    }
                    
                    FieldPose robot = engine.getRobotPose();
                    FieldPose rabbit = target.getRabbitPose(robot.getPoint(), 5);
                    
                    double distanceLeft = robot.getPoint().clone().add(target.getPoint().clone().scale(-1)).getMagnitude();
                    if (distanceLeft < 3)
                    {
                        pastOne = true;
                    }
                    
                    double angleToRabbit = target.getDeltaAngleToRabbit(robot, 5);
                    double turnFactorA = angleToRabbit / 180 * 15;
                                        
                    engine.step(0.1, turnFactorA);
                    
                    PursuitEnvironmentState state = new PursuitEnvironmentState(
                            engine.getRobotPose(),
                            target,
                            target.getRabbitPose(robot.getPoint(), 5),
                            engine.loops,
                            angleToRabbit,
                            turnFactorA);
                    
                    asyncJob.onNewStep(state);
                }
            }, 0, (int)(100*this.periodMultiplier));
    }
    
    public void stopTestEnv() {
        asyncTimer.cancel();
    }
}