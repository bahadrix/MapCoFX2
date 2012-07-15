/*
 *  Renklendirme işini yapan sınıf. Buradaki kodlar MapCoFX'.java dosyasına kaydırılarak
 * bu dosya ortadan kaldırılabilir.
 */
package mapcofx2;

import mapcofx2.CSP.*;
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
            return "Node{" + "x=" + x + ", y=" + y + '}';
        }
    }

    public Colorizer(Plotter plotter) {
        this.plotter = plotter;
        graph = new Graph<>();
        mainRoutine();
    }

    public void mainRoutine() {
        createGraph();
        // Kuru backTracking yap
        //runWith(SUVType.SIMPLE, ODVType.SIMPLE, false, true);
        
        //Kuru + LCV 
        //runWith(SUVType.SIMPLE, ODVType.LCV, false, true);

        //Forward checking yap
        //CSP.activeForwardChecking = false; // Forward checking domainlere etki etsin mi?
        //runWith(SUVType.SIMPLE, ODVType.SIMPLE, true, false);
        
        //TODO Sadece MRV olunca çalışmıyor enteresan
        //runWith(SUVType.MRV, ODVType.SIMPLE, true, true);
        
        
        // a şıkkı
        runWith(SUVType.SIMPLE, ODVType.SIMPLE, false, false);
        
        // b şıkkı
        runWith(SUVType.SIMPLE, ODVType.SIMPLE, true, false);
        
        // c şıkkı
        runWith(SUVType.DEGREE, ODVType.LCV, true, true);
        
        System.out.println("OK");
    }

    public void runWith(SUVType suvType, ODVType odvType, boolean forwarChecking, boolean paintIt) {
        // Problemi oluştur
        CSP csp = new CSP(graph, 5, suvType, odvType, forwarChecking);
        CSP.AssignmentsState result = csp.backTrack();

        // Atamaya göre düğümleri boya
        if (paintIt) {
            paintAssignment(result.getAssignments());
        }

        // İstatistikleri yaz
        System.out.println(csp.stats);
        // Sonucu bildir
        if (result.checkComplete()) {
            System.out.println("Problem çözüldü!");
        } else {
            System.out.println(result.getAssignments().size() + ". düğümden sonrası gelmedi ");
        }

        
    }

    public void paintAssignment(List<Assignment> assignments) {

        for (Assignment asg : assignments) {

            Node element = (Node) asg.variable.getElement();
            element.circle.setFill(asg.color);

        }


    }

    public void createGraph() {

        int N = 100;
        int T = 12;
        int x = 0;
        int y = 0;

        Random rando = new Random();

        ArrayList<String> usedPositions = new ArrayList();

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
