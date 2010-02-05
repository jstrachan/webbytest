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

    
Now this should generate the *target/scala_XXXX/tests.html* file whenever you run the tests.

If you use the continuous testing feature in sbt, your html file will refresh each time you edit source code...

    sbt
    ~ test-quick


Using the HTML Test Report
-------------------------
Once you open the test reports file in your browser you should be able to expand/collapse the test classes, test cases and stack traces.

By default the failing tests are open; as they are the things you typically want to see first. Also to avoid noise the stack traces are collapsed by default.


When you expand a failing test and see a stack trace you should be able to click on the icon next to each stack trace to open the file at the line which threw the exception
(assuming you have the [Atlassian IDE Connector](http://www.atlassian.com/software/ideconnector/) installed and the project open in your IDE).

Example project
---------------

To see an example project using WebbyTest try the [scalate project](http://scalate.fusesource.org/) project, you can get the [source code here](http://scalate.fusesource.org/source.html)

For example to run the tests and view the HTML report try this

    git clone git://github.com/scalate/scalate.git
    cd scalate
    ./sbt
    update
    test
    exit
    open scalate-core/target/scala_2.8.0.Beta1/tests.html


License
-------

WebbyTest is released under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0) 
a copy of which is included in the source as [LICENSE.txt](http://github.com/jstrachan/webbytest/blob/master/LICENSE.txt)



