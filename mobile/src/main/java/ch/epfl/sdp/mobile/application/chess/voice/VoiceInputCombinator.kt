package ch.epfl.sdp.mobile.application.chess.voice

import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Move
import ch.epfl.sdp.mobile.application.chess.engine.rules.Action.Promote
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.combine
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.failure
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.filter
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.checkFinished
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.convertTokenToChar
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.convertTokenToToken
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.token
import ch.epfl.sdp.mobile.application.speech.ChessSpeechEnglishDictionary

/** An object that parse a "perfect" voice input into engine notation */
object VoiceInputCombinator {
  /**
   * A [Parser] which returns the [Rank] of a piece, and defaults to [Rank.Pawn] if no matching rank
   * letter was found.
   */
  // TODO : Internationalization
  private val rank =
      convertTokenToToken(ChessSpeechEnglishDictionary.chessPieces).map {
        when (it) {
          "king" -> Rank.King
          "queen" -> Rank.Queen
          "rook" -> Rank.Rook
          "bishop" -> Rank.Bishop
          "knight" -> Rank.Knight
          "pawn" -> Rank.Pawn
          else -> null
        }
      }

  val column =
      convertTokenToChar(ChessSpeechEnglishDictionary.letters).filter { it in 'a'..'h' }.map {
        it!! - 'a'
      }

  val row =
      convertTokenToChar(ChessSpeechEnglishDictionary.numbers)
          .filter { it in '0'..'9' }
          .map {
            // TODO : What happen id it is null ?
            8 - (it!! - '0')
          }
          .filter { it in 0 until Board.Size }

  val position = column.flatMap { x -> row.map { y -> Position(x, y) } }.filter { it.inBounds }

  /** A [Parser] which indicate the action between 2 position */
  private val actionSeparator = token("to")

  /** A [Parser] for a [Move] action. */
  private val move =
      rank.flatMap {
        if (it == null) {
              failure()
            } else {
              position.flatMap { from ->
                actionSeparator.flatMap { position.map { to -> Move(from, to) } }
              }
            }
            .checkFinished()
      }

  /** A [Parser] for a [Promote] action. */
  private val promote =
      position // No leading rank because only pawns may be promoted.
          .flatMap { from ->
            actionSeparator.flatMap {
              position.flatMap { to ->
                rank.map { rank ->
                  if (rank == null) {
                    failure<Rank>()
                  } else {
                    Promote(from, to, rank)
                  }
                }
              }
            }
          }
          .checkFinished()

  /** A [Parser] for an action. */
  private val action = combine(move, promote)

  /** Returns a [Parser] for an [Action]. */
  fun action(): Parser<String, Any> = action
}
