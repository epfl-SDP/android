package ch.epfl.sdp.mobile.ui.prepare_game

/**
 * State interface of the [PrepareGameScreen]
 * @property colorChoice chosen color
 * @property gameType chosen game type
 * @property onNewLocalGame called action when a local game gets chosen
 * @property onNewOnlineGame called action when an online game gets chosen
 */
interface PrepareGameScreenState {
  var colorChoice: ColorChoice
  var gameType: GameType

  val onNewLocalGame: () -> Unit
  val onNewOnlineGame: () -> Unit
}

/**
 * Color choices for a chess game
 */
enum class ColorChoice {
  WHITE,
  BLACK
}

/**
 * Game types for local and online chess games
 */
enum class GameType {
  LOCAL,
  ONLINE
}
