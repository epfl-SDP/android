package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.chess.engine.Delta
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.state.StatefulGameScreenActions
import ch.epfl.sdp.mobile.state.StatefulPuzzleGameScreen
import ch.epfl.sdp.mobile.test.infrastructure.assets.fake.FakeAssetManager
import ch.epfl.sdp.mobile.test.ui.game.ChessBoardRobot
import ch.epfl.sdp.mobile.test.ui.game.click
import ch.epfl.sdp.mobile.test.ui.game.play
import ch.epfl.sdp.mobile.ui.game.ChessBoardState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulPuzzleGameScreenTest {
  @get:Rule val rule = createComposeRule()

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun given_aPuzzleOnGameScreen_when_itIsDisplayed_then_expectedInfoDisplayed() = runTest {
    val assets =
        FakeAssetManager(
            csvString =
                "PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl\n" +
                    "008Nz,6k1/2p2ppp/pnp5/B7/2P3PP/1P1bPPR1/r6r/3R2K1 b - - 1 29,d3e2 d1d8,600,101,85,298,backRankMate mate mateIn1 middlegame oneMove,https://lichess.org/HNU4zavC/black#58\n",
        )
    val (_, _, strings) =
        rule.setContentWithTestEnvironment(assets = assets) {
          StatefulPuzzleGameScreen(
              user = user,
              puzzleId = "008Nz",
              actions = StatefulGameScreenActions(onBack = {}, onShowAr = {}),
          )
        }

    rule.onNodeWithText(strings.puzzlesTitle.uppercase()).assertExists()
    rule.onNodeWithText(strings.puzzleSolving("White")).assertExists()
    rule.onNodeWithText(strings.puzzleNumber("008Nz")).assertExists()
    rule.onNodeWithText(strings.puzzleRating("600")).assertExists()
    rule.onNodeWithText("1. Bd3-e2").assertExists()
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun given_aOneMovePuzzle_when_itIsSolved_then_solvedMessageDisplayed() = runTest {
    val assets =
        FakeAssetManager(
            csvString =
                "PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl\n" +
                    "008Nz,6k1/2p2ppp/pnp5/B7/2P3PP/1P1bPPR1/r6r/3R2K1 b - - 1 29,d3e2 d1d8,600,101,85,298,backRankMate mate mateIn1 middlegame oneMove,https://lichess.org/HNU4zavC/black#58\n",
        )
    val (_, _, strings) =
        rule.setContentWithTestEnvironment(assets = assets) {
          StatefulPuzzleGameScreen(
              user = user,
              puzzleId = "008Nz",
              actions = StatefulGameScreenActions(onBack = {}, onShowAr = {}),
          )
        }

    val robot = ChessBoardRobot(rule, strings)

    rule.onNodeWithText(strings.puzzleSolving("White")).assertExists()

    robot.performInput {
      click('d', 1)
      click('d', 8)
    }

    rule.onNodeWithText(strings.puzzleSolved).assertExists()
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun given_aOneMovePuzzle_when_itIsFailed_then_failedMessageDisplayed() = runTest {
    val assets =
        FakeAssetManager(
            csvString =
                "PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl\n" +
                    "008Nz,6k1/2p2ppp/pnp5/B7/2P3PP/1P1bPPR1/r6r/3R2K1 b - - 1 29,d3e2 d1d8,600,101,85,298,backRankMate mate mateIn1 middlegame oneMove,https://lichess.org/HNU4zavC/black#58\n",
        )

    val (_, _, strings) =
        rule.setContentWithTestEnvironment(assets = assets) {
          StatefulPuzzleGameScreen(
              user = user,
              puzzleId = "008Nz",
              actions = StatefulGameScreenActions(onBack = {}, onShowAr = {}),
          )
        }

    val robot = ChessBoardRobot(rule, strings)

    rule.onNodeWithText(strings.puzzleSolving("White")).assertExists()

    robot.performInput {
      click('d', 1)
      click('d', 7)
    }

    rule.onNodeWithText(strings.puzzleSolved).assertDoesNotExist()
    // Wait until puzzle is reset
    rule.waitUntil(timeoutMillis = 2000) {
      robot.hasPiece(3, 7, ChessBoardState.Color.White, ChessBoardState.Rank.Rook)
    }
    rule.onNodeWithText(strings.puzzleSolving("White")).assertExists()
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun given_aMultiMovePuzzle_when_correctMoves_then_puzzleSolved() = runTest {
    val assets =
        FakeAssetManager(
            csvString =
                "PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl\n" +
                    "009tE,6k1/6pp/p1N2p2/1pP2bP1/5P2/8/PPP5/3K4 b - - 1 28,f6g5 c6e7 g8f7 e7f5,600,103,90,340,crushing endgame fork short,https://lichess.org/fUV1iXBx/black#56\n",
        )

    val (_, _, strings) =
        rule.setContentWithTestEnvironment(assets = assets) {
          StatefulPuzzleGameScreen(
              user = user,
              puzzleId = "009tE",
              actions = StatefulGameScreenActions(onBack = {}, onShowAr = {}),
          )
        }

    val robot = ChessBoardRobot(rule, strings)

    rule.onNodeWithText(strings.puzzleSolving("White")).assertExists()
    robot.performInput {
      click('c', 6)
      click('e', 7)
    }

    // Piece was moved as expected and puzzle not reset
    rule.onNodeWithText(strings.puzzleSolving("White")).assertExists()
    robot.assertHasPiece(4, 1, ChessBoardState.Color.White, ChessBoardState.Rank.Knight)

    // Wait until "computer" has played"
    rule.waitUntil(timeoutMillis = 2000) {
      robot.hasPiece(5, 1, ChessBoardState.Color.Black, ChessBoardState.Rank.King)
    }

    robot.performInput {
      click('e', 7)
      click('f', 5)
    }

    rule.onNodeWithText(strings.puzzleSolved).assertExists()
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun given_aPuzzleOnlyPlayerPromotion_when_correctMove_then_isSolved() = runTest {
    val assets =
        FakeAssetManager(
            csvString =
                "PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl\n" +
                    "03SLJ,7k/4RN1P/1r6/6K1/8/8/8/1q6 b - - 1 56,h8g7 h7h8q,1727,76,90,4996,advancedPawn endgame mate mateIn1 oneMove promotion,https://lichess.org/l7BvBZgT/black#112\n",
        )

    val (_, _, strings) =
        rule.setContentWithTestEnvironment(assets = assets) {
          StatefulPuzzleGameScreen(
              user = user,
              puzzleId = "03SLJ",
              actions = StatefulGameScreenActions(onBack = {}, onShowAr = {}),
          )
        }

    val robot = ChessBoardRobot(rule, strings)

    rule.onNodeWithText(strings.puzzleSolving("White"))
    robot.play { tryPromote(Position(7, 1), Delta(0, -1), Rank.Queen) }
    rule.onNodeWithText(strings.puzzleSolved)
  }

  @OptIn(ExperimentalPermissionsApi::class)
  @Test
  fun given_aPuzzleStartsComputerPromotion_when_correctMove_then_isSolved() = runTest {
    val assets =
        FakeAssetManager(
            csvString =
                "PuzzleId,FEN,Moves,Rating,RatingDeviation,Popularity,NbPlays,Themes,GameUrl\n" +
                    "01c1h,4r1k1/QP2pq2/3p2r1/3N4/4P3/8/P4p1P/1R3R1K w - - 2 30,b7b8q f7f3,1524,90,68,656,mate mateIn1 middlegame oneMove,https://lichess.org/HZh6fMAQ#59\n",
        )

    val (_, _, strings) =
        rule.setContentWithTestEnvironment(assets = assets) {
          StatefulPuzzleGameScreen(
              user = user,
              puzzleId = "01c1h",
              actions = StatefulGameScreenActions(onBack = {}, onShowAr = {}),
          )
        }

    val robot = ChessBoardRobot(rule, strings)

    rule.onNodeWithText(strings.puzzleSolving("White"))
    robot.performInput {
      click('f', 7)
      click('f', 3)
    }

    rule.onNodeWithText(strings.puzzleSolved).assertExists()
  }
}
