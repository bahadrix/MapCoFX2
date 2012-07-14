/*
 * Çizge implementasyonu
 */
package mapcofx2;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Adjecency List ile implemente edilmistir
 *
 * @author Bahadir
 */
public class Graph<E> {

    private List<Vertex> Vertices = new LinkedList<>();
    private List<Edge> Edges = new LinkedList<>();

    // Structs
    public class Vertex {

        private E element;
        public List<Edge> incidents;

        public Vertex(E data) {
            this.element = data;
            this.incidents =  new LinkedList<>();
        }

        public E getElement() {
            return element;
        }
        
        public List<Vertex> getNeighbours() {
            List<Vertex> neighbours = new LinkedList<>();
            
            for(Edge edge : incidents) {
                neighbours.add(edge.getOpposite(this));
            }
            return neighbours;
        }

        public boolean areAdjecent(Vertex v) {
            
            for (Vertex n : v.getNeighbours()) {
                if (n.equals(this))
                    return true;
            }
            
            return false;
        }

        public int neighbourCount() {
            return incidents.size();
        }
      
        
    }

    public class Edge {

        double length;
        private Vertex start;
        private Vertex finish;

        public Edge(double length, Vertex start, Vertex finish) {

            this.length = length;
            this.start = start;
            this.finish = finish;

            start.incidents.add(this);
            finish.incidents.add(this);

        }

        public Vertex getOpposite(Vertex v) {
            if (start.equals(v))
                return finish;
            if (finish.equals(v))
                return start;
            return null;
        }

        public Vertex getFinish() {
            return finish;
        }

        public Vertex getStart() {
            return start;
        }
        
        
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Edge other = (Edge) obj;

            /**
             * 1 --> 2 ile 2 <--- 1 aynı
             */
            return ((Objects.equals(this.start, other.finish) && Objects.equals(this.finish, other.start)) || (Objects.equals(this.start, other.start) && Objects.equals(this.finish, other.finish)));
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 41 * hash + Objects.hashCode(this.start);
            hash = 41 * hash + Objects.hashCode(this.finish);
            return hash;
        }

        
        
        
    }

    // Constructors
    public Graph() {
    }
        
    // Methods
  
    public List<Edge> getEdges() {
        return Edges;
    }

    public List<Vertex> getVertices() {
        return Vertices;
    }

    public void insertVertex(E element) {
        Vertex newVertex = new Vertex(element);

        Vertices.add(newVertex);
    }
    
    
    /**
     * Paralel kose yapilmaya calisiliyorsa false dondurur
     * @param length
     * @param start
     * @param finish
     * @return 
     */
    public boolean insertEdge(double length, Vertex start, Vertex finish) {
        Edge newEdge = new Edge(length, start, finish);
        if (Edges.contains(newEdge))
            return false;
        Edges.add(newEdge);
        return true;
    }
    
    public E removeVertex(Vertex v) throws Exception {
        
        if (!Vertices.remove(v)) {
            throw new Exception("Boyle bir kenar bulunamadigi icin kaldirilamadi");
        }
        
        return v.element;
    }
    
    public double removeEdges(Edge e) {
        // Listeden kaldir
        Edges.remove(e);
        // Incidentlardan sil
        for (Vertex v : Vertices) {
            while (v.incidents.remove(e)) {}
            
        }
        
        return e.length;
    }
    
}
