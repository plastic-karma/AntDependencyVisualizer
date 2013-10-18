package antgraphvisualizer

import scala.language.implicitConversions
import scala.language.postfixOps

import scala.xml.NodeSeq
import java.io.File
import scala.xml.Node

object DependencyCalculator {
  
  /**
   * Extension class for Node for some convenience methods.
   */
  implicit class NodeExtension(node: Node) {
    
    /**
     * Gets the attribute value of the attribute with the given name.
     * Returns empty Option if attribute does not exist
     */
    def getAttributeValue(attributeName: String): Option[String] = {
      val attribute = node \ ("@" + attributeName)
      if (attribute.isEmpty) None
      else Option(attribute.head.text)
    }
    	
    /**
     * Returns true iff an attribute with the given name exists in the node.
     */
    def hasAttribute(attributeName: String): Boolean = {
      return !(node \ ("@" + attributeName) isEmpty)
    }
  }
  
  /**
   * Gets the dependencies of the given target in the given build file. The result is a map in the form of target -> (dependency1,..,dependencyn), 
   * whereas only direct dependencies for a target are listed, however for every dependent target, there is a  distinct entry in the result map.
   */
  def getDependencies(buildFileResolver: String => NodeSeq, buildFileName: String, targetName:String): Map[String, List[String]] = {
     
    /**
     * Maps a Sequence of Nodes to map where the keys are coming from the name attribute and the values are coming from the value attribute 
     */
    def getProperties(nodes: NodeSeq): Map[String, String] = {
      nodes.foldLeft(Map[String, String]())((map, node) => map + ((node.getAttributeValue("name").get -> node.getAttributeValue("value").get)))
    }
    
    
    lazy val PROPERTY_PATTERN = "\\$\\{(.+)\\}".r
    
    /**
     * Replaces property usages with the values.
     */
    def replaceProperties(input: String, properties: Map[String, String]): String = {
	  PROPERTY_PATTERN.replaceAllIn(input, m => properties.getOrElse(m.group(1), ""))
	}
    
	def getAllTargetNodesAndProperties(buildFileName: String, properties: Map[String, String]): (NodeSeq, Map[String, String]) = {
	  val antFile = buildFileResolver(buildFileName) 
	  val properties = getProperties((antFile \ "property").filter(node => node.hasAttribute("name") && node.hasAttribute("value")))
	  											
	  
	  (antFile \ "import").foldLeft[(NodeSeq, Map[String, String])]((antFile \ "target", properties))(
			  					(p, node) => {
			  					  val (ns, properties) = p
			  					  val (newTargets, newProperties) =  getAllTargetNodesAndProperties(
			  					        replaceProperties(node.getAttributeValue("file").get, properties), 
			  					        properties)
			  					  (newTargets ++ ns, newProperties ++ properties)
			  					}
		  					)
	}
	
	/**
	 * Interal recursive function to calculate dependencies.
	 */
	def getDependenciesInternal(targetName: String, targets: NodeSeq): Map[String, List[String]] = {
	  val target = targets.find(currentTarget => currentTarget.attributes.exists(md => md.key == "name" && md.value.text == targetName))
	  assert(target.nonEmpty)
	  val directDepends = target.get.getAttributeValue("depends")
	  if (directDepends isDefined) {
	    val directDependsList = directDepends.get.split(",").map(str => str.trim)
	    directDependsList.foldLeft[Map[String, List[String]]](Map())((map, str) =>
	      map // old Map
	       + (targetName -> (str :: map.getOrElse(targetName, List()))) // The entry of targetName + currentDepend
	      ++ getDependenciesInternal(str, targets)) // Recursive dependencies of currentDepend
	  }
	  else Map()
	}
	
    getDependenciesInternal(targetName, getAllTargetNodesAndProperties(buildFileName, Map())._1)
  }
}