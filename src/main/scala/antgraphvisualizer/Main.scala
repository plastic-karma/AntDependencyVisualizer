package antgraphvisualizer

import com.jgraph.layout.graph.JGraphSimpleLayout
import com.jgraph.layout.JGraphFacade
import org.jgraph.JGraph
import javax.swing.JFrame
import javax.swing.JScrollPane
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout

object Main {

  def main(args: Array[String]): Unit = {
    val dependencies = DependencyCalculator.getDependencies(
  											"C:/Source/WorkspacePosy/Build",
  											"build_posy_batch.xml",
  											"build_all_posy_batch"
  )


 
 
 	val graph = antgraphvisualizer.AntJGraphAdapter.generateJGraph(dependencies)

 
 
   //val graphLayout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_TILT, 400, 400)
   val graphLayout = new JGraphHierarchicalLayout
   val graphFacade = new JGraphFacade(graph)     
   graphLayout.run(graphFacade);
	 val nestedMap = graphFacade.createNestedMap(true, true)

	 graph.getGraphLayoutCache().edit(nestedMap)
  
  new SampleGraph(graph)                          
  
     
    
    
    
  }
  
  class SampleGraph(graph: JGraph) extends JFrame {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
	getContentPane().add(new JScrollPane(graph))
	pack()
	setVisible(true)
}

}