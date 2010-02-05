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


  // publishing
  override def managedStyle = ManagedStyle.Maven

  //val publishTo = "WebbyTest Repo" at "dav:http://fusesource.com/forge/dav/webbytest/repo/release"
  val publishTo = "WebbyTest Repo" at "dav:http://fusesource.com/forge/dav/webbytest/repo/snapshot"
}
