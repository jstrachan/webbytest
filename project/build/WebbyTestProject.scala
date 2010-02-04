import sbt._

/**
 * @version $Revision : 1.1 $
 */
class WebbyTest(info: ProjectInfo) extends PluginProject(info) { // DefaultProject(info) { //with test.ScalaScripted

  //  val ivy = "org.apache.ivy" % "ivy" % "2.0.0"

  val scalatest = "org.scalatest" % "scalatest" % "1.0"
  val junit = "junit" % "junit" % "4.7" % "test"

  //val sbt     = "org.scala-tools.sbt" % "sbt" % "sbt_2.7.7-0.5.6"
  //val sbtTest = "org.scala-tools.sbt" % "test" % "0.5.6"

  // use local maven repo
  val mavenLocal = "Local Maven Repository" at "file://" + Path.userHome + "/.m2/repository"


  //def testFrameworks: Iterable[TestFramework] = ScalaTestFramework :: Nil
  //def testFrameworks: Iterable[TestFramework] = ScalaCheckFramework :: SpecsFramework :: ScalaTestFramework :: Nil

}
