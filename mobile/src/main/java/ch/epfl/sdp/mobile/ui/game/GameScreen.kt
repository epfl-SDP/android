package ch.epfl.sdp.mobile.ui.game

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.unit.dp
import ch.epfl.sdp.mobile.state.LocalLocalizedStrings
import ch.epfl.sdp.mobile.ui.BlackKing
import ch.epfl.sdp.mobile.ui.ChessIcons
import ch.epfl.sdp.mobile.ui.PawniesColors
import ch.epfl.sdp.mobile.ui.PawniesColors.Green500
import ch.epfl.sdp.mobile.ui.PawniesColors.Green800
import ch.epfl.sdp.mobile.ui.PawniesColors.Orange200
import ch.epfl.sdp.mobile.ui.WhiteKing
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.*
import ch.epfl.sdp.mobile.ui.game.MovesInfoState.*
import ch.epfl.sdp.mobile.ui.game.PlayersInfoState.Message
import ch.epfl.sdp.mobile.ui.game.classic.ClassicChessBoard
import com.google.accompanist.flowlayout.FlowRow

/**
 * This screen display an ongoing game of chest
 *
 * @param state the [GameScreenState] that manage the composable contents
 * @param modifier the [Modifier] for the composable
 * @param contentPadding the [PaddingValues] for this composable.
 */
@Composable
fun <Piece : ChessBoardState.Piece> GameScreen(
    state: GameScreenState<Piece>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {

  val confettiState = rememberConfettiState()

  // Display plenty of confetti if one of the player wins.
  LaunchedEffect(state.black, state.white) {
    if (Message.Checkmate in listOf(state.black.message, state.white.message)) {
      confettiState.party(ConfettiDurationMillis, angle = 270f, spread = 120f)
    }
  }

  Scaffold(
      modifier = modifier,
      scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
      topBar = {
        GameScreenTopBar(
            onBackClick = state::onBackClick,
            onArClick = state::onArClick,
            onListenClick = state::onListenClick,
            onTTsClick = state::onTTsVolumeClick,
            listening = state.listening,
            muted = state.muted,
            modifier = Modifier.fillMaxWidth().confetti(confettiState) { Offset(center.x, 0f) },
        )
      },
      content = { scaffoldPadding ->
        Column(
            modifier =
                modifier
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding)
                    .padding(scaffoldPadding)
                    .padding(start = 32.dp, end = 32.dp, top = 48.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
          Column {
            Player(White, state.white.name, state.white.message)
            Player(Black, state.black.name, state.black.message)
          }
          ProvideTextStyle(MaterialTheme.typography.subtitle1) { ClassicChessBoard(state) }
          Moves(state.moves, Modifier.fillMaxWidth())
        }
      },
  )
}

/**
 * A composable which displays some basic information about a player. Each player may have a name,
 * and an associated message.
 *
 * @param color the [ChessBoardState.Color] of the player.
 * @param name the name of the player.
 * @param message the message associated to the player.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
private fun Player(
    color: ChessBoardState.Color,
    name: String?,
    message: Message,
    modifier: Modifier = Modifier,
) {
  val green = if (color == White) Green500 else Green800
  val icon = if (color == White) ChessIcons.WhiteKing else ChessIcons.BlackKing

  Row(modifier, Arrangement.spacedBy(16.dp), CenterVertically) {
    ProvideTextStyle(MaterialTheme.typography.subtitle1) {
      CompositionLocalProvider(LocalContentColor provides green) {
        Icon(icon, null, Modifier.size(24.dp))
        Text(name ?: "")
      }
      Spacer(Modifier.weight(1f, fill = true))
      CompositionLocalProvider(LocalContentColor provides Orange200) { Text(message.text) }
    }
  }
}

/** Retrieves the text associated with a [GameScreenState.Message]. */
private val Message.text: String
  @Composable
  get() =
      when (this) {
        Message.None -> ""
        Message.YourTurn -> LocalLocalizedStrings.current.gameMessageYourTurn
        Message.InCheck -> LocalLocalizedStrings.current.gameMessageCheck
        Message.Checkmate -> LocalLocalizedStrings.current.gameMessageCheckmate
        Message.Stalemate -> LocalLocalizedStrings.current.gameMessageStalemate
      }

/**
 * Displays the list of moves that have been played.
 *
 * @param moves A list of [Move] that needs to be displayed
 * @param modifier modifier the [Modifier] for this composable.
 */
@Composable
fun Moves(
    moves: List<Move>,
    modifier: Modifier = Modifier,
    firstColor: ChessBoardState.Color = White,
) {
  val (firstTextColor, secondTextColor) =
      when (firstColor) {
        White -> Pair(Green500, Green800)
        Black -> Pair(Green800, Green500)
      }

  FlowRow(
      modifier =
          modifier
              .border(
                  width = 2.dp,
                  color = PawniesColors.Green200,
                  shape = RoundedCornerShape(8.dp),
              )
              .padding(vertical = 8.dp, horizontal = 16.dp),
      mainAxisSpacing = 8.dp,
  ) {
    ProvideTextStyle(MaterialTheme.typography.subtitle1) {
      for ((index, move) in moves.withIndex()) {
        key(index) {
          Text(
              text = "${index + 1}. ${move.text}",
              color = if (index % 2 == 0) firstTextColor else secondTextColor,
          )
        }
      }
    }
  }
}

/** The duration during which some confetti will be spawned when a player wins. */
private const val ConfettiDurationMillis = 2000L
