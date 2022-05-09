package ch.epfl.sdp.mobile.test.ui.tournaments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import ch.epfl.sdp.mobile.test.state.setContentWithLocalizedStrings
import ch.epfl.sdp.mobile.ui.tournaments.CreateDialog
import ch.epfl.sdp.mobile.ui.tournaments.CreateDialogState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.channels.Channel
import org.junit.Rule
import org.junit.Test

class CreateDialogTest {

  @get:Rule val rule = createComposeRule()

  object SingleChoice : CreateDialogState.Choice {
    override val name = "Choice"
  }

  @Test
  fun given_dialog_when_createNotEnabled_then_createButtonIsDisabled() {
    val state =
        object : CreateDialogState<SingleChoice, SingleChoice> {
          override var name: String = ""
          override val bestOfChoices: List<Int> = emptyList()
          override var bestOf: Int? = null
          override fun onBestOfClick(count: Int) = Unit
          override var maximumPlayerCount = ""
          override val poolSizeChoices: List<SingleChoice> = emptyList()
          override val poolSize: SingleChoice? = null
          override fun onPoolSizeClick(poolSize: SingleChoice) = Unit
          override val eliminationRoundChoices: List<SingleChoice> = emptyList()
          override val eliminationRound: SingleChoice? = null
          override fun onEliminationRoundClick(eliminationRound: SingleChoice) = Unit
          override val confirmEnabled = false
          override fun onConfirm() = Unit
          override fun onCancel() = Unit
        }
    val strings = rule.setContentWithLocalizedStrings { CreateDialog(state) }
    rule.onNodeWithText(strings.tournamentsCreateActionCreate).assertIsNotEnabled()
  }

  @Test
  fun given_createEnabled_when_clickingCreate_then_callsCallback() {
    val channel = Channel<Unit>(1)
    val state =
        object : CreateDialogState<SingleChoice, SingleChoice> {
          override var name: String = ""
          override val bestOfChoices: List<Int> = emptyList()
          override var bestOf: Int? = null
          override fun onBestOfClick(count: Int) = Unit
          override var maximumPlayerCount = ""
          override val poolSizeChoices: List<SingleChoice> = emptyList()
          override val poolSize: SingleChoice? = null
          override fun onPoolSizeClick(poolSize: SingleChoice) = Unit
          override val eliminationRoundChoices: List<SingleChoice> = emptyList()
          override val eliminationRound: SingleChoice? = null
          override fun onEliminationRoundClick(eliminationRound: SingleChoice) = Unit
          override val confirmEnabled = true
          override fun onConfirm() {
            channel.trySend(Unit)
          }
          override fun onCancel() = Unit
        }
    val strings = rule.setContentWithLocalizedStrings { CreateDialog(state) }
    rule.onNodeWithText(strings.tournamentsCreateActionCreate).performClick()
    assertThat(channel.tryReceive().getOrNull()).isEqualTo(Unit)
  }

  @Test
  fun given_singleChoice_when_clickingChoice_then_callsCallback() {
    val channel = Channel<Unit>(1)
    val state =
        object : CreateDialogState<SingleChoice, SingleChoice> {
          override var name: String = ""
          override val bestOfChoices: List<Int> = emptyList()
          override var bestOf: Int? = null
          override fun onBestOfClick(count: Int) = Unit
          override var maximumPlayerCount = ""
          override val poolSizeChoices: List<SingleChoice> = listOf(SingleChoice)
          override val poolSize: SingleChoice? = null
          override fun onPoolSizeClick(poolSize: SingleChoice) {
            channel.trySend(Unit)
          }
          override val eliminationRoundChoices: List<SingleChoice> = emptyList()
          override val eliminationRound: SingleChoice? = null
          override fun onEliminationRoundClick(eliminationRound: SingleChoice) = Unit
          override val confirmEnabled = false
          override fun onConfirm() = Unit
          override fun onCancel() = Unit
        }
    rule.setContentWithLocalizedStrings { CreateDialog(state) }
    rule.onNodeWithText(SingleChoice.name).performClick()
    assertThat(channel.tryReceive().getOrNull()).isEqualTo(Unit)
  }

  @Test
  fun given_bestOfChoices_when_clickingChoice_then_callsCallback() {
    val channel = Channel<Int>(1)
    val state =
        object : CreateDialogState<SingleChoice, SingleChoice> {
          override var name: String = ""
          override val bestOfChoices: List<Int> = listOf(1, 3, 5)
          override var bestOf: Int? = 1
          override fun onBestOfClick(count: Int) {
            channel.trySend(count)
          }
          override var maximumPlayerCount = ""
          override val poolSizeChoices: List<SingleChoice> = emptyList()
          override val poolSize: SingleChoice? = null
          override fun onPoolSizeClick(poolSize: SingleChoice) = Unit
          override val eliminationRoundChoices: List<SingleChoice> = emptyList()
          override val eliminationRound: SingleChoice? = null
          override fun onEliminationRoundClick(eliminationRound: SingleChoice) = Unit
          override val confirmEnabled = false
          override fun onConfirm() = Unit
          override fun onCancel() = Unit
        }
    rule.setContentWithLocalizedStrings { CreateDialog(state) }
    rule.onNodeWithText("3").performClick()
    assertThat(channel.tryReceive().getOrNull()).isEqualTo(3)
  }

  @Test
  fun given_snapshotState_when_editingTournamentName_then_updatesTexts() {
    val state =
        object : CreateDialogState<SingleChoice, SingleChoice> {
          override var name: String by mutableStateOf("")
          override val bestOfChoices: List<Int> = emptyList()
          override var bestOf: Int? = null
          override fun onBestOfClick(count: Int) = Unit
          override var maximumPlayerCount by mutableStateOf("")
          override val poolSizeChoices: List<SingleChoice> = emptyList()
          override val poolSize: SingleChoice? = null
          override fun onPoolSizeClick(poolSize: SingleChoice) = Unit
          override val eliminationRoundChoices: List<SingleChoice> = emptyList()
          override val eliminationRound: SingleChoice? = null
          override fun onEliminationRoundClick(eliminationRound: SingleChoice) = Unit
          override val confirmEnabled = false
          override fun onConfirm() = Unit
          override fun onCancel() = Unit
        }
    val strings = rule.setContentWithLocalizedStrings { CreateDialog(state) }
    rule.onNodeWithText(strings.tournamentsCreateNameHint).performTextInput("Hello")
    rule.onNodeWithText("Hello").assertExists()
  }

  @Test
  fun given_snapshotState_when_editingPlayersCount_then_updatesTexts() {
    val state =
        object : CreateDialogState<SingleChoice, SingleChoice> {
          override var name: String by mutableStateOf("")
          override val bestOfChoices: List<Int> = emptyList()
          override var bestOf: Int? = null
          override fun onBestOfClick(count: Int) = Unit
          override var maximumPlayerCount by mutableStateOf("")
          override val poolSizeChoices: List<SingleChoice> = emptyList()
          override val poolSize: SingleChoice? = null
          override fun onPoolSizeClick(poolSize: SingleChoice) = Unit
          override val eliminationRoundChoices: List<SingleChoice> = emptyList()
          override val eliminationRound: SingleChoice? = null
          override fun onEliminationRoundClick(eliminationRound: SingleChoice) = Unit
          override val confirmEnabled = false
          override fun onConfirm() = Unit
          override fun onCancel() = Unit
        }
    val strings = rule.setContentWithLocalizedStrings { CreateDialog(state) }
    rule.onNodeWithText(strings.tournamentsCreateMaximumPlayerHint).performTextInput("1234")
    rule.onNodeWithText("1234").assertExists()
  }
}
