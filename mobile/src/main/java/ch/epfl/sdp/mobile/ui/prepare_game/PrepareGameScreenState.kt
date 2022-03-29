package ch.epfl.sdp.mobile.ui.prepare_game

/**
 * State interface of the [PrepareGameScreen]
 * @property colorChoice chosen color
 * @property gameType chosen game type
 */
interface PrepareGameScreenState {
  var colorChoice: ColorChoice
  var gameType: GameType
}

/** Color choices for a chess game */
enum class ColorChoice {
  White,
  Black
}

/** Game types for local and online chess games */
enum class GameType {
  Local,
  Online
}
