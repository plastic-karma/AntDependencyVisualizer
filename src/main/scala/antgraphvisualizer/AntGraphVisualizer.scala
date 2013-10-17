package antgraphvisualizer

import scala.xml.NodeSeq
import java.io.File
import scala.xml.Node

object AntGraphVisualizer {
  
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
  
  def getDependencies(baseFolder: String, buildFileName:String, targetName:String): List[(String, String)] = {
    
	def getAllTargetNodes(baseFolder: String, buildFileName: String): NodeSeq = {
	  val antFile = scala.xml.XML.loadFile(new File(baseFolder, buildFileName))   
	  (antFile \ "target") ++
	  (antFile \ "import").foldLeft[NodeSeq](NodeSeq.Empty)(
			  					(ns, node) => ns ++ getAllTargetNodes(baseFolder, node.getAttributeValue("file").get))
	}
	
	def getDependenciesInternal(targetName: String, targets: NodeSeq): List[(String, String)] = {
	  val target = targets.find(currentTarget => currentTarget.attributes.exists(md => md.key == "name" && md.value.text == targetName))
	  assert(target.nonEmpty)
	  val directDepends = target.get.getAttributeValue("depends")
	  if (directDepends isDefined) {
	    val directDependsList = directDepends.get.split(",").map(str => str.trim)
	    directDependsList.foldLeft[List[(String, String)]](List())((list, str) => (targetName, str) :: list ++ getDependenciesInternal(str, targets))
	  }
	  else List()
	}
	
    getDependenciesInternal(targetName, getAllTargetNodes(baseFolder, buildFileName))
  }
}