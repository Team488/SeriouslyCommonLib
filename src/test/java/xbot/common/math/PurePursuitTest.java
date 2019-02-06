package xbot.common.math;

import java.util.Timer;
import java.util.TimerTask;

import xbot.common.injection.BaseWPITest;
import xbot.common.subsystems.drive.BaseDriveSubsystem;
import xbot.common.subsystems.drive.ConfigurablePurePursuitCommand;
import xbot.common.subsystems.drive.MockDriveSubsystem;
import xbot.common.subsystems.drive.PurePursuitCommand.RabbitChaseInfo;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.MockBasePoseSubsystem;

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

    private ConfigurablePurePursuitCommand command;
    MockBasePoseSubsystem poseSystem;
    
    public void vizRun() {
        super.setUp();        
        engine = new PlanarEngine();
        PIDFactory pf = injector.getInstance(PIDFactory.class);
        MockDriveSubsystem b = (MockDriveSubsystem)injector.getInstance(BaseDriveSubsystem.class);
        this.poseSystem = (MockBasePoseSubsystem)injector.getInstance(BasePoseSubsystem.class);
        b.changeRotationalPid(pf.createPIDManager("testRot", 0.05, 0, 0));
        b.changePositionalPid(pf.createPIDManager("testPos", 0.1, 0, 0));
        command = injector.getInstance(ConfigurablePurePursuitCommand.class);
        
        command.addPoint(new FieldPose(new XYPair(10, 70), new ContiguousHeading(90)));
        command.addPoint(new FieldPose(new XYPair(50, 0), new ContiguousHeading(-90)));
        command.initialize();
        startAsyncTimer();    
    }
    
    public void startAsyncTimer() {
        
        
        
        asyncTimer = new Timer();
        asyncTimer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                    FieldPose robot = engine.getRobotPose();
                    poseSystem.setCurrentHeading(robot.getHeading().getValue());
                    RabbitChaseInfo info = command.evaluateCurrentPoint(robot);
                    FieldPose target = info.target;
                    double angleToRabbit = target.getDeltaAngleToRabbit(robot, 5);
                                        
                    engine.step(info.translation, info.rotation);
                    
                    PursuitEnvironmentState state = new PursuitEnvironmentState(
                            engine.getRobotPose(),
                            target,
                            info.rabbit,
                            engine.loops,
                            angleToRabbit,
                            info.rotation);
                    
                    asyncJob.onNewStep(state);
                }
            }, 0, (int)(100*this.periodMultiplier));
    }
    
    public void stopTestEnv() {
        asyncTimer.cancel();
    }
}