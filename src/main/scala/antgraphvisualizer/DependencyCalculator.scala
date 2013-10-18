package antgraphvisualizer

import scala.language.implicitConversions
import scala.language.postfixOps

import scala.xml.NodeSeq
import java.io.File
import scala.xml.Node

object DependencyCalculator {
  
  class NodeExtension(node: Node) {
    def getAttributeValue(attributeName: String): Option[String] = {
      val attribute = node \ ("@" + attributeName)
      if (attribute.isEmpty) None
      else Option(attribute.head.text)
    }
    
    def hasAttribute(attributeName: String): Boolean = {
      return !(node \ ("@" + attributeName) isEmpty)
    }
  }
  implicit def nodeCoercer(node: Node) = new NodeExtension(node)
  
  def getDependencies(buildFileResolver: String => NodeSeq, buildFileName: String, targetName:String): Map[String, List[String]] = {
     
	def getAllTargetNodesAndProperties(buildFileName: String, properties: Map[String, String]): (NodeSeq, Map[String, String]) = {
	  val antFile = buildFileResolver(buildFileName) 
	  val properties = (antFile \ "property").filter(node => node.hasAttribute("name") && node.hasAttribute("value")).
	  											foldLeft(Map[String, String]())(
	  											    (map, node) => map + ((node.getAttributeValue("name").get -> node.getAttributeValue("value").get)))
	  
	  (antFile \ "import").foldLeft[(NodeSeq, Map[String, String])]((antFile \ "target", properties))(
			  					(p, node) => {
			  					  val (ns, properties) = p
			  					  val (newTargets, newProperties) = 
			  					    getAllTargetNodesAndProperties(
			  					        replaceProperties(node.getAttributeValue("file").get, properties), 
			  					        properties
		  					        )
			  					  (newTargets ++ ns, newProperties ++ properties)
			  					})
	}
	
	def replaceProperties(input: String, properties: Map[String, String]): String = {
	  val pattern = "\\$\\{(.+)\\}".r
	  pattern.replaceAllIn(input, m => properties.getOrElse(m.group(1), ""))
	}
	
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