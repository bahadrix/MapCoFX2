/*
 * Problemin CSP hali
 */
package mapcofx2;

import java.awt.Transparency;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
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
    private List<Graph.Vertex> unassignedVariables;
    private Graph graph;
    private int vertexCount;
    private int colorCount;
    private List<Paint> standartDomain;
    private SUVType suvType;
    private ODVType odvType;
    private boolean forwardChecking;

    
    public class Assignment {

        public Graph.Vertex variable;
        public Paint color;

        public Assignment(Vertex variable, Paint color) {
            this.variable = variable;
            this.color = color;
        }
    }

    public class AssignmentsState {

        private List<Assignment> assignments;

        public AssignmentsState() {
            this.assignments = new LinkedList<>();
        }

        public AssignmentsState(AssignmentsState cloneFrom) {
            this.assignments = new LinkedList<>(cloneFrom.getAssignments());
        }

        public List<Assignment> getAssignments() {
            return assignments;
        }

        public boolean addAssignment(Assignment assignment) {

            if (this.assignments.contains(assignment)) {
                return false;
            }

            if (CSP.isConsistent(assignment, assignments)) {
                this.assignments.add(assignment);
                return true;
            } else {
                return false;
            }

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

        this.forwardChecking = forwardChecking;
        
        Queue<Paint> colors = new LinkedList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.FUCHSIA);
        colors.add(Color.CYAN);
        


        this.standartDomain = new LinkedList<>();

        for (int i = 0; i < colorCount; i++) {
            standartDomain.add(colors.poll());
        }

        this.graph = graph;
        this.vertexCount = graph.getVertices().size();
        this.colorCount = colorCount;

        this.unassignedVariables = new LinkedList<>();
        unassignedVariables.addAll(graph.getVertices());
    }

    public static boolean isConsistent(Assignment assignment, List<Assignment> assignments) {
        List<Vertex> neighbours = assignment.variable.getNeighbours();
        for (Assignment asg : assignments) {
            // Yapılmış olanan bütün atamaları gez
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

    public boolean checkComplete(List<Assignment> assignments) {
        // Bütün atamalar yapılmış mı?
        if (unassignedVariables.size() > 0) {
            System.out.println("Sayı dutmuyor" + unassignedVariables.size());
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

    public Vertex selectUnassigned() {
        switch (suvType) {
            case SIMPLE:
                return unassignedVariables.size() > 0 ? unassignedVariables.remove(0) : null;
        }
        return null;
    }

    public List<Paint> orderDomain(Graph.Vertex variable) {
        switch (odvType) {
            case SIMPLE:
                return new LinkedList<>(standartDomain);
        }
        return null;
    }


}
