package ch.epfl.sdp.mobile.state

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.Profile.Color
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.chess.Match
import ch.epfl.sdp.mobile.application.chess.engine.NextStep
import ch.epfl.sdp.mobile.application.chess.notation.serialize
import ch.epfl.sdp.mobile.ui.setting.SettingScreenState
import ch.epfl.sdp.mobile.ui.setting.SettingsScreen
import ch.epfl.sdp.mobile.ui.social.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthenticatedUserProfileScreenState(
    private val user: AuthenticatedUser,
    private val chessFacade: ChessFacade,
    private val scope: CoroutineScope,
) : SettingScreenState {
  override val email = user.email
  override var pastGamesCount by mutableStateOf(0)
  override val puzzlesCount = 0
  override var matches by mutableStateOf(emptyList<ChessMatch>())

  init {
    scope.launch {
      chessMatches(user, chessFacade)
          .onEach {
            matches = it
            pastGamesCount = matches.size
          }
          .collect()
    }
  }
  override val backgroundColor = Color.Orange
  override val name = user.name
  override val emoji = user.emoji
  override val followed = user.followed

  override fun onSettingsClick() {}
  override fun onEditClick() {}
}

/**
 * A stateful composable to visit setting page of the loged-in user
 *
 * @param user the current logged-in user.
 * @param modifier the [Modifier] for this composable.
 */
@Composable
fun StatefulSettingsScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
  val chessFacade = LocalChessFacade.current
  val scope = rememberCoroutineScope()
  val state = remember(user) { AuthenticatedUserProfileScreenState(user, chessFacade, scope) }
  SettingsScreen(state, modifier, contentPadding)
}

/**
 * Determines the [MatchResult] of a given [Match].
 *
 * @param color the [Color] of the current player.
 * @param match the [Match] to determine its [MatchResult].
 */
private suspend fun determineMatchOutcome(
    color: ch.epfl.sdp.mobile.application.chess.engine.Color,
    match: Match
): MatchResult? {
  val game = match.game.filterNotNull().first()
  return when (game.nextStep) {
    is NextStep.Checkmate -> {
      if (color == (game.nextStep as NextStep.Checkmate).winner) Win(MatchResult.Reason.CHECKMATE)
      else Loss(MatchResult.Reason.CHECKMATE)
    }
    is NextStep.Stalemate -> Tie
    else -> Tie // Should be changed to null but left for test purposes.
  }
}

/**
 * Returns a [Flow] of a list of [ChessMatch] of the matches played by the given profile.
 *
 * @param profile the profile we want to know their played [ChessMatch]es
 */
fun chessMatches(profile: Profile, chessFacade: ChessFacade): Flow<List<ChessMatch>> {
  val matches = chessFacade.matches(profile)
  return matches.map { list -> list.mapNotNull { match -> match.toChessMatch(profile.uid) } }
}

/**
 * Transforms a given [Match] to a [ChessMatch].
 *
 * @param currentUid the uid of the current user.
 */
private suspend fun Match.toChessMatch(
    currentUid: String,
): ChessMatch? {
  val black = this.black.filterNotNull().first()
  val white = this.white.filterNotNull().first()
  val adversary = if (black.uid == currentUid) white.name else black.name
  val color =
      if (black.uid == currentUid) ch.epfl.sdp.mobile.application.chess.engine.Color.Black
      else ch.epfl.sdp.mobile.application.chess.engine.Color.White
  val result = determineMatchOutcome(color, this)
  val game = this.game.first()
  val moveNum = game.serialize().size
  return result?.let { ChessMatch(adversary, result, moveNum) }
}
