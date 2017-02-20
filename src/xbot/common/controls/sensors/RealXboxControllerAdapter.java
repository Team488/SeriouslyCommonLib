package xbot.common.controls.sensors;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.GenericHID.HIDType;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.XboxController;

public class RealXboxControllerAdapter extends BaseXboxControllerAdapter {

    protected XboxController controller;

    @Inject
    public RealXboxControllerAdapter(@Assisted("port") int port) {
        super(port);
        controller = new XboxController(port);
    }

    @Override
    public boolean getBumper() {
        return controller.getBumper();
    }

    @Override
    public boolean getStickButton() {
        return controller.getStickButton();
    }

    @Override
    public int hashCode() {
        return controller.hashCode();
    }

    @Override
    public final double getX() {
        return controller.getX();
    }

    @Override
    public double getX(Hand hand) {
        return controller.getX(hand);
    }

    @Override
    public final double getY() {
        return controller.getY();
    }

    @Override
    public double getY(Hand hand) {
        return controller.getY(hand);
    }

    @Override
    public double getRawAxis(int axis) {
        return controller.getRawAxis(axis);
    }

    @Override
    public boolean getBumper(Hand hand) {
        return controller.getBumper(hand);
    }

    @Override
    public boolean getTrigger(Hand hand) {
        return controller.getTrigger(hand);
    }

    @Override
    public int getPOV() {
        return controller.getPOV();
    }

    @Override
    public int getPort() {
        return controller.getPort();
    }

    @Override
    public boolean getTop(Hand hand) {
        return controller.getTop(hand);
    }

    @Override
    public boolean equals(Object obj) {
        return controller.equals(obj);
    }

    @Override
    public boolean getRawButton(int button) {
        return controller.getRawButton(button);
    }

    @Override
    public double getTriggerAxis(Hand hand) {
        return controller.getTriggerAxis(hand);
    }

    @Override
    public boolean getAButton() {
        return controller.getAButton();
    }

    @Override
    public boolean getBButton() {
        return controller.getBButton();
    }

    @Override
    public boolean getXButton() {
        return controller.getXButton();
    }

    @Override
    public boolean getYButton() {
        return controller.getYButton();
    }

    @Override
    public boolean getStickButton(Hand hand) {
        return controller.getStickButton(hand);
    }

    @Override
    public boolean getBackButton() {
        return controller.getBackButton();
    }

    @Override
    public boolean getStartButton() {
        return controller.getStartButton();
    }

    @Override
    public int getPOV(int pov) {
        return controller.getPOV(pov);
    }

    @Override
    public int getPOVCount() {
        return controller.getPOVCount();
    }

    @Override
    public HIDType getType() {
        return controller.getType();
    }

    @Override
    public String getName() {
        return controller.getName();
    }

    @Override
    public void setOutput(int outputNumber, boolean value) {
        controller.setOutput(outputNumber, value);
    }

    @Override
    public void setOutputs(int value) {
        controller.setOutputs(value);
    }

    @Override
    public void setRumble(RumbleType type, double value) {
        controller.setRumble(type, value);
    }

    @Override
    public String toString() {
        return controller.toString();
    }
}
