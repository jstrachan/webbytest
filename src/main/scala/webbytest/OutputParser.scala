package webbytest

import scala.util.parsing.combinator._
import java.io.Reader

abstract class OutputLine {
  def text: String
}

case class TextLine(text: String) extends OutputLine {}

case class LogLine(level: String, line: OutputLine) extends OutputLine {
  def text = "[" + level + "] " + line.text
}

case class WrapLine(prefix: String, line: OutputLine, postfix: String) extends OutputLine {
  def text = prefix + line.text + postfix
}

case class StackTrace(packageName: String, className: String, methodName: String,
                      fileName: String, line: String) extends OutputLine {
  def text = methodCall + "(" + fileName + ":" + line + ")"

  def methodCall = packageName + dotPrefix(className) + dotPrefix(methodName)

  def fullFileName = if (packageName.length == 0) {fileName} else {packageName.replace('.', '/') + "/" + fileName}

  private def dotPrefix(text: String) = if (text == null || text.length == 0) {""} else {"." + text}

}

/**
 * Parses program output looking for logging and stack traces so it can be pretty-printed in web UIs
 *
 * @version $Revision : 1.1 $
 */
class OutputParser extends JavaTokenParsers {
  def parse(in: Reader) = {
    parseAll(lines, in)
  }

  def parse(in: CharSequence) = {
    parseAll(lines, in)
  }

  //def lines = rep(line | any)
  def lines = rep(line | textLine) ^^ {
    case lines  => lines
  }

  def line = opt("at") ~ stackTrace ~ opt(any <~ "\n") ^^ {
    case prefix ~ st ~ postfix =>
      prefix match {
        case Some(text) => WrapLine(text, st, postfix getOrElse "")

        case _ => st
      }
  }


  def stackTrace = (packageName <~ "(") ~ (fileName <~ ".") ~ (fileExt <~ ":") ~ (wholeNumber <~ ")") ^^ {
    case pn ~ fn ~ fe ~ line => {
      val size = pn.length
      val packagePrefix = pn.dropRight(2).mkString(".")
      val className = if (size > 2) {pn(size - 2)} else {""}
      val methodName = if (size > 1) {pn(size - 1)} else {""}

      val s = StackTrace(packagePrefix, className, methodName, fn + "." + fe, line.toString)
      println("stack trace: " + s)
      s
    }
  }


  def textLine = any ^^ {
    case a => TextLine(a.toString)
  }

  def any = """.+""".r
  def newline = """$""".r

  def packageName = repsep(token, ".")

  def className = token

  def methodName = token

  def fileName = token

  def fileExt = token

  def token = """[a-zA-Z0-9\$_]+""".r
}