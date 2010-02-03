WebbyTest
=========

WebbyTest is a little experimental parser of [sbt]() output which can render test results nicely, with stack traces transformed into nice HTML links which are then clickable - which then opens the source file in your IDE if you have the [Atlassian IDE Connector]() installed.

To try it out, try running the webbytest.Main on some output, or piping the output of sbt into it.

Ideally we'd configure WebbyTest as a test renderer of sbt...
