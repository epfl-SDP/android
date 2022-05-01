package ch.epfl.sdp.mobile.application.chess.parser

import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.string
import org.junit.Assert.*
import org.junit.Test

class StringCombinatorsTest {

  @Test
  fun given_aString_when_parsed_then_returnSetOfResult() {
    val res = string().parse("This is a test")
    val expected = setOf(Parser.Result("is a test", "This"))
    assertEquals(expected, res)
  }

  @Test
  fun given_aString_when_parsedWithAGiven1stWord_then_returnSetOfResult() {
    val res = string("This").parse("This is a test")
    val expected = setOf(Parser.Result("is a test", "This"))
    assertEquals(expected, res)
  }

  @Test
  fun given_aEmptyString_when_parsed_then_returnEmptySet() {
    val res = string().parse("")
    val expected = emptySet<Parser<String, Token>>()
    assertEquals(expected, res)
  }
}
