package ch.epfl.sdp.mobile.application.chess.engine

import ch.epfl.sdp.mobile.application.chess.engine2.core.Rules
import ch.epfl.sdp.mobile.application.chess.engine2.core.ranks.*

/** An enumeration representing the abilities of each [Piece] in classic chess. */
enum class Rank(private val delegate: Rules) : Rules by delegate {
  King(KingRules),
  Queen(QueenRules),
  Rook(RookRules),
  Bishop(BishopRules),
  Knight(KnightRules),
  Pawn(PawnRules),
}
