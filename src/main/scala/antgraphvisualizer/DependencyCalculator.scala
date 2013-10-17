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
     
	def getAllTargetNodes(buildFileName: String): NodeSeq = {
	  val antFile = buildFileResolver(buildFileName) 
	  (antFile \ "target") ++
	  (antFile \ "import").foldLeft[NodeSeq](NodeSeq.Empty)(
			  					(ns, node) => ns ++ getAllTargetNodes(node.getAttributeValue("file").get))
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
	
    getDependenciesInternal(targetName, getAllTargetNodes(buildFileName))
  }
}