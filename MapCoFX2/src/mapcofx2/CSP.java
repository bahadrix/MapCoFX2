/*
 * Problemin CSP hali
 */
package mapcofx2;

import java.util.LinkedList;
import java.util.List;
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
    
    public List<Assignment> assignments;
    private List<Graph.Vertex> unassignedVariables;
    private Graph graph;

    public CSP(Graph graph, final Colorizer outer) {

        this.graph = graph;
        this.assignments = new LinkedList<>();
        this.unassignedVariables = new LinkedList<>();
        unassignedVariables.addAll(graph.getVertices());
    }

    public class Assignment {

        public Graph.Vertex variable;
        public Paint color;
    }

    public boolean isConsistent(Assignment assignment) {
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

    public boolean isComplete() {
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

    public Vertex selectUnassigned(SUVType type) {
        switch (type) {
            case SIMPLE:
                return unassignedVariables.size() > 0 ? unassignedVariables.remove(0) : null;
        }
        return null;
    }
    
    public List<Assignment> orderDomain(ODVType type) {
        switch (type) {
            case SIMPLE:
                
        }
        return null;
    }
}
