package antgraphvisualizer

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.xml.NodeSeq

class DependencyCalculatorImportSpec extends FlatSpec with Matchers {

  def buildFileRepository(buildFiles: Map[String, NodeSeq])(buildFileName: String) = buildFiles(buildFileName)
  
  "T1 in B1 depends on T2 in B2 and T2 depends on T3 in B2" should "produce T1 -> (T2), T2 -> (T3)" in {
	  	val buildFiles = Map(
	  	    "B1" -> 
	  	              <project>
	  	    			<import file="B2" />
        				<target name="T1" depends="T2"/>
	  	    		  </project>,
    		"B2" ->
		             <project>
	  	    		   <target name="T2" depends="T3"/>
	  	    		   <target name="T3"/>
	  	    		 </project>
  			)
        DependencyCalculator.getDependencies(buildFileRepository(buildFiles), "B1", "T1") should equal (Map("T1" -> List("T2"), "T2" -> List("T3")))
  }
  
  "T1 in B1 depends on T2 in B2 and T3 in B1. T2 depends on T4 in B3. T3 depends on T5 in B3" should 
  "T1 -> (T2, T3), T2 -> (T4), T3 -> (T5)" in {
  	  	val buildFiles = Map(
	  	    "B1" -> 
	  	              <project>
	  	    			<import file="B2" />
	  	    			<import file="B3" />
        				<target name="T1" depends="T2, T3"/>
	  	    			<target name="T3" depends="T5"/>
	  	    		  </project>,
    		"B2" ->
		             <project>
	  	    		   <import file="B3" />
	  	    		   <target name="T2" depends="T4"/>
	  	    		 </project>,
    		"B3" ->
		              <project>
	  	    		    <target name="T4"/>
	  	    		    <target name="T5"/>
	  	    		 </project>
  			)
        DependencyCalculator.getDependencies(buildFileRepository(buildFiles), "B1", "T1") should equal (
            Map("T1" -> List("T3", "T2"), "T2" -> List("T4"), "T3" -> List("T5")))
  }
  
  "T1 in B1 depends on T2 in B2. T2 depends on T3. Import of B2 uses property defined in B3" should "produce T1 -> (T2), T2 -> (T3)" in {
  	  	val buildFiles = Map(
	  	    "B1" -> 
	  	              <project>
	  	    			<import file="B3" />
	  	    			<import file="${import.b2}/B2" />
        				<target name="T1" depends="T2"/>
	  	    		  </project>,
    		"propValue/B2" ->
		             <project>
	  	    		   <target name="T2" depends="T3" />
	  	    		   <target name="T3"/>
	  	    		 </project>,
    		"B3" ->
		              <project>
	  	    		 	<property name="import.b2" value="propValue" />
	  	    		 </project>
  			)    
        DependencyCalculator.getDependencies(buildFileRepository(buildFiles), "B1", "T1") should equal (Map("T1" -> List("T2"), "T2" -> List("T3")))
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
}