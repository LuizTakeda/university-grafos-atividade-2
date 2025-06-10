import java.util.Map.Entry;

public class App {
  public static void main(String[] args) throws Exception {
    // Cria o grafo a partir de um arquivo no formato GEXF
    Graph graph = new Graph("data/LesMiserables.gexf");

    // Exibe todos os vértices do grafo
    System.out.println("\nGraph");
    for (Vertex vertex : graph.getVertices()) {
      System.out.println(vertex);
    }

    // Calcula Betweenness Centrality
    BetweennessCentrality betweennessCentrality = new BetweennessCentrality(graph);
  }
}
