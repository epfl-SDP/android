package ch.epfl.sdp.mobile.state.game.delegating

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Piece
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.tts.TextToSpeechFacade
import ch.epfl.sdp.mobile.infrastructure.tts.TextToSpeech
import ch.epfl.sdp.mobile.state.game.core.GameDelegate
import ch.epfl.sdp.mobile.ui.game.TextToSpeechState
import kotlin.properties.Delegates.notNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DelegatingTextToSpeechState
constructor(
    private val gameDelegate: GameDelegate,
    private val facade: TextToSpeechFacade,
    private val scope: CoroutineScope,
) : TextToSpeechState {

  override var muted by mutableStateOf(false)

  var synthesizer by notNull<TextToSpeech>()
  init {
    scope.launch {
      synthesizer = facade.synthesizer()
      synthesizeMoveFlow().collect()
    }
  }
  private fun synthesizeMoveFlow(): Flow<Pair<Action, Board<Piece<Color>>>> {
    return snapshotFlow { gameDelegate.game }
        .mapNotNull {
          it.previous?.let { prev -> Pair(prev.second, it.board) }
        } // TODO:Reformat this looks ugly
        .onEach { (action, board) -> synthesizer.speak(toText(action, board)) }
  }
  override fun onTTsVolumeClick() {
    muted != muted
    if (muted && synthesizer.isSpeaking()) {
      synthesizer.stop()
    } else {
      scope.launch { synthesizeMoveFlow().collect() }
    }
  }


  private fun toText(action: Action, board: Board<Piece<Color>>): String {
    return when (action) {
      is Action.Move -> moveToText(action, board)
      is Action.Promote -> promoteToText(action, board)
    }
  }

  private fun moveToText(move: Action.Move, board: Board<Piece<Color>>): String {
    return "" // TODO:
  }

  private fun promoteToText(promotion: Action.Promote, board: Board<Piece<Color>>): String {
    return "" // TODO:
  }
}
