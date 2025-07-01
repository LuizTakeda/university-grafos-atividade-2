import java.util.*;

/**
 * Calcula e imprime a Centralidade de Intermediação (Betweenness Centrality) de cada vértice de um grafo.
 *
 * A Betweenness Centrality mede a importância de um vértice com base em quantos
 * caminhos mínimos entre pares de outros vértices passam por ele. Ou seja, quantas vezes
 * um vértice age como um “ponte” ao longo do caminho mais curto entre dois outros vértices.
 *
 * Essa métrica é útil para identificar vértices que controlam o fluxo de informações ou
 * recursos em uma rede.
 */
public class BetweennessCentrality {
  private final Graph graph;
  private final Map<Vertex, Float> bc = new HashMap<>(); // Armazena o valor da centralidade de cada vértice

  public BetweennessCentrality(Graph graph) {
    this.graph = graph;

    // Inicializa a centralidade de todos os vértices com 0
    for (Vertex v : graph.getVertices()) {
      bc.put(v, 0.0f);
    }

    // Executa o algoritmo de Brandes
    computeBrandes();

    // Imprime os resultados
    printResults();
  }

  /**
   * Implementação do algoritmo de Brandes para cálculo da Betweenness Centrality.
   */
  private void computeBrandes() {
    for (Vertex source : graph.getVertices()) {
      Stack<Vertex> stack = new Stack<>();
      Map<Vertex, List<Vertex>> predecessors = new HashMap<>();
      Map<Vertex, Integer> shortestPaths = new HashMap<>();
      Map<Vertex, Integer> distance = new HashMap<>();
      Queue<Vertex> queue = new LinkedList<>();

      for (Vertex v : graph.getVertices()) {
        predecessors.put(v, new ArrayList<>());
        shortestPaths.put(v, 0);
        distance.put(v, -1);
      }

      shortestPaths.put(source, 1);
      distance.put(source, 0);
      queue.add(source);

      // Fase 1: Busca em largura (BFS) para encontrar distâncias e predecessores
      while (!queue.isEmpty()) {
        Vertex v = queue.poll();
        stack.push(v);

        for (EdgeTo edge : v.getEdges()) {
          Vertex w = edge.getVertex();

          if (distance.get(w) < 0) {
            queue.add(w);
            distance.put(w, distance.get(v) + 1);
          }

          if (distance.get(w) == distance.get(v) + 1) {
            shortestPaths.put(w, shortestPaths.get(w) + shortestPaths.get(v));
            predecessors.get(w).add(v);
          }
        }
      }

      // Fase 2: Acumulação de dependências
      Map<Vertex, Float> dependencies = new HashMap<>();
      for (Vertex v : graph.getVertices()) {
        dependencies.put(v, 0.0f);
      }

      while (!stack.isEmpty()) {
        Vertex w = stack.pop();

        for (Vertex v : predecessors.get(w)) {
          float ratio = (float) shortestPaths.get(v) / shortestPaths.get(w);
          float delta = ratio * (1 + dependencies.get(w));
          dependencies.put(v, dependencies.get(v) + delta);
        }

        if (!w.equals(source)) {
          bc.put(w, bc.get(w) + dependencies.get(w));
        }
      }
    }

    // Ajuste final: para grafos não direcionados, divide por 2 (valores são contados em duplicidade)
    int n = graph.getVertices().size();
    if (n > 2) {
      for (Vertex v : bc.keySet()) {
        bc.put(v, bc.get(v) / 2.0f);
      }
    } else {
      for (Vertex v : bc.keySet()) {
        bc.put(v, 0.0f);
      }
    }
  }

  /**
   * Imprime os valores de centralidade de cada vértice, ordenados alfabeticamente pelo nome.
   */
  private void printResults() {
    System.out.println("Betweenness Centrality:");

    TreeMap<String, Float> sorted = new TreeMap<>();
    for (Map.Entry<Vertex, Float> entry : bc.entrySet()) {
      sorted.put(entry.getKey().getName(), entry.getValue());
    }

    for (Map.Entry<String, Float> entry : sorted.entrySet()) {
      System.out.printf("BC(%s) = %.4f\n", entry.getKey(), entry.getValue());
    }
  }
}
