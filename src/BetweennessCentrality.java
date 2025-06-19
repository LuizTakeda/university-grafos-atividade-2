import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class BetweennessCentrality {
  private Graph graph;

  private TreeMap<Vertex, Float> crossQuantity;
  private TreeMap<Vertex, Integer> pathQuantity;

  public BetweennessCentrality(Graph graph) {
    this.graph = graph;
    this.crossQuantity = new TreeMap<>();
    this.pathQuantity = new TreeMap<>();

    this.logic();
  }

  private void logic() {
    Vertex[] vertices = this.graph.getVertices().toArray(new Vertex[0]);

    for (Vertex v : vertices) {
      this.crossQuantity.put(v, 0F);
      this.pathQuantity.put(v, 0);
    }

    List<List<Vertex>> paths = new ArrayList<>();

    for (int sourceIndex = 0; sourceIndex < vertices.length - 1; sourceIndex++) {
      Vertex sourceVertex = vertices[sourceIndex];

      // Calcula a árvore de busca do BFS
      Set<Vertex> visit = new HashSet<>();
      Queue<Vertex> queue = new LinkedList<>();

      Map<Vertex, Integer> distance = new HashMap<>();
      Map<Vertex, List<Vertex>> predecessors = new HashMap<>();

      queue.add(sourceVertex);
      distance.put(sourceVertex, 0);
      predecessors.put(sourceVertex, new ArrayList<>()); // a origem não tem predecessores

      while (!queue.isEmpty()) {
        Vertex v = queue.poll();

        if (visit.contains(v)) {
          continue;
        }

        visit.add(v);
        int currentDist = distance.get(v);

        for (EdgeTo edge : v.getEdges()) {
          Vertex neighbor = edge.getVertex();

          int newDist = currentDist + 1;

          if (!distance.containsKey(neighbor)) {
            distance.put(neighbor, newDist);

            predecessors.put(neighbor, new ArrayList<>());
            predecessors.get(neighbor).add(v);

            queue.add(neighbor);
            continue;
          }

          if (distance.get(neighbor) == newDist) {
            // Outro caminho com mesma distância mínima
            predecessors.get(neighbor).add(v);
          }
        }
      }

      // Calculate the minimal paths
      for (int targetIndex = sourceIndex + 1; targetIndex < vertices.length; targetIndex++) {
        Vertex targetVertex = vertices[targetIndex];
        // System.out.print(vertices[targetIndex].getName());
        // this.encontrarCaminhosMinimos(targetVertex, sourceVertex, predecessors);
        List<List<Vertex>> revarsePaths = new ArrayList<>();
        List<Vertex> currentPath = new ArrayList<>();
        this._makePaths(targetVertex, sourceVertex, predecessors, currentPath, revarsePaths);

        for (List<Vertex> path : revarsePaths) {
          Collections.reverse(path); // It is in reverse
          paths.add(path);
        }
      }
    }

    System.out.printf("Geodesic Quantity: %d\n", paths.size());

    System.out.println("Minimal paths");
    for (List<Vertex> minimalPath : paths) {
      System.out.println(minimalPath);
    }

    for (Vertex v : vertices) {
      int totalPathsExcludingExtremes = 0;

      for (List<Vertex> minimalPath : paths) {
        if (minimalPath.get(0) == v || minimalPath.get(minimalPath.size() - 1) == v) {
          continue;
        }

        totalPathsExcludingExtremes++;
      }

      this.pathQuantity.put(v, totalPathsExcludingExtremes);
    }

    for (List<Vertex> minimalPath : paths) {
      if (minimalPath.size() < 2)
        continue;

      for (int i = 1; i < minimalPath.size() - 1; i++) {
        Vertex v = minimalPath.get(i);
        this.crossQuantity.put(v, this.crossQuantity.get(v) + 1);
      }
    }

    for (Map.Entry<Vertex, Integer> entry : this.pathQuantity.entrySet()) {
      Vertex v = entry.getKey();
      int pathQuantity = entry.getValue();
      float cross = this.crossQuantity.get(v);

      float bc = pathQuantity > 0 ? cross / pathQuantity : 0;

      float normalization = (vertices.length - 1) * (vertices.length - 2) / 2.0f;
      float normalizedBC = bc / normalization;

      System.out.printf("BC (%s) %.4f (%f/%d) normalized %f\n", v.getName(), bc, cross, pathQuantity, normalizedBC);

    }
  }

  private void _makePaths(
      Vertex current, Vertex source,
      Map<Vertex, List<Vertex>> predecessors,
      List<Vertex> currentPath,
      List<List<Vertex>> allPaths) {

    currentPath.add(current);

    if (current.equals(source)) {
      allPaths.add(new ArrayList<>(currentPath));
    } else {
      List<Vertex> parents = predecessors.get(current);

      if (parents != null) {
        for (Vertex parent : parents) {
          this._makePaths(parent, source, predecessors, currentPath, allPaths);
        }
      }
    }

    currentPath.remove(currentPath.size() - 1);
  }
}

class Pair<T> {
  private final T first;
  private final T second;

  public Pair(T first, T second) {
    this.first = first;
    this.second = second;
  }

  public T getFirst() {
    return first;
  }

  public T getSecond() {
    return second;
  }

  @Override
  public String toString() {
    return "(" + first + ", " + second + ")";
  }
}