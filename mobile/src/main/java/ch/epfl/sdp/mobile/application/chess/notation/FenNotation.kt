package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.*
import ch.epfl.sdp.mobile.application.chess.notation.FenNotationCombinators.activeColor
import ch.epfl.sdp.mobile.application.chess.notation.FenNotationCombinators.board
import ch.epfl.sdp.mobile.application.chess.notation.FenNotationCombinators.castlingRights
import ch.epfl.sdp.mobile.application.chess.notation.FenNotationCombinators.enPassant
import ch.epfl.sdp.mobile.application.chess.notation.FenNotationCombinators.integer
import ch.epfl.sdp.mobile.application.chess.notation.FenNotationCombinators.spaces
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map

/**
 * An object which contains some utilities to transform FEN (Forsyth–Edwards Notation) notation into
 * games
 */
object FenNotation {

  data class CastlingRights(
      val kingSideWhite: Boolean = false,
      val queenSideWhite: Boolean = false,
      val kingSideBlack: Boolean = false,
      val queenSideBlack: Boolean = false,
  )

  // TODO: Need to accommodate for whose turn to play, castling rights etc...
  data class BoardSnapshot(
      val board: Board<Piece<Color>>,
      val playing: Color,
      val castlingRights: CastlingRights,
      val enPassant: Position?,
      val halfMoveClock: Int,
      val fullMoveClock: Int,
  )

  fun parseFen(text: String): BoardSnapshot? {
    val parser =
        board().flatMap { board ->
          spaces.flatMap {
            activeColor.flatMap { color ->
              spaces.flatMap {
                castlingRights.flatMap { castling ->
                  spaces.flatMap {
                    enPassant.flatMap { enPassant ->
                      spaces.flatMap {
                        integer.flatMap { halfMoveClock ->
                          spaces.flatMap {
                            integer.map { fullMoveClock ->
                              BoardSnapshot(
                                  board = board,
                                  playing = color,
                                  castlingRights = castling,
                                  enPassant = enPassant,
                                  halfMoveClock = halfMoveClock,
                                  fullMoveClock = fullMoveClock,
                              )
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
    return parser.parse(text).singleOrNull()?.output
  }
}
