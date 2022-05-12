package ch.epfl.sdp.mobile.test.state.tournaments

import android.os.SystemClock.sleep
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.epfl.sdp.mobile.application.ProfileDocument
import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.application.TournamentDocument.Companion.StagePools
import ch.epfl.sdp.mobile.application.tournaments.TournamentReference
import ch.epfl.sdp.mobile.infrastructure.persistence.store.arrayUnion
import ch.epfl.sdp.mobile.infrastructure.persistence.store.set
import ch.epfl.sdp.mobile.state.tournaments.StatefulTournamentDetailsScreen
import ch.epfl.sdp.mobile.state.tournaments.TournamentDetailsActions
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.buildAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.buildStore
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.document
import ch.epfl.sdp.mobile.test.state.setContentWithTestEnvironment
import ch.epfl.sdp.mobile.ui.i18n.English
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") {
        document("2", ProfileDocument("1"))
        document("3", ProfileDocument("1"))
        document("4", ProfileDocument("2"))
      }
    }

    val env =
      rule.setContentWithTestEnvironment(store, auth) {
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
          poolSize = 3,
          bestOf = 1,
          stage = StagePools,
          playerIds = listOf("1", "2", "3"),
        ),
      )

    store.collection(TournamentDocument.Collection).document(reference.uid).update {
      arrayUnion(TournamentDocument.Participants, "2", "3", "4")
    }

    rule.onNodeWithText(env.strings.tournamentsDetailsStartEnoughPlayersTitle)
      .assertIsDisplayed()
  }

  @Test
  fun given_tournamentWithAllPlayerJoined_when_startTournament_then_showExactlyOnePool() = runTest {
    val reference = TournamentReference("1")
    val auth = buildAuth { user("email@example.org", "password", "1") }
    val store = buildStore {
      collection("users") {
        document("2", ProfileDocument("1"))
        document("3", ProfileDocument("1"))
        document("4", ProfileDocument("2"))
      }
    }

    val env =
      rule.setContentWithTestEnvironment(store, auth) {
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
          name="testTournamentName",
          adminId = env.user.uid,
          maxPlayers = 3,
          poolSize = 3,
          bestOf = 1,
          playerIds = listOf("1", "2", "3"),
        ),
      )

    store.collection(TournamentDocument.Collection).document(reference.uid).update {
      arrayUnion(TournamentDocument.Participants, "2", "3", "4")
    }

    rule.onNodeWithText(env.strings.tournamentsDetailsStartEnoughPlayersTitle).performClick()
    rule.onNodeWithText(env.strings.tournamentDetailsPoolName(1)).assertExists()
  }
}
