package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.BlackKing
import ch.epfl.sdp.mobile.ui.ChessIcons
import ch.epfl.sdp.mobile.ui.game.*
import ch.epfl.sdp.mobile.ui.game.classic.ClassicChessBoard
import ch.epfl.sdp.mobile.ui.puzzles.PuzzleInfoState.*

/**
 * This screen display an ongoing chess puzzle.
 *
 * @param Piece The actual type of [ChessBoardState.Piece] used.
 * @param state The [GameScreenState] that manage the composable contents.
 * @param modifier The [Modifier] for the composable.
 * @param contentPadding The [PaddingValues] for this composable.
 * @param snackbarHostState The [SnackbarHostState] for this composable.
 */
@Composable
fun <Piece : ChessBoardState.Piece> PuzzleGameScreen(
    state: PuzzleGameScreenState<Piece>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
  val strings = LocalLocalizedStrings.current
  val typo = MaterialTheme.typography
  val firstColor =
      when (state.puzzleInfo.playerColor) {
        ChessBoardState.Color.White -> ChessBoardState.Color.Black
        ChessBoardState.Color.Black -> ChessBoardState.Color.White
      }

  val confettiState = rememberConfettiState()

  // Display plenty of confetti if one of the player wins.
  LaunchedEffect(state.puzzleState) {
    if (state.puzzleState == PuzzleState.Solved) {
      confettiState.party(ConfettiDurationMillis, angle = 270f, spread = 120f)
    }
  }

  Scaffold(
      modifier = modifier,
      scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
      topBar = {
        GameScreenTopBar(
            onBackClick = state::onBackClick,
            onArClick = {},
            onListenClick = state::onListenClick,
            listening = state.listening,
            modifier = Modifier.fillMaxWidth().confetti(confettiState) { Offset(center.x, 0f) },
        )
      },
      content = { scaffoldPadding ->
        Column(
            modifier =
                modifier
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding)
                    .padding(scaffoldPadding)
                    .padding(start = 32.dp, end = 32.dp, top = 48.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          Text(
              strings.puzzlesTitle.uppercase(),
              style = typo.h6,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis)
          PuzzleDirective(color = state.puzzleInfo.playerColor, puzzleState = state.puzzleState)
          ProvideTextStyle(typo.subtitle1) { ClassicChessBoard(state) }
          Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                strings.puzzleNumber(state.puzzleInfo.uid),
                style = typo.subtitle2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
            Text(
                strings.puzzleRating(state.puzzleInfo.elo.toString()),
                style = typo.subtitle2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
          }
          Moves(state.moves, Modifier.fillMaxWidth(), firstColor = firstColor)
        }
      },
  )
}

/**
 * A composable which displays some basic information about what is going on in a puzzle.
 *
 * @param color the [ChessBoardState.Color] of the player.
 * @param puzzleState the [PuzzleState] for this puzzle.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun PuzzleDirective(
    color: ChessBoardState.Color,
    puzzleState: PuzzleState,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val colors = MaterialTheme.colors
  Row(modifier, Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
    Icon(ChessIcons.BlackKing, null, Modifier.size(32.dp))
    when (puzzleState) {
      PuzzleState.Solving ->
          Text(
              strings.puzzleSolving(color.toString()),
              color = colors.primary,
              style = MaterialTheme.typography.subtitle1,
          )
      PuzzleState.Failed ->
          Text(
              strings.puzzleFailed,
              color = colors.secondary,
              style = MaterialTheme.typography.subtitle1,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis)
      PuzzleState.Solved ->
          Text(
              strings.puzzleSolved,
              color = colors.primaryVariant,
              style = MaterialTheme.typography.subtitle1,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis)
    }
  }
}

/** The duration during which some confetti will be spawned when a puzzle is solved. */
private const val ConfettiDurationMillis = 2000L
