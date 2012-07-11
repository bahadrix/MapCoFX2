/*
 *  Problemin çözüleceği sınıf burası. CSP algoritmaları burada implemente edilecek
 */
package mapcofx2;

import java.util.*;
import javafx.scene.shape.Ellipse;

/**
 * Verilen çizgedeki köşeleri renklendirir
 *
 * @author Bahadir
 */
public final class Colorizer {

    // Vars
    private Plotter plotter;
    private Graph<Node> graph;
    private static Scanner scn = new Scanner(System.in);

    public class Node {

        private double x;
        private double y;
        private Ellipse circle;

        public Node(double x, double y) {
            this.x = x;
            this.y = y;
            this.circle = plotter.addCircle(x, y);
        }

        @Override
        public String toString() {
            return "Node{" + "x=" + x + ", y=" + y + ", circle=" + circle + '}';
        }
        
        
    }

    public Colorizer(Plotter plotter) {
        this.plotter = plotter;
        graph = new Graph<>();
        mainRoutine();
    }

    public void mainRoutine() {

        System.out.println("Çizge oluşturuluyor..");
        createGraph();
        System.out.println("Çizge oluşturuldu.");
        
        CSP csp = new CSP(graph, this);
        backTrack(csp, CSP.SUVType.SIMPLE, CSP.ODVType.SIMPLE);


    }

    public void backTrack(CSP csp, CSP.SUVType suvType, CSP.ODVType odvType) {


        Graph.Vertex variable;

        while (true) {
            variable = csp.selectUnassigned(suvType);
            if (variable == null) {
                break;
            }
            Node node = (Node)variable.getElement();
            
            

        }



    }

    public void createGraph() {

        int N = 100;
        int T = 16;
        int x = 0;
        int y = 0;

        Random rando = new Random();

        ArrayList usedPositions = new ArrayList();

        // Create vertices
        for (int i = 0; i < N; i++) {

            do {
                x = rando.nextInt(100) + 1;
                y = rando.nextInt(100) + 1;
            } while (usedPositions.contains(x + "," + y));

            usedPositions.add(x + "," + y);
            graph.insertVertex(new Node(x, y));

        }

        //Create edges
        // n^2
        for (Graph.Vertex v1 : graph.getVertices()) {

            for (Graph.Vertex v2 : graph.getVertices()) {

                Node n1 = (Node) v1.getElement();
                Node n2 = (Node) v2.getElement();

                if (n1.equals(n2)) {
                    continue;
                }

                double distance = Math.sqrt(
                        Math.pow((n1.x - n2.x), 2)
                        + Math.pow((n1.y - n2.y), 2));

                if (distance <= T) {
                    if (graph.insertEdge(distance, v1, v2)) {
                        //plotter.connectCircles(plotter.getCircles().get(n1.index), plotter.getCircles().get(n2.index));
                        plotter.drawLine(n1.x, n1.y, n2.x, n2.y);
                        //System.out.println("(" + n1.x + "," + n1.y + ")" + "," + "(" + n2.x + "," + n2.y + ")");
                    }
                }

            }

        }

    }
}
