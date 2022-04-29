package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ch.epfl.sdp.mobile.application.Profile
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.ui.profile.ProfileScreenState
import ch.epfl.sdp.mobile.ui.social.ChessMatch
import ch.epfl.sdp.mobile.ui.social.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * An implementation of the [ProfileScreenState] that performs a given profile's [ChessMatch]
 * requests.
 *
 * @param user the given [Profile].
 * @param chessFacade the [ChessFacade] used to perform some requests.
 * @param scope the [CoroutineScope] on which requests are performed.
 */
class StatefulProfileScreen(
    user: Profile,
    private val chessFacade: ChessFacade,
    private val scope: CoroutineScope,
) : ProfileScreenState, Person by ProfileAdapter(user) {
  override var matches by mutableStateOf(emptyList<ChessMatch>())
    private set
  override var pastGamesCount = matches.size
    private set
  init {
    scope.launch {
      fetchForUser(user, chessFacade).collect { list ->
        matches = list.map { createChessMatch(it, user) }
      }
    }
  }
}
