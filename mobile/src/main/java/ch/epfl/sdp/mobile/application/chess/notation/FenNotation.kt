package ch.epfl.sdp.mobile.application.chess.notation

import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Piece
import ch.epfl.sdp.mobile.application.chess.engine.Position
import ch.epfl.sdp.mobile.application.chess.notation.FenNotationCombinators.activeColor
import ch.epfl.sdp.mobile.application.chess.notation.FenNotationCombinators.board
import ch.epfl.sdp.mobile.application.chess.notation.FenNotationCombinators.castlingRights
import ch.epfl.sdp.mobile.application.chess.notation.FenNotationCombinators.enPassant
import ch.epfl.sdp.mobile.application.chess.notation.GenericNotationCombinators.integer
import ch.epfl.sdp.mobile.application.chess.notation.GenericNotationCombinators.spaces
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.flatMap
import ch.epfl.sdp.mobile.application.chess.parser.Combinators.map

/**
 * An object which contains some utilities to transform FEN (Forsyth–Edwards Notation) notation into
 * games.
 */
object FenNotation {

  /**
   * Represents castling rights for a board state
   * @param kingSideWhite Indicates if white can castle king side
   * @param queenSideWhite Indicates if white can castle queen side
   * @param kingSideBlack Indicates if black can castle king side
   * @param queenSideBlack Indicates if black can castle queen side
   */
  data class CastlingRights(
      val kingSideWhite: Boolean,
      val queenSideWhite: Boolean,
      val kingSideBlack: Boolean,
      val queenSideBlack: Boolean,
  )

  // TODO: Need to accommodate for whose turn to play, castling rights etc...
  /**
   * Represents the state of a board at a given time, without game history
   * @param board The [Board] describing the position of every [Piece] at the present moment
   * @param playing The [Color] of the currently playing player
   * @param castlingRights The [CastlingRights] at the present moment
   * @param enPassant target square in algebraic notation. If there's no en passant target square,
   * this is null. If a pawn has just made a two-square move, this is the [Position] "behind" the
   * pawn. This is recorded regardless of whether there is a pawn in position to make an en passant
   * capture.
   * @param halfMoveClock The number of halfmoves since the last capture or pawn advance, used for
   * the fifty-move rule.
   * @param fullMoveClock The number of the full move. It starts at 1, and is incremented after
   * Black's move.
   */
  data class BoardSnapshot(
      val board: Board<Piece<Color>>,
      val playing: Color,
      val castlingRights: CastlingRights,
      val enPassant: Position?,
      val halfMoveClock: Int,
      val fullMoveClock: Int,
  )

  /**
   * Parses a FEN String to a [BoardSnapshot].
   *
   * @param text The FEN String
   * @return The parsed [BoardSnapshot], if successful.
   */
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
