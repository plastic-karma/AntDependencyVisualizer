package antgraphvisualizer

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Matchers
import scala.xml.NodeSeq

class DependencyCalculatorSpec extends FlatSpec with Matchers {
  
  
  "No dependency attribute" should "produce empty dependency list" in {
    val buildFile = <project><target name="T1"/></project>
    DependencyCalculator.getDependencies(_ => buildFile, "testFile", "T1") should equal (List())
  }
  
  "T1 with dependency to T2" should "produce (T1, T2)" in {
        val buildFile = 
          <project>
        		<target name="T1" depends="T2"/>
        		<target name="T2"/>
         </project>
        DependencyCalculator.getDependencies(_ => buildFile, "testFile", "T1") should equal (List(("T1", "T2")))
  }
  
  "T1 depends on T2 and T2 depends on T3" should  "produce (T1, T2), (T2,T3)" in {
    val buildFile = 
         <project>
        		<target name="T1" depends="T2"/>
        		<target name="T2" depends="T3"/>
    			<target name="T3" />
         </project>
    DependencyCalculator.getDependencies(_ => buildFile, "testFile", "T1") should equal (List(("T1", "T2"), ("T2", "T3")))
  }
}