package xbot.common.math;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import xbot.common.injection.BaseWPITest;
import xbot.common.injection.components.BaseComponent;
import xbot.common.injection.components.DaggerPurePursuitTestComponent;
import xbot.common.injection.components.PurePursuitTestComponent;
import xbot.common.math.PIDManager.PIDManagerFactory;
import xbot.common.subsystems.drive.ConfigurablePurePursuitCommand;
import xbot.common.subsystems.drive.MockDriveSubsystem;
import xbot.common.subsystems.drive.PurePursuitCommand.RabbitChaseInfo;
import xbot.common.subsystems.drive.RabbitPoint;
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
    List<RabbitPoint> points;

    public PurePursuitTest(List<RabbitPoint> points)
    {
        this.points = points;
    }

    @Override
    protected BaseComponent createDaggerComponent() {
        return DaggerPurePursuitTestComponent.create();
    }

    protected PurePursuitTestComponent getInjectorComponent() {
        return (PurePursuitTestComponent)super.getInjectorComponent();
    }

    public static class PursuitEnvironmentState {
        public FieldPose robot;
        public FieldPose goal;
        public FieldPose rabbit;
        public int loops;
        public double rabbitAngle;
        public double turnPower;
        public double translatePower;
                
        public PursuitEnvironmentState(FieldPose robot, FieldPose goal, FieldPose rabbit, 
        int loops, double rabbitAngle, double turnPower, double translatePower) {
            this.robot = robot;
            this.goal = goal;
            this.loops = loops;
            this.rabbit = rabbit;
            this.rabbitAngle = rabbitAngle;
            this.turnPower = turnPower;
            this.translatePower = translatePower;
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

    protected ConfigurablePurePursuitCommand command;
    MockBasePoseSubsystem poseSystem;
    
    public void vizRun() {
        super.setUp();        
        engine = new PlanarEngine();
        PIDManagerFactory pf = getInjectorComponent().pidFactory();
        MockDriveSubsystem b = (MockDriveSubsystem)getInjectorComponent().driveSubsystem();
        this.poseSystem = (MockBasePoseSubsystem)getInjectorComponent().poseSubsystem();
        b.changeRotationalPid(pf.create("testRot", 0.05, 0, 0));
        b.changePositionalPid(pf.create("testPos", 0.1, 0, 0.1));
        command = getInjectorComponent().configurablePurePursuitCommand();
        command.setDotProductDrivingEnabled(true);
        setPoints();
        command.initialize();
        startAsyncTimer();    
    }

    public void setPoints() {
        command.setPoints(points);
    }
    
    public void startAsyncTimer() {
        asyncTimer = new Timer();
        asyncTimer.schedule(
            new TimerTask() {
                @Override
                public void run() {
                    FieldPose robot = engine.getRobotPose();
                    poseSystem.setCurrentHeading(robot.getHeading().getDegrees());
                    poseSystem.setCurrentPosition(robot.getPoint().x, robot.getPoint().y);
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
                            info.rotation,
                            info.translation);
                    
                    asyncJob.onNewStep(state);
                }
            }, 0, (int)(100*this.periodMultiplier));
    }
    
    public void stopTestEnv() {
        asyncTimer.cancel();
    }
}