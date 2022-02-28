package ch.epfl.sdp.mobile.ui.features.robots

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import ch.epfl.sdp.mobile.ui.i18n.LocalizedStrings

/**
 * An [AbstractRobot] provides a basic layer of abstraction around user interface tests. Rather than
 * building with the underlying [ComposeTestRule], an [AbstractRobot] implementation offers some
 * high-level primitives that provide easy access to the underlying nodes.
 *
 * @param rule the [ComposeTestRule] that backs this [AbstractRobot].
 * @param strings the [LocalizedStrings] of the application.
 */
abstract class AbstractRobot(
    val rule: ComposeTestRule,
    val strings: LocalizedStrings,
) : ComposeTestRule by rule {

  /**
   * Finds the [SemanticsNodeInteraction] with a node with the given text.
   *
   * @see onNodeWithText
   *
   * @param substring whether to match nodes by substring.
   * @param ignoreCase whether to ignore case in text matching.
   * @param useUnmergedTree true if the unmerged tree should be used.
   * @param text a lambda which provides the [LocalizedStrings] to use to search the node.
   *
   * @return the [SemanticsNodeInteraction] corresponding to the matched node.
   */
  fun onNodeWithLocalizedText(
      substring: Boolean = false,
      ignoreCase: Boolean = false,
      useUnmergedTree: Boolean = false,
      text: LocalizedStrings.() -> String,
  ): SemanticsNodeInteraction =
      onNodeWithText(
          substring = substring,
          ignoreCase = ignoreCase,
          useUnmergedTree = useUnmergedTree,
          text = text(strings),
      )

  /**
   * Finds the [SemanticsNodeInteractionCollection] with the nodes with the given text.
   *
   * @see onAllNodesWithText
   *
   * @param substring whether to match nodes by substring.
   * @param ignoreCase whether to ignore case in text matching.
   * @param useUnmergedTree true if the unmerged tree should be used.
   * @param text a lambda which provides the [LocalizedStrings] to use to search the nodes.
   *
   * @return the [SemanticsNodeInteractionCollection] corresponding to the matched nodes.
   */
  fun onAllNodesWithLocalizedText(
      substring: Boolean = false,
      ignoreCase: Boolean = false,
      useUnmergedTree: Boolean = false,
      text: LocalizedStrings.() -> String,
  ): SemanticsNodeInteractionCollection =
      onAllNodesWithText(
          substring = substring,
          ignoreCase = ignoreCase,
          useUnmergedTree = useUnmergedTree,
          text = text(strings),
      )

  /**
   * Finds the [SemanticsNodeInteraction] with a node with the given content description.
   *
   * @see onNodeWithContentDescription
   *
   * @param substring whether to match nodes by substring.
   * @param ignoreCase whether to ignore case in text matching.
   * @param useUnmergedTree true if the unmerged tree should be used.
   * @param label a lambda which provides the [LocalizedStrings] to use to search the node.
   *
   * @return the [SemanticsNodeInteraction] corresponding to the matched node.
   */
  fun onNodeWithLocalizedContentDescription(
      substring: Boolean,
      ignoreCase: Boolean,
      useUnmergedTree: Boolean,
      label: LocalizedStrings.() -> String,
  ): SemanticsNodeInteraction =
      onNodeWithContentDescription(
          substring = substring,
          ignoreCase = ignoreCase,
          useUnmergedTree = useUnmergedTree,
          label = label(strings),
      )

  /**
   * Finds the [SemanticsNodeInteractionCollection] with the nodes with the given content
   * description.
   *
   * @see onAllNodesWithText
   *
   * @param substring whether to match nodes by substring.
   * @param ignoreCase whether to ignore case in text matching.
   * @param useUnmergedTree true if the unmerged tree should be used.
   * @param label a lambda which provides the [LocalizedStrings] to use to search the nodes.
   *
   * @return the [SemanticsNodeInteractionCollection] corresponding to the matched nodes.
   */
  fun onAllNodesWithLocalizedContentDescription(
      substring: Boolean,
      ignoreCase: Boolean,
      useUnmergedTree: Boolean,
      label: LocalizedStrings.() -> String,
  ): SemanticsNodeInteractionCollection =
      onAllNodesWithContentDescription(
          substring = substring,
          ignoreCase = ignoreCase,
          useUnmergedTree = useUnmergedTree,
          label = label(strings),
      )

  /**
   * Switches to the given screen, using the provided [block] for configuration.
   *
   * @param builder the builder for the result of the switch.
   * @param block the builder block.
   */
  inline fun <R> switchTo(
      builder: (rule: ComposeTestRule, strings: LocalizedStrings) -> R,
      block: R.() -> Unit = {},
  ): R = builder(rule, strings).apply(block)
}
