package webbytest

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FunSuite


/**
 * @version $Revision: 1.1 $
 */
@RunWith(classOf[JUnitRunner])
class OutputParserTest extends FunSuite {
  val parser = new OutputParser()

  test("parse stack trace") {
    val text = "        at scala.tools.nsc.symtab.Scopes$Scope.enterInHash(Scopes.scala:145)"
    parser.parse(text) match {
      case parser.Success(lines : List[OutputLine], _) =>
        println("parsed: " + lines)

        expect(1) { lines.length }
        expect(WrapLine("at", StackTrace("scala.tools.nsc.symtab", "Scopes$Scope", "enterInHash", "Scopes.scala", "145"), "")) { lines(0) }

      case r => fail("failed to parse: " + r)
    }
  }

  test("parse stack trace in middle of text") {
    val text = """some text
            at scala.tools.nsc.symtab.Scopes$Scope.enterInHash(Scopes.scala:145)
some more text
"""
    parser.parse(text) match {
      case parser.Success(lines : List[OutputLine], _) =>
        println("parsed: " + lines)

        expect(3) { lines.length }
        expect(WrapLine("at", StackTrace("scala.tools.nsc.symtab", "Scopes$Scope", "enterInHash", "Scopes.scala", "145"), "")) { lines(1) }
        expect(TextLine("some text")) { lines(0) }
        expect(TextLine("some more text")) { lines(2) }

      case r => fail("failed to parse: " + r)
    }
  }
}