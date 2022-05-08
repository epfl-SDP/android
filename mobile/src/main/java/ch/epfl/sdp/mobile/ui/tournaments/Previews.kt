package ch.epfl.sdp.mobile.ui.tournaments

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ch.epfl.sdp.mobile.ui.PawniesTheme
import kotlinx.coroutines.CoroutineScope

// FIXME : REMOVE THESE PREVIEW COMPOSABLES

@Preview
@Composable
private fun TournamentsPreview() = PawniesTheme {
  val scope = rememberCoroutineScope()
  val state = remember(scope) { TournamentDetailsScreenStateImpl(scope) }
  TournamentDetails(
      state = state,
      modifier = Modifier.fillMaxSize(),
  )
}

private val matches =
    listOf(
        object : TournamentMatch {
          override val firstPlayerName = "Alexandre"
          override val secondPlayerName = "Matthieu"
          override val result: TournamentMatch.Result = TournamentMatch.Result.Ongoing
        },
        object : TournamentMatch {
          override val firstPlayerName = "Chau"
          override val secondPlayerName = "Matthieu"
          override val result: TournamentMatch.Result = TournamentMatch.Result.Draw
        },
        object : TournamentMatch {
          override val firstPlayerName = "Alexandre"
          override val secondPlayerName = "Chau"
          override val result: TournamentMatch.Result = TournamentMatch.Result.FirstWon
        },
        object : TournamentMatch {
          override val firstPlayerName = "Alexandre"
          override val secondPlayerName = "Lars"
          override val result: TournamentMatch.Result = TournamentMatch.Result.SecondWon
        },
        object : TournamentMatch {
          override val firstPlayerName = "Lars"
          override val secondPlayerName = "Chau"
          override val result: TournamentMatch.Result = TournamentMatch.Result.Ongoing
        },
        object : TournamentMatch {
          override val firstPlayerName = "Lars"
          override val secondPlayerName = "Matthieu"
          override val result: TournamentMatch.Result = TournamentMatch.Result.Draw
        },
    )

class TournamentDetailsScreenStateImpl(
    private val scope: CoroutineScope,
) : TournamentDetailsState<IndexedPoolMember, TournamentMatch> {

  override val badge = BadgeType.Join
  override val title = "EPFL Masters"
  override val pools = List(5) { IndexPoolInfo(it, scope) }
  override val finals =
      listOf(
          TournamentsFinalsRound("1/8", matches),
          TournamentsFinalsRound("1/4", matches),
          TournamentsFinalsRound("1/2", matches),
      )

  override fun onBadgeClick() = Unit
  override fun onWatchMatchClick(match: TournamentMatch) = Unit
  override fun onCloseClick() = Unit
}

data class IndexedPoolMember(
    val index: Int,
    private val nameState: State<String>,
    override val total: PoolScore?,
) : PoolMember {

  constructor(
      index: Int,
      name: String,
      total: PoolScore?,
  ) : this(index, mutableStateOf(name), total)

  override val name by nameState
}

class IndexPoolInfo(
    private val index: Int,
    private val scope: CoroutineScope,
) : PoolInfo<IndexedPoolMember> {

  private var matthieu = mutableStateOf("Matthieu")

  init {
    // scope.launch {
    //   while (true) {
    //
    //     matthieu.value = "Matthieu Burguburu The Overflow"
    //     delay(3000)
    //
    //     matthieu.value = "Matthieu"
    //     delay(3000)
    //   }
    // }
  }

  override val members =
      listOf(
          IndexedPoolMember(0, "Alexandre", 2),
          IndexedPoolMember(1, "Badr", 10),
          IndexedPoolMember(2, "Chau", 16),
          IndexedPoolMember(3, "Fouad", 5),
          IndexedPoolMember(4, "Lars", 6),
          IndexedPoolMember(5, matthieu, 4),
      )

  override fun IndexedPoolMember.scoreAgainst(other: IndexedPoolMember) =
      ((index + other.index) * 373) % 5

  override val name: String = "Pool #$index"

  override val status: PoolInfo.Status = PoolInfo.Status.StillOpen

  override var startNextRoundEnabled by mutableStateOf((index % 2) == 0)
    private set

  override fun onStartNextRound() {
    startNextRoundEnabled = false
  }
}
