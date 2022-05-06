package ch.epfl.sdp.mobile.ui.puzzles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
fun PuzzleGameScreen(
    state: PuzzleGameScreenState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  Scaffold(
      modifier = modifier,
      content = { scaffoldPadding ->
        Column(
            modifier =
                modifier
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding)
                    .padding(scaffoldPadding)
                    .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
          Text("Puzzle id: ${state.puzzle.uid}")
          Text("Playing as: ${state.puzzle.boardSnapshot.playing}")
          Text("Elo: ${state.puzzle.elo}")
          Text("Moves: ${state.puzzle.puzzleMoves}")
          Text("Castling rights: ${state.puzzle.boardSnapshot.castlingRights}")
          Button(onClick = { state.solve() }) {
            Text("Solve")
          }
        }
      },
  )
}
