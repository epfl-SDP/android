package ch.epfl.sdp.mobile.application.chess.engine

import ch.epfl.sdp.mobile.application.chess.engine.rules.*
import ch.epfl.sdp.mobile.application.chess.engine.rules.Rules

/** An enumeration representing the abilities of each [Piece] in classic chess. */
enum class Rank(delegate: Rules) : Rules by delegate {
  King(KingRules),
  Queen(QueenRules),
  Rook(RookRules),
  Bishop(BishopRules),
  Knight(KnightRules),
  Pawn(PawnRules),
}
