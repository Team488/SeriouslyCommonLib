package xbot.common.subsystems.drive;

public class SwervePointKinematics {

    final double a; // Acceleration
    final double vi; // Initial velocity
    final double vg; // Goal velocity/Velocity at goal (may not 100% fulfilled)
    final double vm; // Max velocity

    public SwervePointKinematics() {
        this(0, 0, 0, 0);
    }

    public SwervePointKinematics(double a, double vi, double vg, double vm) {
        this.a = a;
        this.vi = vi;
        this.vg = Math.max(Math.min(vm, vg), -vm);
        this.vm = vm;
    }

    public SwervePointKinematics kinematicsWithNewVi(double newVi) {
        return new SwervePointKinematics(a, newVi, vg, vm);
    }
}
