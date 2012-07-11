/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mapcofx2;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.EllipseBuilder;
import javafx.stage.Stage;

/**
 *
 * @author vaıo
 */
public class MapCoFX2 extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Map Coloring FX v0.1");
        // final StackPane root = new StackPane();

        final Group root = GroupBuilder.create().autoSizeChildren(true).build();

        final Scene scene = SceneBuilder.create().root(root).height(850).width(800).build();

        //------


        Ellipse el = EllipseBuilder.create().centerX(100).centerY(100).fill(Color.ALICEBLUE).radiusX(100).radiusY(100).build();

        root.getChildren().add(el);

        final Delta dragDelta = new Delta();

        scene.setOnScroll(new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent e) {
                System.out.println(e.getDeltaY());

                double currentScale = root.getScaleX();
                double delta = e.getDeltaY() / 1000;
                double newScale = (currentScale + delta) < 0 ? 0.001
                        : (currentScale + delta) > 20 ? 20
                        : (currentScale + delta);
                root.setScaleX(newScale);
                root.setScaleY(newScale);


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

        Plotter plotter = new Plotter(pane);
        Colorizer colorizer = new Colorizer(plotter);
    }

    class Delta {

        double x, y;
    }
}
