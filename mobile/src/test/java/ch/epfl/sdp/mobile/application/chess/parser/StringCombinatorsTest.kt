package ch.epfl.sdp.mobile.application.chess.parser

import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.token
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringCombinatorsTest {

  @Test
  fun given_aString_when_parsed_then_returnSequenceOfResult() {
    val res = token().parse("This is a test")
    val expected = Parser.Result("is a test", "This")
    assertThat(res.first()).isEqualTo(expected)
  }

  @Test
  fun given_aString_when_parsedWithAGiven1stWord_then_returnSequenceOfResult() {
    val res = token("This").parse("This is a test")
    val expected = Parser.Result("is a test", "This")
    assertThat(res.first()).isEqualTo(expected)
  }

  @Test
  fun given_aEmptyString_when_parsed_then_returnEmptySequence() {
    val res = token().parse("")
    val expected = emptySequence<Parser<String, Token>>()
    assertThat(res).isEqualTo(expected)
  }

  @Test
  fun given_aString_when_parsedWithAGivenToken_then_returnEmptySequence() {
    val res = token("That").parse("This is a test")
    assertThat(res.count()).isEqualTo(0)
  }

  @Test
  fun given_aStringWithOnlyDelimiter_when_parsed_then_returnEmptySequence() {
    val res = token(delimiter = '-').parse("---")
    assertThat(res.count()).isEqualTo(0)
  }
}
