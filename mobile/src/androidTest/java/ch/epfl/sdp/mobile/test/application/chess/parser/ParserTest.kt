package ch.epfl.sdp.mobile.test.application.chess.parser

import ch.epfl.sdp.mobile.application.chess.parser.Combinators
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.filterNotNull
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.repeat
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ParserTest {

  @Test
  fun given_digitParser_when_repeat_then_returnsDigitsInOrder() {
    val digit = StringCombinators.digit()
    val repeatingDigits = digit.repeat()
    val result = repeatingDigits.parse("1234567890").single()

    assertThat(result.output).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 0).inOrder()
    assertThat(result.remaining).isEmpty()
  }

  @Test
  fun given_digitParser_when_repeatOnEmpty_then_returnsEmptyList() {
    val digit = StringCombinators.digit()
    val repeatingDigits = digit.repeat()
    val result = repeatingDigits.parse("hello").single()

    assertThat(result.output).isEmpty()
    assertThat(result.remaining).isEqualTo("hello")
  }

  @Test
  fun given_digitParser_when_repeatWithBadCharacter_then_returnsPrefixList() {
    val digit = StringCombinators.digit()
    val repeatingDigits = digit.repeat()
    val result = repeatingDigits.parse("1234hello567890").single()

    assertThat(result.output).containsExactly(1, 2, 3, 4).inOrder()
    assertThat(result.remaining).isEqualTo("hello567890")
  }

  @Test
  fun given_inputThatCanOutputNull_when_parsed_then_containsOnlyNotNullValue() {
    val digit = StringCombinators.digit()
    val even = digit.map { if (it % 2 == 0) null else it }
    val multipleThree = digit.map { if (it % 3 == 0) null else it }
    val parserWithoutNull = Combinators.combine(even, multipleThree).filterNotNull()

    val result = parserWithoutNull.parse("3456789")

    assertThat(result.count()).isEqualTo(1)
  }
}
