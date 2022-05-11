package ch.epfl.sdp.mobile.state.game

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.state.StatefulGameScreenActions
import ch.epfl.sdp.mobile.state.game.core.MatchGameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Piece
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingMovesInfoState
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingPlayersInfoState
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingPromotionMovableChessBoardState
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingPromotionState
import ch.epfl.sdp.mobile.ui.game.*
import kotlinx.coroutines.CoroutineScope

/**
 * Creates a new [ActualGameScreenState].
 *
 * @param actions the actions to perform when navigating.
 * @param user the currently authenticated user.
 * @param match the match to display.
 * @param scope a [CoroutineScope] keeping track of the state lifecycle.
 * @param speechRecognizerState the [SpeechRecognizerState] that speech recognition uses.
 */
fun ActualGameScreenState(
    actions: StatefulGameScreenActions,
    user: AuthenticatedUser,
    match: Match,
    scope: CoroutineScope,
    speechRecognizerState: SpeechRecognizerState,
): ActualGameScreenState {

  // The backing MutableGameDelegate.
  val chessBoard = MatchGameDelegate(match, scope)

  // The delegates which use the MutableGameDelegate.
  val promotions = DelegatingPromotionState(chessBoard)
  val players = DelegatingPlayersInfoState(match, scope, chessBoard)
  val moves = DelegatingMovesInfoState(chessBoard)
  val moveableChessBoard =
      DelegatingPromotionMovableChessBoardState(
          user,
          chessBoard,
          players,
          promotions,
      )

  return ActualGameScreenState(
      actions = actions,
      match = match,
      moveableChessBoard = moveableChessBoard,
      promotionState = promotions,
      movesInfo = moves,
      playersInfo = players,
      speechRecognizer = speechRecognizerState,
  )
}

class ActualGameScreenState
constructor(
    private val actions: StatefulGameScreenActions,
    private val match: Match,
    moveableChessBoard: MovableChessBoardState<Piece>,
    promotionState: PromotionState,
    movesInfo: MovesInfoState,
    playersInfo: PlayersInfoState,
    speechRecognizer: SpeechRecognizerState,
) :
    GameScreenState<Piece>,
    MovableChessBoardState<Piece> by moveableChessBoard,
    PromotionState by promotionState,
    MovesInfoState by movesInfo,
    PlayersInfoState by playersInfo,
    SpeechRecognizerState by speechRecognizer {

  override fun onArClick() = actions.onShowAr(match)
  override fun onBackClick() = actions.onBack()
}
