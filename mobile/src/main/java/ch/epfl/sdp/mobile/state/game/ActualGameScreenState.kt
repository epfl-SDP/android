package ch.epfl.sdp.mobile.state.game

import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.state.StatefulGameScreenActions
import ch.epfl.sdp.mobile.state.game.GameChessBoardState.Piece
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

  val chessBoard = ActualChessBoardState(match, scope)
  val promotions = ActualPromotionState(chessBoard)
  val players = ActualPlayersInfoState(match, scope, chessBoard)
  val moves = ActualMovesInfoState(chessBoard)
  val moveableChessBoard = PromotionMovableChessBoardState(user, chessBoard, players, promotions)

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
    moveableChessBoard: AbstractMovableChessBoardState,
    promotionState: ActualPromotionState,
    movesInfo: MovesInfoState,
    playersInfo: ActualPlayersInfoState,
    speechRecognizer: SpeechRecognizerState,
) :
    MovableChessBoardState<Piece> by moveableChessBoard,
    GameScreenState<Piece>,
    PromotionState by promotionState,
    MovesInfoState by movesInfo,
    PlayersInfoState by playersInfo,
    SpeechRecognizerState by speechRecognizer {

  override fun onArClick() = actions.onShowAr(match)
  override fun onBackClick() = actions.onBack()
}
