package ch.epfl.sdp.mobile.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ch.epfl.sdp.mobile.application.authentication.AuthenticatedUser
import ch.epfl.sdp.mobile.ui.game.GameScreen
import ch.epfl.sdp.mobile.ui.game.GameScreenState
import ch.epfl.sdp.mobile.ui.game.Move

class ChessGameScreenState(
    private val user: AuthenticatedUser,
    override val moves: List<Move>,
) : GameScreenState {}

data class ChessMove(override val number: Int, override val name: String) : Move

@Composable
fun StatefulGameScreen(
    user: AuthenticatedUser,
    modifier: Modifier = Modifier,
) {
  val state =
      remember(user) {
        ChessGameScreenState(user, randomMoves(20))
      }
  GameScreen(state, modifier)
}

private fun randomMoves(number: Int): List<Move> {

  val piecePool: List<String> = listOf("K", "Q", "N", "B", "R", "")
  val columnPool: List<Char> = ('a'..'h').toList()
  val rowPool: List<Char> = ('1'..'8').toList()

  return (1 until number).map {
    val piece = piecePool[kotlin.random.Random.nextInt(0, piecePool.size)]
    val column = columnPool[kotlin.random.Random.nextInt(0, columnPool.size)]
    val row = rowPool[kotlin.random.Random.nextInt(0, rowPool.size)]
    val randomString = piece + column + row.toString()

    ChessMove(number = it, name = randomString)
  }
}

