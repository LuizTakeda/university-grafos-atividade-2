// Classe que representa uma aresta direcionada para um vértice
public class EdgeTo {
  // Vértice de destino da aresta
  private Vertex vertex;

  // Construtor: inicializa a aresta com o vértice de destino
  public EdgeTo(Vertex vertex) {
    this.vertex = vertex;
  }

  // Retorna o vértice de destino da aresta
  public Vertex getVertex() {
    return this.vertex;
  }

  // Representação textual da aresta (nome do vértice de destino)
  @Override
  public String toString() {
    return this.vertex.getName();
  }
}
