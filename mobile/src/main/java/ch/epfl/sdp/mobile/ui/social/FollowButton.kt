package ch.epfl.sdp.mobile.ui.social

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonColors
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.FadeEnterTransition
import ch.epfl.sdp.mobile.ui.FadeExitTransition
import ch.epfl.sdp.mobile.ui.PawniesColors

/**
 * An implementation of [ButtonColors] used to change the color of a button according to whether it
 * is selected.
 *
 * @param selected a [State] of [Boolean] indicating whether the button is selected.
 */
private class FollowButtonColors(selected: State<Boolean>) : ButtonColors {

  /** A [State] of [Boolean] indicating whether the button is selected. */
  private val selected by selected

  @Composable
  override fun backgroundColor(enabled: Boolean): State<Color> {
    return animateColorAsState(if (selected) PawniesColors.Green800 else PawniesColors.Beige050)
  }

  @Composable
  override fun contentColor(enabled: Boolean): State<Color> {
    return animateColorAsState(if (selected) PawniesColors.Green100 else PawniesColors.Green800)
  }
}

/**
 * A variation of [OutlinedButton] used to follow or unfollow a user.
 *
 * @param following a [Boolean] indicating whether the button's user is followed by the current
 * user.
 * @param onClick the action to be performed once the button is clicked.
 * @param modifier the [Modifier] for the composable.
 * @param shape the [RoundedCornerShape] of the button.
 */
@Composable
fun FollowButton(
    following: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = CircleShape,
) {
  val strings = LocalLocalizedStrings.current
  val currentFollowing = rememberUpdatedState(following)
  OutlinedButton(
      onClick = onClick,
      modifier = modifier,
      shape = shape,
      colors = remember { FollowButtonColors(currentFollowing) },
  ) {
    val transition = updateTransition(following, null)
    transition.AnimatedContent(
        contentAlignment = Alignment.Center,
        transitionSpec = {
          FadeEnterTransition with FadeExitTransition using SizeTransform(clip = false)
        },
    ) { following ->
      val text = if (following) strings.socialPerformUnfollow else strings.socialPerformFollow
      val icon = if (following) Icons.Default.Check else Icons.Default.Add
      Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(icon, null)
        Text(text)
      }
    }
  }
}
