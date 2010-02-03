import sbt._

/**
 * @version $Revision: 1.1 $
 */
class WebbyTest(info: ProjectInfo) extends DefaultProject(info) {
  //val scalatest = "org.scala-tools.testing" % "scalatest" % "1.0" % "test"
  val scalatest = "org.scalatest" % "scalatest" % "1.0" % "test"
  val junit = "junit" % "junit" % "4.7" % "test"

  //val sbt     = "sbt" % "sbt" % "sbt_2.7.7-0.6.10"

  // use local maven repo
  val mavenLocal = "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository"


  //def testFrameworks: Iterable[TestFramework] = ScalaTestFramework :: Nil
  //def testFrameworks: Iterable[TestFramework] = ScalaCheckFramework :: SpecsFramework :: ScalaTestFramework :: Nil

}
