package ch.epfl.sdp.mobile.state.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Promote
import ch.epfl.sdp.mobile.application.chess.notation.AlgebraicNotation.toAlgebraicNotation
import ch.epfl.sdp.mobile.state.StatefulGameScreenActions
import ch.epfl.sdp.mobile.state.game.GameChessBoardState.Companion.toRank
import ch.epfl.sdp.mobile.state.game.GameChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.GameScreenState
import ch.epfl.sdp.mobile.ui.game.GameScreenState.Message
import ch.epfl.sdp.mobile.ui.game.GameScreenState.Move
import ch.epfl.sdp.mobile.ui.game.PromotionState
import ch.epfl.sdp.mobile.ui.game.SpeechRecognizerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Creates a new [MatchGameScreenState].
 *
 * @param actions the actions to perform when navigating.
 * @param user the currently authenticated user.
 * @param match the match to display.
 * @param scope a [CoroutineScope] keeping track of the state lifecycle.
 * @param speechRecognizerState the [SpeechRecognizerState] that speech recognition uses.
 */
fun MatchGameScreenState(
    actions: StatefulGameScreenActions,
    user: AuthenticatedUser,
    match: Match,
    scope: CoroutineScope,
    speechRecognizerState: SpeechRecognizerState,
): MatchGameScreenState {
  val chessBoardDelegate = MatchChessBoardState(match, scope)
  return MatchGameScreenState(
      actions,
      user,
      match,
      scope,
      chessBoardDelegate,
      GamePromotionState(chessBoardDelegate),
      speechRecognizerState,
  )
}

/**
 * An implementation of [GameScreenState] and [PromotionState] that starts with default chess
 * positions, can move pieces and has a static move list.
 *
 * @param actions the actions to perform when navigating.
 * @param user the currently authenticated user.
 * @param match the match to display.
 * @param scope a [CoroutineScope] keeping track of the state lifecycle.
 * @param chessBoardDelegate the [MatchChessBoardState] to delegate to.
 * @param speechRecognizerDelegate the [SpeechRecognizerState] to delegate to
 */
class MatchGameScreenState
constructor(
    private val actions: StatefulGameScreenActions,
    private val user: AuthenticatedUser,
    private val match: Match,
    private val scope: CoroutineScope,
    private val chessBoardDelegate: GameChessBoardState,
    private val promotionState: GamePromotionState,
    private val speechRecognizerDelegate: SpeechRecognizerState,
) :
    AbstractMovableChessBoardState(chessBoardDelegate),
    GameScreenState<Piece>,
    PromotionState by promotionState,
    SpeechRecognizerState by speechRecognizerDelegate {

  override fun onArClick() = actions.onShowAr(match)

  override fun onBackClick() = actions.onBack()

  private var whiteProfile by mutableStateOf<Profile?>(null)
  private var blackProfile by mutableStateOf<Profile?>(null)

  override val white: GameScreenState.Player
    get() = GameScreenState.Player(whiteProfile?.name, message(Color.White))

  override val black: GameScreenState.Player
    get() = GameScreenState.Player(blackProfile?.name, message(Color.Black))

  /**
   * Computes the [Message] to display depending on the player color.
   *
   * @param color the [Color] of the player in the engine.
   */
  private fun message(color: Color): Message {
    return when (val step = chessBoardDelegate.game.nextStep) {
      is NextStep.Checkmate -> if (step.winner == color) Message.None else Message.Checkmate
      is NextStep.MovePiece ->
          if (step.turn == color) if (step.inCheck) Message.InCheck else Message.YourTurn
          else Message.None
      NextStep.Stalemate -> if (color == Color.White) Message.Stalemate else Message.None
    }
  }

  init {
    scope.launch { match.white.collect { whiteProfile = it } }
    scope.launch { match.black.collect { blackProfile = it } }
  }

  override val moves: List<Move>
    get() = game.toAlgebraicNotation().map(::Move)

  override fun tryPerformMove(from: ChessBoardState.Position, to: ChessBoardState.Position) {
    val available = chessBoardDelegate.availableActions(from, to)
    val step = game.nextStep as? NextStep.MovePiece ?: return
    val currentPlayingId =
        when (step.turn) {
          Color.Black -> blackProfile?.uid
          Color.White -> whiteProfile?.uid
        }
    if (currentPlayingId == user.uid) {
      if (available.size == 1) {
        game = step.move(available.first())
      } else {
        promotionState.updatePromotion(
            from = from,
            to = to,
            choices = available.filterIsInstance<Promote>().map { it.rank.toRank() },
        )
      }
    }
  }
}
