package ch.epfl.sdp.mobile.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameScreen(
    state: GameScreenState,
    modifier: Modifier = Modifier,
) {
  Column(modifier) {
    TitleText(text = "Chess game")
    MoveList(state.moves, modifier)
    Chessboard(size = 8)
    TitleText(text = "Voice Recognition")
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
                  color = MaterialTheme.colors.secondary,
                  shape = RoundedCornerShape(8.dp)),
  ) {
    items(moves) { move ->
      Text(
          text = move.number.toString() + ". " + move.name,
          color = MaterialTheme.colors.primary,
          style = MaterialTheme.typography.body1,
          modifier = modifier.padding(horizontal = 8.dp, vertical = 8.dp))
    }
  }
}

@Composable
fun Chessboard(modifier: Modifier = Modifier, size: Int = 8) {
  BoxWithConstraints(modifier.fillMaxSize().aspectRatio(1f).padding(16.dp)) {
    Column(modifier = Modifier.squareBorder(2.dp)) {
      for (i in 0 until size) {
        Row(modifier = Modifier.weight(1f)) {
          for (j in 0 until size) {
            Square((i + j) % 2 == 1, modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}

@Composable
fun Square(darkSquare: Boolean, modifier: Modifier = Modifier) {
  val color =
      if (darkSquare) MaterialTheme.colors.onPrimary.copy(alpha = 0.4f) else Color.Transparent

  Canvas(modifier = modifier.fillMaxSize().squareBorder(1.dp)) { drawRect(color = color) }
}

fun Modifier.squareBorder(width: Dp): Modifier = composed {
  border(width = width, color = MaterialTheme.colors.primary, shape = RoundedCornerShape(0.dp))
}

@Composable
fun TitleText(text: String, modifier: Modifier = Modifier) {
  Text(
      text = text,
      color = MaterialTheme.colors.primary,
      style = MaterialTheme.typography.h4,
      modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp))
}
