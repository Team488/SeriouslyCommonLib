package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.Trigger.ButtonScheduler;
import edu.wpi.first.wpilibj.command.Command;
import xbot.common.logic.Latch;
import xbot.common.logic.Latch.EdgeType;

public abstract class AdvancedButton extends Button {
    protected boolean isInverted = false;

    public AdvancedButton() {}

    public void setInverted(boolean inverted) {
        isInverted = inverted;
    }

    public boolean getInverted() {
        return isInverted;
    }

    public void whilePressedNoRestart(final Command command) {
        // Based on source from WPILib's Trigger
        new ButtonScheduler() {
            private boolean pressedLast = get();
            private Latch pressedLatch = new Latch(get(), EdgeType.Both, edge -> {
                if (edge == EdgeType.RisingEdge) {
                    command.start();
                }
                else if (edge == EdgeType.FallingEdge) {
                    command.cancel();
                }
            });

            @Override
            public void start() {
                super.start();
            }

            @Override
            public void execute() {
                pressedLatch.setValue(get());
            }
        }.start();
    }
}
