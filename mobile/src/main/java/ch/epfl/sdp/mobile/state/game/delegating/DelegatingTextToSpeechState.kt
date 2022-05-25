package ch.epfl.sdp.mobile.state.game.delegating

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Piece
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.state.game.core.GameDelegate
import ch.epfl.sdp.mobile.ui.game.TextToSpeechState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DelegatingTextToSpeechState
constructor(
    private val gameDelegate: GameDelegate,
    private val facade: SpeechFacade,
    private val scope: CoroutineScope,
) : TextToSpeechState {

  init {
    scope.launch { synthesizeMoveFlow().collect() }
  }

  private fun synthesizeMoveFlow(): Flow<Pair<Action, Board<Piece<Color>>>> {
    return snapshotFlow { gameDelegate.game }
        .mapNotNull {
          it.previous?.let { prev -> Pair(prev.second, it.board) }
        } // TODO:Reformat this looks ugly
        .onEach { (action, board) -> facade.synthesize(toText(action, board)) }
  }

  override fun onTTsVolumeClick() {
    facade.muted != facade.muted
  }

  private fun toText(action: Action, board: Board<Piece<Color>>): String {
    return when (action) {
      is Action.Move -> moveToText(action, board)
      is Action.Promote -> promoteToText(action, board)
    }
  }

  private fun moveToText(move: Action.Move, board: Board<Piece<Color>>): String {
    return "Good move" // TODO:
  }

  private fun promoteToText(promotion: Action.Promote, board: Board<Piece<Color>>): String {
    return "Promote" // TODO:
  }
}
