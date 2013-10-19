package antgraphvisualizer

import javax.swing.JFrame
import javax.swing.JScrollPane
import java.io.File
import com.mxgraph.view.mxGraph
import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.layout.mxIGraphLayout
import com.mxgraph.layout.mxFastOrganicLayout
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout
import com.mxgraph.layout.mxCircleLayout
import com.mxgraph.layout.mxOrganicLayout
import com.mxgraph.layout.mxStackLayout

object Main {

  def main(args: Array[String]): Unit = {
    val startTimeDependencies = System.currentTimeMillis()
    val dependencies = DependencyCalculator.getDependencies(
        str => scala.xml.XML.loadFile(new File(args(0), str)),
        args(1),
        args(2))
    val endTimeDependencies = System.currentTimeMillis()
  	val graph = antgraphvisualizer.AntJGraphAdapter.generateJGraph(dependencies)
  	val endTimeGraph = System.currentTimeMillis()
  	val graphLayout = new mxHierarchicalLayout(graph);
    graph.getModel().beginUpdate();
    graphLayout.execute(graph.getDefaultParent());
    graph.getModel().endUpdate()
    val endTimeLayout = System.currentTimeMillis()
    
    println("Dependency time: " + (endTimeDependencies - startTimeDependencies) / 1000.0)
    println("mxGraph    time: " + (endTimeGraph - endTimeDependencies) / 1000.0)
    println("layout     time: " + (endTimeLayout - endTimeGraph) / 1000.0)
    
    
    
    
    new SampleGraph(graph)                          
  }
  
  class SampleGraph(graph: mxGraph) extends JFrame {
  	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)




	val graphComponent = new mxGraphComponent(graph);
	getContentPane().add(new JScrollPane(graphComponent))
	pack()
	setVisible(true)
  }
}