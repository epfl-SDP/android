package ch.epfl.sdp.mobile.ui.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog as NativeDialog
import androidx.compose.ui.window.DialogProperties
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.*
import ch.epfl.sdp.mobile.ui.game.classic.contentDescription
import ch.epfl.sdp.mobile.ui.game.classic.icon
import ch.epfl.sdp.mobile.ui.prepare_game.Dialog

/**
 * An icon which can be selected and indicates the choice of the user when it comes to promoting a
 * piece.
 *
 * @param color the [ChessBoardState.Color] of the user who will promote a piece.
 * @param rank the [ChessBoardState.Rank] of the promotion choice.
 * @param selected true if the piece choice is currently selected.
 * @param onClick a callback which will be called when the icon is pressed.
 * @param modifier the [Modifier] for this composable
 */
@Composable
private fun PromoteIcon(
    color: ChessBoardState.Color,
    rank: ChessBoardState.Rank,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  val tint by animateColorAsState(if (selected) PawniesColors.Orange200 else PawniesColors.Green800)
  Icon(
      painter = rank.icon(color = color),
      contentDescription = rank.contentDescription(strings),
      tint = tint,
      modifier =
          modifier
              .clip(CircleShape)
              .clickable { onClick() }
              .background(PawniesColors.Green100.copy(alpha = 0.4f), CircleShape)
              .padding(8.dp)
              .size(48.dp),
  )
}

/**
 * The content of the promote dialog.
 *
 * @param color the [ChessBoardState.Color] of the user who will promote a piece.
 * @param selected the [ChessBoardState.Rank] of the currently selected promotion choice.
 * @param onSelectRank a callback which will be called when a promotion action is clicked.
 * @param onConfirm a callback which will be called when the user wants to submit a choice.
 * @param modifier the [Modifier] for this composable.
 * @param confirmEnabled the [Modifier] for this composable.
 * @param choices the available choices for ranks.
 */
@Composable
private fun PromoteDialogContent(
    color: ChessBoardState.Color,
    selected: ChessBoardState.Rank?,
    onSelectRank: (ChessBoardState.Rank) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmEnabled: Boolean = true,
    choices: List<ChessBoardState.Rank> = listOf(Bishop, Rook, Knight, Queen)
) {
  val strings = LocalLocalizedStrings.current
  Dialog(
      modifier = modifier.width(IntrinsicSize.Min),
      confirm = {
        OutlinedButton(
            onClick = onConfirm,
            enabled = confirmEnabled,
            shape = CircleShape,
        ) { Text(strings.gamePromoteConfirm) }
      },
      content = {
        Column(Modifier.padding(16.dp), Arrangement.spacedBy(16.dp)) {
          Text(strings.gamePromoteTitle, style = MaterialTheme.typography.subtitle1)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (rank in choices) {
              key(rank) {
                PromoteIcon(color, rank, selected == rank, onClick = { onSelectRank(rank) })
              }
            }
          }
        }
      },
  )
}

/**
 * A window dialog which lets the user pick the result of the promotion of a piece.
 *
 * @param color the [ChessBoardState.Color] of the user who will promote a piece.
 * @param selected the [ChessBoardState.Rank] of the currently selected promotion choice.
 * @param onSelectRank a callback which will be called when a promotion action is clicked.
 * @param onConfirm a callback which will be called when the user wants to submit a choice.
 * @param modifier the [Modifier] for this composable.
 * @param confirmEnabled the [Modifier] for this composable.
 * @param choices the available choices for ranks.
 */
@Composable
fun PromoteDialog(
    color: ChessBoardState.Color,
    selected: ChessBoardState.Rank?,
    onSelectRank: (ChessBoardState.Rank) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmEnabled: Boolean = true,
    choices: List<ChessBoardState.Rank> = listOf(Bishop, Rook, Knight, Queen)
) {
  NativeDialog(
      onDismissRequest = { /* Ignored. */},
      properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
  ) {
    PromoteDialogContent(
        color = color,
        selected = selected,
        onSelectRank = onSelectRank,
        onConfirm = onConfirm,
        modifier = modifier,
        confirmEnabled = confirmEnabled,
        choices = choices,
    )
  }
}
