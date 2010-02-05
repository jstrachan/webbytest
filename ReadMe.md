WebbyTest
=========

WebbyTest is a plugin for [sbt]() which generates nice HTML reports of tests results, with stack traces transformed
into nice HTML links which are then clickable.

Clicking on the icon next to a line in the stack trace then opens the
source file in your IDE if you have the [Atlassian IDE Connector](http://www.atlassian.com/software/ideconnector/) installed. (For background in how this works see [this thread](http://www.jetbrains.net/devnet/message/5254292#5254292))

The idea is that we can use sbt to run/compile/test our code continuously, then we get a nice web page we can refresh (or which ideally will auto-refresh) showing a summary of errors, which we can then easily click on to see actual failures.

Building WebbyTest locally
--------------------------

First you need to build it locally (we'll have it hosted on a maven repo soon!)

    ./sbt
    update
    publish-local

You're local repository should now include the local build of WebbyTest


Using WebbyTest in your sbt build
---------------------------------

First you need to create a file in *project/plugins/Plugins.scala*


    import sbt._

    class Plugins(info: ProjectInfo) extends PluginDefinition(info) {

      val webbytest = "org.fusesource" % "webbytest" % "1.0-SNAPSHOT"
    }

Then in your build file in, say, *project/build/MyProject.scala* (call it whatever class name you like) you use the *HtmlTestsProject* trait


    import sbt._
    import webbytest.HtmlTestsProject

    class MyProject(info: ProjectInfo) extends DefaultProject(info) with HtmlTestsProject {
    }

    
Now this should generate the *target/tests.html* file whenever you run the tests.

If you use the continuous testing feature in sbt, your html file will refresh each time you edit source code...

    sbt
    ~ test-quick


Example project
---------------

To see an example project using WebbyTest try the [scalate project](http://scalate.fusesource.org/) project, you can get the [source code here](http://scalate.fusesource.org/source.html)
