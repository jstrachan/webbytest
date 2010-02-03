package webbytest

import xml.{Text, Node}

class Renderer {
  var idePluginPort = 51235
  val logParser = new OutputParser()


  def html(suite: Seq[TestClass]): Seq[Node] = {
    <html>
      <head>
          <link media="screen" type="text/css" href="qunit.css" rel="stylesheet"/>
          <script src="jquery-latest.js" type="text/javascript"/>
        <script>
          {javascript}
        </script>
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
          Tests completed in 8 milliseconds.<br/>
          <span class="passed">5</span>
          tests of
          <span class="total">6</span>
          passed, <span class="failed">1</span>
          failed.
        </p>
      </body>
    </html>
  }

  def html(c: TestClass): Seq[Node] = {
    <li class={statusStyle(c.failed)}>
      <strong>
        {c.className}<b style="color: black;">(0, 2, 2)</b>
      </strong>
      <ol style={displayStyle(c)}>
        {c.results.flatMap {html(_)}}
      </ol>
    </li>
  }

  def html(t: TestCase): Seq[Node] = {
    <li class={statusStyle(t.failed)}>
      <strong>
        {t.testName}
      </strong>
      <ul style={displayStyle(t)}>
        {t.output.flatMap {htmlOutput(_)}}
      </ul>
    </li>
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
                   onclick={"this.src='http://localhost:51235/file?file=" + fullFileName + "&line=" + s.line + "&id=' + Math.floor(Math.random()*1000);"}
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

  def displayStyle(t: TestResult) = if (t.failed) {""} else {"display: none;"}

  def statusStyle(failed: Boolean) = if (failed) {"fail"} else {"pass"}

  def sourceUrl(packageName: String, fileName: String, line: Any) = {
    val packageNameAndSlash = if (packageName.length == 0) {""} else {packageName.replace('.', '/') + "/"}
    packageNameAndSlash + fileName

    "http://localhost:" + idePluginPort + "/file?file=" + packageNameAndSlash + fileName + "&line=" + line.toString.trim
  }
}
