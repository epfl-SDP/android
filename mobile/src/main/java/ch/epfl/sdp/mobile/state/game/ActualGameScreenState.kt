@file:OptIn(ExperimentalPermissionsApi::class)

package ch.epfl.sdp.mobile.state.game

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.state.StatefulGameScreenActions
import ch.epfl.sdp.mobile.state.game.core.MatchGameDelegate
import ch.epfl.sdp.mobile.state.game.delegating.*
import ch.epfl.sdp.mobile.state.game.delegating.DelegatingChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.*
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import kotlinx.coroutines.CoroutineScope

/**
 * Creates a new [ActualGameScreenState].
 *
 * @param actions the state of actions to perform when navigating.
 * @param strings the state of currently used strings.
 * @param user the currently authenticated user.
 * @param match the match to display.
 * @param permission the [PermissionState] for the microphone permission.
 * @param speechFacade the [SpeechFacade] to access speech recognition facilities.
 * @param snackbarHostState the [SnackbarHostState] to display some info.
 * @param scope a [CoroutineScope] keeping track of the state lifecycle.
 */
fun ActualGameScreenState(
    actions: State<StatefulGameScreenActions>,
    strings: State<LocalizedStrings>,
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

  val textToSpeech = DelegatingTextToSpeechState(chessBoard, speechFacade, strings, scope)

  return ActualGameScreenState(
      actions = actions,
      match = match,
      movableChessBoard = movableChessBoard,
      promotionState = promotions,
      movesInfo = moves,
      playersInfo = players,
      speechRecognizer = speechRecognizer,
      textToSpeech = textToSpeech,
  )
}

/**
 * An implementation of [GameScreenState] which delegates its behaviors to different underlying
 * states.
 *
 * @param actions the [StatefulGameScreenActions] for this screen.
 * @property match the [Match] for this screen.
 * @param movableChessBoard the underlying [MovableChessBoardState].
 * @param promotionState the underlying [PromotionState].
 * @param movesInfo the underlying [MovesInfoState].
 * @param playersInfo the underlying [PlayersInfoState].
 * @param speechRecognizer the underlying [SpeechRecognizerState].
 * @param textToSpeech the underlying [TextToSpeechState].
 */
class ActualGameScreenState
constructor(
    actions: State<StatefulGameScreenActions>,
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

  private val actions by actions

  override fun onArClick() = actions.onShowAr(match)
  override fun onBackClick() = actions.onBack()
}
