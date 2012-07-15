/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcofx2;


import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author vaıo
 */
public class MapCoFX2 extends Application {

    private Plotter plotter;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Map Coloring FX v0.1");
        // final StackPane root = new StackPane();

        final Group root = GroupBuilder.create()
                .autoSizeChildren(true)
                .scaleX(0.25)
                .scaleY(0.25)
                .layoutX(-1120)
                .layoutY(-1120)
                .build();
    
        final Scene scene = SceneBuilder.create()
                .root(root)
                .height(850)
                .width(800)
                .fill(Color.BLACK)
                .build();

        //------

        final Delta dragDelta = new Delta();

        scene.setOnScroll(new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent e) {
                
                
                double currentScale = root.getScaleX();
                double delta = e.getDeltaY() / 1000;
                double newScale = (currentScale + delta) < 0 ? 0.1
                        : (currentScale + delta) > 20 ? 20
                        : (currentScale + delta);
                root.setScaleX(newScale);
                root.setScaleY(newScale);

            }
        });

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent e) {
                
                switch(e.getCode()) {
                    case F11:
                       primaryStage.setFullScreen(true); 
                       break;
                    case F5:
                      plotter.clear();
                      drawScene(root);
                }
            }
        });

        
                
        
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = root.getLayoutX() - mouseEvent.getSceneX();
                dragDelta.y = root.getLayoutY() - mouseEvent.getSceneY();
                scene.setCursor(Cursor.CLOSED_HAND);
            }
        });
        scene.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                scene.setCursor(Cursor.DEFAULT);
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                root.setLayoutX(mouseEvent.getSceneX() + dragDelta.x);
                root.setLayoutY(mouseEvent.getSceneY() + dragDelta.y);
            }
        });
        scene.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent mouseEvent) {
                //scene.setCursor(Cursor.OPEN_HAND);
            }
        });


        ///--



        primaryStage.setScene(scene);
        
        primaryStage.show();
        

        drawScene(root);
    }

    private void drawScene(Group pane) {

        plotter = new Plotter(pane);
        Colorizer colorizer = new Colorizer(plotter);
    }

    class Delta {

        double x, y;
    }
}
