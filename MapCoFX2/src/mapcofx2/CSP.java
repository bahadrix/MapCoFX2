/*
 * Problemin çözüleceği sınıf burası. CSP algoritmaları burada implemente edilecek
 */
package mapcofx2;

import com.sun.javaws.exceptions.ExitException;
import java.util.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import mapcofx2.Graph.Vertex;

/**
 *
 * @author Bahadir
 */
public class CSP {

    // Enums
    public enum SUVType {

        SIMPLE, MRV, DEGREE
    }

    public enum ODVType {

        SIMPLE, LCV
    }
    private Graph graph;
    private int vertexCount;
    private int colorCount;
    private List<Paint> standartDomain;
    private SUVType suvType;
    private ODVType odvType;
    private boolean forwardChecking;
    final public Stats stats;
    private static int checkIsConsistentCount = 0;
    private static int selectUnassigneCallCount = 0;
    public static boolean activeForwardChecking = true;

    public class Stats {

        public int checkIsCompleteCount = 0;
        public int successfulAssignmentCount = 0;
        public int failedAssignmentCount = 0;
        public int orderDomainCallCount = 0;
        public int revertCount = 0;
        public int forwardCheckFailure = 0;

        public Stats() {
            checkIsConsistentCount = 0;
            selectUnassigneCallCount = 0;
        }

        @Override
        public String toString() {

            return "STATS:\r\n"
                    // + "Is Soluble:" + (unassignedVariables.isEmpty()) + "\r\n"
                    + "VARIABLE SELECT METHOD:" + suvType.name() + "\r\n"
                    + "VALUE ORDER METHOD:" + odvType.name() + "\r\n"
                    + "FORWARD CHECKING:" + forwardChecking + "\r\n"
                    + "Forward Check Failure Count:" + forwardCheckFailure + "\r\n"
                    + "Check is complete count:" + checkIsCompleteCount + "\r\n"
                    + "Successfull assignment count:" + successfulAssignmentCount + "\r\n"
                    + "Failed Assignment Count:" + failedAssignmentCount + "\r\n"
                    + "Consistency check count:" + checkIsConsistentCount + "\r\n"
                    + "Order-Domain Call Count:" + orderDomainCallCount + "\r\n"
                    + "Select-Unassigned Call Count:" + selectUnassigneCallCount + "\r\n"
                    + "Backtrack count:" + revertCount;
        }
    }

    public class Assignment {

        final public Graph.Vertex variable;
        final public Paint color;

        public Assignment(Vertex variable, Paint color) {
            this.variable = variable;
            this.color = color;
        }
    }

    public class AssignmentsState {

        private List<Assignment> assignments;
        private Map<Vertex, List<Paint>> domains;
        private List<Graph.Vertex> unassignedVariables;
        private Assignment actionAssignment;

        public AssignmentsState() {
            assignments = new LinkedList<>();



            this.unassignedVariables = new LinkedList<>();
            unassignedVariables.addAll(graph.getVertices());

            domains = new HashMap<>();
            for (Vertex v : unassignedVariables) {
                domains.put(v, new LinkedList<>(standartDomain));
            }

        }

        /**
         * Bunlar her state'te ayrı tutulan özellikler 1. Yapılmış atamalar 2.
         * Değişkenlerin mevcut domainleri 3. Atanmamış değişkenler
         *
         * @param cloneFrom Değerlerin kopyalandığı state
         */
        public AssignmentsState(AssignmentsState cloneFrom) {
            this.assignments = new LinkedList<>(cloneFrom.getAssignments());

            //Cloning domain set
            // ForwardCheck'in son kalan rengi kullanmaması hatasının sebebi
            // bunun yerine this.domains = new HashMap<>(cloneFrom.domains) kullanılmasıymış

            this.domains = new HashMap<>();
            for (Vertex cv : cloneFrom.domains.keySet()) {
                domains.put(cv, new LinkedList<>(cloneFrom.domains.get(cv)));
            }
            // eof CLoning domain set

            this.unassignedVariables = new LinkedList<>(cloneFrom.unassignedVariables);

        }

        public Vertex selectUnassigned() {
            selectUnassigneCallCount++;
            switch (suvType) {
                case SIMPLE:
                    return unassignedVariables.size() > 0 ? unassignedVariables.remove(0) : null;
            }
            return null;
        }

        public List<Paint> orderDomain(Graph.Vertex variable) {
            stats.orderDomainCallCount++;
            switch (odvType) {
                case SIMPLE:
                    return domains.get(variable);
            }
            return null;
        }

        public List<Assignment> getAssignments() {
            return assignments;
        }

        public AssignmentsState addAssignment(Assignment assignment) {

            if (this.assignments.contains(assignment)) {
                stats.failedAssignmentCount++;
                return null;
            }

            if (CSP.isConsistent(assignment, assignments)) {
                // Bu state'in bu variable'inin domaininden atanan renk 
                // burada değil backtrack'in içinde çıkarılıyor

                // Bu state'ten Yeni bir state oluştur
                AssignmentsState newState = new AssignmentsState(this);
                // Yeni state'e ilgili atamayı ekle
                newState.assignments.add(assignment);
                newState.actionAssignment = assignment;
                stats.successfulAssignmentCount++;
                return newState;
            } else {
                stats.failedAssignmentCount++;
                return null;
            }

        }

        public boolean checkComplete() {
            stats.checkIsCompleteCount++;
            // Bütün atamalar yapılmış mı?
            if (unassignedVariables.size() > 0) {

                return false;
            }

            // Consistency check:
            for (Assignment asg : assignments) {
                // Bütün atamaların komşuları
                List<Vertex> neighbours = asg.variable.getNeighbours();
                for (Assignment asg2 : assignments) {
                    // Bütün atamalarda
                    for (Vertex neighbour : neighbours) {
                        // Komşular
                        if (asg2.variable.equals(neighbour) && asg2.color.equals(asg.color)) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }

        public boolean forwardCheck() throws Exception {

            if (actionAssignment == null) { // Atama yapmadan forwardCheck yapılmak istenirse hata vermek amaçlı
                throw new Exception("Forward check yanlış yerde kullanılmış, backtrack algoritmasını kontrol edin");
            }

            boolean inference = true;

            //komşuların domainlerini revize et, boşalan varsa false dön
            List<Vertex> neighbours = actionAssignment.variable.getNeighbours();

            //Map<Vertex, List<Paint>> domainsClone = new HashMap<>(domains);
            
           
            for (Vertex neighbour : neighbours) { // Komşulardan    
                if (unassignedVariables.contains(neighbour)) { // Atanmamış olanlara bak
                    
                    List<Paint> neighbourDomain = domains.get(neighbour);


                    if (neighbourDomain.contains(actionAssignment.color)) {

                        if (neighbourDomain.size() == 1) { // komşu domain'in son kalan elemanı bu renk ise FC: false
                            inference = false;
                        }
                        if (activeForwardChecking) { // FC kullanılamayacak renkleri domainlerden silsin mi?
                            neighbourDomain.remove(actionAssignment.color);
                        }


                    }
                }

            }


            return inference;
        }

        @Override
        public String toString() {

            String outStr = "";

            int i = 0;
            for (Assignment asg : this.assignments) {
                i++;
                Colorizer.Node element = (Colorizer.Node) asg.variable.getElement();
                outStr += (i + ". " + element.toString() + " " + asg.color + "\r\n");
            }

            return outStr;
        }
    }

    public CSP(Graph graph, int colorCount, SUVType suvType, ODVType odvType, boolean forwardChecking) {
        this.suvType = suvType;
        this.odvType = odvType;
        this.stats = new Stats();
        this.forwardChecking = forwardChecking;

        Queue<Paint> colors = new LinkedList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.FUCHSIA);
        colors.add(Color.CYAN);

        /*
         * Random rndm = new Random(); Queue<Color> colors = new LinkedList<>();
         * for (int i=0; i<colorCount;i++) { Color clr = new
         * Color(rndm.nextDouble(),rndm.nextDouble(),rndm.nextDouble(),1.0);
         * //red,green,blue,opacity while
         * (clr.getRed()+clr.getGreen()+clr.getBlue()<1.2) clr = clr.brighter();
         * while (clr.getRed()+clr.getGreen()+clr.getBlue()>2.8) clr =
         * clr.darker(); clr = clr.saturate(); while (colors.contains(clr)) clr
         * = new
         * Color(rndm.nextDouble(),rndm.nextDouble(),rndm.nextDouble(),1.0);
         * colors.add(clr); }
         */

        this.standartDomain = new LinkedList<>();

        for (int i = 0; i < colorCount; i++) {
            standartDomain.add(colors.poll());
        }

        this.graph = graph;
        this.vertexCount = graph.getVertices().size();
        this.colorCount = colorCount;





    }

    public static boolean isConsistent(Assignment assignment, List<Assignment> assignments) {
        checkIsConsistentCount++;
        List<Vertex> neighbours = assignment.variable.getNeighbours();
        for (Assignment asg : assignments) {
            // Yapılmış olan bütün atamaları gez
            for (Graph.Vertex neighbour : neighbours) {
                // Verilen atamada kullanılan köşenin tüm komşularını gez
                // Komşuya yapılmış bir atama var ve bu atama verilen atamadaki vertex ile aynı ise constraint sağlanmaz.
                if (asg.variable.equals(neighbour) && asg.color.equals(assignment.color)) {
                    return false;
                }
            }
        }
        return true;
    }

    public CSP.AssignmentsState backTrack() {
        AssignmentsState result = backTrack(new AssignmentsState());
        System.out.println(result.domains);
        return result;
    }

    public AssignmentsState backTrack(AssignmentsState state) {
        try {
            if (state.checkComplete()) {
                return state;
            }
            // Verilen state ait atanmamış değişken ve domainlerden seç
            Graph.Vertex variable = state.selectUnassigned();
            List<Paint> domain = state.orderDomain(variable);


            Iterator<Paint> parentStateIterator = domain.iterator();
            while (parentStateIterator.hasNext()) {

                Paint value = parentStateIterator.next();




                Assignment assignment = new Assignment(variable, value);
                parentStateIterator.remove(); // Atama uygun olsa da olmasada domainden rengi çıkar
                
                AssignmentsState newState = state.addAssignment(assignment);
                 
                if (newState != null) {

                    //Inference
                    if (forwardChecking) {
                        if (newState.forwardCheck()) {
                            // atama olduysa burdan yürü
                            return backTrack(newState);
                        } else {
                            stats.forwardCheckFailure++;
                        }
                    } else {
                        // atama olduysa burdan yürü
                        return backTrack(newState);
                    }

                }


                stats.revertCount++;

            }
            System.out.println("Olmuyordu, zorlamadim..");

        } catch (Exception e) {
            System.out.println("HATA: " + e.getMessage());
            System.exit(1);
        }
        return state;
    }
//    private boolean domainRevise(Graph.Edge arc) {
//        boolean revised = false;
//        List<Paint> startDomain = domains.get(arc.getStart());
//        List<Paint> finishDomain = domains.get(arc.getFinish());
//
//        Iterator<Paint> it = startDomain.iterator();
//        while (it.hasNext()) {
//            Paint value = it.next();
//            if (finishDomain.contains(value)) {
//                it.remove();
//                revised = true;
//            }
//        }
//        return revised;
//    }
}
