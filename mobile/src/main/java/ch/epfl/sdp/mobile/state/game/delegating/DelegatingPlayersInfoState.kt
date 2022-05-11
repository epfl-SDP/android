package ch.epfl.sdp.mobile.state.game.delegating

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.state.game.core.GameDelegate
import ch.epfl.sdp.mobile.ui.game.PlayersInfoState
import ch.epfl.sdp.mobile.ui.game.PlayersInfoState.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * An implementation of [PlayersInfoState] which uses a [GameDelegate], as well as a [Match] to
 * access the profile information of the players.
 *
 * @param match the [Match] used to fetch the player profiles.
 * @param scope the [CoroutineScope] to fetch the profiles..
 * @param delegate the underlying [GameDelegate].
 */
class DelegatingPlayersInfoState(
    match: Match,
    scope: CoroutineScope,
    private val delegate: GameDelegate,
) : PlayersInfoState {

  /** The [Profile] of the white player, if it has been loaded successfully. */
  var whiteProfile by mutableStateOf<Profile?>(null)
    private set

  /** The [Profile] of the black player, if it has been loaded successfully. */
  var blackProfile by mutableStateOf<Profile?>(null)
    private set

  init {
    scope.launch { match.white.collect { whiteProfile = it } }
    scope.launch { match.black.collect { blackProfile = it } }
  }

  override val white: PlayersInfoState.Player
    get() = PlayersInfoState.Player(whiteProfile?.name, message(Color.White))

  override val black: PlayersInfoState.Player
    get() = PlayersInfoState.Player(blackProfile?.name, message(Color.Black))

  /**
   * Computes the [Message] to display depending on the player color.
   *
   * @param color the [Color] of the player in the engine.
   */
  private fun message(color: Color): Message {
    return when (val step = delegate.game.nextStep) {
      is NextStep.Checkmate -> if (step.winner == color) Message.None else Message.Checkmate
      is NextStep.MovePiece ->
          if (step.turn == color) if (step.inCheck) Message.InCheck else Message.YourTurn
          else Message.None
      NextStep.Stalemate -> if (color == Color.White) Message.Stalemate else Message.None
    }
  }
}
