package ch.epfl.sdp.mobile.test.ui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.PromoteDialog
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

class PromoteDialogTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_noSelection_when_clickRook_then_rookIsSelected() {
    var selection by mutableStateOf<ChessBoardState.Rank?>(null)
    val strings =
        rule.setContentWithLocalizedStrings {
          PromoteDialog(
              color = ChessBoardState.Color.Black,
              selected = selection,
              onSelectRank = { selection = it },
              onConfirm = { /* Ignored. */},
          )
        }
    rule.onNodeWithContentDescription(strings.boardPieceRook).performClick()
    assertThat(selection).isEqualTo(ChessBoardState.Rank.Rook)
  }

  @Test
  fun given_dialog_when_clickingConfirm_then_callsCallback() {
    var clicked by mutableStateOf(false)
    val strings =
        rule.setContentWithLocalizedStrings {
          PromoteDialog(
              color = ChessBoardState.Color.Black,
              selected = null,
              onSelectRank = { /* Ignored. */},
              onConfirm = { clicked = true },
          )
        }
    rule.onNodeWithText(strings.gamePromoteConfirm).performClick()
    assertThat(clicked).isTrue()
  }
}
