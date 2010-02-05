/**
 * Copyright 2010 James Strachan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package webbytest

import sbt._
import webbytest.parser._
import collection.mutable.{HashSet, ListBuffer}
import org.scalatools.testing.Event
import org.scalatools.testing.{Result => TestingResult}

/**
 * @version $Revision : 1.1 $
 */
class HtmlTestsListener(var fileName: String) extends TestsListener
{
  var results = new ListBuffer[TestClass]()
  var renderer = new Renderer()
  var testClass: TestClass = _
  var start: Long = _

  def startGroup(name: String) = {
    println("XXX> startGroup " + name)

    testClass = TestClass(name)
    results.append(testClass)
  }

  def testEvent(event: TestEvent) = {
    event match {
      case e =>
        val detail = e.detail

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
  }


  def endGroup(name: String, result: Result.Value) = {
  }

  def doInit = {
    results.clear
    start = System.currentTimeMillis
  }

  def doComplete(finalResult: Result.Value) = {
    println("generating HTML report to: " + fileName)

    val elapsed = System.currentTimeMillis - start
    renderer.writeTo(fileName, results, elapsed)


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
