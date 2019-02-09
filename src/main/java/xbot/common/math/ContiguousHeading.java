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
    
    @Override
    public ContiguousHeading shiftValue(double shiftMagnitude) {
        super.shiftValue(shiftMagnitude);
        return this;
    }

    public XYPair getUnitVector() {
        double x = Math.cos(this.getValue() / 180 * Math.PI);
        double y = Math.sin(this.getValue() / 180 * Math.PI);
        return new XYPair(x, y);
    }
    
}
