package ch.epfl.sdp.mobile.state.game.delegating

import androidx.compose.runtime.*
import ch.epfl.sdp.mobile.application.chess.engine.Action
import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Piece
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.state.game.core.GameDelegate
import ch.epfl.sdp.mobile.ui.game.TextToSpeechState
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DelegatingTextToSpeechState
constructor(
    private val gameDelegate: GameDelegate,
    private val facade: SpeechFacade,
    strings: State<LocalizedStrings>,
    private val scope: CoroutineScope,
) : TextToSpeechState {

  private val strings by strings

  private var settings by mutableStateOf(SpeechFacade.TextToSpeechSettings(true, facade))

  init {
    scope.launch { facade.textToSpeechSettings().onEach { settings = it }.collect() }
  }

  private suspend fun synthesizeMoveFlow(): Flow<*> {
    return snapshotFlow { gameDelegate.game }
        .mapNotNull { it.previous?.let { (game, action) -> action to game.board } }
        .distinctUntilChanged()
        .onEach { (action, board) -> facade.synthesize(toText(action, board)) }
  }

  init {
    scope.launch { synthesizeMoveFlow().collect() }
  }

  override val textToSpeechEnabled: Boolean
    get() = settings.enabled

  override fun onTextToSpeechToggle() {
    scope.launch { settings.update { enabled(!textToSpeechEnabled) } }
  }

  private fun toText(action: Action, board: Board<Piece<Color>>): String {
    return when (action) {
      is Action.Move -> moveToText(action, board)
      is Action.Promote -> promoteToText(action, board)
    }
  }

  private fun moveToText(move: Action.Move, board: Board<Piece<Color>>): String {
    val from = strings.boardPosition(move.from.x, move.from.y)
    val to = strings.boardPosition(move.from.x + move.delta.x, move.from.y + move.delta.y)
    // TOTO : Use the actual piece.
    return strings.boardMove("white pawn", from, to)
  }

  private fun promoteToText(promotion: Action.Promote, board: Board<Piece<Color>>): String {
    return "Promote" // TODO:
  }
}
