/*
 * Çizgeyi ekrana çizdermeyi sağlayan sınıf
 */
package mapcofx2;

import java.util.LinkedList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.EllipseBuilder;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.stage.Screen;

/**
 *
 * @author Bahadir
 */
public class Plotter {
    private Group pane;
    private double screenWidth;
    private double screenHeight;
    public double circleRadius = 10;
    public double scaleFactor = 3;
    

    private List<Line> lines;
    
    public Plotter(Group pane) {

        this.lines = new LinkedList<>();
        this.pane = pane;
        this.screenWidth = Screen.getPrimary().getBounds().getWidth();
        this.screenHeight = Screen.getPrimary().getBounds().getHeight();
    }

    public Ellipse addCircle(double x, double y) {
        
        Ellipse circle = EllipseBuilder.create()
                .centerX(scaleScalar(x))
                .centerY(scaleScalar(y))
                .radiusX(circleRadius)
                .radiusY(circleRadius)
                .fill(Color.ORANGERED)
                .build();
        
        
        pane.getChildren().add(circle);
        return circle;
    }
    
    private double scaleScalar(double scalar) {
        return scalar * circleRadius * scaleFactor;
    }

    
    public void drawLine(double x1, double y1, double x2, double y2) {
        
        final Line line = LineBuilder.create()
                .startX(scaleScalar(x1))
                .startY(scaleScalar(y1))
                .endX(scaleScalar(x2))
                .endY(scaleScalar(y2))
                .strokeWidth(2)
                .build();
        
        line.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                line.setStroke(Color.RED);
            }
        });
        line.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                line.setStroke(Color.BLACK);
            }
        });
        
        lines.add(line);
        pane.getChildren().add(line);
        line.toBack();
   
    }

    public List<Line> getLines() {
        return lines;
    }
   
    
    
}
