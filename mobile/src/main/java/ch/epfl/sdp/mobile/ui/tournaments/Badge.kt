package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.tournaments.BadgeType.*

/** The type of the badge. */
enum class BadgeType {

  /** Indicates that this tournament can be joined. */
  Join,

  /** Indicates that we participate in this tournament. */
  Participant,

  /** Indicates that we administer this tournament. */
  Admin,
}

/**
 * A badge which represents whether the user may join an existing tournament, or if they're already
 * included as a participant or administrator in it.
 *
 * @param type the [BadgeType] for this badge.
 * @param onClick the callback which is called when the user clicks.
 * @param modifier the [Modifier] for this composable.
 * @param enabled true if the [Badge] may be clicked, false otherwise.
 */
@Composable
fun Badge(
    type: BadgeType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
  val currentType = rememberUpdatedState(type)
  val colors = remember(currentType) { BadgeColors(currentType) }
  Button(
      onClick = onClick,
      modifier = modifier.heightIn(min = 32.dp),
      colors = colors,
      enabled = enabled,
      elevation = null,
      border = BorderStroke(2.dp, PawniesColors.Green500),
      shape = CircleShape,
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
  ) {
    ProvideTextStyle(MaterialTheme.typography.subtitle1) {
      AnimatedContent(
          targetState = type,
          transitionSpec = {
            FadeEnterTransition with FadeExitTransition using SizeTransform(clip = false)
          },
      ) { current -> BadgeContent(current) }
    }
  }
}

/**
 * The content of the badge which displays the status.
 *
 * @param type the type of the badge.
 * @param modifier the [Modifier] for this badge.
 */
@Composable
private fun BadgeContent(
    type: BadgeType,
    modifier: Modifier = Modifier,
) {
  val strings = LocalLocalizedStrings.current
  when (type) {
    Join ->
        Row(modifier, spacedBy(8.dp), CenterVertically) {
          Icon(PawniesIcons.Add, null)
          Text(strings.tournamentsBadgeJoin, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    Participant ->
        Text(
            strings.tournamentsBadgeParticipant,
            modifier,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis)
    Admin ->
        Text(
            strings.tournamentsBadgeAdmin, modifier, maxLines = 1, overflow = TextOverflow.Ellipsis)
  }
}

/**
 * The [ButtonColors] which should be applied to the [Badge].
 *
 * @param type the current type of the badge.
 */
private class BadgeColors(type: State<BadgeType>) : ButtonColors {

  /** The current [BadgeType]. */
  private val type by type

  /** Returns the background color. */
  private fun background(): Color =
      when (type) {
        Join, Participant -> PawniesColors.Green100.copy(alpha = 0f)
        Admin -> PawniesColors.Green100
      }

  /** Returns the foreground color. */
  private fun content(): Color = PawniesColors.Green500

  @Composable override fun backgroundColor(enabled: Boolean) = animateColorAsState(background())
  @Composable override fun contentColor(enabled: Boolean) = animateColorAsState(content())
}
