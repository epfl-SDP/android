package ch.epfl.sdp.mobile.test.state.tournaments

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.ChessDocument
import ch.epfl.sdp.mobile.application.ChessMetadata
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.arrayUnion
import ch.epfl.sdp.mobile.infrastructure.persistence.store.get
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import ch.epfl.sdp.mobile.state.tournaments.StatefulTournamentDetailsScreen
import ch.epfl.sdp.mobile.state.tournaments.TournamentDetailsActions
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.state.setContentWithTestEnvironment
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulTournamentDetailsScreenTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun given_statefulTournamentDetails_when_clickingBack_then_callsCallback() = runTest {
    val channel = Channel<Unit>(1)
    val env =
        rule.setContentWithTestEnvironment {
          StatefulTournamentDetailsScreen(
              user = user,
              reference = TournamentReference(""),
              actions = TournamentDetailsActions(onBackClick = { channel.trySend(Unit) }),
          )
        }
    rule.onNodeWithContentDescription(env.strings.tournamentDetailsBackContentDescription)
        .performClick()
    assertThat(channel.tryReceive().getOrNull()).isNotNull()
  }

  @Test
  fun given_statefulTournamentDetails_when_displayingTournament_then_showsRightTitle() = runTest {
    val reference = TournamentReference("1")
    val store = buildStore {
      collection(TournamentDocument.Collection) {
        document(reference.uid, TournamentDocument(name = "Sample"))
      }
    }
    rule.setContentWithTestEnvironment(store = store) {
      StatefulTournamentDetailsScreen(
          user = user,
          reference = reference,
          actions = TournamentDetailsActions(onBackClick = {}),
      )
    }
    rule.onNodeWithText("Sample", ignoreCase = true).assertIsDisplayed()
  }

  @Test
  fun given_notStartedTournament_when_enoughParticipants_then_showsStartBanner() = runTest {
    val reference = TournamentReference("1")
    val env =
        rule.setContentWithTestEnvironment {
          StatefulTournamentDetailsScreen(
              user = user,
              reference = reference,
              actions = TournamentDetailsActions(onBackClick = {}),
          )
        }
    env.infrastructure
        .store
        .collection(TournamentDocument.Collection)
        .document(reference.uid)
        .set(
            TournamentDocument(
                adminId = env.user.uid,
                maxPlayers = 3,
                playerIds = listOf("1", "2", "3"),
            ),
        )
    rule.onNodeWithText(env.strings.tournamentsDetailsStartEnoughPlayersTitle).assertIsDisplayed()
    rule.onNodeWithText(env.strings.tournamentsDetailsStartEnoughPlayersSubtitle)
        .assertIsDisplayed()
  }

  @Test
  fun given_notStartedTournament_when_notEnoughParticipants_then_showsStartBanner() = runTest {
    val reference = TournamentReference("1")
    val env =
        rule.setContentWithTestEnvironment {
          StatefulTournamentDetailsScreen(
              user = user,
              reference = reference,
              actions = TournamentDetailsActions(onBackClick = {}),
          )
        }
    env.infrastructure
        .store
        .collection(TournamentDocument.Collection)
        .document(reference.uid)
        .set(
            TournamentDocument(
                adminId = env.user.uid,
                maxPlayers = 20,
                playerIds = listOf("1", "2", "3"),
            ),
        )
    rule.onNodeWithText(env.strings.tournamentsDetailsStartNotEnoughPlayersTitle)
        .assertIsDisplayed()
    rule.onNodeWithText(env.strings.tournamentsDetailsStartNotEnoughPlayersSubtitle)
        .assertIsDisplayed()
  }

  @Test
  fun given_tournament_when_allParticipantsJoined_then_showStartBanner() = runTest {
    val reference = TournamentReference("1")
    val store = buildStore {
      collection("users") {
        document("1", ProfileDocument("1", "Player 1"))
        document("2", ProfileDocument("2", "Player 2"))
        document("3", ProfileDocument("3", "Player 3"))
      }
    }

    val env =
        rule.setContentWithTestEnvironment(store) {
          StatefulTournamentDetailsScreen(
              user = user,
              reference = reference,
              actions = TournamentDetailsActions(onBackClick = {}),
          )
        }

    env.infrastructure
        .store
        .collection(TournamentDocument.Collection)
        .document(reference.uid)
        .set(
            TournamentDocument(
                name = "testTournamentName",
                adminId = env.user.uid,
                maxPlayers = 3,
                poolSize = 3,
                bestOf = 1,
                playerIds = listOf("1"),
            ),
        )

    env.infrastructure
        .store
        .collection(TournamentDocument.Collection)
        .document(reference.uid)
        .update { arrayUnion(TournamentDocument.Participants, "2", "3") }

    rule.onNodeWithText(env.strings.tournamentsDetailsStartEnoughPlayersTitle).assertIsDisplayed()
  }

  @Test
  fun given_tournamentWithAllPlayerJoined_when_startTournament_then_showExactlyOnePool() = runTest {
    val reference = TournamentReference("1")
    val store = buildStore {
      collection("users") {
        document("1", ProfileDocument("1", "Player 1"))
        document("2", ProfileDocument("2", "Player 2"))
        document("3", ProfileDocument("3", "Player 3"))
      }
    }

    val env =
        rule.setContentWithTestEnvironment(store) {
          StatefulTournamentDetailsScreen(
              user = user,
              reference = reference,
              actions = TournamentDetailsActions(onBackClick = {}),
          )
        }

    env.infrastructure
        .store
        .collection(TournamentDocument.Collection)
        .document(reference.uid)
        .set(
            TournamentDocument(
                name = "testTournamentName",
                adminId = env.user.uid,
                maxPlayers = 3,
                poolSize = 3,
                bestOf = 1,
                playerIds = listOf("1", "2", "3"),
            ),
        )

    rule.onNodeWithText(env.strings.tournamentsDetailsStartEnoughPlayersTitle).performClick()
    rule.onNodeWithText(env.strings.tournamentDetailsPoolName(1)).assertExists()
  }

  @Test
  fun given_tournamentWith2Players_when_goingThroughTournament_then_showsWinLossFinal() = runTest {
    val reference = TournamentReference("1")
    val store = buildStore {
      collection("users") {
        document("1", ProfileDocument("1", "Player 1"))
        document("2", ProfileDocument("2", "Player 2"))
      }
    }

    val env =
        rule.setContentWithTestEnvironment(store) {
          StatefulTournamentDetailsScreen(
              user = user,
              reference = reference,
              actions = TournamentDetailsActions(onBackClick = {}),
          )
        }

    env.infrastructure
        .store
        .collection(TournamentDocument.Collection)
        .document(reference.uid)
        .set(
            TournamentDocument(
                name = "testTournamentName",
                adminId = env.user.uid,
                maxPlayers = 2,
                poolSize = 2,
                bestOf = 1,
                eliminationRounds = 1,
                playerIds = listOf("1", "2"),
            ),
        )

    rule.onNodeWithText(env.strings.tournamentsDetailsStartEnoughPlayersTitle).performClick()
    rule.onNodeWithText(env.strings.tournamentsPoolStartNextRound).performClick()
    rule.onNodeWithText(env.strings.tournamentsDetailsStartDirectEliminationTitle).performClick()
    rule.onNodeWithText(env.strings.tournamentsDetailsFinals).performClick()
    rule.awaitIdle()

    val finalGame =
        env.infrastructure
            .store
            .collection("games")
            .whereEquals("roundDepth", 1)
            .get<ChessDocument>()
            .first()

    env.infrastructure
        .store
        .collection("games")
        .document(finalGame.uid ?: "")
        .set(
            finalGame.copy(
                metadata =
                    ChessMetadata(
                        status = ChessMetadata.WhiteWon,
                        whiteName = "Player1",
                        blackName = "Player2",
                    ),
            ),
        )

    rule.onNodeWithText(env.strings.tournamentsDetailsMatchWon).assertExists()
    rule.onNodeWithText(env.strings.tournamentsDetailsMatchLost).assertExists()
  }
}
