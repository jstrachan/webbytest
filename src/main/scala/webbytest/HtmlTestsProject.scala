package webbytest

import sbt._

/**
 * @version $Revision : 1.1 $
 */
trait HtmlTestsProject extends DefaultProject {
  //this : DefaultProject =>

  override def testListeners: Seq[TestReportListener] = {
    val htmlTestListener = new HtmlTestsListener("scalate-core/target/tests.html")
    (htmlTestListener :: Nil) ++ super.testListeners
  }
}