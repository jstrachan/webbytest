package webbytest

import sbt._
import webbytest.parser._
import collection.mutable.{HashSet, ListBuffer}
import org.scalatools.testing.Event
import org.scalatools.testing.{Result => TestingResult}

/**
 * @version $Revision : 1.1 $
 */
class HtmlTestsListener(val fileName: String) extends TestsListener
{
  var results = new ListBuffer[TestClass]()
  var testClass: TestClass = _
  var renderer = new Renderer()

  def startGroup(name: String) = {
    println("XXX> startGroup " + name)

    testClass = TestClass(name)
    results.append(testClass)
  }

  def testEvent(event: TestEvent) = {

    event match {
    //case s: ScalaTestEvent => println("XXX> ScalaTestEvent: " + s)
      case e =>
        val detail = e.detail
        //println("XXX> event: " + e + " with result: " + e.result) // + " detail: " + detail)

        for (i <- detail) {
          i match {
            case te: Event =>
              //println("XXX>     testingEvent: " + te.testName + " description: " + te.description + " result: " + te.result + " error: " + te.error)
              //val output = if (te.error == null) List() else {}

              val output = Seq()
              val status = if (te.result == TestingResult.Success) true else false
              val testCase = TestCase(te.testName, output, status, te.error)
              testClass.results.append(testCase)
          }
        }
    }
  }

  def endGroup(name: String, t: Throwable) = {
    //println("XXX> endGroup(" + name + " t" + t)
  }


  def endGroup(name: String, result: Result.Value) = {
    //println("XXX> endGroup(" + name + " result: " + result)
  }

  def doInit = {
    results.clear
  }

  def doComplete(finalResult: Result.Value) = {
    //println("XXX> onComplete " + finalResult)

    println("generating HTML report to: " + fileName)
    renderer.writeTo(fileName, results)

    results.clear
  }


  def classesOf(aClass: Class[_], set: HashSet[Class[_]]) : Unit = {
    set += aClass
    val superclass = aClass.getSuperclass
    if (superclass != null && aClass != classOf[Object]) {
      classesOf(superclass, set)
    }

    for (i <- aClass.getInterfaces if i != aClass) {
      classesOf(i, set)
    }
  }

}
