import java.util.ArrayList;

public class Vertex {

  // Lista de arestas que partem deste vértice
  private ArrayList<EdgeTo> edges;
  
  // Identificador único do vértice (usado internamente)
  private String id;
  
  // Nome do vértice (pode ser o rótulo usado no GEXF)
  private String name;

  // Construtor com a lista de arestas (opcional)
  public Vertex(String id, String name, ArrayList<EdgeTo> edges) {
    this.edges = edges;
    this.id = id;
    this.name = name;
  }

  // Construtor padrão (inicializa lista de arestas vazia)
  public Vertex(String id, String name) {
    this.edges = new ArrayList<>();
    this.id = id;
    this.name = name;
  }

  // Adiciona uma nova aresta a este vértice
  public void addEdge(EdgeTo edge) {
    this.edges.add(edge);
  }

  // Retorna uma aresta específica pelo índice
  public EdgeTo getEdge(int index) {
    return this.edges.get(index);
  }

  // Retorna todas as arestas deste vértice
  public ArrayList<EdgeTo> getEdges() {
    return this.edges;
  }

  // Retorna o nome do vértice
  public String getName() {
    return this.name;
  }

  // Representação em string do vértice e suas conexões
  @Override
  public String toString() {
    return this.name + "->" + this.edges.toString();
  }
}
