package ch.epfl.sdp.mobile.application.chess.parser

import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.token
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringCombinatorsTest {

  @Test
  fun given_aString_when_parsed_then_returnSetOfResult() {
    val res = token().parse("This is a test")
    val expected = Parser.Result("is a test", "This")
    assertThat(res.first()).isEqualTo(expected)
  }

  @Test
  fun given_aString_when_parsedWithAGiven1stWord_then_returnSetOfResult() {
    val res = token("This").parse("This is a test")
    val expected = Parser.Result("is a test", "This")
    assertThat(res.first()).isEqualTo(expected)
  }

  @Test
  fun given_aEmptyString_when_parsed_then_returnEmptySet() {
    val res = token().parse("")
    val expected = emptySequence<Parser<String, Token>>()
    assertThat(res).isEqualTo(expected)
  }
}
