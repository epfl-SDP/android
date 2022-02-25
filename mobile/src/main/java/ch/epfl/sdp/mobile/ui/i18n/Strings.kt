package ch.epfl.sdp.mobile.ui.i18n

/**
 * A (temporary) function which indicates that we're fetching a [String]. In the future, we may want
 * to use resources or a homegrown solution instead.
 *
 * TODO : Choose how we want to handle localization.
 *
 * @param content the default contents of the [String].
 *
 * @return the actual [String] to use.
 */
fun string(content: String): String {
  return content
}
