@file:OptIn(ExperimentalPermissionsApi::class)

package ch.epfl.sdp.mobile.state.game

import androidx.compose.material.SnackbarHostState
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.state.StatefulGameScreenActions
import ch.epfl.sdp.mobile.state.game.core.MatchGameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.*
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import kotlinx.coroutines.CoroutineScope

/**
 * Creates a new [ActualGameScreenState].
 *
 * @param actions the actions to perform when navigating.
 * @param user the currently authenticated user.
 * @param match the match to display.
 * @param permission the [PermissionState] for the microphone permission.
 * @param speechFacade the [SpeechFacade] to acecess speech recognition facilities.
 * @param snackbarHostState the [SnackbarHostState] to display some info.
 * @param scope a [CoroutineScope] keeping track of the state lifecycle.
 */
fun ActualGameScreenState(
    actions: StatefulGameScreenActions,
    user: AuthenticatedUser,
    match: Match,
    permission: PermissionState,
    speechFacade: SpeechFacade,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
): ActualGameScreenState {

  // The backing MutableGameDelegate.
  val chessBoard = MatchGameDelegate(match, scope)

  // The delegates which use the MutableGameDelegate.
  val promotions = DelegatingPromotionState(chessBoard)
  val players = DelegatingPlayersInfoState(match, scope, chessBoard)
  val moves = DelegatingMovesInfoState(chessBoard)
  val movableChessBoard =
      DelegatingPromotionMovableChessBoardState(
          user,
          chessBoard,
          players,
          promotions,
      )
  val speechRecognizer =
      DelegatingSpeechRecognizerState(
          chessBoard,
          permission,
          speechFacade,
          snackbarHostState,
          scope,
      )

  val ttsSynthesizer = DelegatingTextToSpeechState(chessBoard,speechFacade,scope)

  return ActualGameScreenState(
      actions = actions,
      match = match,
      movableChessBoard = movableChessBoard,
      promotionState = promotions,
      movesInfo = moves,
      playersInfo = players,
      speechRecognizer = speechRecognizer,
      textToSpeech = ttsSynthesizer,
  )
}

/**
 * An implementation of [GameScreenState] which delegates its behaviors to different underlying
 * states.
 *
 * @param actions the [StatefulGameScreenActions] for this screen.
 * @param match the [Match] for this screen.
 * @param movableChessBoard the underlying [MovableChessBoardState].
 * @param promotionState the underlying [PromotionState].
 * @param movesInfo the underlying [MovesInfoState].
 * @param playersInfo the underlying [PlayersInfoState].
 * @param speechRecognizer the underlying [SpeechRecognizerState].
 */
class ActualGameScreenState
constructor(
    private val actions: StatefulGameScreenActions,
    private val match: Match,
    movableChessBoard: MovableChessBoardState<Piece>,
    promotionState: PromotionState,
    movesInfo: MovesInfoState,
    playersInfo: PlayersInfoState,
    speechRecognizer: SpeechRecognizerState,
    textToSpeech: TextToSpeechState,
) :
    GameScreenState<Piece>,
    MovableChessBoardState<Piece> by movableChessBoard,
    PromotionState by promotionState,
    MovesInfoState by movesInfo,
    PlayersInfoState by playersInfo,
    SpeechRecognizerState by speechRecognizer,
    TextToSpeechState by textToSpeech {

  override fun onArClick() = actions.onShowAr(match)
  override fun onBackClick() = actions.onBack()
}
