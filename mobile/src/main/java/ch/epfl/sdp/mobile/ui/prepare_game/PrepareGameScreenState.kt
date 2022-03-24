package ch.epfl.sdp.mobile.ui.prepare_game

interface PrepareGameScreenState {
  var colorChoice: ColorChoice
  var gameType: GameType

  val onNewLocalGame: () -> Unit
  val onNewOnlineGame: () -> Unit
}

enum class ColorChoice {
  WHITE,
  BLACK
}

enum class GameType {
  LOCAL,
  ONLINE
}
