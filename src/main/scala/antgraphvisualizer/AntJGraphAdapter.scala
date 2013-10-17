package antgraphvisualizer

import org.jgraph.JGraph
import org.jgraph.graph.DefaultGraphCell
import org.jgraph.graph.DefaultGraphModel
import org.jgraph.graph.GraphConstants
import org.jgraph.graph.DefaultEdge

object AntJGraphAdapter {

  def generateJGraph(dependencies: Map[String, List[String]]): JGraph = {
    def createGraph(cells: List[DefaultGraphCell]): JGraph = {
      val graph = new JGraph(new DefaultGraphModel)
      cells.foreach (cell => graph.getGraphLayoutCache().insert(cell))
      graph
    }
    
    def createVertex(name: String): DefaultGraphCell = {
      val vertex = new DefaultGraphCell(name)
      vertex.addPort
      GraphConstants.setAutoSize(vertex.getAttributes(), true)
      vertex
    }
    
    def createEdge(source: DefaultGraphCell, target: DefaultGraphCell): DefaultGraphCell = {
        val edge = new DefaultEdge() 
        GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_CLASSIC);
        GraphConstants.setEndFill(edge.getAttributes(), true);
        edge.setSource(source.getChildAt(0))
        edge.setTarget(target.getChildAt(0))
        edge
    }
    
    def generateJGraphInternal(dependencies: Map[String, List[String]], vertecies : Map[String, DefaultGraphCell], edges: List[DefaultGraphCell]): JGraph = {
      if (dependencies.isEmpty) createGraph(edges ++ vertecies.values)
      else {
    	  val (source, targets) = dependencies.head
    	  if (targets.isEmpty) generateJGraphInternal(dependencies - source, vertecies, edges)
    	  else {
    		  val sourceCell = vertecies getOrElse(source, createVertex(source))
    		  val targetCell = createVertex(targets.head)
    		  generateJGraphInternal(dependencies + (source -> targets.tail), vertecies + (source -> sourceCell, targets.head -> targetCell), createEdge(sourceCell, targetCell) :: edges)
    	  }
        
      }
    }
    generateJGraphInternal(dependencies, Map(), List())
  }
}