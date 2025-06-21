import java.util.*;

public class BetweennessCentrality {
  private final Graph graph;
  // Map para armazenar a Centralidade de Intermediação calculada para cada
  // vértice
  private final Map<Vertex, Float> bc = new HashMap<>();

  public BetweennessCentrality(Graph graph) {
    this.graph = graph;
    // Inicializa BC para todos os vértices como 0.0f
    for (Vertex v : graph.getVertices()) {
      bc.put(v, 0F);
    }
    // Chama o método para computar a Centralidade de Intermediação usando o
    // algoritmo de Brandes
    computeBrandes();
    // Imprime os resultados
    printResults();
  }

  /**
   * Implementa o algoritmo de Brandes para calcular a Centralidade de
   * Intermediação.
   */
  private void computeBrandes() {
    // Itera sobre cada vértice do grafo, usando-o como o vértice 'source' (s)
    for (Vertex source : graph.getVertices()) {
      // Pilha para armazenar os vértices na ordem em que são processados pela BFS
      // (para a fase de acumulação)
      Stack<Vertex> stack = new Stack<>();
      // Mapa para armazenar os predecessores de cada vértice em caminhos mínimos a
      // partir de 'source'
      Map<Vertex, List<Vertex>> predecessors = new HashMap<>();
      // Mapa para armazenar o número de caminhos mínimos de 'source' para cada
      // vértice
      Map<Vertex, Integer> shortestPaths = new HashMap<>();
      // Mapa para armazenar a distância (número de arestas) de 'source' para cada
      // vértice
      Map<Vertex, Integer> distance = new HashMap<>();
      // Fila para a travessia BFS
      Queue<Vertex> queue = new LinkedList<>();

      // Inicializa as estruturas de dados para a BFS
      for (Vertex v : graph.getVertices()) {
        predecessors.put(v, new ArrayList<>()); // Cada vértice começa sem predecessores
        shortestPaths.put(v, 0); // Número de caminhos mínimos inicializado em 0
        distance.put(v, -1); // Distância inicializada em -1 (não visitado)
      }

      // Configura o vértice 'source' para a BFS
      shortestPaths.put(source, 1); // Há 1 caminho mínimo de 'source' para si mesmo
      distance.put(source, 0); // Distância de 'source' para si mesmo é 0
      queue.add(source); // Adiciona 'source' à fila para iniciar a BFS

      // Fase 1: BFS para encontrar caminhos mínimos, distâncias e predecessores
      while (!queue.isEmpty()) {
        Vertex v = queue.poll(); // Remove o vértice atual da fila
        stack.push(v); // Adiciona o vértice à pilha para a fase de acumulação

        // Explora os vizinhos de 'v'
        for (EdgeTo edge : v.getEdges()) {
          Vertex w = edge.getVertex(); // Vizinho 'w'

          // Se 'w' ainda não foi visitado (distância < 0)
          if (distance.get(w) < 0) {
            queue.add(w); // Adiciona 'w' à fila
            distance.put(w, distance.get(v) + 1); // Define a distância de 'w'
          }

          // Se 'w' foi alcançado por um caminho mínimo passando por 'v'
          // (ou seja, a distância para 'w' é a distância para 'v' + 1)
          if (distance.get(w) == distance.get(v) + 1) {
            // Incrementa o número de caminhos mínimos para 'w' com base nos caminhos para
            // 'v'
            shortestPaths.put(w, shortestPaths.get(w) + shortestPaths.get(v));
            predecessors.get(w).add(v); // Adiciona 'v' como predecessor de 'w'
          }
        }
      }

      // Mapa para armazenar as dependências acumuladas para cada vértice em relação
      // ao 'source' atual
      Map<Vertex, Float> dependencies = new HashMap<>();
      for (Vertex v : graph.getVertices()) {
        dependencies.put(v, 0F); // Inicializa todas as dependências como 0
      }

      // Fase 2: Acumulação de dependências (processando os vértices da pilha, do mais
      // distante ao mais próximo da 'source')
      while (!stack.isEmpty()) {
        Vertex w = stack.pop(); // Remove o vértice 'w' da pilha

        // Para cada predecessor 'v' de 'w' em um caminho mínimo de 'source'
        for (Vertex v : predecessors.get(w)) {
          // Calcula a proporção de caminhos mínimos de 'source' para 'w' que passam por
          // 'v'
          float ratio = (float) shortestPaths.get(v) / shortestPaths.get(w);
          // Calcula a 'delta' de dependência que 'w' contribui para 'v'
          float delta = ratio * (1 + dependencies.get(w));
          // Adiciona essa 'delta' à dependência acumulada de 'v'
          dependencies.put(v, dependencies.get(v) + delta);
        }

        // Se 'w' não é o vértice 'source' (pois 'source' não é um vértice intermediário
        // para si mesmo)
        if (!w.equals(source)) {
          // Adiciona a dependência acumulada de 'w' à sua Centralidade de Intermediação
          // total
          // Para grafos não direcionados, o algoritmo Brandes acumula o dobro do valor
          // real da BC,
          // mas o ajuste final na normalização cuidará disso.
          bc.put(w, bc.get(w) + dependencies.get(w));
        }
      }
    }

    // --- INÍCIO DA NORMALIZAÇÃO CORRIGIDA ---
    int n = graph.getVertices().size();

    // Para grafos com 0, 1 ou 2 vértices, não há caminhos intermediários, então BC
    // é 0.
    // Além disso, evita divisão por zero.
    if (n <= 2) {
      for (Vertex v : bc.keySet()) {
        bc.put(v, 0.0F);
      }
      return;
    }

    // Para grafos não direcionados, o algoritmo de Brandes calcula 2 * C_B(v) (o
    // dobro do valor correto).
    // A normalização máxima possível para C_B(v) é (n-1)*(n-2)/2.
    // Portanto, para normalizar o valor acumulado (que é o dobro) e obter um
    // resultado entre [0,1],
    // precisamos dividir o valor acumulado pelo dobro do valor máximo, que é
    // (n-1)*(n-2).
    float normalizationDivisor = (float) (n - 1) * (n - 2);

    for (Vertex v : bc.keySet()) {
      bc.put(v, bc.get(v) / normalizationDivisor);
    }
    // --- FIM DA NORMALIZAÇÃO CORRIGIDA ---
  }

  /**
   * Imprime os resultados da Centralidade de Intermediação.
   */
  private void printResults() {
    System.out.println("Betweenness Centrality:");
    // Usa um TreeMap para ordenar a saída pelo nome do vértice, se o Vertex for
    // Comparable
    // Ou, se Vertex não for Comparable, um HashMap ou ordenar a lista de EntrySet.
    // Para garantir a ordenação na impressão, vamos copiar para um TreeMap
    // temporário
    // ou ordenar uma lista de pares para impressão.

    // Se Vertex.getName() é único e você quer ordenar por ele:
    TreeMap<String, Float> sortedBc = new TreeMap<>();
    for (Map.Entry<Vertex, Float> entry : bc.entrySet()) {
      sortedBc.put(entry.getKey().getName(), entry.getValue());
    }

    for (Map.Entry<String, Float> entry : sortedBc.entrySet()) {
      System.out.printf("BC(%s) = %.4f\n", entry.getKey(), entry.getValue());
    }
  }
}