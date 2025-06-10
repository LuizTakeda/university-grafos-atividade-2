import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

public class Graph {
  // Estrutura que armazena os vértices do grafo, mapeando o ID para o objeto
  // Vertex
  private TreeMap<String, Vertex> vertices;

  // Cria um grafo a partir de um arquivo GEXF.
  public Graph(String path) {
    // TreeMap ordenado numericamente pelas chaves (que são Strings representando
    // números)
    this.vertices = new TreeMap<>((a, b) -> {
      return Float.compare(Float.parseFloat(a), Float.parseFloat(b));
    });

    // Cria o parser de XML
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    try {
      File file = new File(path);
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(file);
      doc.getDocumentElement().normalize(); // Normaliza o documento XML

      // Lê todos os nós <node> do GEXF
      NodeList nodeList = doc.getElementsByTagName("node");

      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        // Extrai os atributos id e label do nó
        String id = node.getAttributes().getNamedItem("id").getNodeValue();
        String name = node.getAttributes().getNamedItem("label").getNodeValue();
        // Cria um novo vértice e adiciona ao mapa
        this.vertices.put(id, new Vertex(id, name));
      }

      // Lê todos os elementos <edge> (arestas)
      NodeList edgeList = doc.getElementsByTagName("edge");

      for (int i = 0; i < edgeList.getLength(); i++) {
        Node edge = edgeList.item(i);

        // Extrai os IDs de origem e destino da aresta
        String sourceId = edge.getAttributes().getNamedItem("source").getNodeValue();
        String targetId = edge.getAttributes().getNamedItem("target").getNodeValue();

        // Obtém os vértices correspondentes
        Vertex sourceVertex = this.vertices.get(sourceId);
        Vertex targetVertex = this.vertices.get(targetId);

        // Adiciona a aresta nos dois sentidos (grafo não direcionado)
        this.vertices.get(sourceId).addEdge(new EdgeTo(targetVertex));
        this.vertices.get(targetId).addEdge(new EdgeTo(sourceVertex));
      }
    } catch (Exception e) {
      e.printStackTrace(); // Em caso de erro, imprime o stack trace
    }
  }

  // Retorna o número total de vértices
  public int getLength() {
    return this.vertices.size();
  }

  // Retorna um vértice específico a partir do seu ID
  public Vertex getVertex(String id) {
    return this.vertices.get(id);
  }

  // Retorna o conjunto de todos os IDs de vértices
  public Set<String> getVerticesId() {
    return this.vertices.keySet();
  }

  // Retorna todos os objetos Vertex presentes no grafo
  public Collection<Vertex> getVertices() {
    return this.vertices.values();
  }

  // Representação em string do grafo (baseado no TreeMap de vértices)
  @Override
  public String toString() {
    return this.vertices.toString();
  }
}
