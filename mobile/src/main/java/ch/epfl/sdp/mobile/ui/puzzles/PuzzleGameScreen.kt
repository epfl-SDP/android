package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.BlackKing
import ch.epfl.sdp.mobile.ui.ChessIcons
import ch.epfl.sdp.mobile.ui.game.*

/**
 * This screen display an ongoing chess puzzle
 *
 * @param state the [GameScreenState] that manage the composable contents
 * @param modifier the [Modifier] for the composable
 * @param contentPadding the [PaddingValues] for this composable.
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
  Scaffold(
      modifier = modifier,
      scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
      topBar = {
        GameScreenTopBar(
            onBackClick = state::onBackClick,
            onArClick = {},
            onListenClick = state::onListenClick,
            listening = state.listening,
            modifier = Modifier.fillMaxWidth(),
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
          Text(strings.puzzlesTitle.uppercase(), style = typo.h6)
          PuzzleDirective(color = state.puzzleInfo.playerColor, puzzleState = state.puzzleState)
          ProvideTextStyle(typo.subtitle1) { ClassicChessBoard(state) }
          Spacer(Modifier)
          Column {
            Text(strings.puzzleNumber(state.puzzleInfo.uid), style = typo.subtitle2)
            Text(strings.puzzleRating(state.puzzleInfo.elo.toString()), style = typo.subtitle2)
          }
          Moves(state.moves, Modifier.fillMaxWidth())
        }
      },
  )
}

/**
 * A composable which displays some basic information about what is going on in a puzzle.
 *
 * @param color the [ChessBoardState.Color] of the player.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun PuzzleDirective(
    color: ChessBoardState.Color,
    puzzleState: PuzzleGameScreenState.PuzzleState,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val colors = MaterialTheme.colors

  @Composable
  fun Directive() =
      when (puzzleState) {
        PuzzleGameScreenState.PuzzleState.Solving ->
            Text(
                strings.puzzleFindMove(color.toString()),
                color = colors.primary,
                style = MaterialTheme.typography.subtitle1,
            )
        PuzzleGameScreenState.PuzzleState.Failed ->
            Text(
                strings.puzzleFailed,
                color = colors.secondary,
                style = MaterialTheme.typography.subtitle1,
            )
        PuzzleGameScreenState.PuzzleState.Solved ->
            Text(
                strings.puzzleSolved,
                color = colors.primaryVariant,
                style = MaterialTheme.typography.subtitle1,
            )
      }

  Row(modifier, Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
    Icon(ChessIcons.BlackKing, null, Modifier.size(32.dp))
    Directive()
  }
}
