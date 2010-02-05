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
package webbytest

import webbytest.parser._

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