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
package webbytest.parser

import xml.{Text, Node}
import java.io.FileWriter

class Renderer {
  var idePluginPort = 51235
  val logParser = new OutputParser()
  var useLocalFiles = false


  def writeTo(fileName: String, results: Seq[TestClass], elapsed: Long) = {
    val out = new FileWriter(fileName)
    try {
      val nodes = html(results, elapsed)
      for (node <- nodes) {
        out.write(node.toString())
      }
    }
    finally {
      out.close
    }
  }

  def html(suite: Seq[TestClass], elapsed: Long): Seq[Node] = {
    var total = 0
    var passed = 0
    var failed = 0
    for (c <- suite) {
      total += c.totalCount
      passed += c.passedCount
      failed += c.failedCount
    }

    <html>
      <head>
        {css}
      </head>
      <body>
        <h1 id="qunit-header">
          WebbyTest
        </h1>
          <h2 id="qunit-banner" class="qunit-fail"/>
        <h2 id="qunit-userAgent">
          directory
          {System.getProperty("user.dir", ".")}
        </h2>
        <ol id="qunit-tests">
          {suite.flatMap {html(_)}}
        </ol>
        <p id="qunit-testresult" class="result">
          Tests completed in {elapsed} milliseconds.<br/>
          <span class="passed">{passed}</span>
          tests of
          <span class="total">{total}</span>
          passed, <span class="failed">{failed}</span>
          failed.
        </p>

        {jquery}
        <script>
        {javascript}
        </script>
      </body>
    </html>
  }

  def html(c: TestClass): Seq[Node] = {
    <li class={statusStyle(c.failed)}>
      <strong>
        {c.className}
        <b style="color:black;">(<b class="fail">{c.failedCount}</b>, <b class="pass">{c.passedCount}</b>, {c.totalCount})</b>
      </strong>
      <ol style={displayTestClassStyle(c)}>
        {c.results.flatMap {html(_)}}
      </ol>
    </li>
  }

  def html(t: TestCase): Seq[Node] = {
    <li class={statusStyle(t.failed)}>
      <strong>
        {t.testName}
      </strong>
      <ul style={displayTestCaseStyle(t)}>
        {t.output.flatMap {htmlOutput(_)}}
        {htmlError(t.error)}
      </ul>
    </li>
  }

  def htmlError(error: Throwable): Seq[Node] = {
    if (error == null) {
      Text("")
    }
    else {
      val message = <strong>{error.getMessage}</strong> :: Nil
      val output = error.getStackTrace flatMap {htmlStackTrace(_)}
      message ++ output ++ errorCausedBy(error)
    }
  }

  def htmlStackTrace(s: StackTraceElement): Seq[Node] = {
    val fullFileName = s.getFileName
    val methodCall = s.getClassName + "." + s.getMethodName + "(" + s.getFileName + ":" + s.getLineNumber + ") "

    <li>{methodCall} <img class="ide-icon tb_right_mid"
                   id={"ide-" + s.hashCode}
                   title={"Open file " + fullFileName + " in IDE"}
                   onclick={"this.src='http://localhost:51235/file?file=" + fullFileName + "&line=" + idePluginLine(s.getLineNumber) + "&id=' + Math.floor(Math.random()*1000);"}
                   alt="Open in IDE"
                   src={"http://localhost:" + idePluginPort + "/icon"}/>
      </li>
  }

  def errorCausedBy(error: Throwable): Seq[Node] = {
    val cause = error.getCause
    if (cause == null || cause == error) {
      Text("")
    }
    else {
      <ul>
        <strong>Caused By</strong>
        {htmlError(cause)}
      </ul>
    }
  }

  def htmlOutput(t: String): Seq[Node] = {
    <li>
      {stackTraceToLink(t)}
    </li>
  }

  def stackTraceToLink(text: String): Seq[Node] = logParser.parse(text) match {
    case logParser.Success(lines: List[OutputLine], _) => lines.flatMap {renderLine(_)}
    case _ => Text(text) :: Nil
  }

  def renderLine(line: OutputLine): Seq[Node] = line match {
    case s: StackTrace => val fullFileName = s.fullFileName

    Text(s.methodCall + "(" + s.fileName + ":" + s.line + ") ") ::
              <img class="ide-icon tb_right_mid"
                   id={"ide-" + s.hashCode}
                   title={"Open file " + fullFileName + " in IDE"}
                   onclick={"this.src='http://localhost:51235/file?file=" + fullFileName + "&line=" + idePluginLine(s.line) + "&id=' + Math.floor(Math.random()*1000);"}
                   alt="Open in IDE"
                   src={"http://localhost:" + idePluginPort + "/icon"}/> :: Nil

    /*
        case s: StackTrace => Text(s.methodCall + "(") ::
                <a href={sourceUrl(s.packageName, s.fileName, s.line).trim()}>{s.fileName}</a> ::
                Text(":" + s.line + ")") :: Nil

    */
    case w: WrapLine => (Text(w.prefix + " ") :: Nil) ++ renderLine(w.line) ++ (Text(" " + w.postfix) :: Nil)

    case l: LogLine => <span class={l.level}>[
      {l.level}
      ]
      {renderLine(l.line)}
    </span>

    case line => Text(line.text) :: Nil
  }


  /**
   * The Atlassian IDE plugin seems to highlight the line after the actual line number, so lets subtract one
   */
  def idePluginLine(line: Int) = if (line > 1) line - 1 else line

  def idePluginLine(line: String) = {
    try {
      val i = Integer.parseInt(line)
      "" + i
    }
    catch {
      case _ =>line
    }
  }

  def css = if (useLocalFiles) {
      <link media="screen" type="text/css" href="qunit.css" rel="stylesheet"/>
  }
  else {
      <link rel="stylesheet" href="http://github.com/jquery/qunit/raw/master/qunit/qunit.css" type="text/css" media="screen"/>
  }

  def jquery = if (useLocalFiles) {
      <script src="jquery-latest.js" type="text/javascript"/>
  }
  else {
      <script src="http://code.jquery.com/jquery-latest.js"/>
  }

  def javascript = """
  $(document).ready(function() {
    $('li').click(function(e) {
      $(e.target).children('ol').toggle();
      $(e.target).children('ul').toggle();
      return false;
    });
    $('strong').click(function(e) {
      $(e.target).next('ol').toggle();
      $(e.target).next('ul').toggle();
      return false;
    });
  });  
"""

  def displayTestClassStyle(t: TestResult) = if (t.failed) {""} else {"display: none;"}
  def displayTestCaseStyle(t: TestResult) = "display: none;"
  def displayCausedByStyle(t: TestResult) = "display: none;"

  def statusStyle(failed: Boolean) = if (failed) {"fail"} else {"pass"}

  def sourceUrl(packageName: String, fileName: String, line: Any) = {
    val packageNameAndSlash = if (packageName.length == 0) {""} else {packageName.replace('.', '/') + "/"}
    packageNameAndSlash + fileName

    "http://localhost:" + idePluginPort + "/file?file=" + packageNameAndSlash + fileName + "&line=" + line.toString.trim
  }
}
