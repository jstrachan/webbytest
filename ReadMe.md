WebbyTest
=========

WebbyTest is a little experimental parser of [sbt](http://code.google.com/p/simple-build-tool/) output which can render test results nicely, with stack traces transformed into nice HTML links which are then clickable - which then opens the source file in your IDE if you have the [Atlassian IDE Connector](http://www.atlassian.com/software/ideconnector/) installed. (For background in how this works see [this thread](http://www.jetbrains.net/devnet/message/5254292#5254292))

The idea being we can use sbt to run/compile/test our code continuously, then we get a nice web page we can refresh (or which ideally will auto-refresh) showing a summary of errors, which we can then easily click on to see actual failures.

Have a play
-----------
To try it out, try running the webbytest.Main on some output, or piping the output of sbt into it.

e.g. try from the console

    ./sbt
    run sampleLog.txt
    quit
    open testresults.html 
    
this should generate the testresults.html file you can then browse in a browser. (Note the links won't work unless you happen to have opened the [scalate](http://scalate.fusesource.org/) project in your IDE - and you have the  [Atlassian IDE Connector](http://www.atlassian.com/software/ideconnector/) installed in your IDE :)

Going forward
-------------
Ideally we'd configure WebbyTest as a test renderer of sbt... I tried taking a look but by sbt foo was not strong enough :(
