package xbot.common.math;

public class ContiguousHeading extends ContiguousDouble {

    public ContiguousHeading() {
        super(0, -180, 180);
    }
    
    public ContiguousHeading(double heading) {
        super(heading, -180, 180);
    }
    
    @Override
    public ContiguousHeading clone() {
        return new ContiguousHeading(this.getValue());
    }
    
}
