package ch.epfl.sdp.mobile.ui.game

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameScreen(
    state: GameScreenState,
    modifier: Modifier = Modifier,
) {
  Column(modifier) {
    TitleText(text = "Chess game")
    MoveList(state.moves)
    ChessBoard(rememberChessBoardState())
    TitleText(text = "VR / Voice Recognition")
  }
}

@Composable
fun MoveList(moves: List<Move>, modifier: Modifier = Modifier) {
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
    items(moves) { move ->
      val color =
          if (move.number % 2 == 1) MaterialTheme.colors.primary
          else MaterialTheme.colors.primaryVariant
      Text(
          text = move.number.toString() + ". " + move.name,
          color = color,
          style = MaterialTheme.typography.subtitle1,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp))
    }
  }
}

@Composable
fun TitleText(text: String, modifier: Modifier = Modifier) {
  Text(
      text = text,
      color = MaterialTheme.colors.primary,
      style = MaterialTheme.typography.h4,
      modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp))
}
