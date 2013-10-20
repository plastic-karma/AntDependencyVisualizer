package antgraphvisualizer

import com.mxgraph.view.mxGraph


object AntJGraphAdapter {

  def generateJGraph(dependencies: Map[String, List[String]]): mxGraph = {
    
    def createVertex(graph: mxGraph, name: String): Object = {
      graph.insertVertex(null, name, name, 10, 10, 70, 20)
    }
    
    def createEdge(graph: mxGraph, source: Object, target: Object): Unit = {
      graph.insertEdge(null, "", "", source, target)
    }
    
    def generateJGraphInternal(graph: mxGraph, dependencies: Map[String, List[String]], vertecies: Map[String, Object]): mxGraph = {
    	if (dependencies.isEmpty) graph
    	else {
    	  val (target, targetDependencies) = dependencies.head
    	  val targetVertex = vertecies.getOrElse(target, createVertex(graph, target))
    	  val dependentVertecies = targetDependencies.foldLeft[Map[String, Object]](Map())((map, currentTarget) => 
    	    map + (currentTarget -> vertecies.getOrElse(currentTarget,createVertex(graph, currentTarget))))
    	  dependentVertecies.values.foreach(v => createEdge(graph, targetVertex, v))
    	  generateJGraphInternal(graph, dependencies.tail, vertecies  + (target -> targetVertex) ++ dependentVertecies)
    	}
    }
    generateJGraphInternal(new mxGraph, dependencies, Map())
  }
}