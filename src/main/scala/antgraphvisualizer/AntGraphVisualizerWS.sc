import org.jgraph.JGraph
import scala.xml.NodeSeq
import java.io.File
import antgraphvisualizer.AntGraphVisualizer
import org.jgraph.graph.DefaultGraphModel
import javax.swing.JFrame
import javax.swing.JScrollPane
import org.jgraph.graph.DefaultGraphCell
import org.jgraph.graph.GraphLayoutCache
import org.jgraph.graph.DefaultCellViewFactory
import org.jgraph.graph.GraphConstants
import java.awt.geom.Rectangle2D
import com.jgraph.layout.graph.JGraphSimpleLayout
import com.jgraph.layout.JGraphFacade
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout
import com.jgraph.layout.simple.SimpleGridLayout
import org.jgraph.graph.DefaultEdge

object AntGraphVisualizerWS {


  val dependencies = AntGraphVisualizer.getDependencies(
  											"/Users/benjaminrogge/progfun-workspace/AntGraphVisualizer/testfiles",
  											"build1.xml",
  											"T1"
  )                                               //> dependencies  : List[(String, String)] = List((T1,T3), (T1,T2), (T2,T4), (T3
                                                  //| ,T5))

 
 
 	val graph = antgraphvisualizer.AntJGraphAdapter.generateJGraph(dependencies)
                                                  //> graph  : org.jgraph.JGraph = org.jgraph.JGraph[,0,0,0x0,invalid,alignmentX=0
                                                  //| .0,alignmentY=0.0,border=,flags=16777577,maximumSize=,minimumSize=,preferred
                                                  //| Size=,editable=true,invokesStopCellEditing=false]
 
 
   val graphLayout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_RANDOM, 100, 100)
                                                  //> graphLayout  : com.jgraph.layout.graph.JGraphSimpleLayout = Random
   val graphFacade = new JGraphFacade(graph)      //> graphFacade  : com.jgraph.layout.JGraphFacade = com.jgraph.layout.JGraphFac
                                                  //| ade@1e2b1495
   graphLayout.run(graphFacade);
	 val nestedMap = graphFacade.createNestedMap(true, true)
                                                  //> nestedMap  : java.util.Map[_, _] = {T1={autosize=true, bounds=org.jgraph.gr
                                                  //| aph.AttributeMap$SerializableRectangle2D[x=0.0,y=15.0,w=20.0,h=20.0]}, T2={
                                                  //| autosize=true, bounds=org.jgraph.graph.AttributeMap$SerializableRectangle2D
                                                  //| [x=31.0,y=14.0,w=20.0,h=20.0]}, T5={autosize=true, bounds=org.jgraph.graph.
                                                  //| AttributeMap$SerializableRectangle2D[x=91.0,y=0.0,w=20.0,h=20.0]}, T4={auto
                                                  //| size=true, bounds=org.jgraph.graph.AttributeMap$SerializableRectangle2D[x=3
                                                  //| 9.0,y=7.0,w=20.0,h=20.0]}, T3={autosize=true, bounds=org.jgraph.graph.Attri
                                                  //| buteMap$SerializableRectangle2D[x=33.0,y=24.0,w=20.0,h=20.0]}}
	 graph.getGraphLayoutCache().edit(nestedMap)
  
  new SampleGraph(graph)                          //> res0: SampleGraph = SampleGraph[frame0,57,22,111x66,invalid,layout=java.awt
                                                  //| .BorderLayout,title=,resizable,normal,defaultCloseOperation=EXIT_ON_CLOSE,r
                                                  //| ootPane=javax.swing.JRootPane[,0,22,111x44,invalid,layout=javax.swing.JRoot
                                                  //| Pane$RootLayout,alignmentX=0.0,alignmentY=0.0,border=,flags=16777673,maximu
                                                  //| mSize=,minimumSize=,preferredSize=],rootPaneCheckingEnabled=true]-
  
  
  
  

	  
  
}

class SampleGraph(graph: JGraph) extends JFrame {
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
	getContentPane().add(new JScrollPane(graph))
	pack()
	setVisible(true)
}