@file:OptIn(ExperimentalPermissionsApi::class)

package ch.epfl.sdp.mobile.state

import android.Manifest.permission.RECORD_AUDIO
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.*
import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.notation.AlgebraicNotation.toAlgebraicNotation
import ch.epfl.sdp.mobile.state.game.MatchChessBoardState
import ch.epfl.sdp.mobile.state.game.MatchChessBoardState.Companion.toEngineRank
import ch.epfl.sdp.mobile.state.game.MatchChessBoardState.Companion.toPosition
import ch.epfl.sdp.mobile.state.game.MatchChessBoardState.Companion.toRank
import ch.epfl.sdp.mobile.ui.game.*
import ch.epfl.sdp.mobile.ui.game.GameScreenState.*
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleGameScreen
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleGameScreenState
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleGameScreenState.PuzzleState
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The [StatefulPuzzleGameScreen] to be used for the Navigation
 *
 * @param user the currently logged-in user.
 * @param puzzleId the identifier for the puzzle.
 * @param actions the [StatefulGameScreenActions] to perform.
 * @param modifier the [Modifier] for the composable.
 * @param paddingValues the [PaddingValues] for this composable.
 * @param audioPermissionState the [PermissionState] which provides access to audio content.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StatefulPuzzleGameScreen(
    user: AuthenticatedUser,
    puzzleId: String,
    actions: StatefulGameScreenActions,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    audioPermissionState: PermissionState = rememberPermissionState(RECORD_AUDIO),
) {
  val chessFacade = LocalChessFacade.current
  val speechFacade = LocalSpeechFacade.current

  val scope = rememberCoroutineScope()
  val puzzle = chessFacade.puzzle(uid = puzzleId) ?: Puzzle()
  val userState = rememberUpdatedState(user)
  val currentActions = rememberUpdatedState(actions)

  val snackbarHostState = remember { SnackbarHostState() }
  val speechRecognizerState =
      remember(audioPermissionState, speechFacade, snackbarHostState, scope) {
        SnackbarSpeechRecognizerState(
            permission = audioPermissionState,
            facade = speechFacade,
            snackbarHostState = snackbarHostState,
            scope = scope,
        )
      }

  val puzzleGameScreenState =
      remember(currentActions, userState, puzzle, chessFacade, scope) {
        SnapshotPuzzleGameScreenState(
            currentActions = currentActions,
            user = userState,
            puzzle = puzzle,
            scope = scope,
            facade = chessFacade,
            speechRecognizerState = speechRecognizerState,
        )
      }

  StatefulPromoteDialog(puzzleGameScreenState)

  PuzzleGameScreen(
      state = puzzleGameScreenState,
      modifier = modifier,
      contentPadding = paddingValues,
      snackbarHostState = snackbarHostState,
  )
}

class SnapshotPuzzleGameScreenState(
    currentActions: State<StatefulGameScreenActions>,
    private val user: State<AuthenticatedUser>,
    private val puzzle: Puzzle,
    private val scope: CoroutineScope,
    private val facade: ChessFacade,
    private val speechRecognizerState: SpeechRecognizerState,
) :
    PuzzleGameScreenState<ChessBoardState.Piece>,
    PromotionState,
    MovableChessBoardState<ChessBoardState.Piece>,
    SpeechRecognizerState by speechRecognizerState {

  private val actions by currentActions

  /** The current [Game], which is updated when the [Match] progresses. */
  var game: Game by mutableStateOf(puzzle.baseGame())
    private set
  private var currentMoveNumber: Int = 1 // Always start with playing "computer" move

  override var puzzleState: PuzzleState by mutableStateOf(PuzzleState.Solving)

  override fun onBackClick() = actions.onBack()

  /** The currently selected [Position] of the board. */
  override var selectedPosition by mutableStateOf<ChessBoardState.Position?>(null)
    private set

  override val availableMoves: Set<ChessBoardState.Position>
    // Display all the possible moves for all the pieces on the board.
    get() {
      val position = selectedPosition ?: return emptySet()
      return game.actions(Position(position.x, position.y))
          .mapNotNull { it.from + it.delta }
          .map { it.toPosition() }
          .toSet()
    }

  override fun onDropPiece(piece: ChessBoardState.Piece, endPosition: ChessBoardState.Position) {
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

  private fun tryPerformMove(
      from: ChessBoardState.Position,
      to: ChessBoardState.Position,
  ) {
    // Hide the current selection.
    selectedPosition = null

    val step = game.nextStep as? NextStep.MovePiece ?: return

    val userCurrentlyPlaying =
        step.turn ==
            when (puzzleInfo.playerColor) {
              ChessBoardState.Color.White -> Color.White
              ChessBoardState.Color.Black -> Color.Black
            }

    if (userCurrentlyPlaying && currentMoveNumber < puzzle.puzzleMoves.size) {
      val actions =
          game.actions(Position(from.x, from.y))
              .filter { it.from + it.delta == Position(to.x, to.y) }
              .toList()

      if (actions.size == 1) {
        val action = actions.first()
        playPuzzleAction(step, action)
      } else {
        promotionFrom = from
        promotionTo = to
        choices = actions.filterIsInstance<Action.Promote>().map { it.rank.toRank() }
      }
    }
  }

  private fun playPuzzleAction(step: NextStep.MovePiece, action: Action, delay: Long = 1000) {
    val expected = puzzle.puzzleMoves[currentMoveNumber]

    game = step.move(action)
    currentMoveNumber++

    if (action == expected) {
      scope.launch { attemptNextBotMove(delay) }
    } else {
      scope.launch { resetPuzzle(delay) }
    }
  }

  private suspend fun resetPuzzle(delay: Long) {
    puzzleState = PuzzleState.Failed
    delay(delay)
    game = puzzle.baseGame()
    currentMoveNumber = 1
    puzzleState = PuzzleState.Solving
  }

  private suspend fun attemptNextBotMove(delay: Long) {
    if (currentMoveNumber < puzzle.puzzleMoves.size) {
      delay(delay)
      val action = puzzle.puzzleMoves[currentMoveNumber]
      val step = game.nextStep as? NextStep.MovePiece ?: return

      game = step.move(action)
      currentMoveNumber++
    } else {
      puzzleState = PuzzleState.Solved
      scope.launch { user.value.solvePuzzle(puzzle) }
    }
  }

  override val moves: List<Move>
    get() = game.toAlgebraicNotation().map(::Move)

  override val puzzleInfo: PuzzleInfo
    get() = puzzle.toPuzzleInfoAdapter()

  override val pieces: Map<ChessBoardState.Position, MatchChessBoardState.Piece>
    get() =
        game.board.associate { (pos, piece) ->
          pos.toPosition() to MatchChessBoardState.Piece(piece)
        }

  override val checkPosition: ChessBoardState.Position?
    get() {
      val nextStep = game.nextStep
      if (nextStep !is NextStep.MovePiece || !nextStep.inCheck) return null
      return game.board
          .firstNotNullOf { (position, piece) ->
            position.takeIf { piece.color == nextStep.turn && piece.rank == Rank.King }
          }
          .toPosition()
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
    val step = game.nextStep as? NextStep.MovePiece ?: return
    playPuzzleAction(step, action)
    choices = emptyList()
  }

  override fun onSelect(rank: ChessBoardState.Rank) {
    selection = if (rank == selection) null else rank
  }
}
