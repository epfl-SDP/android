package ch.epfl.sdp.mobile.ui.game

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.ui.BlackKing
import ch.epfl.sdp.mobile.ui.ChessIcons
import ch.epfl.sdp.mobile.ui.PawniesColors.Green500
import ch.epfl.sdp.mobile.ui.PawniesColors.Green800
import ch.epfl.sdp.mobile.ui.PawniesColors.Orange200
import ch.epfl.sdp.mobile.ui.WhiteKing
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.White
import ch.epfl.sdp.mobile.ui.game.GameScreenState.Move

/**
 * This screen display an ongoing game of chest
 *
 * @param state the [GameScreenState] that manage the composable contents
 * @param modifier the [Modifier] for the composable
 */
@Composable
fun <Piece : ChessBoardState.Piece> GameScreen(
    state: GameScreenState<Piece>,
    modifier: Modifier = Modifier,
) {
  Column(modifier) {
    Player(White, state.white.name, state.white.message)
    Player(ChessBoardState.Color.Black, state.black.name, state.black.message)
    ChessBoard(state)
    Moves(state.moves)
  }
}

@Composable
fun Player(
    color: ChessBoardState.Color,
    name: String?,
    message: String?,
    modifier: Modifier = Modifier,
) {
  val green = if (color == White) Green500 else Green800
  val icon = if (color == White) ChessIcons.WhiteKing else ChessIcons.BlackKing
  Row(modifier, Arrangement.spacedBy(16.dp), Alignment.CenterVertically) {
    ProvideTextStyle(MaterialTheme.typography.subtitle1) {
      CompositionLocalProvider(LocalContentColor provides green) {
        Icon(icon, null, Modifier.size(24.dp))
        Text(name ?: "")
      }
      Spacer(Modifier.weight(1f, fill = true))
      CompositionLocalProvider(LocalContentColor provides Orange200) { Text(message ?: "") }
    }
  }
}

/**
 * Displays the list of moves that have been played.
 *
 * @param moves A list of [Move] that needs to be displayed
 * @param modifier modifier the [Modifier] for this composable.
 */
@Composable
fun Moves(moves: List<Move>, modifier: Modifier = Modifier) {
  LazyRow(
      horizontalArrangement = Arrangement.Start,
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          modifier
              .padding(16.dp)
              .border(
                  width = 8.dp,
                  color = MaterialTheme.colors.onPrimary,
                  shape = RoundedCornerShape(16.dp)),
  ) {
    itemsIndexed(moves) { index, move ->
      val color = if (index % 2 == 0) Green500 else Green800
      Text(
          text = "$index. ${move.text}",
          color = color,
          style = MaterialTheme.typography.subtitle1,
          modifier = Modifier.padding(8.dp),
      )
    }
  }
}
