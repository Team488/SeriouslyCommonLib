package xbot.common.math;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import edu.wpi.first.math.geometry.Rotation2d;

//@SuppressWarnings("serial")
public class PlanarVisualizationPanel extends JPanel {
    private int centerX;
    private int centerY;
    
    private String loops = "0";
    private String robotStats = "0, 0";
    private FieldPose robotCurrentPosition = new FieldPose(new XYPair(0,0), new Rotation2d());
    private XYPair goalPosition = new XYPair(0,0);
    private FieldPose rabbitPosition = new FieldPose(new XYPair(0,0), new Rotation2d());
    private String rabbitStats = "...";

    private int preferredWidth = 300;
    private int minimumWidth = 300;

    public PlanarVisualizationPanel() {

    }

    public PlanarVisualizationPanel(int preferredWidth, int minimumWidth) {
        this.preferredWidth = preferredWidth;
        this.minimumWidth = minimumWidth;
    }
    /*
    public void setRobotDistance(XYPair position) {
        robotCurrentPosition = position;
    }*/
    
    public void updateViz(PurePursuitTest.PursuitEnvironmentState state) {
        robotCurrentPosition = state.robot;
        loops = Integer.toString(state.loops);
        goalPosition = state.goal.getPoint();
        rabbitPosition = state.rabbit;
        
        robotStats = String.format("%.2f, %.2f, Angle: %.2f", 
                robotCurrentPosition.getPoint().x, 
                robotCurrentPosition.getPoint().y, 
                robotCurrentPosition.getHeading().getDegrees());
        rabbitStats = String.format("Angle to Rabbit: %.2f, TurnPower: %.2f, Translate: %.2f", 
            state.rabbitAngle, state.turnPower, state.translatePower);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;

        centerX = this.getWidth() / 4;
        centerY = this.getHeight() / 2;
        
        int linearFactor = 1;
        
        // draw start
        graphics.setColor(Color.GREEN);
        graphics.setStroke(new BasicStroke(5));
        graphics.drawOval(centerX, centerY, 5, 5);
        
        // draw finish
        graphics.setColor(Color.RED);
        graphics.setStroke(new BasicStroke(5));
        graphics.drawOval(
                (int)(centerX+goalPosition.x*linearFactor), 
                (int)(centerY-goalPosition.y*linearFactor),
                5, 5);
        
        // draw rabbit
        graphics.setColor(Color.GREEN);
        graphics.setStroke(new BasicStroke(5));
        graphics.drawOval(
                (int)(centerX+rabbitPosition.getPoint().x*linearFactor), 
                (int)(centerY-rabbitPosition.getPoint().y*linearFactor),
                5, 5);
                
        // draw robot
        graphics.setColor(Color.BLUE);
        graphics.setStroke(new BasicStroke(5));
        graphics.drawOval(
                (int)(centerX+robotCurrentPosition.getPoint().x * linearFactor), 
                (int)(centerY-robotCurrentPosition.getPoint().y * linearFactor), 
                5, 5);
        
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(5));
        graphics.drawString(robotStats, centerX, centerY+150);
        
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(5));
        graphics.drawString(rabbitStats, centerX, centerY+100);
        
        //draw loops
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(5));
        graphics.drawString(loops, centerX, centerY+50);
        
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(preferredWidth, 0);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(minimumWidth, 0);
    }
    
}