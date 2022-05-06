package ch.epfl.sdp.mobile.application.chess.parser

import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.convertToken
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.token
import ch.epfl.sdp.mobile.application.speech.ChessSpeechEnglishDictionary
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
    assertThat(res.count()).isEqualTo(0)
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

  @Test
  fun given_aHomophone_when_parsedWithDictionary_then_giveCorrectToken(){
    val res = convertToken(ChessSpeechEnglishDictionary.chessPieces).parse("quean a2 to g3")
    assertThat(res.first()).isEqualTo(Parser.Result("a2 to g3", "queen"))
  }
}
