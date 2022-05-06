package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.application.authentication.AuthenticationFacade
import ch.epfl.sdp.mobile.application.chess.ChessFacade
import ch.epfl.sdp.mobile.application.social.SocialFacade
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.state.ProvideFacades
import ch.epfl.sdp.mobile.state.StatefulPuzzleSelectionScreen
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.FakeAssetManager
import ch.epfl.sdp.mobile.test.infrastructure.persistence.auth.emptyAuth
import ch.epfl.sdp.mobile.test.infrastructure.persistence.store.emptyStore
import ch.epfl.sdp.mobile.test.infrastructure.speech.FailingSpeechRecognizerFactory
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulPuzzleSelectionScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun given_statefulPuzzleSelectionScreen_when_itIsDisplayed_then_ExpectedPuzzlesAreDisplayed() =
      runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val assets =
        FakeAssetManager(
            csvString =
                "PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl\n" +
                    "009tE,6k1/6pp/p1N2p2/1pP2bP1/5P2/8/PPP5/3K4 b - - 1 28,f6g5 c6e7 g8f7 e7f5,600,103,90,340,crushing endgame fork short,https://lichess.org/fUV1iXBx/black#56\n" +
                    "005N7,r6k/2q3pp/8/2p1n3/R1Qp4/7P/2PB1PP1/6K1 b - - 0 32,e5c4 a4a8 c7b8 a8b8,721,93,93,957,backRankMate endgame hangingPiece mate mateIn2 short,https://lichess.org/jxZhmGhg/black#64\n" +
                    "008Nz,6k1/2p2ppp/pnp5/B7/2P3PP/1P1bPPR1/r6r/3R2K1 b - - 1 29,d3e2 d1d8,600,101,85,298,backRankMate mate mateIn1 middlegame oneMove,https://lichess.org/HNU4zavC/black#58\n" +
                    "00Bn4,1k6/pp6/4nNp1/P6p/3pr3/7P/3R1PPK/8 b - - 0 40,e4e5 f6d7 b8c7 d7e5,613,105,92,262,crushing endgame fork short,https://lichess.org/jvMUtZF5/black#80\n" +
                    "00GRa,1r3rk1/2p1Nppb/p2nq3/1p2p1Pp/4Qn1P/2P1N3/PPB2P1K/3R2R1 b - - 5 28,e6e7 e4h7,630,106,93,335,kingsideAttack mate mateIn1 middlegame oneMove,https://lichess.org/QiJhfG8J/black#56\n" +
                    "00IaZ,4R3/1k2R3/3K2p1/1P6/1P6/2rp3r/8/8 b - - 3 45,b7b6 e8b8,946,84,91,419,endgame master mate mateIn1 oneMove rookEndgame,https://lichess.org/MA0bo8dV/black#90\n" +
                    "001wr,r4rk1/p3ppbp/Pp1q1np1/3PpbB1/2B5/2N5/1PPQ1PPP/3RR1K1 w - - 4 18,f2f3 d6c5 g1h1 c5c4,1045,75,96,828,advantage fork master masterVsMaster middlegame short,https://lichess.org/KnJ2mojX#35\n" +
                    "009Wc,1r3rk1/1pq2pbp/p1p1pnp1/2N1N3/3P4/1QP5/PP3PPP/3RR1K1 w - - 2 19,e5d7 f6d7 c5d7 c7d7,1066,74,95,1148,crushing middlegame short,https://lichess.org/WnthPrdl#37\n" +
                    "00Aas,3r1rk1/1p2q1pp/5p2/8/1P1n4/6Q1/PPbB1PPP/R2B1RK1 w - - 9 20,d1c2 d4e2 g1h1 e2g3 f2g3 d8d2,1309,75,100,437,crushing fork long middlegame,https://lichess.org/wYjuq3zz#39\n" +
                    "003nQ,6rk/pp6/2n5/3ppn1p/3p4/2P2P1q/PP3QNB/R4R1K w - - 2 29,f1g1 f5g3 f2g3 g8g3,1334,93,65,22,crushing kingsideAttack master middlegame pin short,https://lichess.org/p85fiZcl#57\n" +
                    "009Os,r2b2k1/1p3q1p/p2p4/3P2p1/2P1PRQr/8/P2B3P/2R4K w - - 1 29,g4g3 h4f4 d2f4 f7f4 g3f4 g5f4,1447,93,76,34,crushing long middlegame,https://lichess.org/AdjEnXlm#57\n" +
                    "00C7m,8/5k2/1P4R1/6PK/1r6/8/8/8 w - - 1 58,h5h6 b4h4,1347,339,100,10,endgame mate mateIn1 oneMove rookEndgame,https://lichess.org/ZbuTrTYp#115\n",
        )

    val authFacade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val speechFacade = SpeechFacade(FailingSpeechRecognizerFactory)

    authFacade.signUpWithEmail("email@example.org", "user", "password")
    authFacade.signInWithEmail("email@example.org", "password")

    val userAuthenticated = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    rule.setContentWithLocalizedStrings {
      ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade) {
        StatefulPuzzleSelectionScreen(
            user = userAuthenticated,
            onPuzzleItemClick = {},
        )
      }
    }
    rule.onNodeWithText("Unsolved Puzzles").assertExists()
    rule.onNodeWithText("009tE", substring = true).performScrollTo() // black pawn
    rule.onNodeWithText("005N7", substring = true).performScrollTo() // black knight
    rule.onNodeWithText("008Nz", substring = true).performScrollTo() // black bishop
    rule.onNodeWithText("00Bn4", substring = true).performScrollTo() // black rook
    rule.onNodeWithText("00GRa", substring = true).performScrollTo() // black queen
    rule.onNodeWithText("00IaZ", substring = true).performScrollTo() // black king
    rule.onNodeWithText("001wr", substring = true).performScrollTo() // white pawn
    rule.onNodeWithText("009Wc", substring = true).performScrollTo() // white knight
    rule.onNodeWithText("00Aas", substring = true).performScrollTo() // white bishop
    rule.onNodeWithText("003nQ", substring = true).performScrollTo() // white rook
    rule.onNodeWithText("009Os", substring = true).performScrollTo() // white queen
    rule.onNodeWithText("00C7m", substring = true).performScrollTo() // white king
  }

  @Test
  fun given_statefulPuzzleSelectionScreen_when_erroredCSV_then_errorPuzzleDisplayed() = runTest {
    val auth = emptyAuth()
    val store = emptyStore()
    val assets =
        FakeAssetManager(
            csvString =
                "PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl\n" +
                    "009tE,6k1/6pp/p1N2p2/1pP2bP1/5P2/8/PPP5/3K4 b - - 1 28,f6g5 c6e7 g8f7 e7f5,600,103,90,340,crushing endgame fork short,https://lichess.org/fUV1iXBx/black#56\n" +
                    "Bruh,This is not FEN, Not UCI either..., Seven??,74,97,1444,crushing hangingPiece long middlegame,https://lichess.org/787zsVup/black#48\n")

    val authFacade = AuthenticationFacade(auth, store)
    val socialFacade = SocialFacade(auth, store)
    val chessFacade = ChessFacade(auth, store, assets)
    val speechFacade = SpeechFacade(FailingSpeechRecognizerFactory)

    authFacade.signUpWithEmail("email@example.org", "user", "password")
    authFacade.signInWithEmail("email@example.org", "password")

    val userAuthenticated = authFacade.currentUser.filterIsInstance<AuthenticatedUser>().first()
    rule.setContentWithLocalizedStrings {
      ProvideFacades(authFacade, socialFacade, chessFacade, speechFacade) {
        StatefulPuzzleSelectionScreen(
            user = userAuthenticated,
            onPuzzleItemClick = {},
        )
      }
    }
    rule.onNodeWithText("Unsolved Puzzles").assertExists()
    rule.onNodeWithText("009tE", substring = true).assertExists()
    rule.onNodeWithText("Error", substring = true).assertExists()
  }
}
