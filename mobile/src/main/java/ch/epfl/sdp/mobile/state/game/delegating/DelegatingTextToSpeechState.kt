package ch.epfl.sdp.mobile.state.game.delegating

import androidx.compose.runtime.*
import ch.epfl.sdp.mobile.application.chess.engine.Action
import ch.epfl.sdp.mobile.application.chess.engine.Board
import ch.epfl.sdp.mobile.application.chess.engine.Color
import ch.epfl.sdp.mobile.application.chess.engine.Piece
import ch.epfl.sdp.mobile.application.speech.SpeechFacade
import ch.epfl.sdp.mobile.state.game.core.GameDelegate
import ch.epfl.sdp.mobile.ui.game.TextToSpeechState
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Delegating class of text to speech.
 * @param gameDelegate [GameDelegate] game delegate.
 * @param facade [SpeechFacade] currently used speech facade.
 * @param strings [LocalizedStrings] provided strings.
 * @property settings persisted settings for the Text to Speech.
 * @param scope [CoroutineScope] scope under which suspending functions are executed.
 */
class DelegatingTextToSpeechState
constructor(
    private val gameDelegate: GameDelegate,
    private val facade: SpeechFacade,
    strings: State<LocalizedStrings>,
    private val scope: CoroutineScope,
) : TextToSpeechState {

  private val strings by strings

  private var settings by mutableStateOf(SpeechFacade.TextToSpeechSettings(true, facade))

  // Fetch stored settings
  init {
    scope.launch { facade.textToSpeechSettings().onEach { settings = it }.collect() }
  }

  /**
   * Creates a flow that synthesizes actions performed on the board by observing the game in the
   * game delegates.
   */
  private suspend fun synthesizeMoveFlow(): Flow<*> {
    return snapshotFlow { gameDelegate.game }
        .mapNotNull { it.previous?.let { (game, action) -> action to game.board } }
        .distinctUntilChanged()
        .onEach { (action, board) -> facade.synthesize(toText(action, board)) }
  }

  // Fires the Text to speech
  init {
    scope.launch { synthesizeMoveFlow().collect() }
  }

  override val textToSpeechEnabled: Boolean
    get() = settings.enabled

  override fun onTextToSpeechToggle() {
    scope.launch { settings.update { enabled(!textToSpeechEnabled) } }
  }

  /**
   * Converts a given action to text.
   * @param action [Action] action performed on board.
   * @param board [Board] the previous game board before action.
   */
  private fun toText(action: Action, board: Board<Piece<Color>>): String {
    return when (action) {
      is Action.Move -> moveToText(action, board)
      is Action.Promote -> promoteToText(action, board)
    }
  }

  /**
   * Forms text to be synthesized for Move action.
   * @param move [Action.Move] the move action.
   * @param board [Board] the previous game board before promotion.
   */
  private fun moveToText(move: Action.Move, board: Board<Piece<Color>>): String {
    val from = strings.boardPosition(move.from.x, move.from.y)
    val to = strings.boardPosition(move.from.x + move.delta.x, move.from.y + move.delta.y)
    val color = board[move.from]?.color?.name ?: ""
    val rank = board[move.from]?.rank?.name ?: ""
    val piece = strings.boardPieceContentDescription(color, rank)
    return strings.boardMove(piece, from, to)
  }

  /**
   * Forms text to be synthesized for Promotion action.
   * @param promotion [Action.Promote] the promotion action.
   * @param board [Board] the previous game board before promotion.
   */
  private fun promoteToText(promotion: Action.Promote, board: Board<Piece<Color>>): String {
    val from = strings.boardPosition(promotion.from.x, promotion.from.y)
    val to =
        strings.boardPosition(
            promotion.from.x + promotion.delta.x, promotion.from.y + promotion.delta.y)
    val oldRank = board[promotion.from]?.rank?.name ?: ""
    val newRank = promotion.rank.name
    return strings.boardPromoted(oldRank, from, to, newRank)
  }
}
