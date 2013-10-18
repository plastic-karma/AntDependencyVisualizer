package antgraphvisualizer

import javax.swing.JFrame
import javax.swing.JScrollPane
import java.io.File
import com.mxgraph.view.mxGraph
import com.mxgraph.swing.mxGraphComponent
import com.mxgraph.layout.mxIGraphLayout
import com.mxgraph.layout.mxFastOrganicLayout
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout

object Main {

  def main(args: Array[String]): Unit = {
//    val dependencies = DependencyCalculator.getDependencies(
//    									    str => scala.xml.XML.loadFile(new File("/Users/benjaminrogge/workspace-export/Build", str)),
//  											"build_posy_batch.xml",
//  											"build_all_posy_batch"
  											val dependencies = DependencyCalculator.getDependencies(
  													str => scala.xml.XML.loadFile(new File("./testfiles", str)),
  													"build1.xml",
  													"T1"
	)
  	val graph = antgraphvisualizer.AntJGraphAdapter.generateJGraph(dependencies)

 
 
    //val graphLayout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_TILT, 400, 400)

  
    new SampleGraph(graph)                          
  }
  
  class SampleGraph(graph: mxGraph) extends JFrame {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
	
        val graphLayout = new mxHierarchicalLayout(graph);

        // layout using morphing
        graph.getModel().beginUpdate();
        graphLayout.execute(graph.getDefaultParent());
        graph.getModel().endUpdate()
	
	val graphComponent = new mxGraphComponent(graph);
	getContentPane().add(new JScrollPane(graphComponent))
	pack()
	setVisible(true)
  }
}