package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.PawniesIcons
import ch.epfl.sdp.mobile.ui.TournamentsNextStep

/**
 * A green variation of [NextStepBanner].
 *
 * @see NextStepBanner
 */
@Composable
fun GreenNextStepBanner(
    title: String,
    message: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  NextStepBanner(
      title = title,
      message = message,
      onClick = onClick,
      titleColor = PawniesColors.Green800,
      messageColor = PawniesColors.Green500,
      backgroundColor = PawniesColors.Green100.copy(alpha = 0.4f),
      borderColor = PawniesColors.Green200,
      modifier = modifier,
  )
}

/**
 * An orange variation of [NextStepBanner].
 *
 * @see NextStepBanner
 */
@Composable
fun OrangeNextStepBanner(
    title: String,
    message: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  NextStepBanner(
      title = title,
      message = message,
      onClick = onClick,
      titleColor = PawniesColors.Orange500,
      messageColor = PawniesColors.Orange500,
      backgroundColor = PawniesColors.Orange200.copy(alpha = 0.2f),
      borderColor = PawniesColors.Orange500,
      modifier = modifier,
  )
}

/**
 * A banner which indicates a next step of a round.
 *
 * @param title the title of the banner.
 * @param message the message of the banner.
 * @param onClick the listener called when the banner is clicked.
 * @param titleColor the color of the title.
 * @param messageColor the color of the message.
 * @param backgroundColor the color of the background.
 * @param borderColor the color of the border.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun NextStepBanner(
    title: String,
    message: String,
    onClick: () -> Unit,
    titleColor: Color,
    messageColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier =
          modifier
              .fillMaxWidth()
              .border(2.dp, borderColor, RoundedCornerShape(16.dp))
              .clip(RoundedCornerShape(16.dp))
              .clickable { onClick() }
              .background(backgroundColor)
              .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(Modifier.weight(1f)) {
      Text(
          text = title,
          style = MaterialTheme.typography.subtitle1,
          color = titleColor,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )
      Text(
          text = message,
          style = MaterialTheme.typography.body1,
          color = messageColor,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )
    }
    CompositionLocalProvider(LocalContentColor provides titleColor) {
      Icon(PawniesIcons.TournamentsNextStep, null)
    }
  }
}
