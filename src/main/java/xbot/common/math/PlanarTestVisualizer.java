package xbot.common.math;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;

import edu.wpi.first.math.geometry.Rotation2d;
import xbot.common.subsystems.drive.RabbitPoint;


public class PlanarTestVisualizer {
    
    public JFrame frmLinearTestVisualizer;
    public PlanarVisualizationPanel vizPanel;
    public JPanel controlPanel;
    public JSlider speedSlider;
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    RabbitPoint p = new RabbitPoint(new FieldPose(new XYPair(100, 100), Rotation2d.fromDegrees(135)));
                    PlanarTestVisualizer window = new PlanarTestVisualizer(new ArrayList<RabbitPoint>(Arrays.asList(p)));
                    window.frmLinearTestVisualizer.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public PlanarTestVisualizer(List<RabbitPoint> points) {
        initialize(points);
    }
    
    private void initialize(List<RabbitPoint> points) {
        frmLinearTestVisualizer = new JFrame();
        
        frmLinearTestVisualizer.setTitle("Linear test visualizer");
        frmLinearTestVisualizer.setBounds(100, 100, 1600, 1000);
        frmLinearTestVisualizer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmLinearTestVisualizer.getContentPane().setLayout(new BoxLayout(frmLinearTestVisualizer.getContentPane(), BoxLayout.Y_AXIS));
        
        JSplitPane splitPane = new JSplitPane();
        frmLinearTestVisualizer.getContentPane().add(splitPane);
        
        vizPanel = new PlanarVisualizationPanel(1200, 1200);
        splitPane.setLeftComponent(vizPanel);
        
        controlPanel = new JPanel();
        splitPane.setRightComponent(controlPanel);
        
        speedSlider = new JSlider();
        speedSlider.setMinimum(1);
        speedSlider.setValue(10);
        controlPanel.add(speedSlider);
        
        startTest(points);
    }
        
    private void startTest(List<RabbitPoint> points) {
        PurePursuitTest test = new PurePursuitTest(points);
        test.setAsAsync((envState) -> {
            vizPanel.updateViz(envState);
            vizPanel.repaint();
            
            
            test.setAsyncPeriodMultiplier(10d / speedSlider.getValue());
        });
        
        test.vizRun();
    }
}