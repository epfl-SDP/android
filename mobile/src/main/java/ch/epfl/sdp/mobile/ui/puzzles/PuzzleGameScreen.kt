package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        verticalArrangement = Arrangement.spacedBy(32.dp),
      ) {
        Text("Puzzle #${state.puzzleInfo.uid}")
        ProvideTextStyle(MaterialTheme.typography.subtitle1) { ClassicChessBoard(state) }
        Moves(state.moves, Modifier.fillMaxWidth())
      }
    },
  )
}
