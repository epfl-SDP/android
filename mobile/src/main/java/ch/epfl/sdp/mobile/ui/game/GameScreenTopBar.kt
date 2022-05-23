package ch.epfl.sdp.mobile.ui.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.*
import ch.epfl.sdp.mobile.ui.PawniesColors.Beige050
import ch.epfl.sdp.mobile.ui.PawniesColors.Green100
import ch.epfl.sdp.mobile.ui.PawniesColors.Green800

/**
 * The top app which will be displayed on the [GameScreen].
 *
 * @param onBackClick a callback called when the close icon is pressed.
 * @param onArClick a callback called when the AR icon is pressed.
 * @param onListenClick a callback called when the listening icon is pressed.
 * @param listening true if the listening indicator should be displayed.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun GameScreenTopBar(
    onBackClick: () -> Unit,
    onArClick: () -> Unit,
    onListenClick: () -> Unit,
    onTTsClick: () -> Unit,
    listening: Boolean,
    muted: Boolean,
    modifier: Modifier = Modifier,
) {
  TopAppBar(
      modifier = modifier,
      title = {},
      elevation = 0.dp,
      navigationIcon = {
        IconButton(onBackClick) {
          Icon(Icons.Branded.GameClose, LocalLocalizedStrings.current.gameBack)
        }
      },
      actions = {
        TTsVolumeButton(onClick = onTTsClick, muted = muted)
        Spacer(Modifier.width(8.dp))
        ArButton(onClick = onArClick)
        Spacer(Modifier.width(8.dp))
        ListeningButton(onClick = onListenClick, selected = listening)
        Spacer(Modifier.width(8.dp))
      },
      backgroundColor = MaterialTheme.colors.background,
  )
}

/**
 * A button which can be pressed to display the game in AR.
 *
 * @param onClick a callback called when the user clicks the button.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun ArButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  OutlinedButton(
      onClick = onClick,
      shape = CircleShape,
      modifier = modifier.height(48.dp).widthIn(min = 48.dp),
      contentPadding = PaddingValues(),
      border = BorderStroke(Dp.Hairline, Green100),
  ) { Icon(PawniesIcons.ArView, LocalLocalizedStrings.current.gameShowAr) }
}

/**
 * A button which indicates if the application is currently listening to voice commands by the user.
 *
 * @param onClick a callback called when the user clicks the button.
 * @param selected true if the listening indicator should be displayed.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun ListeningButton(
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
  val currentSelected = rememberUpdatedState(selected)
  val colors = remember(currentSelected) { ListeningButtonColors(currentSelected) }
  val borderWidth by animateDpAsState(if (selected) 2.dp else Dp.Hairline)
  Button(
      onClick = onClick,
      shape = CircleShape,
      modifier = modifier.height(48.dp).widthIn(min = 48.dp),
      contentPadding = PaddingValues(),
      colors = colors,
      elevation = null,
      border = BorderStroke(borderWidth, Green100),
  ) {
    AnimatedContent(
        targetState = selected,
        transitionSpec = { FadeEnterTransition with FadeExitTransition },
        contentAlignment = Alignment.CenterStart,
    ) { on ->
      if (on) {
        Row(Modifier.padding(horizontal = 16.dp), spacedBy(8.dp), CenterVertically) {
          Icon(Icons.Branded.GameMicOn, LocalLocalizedStrings.current.gameMicOnContentDescription)
          Text(LocalLocalizedStrings.current.gameListening)
        }
      } else {
        Icon(Icons.Branded.GameMicOff, LocalLocalizedStrings.current.gameMicOffContentDescription)
      }
    }
  }
}

/**
 * The [ButtonColors] for the listening button.
 *
 * @param listening the current listening [State].
 */
private class ListeningButtonColors(
    listening: State<Boolean>,
) : ButtonColors {

  private val listening by listening

  @Composable
  override fun backgroundColor(enabled: Boolean): State<Color> {
    return animateColorAsState(if (listening) Green800 else Beige050)
  }

  @Composable
  override fun contentColor(enabled: Boolean): State<Color> {
    return animateColorAsState(if (listening) Green100 else Green800)
  }
}

@Composable
private fun TTsVolumeButton(
    onClick: () -> Unit,
    muted: Boolean,
    modifier: Modifier = Modifier
) {
  val currentVolume = rememberUpdatedState(muted)
  val colors = remember(currentVolume) { TTsVolumeButtonColors(currentVolume) }
  OutlinedButton(
      onClick = onClick,
      shape = CircleShape,
      modifier = modifier.height(48.dp).widthIn(min = 48.dp),
      contentPadding = PaddingValues(),
      colors = colors,
      border = BorderStroke(Dp.Hairline, Green100),
  ) {
    if (muted) {
      Icon(PawniesIcons.TTsOff, LocalLocalizedStrings.current.gameTTsOffContentDescription)
    } else {
      Icon(PawniesIcons.TTsOn, LocalLocalizedStrings.current.gameTTsOnContentDescription)
    }
  }
}

/**
 * The [ButtonColors] for the text to speech volume button.
 *
 * @param muted the current volume [State].
 */
private class TTsVolumeButtonColors(
    muted: State<Boolean>,
) : ButtonColors {

  private val muted by muted

  @Composable
  override fun backgroundColor(enabled: Boolean): State<Color> {
    return animateColorAsState(if (!muted) Green800 else Beige050)
  }

  @Composable
  override fun contentColor(enabled: Boolean): State<Color> {
    return animateColorAsState(if (!muted) Green100 else Green800)
  }
}
