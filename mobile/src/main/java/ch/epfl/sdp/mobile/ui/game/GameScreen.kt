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
import ch.epfl.sdp.mobile.ui.social.Person
import ch.epfl.sdp.mobile.ui.social.SocialScreenState

/**
 * This screen display an ongoing game of chest
 *
 * @param state the [GameScreenState] that manage the composable contents
 * @param modifier the [Modifier] for the composable
 */
@Composable
fun <Identifier> GameScreen(
    state: GameScreenState<Identifier>,
    modifier: Modifier = Modifier,
) {
  Column(modifier) {
    TitleText(text = "Chess game")
    MoveList(state.moves)
    ChessBoard(state, modifier = Modifier.padding(16.dp))
    TitleText(text = "VR / Voice Recognition")
  }
}

/**
 * Display the list of moves that have been played
 * @param moves A list of [Move] that need to be displayed
 * @param modifier modifier the [Modifier] for the composable
 */
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

/**
 * A temporary text composable to give some life to the [GameScreen]
 * @param text The [String] to be displayed
 * @param modifier modifier the [Modifier] for the composable
 */
@Composable
fun TitleText(text: String, modifier: Modifier = Modifier) {
  Text(
      text = text,
      color = MaterialTheme.colors.primary,
      style = MaterialTheme.typography.h4,
      modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp))
}
