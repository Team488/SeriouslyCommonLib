package xbot.common.math;

import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;


public class PlanarTestVisualizer {
    
    private JFrame frmLinearTestVisualizer;
    private PlanarVisualizationPanel vizPanel;
    private JPanel controlPanel;
    private JSlider speedSlider;
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PlanarTestVisualizer window = new PlanarTestVisualizer();
                    window.frmLinearTestVisualizer.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public PlanarTestVisualizer() {
        initialize();
    }
    
    private void initialize() {
        frmLinearTestVisualizer = new JFrame();
        
        frmLinearTestVisualizer.setTitle("Linear test visualizer");
        frmLinearTestVisualizer.setBounds(100, 100, 800, 400);
        frmLinearTestVisualizer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmLinearTestVisualizer.getContentPane().setLayout(new BoxLayout(frmLinearTestVisualizer.getContentPane(), BoxLayout.Y_AXIS));
        
        JSplitPane splitPane = new JSplitPane();
        frmLinearTestVisualizer.getContentPane().add(splitPane);
        
        vizPanel = new PlanarVisualizationPanel(800, 500);
        splitPane.setLeftComponent(vizPanel);
        
        controlPanel = new JPanel();
        splitPane.setRightComponent(controlPanel);
        
        speedSlider = new JSlider();
        speedSlider.setMinimum(1);
        speedSlider.setValue(10);
        controlPanel.add(speedSlider);
        
        startTest();
    }
        
    private void startTest() {
        PurePursuitTest test = new PurePursuitTest();
        test.setAsAsync((envState) -> {
            vizPanel.updateViz(envState);
            vizPanel.repaint();
            
            
            test.setAsyncPeriodMultiplier(10d / speedSlider.getValue());
        });
        
        test.vizRun();
    }
}