package ch.epfl.sdp.mobile.test.state

import ch.epfl.sdp.mobile.ui.game.ChessBoardState.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Color.*
import ch.epfl.sdp.mobile.ui.game.ChessBoardState.Rank.*

class SnapshotChessBoardStateTest {
  /*
    @Test
    @Composable
    fun movingAPawn_IsSuccessful() {
      val auth = emptyAuth()
      val store = buildStore {
        collection("users") {
          document("userId1", ProfileDocument(uid = "userId1"))
          document("userId2", ProfileDocument(uid = "userId2"))
        }
        collection("games") {
          document(
              "gameId",
              ChessDocument(uid = "uid", moves = null, whiteId = "userId1", blackId = "userId2"))
        }
      }
      LocalChessFacade provides ChessFacade(auth, store)
      val chessFacade = LocalChessFacade.current

      //Setting up two distinct players on different [SnapshotChessBoardState]s

      // Player 1
      val user1 = mockk<AuthenticatedUser>()
      every { user1.uid } returns "userId1"
      val scopeUser1 = rememberCoroutineScope()
      val matchUser1 =
          remember(chessFacade, user1) {
                chessFacade.fetchMatchesForUser(user1).map { it.firstOrNull() ?: Match.create() }
              }
              .collectAsState(Match.create())
      val chessBoardUser1 =
          remember(matchUser1, scopeUser1, user1, chessFacade) {
            SnapshotChessBoardState(matchUser1, user1, scopeUser1, chessFacade::updateMatch)
          }

      // Player 2
      val user2 = mockk<AuthenticatedUser>()
      every { user2.uid } returns "userId2"
      val scopeUser2 = rememberCoroutineScope()
      val matchUser2 =
          remember(chessFacade, user2) {
                chessFacade.fetchMatchesForUser(user1).map { it.firstOrNull() ?: Match.create() }
              }
              .collectAsState(Match.create())
      val chessBoardUser2 =
          remember(matchUser2, scopeUser2, user2, chessFacade) {
            SnapshotChessBoardState(matchUser2, user2, scopeUser2, chessFacade::updateMatch)
          }

      // Player 1 plays
      val initPos = Position(4, 6)
      val finalPos = Position(4, 5)

      val piece = requireNotNull(chessBoardUser1.pieces[initPos])
      assertThat(piece.rank).isEqualTo(Pawn)
      assertThat(piece.color).isEqualTo(White)

      chessBoardUser1.onDropPiece(piece, finalPos)

      // Check the new expected position of the piece for both players
      val newPiecePlayer1 = requireNotNull(chessBoardUser1.pieces[finalPos])
      val newPiecePlayer2 = requireNotNull(chessBoardUser2.pieces[finalPos])

      assertThat(newPiecePlayer1.rank).isEqualTo(Pawn)
      assertThat(newPiecePlayer1.color).isEqualTo(White)

      assertThat(newPiecePlayer2.rank).isEqualTo(Pawn)
      assertThat(newPiecePlayer2.color).isEqualTo(White)
    }

    @Test
    fun movingAPawnThatNoLongerExists_DoesNothing() {
      val game = SnapshotChessBoardState()

      val initAttackerPos = Position(3, 0)
      val initVictimPos = Position(4, 1)
      val finalVictimPos = Position(4, 2)

      val victim = requireNotNull(game.pieces[initVictimPos])
      val attacker = requireNotNull(game.pieces[initAttackerPos])

      assertThat(victim.rank).isEqualTo(Pawn)
      assertThat(victim.color).isEqualTo(Black)

      assertThat(attacker.rank).isEqualTo(Queen)
      assertThat(attacker.color).isEqualTo(Black)

      // Eat the pawn with the queen
      game.onDropPiece(attacker, initVictimPos)

      // Try to move the pawn
      game.onDropPiece(victim, finalVictimPos)

      val pieceThatShouldNotExist = game.pieces[finalVictimPos]

      assertThat(pieceThatShouldNotExist).isNull()
    }

    @Test
    fun fixedMoveList_returnsExpectedMoves() {
      val game = SnapshotChessBoardState()
      val expected = listOf(1 to "f3", 2 to "e5", 3 to "g4", 4 to "Qh4#")

      assertThat(game.moves.map { it.number to it.name }).containsExactlyElementsIn(expected)
    }
  */
}
