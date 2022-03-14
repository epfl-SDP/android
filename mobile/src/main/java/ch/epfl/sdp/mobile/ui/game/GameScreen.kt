package ch.epfl.sdp.mobile.ui.game

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameScreen(
    state: GameScreenState,
    modifier: Modifier = Modifier,
) {
  Column(modifier) {
    TempText(text = "Chess game")
    MoveList(state.moves, modifier)
    TempText(text = "Voice Recognition")
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
fun TempText(text: String, modifier: Modifier = Modifier) {
  Text(
      text = text,
      color = MaterialTheme.colors.primary,
      style = MaterialTheme.typography.h4,
      modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp))
}
