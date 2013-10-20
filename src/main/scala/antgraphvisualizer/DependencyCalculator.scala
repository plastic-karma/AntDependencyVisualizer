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
   
    
    
    /**
     * Returns all target nodes from a given file and it's imports.
     */
    def getAllTargetNodes(buildFileName: String): NodeSeq = {
      
        /**
         * Auxiliary function to calculate target nodes from a given file and it's imports.
         * @param buildFileName The build file to get the targets from.
         * @param Auxiliary parameter to keep track of ant properties.
         * @param visitedImports Auxiliary parameter to keep track of visited imports, 
         * so every file is only visited once.
         */
		def getAllTargetNodesAndPropertiesInternal(
		    buildFileName: String, 
		    existingProperties: Map[String, String], 
		    visitedImports: List[String])
		: (NodeSeq, Map[String, String], List[String]) = {
		  
	      if (visitedImports.contains(buildFileName)) (NodeSeq.Empty, Map(), visitedImports)
	      else {
	    	  val allVisitedImports = buildFileName :: visitedImports
			  val antFile = buildFileResolver(buildFileName) 
			  val allExistingProperties = 
			    getProperties((antFile \ "property").filter(node => node.hasAttribute("name") && node.hasAttribute("value"))) ++ existingProperties
			  
			  
			  (antFile \ "import").foldLeft[(NodeSeq, Map[String, String], List[String])]((antFile \ "target", allExistingProperties, allVisitedImports))(
					  (p, node) => {
						  val (ns, currentProperties, currentVisitedImports) = p
								  val (newTargets, newProperties, newVisitedImports) =  getAllTargetNodesAndPropertiesInternal(
										  replaceProperties(node.getAttributeValue("file").get, currentProperties), 
										  currentProperties, currentVisitedImports)
										  (newTargets ++ ns, newProperties ++ currentProperties, newVisitedImports)
					  }
					  )
	      }
		}
		getAllTargetNodesAndPropertiesInternal(buildFileName, Map(), List())._1
    }
   
	
	/**
	 * Interal recursive function to calculate dependencies.
	 * @param targetName The name of the target to calculate dependencies for.
	 * @param targets All available ant targets to calculate dependencies from.
	 * @param visitedNodes Auxiliary parameter to keep track of already visited targets, 
	 * so dependencies get for every target get only calculated once. 
	 */
	def getDependenciesInternal(
	    targetName: String, 
	    targets: NodeSeq, 
	    visitedNodes: List[String])
	: Map[String, List[String]] = {
	  
	  if (visitedNodes.contains(targetName)) Map()
	  else {
		  val target = targets.find(currentTarget => currentTarget.attributes.exists(md => md.key == "name" && md.value.text == targetName))
		  assert(target.nonEmpty)
		  val directDepends = target.get.getAttributeValue("depends")
		  if (directDepends isDefined) {
		    val directDependsList = directDepends.get.split(",").map(str => str.trim)
		    directDependsList.foldLeft[Map[String, List[String]]](Map())((map, str) =>
		      map
		       + (targetName -> (str :: map.getOrElse(targetName, List())))
		      ++ getDependenciesInternal(str, targets, visitedNodes ++ map.keys))
		  }
		  else Map()
	  }
	}
	
	
	/*
	 * Actual entry point for getDependencies
	 */
    getDependenciesInternal(targetName, getAllTargetNodes(buildFileName), List())
  }
}