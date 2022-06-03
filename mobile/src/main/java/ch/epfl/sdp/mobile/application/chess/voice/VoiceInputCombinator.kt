package ch.epfl.sdp.mobile.application.chess.voice

import ch.epfl.sdp.mobile.application.chess.engine.Action
import ch.epfl.sdp.mobile.application.chess.engine.Action.Companion.Move
import ch.epfl.sdp.mobile.application.chess.engine.Action.Companion.Promote
import ch.epfl.sdp.mobile.application.chess.engine.Action.Move
import ch.epfl.sdp.mobile.application.chess.engine.Action.Promote
import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Rank
import ch.epfl.sdp.mobile.application.chess.notation.CommonNotationCombinators
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.combine
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.failure
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.filter
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.filterNotNull
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map
import ch.epfl.sdp.mobile.application.chess.parser.Parser
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.checkFinished
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.convertToken
import ch.epfl.sdp.mobile.application.chess.parser.StringCombinators.token
import ch.epfl.sdp.mobile.application.speech.ChessSpeechEnglishDictionary
import ch.epfl.sdp.mobile.application.speech.ChessSpeechFilterRules.rulesSet

/** An object that parse a "perfect" voice input into engine notation. */
object VoiceInputCombinator {
  /**
   * A [Parser] which returns the [Rank] of a piece, and defaults to [Rank.Pawn] if no matching rank
   * letter was found.
   */
  // TODO : Internationalization
  private val rank =
  // Try to transformed into a our chess word
  combine(
              convertToken(ChessSpeechEnglishDictionary.chessPieces),
              token().map { token ->
                rulesSet
                    .map { chessRule ->
                      // Apply rule
                      val res = chessRule.rule(token)
                      res
                    }
                    .firstOrNull { res ->
                      // Get the 1st value that have been transformed
                      res != null
                    }
              })
          .map {
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

  /** A parser which parses a column. */
  val column =
      convertToken(ChessSpeechEnglishDictionary.letters)
          .filter { it in 'a'..'h' }
          .filterNotNull()
          .map { it - 'a' }

  /** A parser which parses a row number. */
  val row =
      convertToken(ChessSpeechEnglishDictionary.numbers)
          .filter { it in '0'..'9' }
          .filterNotNull()
          .map { 8 - (it - '0') }
          .filter { it in 0 until Board.Size }

  /** A [Parser] which parses a position. */
  val position =
      combine(
          CommonNotationCombinators.position,
          CommonNotationCombinators.computePosition(column, row))

  /** A [Parser] which indicate the action between 2 position. */
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
                // remove word that cannot be converted
                rank.filterNotNull().map { rank -> Promote(from, to, rank) }
              }
            }
          }
          .checkFinished()

  /** A [Parser] for an action. */
  private val action = combine(move, promote)

  /** Returns a [Parser] for an [Action]. */
  fun action(): Parser<String, Action> = action
}
