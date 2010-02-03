package webbytest

import collection.mutable.ListBuffer
import java.io._
import util.matching.Regex

/**
 * a simple application which parses the SBT output and generates a HTML representation of the test results
 * which are clickable to navigate to the source code via the 
 * <a "http://www.atlassian.com/software/ideconnector/">Atlassian IDE Connector</a> plugin for IDEA or Eclipse
 *
 * @version $Revision : 1.1 $
 */
object Main {
  def main(args: Array[String]) {
    val filter = new TestFilter()
    if (args.isEmpty) {
      filter.run(System.in)
    }
    else {
      val file = args(0)
      println("Parsing file: " + file)
      filter.run(new FileInputStream(file))
    }
  }
}

trait TestResult {
  def status: Boolean

  def failed = !status

  /**@returns the CSS style of whether this passed or failed */
}

case class TestCase(testName: String, output: Seq[String], status: Boolean) extends TestResult {
}

case class TestClass(className: String) extends TestResult {
  val results = new ListBuffer[TestCase]()

  def status = !results.exists(_.failed)
}

class TestFilter {
  val fileName = "testresults.html"
  val regexTestClass = """\[.*info.*\].*==\s(.*)\s==""".r
  val regexTestCompleted = """.*==\stest-(complete|finish)\s==.*"""
  val regexTestStarted = """\[.*info.*].*Test Starting: (.*)""".r
  val regexTestPassed = """\[.*info.*].*Test Passed: (.*)""".r
  val regexTestFailed = """\[.*error.*].*Test Failed: (.*)""".r

  var output = new ListBuffer[String]()
  var testClass = TestClass("n/a")
  var testName: String = "n/a"
  var results = new ListBuffer[TestClass]()
  var renderer = new Renderer()

  def run(input: InputStream): Unit = {
    run(new BufferedReader(new InputStreamReader(input)))
  }

  def run(in: BufferedReader): Unit = {
    println("Writing HTML view to file: " + fileName)

    reset
    var testsStarted = false
    var ok = true
    while (ok) {
      val line = in.readLine
      if (line == null) {
        ok = false
      }
      else {
        // lets output the line to the console
        //println(line)

        // now lets see if its a test case

        if (line.contains("== test-start ==")) {
          reset
          testsStarted = true
        }
        else {
          if (line.matches(regexTestCompleted)) {
            if (testsStarted) {
              testsStarted = false
              completed
              println("Completed creating the HTML")
            }
          }
          else {
            if (testsStarted) {
              // are we a new test class
              firstMatchingGroup(regexTestClass, line) match {
                case Some(n) =>
                  if (n != testClass.className) {
                    testClass = TestClass(n)
                    results += testClass
                  }

                case _ => firstMatchingGroup(regexTestStarted, line) match {
                  case Some(n) =>
                    testName = n

                  case _ => firstMatchingGroup(regexTestPassed, line) match {
                    case Some(n) =>
                      add(TestCase(testName, output, true))

                    case _ => firstMatchingGroup(regexTestFailed, line) match {
                      case Some(n) =>
                        add(TestCase(testName, output, false))

                      case _ => output += line
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    println()
    println()
    for (c <- results) {
      println("" + c.className + " failed = " + c.failed)
      for (r <- c.results) {
        println("\t" + r.testName + " failed = " + r.failed + " output = " + r.output.length)
      }
      println()
    }
  }

  /**We may do nothing at this step if we pull the view of current results in a web app */
  def completed: Unit = {
    // lets write the HTML
    val out = new FileWriter(fileName)
    try {
      val html = renderer.html(results)
      for (node <- html) {
        out.write(node.toString())
      }
    }
    finally {
      out.close
    }
  }


  def reset = {
    testClass = TestClass("n/a")
    testName = "n/a"
    results = new ListBuffer[TestClass]()
    resetOutput
  }

  def resetOutput = {output = new ListBuffer[String]()}

  def add(result: TestCase) {
    testClass.results += result
    resetOutput
  }

  def firstMatchingGroup(re: Regex, text: String): Option[String] = {
    re.findFirstMatchIn(text) match {
      case Some(m: Regex.Match) => Some(m.group(1))
      case _ => None
    }
  }
}