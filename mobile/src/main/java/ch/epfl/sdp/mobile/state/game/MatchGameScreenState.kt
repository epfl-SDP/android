package ch.epfl.sdp.mobile.state.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.notation.AlgebraicNotation.toAlgebraicNotation
import ch.epfl.sdp.mobile.state.StatefulGameScreenActions
import ch.epfl.sdp.mobile.state.game.MatchChessBoardState.Companion.toEngineRank
import ch.epfl.sdp.mobile.state.game.MatchChessBoardState.Companion.toPosition
import ch.epfl.sdp.mobile.state.game.MatchChessBoardState.Companion.toRank
import ch.epfl.sdp.mobile.state.game.MatchChessBoardState.Piece
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import ch.epfl.sdp.mobile.ui.game.GameScreenState
import ch.epfl.sdp.mobile.ui.game.GameScreenState.Message
import ch.epfl.sdp.mobile.ui.game.GameScreenState.Move
import ch.epfl.sdp.mobile.ui.game.PromotionState
import ch.epfl.sdp.mobile.ui.game.SpeechRecognizerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
private constructor(
    private val actions: StatefulGameScreenActions,
    private val user: AuthenticatedUser,
    private val match: Match,
    private val scope: CoroutineScope,
    private val chessBoardDelegate: MatchChessBoardState,
    private val speechRecognizerDelegate: SpeechRecognizerState,
) :
    GameScreenState<Piece>,
    PromotionState,
    ChessBoardState<Piece> by chessBoardDelegate,
    SpeechRecognizerState by speechRecognizerDelegate {

  /**
   * Creates a new [MatchGameScreenState].
   *
   * @param actions the actions to perform when navigating.
   * @param user the currently authenticated user.
   * @param match the match to display.
   * @param scope a [CoroutineScope] keeping track of the state lifecycle.
   * @param speechRecognizerState the [SpeechRecognizerState] that speech recognition uses.
   */
  constructor(
      actions: StatefulGameScreenActions,
      user: AuthenticatedUser,
      match: Match,
      scope: CoroutineScope,
      speechRecognizerState: SpeechRecognizerState,
  ) : this(actions, user, match, scope, MatchChessBoardState(match, scope), speechRecognizerState)

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

  /** The currently selected [Position] of the board. */
  override var selectedPosition by mutableStateOf<ChessBoardState.Position?>(null)
    private set

  override val availableMoves: Set<ChessBoardState.Position>
    // Display all the possible moves for all the pieces on the board.
    get() {
      val position = selectedPosition ?: return emptySet()
      return chessBoardDelegate
          .game
          .actions(Position(position.x, position.y))
          .mapNotNull { it.from + it.delta }
          .map { it.toPosition() }
          .toSet()
    }

  override fun onDropPiece(piece: Piece, endPosition: ChessBoardState.Position) {
    val startPosition = pieces.entries.firstOrNull { it.value == piece }?.key ?: return
    tryPerformMove(startPosition, endPosition)
  }

  override fun onPositionClick(position: ChessBoardState.Position) {
    val from = selectedPosition
    if (from == null) {
      selectedPosition = position
    } else {
      tryPerformMove(from, position)
    }
  }

  /**
   * Attempts to perform a move from the given [ChessBoardState.Position] to the given
   * [ChessBoardState.Position]. If the move can't be performed, this will result in a no-op.
   *
   * @param from the start [ChessBoardState.Position].
   * @param to the end [ChessBoardState.Position].
   */
  private fun tryPerformMove(
      from: ChessBoardState.Position,
      to: ChessBoardState.Position,
  ) {
    // Hide the current selection.
    selectedPosition = null

    val step = chessBoardDelegate.game.nextStep as? NextStep.MovePiece ?: return

    val currentPlayingId =
        when (step.turn) {
          Color.Black -> blackProfile?.uid
          Color.White -> whiteProfile?.uid
        }

    if (currentPlayingId == user.uid) {
      // TODO: Update game locally first, then verify upload was successful?
      val actions =
          chessBoardDelegate
              .game
              .actions(Position(from.x, from.y))
              .filter { it.from + it.delta == Position(to.x, to.y) }
              .toList()
      if (actions.size == 1) {
        scope.launch {
          val newGame = step.move(actions.first())
          match.update(newGame)
        }
      } else {
        promotionFrom = from
        promotionTo = to
        choices = actions.filterIsInstance<Action.Promote>().map { it.rank.toRank() }
      }
    }
  }

  // Promotion management.

  override var choices: List<ChessBoardState.Rank> by mutableStateOf(emptyList())
    private set

  override val confirmEnabled: Boolean
    get() = selection != null

  override var selection: ChessBoardState.Rank? by mutableStateOf(null)
    private set

  private var promotionFrom by mutableStateOf(ChessBoardState.Position(0, 0))
  private var promotionTo by mutableStateOf(ChessBoardState.Position(0, 0))

  override fun onConfirm() {
    val rank = selection ?: return
    selection = null
    val action =
        Action.Promote(
            from = Position(promotionFrom.x, promotionFrom.y),
            to = Position(promotionTo.x, promotionTo.y),
            rank = rank.toEngineRank(),
        )
    val step = chessBoardDelegate.game.nextStep as? NextStep.MovePiece ?: return
    scope.launch {
      val newGame = step.move(action)
      match.update(newGame)
      choices = emptyList()
    }
  }

  override fun onSelect(rank: ChessBoardState.Rank) {
    selection = if (rank == selection) null else rank
  }

  override val moves: List<Move>
    get() = chessBoardDelegate.game.toAlgebraicNotation().map(::Move)
}
